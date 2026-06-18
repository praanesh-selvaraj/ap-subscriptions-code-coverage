package com.kmp.aeroparker.application.barcode.sender;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kmp.aeroparker.application.config.CustomConnectionProvider;
import com.kmp.aeroparker.application.model.SubscriptionConfigBean;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class AncillaryBarcodeGenerator
{
	private static final String MESSAGE_TYPE = "com.kmp.aeroparker.application.model.AncillaryBarcodeSQSMessage";
	private static final String MESSAGE_STATUS_NEW = "NEW";

	private final AmazonSQS sqs;
	private final SubscriptionConfigBean requestBean;
	private final CustomConnectionProvider connectionProvider;

	public void generate(String reference, int productId)
	{
		int siteId = requestBean.getSiteId();
		String queueUrl = requestBean.getAncillaryBarcodeQueueUrl();
		String schema = connectionProvider.getSchema();
		log.info(
				"Generating new third party barcode for subscription product ID {}, site ID {} and subscription booking reference {} on schema {} with queue url {}",
				productId, siteId, reference, schema, queueUrl);

		sendSQMessage(reference, productId, siteId, queueUrl, schema);
	}

	private void sendSQMessage(String reference, int productId, int siteId, String queueUrl, String schema)
	{
		if (!StringUtils.hasText(queueUrl))
		{
			log.info("Queue url was empty, no request has been sent to the ancillary barcode queue");
			return;
		}
		else if (!StringUtils.hasText(schema))
		{
			log.info("schema was empty, no request has been sent to the ancillary barcode queue");
			return;
		}

		final SendMessageRequest request = createRequest(productId, siteId, reference, schema, queueUrl);

		try
		{
			final SendMessageResult result = sqs.sendMessage(request);
			log.info(
					"Message successfully sent to SQS with message ID {}  and sequence number {} for reference {} and product id {}",
					result.getMessageId(), result.getSequenceNumber(), reference, productId);
		}
		catch (final AmazonServiceException ase)
		{
			log.info("Amazon service exception caught which means the request made to to Amazon SQS "
					+ "but was rejected with an error response");
			log.info("Error Message: " + ase.getMessage());
			log.info("HTTP Status Code: " + ase.getStatusCode());
			log.info("AWS Error Code: " + ase.getErrorCode());
			log.info("Error Type: " + ase.getErrorType());
			log.info("Request ID: " + ase.getRequestId());
			log.error("Message rejected by SQS : " + ase.getErrorMessage(), ase);
		}
		catch (final AmazonClientException ace)
		{
			log.error("Amazon client exception caught which means the client encountered a serious internal "
					+ "problem while trying to communicate with Amazon SQS e.g. not being able to access "
					+ "the network: " + ace.getMessage(), ace);
		}
		catch (final Exception e)
		{
			log.error("Unexpected exception sending request to AWS SQS: " + e.getMessage(), e);
		}
	}

	private SendMessageRequest createRequest(int productId, int siteId, String reference, String schema,
			String queueUrl)
	{
		log.info("Building request for reference {} and product id {}", reference, productId);
		final SendMessageRequest request = new SendMessageRequest();
		final ObjectNode node = JsonNodeFactory.instance.objectNode();
		node.put("uuid", UUID.randomUUID()
				.toString());
		node.put("subscriptionProductId", productId);
		node.put("siteId", siteId);
		node.put("subscriptionBookingRef", reference);
		node.put("schema", schema);
		node.put("status", MESSAGE_STATUS_NEW);
		final String messageBody = node.toString();

		log.info("Sending message to ancillary barcode sqs {}", messageBody);

		request.setQueueUrl(queueUrl);
		request.setMessageBody(messageBody);

		Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
		MessageAttributeValue attribute = new MessageAttributeValue().withDataType("String")
				.withStringValue(MESSAGE_TYPE);
		messageAttributes.put("type", attribute);
		request.setMessageAttributes(messageAttributes);

		return request;
	}
}
