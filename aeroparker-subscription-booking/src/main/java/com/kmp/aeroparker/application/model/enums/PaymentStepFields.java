package com.kmp.aeroparker.application.model.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum PaymentStepFields
{
	TITLE(1, "Title"),
	FIRST_NAME(2, "Cardholder First Name"),
	LAST_NAME(3, "Cardholder Last Name"),
	EMAIL(4, "Cardholder Email"),
	MOBILE_NUMBER(5, "Mobile Number"),
	POSTCODE(6, "Postcode"),
	STREET(7, "Street / no."),
	ADDRESS2(8, "Address Line 2"),
	TOWN(9, "Town"),
	TAX_RECEIPT(10, "Tax Receipt"),
	COMPANY(11, "Company"),
	COUNTY(12, "County"),
	ADDRESS1_RECEIPT(13, "Address 1 Receipt"),
	ADDRESS2_RECEIPT(14, "Address 2 Receipt"),
	POSTCODE_RECEIPT(15, "Postcode Receipt"),
	TOWN_RECEIPT(16, "Town Receipt"),
	COUNTY_RECEIPT(17, "County Receipt"),
	TAX_IDENTIFICATION_NUMBER(18, "Tax Identification Number"),
	OUTGOING_FLIGHT_NO(19, "Outgoing Flight Number"),
	DESTINATION(20, "Destination"),
	COUNTRY(21, "Country"),
	COUNTRY_RECEIPT(22, "Country Receipt"),
	AIRLINE(23, "Airline"),
	REASON_FOR_TRAVEL(24, "Reason for travel"),
	CAR_REGISTRATION(25, "Car Registration"),
	CAR_MAKE(26, "Car Make"), 
	CAR_MODEL(27, "Car Model"),
	CAR_COLOUR(28, "Car Colour"),
	OUTGOING_AIRLINE(29, "Outgoing Airline"), 
	RETURN_FLIGHT_NO(30, "Return Flight Number"),
	RETURN_AIRLINE(31, "Return Airline"),
	CAR_COUNTRY(32, "Car Country"),
	HOUSE_NAME_NUM(33, "House Name/Number"),
	HOUSE_NUM_ADD(34, "House Number Addition"), 
	CAR_STATE(35, "Car State"),
	CONFIRM_EMAIL(36, "Confirm Email"),
	TITLE_MR(37, "Mr"), 
	TITLE_MISS(38, "Miss"), 
	TITLE_MRS(39, "Mrs"), 
	TITLE_MS(40, "Ms"), 
	TITLE_DR(41, "Dr"),
	CAR_REG_YEAR(42, "Car Registration Year"),
	CAR_POL(43, "Car CO<sub>2</sub>"),
	COUNTRY_CODES(44, "Country Codes"),
	COMPANY_VAT_REGISTRATION_NUMBER(47, "Company VAT Registration Number"),
	REFERRED_BY_FRIEND(53, "Refer a friend membership no.");

	private int id;
	private String label;

	/**
	 * @param id
	 * @param label
	 * @return
	 */
	PaymentStepFields(final int id, final String label)
	{
		this.id = id;
		this.label = label;
	}

	/**
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return
	 */
	public String getLabel() {
		return label;
	}
	
	public static List<Integer> getTitleFieldIds()
	{
		final List<Integer> titleFieldIds = new ArrayList<>();
		titleFieldIds.add(TITLE_MR.getId());
		titleFieldIds.add(TITLE_MISS.getId());
		titleFieldIds.add(TITLE_MRS.getId());
		titleFieldIds.add(TITLE_MS.getId());
		titleFieldIds.add(TITLE_DR.getId());
		return titleFieldIds;
	}
	
	public static List<Integer> getSubscriptionProductPaymentStepFieldIds()
	{
		final List<Integer> subscriptionProductPaymentStepFields = new ArrayList<>();
		subscriptionProductPaymentStepFields.add(CAR_REGISTRATION.getId());
		subscriptionProductPaymentStepFields.add(CAR_MAKE.getId());
		subscriptionProductPaymentStepFields.add(CAR_MODEL.getId());
		subscriptionProductPaymentStepFields.add(CAR_COLOUR.getId());
		return subscriptionProductPaymentStepFields;
	}

	public static PaymentStepFields getById(int id)
	{
		return Arrays.asList(PaymentStepFields.values())
				.stream()
				.filter(psf -> psf.getId() == id)
				.findFirst()
				.orElse(null);
	}
}