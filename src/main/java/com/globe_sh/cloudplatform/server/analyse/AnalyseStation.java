package com.globe_sh.cloudplatform.server.analyse;

import org.apache.log4j.Logger;


public class AnalyseStation {

	private static Logger logger = Logger.getLogger(AnalyseStation.class);
	
	private static AnalyseStation instance;
	
	private AnalyseStation() {
		
	}
	
	public static synchronized AnalyseStation getInstance() {
		if(instance == null)
			instance = new AnalyseStation();
		
		return instance;
	}
	
	public Analyse getAnalyse(byte[] exchange) {
		Analyse analyse = null;
		byte cmd = exchange[2];
		try {
			switch(cmd) {
				case 0x01:
				    analyse = new LoginAnalyse();
				    break;
				case 0x02:
					analyse = new StatusAnalyse();
					break;
				case 0x03:
					analyse = new SupplementAnalyse();
					break;
				case 0x05:
					analyse = new LogoutAnalyse();
					break;	
				case 0x07:
					analyse = new HeartAnalyse();
					break;	
				case 0x08:
					analyse = new TimingAnalyse();
					break;	
				case (byte)0x40:
					analyse = new KickoutAnalyse();
					break;	
				case (byte)0x41:
					analyse = new KickallAnalyse();
					break;			
				default:
					analyse = null;
			}
		} catch(Exception e) {
			logger.error("Data Error Found，When Create Analyse Object.");
		}
		
		return analyse;
	}
}
