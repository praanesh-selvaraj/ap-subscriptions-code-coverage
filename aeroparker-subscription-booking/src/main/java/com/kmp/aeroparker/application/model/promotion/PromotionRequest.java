package com.kmp.aeroparker.application.model.promotion;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionRequest
{
    private Integer subscriptionProductId;
    private String promoCode;
    private String email;
}
