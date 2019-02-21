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

package com.anarsultanov.airportmap.util;

import com.anarsultanov.airportmap.dto.LocationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.geo.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for parsing information.
 *
 * @author Anar Sultanov
 */

public class DataParser {

    private static final ObjectMapper mapper = new ObjectMapper();

    private DataParser() {
        throw new AssertionError();
    }

    /*
     * This method is to parse user's location
     * by using getUsersIp, parseLocationByIp methods
     *
     */
    public static PointFeature getLocationFeauture() {
        try {
            String ip = getUsersIp();
            LocationResponse locationResponse = getLocationByIp(ip);

            Location loc = new Location(locationResponse.getLatitude(), locationResponse.getLongitude());
            PointFeature point = new PointFeature(loc);

            point.putProperty("ip", locationResponse.getIp());
            point.putProperty("code", locationResponse.getCode());
            point.putProperty("city", locationResponse.getCity());
            point.putProperty("country", locationResponse.getCountry());

            return point;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // Helper method for getting user's location data by IP
    private static LocationResponse getLocationByIp(String ip) throws IOException {
        URL url = new URL("http://api.ipstack.com/" + ip + "?access_key=3742d1a1533cb23212fe911f32d5bb7d");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return mapper.readValue(in.readLine(), LocationResponse.class);
        }
    }

    // Helper method for getting user's external IP
    private static String getUsersIp() throws IOException {
        URL url = new URL("http://checkip.amazonaws.com");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return in.readLine();
        }
    }

    /*
     * This method is to parse a file containing airport information.
     * The file and its format can be found:
     * http://openflights.org/data.html#airport
     *
     */
    public static List<PointFeature> getAirportFeatures() {
        List<PointFeature> features = new ArrayList<>();

        try {
            URL url = new URL("https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String row;
                while ((row = in.readLine()) != null) {
                    // hot-fix for altitude when lat lon out of place
                    int i = 0;

                    // split row by commas not in quotations
                    String[] columns = row.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                    // get location and create feature
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
                    if (!columns[4].equals("\\N")) {
                        point.putProperty("code", (columns[4].replace("\"", "")));
                    }
                    // get airport ICAO code if no IATA
                    else if (!columns[5].equals("\\N")) {
                        point.putProperty("code", (columns[5].replace("\"", "")));
                    }

                    point.putProperty("altitude", columns[8 + i]);

                    features.add(point);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return features;
    }

    /*
     * This method is to parse a file containing airport route information.
     * The file and its format can be found:
     * http://openflights.org/data.html#route
     */
    public static List<ShapeFeature> getRouteFeatures() {
        List<ShapeFeature> routes = new ArrayList<>();
        try {
            URL url = new URL("https://raw.githubusercontent.com/jpatokal/openflights/master/data/routes.dat");
            try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
                String row;
                while ((row = in.readLine()) != null) {
                    String[] columns = row.split(",");

                    ShapeFeature route = new ShapeFeature(Feature.FeatureType.LINES);

                    // set id to be OpenFlights identifier for source airport

                    // check that both airports on route have OpenFlights Identifier
                    if (!columns[3].equals("\\N") && !columns[5].equals("\\N")) {
                        // set "source" property to be OpenFlights identifier for source airport
                        route.putProperty("source", columns[3]);
                        // "destination property" -- OpenFlights identifier
                        route.putProperty("destination", columns[5]);

                        routes.add(route);
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return routes;
    }
}