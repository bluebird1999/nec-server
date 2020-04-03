package com.globe_sh.cloudplatform.server.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import com.globe_sh.cloudplatform.server.entity.EventMessage;
import com.globe_sh.cloudplatform.server.service.EventService;

public class PersistentMessageProcess implements Processor {

	private static Logger logger = Logger.getLogger(PersistentMessageProcess.class);
	
	private EventService eventService;
	
	public void process(Exchange message) {
		logger.info("Persistent Message . ###start###");
		byte[] exchange = (byte[])message.getIn().getBody();
		String statusString = "";
		try { 
			statusString = new String(exchange);
			EventMessage eventMessage = new EventMessage();
			eventMessage.fromJsonString(statusString);
			eventService.insertEvent(eventMessage);
		} catch(Exception jmse) {
			logger.error("Persistent Message:::::::::::::::::::::: " + statusString);
			logger.error("Persistent Message Found Exception: ",jmse);
		}
    	
		logger.info("Persistent Message. ###end###");
	}
	
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}
}
