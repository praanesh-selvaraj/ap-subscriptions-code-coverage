package com.kmp.aeroparker.application.vehiclelookup;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import com.google.gson.JsonObject;
import com.kmp.aeroparker.application.builder.VehicleLookupClientBuilder;
import com.kmp.aeroparker.application.model.enums.LookupService;
import com.kmp.aeroparker.application.model.interfaces.IVehicleLookup;
import com.kmp.aeroparker.i18n.LanguageFieldsList;
import com.kmp.aeroparker.subscription.booking.kmp.tables.pojos.VehiclelookupAffiliateLogins;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
@NoArgsConstructor
public class MotorcheckLookup implements IVehicleLookup
{
	private VehicleLookupClientBuilder clientBuilder = new VehicleLookupClientBuilder();

	@Override
	public String doLookup(VehiclelookupAffiliateLogins loginDetails, String reg, LanguageFieldsList list)
	{
		// Strip out rubbish from reg field
		reg = reg.replaceAll("\\W|_", "");
		JsonObject json = new JsonObject();

		String lookupServiceUrl = loginDetails.getUrl();
		URI uri = null;
		try
		{
			uri = new URI(lookupServiceUrl);
		}
		catch (URISyntaxException e)
		{
			log.error("Couldn't parse URL for the lookup service - url: {}, {}", lookupServiceUrl, e.getMessage(), e);
			return json.toString();
		}
		String host = uri.getHost();
		HttpHost targetHost = new HttpHost(host);
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()),
				new UsernamePasswordCredentials(loginDetails.getUsername(), loginDetails.getPassword()));

		AuthCache authCache = new BasicAuthCache();
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(targetHost, basicAuth);

		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credsProvider);
		context.setAuthCache(authCache);

		HttpGet httpGet = new HttpGet("/vehicle/basic?reg=" + reg);
		try (CloseableHttpResponse httpResponse = clientBuilder.buildClient()
				.execute(targetHost, httpGet, context))
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(httpResponse.getEntity()
					.getContent());
			if (doc.getElementsByTagName("errors")
					.getLength() == 0)
			{
				String carReg = doc.getElementsByTagName("reg")
						.item(0)
						.getTextContent();
				String carMake = doc.getElementsByTagName("make")
						.item(0)
						.getTextContent();
				String carModel = doc.getElementsByTagName("model")
						.item(0)
						.getTextContent();
				String carColour = doc.getElementsByTagName("colour")
						.item(0)
						.getTextContent();

				log.info("Car reg: {}, Make: {}, Model: {}, Colour: {}", carReg, carMake, carModel, carColour);

				json.addProperty("reg", carReg);
				json.addProperty("make", carMake);
				json.addProperty("model", carModel);
				json.addProperty("colour", carColour);
			}
		}
		catch (Exception e)
		{
			log.error("Error sending request to motorcheck: {}", e.getMessage(), e);
		}

		return json.toString();
	}

	@Override
	public int getLookupServiceId()
	{
		return LookupService.MOTORCHECK_IE.getId();
	}
}
