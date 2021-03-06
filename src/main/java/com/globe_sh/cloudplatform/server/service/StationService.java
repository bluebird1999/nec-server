package com.globe_sh.cloudplatform.server.service;

import com.globe_sh.cloudplatform.server.base.BaseService;
import com.globe_sh.cloudplatform.server.entity.StationBean;

public interface StationService extends BaseService<StationBean, String> {

	boolean validateStation(int station);
	StationBean getStationBean(int station);
}
