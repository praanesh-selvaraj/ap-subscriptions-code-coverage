package com.kmp.aeroparker.subscription.payments.klix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kmp.aeroparker.subscription.payments.credentials.KlixCredentials;
import com.kmp.aeroparker.subscription.payments.service.PaymentService;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;
import com.kmp.aeroparker.subscription.payments.tables.pojos.KlixSession;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

@ExtendWith(MockitoExtension.class)
class KlixResponseBuilderTest
{
	@InjectMocks
	private KlixResponseBuilder builder;
	@Mock
	private PaymentService paymentService;
	@Mock
	private KlixHttpClient client;
	@Mock
	private KlixCredentials credentials;
	@Mock
	private Affiliates affiliates;
	@Mock
	private KlixHttpClient Client;
	@Mock
	private KlixSession klixSession;
	private JsonObject json;

	private static final String PAYMENT_METHODS_JSON = "{\n" + " \"names\":{\n" + " \"klix\":\"Pay by card\",\n"
			+ "      \"bank_lv\":\"Bank\",\n" + " \"bank_lt\":\"Bank\",\n" + " \"bank_2_lv\":\"Bank 2\",\n"
			+ "      \"bank_2_lt\":\"Bank 2\"\n" + " },\n" + "  \"logos\":{\n" + " \"klix\":\"klixLogo\",\n"
			+ "      \"bank_lv\":\"bankLogoLv\",\n" + " \"bank_lt\":\"bankLogoLt\",\n"
			+ "      \"bank_2_lv\":\"bank2LogoLv\",\n" + " \"bank_2_lt\":\"bank2LogoLt\"\n" + " },\n"
			+ "   \"payment_method_groups\":[\n" + " {\n" + " \"name\":\"klix\",\n"
			+ "         \"logo\":\"klixLogo\",\n" + " \"label\":\"Pay by card\",\n" + " \"methods\":[\n"
			+ "            \"klix_card\"\n" + " ]\n" + " },\n" + " {\n" + " \"name\":\"bank\",\n"
			+ " \"logo\":\"\",\n" + " \"label\":\"Bank Transfer\",\n" + "  \"methods\":[\n" + " \"bank_lv\",\n"
			+ " \"bank_lt\",\n" + " \"bank_2_lv\",\n" + " \"bank_2_lt\"\n" + " ]\n" + " }\n" + " ]\n" + "}";

	@BeforeEach
	public void init()
	{
		json = new JsonObject();
		JsonObject payment = new JsonObject();
		JsonObject client = new JsonObject();
		JsonObject purchase = new JsonObject();
		JsonObject issuerDetails = new JsonObject();
		JsonArray products = new JsonArray();

		json.addProperty("id", "id");
		json.addProperty("issued", "issued");
		json.addProperty("status", "status");
		json.addProperty("product", "product");
		json.addProperty("brand_id", "brandId");
		json.addProperty("client_id", "clientId");
		json.addProperty("created_on", "createdOn");
		json.addProperty("event_type", "eventType");
		json.addProperty("created_from_ip", "createdFromIp");
		json.addProperty("reference_generated", "referenceGenerated");
		json.addProperty("type", "type");
		json.addProperty("reference", "bookingReference, firstName lastName");
		json.addProperty("checkout_url", "checkoutUrl");
		json.addProperty("is_recurring_token", true);

		client.addProperty("city", "city");
		client.addProperty("email", "email");
		client.addProperty("phone", "phone");
		client.addProperty("zip_code", "zipCode");
		client.addProperty("full_name", "fullName");
		client.addProperty("street_address", "streetAddress");

		issuerDetails.addProperty("brand_name", "brandName");

		payment.addProperty("payment_type", "paymentType");
		payment.addProperty("paid_on", "paidOn");
		payment.addProperty("payment_type", "paymentType");
		payment.addProperty("currency", "currency");
		payment.addProperty("amount", 1000);

		json.add("client", client);
		json.add("payment", payment);
		json.add("purchase", purchase);
		json.add("issuer_details", issuerDetails);
		json.add("products", products);
	}

	@Test
	public void testBuildResponseParameters_EmptyAttemptsArray()
	{
		KlixResponse response = builder.buildResponseParameters(json);

		assertNull(response.getErrorMessage());
	}

	@Test
	public void testBuildPaymentMethods()
	{
		JsonObject paymentMethodsResponse = StringUtil.toJsonObject(PAYMENT_METHODS_JSON);

		List<KlixPaymentMethodGroup> result = builder.buildPaymentMethods(paymentMethodsResponse);

		assertEquals(result.size(), 2);
		assertEquals(result.get(0)
				.getLabel(), "Pay by card");
		assertEquals(result.get(1)
				.getLabel(), "Bank Transfer");
		assertEquals(result.get(0)
				.getName(), "klix");
		assertEquals(result.get(1)
				.getName(), "bank");
		assertEquals(result.get(0)
				.getLogoUrls()
				.size(), 1);
		assertEquals(result.get(1)
				.getLogoUrls()
				.size(), 2);
		assertEquals(result.get(0)
				.getWhitelistJson()
				.size(), 1);
		assertEquals(result.get(1)
				.getWhitelistJson()
				.size(), 4);
	}

	@Test
	public void testBuildPaymentMethods_EmptyJson()
	{
		assertEquals(0, builder.buildPaymentMethods(new JsonObject())
				.size());
	}

