package com.f13.crimefinder.shared;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import com.google.gwt.user.client.rpc.IsSerializable;


// can add in lat long etc fields  later
public class Crime implements IsSerializable{
        private int id;
        private String type;
        private double lat;
        private double lng;
        double latrad;
        double lngrad;
        
        
    	public Crime(){
    		super();
    	}

        
        
        
        
        public double getLatrad() {
               return latrad;
          }


          public void setLatrad(double latrad) {
               this.latrad = latrad;
          }


          public double getLngrad() {
               return lngrad;
          }


          public void setLngrad(double lngrad) {
               this.lngrad = lngrad;
          }
          private String address;
        private int count;
        private String rating;
        private String formatted_address;

        public Crime(String crimeaddress, String type){
                address = crimeaddress;
                count = 1;
                this.type = type;
        }

       
        public Crime(String type, double lat, double lng, String address, int count,
                        String rating) {
                super();
                this.type = type;
                this.lat = lat;
                this.lng = lng;
                this.formatted_address = address;
                this.count = count;
                this.rating = rating;
        }
       
        public String getType() {
                return type;
        }
        public double getLat() {
                return lat;
        }
        public double getLong() {
                return lng;
        }
        public String getAddress() {
                return address;
        }
        public int getCount() {
                return count;
        }
       
        public int addCount(){
             return count++;
        }
        public String getRating() {
                return rating;
        }
        public void setType(String type) {
                this.type = type;
        }
        public void setLat(double lat) {
                this.lat = lat;
        }
        public void setLong(double lng) {
                this.lng = lng;
        }
        public void setAddress(String address) {
                this.address = address;
        }
        public void setCount(int count) {
                this.count = count;
        }
        public void setRating(String rating) {
                this.rating = rating;
        }
       
        public Integer getID(){
             return id;
        }
        public void setID(Integer id){
              this.id = id;
        }
       
        public String getFormattedAddress(){
             return formatted_address;
        }
        public void setFormattedAddress(String formatted_address){
              this.formatted_address = formatted_address;
        }



       
       
}