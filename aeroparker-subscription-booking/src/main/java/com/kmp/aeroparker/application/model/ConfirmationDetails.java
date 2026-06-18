package com.kmp.aeroparker.application.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmationDetails
{
	private int bookingId;
	private String reference;
	private String bookingDateTime;
	private String name;
	private String email;
	private String vatNo;
	private String vatCompany;
	private String vatAddress;
	private BigDecimal vatRate;
	private BigDecimal stateTaxRate;
	private BigDecimal taxDivisor;
	private BigDecimal grandTotalBeforeTax;
	private BigDecimal grandTotalTax;
	private BigDecimal grandTotal;
	private BigDecimal totalBookingFee;
	private List<ConfirmationProductDetails> confirmationProductDetailList = new ArrayList<>();
	private Map<Integer, String> confirmationMessages = new HashMap<>();
}