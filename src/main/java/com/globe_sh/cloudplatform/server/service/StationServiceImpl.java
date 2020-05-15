package com.globe_sh.cloudplatform.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.globe_sh.cloudplatform.common.cache.JedisOperater;
import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.server.base.BaseDAO;
import com.globe_sh.cloudplatform.server.base.BaseServiceImpl;
import com.globe_sh.cloudplatform.server.dao.StationDAO;
import com.globe_sh.cloudplatform.server.entity.StationBean;

@Service("stationService")
public class StationServiceImpl extends BaseServiceImpl<StationBean, String> implements StationService {	
	@Autowired
    private StationDAO stationDAO;
	
	@Override
	public BaseDAO<StationBean, String> getDAO() {
		return stationDAO;
	}
	
	public boolean validateStation(int id) {
		StationBean bean = getStationBean(id);
		if(bean == null || bean.getStationStatus() != 2)
			return false;
		
		return true;
	}
	
	public StationBean getStationBean(int id) {
		String stationJson = JedisOperater.getStation(String.valueOf(id));
		if(StaticMethod.isNull(stationJson))
			return null;
		StationBean bean = new StationBean();
		bean.fromJsonString(stationJson);
		return bean;
	}
}
