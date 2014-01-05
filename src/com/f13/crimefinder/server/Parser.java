package com.f13.crimefinder.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.f13.crimefinder.shared.Crime;
import com.f13.crimefinder.shared.NoAddressException;

import au.com.bytecode.opencsv.CSVReader;

import java.sql.*;


public class Parser {
    
     public Parser(){
         
     }
    
/*downlaod csv file base on dataurl, parse and clean the address
then store into arraylist of crime location
*/    
     public static ArrayList<Crime> parseLocation(String dataurl){
         
          ArrayList<Crime>  locationarray = new ArrayList<Crime>();
          try {
               InputStream stream = new URL(dataurl).openStream();
               Reader reader = new InputStreamReader(stream);
              CSVReader csvreader = new CSVReader(reader);
              String [] nextLine;
              ArrayList<String> stringarray = new ArrayList<String>();
              while ((nextLine = csvreader.readNext()) != null) {
              String cleanlocation= cleanLocation(nextLine[3]);
              String type = nextLine[0];
              //nextline[3] is where the address stored into csv file.
              Crime cl = new Crime(cleanlocation, type);
              Integer index = stringarray.indexOf(cleanlocation);
             if (index!=-1){
                  // if repeated.
                  cl.setCount(locationarray.get(index).getCount());
                  cl.addCount();
                  stringarray.set(index, cleanlocation);
                  locationarray.set(index, cl);
             }
             else
             {locationarray.add(cl);
             stringarray.add(cleanlocation);}
                  
                  
              }


          } catch (MalformedURLException e) {
               e.printStackTrace();
          } catch (IOException e) {
               e.printStackTrace();
          }
          return locationarray;
         
         
     }
     //replace the address to something acceptable by Google api.
     private static String cleanLocation(String location){
          location = location.replace("XX", "50");
          location =location.replace("/", "and");
          location =location.replace("BROADWAY AVE", "broadway");
          location = location + ", BC, Canada";
          location =location.replace(" ", "+");
          return location;
     }
    
    
    
     //For a CrimeLocation, access google API, parse JSON file to add lat,lng,formatted address.
     //then upload it to SQL databse
     private static void parseJson(Crime c, int i) throws SQLException{
          try {
               URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address="+c.getAddress()+"&sensor=false");
               InputStream input = url.openStream();

               java.util.Scanner s = new java.util.Scanner(input).useDelimiter("\\A");
              String jsonstring =  s.hasNext() ? s.next() : "";
              s.close();
              c.setID(i);
              if (jsonstring.contains("Vancouver")){
                   //some addresses are outside of vancouver
                     JSONParser parser=new JSONParser();
                     JSONObject jsonobj=(JSONObject) parser.parse(jsonstring);
                     JSONArray results = (JSONArray) jsonobj.get("results");

                     //get formatted address
                     JSONObject resultsfirst = (JSONObject) results.get(0);
                     c.setFormattedAddress((String) resultsfirst.get("formatted_address")); 
                    
                     //get lat lng
                     JSONObject geometry = (JSONObject)resultsfirst.get("geometry");
                     JSONObject location = (JSONObject)geometry.get("location");
                     double lat = (Double) location.get("lat");
                     double lng = (Double) location.get("lng");
                         double latrad = Math.toRadians(lat);
                         double lngrad = Math.toRadians(lng);
                         c.setLatrad(latrad);
                         c.setLngrad(lngrad);
                     c.setLat(lat);
                     c.setLong(lng);
                     
                     
                     DatabaseConnection dbc = new DatabaseConnection();
                     dbc.addCrime(c);
                     Thread.sleep(300);
                      //pause for 500ms to allow data to be uploaded
                      //prepare for next google query

              }
             
             
          } catch (MalformedURLException e) {
               e.printStackTrace();
          } catch (IOException e) {
               e.printStackTrace();
          } catch (ParseException e) {
               e.printStackTrace();
          } catch (InterruptedException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
          }
     }
    
    
    
    
     public static void dataUpload(Integer INIT, Integer END) throws SQLException{
          ArrayList<Crime>  locationarray = parseLocation("ftp://webftp.vancouver.ca/opendata/csv/crime_2011.csv");
          for (int i = INIT; i<=END; i++){
               try {
                    parseJson(locationarray.get(i), i);
               } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
               }}
    

     }

     public static void parser(String [ ] args) throws NoAddressException, SQLException 
     {
          // dataUpload uploads data into database, uncomment when need to upload.
          // dataUpload(503, 600);
         
          //Test SearchHandler
          //Default distance = 3KM.
        //  SearchHandler sl1 = new SearchHandler("west broadway and alma");
          //Can change to 0.5KM etc.
          //SearchHandler sl2 = new SearchHandler("west broadway and alma", 0.5);
         
          //Call getSurrounding will return array of Crime ID within surrounding
          //ArrayList<Crime> idarray =  (ArrayList<Crime>) sl2.getSurrounding();
         
         
         
          //Printing stuff
          //System.out.println("Entered Location:");
          //System.out.println( sl2.getLat()+","+sl2.getLng());
          //System.out.println("Data within distance:");
          //for (Crime i:idarray ){
           //   System.out.println (i.getFormattedAddress());
         // }
     }

     }

    
    
