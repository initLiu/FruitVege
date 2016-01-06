package com.neusoft.fruitvegemis.datapool;

public class Goods {
	public final String gname;
	public final float gprice;
	public final String sname;
	public final byte[] gpicture;
	
	public Goods(String _gname,float _gprice,String _sname,byte[] _gpicture){
		gname = _gname;
		gpicture = _gpicture;
		sname = _sname;
		gprice = _gprice;
	}
}
