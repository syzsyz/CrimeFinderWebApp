package com.f13.crimefinder.server;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.f13.crimefinder.client.ServerConnection;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.f13.crimefinder.server.*;
import com.f13.crimefinder.shared.Account;
import com.f13.crimefinder.shared.Crime;
import com.f13.crimefinder.shared.History;
import com.f13.crimefinder.shared.NoAddressException;

public class ServerConnectionImpl  extends RemoteServiceServlet implements ServerConnection {
	
	public Account getUser(String name){
		try {
			DatabaseConnection x=new DatabaseConnection();
			return x.getUser(name);
		}
		catch (SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public Boolean addUser(String name, String password, String email){
		try {
			DatabaseConnection x=new DatabaseConnection();
			x.addUser(name, password, email);
			return true;
		}
		catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public Boolean addUser(String name, String password, String email, Boolean isVerified, Boolean isAdmin){
		try {
			DatabaseConnection x=new DatabaseConnection();
			x.addUser(name, password, email, isVerified, isAdmin);
			return true;
		}
		catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public Boolean deleteUser(String name){
		try {
			DatabaseConnection x=new DatabaseConnection();
			x.deleteUser(name); 
			return true;
		}
		catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}

	public ArrayList<History> getHistory(String name){
		try {
			DatabaseConnection x=new DatabaseConnection();
			return x.getHistory(name); 
		}
		catch (SQLException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public Boolean addHistory(String name, String address){
		try {
			DatabaseConnection x=new DatabaseConnection();
			x.addHistory(name, address);
			return true;
		}
		catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public Boolean deleteHistory(String name){
		try {
			DatabaseConnection x=new DatabaseConnection();
			x.deleteHistory(name); 
			return true;
		}
		catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	
	public Boolean deleteHistory(String name, String address){
		try {
			DatabaseConnection x=new DatabaseConnection();
			x.deleteHistory(name, address); 
			return true;
		}
		catch (SQLException e){
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public ArrayList<Crime> searchCrime(String s)  {
		try {
			DatabaseConnection x=new DatabaseConnection();
			return x.searchCrime( s );

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}

	@Override
	public ArrayList<Crime> searchCrime(String s, Double d)  {
		DatabaseConnection x;
		try {
			x = new DatabaseConnection();
			return x.searchCrime( s , d);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Boolean sendMail(String content, String target) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
 
		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("crimefinderemail","crimefinderf13");
				}
			});
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("crimefinderemail@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(target));
			message.setSubject("Crime Finder Verify Code");
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(content.getBytes("UTF-8"));
	        StringBuffer hexString = new StringBuffer();

	        for (int i = 0; i < 2; i++) {
	            String hex = Integer.toHexString(0xff & hash[i]);
	            if(hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }

			message.setText("Verify code:" + hexString.toString());
 
			Transport.send(message);
			return true;
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	

}
