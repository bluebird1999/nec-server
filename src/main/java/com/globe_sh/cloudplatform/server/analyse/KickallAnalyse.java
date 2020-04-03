package com.globe_sh.cloudplatform.server.analyse;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class KickallAnalyse extends AbstractAnalyse {

	private static Logger logger = Logger.getLogger(KickallAnalyse.class);
	
	public void authentication() {
		
		this.auth = true;
	}
	
	public void nextProcess() {
		
	}

	public void processMessage() {
		try {
			String[] cmd = {"/bin/sh", "-c", "/usr/etc/nec-agent/bin/run.sh stop" };
			logger.info("Exec: " + cmd.toString());
			Process process = Runtime.getRuntime().exec(cmd);
			process.waitFor(10, TimeUnit.SECONDS);
		} catch(Exception e) {
			e.printStackTrace();
		}
		//OnlineManager.getInstance().kickall();
	}
}
