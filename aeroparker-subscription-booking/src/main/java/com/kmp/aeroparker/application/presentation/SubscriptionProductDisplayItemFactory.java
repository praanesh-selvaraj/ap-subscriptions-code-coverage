package com.kmp.aeroparker.application.presentation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.kmp.aeroparker.application.availability.PriceDetails;
import com.kmp.aeroparker.application.availability.SubscriptionProductAvailabilityDetails;
import com.kmp.aeroparker.application.availability.SubscriptionProductAvailabilityDetailsList;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.l10n.Localise;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class SubscriptionProductDisplayItemFactory
{
	private static final String SHORT_FORM_MONTH = "mo";

	private final SeasonTicketDisplayItemBuilder seasonTicketDisplayItemBuilder;
	private final RecurringTicketDisplayItemBuilder recurringTicketDisplayItemBuilder;
	private final Localise localise;
	private final LanguageFieldsList languageFieldsList;

	public SubscriptionProductDisplayItemList build(final SubscriptionProductAvailabilityDetailsList availabilityDetailsList)
	{
		log.debug("Building subscription product item list");
		SubscriptionProductDisplayItemList displayItemList = new SubscriptionProductDisplayItemList();

		for (SubscriptionProductAvailabilityDetails availabilityDetails : availabilityDetailsList)
		{
			SubscriptionProductDisplayItem displayItem = null;

			if (availabilityDetails.isSeasonTicket())
			{
				// this will return a season ticket display type, we can
				// Customise the season ticket how we want so it is different
				// from the rolling ticket
				displayItem = seasonTicketDisplayItemBuilder.build(availabilityDetails);
			}
			else
			{
				displayItem = recurringTicketDisplayItemBuilder.build(availabilityDetails);
			}
			// else if rolling ticket, build using rolling ticket builder
			displayItem.setProduct(availabilityDetails.getProduct());
			displayItem.setProductId(availabilityDetails.getProduct()
					.getId());
			displayItem.setProductAppearance(availabilityDetails.getProductAppearance());
			// we don't really need the terms for now, but in future we might
			// need the minimumTermCancellation, minimumTerm and the bookingFee
			displayItem.setProductTerms(availabilityDetails.getProductTerms());
			displayItem.setPriceDetails(buildpriceDetails(availabilityDetails.getPriceDetails()
					.getPrice(), availabilityDetails.isRecurringTicket()));
			displayItem.setUnescapedMoreInfo(unescapeMoreInfo(availabilityDetails.getProductAppearance()
					.getMoreInfo()));
			// build the bullet points
			if (!StringUtil.isEmpty(availabilityDetails.getProductAppearance()
					.getBulletPoint()))
			{
				displayItem.setBulletPoints(getBulletPointList(availabilityDetails.getProductAppearance()
						.getBulletPoint()));
			}
			displayItemList.add(displayItem);
		}
		log.debug("Finished building subscription product item list");
		return displayItemList;
	}

	private PriceDetails buildpriceDetails(final BigDecimal price, final boolean isRecurring)
	{
		PriceDetails localisedPriceDetails = new PriceDetails();
		localisedPriceDetails.setPrice(price);
		localisedPriceDetails.setPriceIncludePennyPlaceholdersHtml(buildPriceIncludePennyPlaceholdersHtml(price.floatValue(), isRecurring));
		return localisedPriceDetails;
	}

	private String buildPriceIncludePennyPlaceholdersHtml(final float totalPrice, final boolean isRecurring)
	{
		String tempPrice = localise.priceIncludePennyPlaceholders(totalPrice, true)
				.replace("{pennies}", "<span>")
				.replace("{/pennies}", "</span>");
		String price = "<span>" + tempPrice + (isRecurring ? "/" + languageFieldsList.getTranslation(SHORT_FORM_MONTH) : "") + "</span>";
		return price;
	}

	private String unescapeMoreInfo(final String unescapedMoreInfo)
	{
		String moreInfo = "";
		if (!StringUtil.isEmpty(unescapedMoreInfo))
		{
			moreInfo = HtmlUtils.htmlUnescape(unescapedMoreInfo);
		}
		return moreInfo;
	}

	private List<String> getBulletPointList(final String strBulletPoint)
	{
		List<String> bulletPointArray = new ArrayList<>();
		try
		{
			JsonObject jsonObject = new JsonParser().parse(strBulletPoint)
					.getAsJsonObject();
			for (int i = 0; i < jsonObject.size(); i++)
			{
				bulletPointArray.add(HtmlUtils.htmlUnescape(jsonObject.get("line" + (i + 1))
						.getAsString()));
			}
		}
		catch (JsonParseException e)
		{
			log.debug("Error parsing subscription product appearance bullet points - " + strBulletPoint + " : " + e.getMessage(), e);
		}
		return bulletPointArray;
	}
}