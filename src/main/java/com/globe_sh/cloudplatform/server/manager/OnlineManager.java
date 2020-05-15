package com.globe_sh.cloudplatform.server.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.globe_sh.cloudplatform.common.cache.JedisOperater;
import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.common.util.StaticVariable;
import com.globe_sh.cloudplatform.server.bean.LoginBean;
import com.globe_sh.cloudplatform.server.bean.LogoutBean;
import com.globe_sh.cloudplatform.server.service.StationService;
import com.globe_sh.cloudplatform.server.service.StationServiceImpl;

public class OnlineManager {

	private static Logger logger = LogManager.getLogger(OnlineManager.class);

	private static final long EXPIRE_TIME = 1000 *  3600 * 4; //4hour失效
	private static OnlineManager instance;
	private StationService stationService;
	private Map<String, String> loginMap = new ConcurrentHashMap<String, String>();
	private Map<String, Long> heartMap = new ConcurrentHashMap<String, Long>();
	
	private List<String> expireList = new ArrayList<String>();

	private OnlineManager() {

	}

	public static synchronized OnlineManager getInstance() {
		if (instance == null)
			instance = new OnlineManager();

		return instance;
	}

	/**
	 * 仅服务启动时执行一次
	 */
	public void startMonitor() {
		logger.info("Loading Station Online Status From Redis.");
		stationService = (StationServiceImpl) SpringManager.registerSpring().getApplicationContext()
				.getBean("stationService");
		
		ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
		
		exec.scheduleWithFixedDelay(new UpdateRunnable(), 1000, 15000, TimeUnit.MILLISECONDS);
	}
	
	public List<String> getExpireList() {
		return expireList;
	}
	
	public void refreshExpireList() {
		expireList.clear();
	}
	