	@Test
	public void testBuildPaymentMethods_FiltersApplePayFromKlixCard_SingleMethod()
	{
		JsonObject paymentMethodsResponse = StringUtil.toJsonObject("{\n" + "   \"names\":{\n"
				+ "      \"klix_apple_pay\":\"Apple Pay\"\n" + "   },\n" + "   \"logos\":{\n"
				+ "      \"klix_apple_pay\":\"applePayLogo\"\n" + "   },\n" + "   \"payment_method_groups\":[\n"
				+ "      {\n" + "         \"name\":\"klix_card\",\n" + "         \"logo\":\"klixCardLogo\",\n"
				+ "         \"label\":\"Pay by card\",\n" + "         \"methods\":[\n"
				+ "            \"klix_apple_pay\"\n" + "         ]\n" + "      }\n" + "   ]\n" + "}");

		List<KlixPaymentMethodGroup> paymentMethods = builder.buildPaymentMethods(paymentMethodsResponse);
		assertEquals(1, paymentMethods.size());
		assertEquals("klix_card", paymentMethods.get(0).getName());
		assertEquals("Pay by card", paymentMethods.get(0).getLabel());
		assertEquals(0, paymentMethods.get(0).getLogoUrls().size());
		assertEquals(1, paymentMethods.get(0).getWhitelistJson().size());
	}

	@Test
	public void testBuildPaymentMethods_FiltersGooglePayFromKlixCard_SingleMethod()
	{
		JsonObject paymentMethodsResponse = StringUtil.toJsonObject("{\n" + "   \"names\":{\n"
				+ "      \"klix_google_pay\":\"Google Pay\"\n" + "   },\n" + "   \"logos\":{\n"
				+ "      \"klix_google_pay\":\"googlePayLogo\"\n" + "   },\n" + "   \"payment_method_groups\":[\n"
				+ "      {\n" + "         \"name\":\"klix_card\",\n" + "         \"logo\":\"klixCardLogo\",\n"
				+ "         \"label\":\"Pay by card\",\n" + "         \"methods\":[\n"
				+ "            \"klix_google_pay\"\n" + "         ]\n" + "      }\n" + "   ]\n" + "}");

		List<KlixPaymentMethodGroup> paymentMethods = builder.buildPaymentMethods(paymentMethodsResponse);
		assertEquals(1, paymentMethods.size());
		assertEquals("klix_card", paymentMethods.get(0).getName());
		assertEquals("Pay by card", paymentMethods.get(0).getLabel());
		assertEquals(0, paymentMethods.get(0).getLogoUrls().size());
		assertEquals(1, paymentMethods.get(0).getWhitelistJson().size());
	}

	@Test
	public void testBuildPaymentMethods_FiltersAppleAndGooglePayFromKlixCard_MultipleMethods()
	{
		JsonObject paymentMethodsResponse = StringUtil.toJsonObject("{\n" + "   \"names\":{\n"
				+ "      \"klix_card\":\"Card\",\n" + "      \"klix_apple_pay\":\"Apple Pay\",\n"
				+ "      \"klix_google_pay\":\"Google Pay\",\n" + "      \"klix_paypal\":\"PayPal\"\n" + "   },\n"
				+ "   \"logos\":{\n" + "      \"klix_card\":\"cardLogo\",\n"
				+ "      \"klix_apple_pay\":\"applePayLogo\",\n" + "      \"klix_google_pay\":\"googlePayLogo\",\n"
				+ "      \"klix_paypal\":\"paypalLogo\"\n" + "   },\n" + "   \"payment_method_groups\":[\n"
				+ "      {\n" + "         \"name\":\"klix_card\",\n" + "         \"logo\":\"\",\n"
				+ "         \"label\":\"Pay by card\",\n" + "         \"methods\":[\n" + "            \"klix_card\",\n"
				+ "            \"klix_apple_pay\",\n" + "            \"klix_google_pay\",\n"
				+ "            \"klix_paypal\"\n" + "         ]\n" + "      }\n" + "   ]\n" + "}");

		List<KlixPaymentMethodGroup> paymentMethods = builder.buildPaymentMethods(paymentMethodsResponse);
		assertEquals(1, paymentMethods.size());
		assertEquals("klix_card", paymentMethods.get(0).getName());
		assertEquals("Pay by card", paymentMethods.get(0).getLabel());
		assertEquals(2, paymentMethods.get(0).getLogoUrls().size());
		assertEquals(4, paymentMethods.get(0).getWhitelistJson().size());
	}

	@Test
	public void testBuildPaymentMethods_DoesNotFilterApplePayFromOtherGroups()
	{
		JsonObject paymentMethodsResponse = StringUtil.toJsonObject("{\n" + "   \"names\":{\n"
				+ "      \"klix_apple_pay\":\"Apple Pay\"\n" + "   },\n" + "   \"logos\":{\n"
				+ "      \"klix_apple_pay\":\"applePayLogo\"\n" + "   },\n" + "   \"payment_method_groups\":[\n"
				+ "      {\n" + "         \"name\":\"klix_wallet\",\n" + "         \"logo\":\"walletLogo\",\n"
				+ "         \"label\":\"Digital Wallet\",\n" + "         \"methods\":[\n"
				+ "            \"klix_apple_pay\"\n" + "         ]\n" + "      }\n" + "   ]\n" + "}");

		List<KlixPaymentMethodGroup> paymentMethods = builder.buildPaymentMethods(paymentMethodsResponse);
		assertEquals(1, paymentMethods.size());
		assertEquals("klix_wallet", paymentMethods.get(0).getName());
		assertEquals("Digital Wallet", paymentMethods.get(0).getLabel());
		assertEquals(1, paymentMethods.get(0).getLogoUrls().size());
		assertEquals(1, paymentMethods.get(0).getWhitelistJson().size());
	}
}
