package com.globe_sh.cloudplatform.server.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.globe_sh.cloudplatform.common.util.StaticVariable;
import com.globe_sh.cloudplatform.server.entity.CodeEntity;
import com.globe_sh.cloudplatform.server.manager.SpringManager;
import com.globe_sh.cloudplatform.server.service.CodeService;
import com.globe_sh.cloudplatform.server.service.CodeServiceImpl;


public class DictUtil {

	private static DictUtil instance;
	private CodeService codeService;
	private Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
	
	private DictUtil() {
		codeService = (CodeServiceImpl)SpringManager.registerSpring().getApplicationContext().getBean("codeService");
		init();
	}
	
	public static synchronized DictUtil getInstance() {
		if(instance == null)
			instance = new DictUtil();
		
		return instance;
	}
	
	private void init() {
		List<CodeEntity> list = codeService.getAllCode();
		for(CodeEntity entity : list) {
			if(!map.containsKey(entity.getCodeType())) {
				Map<String, String> subMap = new HashMap<String, String>();
				subMap.put(entity.getCodeId(), entity.getCodeName());
				map.put(entity.getCodeType(), subMap);
			} else {
				Map<String, String> subMap = map.get(entity.getCodeType());
				subMap.put(entity.getCodeId(), entity.getCodeName());
				map.put(entity.getCodeType(), subMap);
			}
		}
		
		Map<String, String> statusMap = new HashMap<String, String>();
		List<CodeEntity> statusList = codeService.getStatusAttribute();
		for(CodeEntity entity : statusList) {
			statusMap.put(entity.getCodeId(), entity.getCodeName());
		}
		map.put(StaticVariable.MAP_KEY_STATUS_ATTRIBUTE, statusMap);
	}
	
	public String getDictValue(String dictId, String dictType) {
		Map<String, String> subMap = map.get(dictType);
		if(subMap == null)
			return "未知";
		String value = subMap.get(dictId);
		if(StringUtils.isEmpty(value)) {
			
			return "N/A";
		}
		
		return value;
	}
}
