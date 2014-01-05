package com.f13.crimefinder.server;




/**
 * Represents a point on the surface of a sphere. (The Earth is almost spherical.)
 * To create an instance, call one of the static methods fromDegrees() or fromRadians().
 *
 * This code was originally published at
 * http://JanMatuschek.de/LatitudeLongitudeBoundingCoordinates#Java
 * @author Jan Philip Matuschek
 * @version 22 September 2010 */

public class GeoLocation {

        private double radLat;  // latitude in radians
        private double radLon;  // longitude in radians

        private double degLat;  // latitude in degrees
        private double degLon;  // longitude in degrees

        private static final double MIN_LAT = Math.toRadians(-90d);  // -PI/2
        private static final double MAX_LAT = Math.toRadians(90d);   //  PI/2
        private static final double MIN_LON = Math.toRadians(-180d); // -PI
        private static final double MAX_LON = Math.toRadians(180d);  //  PI
        private static final double EARTH_RADIUS = 6371.01; // in km

        private Integer count;

        private GeoLocation () {
        }

        /*** @param latitude the latitude, in degrees.
         *** @param longitude the longitude, in degrees.        */

        public static GeoLocation fromDegrees(double latitude, double longitude) {

                GeoLocation result = new GeoLocation();

                result.radLat = Math.toRadians(latitude);

                result.radLon = Math.toRadians(longitude);

                result.degLat = latitude;

                result.degLon = longitude;

                result.checkBounds();

                return result;
        }


        /*** @param latitude the latitude, in radians.
         *** @param longitude the longitude, in radians.       */

        public static GeoLocation fromRadians(double latitude, double longitude) {

                GeoLocation result = new GeoLocation();

                result.radLat = latitude;

                result.radLon = longitude;

                result.degLat = Math.toDegrees(latitude);

                result.degLon = Math.toDegrees(longitude);

                result.checkBounds();

                return result;
        }

        private void checkBounds() {
                if (radLat < MIN_LAT || radLat > MAX_LAT ||
                    radLon < MIN_LON || radLon > MAX_LON)
                        throw new IllegalArgumentException();
        }

        public Integer getCount(){    	return count;        }

        public void setCount(Integer c){       count =c;     }

        /*** @return the latitude, in degrees.      */
        public double getLatitudeInDegrees() {    return degLat;  }

        /*** @return the longitude, in degrees.     */
        public double getLongitudeInDegrees() {    return degLon; }


        /*** @return the latitude, in radians.       */
        public double getLatitudeInRadians() {     return radLat;  }

        /*** @return the longitude, in radians.      */
        public double getLongitudeInRadians() {     return radLon; }


        @Override
        public String toString() {
                return "(" + degLat + "\u00B0, " + degLon + "\u00B0) = (" +
                                 radLat + " rad, " + radLon + " rad)";
        }


        /*** Computes the great circle distance between this GeoLocation instance
         *** and the location argument.

         * @param radius the radius of the sphere, e.g. the average radius for a
         * spherical approximation of the figure of the Earth is approximately
         * 6371.01 kilometers.

         * @return the distance, measured in the same unit as the radius argument.   */

        public double distanceTo(GeoLocation location, double radius) {

                return Math.acos(Math.sin(radLat) * Math.sin(location.radLat) +

                                Math.cos(radLat) * Math.cos(location.radLat) *

                                Math.cos(radLon - location.radLon)) * radius;
        }

        /**Computes the bounding coordinates of all points on the surface
         * of a sphere that have a great circle distance to the point represented
         * by this GeoLocation instance that is less or equal to the distance
         * argument.
         * 
         * @param distance the distance from the point represented by this
         * GeoLocation instance. Must me measured in the same unit as the radius
         * argument.

         * @return an array of two GeoLocation objects such that:
         * 
         * 1stArrayEle.latitude <= validPoint.latitude <= 2ndArrayEle.latitude
         *
         * AND 
         * 
         * If (1stArrayEle.longitude <= 2ndArrayEle.longitude) then
         * 1stArrayEle.longitude <= validPoint.longitude <= 2ndArrayEle.longitude
         * 
         * If ( 1stArrayEle.longitude > 2ndArrayEle.longitude)
         * 1stArrayEle.longitude <= validPoint.longitude OR <-- this is different
         * validPoint.longitude <= 2ndArrayEle.longitude       */

