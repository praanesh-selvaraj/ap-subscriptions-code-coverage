package com.kmp.aeroparker.application.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kmp.aeroparker.application.model.enums.SubscriptionMinimumTerm;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.SubscriptionAllBookingData;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class SubscriptionDetailsUtil
{
	public boolean showVehicleDetails(SubscriptionAllBookingData bookingData)
	{
		return (!StringUtil.isEmpty(bookingData.getCustomerVehicleReg())
				|| !StringUtil.isEmpty(bookingData.getCustomerVehicleMake())
				|| !StringUtil.isEmpty(bookingData.getCustomerVehicleModel())
				|| !StringUtil.isEmpty(bookingData.getCustomerVehicleColour()));
	}
	
	public String getCustomerAddress(SubscriptionAllBookingData bookingData)
	{
		log.debug("Getting customer address");
		StringBuilder addressBuilder = new StringBuilder();

		if (!StringUtil.isEmpty(bookingData.getCustomerAddress_1()))
		{
			addressBuilder.append(bookingData.getCustomerAddress_1());
		}
		
		addressBuilder = addSectionToAddress(addressBuilder, bookingData.getCustomerAddress_2());
		addressBuilder = addSectionToAddress(addressBuilder, bookingData.getCustomerTown());
		addressBuilder = addSectionToAddress(addressBuilder, bookingData.getCustomerCounty());
		addressBuilder = addSectionToAddress(addressBuilder, bookingData.getCustomerCountry());
		addressBuilder = addSectionToAddress(addressBuilder, bookingData.getCustomerPostcode());
		
		return addressBuilder.toString();
	}
	
	public void formatCardExpiryDate(SubscriptionAllBookingData bookingData)
	{
		log.debug("Formatting card expiry date");
		String expiryDate = bookingData.getCardExpiryDate();
		if (!StringUtil.contains(expiryDate, "/", false) && !StringUtil.isEmpty(expiryDate))
		{
			StringBuilder stringBuilder = new StringBuilder(expiryDate);
			if (expiryDate.length() >= 5)
			{
				if (expiryDate.length() == 5) {
					stringBuilder.insert(0, "0");
				}
				stringBuilder.delete(2, 4);
			}
			stringBuilder.insert(2, "/");
			expiryDate = stringBuilder.toString();
			bookingData.setCardExpiryDate(expiryDate);
		}
	}

	/**
	 * Updates the minimum term length for a given list of {@link SubscriptionAllBookingData} objects. The minimum term
	 * length is determined based on the currently stored value mapped to {@link SubscriptionMinimumTerm}, using its
	 * integer value.
	 * <p>
	 * If no minimum term length is found, it will be calculated as the number of months between the start and end 
	 * dates within each {@link SubscriptionAllBookingData} object.
	 * <p>
	 * Although the database stores the minimum term in a constant format (e.g., "TWELVE_MONTHS"), this property is set
	 * as an integer represented as a string in this context. Therefore, this formatted value should not be saved back
	 * to the database unless it is safely converted back.
	 *
	 * @param bookingDataList
	 * 		A list of {@link SubscriptionAllBookingData} objects for which the minimum term lengths will be updated.
	 */
	public void formatMinimumTerm(List<SubscriptionAllBookingData> bookingDataList)
	{
		log.debug("Formatting minimum term length");
		for (SubscriptionAllBookingData bookingData : bookingDataList)
		{
			String minimumTermLength = bookingData.getMinimumTermLength();
			String formattedMinimumTermLength = null;
			if (StringUtil.hasText(minimumTermLength))
			{
				formattedMinimumTermLength = String.valueOf(SubscriptionMinimumTerm.valueOf(minimumTermLength).getIntValue());
			}
			else
			{
				LocalDate startDate = bookingData.getStartDate().toLocalDate();
				LocalDate endDate = bookingData.getEndDate().toLocalDate();
				formattedMinimumTermLength = String.valueOf(ChronoUnit.MONTHS.between(startDate, endDate));
			}

			bookingData.setMinimumTermLength(formattedMinimumTermLength);
		}
	}

	private StringBuilder addSectionToAddress(StringBuilder sb, String addressSection)
	{
		String seperator = ", ";
		if (!StringUtil.isEmpty(addressSection))
		{
			if  (sb.length() > 0)
			{
				sb.append(seperator);
			}
			sb.append(addressSection);
		}
		return sb;
	}
}
