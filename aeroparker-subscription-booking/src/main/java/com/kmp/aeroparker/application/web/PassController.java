package com.kmp.aeroparker.application.web;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kmp.aeroparker.application.config.CustomConnectionProvider;
import com.kmp.aeroparker.application.db.service.GlobalPropertiesService;
import com.kmp.aeroparker.application.loginapi.LoginClient;
import com.kmp.aeroparker.application.passes.PassClient;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Controller
@RequestMapping(value = "/subscriptions/passes")
public class PassController
{
	private final GlobalPropertiesService globalProperties;
	private final LoginClient loginClient;
	private final PassClient passClient;
	private final CustomConnectionProvider connectionProvider;

	private final static String LOGIN_ENDPOINT = "aeroparker.api.login.endpoint";
	private final static String PASSKIT_ENDPOINT = "aeroparker.api.passkit.endpoint";
	private final static String GOOGLE_PASS_ENDPOINT = "aeroparker.api.googlepasses.endpoint";

	@GetMapping(value = "/passkit")
	public ResponseEntity<ByteArrayResource> generatePasskit(
			@RequestParam(name = "encryptedReference", required = false) String encryptedReference,
			@RequestParam(name = "siteId", required = false) int siteId)
	{
		log.info("Generating Apple Passkit pass for encrypted Reference {}", encryptedReference);

		byte[] generatedPass = generatePasskitResponse(encryptedReference, siteId);

		ResponseEntity<ByteArrayResource> passkitResponse = ResponseEntity.badRequest()
				.build();

		if (generatedPass != null)
		{
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"pass.pkpass\"");
			headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.apple.pkpass");
			headers.setContentLength(generatedPass.length);
			ByteArrayResource passkitArray = new ByteArrayResource(generatedPass);

			passkitResponse = ResponseEntity.ok()
					.headers(headers)
					.body(passkitArray);
		}

		return passkitResponse;
	}

	@GetMapping(value = "/googlepass")
	public String generateGooglePass(
			@RequestParam(name = "encryptedReference", required = false) String encryptedReference,
			@RequestParam(name = "siteId", required = false) int siteId)
	{
		log.info("Generating Google pass for encrypted Reference {}", encryptedReference);

		String generatedPass = generateGooglePassResponse(encryptedReference, siteId);

		if (StringUtils.isEmpty(generatedPass))
		{
			log.info("Google Pass was not generated and returned empty response ");
		}

		return "redirect:" + generatedPass;
	}

	private byte[] generatePasskitResponse(String encryptedReference, int siteId)
	{
		byte[] generatedPass = null;

		String schema = connectionProvider.getSchema();
		String loginEndpoint = globalProperties.fetchProperty(LOGIN_ENDPOINT);
		String passkitEndpoint = globalProperties.fetchProperty(PASSKIT_ENDPOINT);

		String authToken = loginClient.getLoginToken(loginEndpoint, siteId, schema);

		if (!StringUtils.isEmpty(authToken) && !StringUtils.isEmpty(encryptedReference))
		{
			generatedPass = passClient.getPasskitPass(authToken, encryptedReference, passkitEndpoint);
		}
		return generatedPass;
	}

	private String generateGooglePassResponse(String encryptedReference, int siteId)
	{
		String generatedGooglePass = "";

		String schema = connectionProvider.getSchema();
		String loginEndpoint = globalProperties.fetchProperty(LOGIN_ENDPOINT);
		String passEndpoint = globalProperties.fetchProperty(GOOGLE_PASS_ENDPOINT);

		String authToken = loginClient.getLoginToken(loginEndpoint, siteId, schema);

		if (!StringUtils.isEmpty(authToken) && !StringUtils.isEmpty(encryptedReference))
		{
			generatedGooglePass = passClient.getGooglePass(encryptedReference, authToken, passEndpoint);
		}
		return generatedGooglePass;
	}
}
