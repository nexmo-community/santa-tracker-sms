package com.nexmo.santa.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationResponse {
    private List<LocationInfo> postalCodes;

    public List<LocationInfo> getPostalCodes() {
        return postalCodes;
    }

    public static LocationResponse fromJson(String json) throws IOException {
        return new ObjectMapper().readValue(json, LocationResponse.class);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocationInfo {
        String placeName;
        String lat;
        String lng;

        public String getPlaceName() {
            return placeName;
        }

        public String getLat() {
            return lat;
        }

        public String getLng() {
            return lng;
        }
    }
}
