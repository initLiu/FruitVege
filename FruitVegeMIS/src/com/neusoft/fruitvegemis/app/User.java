package com.neusoft.fruitvegemis.app;

public class User {
	private String uin;
	private String password;
	private int type;

	public User(String uin, String pwd, int type) {
		this.uin = uin;
		this.password = pwd == null ? "" : pwd;
		this.type = type;
	}

	public String getUin() {
		return uin;
	}

	public void setUin(String uin) {
		this.uin = uin;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof User) {
			User other = (User) o;
			return other.uin.equals(this.uin)
					&& other.password.equals(this.password)
					&& other.type == this.type;
		}
		return false;
	}

}
