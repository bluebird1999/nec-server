package com.globe_sh.cloudplatform.server.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.server.base.BaseDAO;
import com.globe_sh.cloudplatform.server.base.BaseServiceImpl;
import com.globe_sh.cloudplatform.server.dao.EventDAO;
import com.globe_sh.cloudplatform.server.entity.EventMessage;
import com.globe_sh.cloudplatform.server.entity.EventStatusMessage;

public class EventServiceImpl extends BaseServiceImpl<EventMessage, String> implements EventService {

	private static Logger logger = LogManager.getLogger(EventServiceImpl.class);

	@Autowired
    private SqlSessionFactory sqlSessionFactory;

	@Autowired
	private EventDAO eventDAO;
	
	private SqlSession session;
	private EventDAO batchEventDAO;
	
	@Override
	public BaseDAO<EventMessage, String> getDAO() {
		return eventDAO;
	}
	
	public void insertEvent(EventMessage eventMessage) {
		insert(eventMessage);
		String vehicleId = eventMessage.getStation();
		Timestamp sampleTime = eventMessage.getSampleTime();
		String lon = "";
		String lat = "";
		List<EventStatusMessage> statusList = eventMessage.getStatusList();
		List<EventStatusMessage> batchList = new ArrayList<EventStatusMessage>();
		try {
			session = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
			batchEventDAO = session.getMapper(EventDAO.class);
			for(EventStatusMessage eventStatusMessage : statusList) {
				if("p_5_0_1_0".equals(eventStatusMessage.getAttributeId())) {
					lon = eventStatusMessage.getAttributeValue();
				} 
				if("p_5_0_5_0".equals(eventStatusMessage.getAttributeId())) {
					lat = eventStatusMessage.getAttributeValue();
				} 
				batchList.add(eventStatusMessage);				
			}
			batchEventDAO.insertBatchEvent(batchList);
			
			session.commit();
			session.clearCache();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(session != null)
				session.close();
		}
		
		try {
			if(!StaticMethod.isNull(lon) && !StaticMethod.isNull(lat)) {
				if(0 != Double.parseDouble(lon) && 0 != Double.parseDouble(lat)) {
					logger.info("车辆位置信息更新<" + vehicleId + ">， Lat: " + lat + "\tLon: " + lon );
					eventDAO.updateLocation(vehicleId, "" + lon, "" + lat);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
