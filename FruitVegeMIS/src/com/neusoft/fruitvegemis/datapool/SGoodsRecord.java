package com.neusoft.fruitvegemis.datapool;

import com.neusoft.fruitvegemis.persistence.Entity;

public class SGoodsRecord extends Entity{
	public String sname;// 卖家
	public String gname;// 商品名
	public float gprice;// 商品价格
	public byte[] gpicture;// 商品图片

	public void init(String _sname, String _gname, float _gprice,
			byte[] _gpicture) {
		this.sname = _sname;
		this.gname = _gname;
		this.gprice = _gprice;
		this.gpicture = _gpicture;
	}
}
