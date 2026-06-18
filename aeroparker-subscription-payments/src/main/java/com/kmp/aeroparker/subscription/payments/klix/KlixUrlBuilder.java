package com.kmp.aeroparker.subscription.payments.klix;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.webutil.utils.WebUtil;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class KlixUrlBuilder
{
	private static final String HANDLER_SERVLET = "/processPaymentCmd?";

	public String getSuccessRedirectUrl(String frameAncestor, String cmd, int affiliateId, String redirectUrl,
			String guid, String bookingReference, String originalBookingReference, int carParkId, boolean isCallback,
			String startDate, String firstName, String lastName, int siteId)
	{
		String url = buildRedirectUrl(cmd, frameAncestor, affiliateId, redirectUrl, guid, bookingReference, startDate,
				firstName, lastName, siteId);
		return url + "&originalBookingReference=" + originalBookingReference + "&carParkId=" + carParkId + "&callback="
				+ isCallback;
	}

	public String buildRedirectUrl(String cmd, String frameAncestor, int affiliateId, String redirectUrl, String guid,
			String bookingReference, String startDate, String firstName, String lastName, int siteId)
	{
		return frameAncestor + HANDLER_SERVLET + "cmd=" + cmd + "&affiliateId=" + affiliateId + "&redirectUrl="
				+ redirectUrl + "&customerGuid=" + guid + "&bookingReference=" + bookingReference + "&startDate="
				+ startDate + "&firstName=" + WebUtil.urlEncode(firstName) + "&lastName=" + WebUtil.urlEncode(lastName)
				+ "&siteId=" + siteId;
	}
}