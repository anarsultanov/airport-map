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

import java.util.Optional;

/**
 * A class to represent AirportMarkers on a world map.
 *
 * @author Anar Sultanov
 */

public class AirportMarker extends SimplePointMarker {

    public AirportMarker(Location location) {
        super(location);
    }

    public AirportMarker(Location location, java.util.HashMap<java.lang.String, java.lang.Object> properties) {
        super(location, properties);
    }

    private static final float MARKER_SIZE = 5;

    public AirportMarker(Feature city) {
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
        String code = getCode().orElse("-");
        pg.fill(40);
        pg.textSize(8);
        pg.text(code, x - (pg.textWidth(code) / 2), y - MARKER_SIZE);
        pg.fill(150, 30, 30);
        pg.ellipse(x, y, MARKER_SIZE, MARKER_SIZE);
        pg.popStyle();
    }

    public void showTitle(PGraphics pg, float x, float y) {

        String name = getName() + " (" + getCode() + ") ";
        String place = getCity() + ", " + getCountry() + " ";

        pg.pushStyle();

        pg.fill(255, 255, 255);
        pg.textSize(12);
        pg.rectMode(PConstants.CORNER);
        pg.rect(x, y - MARKER_SIZE - 39, Math.max(pg.textWidth(name), pg.textWidth(place)) + 6, 39);
        pg.fill(0, 0, 0);
        pg.textAlign(PConstants.LEFT, PConstants.TOP);
        pg.text(name, x + 3, y - MARKER_SIZE - 33);
        pg.text(place, x + 3, y - MARKER_SIZE - 18);

        pg.popStyle();
    }

    private String getName() {
        return getStringProperty("name");
    }

    private String getCity() {
        return getStringProperty("city");
    }

    private String getCountry() {
        return getStringProperty("country");
    }

    private Optional<String> getCode() {
        return Optional.ofNullable(getStringProperty("code"));
    }
}