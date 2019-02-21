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

package com.anarsultanov.airportmap.marker;


import de.fhpotsdam.unfolding.data.Feature;
import processing.core.PGraphics;

/**
 * A class to represent user's location on a world map.
 *
 * @author Anar Sultanov
 */

public class UserMarker extends AbstractTitledPointMarker {

    private static final float MARKER_SIZE = 7;

    public UserMarker(Feature city) {
        super(city);
    }

    @Override
    public void draw(PGraphics pg, float x, float y) {
        if (!hidden) {
            drawMarker(pg, x, y);
            if (selected) {
                showTitle("IP: " + getIp(), pg, x, y, MARKER_SIZE);
            }
        }
    }

    private void drawMarker(PGraphics pg, float x, float y) {
        pg.pushStyle();
        pg.fill(255, 0, 0);
        pg.ellipse(x, y, MARKER_SIZE, MARKER_SIZE);
        pg.popStyle();
    }

    private String getIp() {
        return getStringProperty("ip");
    }
}