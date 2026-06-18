package com.kmp.aeroparker.application.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.application.builder.SubscriptionBookingRecord;
import com.kmp.aeroparker.application.config.CustomConnectionProvider;
import com.kmp.aeroparker.application.db.service.BookingService;
import com.kmp.aeroparker.application.db.service.ContactService;
import com.kmp.aeroparker.application.email.dispatcher.ConfirmationEmailDispatcher;
import com.kmp.aeroparker.application.model.EmailDispatcherParameters;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.application.model.enums.SubscriptionEmailType;
import com.kmp.aeroparker.sqs.freemarker.FreemarkerSQSProducer;
import com.kmp.aeroparker.sqs.freemarker.FreemarkerSQSStatus;
import com.kmp.aeroparker.sqs.freemarker.FreemarkerSQSType;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.Contacts;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBooking;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionBookingCustomerDetails;
import com.kmp.aeroparker.subscription.payments.affiliate.config.AffiliateConfigKeys;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Service
public class FreemarkerSubmitter
{
	private final BookingService bookingService;
	private final ContactService contactService;
	private final SubscriptionControllerService controllerService;
	private final FreemarkerSQSProducer freemarkerSQSProducer;
	private final CustomConnectionProvider customConnectionProvider;
	private final ConfirmationEmailDispatcher emailDispatcher;

	public String resendConfirmationEmail(HttpServletRequest req, final Model model, String reference, SubscriptionConfigBean requestBean)
	{
		JsonObject data = new JsonObject();
		boolean success = false;

		SubscriptionBooking bookingData =
				bookingService.fetchBookingByReferenceAndAffiliateId(reference, requestBean.getAffiliateId());
		int bookingId = bookingData == null ? 0 : bookingData.getId();
		SubscriptionBookingRecord bookingRecord =
				bookingService.fetchSubscriptionBooking(bookingId, requestBean.getAffiliateId());

		if (bookingRecord != null)
		{
			int contactId = bookingData.getContactId();
			Contacts contact = contactService.fetchById(contactId);

			if (contact != null)
			{
				int currentLangaugeId = requestBean.getCurrentLanguageId();
				int defaultLanguageId = requestBean.getDefaultLanguageId();

				EmailDispatcherParameters emailDispatcherParameters = EmailDispatcherParameters.builder()
						.withEmailType(SubscriptionEmailType.CONFIRMATION)
						.withSite(requestBean.getSite())
						.withCurrentLanguageId(currentLangaugeId)
						.withDefaultLanguageId(defaultLanguageId)
						.withBookingRecord(bookingRecord)
						.withContactId(contactId)
						.withTimeZone(requestBean.getTimeZone())
						.withAffiliate(requestBean.getAffiliate())
						.withServletSchema(req.getScheme())
						.withServletName(req.getServerName())
						.withEmailFromAffiliate(requestBean.getAffiliateConfig()
								.getConfigValue_Boolean(AffiliateConfigKeys.ENABLE_ACTIVATE_ACCOUNT_EMAIL))
						.build();
				int siteId = requestBean.getSiteId();
				if (requestBean.getAffiliateConfig()
						.getConfigValue_Boolean(AffiliateConfigKeys.ENABLE_EMAIL_CONFIRMATIONS))
				{
					log.debug("Preparing to send email");
					if (controllerService.isFreemarkerEnabled(siteId))
					{
						sendFreemarkerSQSMessage(bookingRecord.getBooking(), bookingRecord.getCustomerDetails(), siteId,
								requestBean);
					}
					else
					{
						emailDispatcher.sendEmail(emailDispatcherParameters);
					}

					success = true;
				}
			}
		}
		data.addProperty("sent", success);
		return data.toString();
	}

	private void sendFreemarkerSQSMessage(SubscriptionBooking booking,
			SubscriptionBookingCustomerDetails customerDetails, int siteId, SubscriptionConfigBean requestBean)
	{
		freemarkerSQSProducer.sendMessage(requestBean.getFreemarkerQueueUrl(), booking.getId(), booking.getReference(),
				FreemarkerSQSStatus.NEW, FreemarkerSQSType.SUBSCRIPTION, customConnectionProvider.getSchema(), siteId,
				false, customerDetails.getEmailAddress());
	}
}
