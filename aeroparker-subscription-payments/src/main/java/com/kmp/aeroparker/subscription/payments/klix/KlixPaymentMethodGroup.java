package com.kmp.aeroparker.subscription.payments.klix;

import java.util.List;

import com.google.gson.JsonArray;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class KlixPaymentMethodGroup
{
	private String name;
	private List<String> logoUrls;
	private String label;
	private JsonArray whitelistJson;
}
