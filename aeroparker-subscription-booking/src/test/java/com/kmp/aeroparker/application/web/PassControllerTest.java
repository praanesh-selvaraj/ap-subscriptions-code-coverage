package com.kmp.aeroparker.application.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.application.config.CustomConnectionProvider;
import com.kmp.aeroparker.application.db.service.GlobalPropertiesService;
import com.kmp.aeroparker.application.loginapi.LoginClient;
import com.kmp.aeroparker.application.passes.PassClient;

@ExtendWith(MockitoExtension.class)
class PassControllerTest
{
	@InjectMocks
	private PassController controller;
	@Mock
	private GlobalPropertiesService globalProperties;
	@Mock
	private LoginClient loginClient;
	@Mock
	private PassClient passClient;
	@Mock
	private CustomConnectionProvider connectionProvider;

	@Test
	public void testGeneratePasskit() throws IOException
	{
		byte[] bytes = "byte".getBytes();

		when(globalProperties.fetchProperty(anyString())).thenReturn("endpoint");
		when(connectionProvider.getSchema()).thenReturn("schema");
		when(loginClient.getLoginToken(anyString(), anyInt(), anyString())).thenReturn("token");
		when(passClient.getPasskitPass(anyString(), anyString(), anyString())).thenReturn(bytes);

		controller.generatePasskit("ref", 1);

		verify(loginClient).getLoginToken(anyString(), anyInt(), anyString());
		verify(passClient).getPasskitPass(anyString(), anyString(), anyString());
	}

	@Test
	public void testGeneratePasskit_Null_token() throws IOException
	{
		when(globalProperties.fetchProperty(anyString())).thenReturn("endpoint");
		when(connectionProvider.getSchema()).thenReturn("schema");
		when(loginClient.getLoginToken(anyString(), anyInt(), anyString())).thenReturn(null);

		controller.generatePasskit("ref", 1);

		verify(loginClient).getLoginToken(anyString(), anyInt(), anyString());
		verifyNoInteractions(passClient);
	}

	@Test
	public void testGeneratePasskit_Null_Reference() throws IOException
	{
		when(globalProperties.fetchProperty(anyString())).thenReturn("endpoint");
		when(connectionProvider.getSchema()).thenReturn("schema");
		when(loginClient.getLoginToken(anyString(), anyInt(), anyString())).thenReturn("token");

		controller.generatePasskit(null, 1);

		verify(loginClient).getLoginToken(anyString(), anyInt(), anyString());
		verifyNoInteractions(passClient);
	}

	@Test
	public void testGenerateGooglePass()
	{
		when(globalProperties.fetchProperty(anyString())).thenReturn("endpoint");
		when(connectionProvider.getSchema()).thenReturn("schema");
		when(loginClient.getLoginToken(anyString(), anyInt(), anyString())).thenReturn("token");
		when(passClient.getGooglePass(anyString(), anyString(), anyString())).thenReturn("pass");

		assertEquals("redirect:pass", controller.generateGooglePass("ref", 1));
	}

	@Test
	public void testGenerateGooglePass_Null_Token()
	{
		when(globalProperties.fetchProperty(anyString())).thenReturn("endpoint");
		when(connectionProvider.getSchema()).thenReturn("schema");
		when(loginClient.getLoginToken(anyString(), anyInt(), anyString())).thenReturn(null);

		assertEquals("redirect:", controller.generateGooglePass("ref", 1));
	}

	@Test
	public void testGenerateGooglePass_Null_Reference()
	{
		when(globalProperties.fetchProperty(anyString())).thenReturn("endpoint");
		when(connectionProvider.getSchema()).thenReturn("schema");
		when(loginClient.getLoginToken(anyString(), anyInt(), anyString())).thenReturn("token");

		assertEquals("redirect:", controller.generateGooglePass(null, 1));
	}
}
