package com.kmp.aeroparker.application.model.external.api.datatypes;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Setter;

@Setter
public class SubscriptionQuotes
{
	private ArrayList<SubscriptionQuote> quotes = new ArrayList<>();

	@JsonProperty("SubscriptionQuote")
	public List<SubscriptionQuote> getQuotes()
	{
		return quotes;
	}
}
