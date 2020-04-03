package com.globe_sh.cloudplatform.server.dao;

import com.globe_sh.cloudplatform.server.base.BaseDAO;
import com.globe_sh.cloudplatform.server.entity.StationBean;

public interface StationDAO extends BaseDAO<StationBean, String> {
	
	StationBean getStationById(String station);
}
