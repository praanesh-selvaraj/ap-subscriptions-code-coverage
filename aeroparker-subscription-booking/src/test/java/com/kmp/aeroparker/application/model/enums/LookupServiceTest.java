package com.kmp.aeroparker.application.model.enums;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

public class LookupServiceTest
{
	@Test
	public void testGetId_MOTORCHECK_IE()
	{
		assertEquals(2, LookupService.MOTORCHECK_IE.getId());
	}
}
