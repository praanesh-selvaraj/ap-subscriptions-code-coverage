package com.kmp.aeroparker.application.builder;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.SiteService;
import com.kmp.aeroparker.application.db.service.SubscriptionService;
import com.kmp.aeroparker.application.model.DetailsConfig;
import com.kmp.aeroparker.application.model.enums.BuilderType;
import com.kmp.aeroparker.application.model.enums.PaymentStepFields;
import com.kmp.aeroparker.application.model.interfaces.IBuilder;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentStepFieldsSubscriptionProducts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.PaymentStepFieldsSubscriptions;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionProduct;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfig;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfigKeys;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ConfigBuilder implements IBuilder
{
	private static final Integer MANDATORY_FIELD = 1;
	private final SiteService service;
	private final AffiliateService affService;
	private final SubscriptionService subscriptionService;

	public DetailsConfig build(final int siteId, final int affId, final AffiliateConfig affiliateConfig, int productId)
	{
		DetailsConfig config = new DetailsConfig();
		List<PaymentStepFieldsSubscriptions> paymentStepFields = service.fetchPaymentStepFieldsSubscriptionBySiteId(siteId);
		SubscriptionProduct product = subscriptionService.fetchSubscriptionProductById(productId);

		List<Integer> mandatoryPaymentStepFieldIds = new ArrayList<>();
		List<Integer> paymentStepFieldIds = new ArrayList<>();
		List<Integer> titleFieldIds = new ArrayList<>();
		for (PaymentStepFieldsSubscriptions paymentStepFieldIncluded : paymentStepFields)
		{
			if (paymentStepFieldIncluded.getFieldId() < 37 || paymentStepFieldIncluded.getFieldId() > 41)
			{
				if (paymentStepFieldIncluded.getMandatory() == MANDATORY_FIELD)
				{
					// Get all mandatory fields
					mandatoryPaymentStepFieldIds.add(paymentStepFieldIncluded.getFieldId());
				}
				// Get all fields to show on detail step
				paymentStepFieldIds.add(paymentStepFieldIncluded.getFieldId());
			}
			else
			{
				titleFieldIds.add(paymentStepFieldIncluded.getFieldId());
			}
		}
		if (product.getDisplayPaymentStepFields())
		{
			List<PaymentStepFieldsSubscriptionProducts> productPaymentStepFields = 
					subscriptionService.fetchPaymentStepFieldsSubscriptionProductByProductId(productId);
			List<Integer> subscriptionProductPaymentStepFields = PaymentStepFields.getSubscriptionProductPaymentStepFieldIds();
			paymentStepFieldIds.removeIf(id -> subscriptionProductPaymentStepFields.contains(id));
			mandatoryPaymentStepFieldIds.removeIf(id -> subscriptionProductPaymentStepFields.contains(id));
			for (PaymentStepFieldsSubscriptionProducts productPaymentStepField : productPaymentStepFields)
			{
				int fieldId = productPaymentStepField.getFieldId();
				paymentStepFieldIds.add(fieldId);
				if (productPaymentStepField.getMandatory())
				{
					mandatoryPaymentStepFieldIds.add(fieldId);
				}
			}
		}
		config.setMandatoryFields(mandatoryPaymentStepFieldIds);
		config.setPaymentStepFields(paymentStepFieldIds);
		config.setTitleFields(validateTitleFields(titleFieldIds));
		boolean showOptIn = affiliateConfig.getConfigValue_Boolean(AffiliateConfigKeys.ENABLE_OPT_INS);
		config.setShowOptIn(showOptIn);
		if (showOptIn)
		{
			config.setAffiliatesCrmOptIn(affService.fetchAffiliateCrmOptInByAffiliateId(affId));
		}
		return config;
	}

	private List<Integer> validateTitleFields(final List<Integer> titleFieldIds)
	{
		if (titleFieldIds.isEmpty())
		{
			titleFieldIds.addAll(PaymentStepFields.getTitleFieldIds());
		}
		return titleFieldIds;
	}

	@Override
	public BuilderType getType()
	{
		return BuilderType.CONFIG;
	}
}