package com.kmp.aeroparker.subscription.payments.stripe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stripe.param.PaymentIntentUpdateParams;

@ExtendWith(MockitoExtension.class)
class StripePaymentRequestSenderTest {

	@InjectMocks
	private StripePaymentRequestSender requestSender;

	@Test
	void testBuildParametersForMetadata_withoutMembershipId()
	{
		PaymentIntentUpdateParams params = requestSender.buildParametersForMetadata("REF123", "AFF001",
				"user@example.com", "1234567890", "CAR123", "GUID123", "AFFID123", "false");

		@SuppressWarnings("unchecked")
		Map<String, String> metadata = (Map<String, String>) params.getMetadata();

		assertThat(metadata).containsEntry("Booking_reference", "REF123")
				.containsEntry("Payment_source", StripePaymentRequestSender.PAYMENT_SOURCE_DUBLIN)
				.containsEntry("Affiliate", "AFF001")
				.containsEntry("Customer_email", "user@example.com")
				.containsEntry("Customer_phone", "1234567890")
				.containsEntry("Customer_car_reg", "CAR123")
				.containsEntry("Reservation_guid", "GUID123")
				.containsEntry("Affiliate_id", "AFFID123")
				.containsEntry(StripePaymentRequestSender.METADATA_MEMBERSHIP_ID, null);
	}

	@Test
	void testBuildParametersForMetadata_withMembershipId()
	{
		PaymentIntentUpdateParams params = requestSender.buildParametersForMetadata("REF456", "AFF002",
				"other@example.com", "0987654321", "CAR456", "GUID456", "AFFID456", "MEM123", "false");

		@SuppressWarnings("unchecked")
		Map<String, String> metadata = (Map<String, String>) params.getMetadata();

		assertThat(metadata).containsEntry("Booking_reference", "REF456")
				.containsEntry("Payment_source", StripePaymentRequestSender.PAYMENT_SOURCE_DUBLIN)
				.containsEntry("Affiliate", "AFF002")
				.containsEntry("Customer_email", "other@example.com")
				.containsEntry("Customer_phone", "0987654321")
				.containsEntry("Customer_car_reg", "CAR456")
				.containsEntry("Reservation_guid", "GUID456")
				.containsEntry("Affiliate_id", "AFFID456")
				.containsEntry(StripePaymentRequestSender.METADATA_MEMBERSHIP_ID, "MEM123");
	}

	@Test
	void testBuildParametersForMetadata_withMembershipId_withRenewal()
	{
		PaymentIntentUpdateParams params = requestSender.buildParametersForMetadata("REF456", "AFF002",
				"other@example.com", "0987654321", "CAR456", "GUID456", "AFFID456", "MEM123", "true");

		@SuppressWarnings("unchecked")
		Map<String, String> metadata = (Map<String, String>) params.getMetadata();

		assertThat(metadata).containsEntry("Booking_reference", "REF456")
				.containsEntry("Payment_source", StripePaymentRequestSender.PAYMENT_SOURCE_DUBLIN)
				.containsEntry("Affiliate", "AFF002")
				.containsEntry("Customer_email", "other@example.com")
				.containsEntry("Customer_phone", "0987654321")
				.containsEntry("Customer_car_reg", "CAR456")
				.containsEntry("Reservation_guid", "GUID456")
				.containsEntry("Affiliate_id", "AFFID456")
				.containsEntry(StripePaymentRequestSender.METADATA_MEMBERSHIP_ID, "MEM123")
				.containsEntry(StripePaymentRequestSender.METADATA_RENEWAL, "true");
	}

	@Test
	void testBuildMembershipIdParameterForMetadata() {
		String membershipId = "123456";
		PaymentIntentUpdateParams params = requestSender.buildMembershipIdParameterForMetadata(membershipId);
		Object objectMetadata = params.getMetadata();
		if (objectMetadata instanceof Map<?, ?>) {
			Map<?, ?> tempMetadata = (Map<?, ?>) objectMetadata;

			Map<String, String> metadata = new HashMap<>();
			for (Map.Entry<?, ?> entry : tempMetadata.entrySet()) {
				if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
					metadata.put((String) entry.getKey(), (String) entry.getValue());
				} else {
					fail("Metadata contains non-string key or value");
				}
			}

			assertThat(metadata)
					.containsKey(StripePaymentRequestSender.METADATA_MEMBERSHIP_ID)
					.containsEntry(StripePaymentRequestSender.METADATA_MEMBERSHIP_ID, membershipId);
		} else {
			fail("Intent metadata is not a Map");
		}
	}
}