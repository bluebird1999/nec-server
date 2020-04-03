package com.globe_sh.cloudplatform.server.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;


public class StatusMessageProcess implements Processor {

	private static Logger logger = Logger.getLogger(StatusMessageProcess.class);	
	
	public void process(Exchange message) {
		logger.info("Process Message For Status Operate. ###start###");
		logger.info("Process Message For Status Operate. ###end###");
	}
	
	private void nextProcess(Exchange message) {		
	}	
}