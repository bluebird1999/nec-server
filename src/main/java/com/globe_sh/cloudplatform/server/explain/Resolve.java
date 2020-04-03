package com.globe_sh.cloudplatform.server.explain;

import java.sql.Timestamp;
import java.util.List;

import com.globe_sh.cloudplatform.server.entity.EventStatusMessage;


public interface Resolve {

	void preExecute(byte[] data,int start, String uuid, List<EventStatusMessage> statusList);
	
	public void execute();
}
