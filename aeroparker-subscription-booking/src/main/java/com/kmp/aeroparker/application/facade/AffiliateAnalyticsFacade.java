package com.kmp.aeroparker.application.facade;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kmp.aeroparker.application.db.service.AnalyticsService;
import com.kmp.aeroparker.application.model.AnalyticsLocations;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.AffiliateAnalyticsNew;
import com.kmp.utils.StringUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Service
public class AffiliateAnalyticsFacade
{
	private AnalyticsService analyticsService;

	/**
	 * Fetch enabled affiliate analytics for each step, and sort them based on the step they are enabled for
	 * 
	 * @param affiliateId
	 * @param step
	 * @return a sorted list of enabled analytics based on the step
	 */
	public List<AffiliateAnalyticsNew> getEnabledAnalyticsAtStepByAffiliateId(int affiliateId, int step)
	{
		if (affiliateId > 0 && step > 0)
		{
			List<AffiliateAnalyticsNew> enabledAnalytics = getEnabledAnalyticsByAffiliateId(affiliateId);
			if (log.isDebugEnabled())
			{
				log.debug("Affiliate " + affiliateId + " has " + enabledAnalytics.size() + " analytics enabled.");
			}
			if (!enabledAnalytics.isEmpty())
			{
				enabledAnalytics = enabledAnalytics.stream()
						.filter(temp -> Arrays.asList(temp.getSteps()
								.split(","))
								.contains(StringUtil.intToStr(step)))
						.collect(Collectors.toList());
				return enabledAnalytics;
			}
		}
		if (log.isDebugEnabled())
		{
			log.debug("AffiliateId or step was negative. Nothing will be retrieved");
		}
		return Collections.emptyList();
	}

	private List<AffiliateAnalyticsNew> getEnabledAnalyticsByAffiliateId(int affiliateId)
	{
		if (log.isDebugEnabled())
		{
			log.debug("Retrieving enabled analytics for " + affiliateId);
		}
		List<AffiliateAnalyticsNew> enabledAnalytics = analyticsService.getAnalyticsByAffiliateId(affiliateId);
		sortEnabledAnalytics(enabledAnalytics);
		enabledAnalytics = enabledAnalytics.stream()
				.filter(temp -> temp.getEnabled() == 1)
				.collect(Collectors.toList());

		return enabledAnalytics;
	}

	private List<AffiliateAnalyticsNew> sortEnabledAnalytics(List<AffiliateAnalyticsNew> analytics)
	{
		if (!analytics.isEmpty())
		{
			for (AffiliateAnalyticsNew analytic : analytics)
			{
				// for each analytic, check the steps. If they're in one string, no commas, separate them, otherwise
				// skip.
				String steps = analytic.getSteps();
				if (!StringUtil.isNullOrEmpty(steps) && !steps.contains(","))
				{
					parseEnabledAnalytics(steps, analytic);
				}
			}
			analytics.sort((AffiliateAnalyticsNew a1, AffiliateAnalyticsNew a2) -> a1.getLocation()
					.compareTo(a2.getLocation()));
		}
		return analytics;
	}

	private void parseEnabledAnalytics(String steps, AffiliateAnalyticsNew analytic)
	{
		// Add check to make sure the step is not a double digit number before comma separating the digits
		if (AnalyticsLocations.fromId(StringUtil.strToInt((steps)))
				.equals(AnalyticsLocations.UNKNOWN))
		{
			if (log.isDebugEnabled())
			{
				log.debug("Analytic " + analytic.getId() + " doesn't have comma separated steps: " + steps);
			}
			String formattedStep = steps.replaceAll("\\B", ",");
			analytic.setSteps(formattedStep);
			if (log.isDebugEnabled())
			{
				log.debug("Updated analytic " + analytic.getId() + " steps to: " + formattedStep);
			}
		}
	}
}
