package com.zcunsoft.cfg;

import nl.basjes.parse.useragent.AbstractUserAgentAnalyzer;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserAgentAnalyzerConfig {
	@Bean
	public AbstractUserAgentAnalyzer userAgentAnalyzer() {
		UserAgentAnalyzer userAgentAnalyzer = UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().withCache(10000)
				.build();

		return userAgentAnalyzer;
	}
}
