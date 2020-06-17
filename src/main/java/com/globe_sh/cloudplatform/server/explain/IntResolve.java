package com.globe_sh.cloudplatform.server.explain;

import com.globe_sh.cloudplatform.common.util.ByteArrayUtil;
import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.server.entity.DecoderBean;
import com.globe_sh.cloudplatform.server.util.DictUtil;

public class IntResolve extends AbsResolve {

	
	public IntResolve(DecoderBean decoder) {
		super(decoder);
	}
	
	public void execute() {
		int startIndex = start + decoder.getStartByte();
		short s = 0;
		int v = 0;
		if("1".equals(decoder.getLowPrecede())) {
			s = ByteArrayUtil.getShort(data, startIndex);
		} else {
			s = ByteArrayUtil.getShortLowEnd(data, startIndex);
		}
		v = s;
		
		String dict = decoder.getDataDictionary();
		String value = "";
		
		if(!StaticMethod.isNull(dict)) {
			value = DictUtil.getInstance().getDictValue(StaticMethod.intToHexString(v), dict);
		} else {
			value = translate(v);
		}
		endResolve(value);
	}

}
