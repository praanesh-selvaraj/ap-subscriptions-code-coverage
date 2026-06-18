package com.kmp.aeroparker.application.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmationProductDetails
{
	private String startDate;
	private String endDate;
	private String displayName;
	private boolean isSeasonTicket;
}