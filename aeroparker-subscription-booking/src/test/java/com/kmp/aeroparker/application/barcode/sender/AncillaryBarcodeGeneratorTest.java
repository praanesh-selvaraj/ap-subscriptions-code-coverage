package com.kmp.aeroparker.application.barcode.sender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.InvalidAddressException;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.kmp.aeroparker.application.config.CustomConnectionProvider;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;

@ExtendWith(MockitoExtension.class)
class AncillaryBarcodeGeneratorTest
{
	@Mock
	private AmazonSQS sqs;
	@Mock
	private SubscriptionConfigBean requestBean;
	@Mock
	private CustomConnectionProvider connectionProvider;
	@InjectMocks
	private AncillaryBarcodeGenerator generator;

	@Test
	void testGenerate()
	{
		when(requestBean.getSiteId()).thenReturn(1);
		when(requestBean.getAncillaryBarcodeQueueUrl()).thenReturn("queue");
		when(connectionProvider.getSchema()).thenReturn("schema");
		when(sqs.sendMessage(any(SendMessageRequest.class))).thenReturn(mock(SendMessageResult.class));

		generator.generate("ref", 1);

		verify(sqs).sendMessage(any(SendMessageRequest.class));
	}

	@Test
	void testGenerate_AmazonServiceException()
	{
		when(requestBean.getSiteId()).thenReturn(1);
		when(requestBean.getAncillaryBarcodeQueueUrl()).thenReturn("queue");
		when(connectionProvider.getSchema()).thenReturn("schema");
		when(sqs.sendMessage(any(SendMessageRequest.class))).thenThrow(AmazonServiceException.class);

		generator.generate("ref", 1);

		verify(sqs).sendMessage(any(SendMessageRequest.class));
	}

	@Test
	void testGenerate_AmazonClientException()
	{
		when(requestBean.getSiteId()).thenReturn(1);
		when(requestBean.getAncillaryBarcodeQueueUrl()).thenReturn("queue");
		when(connectionProvider.getSchema()).thenReturn("schema");
		when(sqs.sendMessage(any(SendMessageRequest.class))).thenThrow(AmazonClientException.class);

		generator.generate("ref", 1);

		verify(sqs).sendMessage(any(SendMessageRequest.class));
	}

	@Test
	void testGenerate_Exception()
	{
		when(requestBean.getSiteId()).thenReturn(1);
		when(requestBean.getAncillaryBarcodeQueueUrl()).thenReturn("queue");
		when(connectionProvider.getSchema()).thenReturn("schema");
		when(sqs.sendMessage(any(SendMessageRequest.class))).thenThrow(InvalidAddressException.class);

		generator.generate("ref", 1);

		verify(sqs).sendMessage(any(SendMessageRequest.class));
	}

	@Test
	void testGenerate_NullSchema()
	{
		when(requestBean.getSiteId()).thenReturn(1);
		when(requestBean.getAncillaryBarcodeQueueUrl()).thenReturn("queue");

		generator.generate("ref", 1);

		verify(sqs, never()).sendMessage(any(SendMessageRequest.class));
	}

	@Test
	void testGenerate_NullQueueUrl()
	{
		when(requestBean.getSiteId()).thenReturn(1);

		generator.generate("ref", 1);

		verify(sqs, never()).sendMessage(any(SendMessageRequest.class));
	}

}
