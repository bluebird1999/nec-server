package com.globe_sh.cloudplatform.server.explain;

import com.globe_sh.cloudplatform.common.util.ByteArrayUtil;
import com.globe_sh.cloudplatform.server.entity.DecoderBean;


public class DwordResolve extends AbsResolve {

	public DwordResolve(DecoderBean decoder) {
		super(decoder);
	}
	
	public void execute() {
		int startIndex = start + decoder.getStartByte();
		int s = 0;
		if("1".equals(decoder.getLowPrecede())) {
			s = ByteArrayUtil.getInt(data, startIndex);
		} else {
			s = ByteArrayUtil.getIntLowEnd(data, startIndex);
		}
		
		String value = translate(s);
		endResolve(value);
	}
}
