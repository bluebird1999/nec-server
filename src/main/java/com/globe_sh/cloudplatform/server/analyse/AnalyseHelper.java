package com.globe_sh.cloudplatform.server.analyse;

import org.apache.log4j.Logger;

import com.globe_sh.cloudplatform.common.util.Bcc;
import com.globe_sh.cloudplatform.common.util.ByteArrayUtil;
import com.globe_sh.cloudplatform.common.util.StaticVariable;


public class AnalyseHelper {

	private static Logger logger = Logger.getLogger(AnalyseHelper.class);
	
	public static boolean validate(byte[] exchange) {
		try {
			int packageLength = exchange.length - 1;
			byte valid = exchange[packageLength];
			byte bcc = Bcc.getBccWithoutEnd(exchange);
			if(valid == bcc && exchange[0] == 0x23 && exchange[1] == 0x23) {
				short s = ByteArrayUtil.getShortLowEnd(exchange, StaticVariable.DATA_LENGTH_START);
				int dataLength = exchange.length - StaticVariable.CONTROL_DATA_LENGTH;
				if(s == dataLength) {
					return true;
				}
			}
		} catch(Exception e) {
			logger.error("Exception Found when validate: ",e);
		}

		return false;
	}
	
}
