package com.f13.crimefinder.client;

import java.sql.*;
import java.util.ArrayList;

import com.f13.crimefinder.shared.Account;
import com.f13.crimefinder.shared.Crime;
import com.f13.crimefinder.shared.History;
import com.f13.crimefinder.shared.NoAddressException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("server")
public interface ServerConnection extends RemoteService {

	Account getUser(String name);
	Boolean addUser(String name, String password, String email);
	Boolean addUser(String name, String password, String email, Boolean isVerified, Boolean isAdmin);
	Boolean deleteUser(String name);
	//ArrayList<Crime> searchCrime(double lat, double lng, double distance);
	Boolean addHistory(String name, String address);
	Boolean deleteHistory(String name);
	Boolean deleteHistory(String name, String addres);
	Boolean sendMail(String content, String target);
	ArrayList<History> getHistory(String name);
	ArrayList<Crime> searchCrime(String s);
	ArrayList<Crime> searchCrime(String s, Double d);
}
