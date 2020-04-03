package com.globe_sh.cloudplatform.server.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import com.globe_sh.cloudplatform.common.cache.JedisOperater;
import com.globe_sh.cloudplatform.server.entity.EventMessage;

public class CacheMessageProcess implements Processor {

	private static Logger logger = Logger.getLogger(CacheMessageProcess.class);
	
	public void process(Exchange message) {
		logger.info("Cache Message . ###start###");
		byte[] exchange = (byte[])message.getIn().getBody();
		try { 
			String statusString = new String(exchange);
			EventMessage eventMessage = new EventMessage();
			eventMessage.fromJsonString(statusString);
			String key = eventMessage.getStation();
			String value = statusString;
			if(eventMessage.getStatusList() != null && eventMessage.getStatusList().size() > 0) {
				JedisOperater.updateStationStatus(key, value);
				logger.info("Process Cache Message, Save Status Data To Redis......");
			} else {
				logger.info("Process Cache Message, Data Are Incomplete......");
			}
		} catch(Exception jmse) {
			logger.error("Cache Message Found Exception: ",jmse);
		}
    	
		logger.info("Cache Message. ###end###");
	}
}
