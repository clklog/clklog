package com.zcunsoft.clklog.api.cfg;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zcunsoft.clklog.api.utils.ClientProfileFile;

@Configuration
public class ProfileConfiguration {

	@Bean(initMethod = "init")
	public ClientProfileFile clientProfileFile() {
		return new ClientProfileFile(
				System.getProperty("user.dir") + File.separator + "setting" + File.separator + "profile.json");
	}

}
