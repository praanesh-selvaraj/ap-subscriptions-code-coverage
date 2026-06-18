package com.kmp.aeroparker.application.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AncillaryBarcodeSQSMessage
{
	private int siteId;
	private String schema;
	private String status;
	private String uuid;
	private String subscriptionBookingRef;
	private int subscriptionProductId;
}
