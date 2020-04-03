package com.globe_sh.cloudplatform.server.analyse;

import org.apache.camel.Exchange;

import com.globe_sh.cloudplatform.common.bean.DataPackage;

public interface Analyse {

	void preProcess(Exchange message, DataPackage dataPackage);
	
	void authentication();

	public void nextProcess();
	
	public void processMessage();
}
