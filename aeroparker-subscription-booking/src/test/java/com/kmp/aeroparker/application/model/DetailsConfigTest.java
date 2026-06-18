package com.kmp.aeroparker.application.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.kmp.aeroparker.application.model.enums.PaymentStepFields;

class DetailsConfigTest
{
	@Test
	void testShowCounties()
	{
		DetailsConfig config = new DetailsConfig();
		config.setPaymentStepFields(Arrays.asList(PaymentStepFields.STREET.getId(), PaymentStepFields.COUNTY.getId()));
		assertThat(config.showCounties()).isTrue();
	}

	@Test
	void testShowVehicleDetails_Car_Make()
	{
		DetailsConfig config = new DetailsConfig();
		config.setPaymentStepFields(Arrays.asList(PaymentStepFields.CAR_MAKE.getId()));
		assertThat(config.showVehicleDetails()).isTrue();
	}

	@Test
	void testShowVehicleDetails_Car_Model()
	{
		DetailsConfig config = new DetailsConfig();
		config.setPaymentStepFields(Arrays.asList(PaymentStepFields.CAR_MODEL.getId()));
		assertThat(config.showVehicleDetails()).isTrue();
	}

	@Test
	void testShowVehicleDetails_Car_Registration()
	{
		DetailsConfig config = new DetailsConfig();
		config.setPaymentStepFields(Arrays.asList(PaymentStepFields.CAR_REGISTRATION.getId()));
		assertThat(config.showVehicleDetails()).isTrue();
	}

	@Test
	void testShowVehicleDetails_Null()
	{
		DetailsConfig config = new DetailsConfig();
		config.setPaymentStepFields(Arrays.asList(PaymentStepFields.COUNTY.getId()));
		assertThat(config.showVehicleDetails()).isFalse();
	}

	@Test
	void testShowAdditionalDetails()
	{
		DetailsConfig config = new DetailsConfig();

		config.setPaymentStepFields(Arrays.asList(PaymentStepFields.REFERRED_BY_FRIEND.getId()));

		assertThat(config.showAdditionalDetails()).isTrue();
	}

	@Test
	void testShowAdditionalDetails_Null()
	{
		DetailsConfig config = new DetailsConfig();

		assertThat(config.showAdditionalDetails()).isFalse();
	}
}