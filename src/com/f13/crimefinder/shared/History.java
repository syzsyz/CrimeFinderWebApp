package com.f13.crimefinder.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class History implements IsSerializable{
	private String userName;
	private String address;
	private String type;
	
	public History(){
		super();
		this.userName = null;
		this.address = null;
		this.type = null;
	}
	
	public History(String userName, String address, String type) {//settings empty by def.
		super();
		this.userName = userName;
		this.address = address;
		this.type = type;
	}
	
	public String getUserName() {
		return userName;
	}
	public String getAddress() {
		return address;
	}
	public String getType() {
		return type;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setType(String type) {
		this.type = type;
	}
}
