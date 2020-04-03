package com.globe_sh.cloudplatform.server.service;

import com.globe_sh.cloudplatform.server.base.BaseService;
import com.globe_sh.cloudplatform.server.entity.EventMessage;

public interface EventService extends BaseService<EventMessage, String> {

	void insertEvent(EventMessage eventMessage);

}
