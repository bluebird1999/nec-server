package com.globe_sh.cloudplatform.server.dao;

import java.util.List;

import com.globe_sh.cloudplatform.server.base.BaseDAO;
import com.globe_sh.cloudplatform.server.entity.EventMessage;
import com.globe_sh.cloudplatform.server.entity.EventStatusMessage;

public interface EventDAO extends BaseDAO<EventMessage, String> {
	   
	void insertEvent(EventStatusMessage eventStatusMessage);
	
	void insertBatchEvent(List<EventStatusMessage> batchList);
	
	void updateLocation(String vehicleId, String lon, String lat);
}