	/**
	 * 仅服务启动时执行一次，从缓存加载连接ID和vin的对应关系
	 */
	public void rebuildLoginMap() {
		Map<String, String> map = JedisOperater.initOnlineStatus();
		try {
			for (Entry<String, String> entry : map.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				JSONObject json = JSONObject.parseObject(value);
				long now = System.currentTimeMillis();
				String clientId = json.getString("ClientId");
				if(StaticVariable.STATION_STATUS_ONLINE.equals(json.getString("Status"))) {
					loginMap.put(clientId, key);
					heartMap.put(clientId, now);
				} else {
					expireList.add(clientId);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void login(LoginBean bean) {
		if (!stationService.validateStation( bean.getStation() )) {
			bean.setLoginResult(StaticVariable.STATION_RESPONSE_FAILURE);
			return;
		}
		
		if (loginMap.containsKey(bean.getClientId())) {
			bean.setLoginResult(StaticVariable.STATION_RESPONSE_DUPLICATE);
			return;
		} else {
			String status = JedisOperater.getOnlineStatus(String.valueOf(bean.getStation()));
			if(!StaticMethod.isNull(status)) {
				JSONObject json = JSONObject.parseObject(status);
				if (json.containsKey("Status") && StaticVariable.STATION_STATUS_ONLINE.equals(json.get("Status"))) {
					kickout(bean.getClientId());
					bean.setLoginResult(StaticVariable.STATION_RESPONSE_DUPLICATE);
					return;
				}
			}
		}

		JSONObject json = null;
		String status = JedisOperater.getOnlineStatus(String.valueOf(bean.getStation()));
		if (StaticMethod.isNull(status)) {
			json = new JSONObject();
		} else {
			json = JSONObject.parseObject(status);
			if (StaticVariable.STATION_STATUS_ONLINE.equals(json.getString("Status"))) {
				if(bean.getClientId().equals(json.getString("ClientId"))) {
					bean.setLoginResult(StaticVariable.STATION_RESPONSE_DUPLICATE);
					return;
				} else {
					json.put("Status", StaticVariable.STATION_STATUS_OFFLINE);
					bean.setLoginResult(StaticVariable.STATION_RESPONSE_FAILURE);
					JedisOperater.updateOnlineStatus(String.valueOf(bean.getStation()), json.toJSONString());
					return;
				}
			}
		}

		json.put("Station", bean.getStation());
		json.put("Status", StaticVariable.STATION_STATUS_ONLINE);
		json.put("OnlineTime", StaticMethod.getTimeString(bean.getLoginTime()));
		json.put("HeartTime", StaticMethod.getTimeString(bean.getLoginTime()));
		json.put("Operater", StaticVariable.LOGIN_OPERATER_DEVICE);
		json.put("ClientId", bean.getClientId());
		json.put("ProcessTime", StaticMethod.getTimeString(0));
		JedisOperater.updateOnlineStatus(String.valueOf(bean.getStation()), json.toJSONString());
		loginMap.put(bean.getClientId(), String.valueOf(bean.getStation()));
		bean.setLoginResult(StaticVariable.STATION_RESPONSE_SUCCESS);

		long now = System.currentTimeMillis();
		heartMap.put(bean.getClientId(), now);
	}

	public void logout(LogoutBean bean) {
		if (!loginMap.containsKey(bean.getClientId())) {
			bean.setLogoutResult(StaticVariable.STATION_RESPONSE_DUPLICATE);
			return;
		}
		
		if (!stationService.validateStation(bean.getStation())) {
			bean.setLogoutResult(StaticVariable.STATION_RESPONSE_FAILURE);
			return;
		}
		
		JSONObject json = null;
		String status = JedisOperater.getOnlineStatus(String.valueOf(bean.getStation()));
		if (StaticMethod.isNull(status)) {
			bean.setLogoutResult(StaticVariable.STATION_RESPONSE_FAILURE);
			return;
		}

		json = JSONObject.parseObject(status);
		if (StaticVariable.STATION_STATUS_OFFLINE.equals(json.getString("Status"))) {
			bean.setLogoutResult(StaticVariable.STATION_RESPONSE_DUPLICATE);
			return;
		}

		json.put("Status", StaticVariable.STATION_STATUS_OFFLINE);
		json.put("OfflineTime", StaticMethod.getTimeString(bean.getLogoutTime()));
		json.put("Operater", StaticVariable.LOGIN_OPERATER_DEVICE);
		json.put("ProcessTime", StaticMethod.getTimeString(0));
		JedisOperater.updateOnlineStatus(String.valueOf(bean.getStation()), json.toJSONString());
		loginMap.remove(bean.getClientId());
		heartMap.remove(bean.getClientId());
		expireList.add(bean.getClientId());
		
		bean.setLogoutResult(StaticVariable.STATION_RESPONSE_SUCCESS);
	}

	public void kickout(String clientId) {
		logger.info("System Kickout: " + clientId);
		if(!loginMap.containsKey(clientId)) {
			logger.info("System Kickout: client id not log in!");
			return;
		}
		
		String station = loginMap.get(clientId);
		if (StaticMethod.isNull(station)) {
			logger.info("System Kickout: client id is not related to valid station!");
			return;
		}

		String status = JedisOperater.getOnlineStatus(station);
		if (StaticMethod.isNull(status)) {
			logger.info("System Kickout: station status is not valid!");
			return;
		}

		JSONObject json = JSONObject.parseObject(status);
		if (StaticVariable.STATION_STATUS_ONLINE.equals(json.get("Status"))) {
			json.put("Status", StaticVariable.STATION_STATUS_OFFLINE);
			json.put("OfflineTime", StaticMethod.getTimeString(0));
			json.put("Operater", StaticVariable.LOGIN_OPERATER_SYSTEM);
			json.put("ProcessTime", StaticMethod.getTimeString(0));
			JedisOperater.updateOnlineStatus(station, json.toJSONString());
		}
		else
			logger.info("System Kickout: station status is not online!");
		
		loginMap.remove(clientId);
		heartMap.remove(clientId);
		expireList.add(clientId);
	}
	
	public void kickall() {
		for (Entry<String, String> entry : loginMap.entrySet()) {
			String clientId = entry.getKey();
			kickout(clientId);
		}
	}

	public boolean hasLogin(String clientId) {
		return loginMap.containsKey(clientId);
	}
	
	public void heart(int station) {
		JSONObject json = null;
		String status = JedisOperater.getOnlineStatus(String.valueOf(station));
		if (!StaticMethod.isNull(status)) {
			long now = System.currentTimeMillis();
			json = JSONObject.parseObject(status);
			json.put("HeartTime", StaticMethod.getTimeString(0));
			heartMap.put(json.getString("ClientId"), now);
			JedisOperater.updateOnlineStatus(String.valueOf(station), json.toJSONString());
		}
	}

	public boolean online(String vin, JSONObject json) {
		logger.info("Recevie Station Online Request Of Vin: " + vin);
		try {
			JedisOperater.updateOnlineStatus(vin, json.toJSONString());
			return true;
		} catch (Exception e) {
			logger.error("Error When Save Station Online To Cahce Of Vin: " + vin);
		}
		return false;
	}

	public boolean offline(String vin) {
		logger.info("Recevie Station Offline Request Of Vin: " + vin);
		try {
			String status = JedisOperater.getOnlineStatus(vin);
			if (StaticMethod.isNull(status)) {
				logger.warn("Cache Has Empty Record Of Vin: " + vin);
				return true;
			}
			JSONObject json = JSONObject.parseObject(status);
			json.put("Status", StaticVariable.STATION_STATUS_OFFLINE);
			json.put("OfflineTime", StaticMethod.getTimeString(0));
			JedisOperater.updateOnlineStatus(vin, json.toJSONString());
			return true;
		} catch (Exception e) {
			logger.error("Error When Save Station Online To Cahce Of Vin: " + vin);
		}
		return false;
	}
	
	public String getClientId(String vin) {
		String clientId = null;
		try {
			String status = JedisOperater.getOnlineStatus(vin);
			if (StaticMethod.isNull(status)) {
				return null;
			}
			JSONObject json = JSONObject.parseObject(status);
			clientId = json.getString("ClientId");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return clientId;
	}
	
	private class UpdateRunnable implements Runnable {

		public void run() { 
			for (Entry<String, Long> entry : heartMap.entrySet()) {
				String clientId = entry.getKey();
				String station = loginMap.get(clientId);
				long expireTime = entry.getValue();
				long now = System.currentTimeMillis();
				if((now - expireTime) > EXPIRE_TIME) {
					logger.info("Heart Overtime: " + station);
					kickout(clientId);
				}
			}
		}
	}
}
