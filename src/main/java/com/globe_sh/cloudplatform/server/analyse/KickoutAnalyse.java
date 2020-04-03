package com.globe_sh.cloudplatform.server.analyse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.globe_sh.cloudplatform.server.manager.OnlineManager;

public class KickoutAnalyse extends AbstractAnalyse {

	private static Logger logger = LogManager.getLogger(KickoutAnalyse.class);
	
	public void authentication() {
		
		this.auth = true;
	}
	
	public void nextProcess() {
		
	}

	public void processMessage() {
		logger.info("Kick out request from agent!");
		String clientId = this.dataPackage.getClientId();	
		OnlineManager.getInstance().kickout(clientId);
	}
}
