package com.kmp.aeroparker.application.model.external.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalApiCredentials
{
	private String username;
	private String password;
	private String endpoint;
}
