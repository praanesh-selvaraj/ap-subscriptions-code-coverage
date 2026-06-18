package com.kmp.aeroparker.application.model.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PaymentStepFieldsTest
{

	@Test
	void testSize()
	{
		assertThat(PaymentStepFields.values().length).isEqualTo(46);
	}

	@Test
	void testGetId()
	{
		assertThat(PaymentStepFields.TITLE.getId()).isEqualTo(1);
		assertThat(PaymentStepFields.FIRST_NAME.getId()).isEqualTo(2);
		assertThat(PaymentStepFields.LAST_NAME.getId()).isEqualTo(3);
		assertThat(PaymentStepFields.EMAIL.getId()).isEqualTo(4);
		assertThat(PaymentStepFields.MOBILE_NUMBER.getId()).isEqualTo(5);
		assertThat(PaymentStepFields.POSTCODE.getId()).isEqualTo(6);
		assertThat(PaymentStepFields.STREET.getId()).isEqualTo(7);
		assertThat(PaymentStepFields.ADDRESS2.getId()).isEqualTo(8);
		assertThat(PaymentStepFields.TOWN.getId()).isEqualTo(9);
		assertThat(PaymentStepFields.TAX_RECEIPT.getId()).isEqualTo(10);
		assertThat(PaymentStepFields.COMPANY.getId()).isEqualTo(11);
		assertThat(PaymentStepFields.COUNTY.getId()).isEqualTo(12);
		assertThat(PaymentStepFields.ADDRESS1_RECEIPT.getId()).isEqualTo(13);
		assertThat(PaymentStepFields.ADDRESS2_RECEIPT.getId()).isEqualTo(14);
		assertThat(PaymentStepFields.POSTCODE_RECEIPT.getId()).isEqualTo(15);
		assertThat(PaymentStepFields.TOWN_RECEIPT.getId()).isEqualTo(16);
		assertThat(PaymentStepFields.COUNTY_RECEIPT.getId()).isEqualTo(17);
		assertThat(PaymentStepFields.TAX_IDENTIFICATION_NUMBER.getId()).isEqualTo(18);
		assertThat(PaymentStepFields.OUTGOING_FLIGHT_NO.getId()).isEqualTo(19);
		assertThat(PaymentStepFields.DESTINATION.getId()).isEqualTo(20);
		assertThat(PaymentStepFields.COUNTRY.getId()).isEqualTo(21);
		assertThat(PaymentStepFields.COUNTRY_RECEIPT.getId()).isEqualTo(22);
		assertThat(PaymentStepFields.AIRLINE.getId()).isEqualTo(23);
		assertThat(PaymentStepFields.REASON_FOR_TRAVEL.getId()).isEqualTo(24);
		assertThat(PaymentStepFields.CAR_REGISTRATION.getId()).isEqualTo(25);
		assertThat(PaymentStepFields.CAR_MAKE.getId()).isEqualTo(26);
		assertThat(PaymentStepFields.CAR_MODEL.getId()).isEqualTo(27);
		assertThat(PaymentStepFields.CAR_COLOUR.getId()).isEqualTo(28);
		assertThat(PaymentStepFields.OUTGOING_AIRLINE.getId()).isEqualTo(29);
		assertThat(PaymentStepFields.RETURN_FLIGHT_NO.getId()).isEqualTo(30);
		assertThat(PaymentStepFields.RETURN_AIRLINE.getId()).isEqualTo(31);
		assertThat(PaymentStepFields.CAR_COUNTRY.getId()).isEqualTo(32);
		assertThat(PaymentStepFields.HOUSE_NAME_NUM.getId()).isEqualTo(33);
		assertThat(PaymentStepFields.HOUSE_NUM_ADD.getId()).isEqualTo(34);
		assertThat(PaymentStepFields.CAR_STATE.getId()).isEqualTo(35);
		assertThat(PaymentStepFields.CONFIRM_EMAIL.getId()).isEqualTo(36);
		assertThat(PaymentStepFields.TITLE_MR.getId()).isEqualTo(37);
		assertThat(PaymentStepFields.TITLE_MISS.getId()).isEqualTo(38);
		assertThat(PaymentStepFields.TITLE_MRS.getId()).isEqualTo(39);
		assertThat(PaymentStepFields.TITLE_MS.getId()).isEqualTo(40);
		assertThat(PaymentStepFields.TITLE_DR.getId()).isEqualTo(41);
		assertThat(PaymentStepFields.CAR_REG_YEAR.getId()).isEqualTo(42);
		assertThat(PaymentStepFields.CAR_POL.getId()).isEqualTo(43);
		assertThat(PaymentStepFields.COUNTRY_CODES.getId()).isEqualTo(44);
		assertThat(PaymentStepFields.COMPANY_VAT_REGISTRATION_NUMBER.getId()).isEqualTo(47);
		assertThat(PaymentStepFields.REFERRED_BY_FRIEND.getId()).isEqualTo(53);
	}

	@Test
	void testGetLabel()
	{
		assertThat(PaymentStepFields.TITLE.getLabel()).isEqualTo("Title");
		assertThat(PaymentStepFields.FIRST_NAME.getLabel()).isEqualTo("Cardholder First Name");
		assertThat(PaymentStepFields.LAST_NAME.getLabel()).isEqualTo("Cardholder Last Name");
		assertThat(PaymentStepFields.EMAIL.getLabel()).isEqualTo("Cardholder Email");
		assertThat(PaymentStepFields.MOBILE_NUMBER.getLabel()).isEqualTo("Mobile Number");
		assertThat(PaymentStepFields.POSTCODE.getLabel()).isEqualTo("Postcode");
		assertThat(PaymentStepFields.STREET.getLabel()).isEqualTo("Street / no.");
		assertThat(PaymentStepFields.ADDRESS2.getLabel()).isEqualTo("Address Line 2");
		assertThat(PaymentStepFields.TOWN.getLabel()).isEqualTo("Town");
		assertThat(PaymentStepFields.TAX_RECEIPT.getLabel()).isEqualTo("Tax Receipt");
		assertThat(PaymentStepFields.COMPANY.getLabel()).isEqualTo("Company");
		assertThat(PaymentStepFields.COUNTY.getLabel()).isEqualTo("County");
		assertThat(PaymentStepFields.ADDRESS1_RECEIPT.getLabel()).isEqualTo("Address 1 Receipt");
		assertThat(PaymentStepFields.ADDRESS2_RECEIPT.getLabel()).isEqualTo("Address 2 Receipt");
		assertThat(PaymentStepFields.POSTCODE_RECEIPT.getLabel()).isEqualTo("Postcode Receipt");
		assertThat(PaymentStepFields.TOWN_RECEIPT.getLabel()).isEqualTo("Town Receipt");
		assertThat(PaymentStepFields.COUNTY_RECEIPT.getLabel()).isEqualTo("County Receipt");
		assertThat(PaymentStepFields.TAX_IDENTIFICATION_NUMBER.getLabel()).isEqualTo("Tax Identification Number");
		assertThat(PaymentStepFields.OUTGOING_FLIGHT_NO.getLabel()).isEqualTo("Outgoing Flight Number");
		assertThat(PaymentStepFields.DESTINATION.getLabel()).isEqualTo("Destination");
		assertThat(PaymentStepFields.COUNTRY.getLabel()).isEqualTo("Country");
		assertThat(PaymentStepFields.COUNTRY_RECEIPT.getLabel()).isEqualTo("Country Receipt");
		assertThat(PaymentStepFields.AIRLINE.getLabel()).isEqualTo("Airline");
		assertThat(PaymentStepFields.REASON_FOR_TRAVEL.getLabel()).isEqualTo("Reason for travel");
		assertThat(PaymentStepFields.CAR_REGISTRATION.getLabel()).isEqualTo("Car Registration");
		assertThat(PaymentStepFields.CAR_MAKE.getLabel()).isEqualTo("Car Make");
		assertThat(PaymentStepFields.CAR_MODEL.getLabel()).isEqualTo("Car Model");
		assertThat(PaymentStepFields.CAR_COLOUR.getLabel()).isEqualTo("Car Colour");
		assertThat(PaymentStepFields.OUTGOING_AIRLINE.getLabel()).isEqualTo("Outgoing Airline");
		assertThat(PaymentStepFields.RETURN_FLIGHT_NO.getLabel()).isEqualTo("Return Flight Number");
		assertThat(PaymentStepFields.RETURN_AIRLINE.getLabel()).isEqualTo("Return Airline");
		assertThat(PaymentStepFields.CAR_COUNTRY.getLabel()).isEqualTo("Car Country");
		assertThat(PaymentStepFields.HOUSE_NAME_NUM.getLabel()).isEqualTo("House Name/Number");
		assertThat(PaymentStepFields.HOUSE_NUM_ADD.getLabel()).isEqualTo("House Number Addition");
		assertThat(PaymentStepFields.CAR_STATE.getLabel()).isEqualTo("Car State");
		assertThat(PaymentStepFields.CONFIRM_EMAIL.getLabel()).isEqualTo("Confirm Email");
		assertThat(PaymentStepFields.TITLE_MR.getLabel()).isEqualTo("Mr");
		assertThat(PaymentStepFields.TITLE_MISS.getLabel()).isEqualTo("Miss");
		assertThat(PaymentStepFields.TITLE_MRS.getLabel()).isEqualTo("Mrs");
		assertThat(PaymentStepFields.TITLE_MS.getLabel()).isEqualTo("Ms");
		assertThat(PaymentStepFields.TITLE_DR.getLabel()).isEqualTo("Dr");
		assertThat(PaymentStepFields.CAR_REG_YEAR.getLabel()).isEqualTo("Car Registration Year");
		assertThat(PaymentStepFields.CAR_POL.getLabel()).isEqualTo("Car CO<sub>2</sub>");
		assertThat(PaymentStepFields.COUNTRY_CODES.getLabel()).isEqualTo("Country Codes");
		assertThat(PaymentStepFields.COMPANY_VAT_REGISTRATION_NUMBER.getLabel()).isEqualTo("Company VAT Registration Number");
		assertThat(PaymentStepFields.REFERRED_BY_FRIEND.getLabel()).isEqualTo("Refer a friend membership no.");
	}

	@Test
	void testGetById()
	{
		assertThat(PaymentStepFields.getById(10)).isEqualTo(PaymentStepFields.TAX_RECEIPT);
	}

	@Test
	void testGetById_Unlknown()
	{
		assertThat(PaymentStepFields.getById(100)).isNull();
	}

	@Test
	void testGetTitleFieldIds()
	{
		assertThat(PaymentStepFields.getTitleFieldIds()).hasSize(5)
				.contains(37, 38, 39, 40, 41);
	}
	
	@Test
	void testGetSubscriptionProductPaymentStepFieldIds()
	{
		assertThat(PaymentStepFields.getSubscriptionProductPaymentStepFieldIds()).hasSize(4)
				.contains(25, 26, 27, 28);
	}
}