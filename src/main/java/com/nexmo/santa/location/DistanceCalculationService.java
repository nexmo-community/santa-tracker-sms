package com.nexmo.santa.location;

import org.springframework.stereotype.Service;

@Service
public class DistanceCalculationService {
    public double getDistanceInMiles(SantaLocation santa, Location user) {
        double lng1 = Double.parseDouble(santa.getLongitude());
        double lng2 = Double.parseDouble(user.getLongitude());
        double lat1 = Double.parseDouble(santa.getLatitude());
        double lat2 = Double.parseDouble(user.getLatitude());

        return getDistanceInMiles(lat1, lng1, lat2, lng2);
    }

    public double getDistanceInMiles(double lat1, double lng1, double lat2, double lng2) {
        double theta = lng1 - lng2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;

        return dist;
    }
}
