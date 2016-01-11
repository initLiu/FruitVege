package com.neusoft.fruitvegemis.datapool;

import com.neusoft.fruitvegemis.datapool.Order.OrderState;

public class UOrderRecord {
	public final String odate;
	public final String oid;
	public final float oprice;
	public final OrderState ostate;
	public final int type;
	public final String uname;

	public UOrderRecord(String odate, String oid, float oprice,
			OrderState ostate, int type, String uname) {
		this.odate = odate;
		this.oid = oid;
		this.oprice = oprice;
		this.ostate = ostate;
		this.type = type;
		this.uname = uname;
	}
}
