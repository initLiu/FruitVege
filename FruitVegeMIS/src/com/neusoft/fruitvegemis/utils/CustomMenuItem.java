package com.neusoft.fruitvegemis.utils;

public class CustomMenuItem {
	
	public CustomMenuItem() {
		super();
	}
	public CustomMenuItem( int id,String title) {
		super();
		this.title = title;
		this.id = id;
	}
	String title;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getItemId() {
		return id;
	}
	public void setItemId(int id) {
		this.id = id;
	}
	int id;
}
