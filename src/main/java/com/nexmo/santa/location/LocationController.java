package com.nexmo.santa.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class LocationController {

    private final SantaLocationService santaLocationService;
    private final DistanceCalculationService distanceCalculationService;

    @Autowired
    public LocationController(SantaLocationService santaLocationService,
            DistanceCalculationService distanceCalculationService
    ) {
        this.santaLocationService = santaLocationService;
        this.distanceCalculationService = distanceCalculationService;
    }

    @GetMapping("/location/{timestamp}")
    public Map<String, SantaLocation> location(@PathVariable("timestamp") String timestamp) throws JsonProcessingException {
        Map<String, SantaLocation> locations = new HashMap<>();
        Date date = new Date(Long.parseLong(timestamp));

        Optional<SantaLocation> current = santaLocationService.getSantaCurrentLocation(date);
        Optional<SantaLocation> next = santaLocationService.getSantaNextLocation(date);

        current.ifPresent(location -> locations.put("current", location));
        next.ifPresent(location -> locations.put("next", location));

        return locations;
    }

    @GetMapping("/nearest")
    public SantaLocation nearest(@RequestParam("lat") String lat, @RequestParam("lng") String lng) {
        return santaLocationService.getNearestLocation(lat, lng);
    }

    @GetMapping("/heading")
    public Map<String, Object> heading(@RequestParam("lat") String lat, @RequestParam("lng") String lng
    ) throws JsonProcessingException {
        SantaLocation nearestLocation = santaLocationService.getNearestLocation(lat, lng);

        Optional<SantaLocation> current = santaLocationService.getSantaCurrentLocation();
        Optional<SantaLocation> next = santaLocationService.getSantaNextLocation();

        Map<String, Object> response = new LinkedHashMap<>();

        SantaLocation currentOrNext = current.orElseGet(() -> next.orElse(null));
        if (currentOrNext != null) {
            if (currentOrNext.getId() > nearestLocation.getId()) {
                response.put("direction", "away");
            } else if (currentOrNext.getId().equals(nearestLocation.getId())) {
                response.put("direction", "at");
            } else {
                response.put("direction", "towards");
            }

            response.put("miles",
                    distanceCalculationService.getDistanceInMiles(Double.valueOf(currentOrNext.getLatitude()),
                            Double.valueOf(currentOrNext.getLongitude()),
                            Double.valueOf(lat),
                            Double.valueOf(lng)
                    )
            );
        }

        response.put("nearest", nearestLocation);
        current.ifPresent(santaLocation -> response.put("current", santaLocation));
        next.ifPresent(santaLocation -> response.put("next", santaLocation));

        return response;
    }
}
