package com.kmp.aeroparker.application.builder;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.date.utils.DateUtil;
import com.kmp.aeroparker.subscription.payments.model.SubscriptionBookingData;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

@Component
public class ContactBuilder
{
	public void build(Contacts contact, final SubscriptionBookingData bookingData)
	{
		contact.setSiteId(bookingData.getSiteId());
		contact.setLanguageId(bookingData.getCurrentLanguageId());
		contact.setTitle(StringUtil.parse(bookingData.getTitle()));
		contact.setFirstName(StringUtil.parse(bookingData.getFname()));
		contact.setLastName(StringUtil.parse(bookingData.getLname()));
		contact.setEmailAddress(StringUtil.parse(bookingData.getEmail()));
		contact.setAddress1(StringUtil.parse(bookingData.getAddr1()));
		contact.setAddress2(StringUtil.parse(bookingData.getAddr2()));
		contact.setTown(StringUtil.parse(bookingData.getTown()));
		contact.setCounty(StringUtil.parse(bookingData.getCounty()));
		contact.setPostcode(StringUtil.parse(bookingData.getPostcode()));
		contact.setTelNo(StringUtil.parse(bookingData.getTelno()));
		contact.setMobileNo(StringUtil.parse(bookingData.getTelno()));
		contact.setModified(Timestamp.valueOf(DateUtil.nowLocalDateTime(bookingData.getTimeZone())));
		contact.setPassword("");
		contact.setEmailOptIn(bookingData.isEmailOptIn() ? 1 : 0);
		contact.setSmsOptIn(bookingData.isSmsOptIn() ? 1 : 0);
	}
}