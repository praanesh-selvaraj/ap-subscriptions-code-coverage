package com.kmp.aeroparker.application.web;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.model.SubscriptionBookingQuery;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.presentation.SubscriptionProductDisplayItem;
import com.kmp.aeroparker.application.presentation.SubscriptionProductDisplayItemList;
import com.kmp.aeroparker.application.quotas.SubscriptionQuotaChecker;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateSubscriptionSettings;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping(value = "/subscriptions/{affCode}")
public class ProductController extends AbstractController
{
	private static final String REDIRECT_TO_ERROR = "redirect:error";
	private static final String REDIRECT_TO_DETAILS = "redirect:details";
	private static final String HEADER_SELECT_PRODUCT = "Select Product";
	private static final String DUBLIN_SITE_TITLE_KEYWORD = "DUBLIN";
	private static final String SOLD_OUT_ERROR_MESSAGE = "Sorry! The subscription product you have chosen is sold out";
	private final SubscriptionConfigBean requestBean;
	private final SubscriptionControllerService service;
	private final Localise localise;
	private final SubscriptionQuotaChecker quotaChecker;

	/**
	 * Build subscription product display, this will be called also for redirect
	 * from client page
	 * 
	 * @param model
	 * @param bookingQuery
	 * @return
	 */
	@GetMapping(value = "/products")
	public String selectProducts(final Model model, final RedirectAttributes redirectAttributes, @ModelAttribute("bookingQuery") SubscriptionBookingQuery bookingQuery,
			@RequestParam(value = "startDate", required = false, defaultValue = "") final String startDate,
			@RequestParam(value = "customerGuid", required = false) final String customerGuid)
	{
		Basket basket = null;
		boolean isMultiPurchase = requestBean.isMultiPurchase();
		List<Integer> selectedProductIds = new ArrayList<>();
		boolean isBookingQueryInvalid = bookingQuery == null || !bookingQuery.isValid();
		AffiliateSubscriptionSettings subscriptionSettings = requestBean.getAffiliateSubscriptionSettings();
		// customer has pressed back button or the breadcrumb to step 2
		if (isBookingQueryInvalid)
		{
			log.debug("Booking query is null or invalid");
			/// might be a redirect, so check if there is a date in the query
			/// string and process it, if not redirect to dates page
			if (!StringUtil.isEmpty(startDate))
			{
				log.info("Start date is: {}, create a new Booking query", startDate);
				SubscriptionBookingQuery tempBookingQuery = createBookingQuery(requestBean, startDate);
				if (service.validateBookingTimes(tempBookingQuery))
				{
					log.debug("Booking query is valid");
					bookingQuery = tempBookingQuery;

					if (!StringUtil.isEmpty(customerGuid) && isMultiPurchase)
					{
						log.info("Customer guid: {}, is present, redirected from details step, preparing to display basket and selected products",
								customerGuid);
						int currentLanguageId = requestBean.getCurrentLanguageId();
						int defaultLanguageId = requestBean.getCurrentLanguageId();
						basket = service.getBasket(customerGuid, currentLanguageId, defaultLanguageId,
								requestBean.getAffiliate(), subscriptionSettings);
						if (basket != null)
						{
							selectedProductIds = basket.getAllProductIds();
							model.addAttribute("cacheIds", selectedProductIds);
							model.addAttribute("continueToNextStep", basket.getGrandTotal()
									.compareTo(BigDecimal.ZERO) == 1 ? true : false);
						}
					}
				}
				else
				{
					log.debug("Booking query is invalid, Redirecting to dates (Step one)");
					return "redirect:dates";
				}
			}
			else
			{
				log.debug("No start date is present, Redirecting to dates (Step one)");
				return "redirect:dates";
			}
		}
		int affId = requestBean.getAffiliateId();
		// build the display item list
		SubscriptionProductDisplayItemList productDisplayItemList = service.buildProductDisplayItemList(bookingQuery);
		checkProductOccupancy(productDisplayItemList, startDate, affId);
		boolean isSingleProductAvailable = productDisplayItemList.size() == 1;
		boolean skipProductSelectionIfOneIsAvailable = subscriptionSettings != null ? subscriptionSettings
				.getSkipProductSelectionIfOneIsAvailable() : false;
		if (productDisplayItemList.isEmpty())
		{
			redirectAttributes.addAttribute("noProduct", true);
			log.debug("No products available, Redirecting to dates (Step one)");
			return "redirect:dates";
		}
		if (isMultiPurchase)
		{
			log.info("Setting up multipurchase");
			if (!selectedProductIds.isEmpty())
			{
				productDisplayItemList.setSelectedProducts(selectedProductIds);
			}
			if (basket == null)
			{
				// Add empty basket, so booking summary will show
				basket = new Basket();
				basket.initialize(localise);
			}
		}
		if (isBookingQueryInvalid && isSingleProductAvailable && skipProductSelectionIfOneIsAvailable)
		{
			log.info("Skipping step 2, redirecting to step 1 as booking query is invalid");
			return "redirect:dates";
		}
		else if (isSingleProductAvailable && skipProductSelectionIfOneIsAvailable)
		{
			log.info("Skipping step 2, redirecting to step 3");
			return redirectToDetails(redirectAttributes, buildJsonForSingleProduct(productDisplayItemList, startDate), startDate, customerGuid);
		}

		model.addAttribute("productDisplayItemList", productDisplayItemList);
		model.addAttribute("isMultiPurchase", isMultiPurchase);
		setDatePickerFormat(bookingQuery.getStartDate(), affId, requestBean.getTimeZone(), model);
		setTranslationsForDatePicker(model);
		model.addAttribute("currentStage", 2);
		model.addAttribute("header", HEADER_SELECT_PRODUCT);
		model.addAttribute("basket", basket);
		model.addAttribute("skipProductSelection", false);
		return isMultiPurchase ? "subscription-select-multiple-products" : "subscription-select-products";
	}

