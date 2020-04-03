package com.globe_sh.cloudplatform.server.service;

import com.globe_sh.cloudplatform.server.base.BaseService;
import com.globe_sh.cloudplatform.server.entity.StationBean;

public interface StationService extends BaseService<StationBean, String> {

	boolean validateStation(String station);
	StationBean getStationBean(String station);
}
