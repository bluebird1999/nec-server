package com.globe_sh.cloudplatform.server.explain;

import com.globe_sh.cloudplatform.common.util.StaticMethod;
import com.globe_sh.cloudplatform.server.entity.DecoderBean;
import com.globe_sh.cloudplatform.server.util.DictUtil;

public class ByteResolve extends AbsResolve {
	
	public ByteResolve(DecoderBean decoder) {
		super(decoder);
	}
	
	public void execute() {
		int startIndex = start + decoder.getStartByte();
		int v = data[startIndex] & 0xFF;
		String dict = decoder.getDataDictionary();
		String value = "" + v;
		if(!StaticMethod.isNull(dict)) {
			value = DictUtil.getInstance().getDictValue(StaticMethod.intToHexString(v), dict);
		} else {
			value = translate(v);
		}
		endResolve(value);
	}
}