	private void setDatePickerFormat(final String startDate, final int affId, final String timeZone, final Model model)
	{
		AffiliateSubscriptionSettings affiliateSubscriptionSettings = requestBean.getAffiliateSubscriptionSettings();
		model.addAttribute("datePickerStartDate", service.setUpDate(affiliateSubscriptionSettings, timeZone));
		model.addAttribute("datePickerEntryDate",
				DateUtil.localDateToString(DateUtil.strToLocalDate(startDate, requestBean.getDateFormat()), "MM/dd/yyyy"));
		model.addAttribute("dateInputFormat", localise.getDateFormatForDatePicker(false, true));
		model.addAttribute("dateDisplayFormat", localise.getDateFormatForDatePicker(true, true));
		model.addAttribute("datePickerfixedStartDay", affiliateSubscriptionSettings == null ? 0 : affiliateSubscriptionSettings.getFixedStartDay());
	}

	private void checkProductOccupancy(SubscriptionProductDisplayItemList productDisplayItemList, String startDate,
			int affiliateId)
	{
		Iterator<SubscriptionProductDisplayItem> iterator = productDisplayItemList.iterator();
		while (iterator.hasNext())
		{
			SubscriptionProductDisplayItem displayItem = iterator.next();
			SubscriptionProduct product = displayItem.getProduct();
			int productId = product != null ? product.getId() : 0;
			boolean isProductAvailable = quotaChecker.checkProductOccupancy(productId, DateUtil.strToLocalDate(startDate, localise.getLocation()
					.getDateformat()), affiliateId);
			if (!isProductAvailable)
			{
				iterator.remove();
			}
		}
	}

	/**
	 * This will be called when the client clicks on continue on select product
	 * step, purchaseData is a json array string
	 * 
	 * @param redirectAttributes
	 * @param purchaseData
	 * @return
	 */
	@PostMapping(value = "/products")
	public String redirectToDetails(final RedirectAttributes redirectAttributes,
			@RequestParam(value = "selectedProductData") @NotEmpty final String purchaseData,
			@RequestParam(value = "startDate") final String startDate, @RequestParam(value = "customerGuid", required = false) String customerGuid)
	{
		int affid = requestBean.getAffiliateId();
		customerGuid = service.insertPurchaseData(affid, customerGuid, purchaseData);
		redirectAttributes.addAttribute("customerGuid", customerGuid);
		redirectAttributes.addAttribute("startDate", startDate);
		log.debug("Redirecting to details");
		return "redirect:details";
	}

	private String buildJsonForSingleProduct(SubscriptionProductDisplayItemList productDisplayItemList,
			String startDate)
	{
		JsonArray jsonArray = new JsonArray();
		JsonObject jsonObject = new JsonObject();
		SubscriptionProductDisplayItem displayItem = productDisplayItemList.get(0);

		jsonObject.addProperty("productId", displayItem.getProductId());
		jsonObject.addProperty("startDate", startDate);
		jsonArray.add(jsonObject);

		return jsonArray.toString();
	}
	
	/***
	 * Used for third party websites that have a link with a hardcoded product ID. We create the purchase data with the product ID and the startDate
	 * as today's date and go straight to the details step
	 * @param redirectAttributes Redirect attributes used on the details step
	 * @param productId Product ID to book.
	 * @return
	 */
	@GetMapping(value = "/products-thirdparty")
	public String selectProductsFromThirdParty(final RedirectAttributes redirectAttributes, @RequestParam(value = "productId", required = true) final int productId)
	{
		int affiliateId = requestBean.getAffiliateId();
		LocalDate startDate = DateUtil.nowLocalDate(requestBean.getTimeZone());
		boolean isProductAvailable = service.validateProductIsAvailable(productId, startDate, affiliateId, requestBean.getSiteId(),
				requestBean.getCurrentLanguageId(), requestBean.getDefaultLanguageId());
		boolean isProductAvailableAfterQuota = quotaChecker.checkProductOccupancy(productId, startDate, affiliateId);
		
		if (!isProductAvailable || !isProductAvailableAfterQuota)
		{
			log.info("Product ID {} selected from third party site was not available, showing error page...", productId);
			boolean isDublin = requestBean.getSiteTitle()
					.toUpperCase()
					.contains(DUBLIN_SITE_TITLE_KEYWORD);
			if (isDublin)
			{
				redirectAttributes.addAttribute("errorTitle", SOLD_OUT_ERROR_MESSAGE);
			}
			return REDIRECT_TO_ERROR;
		}

		String dateFormat = requestBean.getDateFormat();
		String purchaseData = service.buildPurchaseDataFromThirdParty(productId, startDate, dateFormat);
		String customerGuid = service.insertPurchaseData(affiliateId, "", purchaseData);
		redirectAttributes.addAttribute("startDate", DateUtil.localDateToString(startDate, dateFormat));
		redirectAttributes.addAttribute("customerGuid", customerGuid);
		
		return REDIRECT_TO_DETAILS;
	}
}