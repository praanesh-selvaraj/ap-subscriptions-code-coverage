package com.kmp.aeroparker.subscription.payments.klix;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kmp.aeroparker.subscription.payments.credentials.KlixCredentials;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;
import com.kmp.aeroparker.subscription.webutil.utils.WebUtil;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class KlixRequestBuilder
{
	private static final int MAX_REFERENCE_LENGTH = 128;
	private static final String SUCCESS_CMD = "processSuccessRedirect";
	private static final String FAILURE_CMD = "processFailRedirect";

	private final JsonParser jsonParser = new JsonParser();
	private final KlixUrlBuilder klixUrlBuilder;

	public JsonObject generateJsonForInitialRequest(final KlixRequest parameters)
	{
		KlixCredentials credentials = parameters.getCredentials();
		JsonObject requestJson = new JsonObject();
		requestJson.addProperty("brand_id", credentials.getBrandId());
		requestJson.addProperty("success_redirect", parameters.getSuccessRedirect());
		requestJson.addProperty("failure_redirect", parameters.getFailureRedirect());
		requestJson.addProperty("success_callback", parameters.getSuccessCallback());
		String bookingRefFullName =
				parameters.getBookingReference() + ", " + parameters.getFirstName() + " " + parameters.getLastName();
		requestJson.addProperty("reference", StringUtil.trimToLength(bookingRefFullName, MAX_REFERENCE_LENGTH));

		if (!StringUtil.isEmpty(parameters.getPaymentMethod()))
		{
			JsonArray paymentMethodWhitelist = jsonParser.parse(parameters.getPaymentMethod())
					.getAsJsonArray();
			requestJson.add("payment_method_whitelist", paymentMethodWhitelist);
		}

		JsonObject purchase = new JsonObject();
		purchase.addProperty("currency", parameters.getCurrency());
		purchase.addProperty("language", parameters.getLanguage());
		JsonArray products = new JsonArray();
		JsonObject product = new JsonObject();
		product.addProperty("name", parameters.getProductName());
		product.addProperty("price", parameters.getAmount());
		products.add(product);
		purchase.add("products", products);

		JsonObject client = new JsonObject();
		client.addProperty("email", parameters.getEmail());
		client.addProperty("phone", parameters.getPhoneNumber());
		client.addProperty("full_name", parameters.getFirstName() + " " + parameters.getLastName());
		client.addProperty("street_address", parameters.getStreetAddress());
		client.addProperty("city", parameters.getCity());
		client.addProperty("zip_code", parameters.getZipCode());

		requestJson.add("purchase", purchase);
		requestJson.add("client", client);

		return requestJson;
	}

	public JsonObject generateJsonForRefund(final BigDecimal amount)
	{
		JsonObject requestJson = new JsonObject();
		requestJson.addProperty("amount", amount.setScale(2, BigDecimal.ROUND_HALF_UP)
				.movePointRight(2));

		return requestJson;
	}

	public KlixRequest getRequestParameters(final SubscriptionBookingData bookingData, final Affiliates affiliates,
			final String absoluteUrl, final String languageCode, final KlixCredentials credentials, BigDecimal amount)
	{
		int carParkId = bookingData.getCarParkId();
		String bookingReference = bookingData.getBookingReference();
		String originalBookingReference = bookingData.getBookingReference();
		String language = languageCode;
		String frameAncestor = absoluteUrl.substring(0, absoluteUrl.lastIndexOf("/"));
		String redirectUrl = absoluteUrl;
		redirectUrl = WebUtil.urlEncode(redirectUrl);
		String startDate = bookingData.getStartDate();
		String firstName = Optional.ofNullable(StringUtils.trimAllWhitespace(bookingData.getFname()))
				.orElse("");
		String lastName = Optional.ofNullable(StringUtils.trimAllWhitespace(bookingData.getLname()))
				.orElse("");
		int affiliateId = affiliates.getId();
		int siteId = affiliates.getSiteid()
				.intValue();

		String guid = HtmlUtils.htmlUnescape(bookingData.getCustomerGuid());
		String successRedirect = klixUrlBuilder.getSuccessRedirectUrl(frameAncestor, SUCCESS_CMD, affiliateId,
				redirectUrl, guid, bookingReference, originalBookingReference, carParkId, false, startDate, firstName,
				lastName, siteId);
		String successCallBack = klixUrlBuilder.getSuccessRedirectUrl(frameAncestor, SUCCESS_CMD, affiliateId,
				redirectUrl, guid, bookingReference, originalBookingReference, carParkId, true, startDate, firstName,
				lastName, siteId);
		String failureRedirect = klixUrlBuilder.buildRedirectUrl(FAILURE_CMD, frameAncestor, affiliateId, redirectUrl,
				guid, bookingReference, startDate, firstName, lastName, siteId);
		String whitelistPaymentMethod = Optional.ofNullable(bookingData.getPaymentMethod())
				.orElse("");

		return new KlixRequest(successRedirect, failureRedirect, successCallBack, language,
				HtmlUtils.htmlUnescape(affiliates.getName()), amount.toPlainString(),
				Optional.ofNullable(bookingData.getEmail())
						.orElse(""),
				affiliates, firstName, originalBookingReference, Optional.ofNullable(bookingData.getTown())
						.orElse(""),
				Optional.ofNullable(bookingData.getPostcode())
						.orElse(""),
				Optional.ofNullable(bookingData.getAddr1())
						.orElse(""),
				credentials, bookingData.getCurrency(), Optional.ofNullable(bookingData.getTelno())
						.orElse(""),
				Optional.ofNullable(bookingData.getCountry())
						.orElse(""),
				lastName, guid, bookingReference, carParkId, "", false, false, whitelistPaymentMethod);
	}
}
