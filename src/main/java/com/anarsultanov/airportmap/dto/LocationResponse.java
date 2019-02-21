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

package com.anarsultanov.airportmap.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationResponse {

    private double latitude;
    private double longitude;
    private String ip;
    private String code;
    private String country;
    private String city;

    public LocationResponse(@JsonProperty("latitude") double latitude, @JsonProperty("longitude") double longitude,
                            @JsonProperty("ip") String ip, @JsonProperty("country_code") String code,
                            @JsonProperty("country_name") String country, @JsonProperty("city") String city) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.ip = ip;
        this.code = code;
        this.country = country;
        this.city = city;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getIp() {
        return ip;
    }

    public String getCode() {
        return code;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationResponse that = (LocationResponse) o;
        return Double.compare(that.latitude, latitude) == 0 &&
                Double.compare(that.longitude, longitude) == 0 &&
                Objects.equals(ip, that.ip) &&
                Objects.equals(code, that.code) &&
                Objects.equals(country, that.country) &&
                Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, ip, code, country, city);
    }

    @Override
    public String toString() {
        return "LocationResponse{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", ip='" + ip + '\'' +
                ", code='" + code + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
