package com.zcunsoft.clklog.api.services;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.zcunsoft.clklog.api.models.ClientProfile;
import com.zcunsoft.clklog.api.models.profile.UpdateProfileRequest;
import com.zcunsoft.clklog.api.utils.ClientProfileFile;

@Service
public class ProfileService {

	@Resource
	private ClientProfileFile clientProfileFile;

	public ClientProfile loadProfile() {
		ClientProfile clientProfile = new ClientProfile();
		BeanUtils.copyProperties(clientProfileFile.getClientProfile(), clientProfile);
//		clientProfile.setClientId(clientProfileFile.getClientProfile().getClientId());
//		clientProfile.setSubscribed(clientProfileFile.getClientProfile().getSubscribed());
		return clientProfile;
	}

	public void setSubscribed(UpdateProfileRequest model) {
		BeanUtils.copyProperties(model, clientProfileFile.getClientProfile());
		clientProfileFile.getClientProfile().setSubscribed(true);
		clientProfileFile.dumpProfile();	
	}
	
}
