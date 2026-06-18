package com.kmp.aeroparker.subscription.payments.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.braintreegateway.CustomerRequest;
import com.braintreegateway.SubscriptionRequest;
import com.braintreegateway.TransactionRequest;
import com.kmp.aeroparker.subscription.payments.config.GlobalProperties;
import com.kmp.aeroparker.subscription.payments.credentials.BraintreeCredentials;
import com.kmp.aeroparker.subscription.payments.factory.BraintreeObjectFactory;

@ExtendWith(MockitoExtension.class)
class BraintreeObjectFactoryTest
{
	@Mock
	private GlobalProperties globalProperties;
	@InjectMocks
	private BraintreeObjectFactory braintreeObjectFactory;

	@Test
	void testCreateGatewayDev()
	{
		BraintreeCredentials credentials = mock(BraintreeCredentials.class);
		when(globalProperties.isDev()).thenReturn(true);
		when(credentials.getMerchantId()).thenReturn("111");
		when(credentials.getPublicKey()).thenReturn("111");
		when(credentials.getPrivateKey()).thenReturn("111");
		assertThat(braintreeObjectFactory.createGateway(credentials)).isNotNull();
	}

	@Test
	void testCreateGatewayStaging()
	{
		BraintreeCredentials credentials = mock(BraintreeCredentials.class);
		when(globalProperties.isDev()).thenReturn(false);
		when(globalProperties.isStaging()).thenReturn(true);
		when(credentials.getMerchantId()).thenReturn("111");
		when(credentials.getPublicKey()).thenReturn("111");
		when(credentials.getPrivateKey()).thenReturn("111");
		assertThat(braintreeObjectFactory.createGateway(credentials)).isNotNull();
	}

	@Test
	void testCreateGatewayProd()
	{
		BraintreeCredentials credentials = mock(BraintreeCredentials.class);
		when(globalProperties.isDev()).thenReturn(false);
		when(globalProperties.isStaging()).thenReturn(false);
		when(credentials.getMerchantId()).thenReturn("111");
		when(credentials.getPublicKey()).thenReturn("111");
		when(credentials.getPrivateKey()).thenReturn("111");
		assertThat(braintreeObjectFactory.createGateway(credentials)).isNotNull();
	}

	@Test
	public void createCustomerRequestTest()
	{
		assertThat(braintreeObjectFactory.createCustomerRequest()).isNotNull()
				.isInstanceOf(CustomerRequest.class);
	}

	@Test
	public void createSubscriptionRequestTest()
	{
		assertThat(braintreeObjectFactory.createSubscriptionRequest()).isNotNull()
				.isInstanceOf(SubscriptionRequest.class);
	}

	@Test
	public void createTransactionRequestTest()
	{
		assertThat(braintreeObjectFactory.createTransactionRequest()).isNotNull()
				.isInstanceOf(TransactionRequest.class);
	}
}