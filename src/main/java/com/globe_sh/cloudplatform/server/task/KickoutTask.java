package com.globe_sh.cloudplatform.server.task;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.server.manager.OnlineManager;
import com.globe_sh.cloudplatform.server.mq.ActivemqOperater;


@Component
public class KickoutTask {

	private static Logger logger = LogManager.getLogger(KickoutTask.class);
	
	@Scheduled(cron= "0 0/1 * * * *")
    public void findLogoutVehicle() {
		logger.info("Running......");
		List<String> expireList = OnlineManager.getInstance().getExpireList();
		for(String clientId : expireList) {
			logger.info("Heart Overtime: " + clientId);
			if(!StaticMethod.isNull(clientId))
				ActivemqOperater.getInstance().sendMessageDefault(clientId.getBytes());
		}
		OnlineManager.getInstance().refreshExpireList();
	}
}
