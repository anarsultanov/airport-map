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

import com.anarsultanov.airportmap.marker.AirportMarker;
import com.anarsultanov.airportmap.marker.RouteMarker;
import com.anarsultanov.airportmap.marker.UserMarker;
import com.anarsultanov.airportmap.util.DataParser;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap.OpenStreetMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * An applet that shows airports (and routes) on a world map.
 *
 * @author Anar Sultanov
 */

public class AirportMap extends PApplet {

    //Map
    private UnfoldingMap map;
    //Markers
    private List<Marker> airportList;
    private List<Marker> routeList;
    private UserMarker userMarker;
    //Last selected and last clicked markers
    private Marker lastSelected;
    private Marker lastClicked;
    //True when click on an empty space
    private boolean emptySpaceClick;

    public static void main(String... args) {
        AirportMap pt = new AirportMap();
        PApplet.runSketch(new String[]{"AirportMap"}, pt);
    }

    public void setup() {
        // setting up PApplet
        size(1920, 1080, OPENGL);
        //get user's location and create marker
        PointFeature userLocation = DataParser.parseLocation();
        userMarker = new UserMarker(userLocation);
        // setting up map and default events
        map = new UnfoldingMap(this, 0, 0, 1920, 1080, new OpenStreetMapProvider());
        MapUtils.createDefaultEventDispatcher(this, map);
        //set zoom levels and pan to user's location
        map.setZoomRange(6, 8);
        map.zoomAndPanTo(6, userLocation.getLocation());

        // get features from airport data
        //URLs for airports and routes databases
        String airportsDataURL = "https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat";
        List<PointFeature> features = DataParser.parseAirports(this, airportsDataURL);

        // list for markers, hashmap for quicker access when matching with routes
        airportList = new ArrayList<Marker>();
        HashMap<Integer, Location> airports = new HashMap<Integer, Location>();

        // create markers from features
        for (PointFeature feature : features) {
            AirportMarker m = new AirportMarker(feature);

            airportList.add(m);

            // put airport in hashmap with OpenFlights unique id for key
            airports.put(Integer.parseInt(feature.getId()), feature.getLocation());

        }


        // parse route data
        String routesDataURL = "https://raw.githubusercontent.com/jpatokal/openflights/master/data/routes.dat";
        List<ShapeFeature> routes = DataParser.parseRoutes(this, routesDataURL);
        routeList = new ArrayList<Marker>();
        for (ShapeFeature route : routes) {

            // get source and destination airportIds
            int source = Integer.parseInt((String) route.getProperty("source"));
            int dest = Integer.parseInt((String) route.getProperty("destination"));

            // get locations for airports on route
            if (airports.containsKey(source) && airports.containsKey(dest)) {
                route.addLocation(airports.get(source));
                route.addLocation(airports.get(dest));
            }

            RouteMarker sl = new RouteMarker(route.getLocations(), route.getProperties());

            sl.setHidden(true);

            routeList.add(sl);
        }
        // add markers to the map
        map.addMarkers(routeList);
        map.addMarkers(airportList);
        map.addMarker(userMarker);

    }

    public void draw() {
        background(0);
        map.draw();
    }

    /**
     * Event handler that gets called automatically when the
     * mouse moves.
     */
    @Override
    public void mouseMoved() {
        // clear the last selection
        if (lastSelected != null) {
            lastSelected.setSelected(false);
            lastSelected = null;

        }
        selectMarkerIfHover(userMarker);
        selectMarkerIfHover(airportList);
        //loop();
    }

    // if there is a marker selected
    private void selectMarkerIfHover(Marker marker) {
        // Abort if there's already a marker selected
        if (lastSelected != null) {
            return;
        }
        if (marker.isInside(map, mouseX, mouseY)) {
            lastSelected = marker;
            marker.setSelected(true);
            return;

        }
    }

    //Overloading previous method
    private void selectMarkerIfHover(List<Marker> markers) {
        // Abort if there's already a marker selected
        if (lastSelected != null) {
            return;
        }

        for (Marker m : markers) {
            if (m.isInside(map, mouseX, mouseY)) {
                lastSelected = m;
                m.setSelected(true);
                return;
            }
        }
    }

    /**
     * The event handler for mouse clicks
     */
    @Override
    public void mouseClicked() {
        // If click on user's location marker
        if (userMarker.isInside(map, mouseX, mouseY)) {
            unhideAirports();
            lastClicked = userMarker;
            Location loc = userMarker.getLocation();
            for (Marker mk : airportList) {
                if (mk.getDistanceTo(loc) > 100) {
                    mk.setHidden(true);
                }
            }
        }
        // if click on other place
        else {
            // If no clicked markers
            if (lastClicked == null) {
                for (Marker mk : airportList) {
                    if (mk.isInside(map, mouseX, mouseY)) {
                        airportClicked(mk);
                        emptySpaceClick = false;
                        return;
                    }
                }
                //If some marker clicked and new click on visible airport marker
            } else if (lastClicked != null && !emptySpaceClick) {
                for (Marker mk : airportList) {
                    if (mk.isInside(map, mouseX, mouseY) && !mk.isHidden()) {
                        unhideAirports();
                        airportClicked(mk);
                        //airport marker found
                        emptySpaceClick = false;
                        return;
                    }//airport marker not found then click was on empty space
                    emptySpaceClick = true;
                }
            } // If some marker clicked and click on empty space
            if (lastClicked != null && emptySpaceClick) {
                unhideAirports();
                lastClicked = null;
                emptySpaceClick = false;
            }
        }
    }

    // Helper method that handles click on airport marker
    private void airportClicked(Marker mk) {
        lastClicked = (AirportMarker) mk;
        //Hide all airports except last clicked
        for (Marker airportMarker : airportList) {
            if (airportMarker != lastClicked) {
                airportMarker.setHidden(true);
            }
        }
        int code = Integer.parseInt((String) lastClicked.getProperty("id"));
        // Show routes from last clicked airport
        for (Marker routeMarker : routeList) {
            int source = Integer.parseInt((String) routeMarker.getProperty("source"));
            int dest = Integer.parseInt((String) routeMarker.getProperty("destination"));
            if (source == code) {
                routeMarker.setHidden(false);
                // Show destination airports
                for (Marker destMarker : airportList) {
                    int destCode = Integer.parseInt((String) destMarker.getProperty("id"));
                    if (dest == destCode) {
                        destMarker.setHidden(false);
                    }
                }
            }
        }
    }

    //Show all airports and hide all routes
    private void unhideAirports() {
        for (Marker marker : airportList) {
            marker.setHidden(false);
        }

        for (Marker marker : routeList) {
            marker.setHidden(true);
        }
    }

}
