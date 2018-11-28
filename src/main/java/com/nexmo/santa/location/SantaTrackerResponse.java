package com.nexmo.santa.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SantaTrackerResponse {
    private List<Destination> destinations;

    public List<Destination> getDestinations() {
        return destinations;
    }

    public static SantaTrackerResponse fromJson(String json) throws IOException {
        return new ObjectMapper().readValue(json, SantaTrackerResponse.class);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Destination {
        private String id;
        private Date arrival;
        private Date departure;
        private Long population;
        private long presentsDelivered;
        private String city;
        private String region;
        private Location location;

        public String getId() {
            return id;
        }

        public Date getArrival() {
            return arrival;
        }

        public Date getDeparture() {
            return departure;
        }

        public Long getPopulation() {
            return population;
        }

        public long getPresentsDelivered() {
            return presentsDelivered;
        }

        public String getCity() {
            return city;
        }

        public String getRegion() {
            return region;
        }

        public Location getLocation() {
            return location;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Location {
            private String lat;
            private String lng;

            public String getLat() {
                return lat;
            }

            public String getLng() {
                return lng;
            }
        }
    }
}
