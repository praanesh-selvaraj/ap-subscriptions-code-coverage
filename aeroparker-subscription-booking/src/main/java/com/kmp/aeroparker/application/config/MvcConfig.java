package com.kmp.aeroparker.application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer
{
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry)
	{
		registry.addResourceHandler("/subscriptions/skins/**", "/subscriptions/images/**", "/subscriptions/images/icons/**",
				"/subscriptions/images/flag_icons/**", "/subscriptions/js/**", "/subscriptions/fonts/**")
				.addResourceLocations("classpath:/static/skins/", "classpath:/static/skins/images/icons/", "classpath:/static/skins/images/",
						"classpath:/static/skins/images/icons/flag_icons/", "classpath:/static/js/", "classpath:/static/fonts/");
	}
}