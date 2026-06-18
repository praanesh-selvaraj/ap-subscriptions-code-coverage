package com.kmp.aeroparker.application.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProcessorTypeTest
{
	@Test
	void test_Size()
	{
		assertThat(ProcessorType.values()).hasSize(13);
	}

	@Test
	void test_ValuesOf()
	{
		assertThat(ProcessorType.valueOf("CONTACT")).isEqualTo(ProcessorType.CONTACT);
		assertThat(ProcessorType.valueOf("GUID_BOOKING")).isEqualTo(ProcessorType.GUID_BOOKING);
		assertThat(ProcessorType.valueOf("CUSTOMER_DETAILS")).isEqualTo(ProcessorType.CUSTOMER_DETAILS);
		assertThat(ProcessorType.valueOf("ITEM_BOOKING")).isEqualTo(ProcessorType.ITEM_BOOKING);
		assertThat(ProcessorType.valueOf("VEHICLE_DETAILS")).isEqualTo(ProcessorType.VEHICLE_DETAILS);
		assertThat(ProcessorType.valueOf("RECEIPT_DETAILS")).isEqualTo(ProcessorType.RECEIPT_DETAILS);
		assertThat(ProcessorType.valueOf("PASSWORD_CHOICE")).isEqualTo(ProcessorType.PASSWORD_CHOICE);
		assertThat(ProcessorType.valueOf("BOOKING_PAYMENT")).isEqualTo(ProcessorType.BOOKING_PAYMENT);
		assertThat(ProcessorType.valueOf("PAYMENT_HISTORY")).isEqualTo(ProcessorType.PAYMENT_HISTORY);
		assertThat(ProcessorType.valueOf("LANGUAGE")).isEqualTo(ProcessorType.LANGUAGE);
		assertThat(ProcessorType.valueOf("CAR_PARK")).isEqualTo(ProcessorType.CAR_PARK);
		assertThat(ProcessorType.valueOf("SUBSCRIPTION_CONTACT_MEMBERSHIP"))
				.isEqualTo(ProcessorType.SUBSCRIPTION_CONTACT_MEMBERSHIP);
	}
}