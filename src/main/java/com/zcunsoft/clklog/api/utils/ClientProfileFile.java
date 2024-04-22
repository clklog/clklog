package com.zcunsoft.clklog.api.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zcunsoft.clklog.api.models.ClientProfile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientProfileFile extends File {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String CLIENT_ID_KEY = "client-id";
	private static final String SUBSCRIBED = "subscribed";

	private ObjectMapper mapper = new ObjectMapper();

	public ClientProfileFile(String pathname) {
		super(pathname);
	}

	private ClientProfile clientProfile;

//	public void init() {
//		if (this.exists()) {
//			Yaml yaml = new Yaml();
//			try {
//				Map<String, Object> obj = yaml.load(new FileReader(this));
//				if (obj.containsKey(CLIENT_ID_KEY) && StringUtils.hasLength(obj.get(CLIENT_ID_KEY).toString())) {
//					clientProfile = new ClientProfile();
//					clientProfile.setClientId(obj.get(CLIENT_ID_KEY).toString());
//					clientProfile.setSubscribed(
//							obj.containsKey(SUBSCRIBED) && Boolean.parseBoolean(obj.get(SUBSCRIBED).toString()));
//				}
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//				log.error("failed to load profiles.", e);
//			} finally {
//			}
//		}
//		if (clientProfile == null) {
//			log.debug("initing profiles...");
//			try {
//				Files.createDirectories(Paths.get(this.getParent()));
//				clientProfile = new ClientProfile();
//				clientProfile.setClientId(UUID.randomUUID().toString());
//				clientProfile.setSubscribed(false);
//				Map<String, Object> data = new LinkedHashMap<String, Object>();
//				data.put(CLIENT_ID_KEY, clientProfile.getClientId());
//				Yaml yaml = new Yaml();
//				FileWriter writer;
//				writer = new FileWriter(this);
//				yaml.dump(data, writer);
//			} catch (IOException e) {
//				e.printStackTrace();
//				log.error("failed to init profiles.", e);
//			}
//		}
//	}

	public void init() {
		if (this.exists()) {
//			Yaml yaml = new Yaml();
			try {
				clientProfile = mapper.readValue(new FileReader(this), ClientProfile.class);
//				clientProfile = yaml.loadAs(new FileReader(this), ClientProfile.class);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				log.error("failed to load profiles.", e);
			} catch (IOException e) {
				e.printStackTrace();
				log.error("failed to parse profiles.", e);
			} finally {
			}
		}
		if (clientProfile == null || !StringUtils.hasLength(clientProfile.getClientId())) {
			log.debug("initing profiles...");
			try {
				Files.createDirectories(Paths.get(this.getParent()));
				clientProfile = new ClientProfile();
				clientProfile.setClientId(UUID.randomUUID().toString());
				clientProfile.setSubscribed(false);
//				Yaml yaml = new Yaml();
				FileWriter writer;
				writer = new FileWriter(this);
//				yaml.dump(clientProfile, writer);
				mapper.writeValue(writer, clientProfile);
			} catch (IOException e) {
				e.printStackTrace();
				log.error("failed to init profiles.", e);
			}
		}
	}

	private static final Object lock = new Object();

//	public void dumpYaml() {
//		if (this.exists() && clientProfile != null) {
//			synchronized (lock) {
//				try {
//					Files.createDirectories(Paths.get(this.getParent()));
//					Map<String, Object> data = new LinkedHashMap<String, Object>();
//					data.put(CLIENT_ID_KEY, clientProfile.getClientId());
//					data.put(SUBSCRIBED, clientProfile.getSubscribed());
//					Yaml yaml = new Yaml();
//					FileWriter writer;
//					writer = new FileWriter(this);
//					yaml.dump(data, writer);
//				} catch (IOException e) {
//					e.printStackTrace();
//					log.error("failed to dump profiles.", e);
//				}
//			}
//		}
//	}

	public void dumpProfile() {
		if (this.exists() && clientProfile != null) {
			synchronized (lock) {
				try {
					Files.createDirectories(Paths.get(this.getParent()));
//					Yaml yaml = new Yaml();
					FileWriter writer;
					writer = new FileWriter(this);
//					yaml.dump(clientProfile, writer);
					mapper.writeValue(writer, clientProfile);
				} catch (IOException e) {
					e.printStackTrace();
					log.error("failed to dump profiles.", e);
				}
			}
		}
	}

	public ClientProfile getClientProfile() {
		return clientProfile;
	}

}
