package com.zcunsoft.cfg;

import com.zcunsoft.handlers.ConstsDataHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Class SpringConfiguration.
 *
 * @author yuhao
 */
@Configuration
public class SpringConfiguration {


	@Bean
	public ConstsDataHolder constsDataHolder() {
		return new ConstsDataHolder();
	}

}
