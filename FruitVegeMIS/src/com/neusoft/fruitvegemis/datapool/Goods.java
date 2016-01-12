package com.neusoft.fruitvegemis.datapool;

public class Goods {
	public final String gname;
	public final float gprice;
	public final String sname;
	public final byte[] gpicture;
	public final int item_type;
	public static final int ITEM_TYPE_GOODS = 0;
	public static final int ITEM_TYPE_COMMIT = 1;;

	public Goods(String _gname, float _gprice, String _sname, byte[] _gpicture) {
		gname = _gname;
		gpicture = _gpicture;
		sname = _sname;
		gprice = _gprice;
		item_type = ITEM_TYPE_GOODS;
	}

	public Goods() {
		gname = "";
		gpicture = null;
		sname = "";
		gprice = 0;
		item_type = ITEM_TYPE_COMMIT;
	}
}
