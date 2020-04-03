package com.globe_sh.cloudplatform.server.process;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import com.globe_sh.cloudplatform.common.bean.DataPackage;
import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.common.util.StaticVariable;
import com.globe_sh.cloudplatform.server.analyse.Analyse;
import com.globe_sh.cloudplatform.server.analyse.AnalyseStation;

public class NorthMessageProcess implements Processor {

	private static Logger logger = Logger.getLogger(NorthMessageProcess.class);
	
	public void process(Exchange message) {
		logger.info("Process Message From Agent. ###start###");
		byte[] exchange = (byte[])message.getIn().getBody();
		logger.info("Data: " + StaticMethod.bytesToHexString(exchange));
		try {
			if(!StaticMethod.isNull(exchange)) {
				DataPackage dataPackage = new DataPackage(exchange);
				dataPackage.unpack();
				if(dataPackage.isValid()) {
					Analyse analyse = AnalyseStation.getInstance().getAnalyse(dataPackage.getSourceData());
					if(analyse != null) {
						analyse.preProcess(message, dataPackage);
						
						analyse.authentication();
						
						analyse.processMessage();
						
						analyse.nextProcess();
						logger.info("Data By BCC Validate Success.");
					} else {
						logger.warn("No Analyse: " + StaticMethod.bytesToHexString(exchange));
						processDirtyData(message, exchange);
						logger.error("No Analyse......");
					}
				}
			} else {
				logger.warn("Received Data Is Empty. ");
			}
		} catch(Exception jmse) {
			jmse.printStackTrace();
			logger.error("Process Message Found Exception: ",jmse);
		}
		
		logger.info("Process Message From Agent. ###end###");
	}
	
	private void processDirtyData(Exchange message, byte[] exchange) {
		message.getIn().setHeader("send", StaticVariable.ROUTE_HEAD_DIRTY);
    	String value = "Time: " + StaticMethod.getTimeString(0) + " Data: " + StaticMethod.bytesToHexString(exchange);
    	message.getIn().setBody(value);
    	logger.info("Process Message, Found Dirty Data... ");
	}
}
