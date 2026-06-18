package com.kmp.aeroparker.application.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.SiteService;
import com.kmp.aeroparker.application.db.service.SubscriptionService;
import com.kmp.aeroparker.application.model.enums.BuilderType;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliatesCrmOptIn;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentStepFieldsSubscriptionProducts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentStepFieldsSubscriptions;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfigKeys;

import io.github.benas.randombeans.api.EnhancedRandom;

@ExtendWith(MockitoExtension.class)
class ConfigBuilderTest
{
	@Mock
	private AffiliateService affService;
	@Mock
	private SiteService service;
	@Mock
	private SubscriptionService subscriptionService;
	@Mock
	private SubscriptionProduct product;
	@InjectMocks
	private ConfigBuilder builder;

	@Test
	void testBuild()
	{
		AffiliateConfig affiliateConfig = mock(AffiliateConfig.class);
		when(affiliateConfig.getConfigValue_Boolean(eq(AffiliateConfigKeys.ENABLE_OPT_INS))).thenReturn(true);
		List<PaymentStepFieldsSubscriptions> paymentStepFields = new ArrayList<>();
		PaymentStepFieldsSubscriptions fieldsIncluded = EnhancedRandom.random(PaymentStepFieldsSubscriptions.class);
		fieldsIncluded.setMandatory(1);
		paymentStepFields.add(fieldsIncluded);
		PaymentStepFieldsSubscriptions fieldsIncluded2 = EnhancedRandom.random(PaymentStepFieldsSubscriptions.class);
		fieldsIncluded2.setMandatory(0);
		paymentStepFields.add(fieldsIncluded2);
		PaymentStepFieldsSubscriptions fieldsIncluded3 = EnhancedRandom.random(PaymentStepFieldsSubscriptions.class);
		fieldsIncluded3.setMandatory(0);
		fieldsIncluded3.setFieldId(37);
		paymentStepFields.add(fieldsIncluded3);
		when(affService.fetchAffiliateCrmOptInByAffiliateId(anyInt())).thenReturn(EnhancedRandom.randomListOf(1, AffiliatesCrmOptIn.class));
		when(service.fetchPaymentStepFieldsSubscriptionBySiteId(anyInt())).thenReturn(paymentStepFields);
		when(subscriptionService.fetchSubscriptionProductById(anyInt())).thenReturn(product);
		assertThat(builder.build(1, 1, affiliateConfig, 1)).isNotNull()
				.hasNoNullFieldsOrProperties();
		verify(service).fetchPaymentStepFieldsSubscriptionBySiteId(anyInt());
		verify(affService).fetchAffiliateCrmOptInByAffiliateId(anyInt());
	}

	@Test
	void testBuild_CrmOptIn_False()
	{
		AffiliateConfig affiliateConfig = mock(AffiliateConfig.class);
		when(affiliateConfig.getConfigValue_Boolean(eq(AffiliateConfigKeys.ENABLE_OPT_INS))).thenReturn(false);
		List<PaymentStepFieldsSubscriptions> paymentStepFields = new ArrayList<>();
		PaymentStepFieldsSubscriptions fieldsIncluded = EnhancedRandom.random(PaymentStepFieldsSubscriptions.class);
		fieldsIncluded.setMandatory(1);
		paymentStepFields.add(fieldsIncluded);
		PaymentStepFieldsSubscriptions fieldsIncluded2 = EnhancedRandom.random(PaymentStepFieldsSubscriptions.class);
		fieldsIncluded2.setMandatory(0);
		paymentStepFields.add(fieldsIncluded2);
		when(service.fetchPaymentStepFieldsSubscriptionBySiteId(anyInt())).thenReturn(paymentStepFields);
		when(subscriptionService.fetchSubscriptionProductById(anyInt())).thenReturn(product);
		assertThat(builder.build(1, 1, affiliateConfig, 1)).isNotNull()
				.hasNoNullFieldsOrProperties();
		verify(service).fetchPaymentStepFieldsSubscriptionBySiteId(anyInt());
		verifyNoInteractions(affService);
	}
	
	@Test
	void testBuild_PaymentStepFieldsEnabledForProduct()
	{
		AffiliateConfig affiliateConfig = mock(AffiliateConfig.class);
		when(affiliateConfig.getConfigValue_Boolean(eq(AffiliateConfigKeys.ENABLE_OPT_INS))).thenReturn(true);
		List<PaymentStepFieldsSubscriptions> paymentStepFields = new ArrayList<>();
		PaymentStepFieldsSubscriptions fieldsIncluded = new PaymentStepFieldsSubscriptions();
		fieldsIncluded.setFieldId(1);
		fieldsIncluded.setMandatory(1);
		paymentStepFields.add(fieldsIncluded);
		PaymentStepFieldsSubscriptions fieldsIncluded2 = new PaymentStepFieldsSubscriptions();
		fieldsIncluded2.setFieldId(26);
		fieldsIncluded2.setMandatory(1);
		paymentStepFields.add(fieldsIncluded2);
		PaymentStepFieldsSubscriptions fieldsIncluded3 = new PaymentStepFieldsSubscriptions();
		fieldsIncluded3.setFieldId(21);
		fieldsIncluded3.setMandatory(0);
		paymentStepFields.add(fieldsIncluded3);
		List<PaymentStepFieldsSubscriptionProducts> productPaymentStepFields = new ArrayList<>();
		PaymentStepFieldsSubscriptionProducts productPaymentStepField = new PaymentStepFieldsSubscriptionProducts();
		productPaymentStepField.setSubscriptionProductId(product.getId());
		productPaymentStepField.setFieldId(25);
		productPaymentStepField.setMandatory(true);
		productPaymentStepFields.add(productPaymentStepField);
		when(affService.fetchAffiliateCrmOptInByAffiliateId(anyInt())).thenReturn(EnhancedRandom.randomListOf(1, AffiliatesCrmOptIn.class));
		when(service.fetchPaymentStepFieldsSubscriptionBySiteId(anyInt())).thenReturn(paymentStepFields);
		when(subscriptionService.fetchSubscriptionProductById(anyInt())).thenReturn(product);
		when(product.getDisplayPaymentStepFields()).thenReturn(true);
		when(subscriptionService.fetchPaymentStepFieldsSubscriptionProductByProductId(anyInt())).thenReturn(productPaymentStepFields);
		
		assertTrue(builder.build(1, 1, affiliateConfig, 1).getPaymentStepFields().contains(25));
	}

	@Test
	void testGetType()
	{
		assertThat(builder.getType()).isEqualTo(BuilderType.CONFIG);
	}
}