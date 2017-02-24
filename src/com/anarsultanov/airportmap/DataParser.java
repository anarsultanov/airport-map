/*
Copyright (c) 2017 Anar Sultanov

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.anarsultanov.airportmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PApplet;

/** 
 * Class for parsing information. 
 *
 * @author Anar Sultanov
 *
 */

public class DataParser {
	private static String ip;
	private static String locationData;
	
	/*
	 * This method is to parse user's location  
	 * by using getUsersIp, parseLocationByIp methods
	 *  
	 * @param p - PApplet being used
	 */
	public static PointFeature parseLocation (PApplet p){
		try {
			getUsersIp();
			parseLocationByIp();
		} catch (MalformedURLException e){
			e.printStackTrace();
		} catch (IOException ex){
			ex.printStackTrace();
		}
		
		String[] columns = locationData.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		float lat = Float.parseFloat(columns[8]);
		float lon = Float.parseFloat(columns[9]);
		Location loc = new Location(lat, lon);
		PointFeature point = new PointFeature(loc);
		
		point.putProperty("ip", columns[0]);
		point.putProperty("code", columns[1]);
		point.putProperty("city", columns[5]);
		point.putProperty("country", columns[2]);
		
		return point;
	}

	// Helper method for getting user's location data by IP
	private static void parseLocationByIp() throws MalformedURLException, IOException {
		URL url = new URL("http://freegeoip.net/csv/" + ip);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.connect();
		InputStream is = connection.getInputStream();
		int status = connection.getResponseCode();
		if (status != 200) {
		    return;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		locationData = reader.readLine();
	}
	// Helper method for getting user's external IP
	private static void getUsersIp() throws MalformedURLException, IOException {
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(
                whatismyip.openStream()));
		ip = in.readLine();
	}
	
	/*
	 * This method is to parse a file containing airport information.  
	 * The file and its format can be found: 
	 * http://openflights.org/data.html#airport
	 *  
	 * @param p - PApplet being used
	 * @param fileName - file name or URL for data source
	 */
	public static List<PointFeature> parseAirports(PApplet p, String fileName) {
		List<PointFeature> features = new ArrayList<PointFeature>();

		String[] rows = p.loadStrings(fileName);
		for (String row : rows) {
			
			// hot-fix for altitude when lat lon out of place
			int i = 0;
			
			// split row by commas not in quotations
			String[] columns = row.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			
			// get location and create feature
			//System.out.println(columns[6]);
			float lat = Float.parseFloat(columns[6]);
			float lon = Float.parseFloat(columns[7]);
			
			Location loc = new Location(lat, lon);
			PointFeature point = new PointFeature(loc);
			
			// set ID to OpenFlights unique identifier
			point.setId(columns[0]);
			
			// get other fields from csv
			point.addProperty("id", columns[0]);
			point.addProperty("name", (columns[1].replace("\"", "")));
			point.putProperty("city", (columns[2].replace("\"", "")));
			point.putProperty("country", (columns[3].replace("\"", "")));
			
			// pretty sure IATA/FAA is used in routes.dat
			// get airport IATA/FAA code
			if(!columns[4].equals("\\N")) {
				point.putProperty("code", (columns[4].replace("\"", "")));
			}
			// get airport ICAO code if no IATA
			else if(!columns[5].equals("\\N")) {
				point.putProperty("code", (columns[5].replace("\"", "")));
			}
			
			point.putProperty("altitude", columns[8 + i]);
			
			features.add(point);
		}
		
		return features;		
	}
	
	/*
	 * This method is to parse a file containing airport route information.  
	 * The file and its format can be found: 
	 * http://openflights.org/data.html#route
	 * 
	 * @param p - PApplet being used
	 * @param fileName - file name or URL for data source
	 */
	public static List<ShapeFeature> parseRoutes(PApplet p, String fileName) {
		List<ShapeFeature> routes = new ArrayList<ShapeFeature>();
		
		String[] rows = p.loadStrings(fileName);
		
		for(String row : rows) {
			String[] columns = row.split(",");
			
			ShapeFeature route = new ShapeFeature(Feature.FeatureType.LINES);
			
			// set id to be OpenFlights identifier for source airport
			
			// check that both airports on route have OpenFlights Identifier
			if(!columns[3].equals("\\N") && !columns[5].equals("\\N")){
				// set "source" property to be OpenFlights identifier for source airport
				route.putProperty("source", columns[3]);
				// "destination property" -- OpenFlights identifier
				route.putProperty("destination", columns[5]);
				
				routes.add(route);
			}
		}		
		
		return routes;		
	}
}