package com.globe_sh.cloudplatform.server.explain;

import java.util.List;

import com.globe_sh.cloudplatform.server.entity.EventStatusMessage;
import com.globe_sh.cloudplatform.server.entity.DecoderBean;

public abstract class AbsResolve implements Resolve {

	protected int start;
	protected byte[] data;
	protected String uuid;
	protected List<EventStatusMessage> statusList;
	protected DecoderBean decoder;
	
	public AbsResolve(DecoderBean decoder) {
		this.decoder = decoder;
	}
	
	public void preExecute(byte[] data, int start, String uuid, List<EventStatusMessage> statusList) {
		this.data = data;
		this.start = start;
		this.uuid = uuid;
		this.statusList = statusList;
	}
	
	protected void endResolve(String value) {
		EventStatusMessage statusMessage = new EventStatusMessage(uuid, decoder.getId(), value);
		statusList.add(statusMessage);
	}
	
	protected String translate(long s) {
		String value = "";
		try {
			if(decoder.getDataPrecision() == 1) {
				long v = s + decoder.getDataDeviation();
				value = "" + v;
			} else {
				double d = (s + decoder.getDataDeviation()) * decoder.getDataPrecision();
				int decimal = getDecimal(decoder.getDataPrecision());
				if(decimal > 0) {
					value = String.format("%." + decimal + "f", d);
				} else {
					value = "" + d;
				}
			}
		} catch(Exception e) {
			value = "N/A";
		}
		return value;
	}
	
	private int getDecimal(double d) {
		String text = Double.toString(Math.abs(d));
		int integerPlaces = text.indexOf('.');
		int decimalPlaces = text.length() - integerPlaces - 1;
		
		return decimalPlaces;
	}
	
	public abstract void execute();
}
