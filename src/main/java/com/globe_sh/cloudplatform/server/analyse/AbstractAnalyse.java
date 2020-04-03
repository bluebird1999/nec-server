package com.globe_sh.cloudplatform.server.analyse;

import org.apache.camel.Exchange;

import com.globe_sh.cloudplatform.common.bean.DataPackage;
import com.globe_sh.cloudplatform.server.manager.OnlineManager;

public abstract class AbstractAnalyse implements Analyse {

	protected DataPackage dataPackage;
	protected Exchange exchange;
	protected boolean auth;
	
	public void authentication() {	
		this.auth = OnlineManager.getInstance().hasLogin(this.dataPackage.getClientId());
		this.auth = true;
	}
	
	public void preProcess(Exchange exchange,DataPackage dataPackage) {
		this.exchange = exchange;
		this.dataPackage = dataPackage;
	}
	
	public abstract void nextProcess();
	
	public abstract void processMessage();
}
