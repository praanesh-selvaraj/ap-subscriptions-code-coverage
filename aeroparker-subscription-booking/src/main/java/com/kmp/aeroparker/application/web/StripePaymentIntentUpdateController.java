package com.kmp.aeroparker.application.web;

import com.kmp.aeroparker.application.engine.Basket;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;
import com.kmp.aeroparker.subscription.payments.stripe.StripePaymentIntentProcessor;
import com.kmp.aeroparker.subscription.string.utils.StringUtil;
import com.stripe.exception.StripeException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping("/subscriptions/{affCode}")
public class StripePaymentIntentUpdateController
{
    private final StripePaymentIntentProcessor intentProcessor;
    private final SubscriptionControllerService subscriptionService;
    private final SubscriptionConfigBean requestBean;

    @PostMapping(value = "/update-payment-intent", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updatePaymentIntent(
            @RequestParam(name = "paymentIntentId", required = false) String paymentIntentId,
            @RequestParam(name = "affiliateId", required = false) Integer affiliateId,
            @RequestParam(name = "customerGuid", required = false) String customerGuid)
    {
        log.info("Updating Stripe payment intent: {}, affiliateId: {}, customerGuid: {}",
                paymentIntentId, affiliateId, customerGuid);
        Map<String, Object> response = new HashMap<>();
        boolean success = false;

        try
        {
            // Validate parameters
            if (StringUtil.isNullOrEmpty(paymentIntentId))
            {
                log.error("Payment intent ID is missing");
            }
            else if (affiliateId == null || affiliateId <= 0)
            {
                log.error("Invalid affiliate ID: {}", affiliateId);
            }
            else if (StringUtil.isNullOrEmpty(customerGuid))
            {
                log.error("Customer GUID is missing");
            }
            else
            {
                // Get basket to calculate total amount (includes promo discount if applied)
                Basket basket = subscriptionService.getBasket(
                        customerGuid,
                        requestBean.getCurrentLanguageId(),
                        requestBean.getDefaultLanguageId(),
                        requestBean.getAffiliate(),
                        requestBean.getAffiliateSubscriptionSettings()
                );

                if (basket == null)
                {
                    log.error("No basket found for customer GUID: {}", customerGuid);
                }
                else
                {
                    // Get grand total from basket (already includes promo discount calculation)
                    BigDecimal grandTotal = basket.getGrandTotal();
                    String amount = grandTotal.toString();
                    log.info("Calculated amount from basket for GUID {}: {}", customerGuid, amount);

                    // Update the payment intent with calculated amount
                    if (grandTotal.compareTo(BigDecimal.ZERO) > 0)
                    {
                        success = intentProcessor.updatePaymentIntent(paymentIntentId, affiliateId, amount);
                    }
                    if (success)
                    {
                        log.info("Successfully updated payment intent: {} with amount: {}", paymentIntentId, amount);
                    }
                    else
                    {
                        log.error("Failed to update payment intent: {}", paymentIntentId);
                    }
                }
            }
        }
        catch (StripeException e)
        {
            log.error("Stripe error updating payment intent: {}", e.getMessage(), e);
        }
        catch (Exception e)
        {
            log.error("Error updating payment intent", e);
        }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("success", success);
        return ResponseEntity.ok(dataMap);
    }
}