        public GeoLocation[] boundingCoordinates(double distance) {
        	
                if (distance < 0d || EARTH_RADIUS < 0d)
                        throw new IllegalArgumentException();

                // angular distance in radians on a great circle
                double radDist =  distance /EARTH_RADIUS;

                double minLat = radLat - radDist;
                double maxLat = radLat + radDist;
                double minLon, maxLon;
                
                if (minLat > MIN_LAT && maxLat < MAX_LAT) {

                        double deltaLon = Math.asin(Math.sin(radDist) /

                                Math.cos(radLat));

                        minLon = radLon - deltaLon;

                        if (minLon < MIN_LON) minLon += 2d * Math.PI;

                        maxLon = radLon + deltaLon;

                        if (maxLon > MAX_LON) maxLon -= 2d * Math.PI;

                } else {
                        // a pole is within the distance

                        minLat = Math.max(minLat, MIN_LAT);

                        maxLat = Math.min(maxLat, MAX_LAT);

                        minLon = MIN_LON;

                        maxLon = MAX_LON;
                }
                return new GeoLocation[]{fromRadians(minLat, minLon),
                                         fromRadians(maxLat, maxLon)};
        }

       /*** @param distance the distance from the point represented by this
        *** GeoLocation instance. Must me measured in the same unit as the radius
        *** argument.
        * 
		* @param geoLoc is the centre location where the radius will be measured

        * @return boolean, true if this geoLocation is within bounds, false if not.
        * 
        * Latitude requirement for valid point:
        * 1stArrayEle.latitude <= validPoint.latitude <= 2ndArrayEle.latitude
        *
        * Longitude requirement for valid point:
        * 
        * If (1stArrayEle.longitude <= 2ndArrayEle.longitude) then
        * 1stArrayEle.longitude <= validPoint.longitude <= 2ndArrayEle.longitude
        * 
        * If ( 1stArrayEle.longitude > 2ndArrayEle.longitude) means 180th meridian is
        * within the radius
        * 
        * 1stArrayEle.longitude <= validPoint.longitude OR <-- this is different
        * validPoint.longitude <= 2ndArrayEle.longitude       */
        
        public boolean withinBounds(double distance, GeoLocation geoLoc){
        	//get bounding geoLocations
        	GeoLocation[] boundaryCoords =  geoLoc.boundingCoordinates(distance);
        	// compare latitude requirements first, if fail return false, if true go to 
        	// more involved longitude requirements
        	if (getLatitudeInDegrees() >= boundaryCoords[0].getLatitudeInDegrees() &&
        		getLatitudeInDegrees() <= boundaryCoords[1].getLatitudeInDegrees()) {
        		if (boundaryCoords[0].getLongitudeInDegrees() <= 
        			boundaryCoords[1].getLongitudeInDegrees()) {
        			return (getLongitudeInDegrees() >= boundaryCoords[0].getLongitudeInDegrees() && 
        			   getLongitudeInDegrees() <= boundaryCoords[1].getLongitudeInDegrees());
        		}
        		else {
        			return (getLongitudeInDegrees() >= boundaryCoords[0].getLongitudeInDegrees() || 
             			   getLongitudeInDegrees() <= boundaryCoords[1].getLongitudeInDegrees());
        		}
        	}
        	else
        		return false;
        }
        	
        
        
        /*** @param distance radius of the sphere.
         * @param location center of the query circle.
         * @param distance radius of the query circle.
         * @param connection an SQL connection.
         * @return places within the specified distance from location.     */

        public java.sql.ResultSet findPlacesWithinDistance(double distance,

                         GeoLocation location, java.sql.Connection connection) throws java.sql.SQLException {



                GeoLocation[] boundingCoordinates = location.boundingCoordinates(distance);

                boolean meridian180WithinDistance =

                        boundingCoordinates[0].getLongitudeInRadians() >

                        boundingCoordinates[1].getLongitudeInRadians();


                java.sql.PreparedStatement statement = connection.prepareStatement(

                        "SELECT * FROM Location_Data WHERE (latrad >= ? AND latrad <= ?) AND (lngrad >= ? " +

                        (meridian180WithinDistance ? "OR" : "AND") + " lngrad <= ?) AND " +

                        "acos(sin(?) * sin(latrad) + cos(?) * cos(latrad) * cos(lngrad - ?)) <= ?");

                statement.setDouble(1, boundingCoordinates[0].getLatitudeInRadians());

                statement.setDouble(2, boundingCoordinates[1].getLatitudeInRadians());

                statement.setDouble(3, boundingCoordinates[0].getLongitudeInRadians());

                statement.setDouble(4, boundingCoordinates[1].getLongitudeInRadians());

                statement.setDouble(5, location.getLatitudeInRadians());

                statement.setDouble(6, location.getLatitudeInRadians());

                statement.setDouble(7, location.getLongitudeInRadians());

                statement.setDouble(8, distance/EARTH_RADIUS);

                return statement.executeQuery();

        }



        /*public static void main(String[] args) {



                double earthRadius = 6371.01;

                GeoLocation myLocation = GeoLocation.fromRadians(1.3963, -0.6981);

                double distance = 1000;

        

                java.sql.Connection connection = 



                java.sql.ResultSet resultSet = findPlacesWithinDistance(

                                earthRadius, myLocation, distance, connection);



                

        } */



}
