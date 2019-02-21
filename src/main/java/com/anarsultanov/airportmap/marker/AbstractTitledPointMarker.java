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
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.Optional;

abstract class AbstractTitledPointMarker extends SimplePointMarker {

    AbstractTitledPointMarker(Feature feature) {
        super(((PointFeature) feature).getLocation(), feature.getProperties());
    }

    public abstract void draw(PGraphics pg, float x, float y);

    void showTitle(String name, PGraphics pg, float x, float y, float size) {
        String title = name + " (" + getCode().orElse("-") + ") ";
        String place = getCity() + ", " + getCountry() + " ";

        pg.pushStyle();

        pg.fill(255, 255, 255);
        pg.textSize(12);
        pg.rectMode(PConstants.CORNER);
        pg.rect(x, y - size - 39, Math.max(pg.textWidth(title), pg.textWidth(place)) + 6, 39);
        pg.fill(0, 0, 0);
        pg.textAlign(PConstants.LEFT, PConstants.TOP);
        pg.text(title, x + 3, y - size - 33);
        pg.text(place, x + 3, y - size - 18);

        pg.popStyle();
    }

    private String getCity() {
        return getStringProperty("city");
    }

    private String getCountry() {
        return getStringProperty("country");
    }

    Optional<String> getCode() {
        return Optional.ofNullable(getStringProperty("code"));
    }
}
