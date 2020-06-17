package com.globe_sh.cloudplatform.server.explain;

import java.util.Arrays;

import com.globe_sh.cloudplatform.common.util.ByteArrayUtil;
import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.server.entity.DecoderBean;
import com.globe_sh.cloudplatform.server.util.DictUtil;

public class RealResolve extends AbsResolve {
	
	public RealResolve(DecoderBean decoder) {
		super(decoder);
	}
	
	public void execute() {
		int startIndex = start + decoder.getStartByte();
		float s;
		byte[] v = new byte[4];
		v = Arrays.copyOfRange(data,startIndex,startIndex+4);
		if("1".equals(decoder.getLowPrecede())) {
			s = StaticMethod.byteArrayToFloat(v);
		} else {
			s = StaticMethod.byteArrayToFloatLowEnd(v);
		}
		
		String value = "" + s;
		endResolve(value);
	}
}
