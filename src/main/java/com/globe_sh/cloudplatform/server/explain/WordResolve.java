package com.globe_sh.cloudplatform.server.explain;

import com.globe_sh.cloudplatform.common.util.ByteArrayUtil;
import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.server.entity.DecoderBean;
import com.globe_sh.cloudplatform.server.util.DictUtil;

public class WordResolve extends AbsResolve {

	
	public WordResolve(DecoderBean decoder) {
		super(decoder);
	}
	
	public void execute() {
		int startIndex = start + decoder.getStartByte();
		int s = 0;
		if("1".equals(decoder.getLowPrecede())) {
			s = ByteArrayUtil.getWord(data, startIndex);
		} else {
			s = ByteArrayUtil.getWordLowEnd(data, startIndex);
		}
		
		String dict = decoder.getDataDictionary();
		String value = "";
		
		if(!StaticMethod.isNull(dict)) {
			value = DictUtil.getInstance().getDictValue(StaticMethod.intToHexString(s), dict);
		} else {
			value = translate(s);
		}
		endResolve(value);
	}

}
