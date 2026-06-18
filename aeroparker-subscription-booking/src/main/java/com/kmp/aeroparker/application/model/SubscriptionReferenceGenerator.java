package com.kmp.aeroparker.application.model;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.application.db.service.AffiliateService;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.model.enums.BuilderType;
import com.kmp.aeroparker.application.model.interfaces.IBuilder;
import com.kmp.aeroparker.i18n.stringutil.StringUtil;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.payments.tables.pojos.Affiliates;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class SubscriptionReferenceGenerator implements IBuilder
{
	private final ReferenceDescriptor descriptor;
	private final BookingService service;
	private final AffiliateService affiliateService;
	public static final String ROUTE_WEB = "W";

	public String generate(final Affiliates affiliate)
	{
		String subscriptionReferenceFormat =
				affiliateService.fetchSubscriptionBookingReferenceFormat(affiliate.getId());
		String bookingReferenceFormat =
				!StringUtil.isNullOrEmpty(subscriptionReferenceFormat) ? subscriptionReferenceFormat
						: affiliate.getBookingReferenceFormat();
		String bookingReferencePrefix = affiliate.getBookingReferencePrefix();
		int siteId = affiliate.getSiteid();
		int affId = affiliate.getId();
		return generate(siteId, affId, bookingReferenceFormat, "SC", bookingReferencePrefix);
	}

	private String generate(final int siteId, final int affId, final String bookingReferenceFormat, final String productCode,
			final String bookingReferencePrefix)
	{
		String reference = "";
		boolean unique = false;
		while (!unique)
		{
			reference = descriptor.generateReference(siteId, bookingReferenceFormat, productCode, ROUTE_WEB, bookingReferencePrefix);
			SubscriptionBooking booking = service.fetchBookingByReferenceAndAffiliateId(reference, affId);
			if (booking == null)
			{
				unique = true;
			}
		}
		return reference;
	}

	@Override
	public BuilderType getType()
	{
		return BuilderType.REFERENCE;
	}
}