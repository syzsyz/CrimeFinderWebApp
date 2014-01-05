package com.f13.crimefinder.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Account implements IsSerializable{
	private String userName;
	private String passWord;
	private String email;
	private String verification;
	private Boolean isVerified;
	private Boolean isAdmin;
	
	public Account(){
		super();
		this.userName = null;
		this.passWord = null;
		this.email = null;
		this.verification = null;
		this.isVerified = false;
		this.isAdmin = false;
	}
	
	
	public Account(Account a){
		super();
		this.userName = a.getUserName();
		this.passWord = a.getPassWord();
		this.email = a.getEmail();
		this.verification = a.getVerification();
		this.isVerified = a.isVerified;
		this.isAdmin = a.isAdmin;
	}

	
	public Account(String userName, String passWord, String email) {
		super();
		this.userName = userName;
		this.passWord = passWord;
		this.email = email;
	}
	
	public String getUserName() {
		return userName;
	}
	public String getPassWord() {
		return passWord;
	}
	public String getEmail() {
		return email;
	}
	public String getVerification(){
		return verification;
	}
	public Boolean isAdmin(){
		return isAdmin;
	}
	public Boolean isVerified(){
		return isVerified;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setVerification(String verification){
		this.verification= verification;
	}
	public void setAdmin(Boolean status){
		this.isAdmin = status;
	}
	public void setVerified(Boolean status){
		this.isVerified = status;
	}
}
