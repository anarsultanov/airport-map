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


import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * A class to represent user's location on a world map.
 *
 * @author Anar Sultanov
 */

public class UserMarker extends SimplePointMarker {

    public UserMarker(Location location) {
        super(location);
    }

    public UserMarker(Location location, java.util.HashMap<java.lang.String, java.lang.Object> properties) {
        super(location, properties);
    }

    private static final float MARKER_SIZE = 7;

    public UserMarker(Feature city) {
        super(((PointFeature) city).getLocation(), city.getProperties());

    }

    public void draw(PGraphics pg, float x, float y) {
        if (!hidden) {
            drawMarker(pg, x, y);
            if (selected) {
                showTitle(pg, x, y);
            }
        }
    }

    public void drawMarker(PGraphics pg, float x, float y) {
        pg.pushStyle();
        pg.fill(255, 0, 0);
        pg.ellipse(x, y, MARKER_SIZE, MARKER_SIZE);
        pg.popStyle();


    }

    public void showTitle(PGraphics pg, float x, float y) {

        String ip = "IP: " + getIp() + " (" + getCode() + ") ";
        String place = getCity() + ", " + getCountry() + " ";

        pg.pushStyle();

        pg.fill(255, 255, 255);
        pg.textSize(12);
        pg.rectMode(PConstants.CORNER);
        pg.rect(x, y - MARKER_SIZE - 39, Math.max(pg.textWidth(ip), pg.textWidth(place)) + 6, 39);
        pg.fill(0, 0, 0);
        pg.textAlign(PConstants.LEFT, PConstants.TOP);
        pg.text(ip, x + 3, y - MARKER_SIZE - 33);
        pg.text(place, x + 3, y - MARKER_SIZE - 18);

        pg.popStyle();
    }

    private String getIp() {
        return getStringProperty("ip");
    }

    private String getCode() {
        return getStringProperty("code");
    }

    private String getCity() {
        return getStringProperty("city");
    }

    private String getCountry() {
        return getStringProperty("country");
    }

}