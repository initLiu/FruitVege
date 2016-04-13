package com.neusoft.fruitvegemis.utils;

import java.util.ArrayList;
import java.util.List;

public class CustomMenu {
	List<CustomMenuItem> menuItems;
	String headerTitle;
	
	public CustomMenu() {
		menuItems = new ArrayList<CustomMenuItem>();
	}
	
	public void add(int id, String title){
		CustomMenuItem menuItem = new CustomMenuItem();
		menuItem.id = id;
		menuItem.title = title;
		menuItems.add(menuItem);
	}
	
	public void add(CustomMenuItem item){
		menuItems.add(item);
	}
	
	public CustomMenuItem getItem(int i){
		return menuItems.get(i);
	}
	
	public void clear(){
		menuItems.clear();
	}
	
	public int size(){
		return menuItems.size();
	}

	public void setHeaderTitle(String contextTitleText) {
		headerTitle = contextTitleText;
	}
	
	public String getHeaderTitle(){
		return headerTitle;
	}

	public CustomMenuItem[] toArray() {
		if(menuItems != null && menuItems.size() >0){
			CustomMenuItem [] items = new CustomMenuItem[menuItems.size()];
			menuItems.toArray(items);
			return items;
		}
		return null;
	}
}
