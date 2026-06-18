package com.kmp.aeroparker.application.model;

import java.util.ArrayList;
import java.util.List;

import com.kmp.aeroparker.application.model.enums.PaymentStepFields;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesCrmOptIn;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailsConfig
{
	private List<Integer> mandatoryFields = new ArrayList<>();
	private List<Integer> paymentStepFields = new ArrayList<>();
	private List<Integer> titleFields = new ArrayList<>();
	private boolean showOptIn;
	private List<AffiliatesCrmOptIn> affiliatesCrmOptIn = new ArrayList<>();

	public boolean showCounties()
	{
		return paymentStepFields.contains(PaymentStepFields.COUNTY.getId());
	}

	public boolean showVehicleDetails()
	{
		return paymentStepFields.stream()
				.anyMatch(x -> x == PaymentStepFields.CAR_MAKE.getId() || x == PaymentStepFields.CAR_MODEL.getId()
						|| x == PaymentStepFields.CAR_REGISTRATION.getId());
	}

	public boolean showAdditionalDetails()
	{
		return paymentStepFields.stream()
				.anyMatch(x -> x == PaymentStepFields.REFERRED_BY_FRIEND.getId());
	}
}