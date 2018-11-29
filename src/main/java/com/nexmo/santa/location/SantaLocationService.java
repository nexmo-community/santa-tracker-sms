package com.nexmo.santa.location;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SantaLocationService {
    private final SantaLocationRepository santaLocationRepository;
    private final DistanceCalculationService distanceCalculationService;

    @Autowired
    public SantaLocationService(SantaLocationRepository santaLocationRepository,
            DistanceCalculationService distanceCalculationService
    ) {
        this.santaLocationRepository = santaLocationRepository;
        this.distanceCalculationService = distanceCalculationService;
    }

    public Optional<SantaLocation> getSantaNextLocation(Date date) {
        return santaLocationRepository.findFirstByArrivalAfter(date);
    }

    public Optional<SantaLocation> getSantaCurrentLocation(Date date) {
        return santaLocationRepository.findFirstByArrivalBeforeAndDepartureAfter(date, date);
    }

    public Optional<SantaLocation> getSantaNextLocation() {
        return getSantaNextLocation(getDate());
    }

    public Optional<SantaLocation> getSantaCurrentLocation() {
        return getSantaCurrentLocation(getDate());
    }

    public SantaLocation getNearestLocation(String lat, String lng) {
        List<SantaLocation> locations = santaLocationRepository.findAll();

        double minimumDistance = Double.MAX_VALUE;
        SantaLocation nearest = null;

        for (SantaLocation location : locations) {
            double lat1 = Double.parseDouble(location.getLatitude());
            double lng1 = Double.parseDouble(location.getLongitude());
            double lat2 = Double.parseDouble(lat);
            double lng2 = Double.parseDouble(lng);

            double distance = distanceCalculationService.getDistanceInMiles(lat1, lng1, lat2, lng2);
            if (distance < minimumDistance) {
                nearest = location;
                minimumDistance = distance;
            }
        }

        return nearest;
    }

    private Date getDate() {
        Date now = new Date();

        // TODO: Remove this logic once Christmas Eve hits
        // Since the timezones are keyed for the 24th and 25th of December, we can do a bit of
        // time travelling to test things out.
        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(now);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);

        // We're going to skip a few locations doing this but if it's after 10am, assume that it's Christmas eve
        // Otherwise it's Christmas Morning.
        System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
        if (calendar.get(Calendar.HOUR_OF_DAY) > 10 && calendar.get(Calendar.MINUTE) > 0) {
            calendar.set(Calendar.DATE, 24);
        } else {
            calendar.set(Calendar.DATE, 25);
        }
        System.out.println(calendar.getTime());
        return calendar.getTime();
    }
}
