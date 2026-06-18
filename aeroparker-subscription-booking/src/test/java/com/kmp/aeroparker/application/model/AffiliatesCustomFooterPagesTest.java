package com.kmp.aeroparker.application.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AffiliatesCustomFooterPagesTest
{
	@Test
	void testGetUrlTitle()
	{
		AffiliatesCustomFooterPages affiliatesCustomFooterPages = new AffiliatesCustomFooterPages();
		affiliatesCustomFooterPages.setLinkTitle("test title");
		assertThat(affiliatesCustomFooterPages.getUrlTitle()).isEqualTo("test+title");
	}
}