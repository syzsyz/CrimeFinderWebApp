package com.f13.crimefinder.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Properties;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.f13.crimefinder.shared.Account;
import com.f13.crimefinder.shared.Crime;
import com.f13.crimefinder.shared.History;
import com.f13.crimefinder.shared.NoAddressException;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class DatabaseConnection{

	final String url = "jdbc:mysql://192.249.63.95:3306/newton";
	final String user="newton";
	final String password="NewtonTse";
	Double searchDistance = 1.00;
	
	public DatabaseConnection() throws SQLException{
	}

	public void getAllUsers() throws SQLException{
		Connection conn=DriverManager.getConnection(url,user,password);
		Statement statement=conn.createStatement();
		ResultSet resultSet=statement.executeQuery("SELECT * FROM User_Data");
		while(resultSet.next()){
			System.out.println(resultSet.getString("userName"));
			//System.out.println(resultSet.getString("passWord"));
			//System.out.println(resultSet.getString("settings"));
		}
		conn.close();
	}
	
	public void addUser(String name, String pass, String email) throws SQLException {
		Connection conn=DriverManager.getConnection(url,user,password);
		try{
			String s= name+pass;
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(s.getBytes("UTF-8"));
	        StringBuffer hexString = new StringBuffer();

	        for (int i = 0; i < 2; i++) {
	            String hex = Integer.toHexString(0xff & hash[i]);
	            if(hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }
	        String verifyCode = hexString.toString();
			PreparedStatement statement=conn.prepareStatement("INSERT INTO User_Data (userName, passWord, email, verification) VALUES (?,?,?,?)");
			statement.setString(1, name);
			statement.setString(2, pass);
			statement.setString(3, email);
			statement.setString(4, verifyCode);
			statement.executeUpdate();
		}
		catch (SQLException e){
			if (e.getSQLState().equals("23000")){
				System.out.println("Duplicate " +name+ " already in db");
			}
			else e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		conn.close();
	}	
	
	public void addUser(String name, String pass, String email, Boolean isVerified, Boolean isAdmin) throws SQLException {
		Connection conn=DriverManager.getConnection(url,user,password);
		try{
			String s= name+pass;
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(s.getBytes("UTF-8"));
	        StringBuffer hexString = new StringBuffer();

	        for (int i = 0; i < 2; i++) {
	            String hex = Integer.toHexString(0xff & hash[i]);
	            if(hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }
	        String verifyCode=hexString.toString();
			PreparedStatement statement=conn.prepareStatement("REPLACE INTO User_Data (userName, passWord, email, verification, isVerified, isAdmin) VALUES (?,?,?,?,?,?)");
			statement.setString(1, name);
			statement.setString(2, pass);
			statement.setString(3, email);
			statement.setString(4, verifyCode);
			statement.setBoolean(5, isVerified);
			statement.setBoolean(6, isAdmin);
			
			statement.executeUpdate();
		}
		catch (SQLException e){
			if (e.getSQLState().equals("23000")){
				System.out.println("Duplicate " +name+ " already in db");
			}
			else e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conn.close();
	}	
	
	public void deleteUser(String name) throws SQLException{
		Connection conn=DriverManager.getConnection(url,user,password);
		try{
			PreparedStatement statement=conn.prepareStatement("DELETE FROM User_Data WHERE userName=(?)");
			statement.setString(1, name);
			statement.executeUpdate();
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		conn.close();
	}
	
	public Account getUser(String name) throws SQLException{
		Connection conn=DriverManager.getConnection(url,user,password);
		Account tempt = new Account();
		try{
			PreparedStatement statement=conn.prepareStatement("SELECT * FROM User_Data WHERE userName=(?)");
			statement.setString(1, name);
			ResultSet results = statement.executeQuery();
			results.next();
			tempt.setUserName(results.getString("userName"));
			tempt.setPassWord(results.getString("passWord"));
			tempt.setEmail(results.getString("email"));
			tempt.setVerification(results.getString("verification"));
			tempt.setAdmin(results.getBoolean("isAdmin"));
			tempt.setVerified(results.getBoolean("isVerified"));
		}
		catch (SQLException e){
			if (e.getSQLState().equals("S1000")){
				System.out.println(name + " not in db");
			}
			else e.printStackTrace();
		}
		conn.close();
		return tempt;
	}
	
	public ArrayList<History> getHistory(String name) throws SQLException{
		Connection conn=DriverManager.getConnection(url,user,password);
		ArrayList<History> answers = new ArrayList<History>();
		try{
			PreparedStatement statement=conn.prepareStatement("SELECT * FROM User_History WHERE userName=(?)");
			statement.setString(1, name);
			ResultSet results=statement.executeQuery();
			while(results.next()){
				History tempt = new History();
				tempt.setUserName(results.getString("userName"));
				tempt.setAddress(results.getString("address"));
				tempt.setType(results.getString("type"));
				answers.add(tempt);
			}
			return answers;
		}
		catch (SQLException e){
			if (e.getSQLState().equals("23000")){
			}
			else e.printStackTrace();
		}
		return null;
	}
	
	public void addHistory(String name, String search) throws SQLException{
		Connection conn=DriverManager.getConnection(url,user,password);
		try{
			PreparedStatement statement=conn.prepareStatement("INSERT INTO User_History (userName, address) VALUES (?,?)");
			statement.setString(1, name);
			statement.setString(2, search);
			statement.executeUpdate();
		}
		catch (SQLException e){
			if (e.getSQLState().equals("23000")){
				System.out.println(search + " for " + name + " already in db");
			}
			else e.printStackTrace();
		}
	}
	
	public void deleteHistory(String name) throws SQLException{
		Connection conn=DriverManager.getConnection(url,user,password);
		try{
			PreparedStatement statement=conn.prepareStatement("DELETE FROM User_History WHERE userName=(?)");
			statement.setString(1, name);
			statement.executeUpdate();
		}
		catch (SQLException e){
			System.out.println(e.getSQLState());
			e.printStackTrace();
		}
	}
	
	public void deleteHistory(String name, String search) throws SQLException{
		Connection conn=DriverManager.getConnection(url,user,password);
		try{
			PreparedStatement statement=conn.prepareStatement("DELETE FROM User_History WHERE userName=(?) and address=(?)");
			statement.setString(1, name);
			statement.setString(2, search);
			statement.executeUpdate();
		}
		catch (SQLException e){
			System.out.println(e.getSQLState());
			e.printStackTrace();
		}
	}
	
	public void addCrime(Crime c) throws SQLException{
		Connection conn=DriverManager.getConnection(url,user,password);
		try{
            PreparedStatement s = conn.prepareStatement("replace into Location_Data(lat, lng, count, id, type,formatted_address,latrad,lngrad) "
                    + "values (?,?,?,?,?,?,?,?)");
          s.setDouble(1, c.getLat());
          s.setDouble(2, c.getLong());
          s.setInt(3, c.getCount());
          s.setInt(4, c.getID());
          s.setString(5, c.getType());
          s.setString(6, c.getFormattedAddress());
          s.setDouble(7, c.getLatrad());
          s.setDouble(8, c.getLngrad());

          s.executeUpdate();
          s.close();
          System.out.println("Crime ID:"+c.getID()+" is uploaded to database.");

		}
		catch (SQLException e){
			e.printStackTrace();
		}
		conn.close();
	}
		
    public ArrayList<Crime> searchCrime(String userInput) {
    	return searchCrime(userInput, searchDistance);
    }
    
    public ArrayList<Crime> searchCrime(String userInput, Double distance) {
    	searchDistance = distance;
    	double[] latlng = new double[2];
        ArrayList<Crime> results = new ArrayList<Crime>();

        
        try {
    		Connection conn=DriverManager.getConnection(url,user,password);
              userInput =userInput.replace(" ", "+");

             URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address="+userInput+"+Vancouver+BC+Canada&sensor=false");
             InputStream input = url.openStream();
             java.util.Scanner s = new java.util.Scanner(input).useDelimiter("\\A");
            String jsonstring =  s.hasNext() ? s.next() : "";
            s.close();
            if(!jsonstring.contains("formatted_address")){

                return results;
            }
            if(!jsonstring.contains("Vancouver")){

                return results;
            }
            if (jsonstring.contains("route")){
                 //some addresses are outside of vancouver
                   JSONParser parser=new JSONParser();
                   JSONObject jsonobj=(JSONObject) parser.parse(jsonstring);
                   JSONArray jsonResuls = (JSONArray) jsonobj.get("results");
                   JSONObject resultsfirst;
                   //get formatted address
                    { resultsfirst = (JSONObject) jsonResuls.get(0);}
                  
                   //get lat lng
                   JSONObject geometry = (JSONObject)resultsfirst.get("geometry");

                   JSONObject location = (JSONObject)geometry.get("location");
                   latlng[0] = (Double) location.get("lat");
                   latlng[1] = (Double) location.get("lng");
                   GeoLocation gl = GeoLocation.fromDegrees(latlng[0], latlng[1]);

                     ResultSet rs = gl.findPlacesWithinDistance(searchDistance, gl, conn);
                     Crime userinput = new Crime("User Input", latlng[0], latlng[1], (String)resultsfirst.get("formatted_address") , 0,"");
                     results.add(userinput);
                while (rs.next()){
                    Crime c = new Crime(rs.getString("type"), rs.getDouble("lat"), rs.getDouble("lng"), 
                                    rs.getString("formatted_address"), rs.getInt("count"),
                        " ");
                    results.add(c);}

                conn.close();
                return results;


            }
        } catch (MalformedURLException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
        } catch (IOException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
        } catch (ParseException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;


       }
      
	
	public static void main(String[] args) throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException{
		DatabaseConnection x=new DatabaseConnection();
		/*
		System.out.println("Adding History UBC Hospital");
		x.addHistory( "Newton", "UBC Hospital");
		
		System.out.println("Adding History UBC Hospital2");
		x.addHistory("Newton", "UBC Hospital2");
	
		System.out.println("Adding History UBC Hospital3");
		x.addHistory("Edward", "UBC Hospital2");
		
		System.out.println("Adding History UBC Hospital3 again");
		x.addHistory("Newton", "UBC Hospital2");
		
		System.out.println("Adding History UBC Hospital2 again");
		x.addHistory("Newton", "UBC Hospital");
		
		System.out.println("Delete null history");
		x.deleteHistory("Edwad");
		
		System.out.println("Print history of Newton");
		ArrayList<History> test =x.getHistory("Newton");
		ListIterator<History> iterate = test.listIterator();
		while(iterate.hasNext()){
			History tempt =iterate.next();
			System.out.println(tempt.getUserName() + " " + tempt.getAddress() + " " + tempt.getType());
		}
		
		System.out.println("Delete Edward at UBC Hospital 2");
		x.deleteHistory("Edward", "UBC Hospital2");
		x.deleteHistory("Newton");
		System.out.println("Delete Newton at UBC Hospital 2");
		x.deleteHistory("Newton", "UBC Hospital2");
	
		*/
		System.out.println("Add Newton(already added) and TEST");
		x.addUser("Newton", "NewtonTse", "Newtbelieve@hotmail.com");
		x.addUser("Test", "ABC", "BCD");
		
		System.out.println("Done print all user");
		x.getAllUsers();
		System.out.println("Done delete all user + delete non existent user");
		
		x.deleteUser("Newton");
		x.deleteUser("Nobody");
		x.deleteUser("Newton");
		x.deleteUser("Test");
		
		System.out.println("Done Print All Users");
		x.getAllUsers();
		
		System.out.println("Done, Getter Test on Newton (non existent)");
		Account tempt = x.getUser("Newton");
		System.out.println(tempt.getUserName());
		System.out.println(tempt.getPassWord());
		System.out.println(tempt.getEmail());
		System.out.println(tempt.isVerified());
		System.out.println(tempt.isAdmin());
		
		System.out.println("Done Adding Test, Newton and Test1");
		x.addUser("Test" , "ABC", "roar");
		x.addUser("Newton", "NewtonTse","tsenewton@gmail.com");
		x.addUser("Test1", "ABC","something");
		tempt = x.getUser("Newton");
		System.out.println("Getter Test on Newton (existent)");
		System.out.println(tempt.getUserName());
		System.out.println(tempt.getPassWord());
		System.out.println(tempt.getEmail());
		System.out.println(tempt.isVerified());
		System.out.println(tempt.isAdmin());
		
		System.out.println("Delete Test1");
		x.deleteUser("Test1");
		
		System.out.println("Print All Users");
		x.getAllUsers();
		
		System.out.println("Delete Test");
		x.deleteUser("test");
		
		System.out.println("Print All Users");
		x.getAllUsers();
		System.out.println("Done");
		
		
		
			System.out.println("====Starting Test on Data Parsing====");
		Parser.dataUpload(999, 1010);
			System.out.println("====Finshed Test on Data Parsing====");
			System.out.println();
			System.out.println("====Starting Test on Search Handling====");
		System.out.println("====Finshed Test on Search Handling====");
		
		
		//sendMail("awefawef", "jianc65@gmail.com");

	}
	
	
	
}