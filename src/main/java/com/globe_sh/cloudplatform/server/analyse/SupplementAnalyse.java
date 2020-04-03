package com.globe_sh.cloudplatform.server.analyse;

import com.globe_sh.cloudplatform.common.util.StaticVariable;

public class SupplementAnalyse extends AbstractAnalyse {


	public void nextProcess() {
		this.exchange.getIn().setHeader("send", StaticVariable.ROUTE_HEAD_SUPPLEMENT);
		this.exchange.getIn().setBody(exchange);
	}
	
	public void processMessage() {
		
	}
}
