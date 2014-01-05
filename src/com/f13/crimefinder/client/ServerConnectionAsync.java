package com.f13.crimefinder.client;

import java.util.ArrayList;

import com.f13.crimefinder.shared.Account;
import com.f13.crimefinder.shared.Crime;
import com.f13.crimefinder.shared.History;
import com.google.gwt.user.client.rpc.AsyncCallback;



public interface ServerConnectionAsync {
	void sendMail(String content, String target, AsyncCallback<java.lang.Boolean> callback);
	void getUser(String name, AsyncCallback<Account> callback);
	void addUser(String name, String password, String email, AsyncCallback<java.lang.Boolean> callback);
	void addUser(String name, String password, String email, Boolean isVerified, Boolean isAdmin, AsyncCallback<java.lang.Boolean> callback);
	void deleteUser(String name, AsyncCallback<java.lang.Boolean> callback);
	void addHistory(String name, String address, AsyncCallback<java.lang.Boolean> callback);
	void deleteHistory(String name, AsyncCallback<java.lang.Boolean> callback);
	void deleteHistory(String name, String address, AsyncCallback<java.lang.Boolean> callback);
	void getHistory(String name, AsyncCallback<ArrayList<History>> callback);
	void  searchCrime(String s, AsyncCallback<ArrayList<Crime>> callback);
	void  searchCrime(String s, Double d, AsyncCallback<ArrayList<Crime>> callback);
}
