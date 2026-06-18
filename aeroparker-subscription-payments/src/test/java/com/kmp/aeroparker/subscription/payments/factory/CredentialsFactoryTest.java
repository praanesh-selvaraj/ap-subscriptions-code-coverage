package com.kmp.aeroparker.subscription.payments.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kmp.aeroparker.subscription.payments.credentials.BraintreeCredentials;
import com.kmp.aeroparker.subscription.payments.credentials.Credentials;
import com.kmp.aeroparker.subscription.payments.enums.PaymentGatewayType;
import com.kmp.aeroparker.subscription.payments.factory.CredentialsFactory;

@ExtendWith(MockitoExtension.class)
class CredentialsFactoryTest
{
	@Mock
	private List<Credentials> credentials;
	@InjectMocks
	private CredentialsFactory factory;

	@BeforeEach
	void init()
	{
		final List<Credentials> credentialsList = Stream.of(new BraintreeCredentials())
				.collect(Collectors.toList());
		when(credentials.spliterator()).thenReturn(credentialsList.spliterator());
		when(credentials.stream()).thenCallRealMethod();
	}

	@Test
	void test()
	{
		assertThat(factory.getInstance(PaymentGatewayType.BRAINTREE)).isNotNull()
				.hasSameClassAs(BraintreeCredentials.class);
	}

	@Test
	void test_Invalid_Type()
	{
		assertThat(factory.getInstance(null)).isNull();
	}
}
