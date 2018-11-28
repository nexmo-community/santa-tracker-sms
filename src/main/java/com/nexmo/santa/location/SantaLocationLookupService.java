package com.nexmo.santa.location;

import com.nexmo.santa.SantaLookupException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Calendar;

@Service
public class SantaLocationLookupService {
    private static final String TRACKER_URL = "https://storage.googleapis.com/santa/route-v1/santa_en.json";

    private final SantaLocationRepository santaLocationRepository;

    public SantaLocationLookupService(SantaLocationRepository santaLocationRepository) {
        this.santaLocationRepository = santaLocationRepository;
    }

    public void seedLocations() {
        if (santaLocationRepository.findAll().isEmpty()) {
            SantaTrackerResponse response = getSantaTrackerResponse();
            response.getDestinations().forEach(destination -> {
                SantaLocation location = new SantaLocation();
                location.setName(destination.getCity() + ", " + destination.getRegion());
                location.setLongitude(destination.getLocation().getLng());
                location.setLatitude(destination.getLocation().getLat());

                // Bump each date by a year
                Calendar arrival = Calendar.getInstance();
                arrival.setTime(destination.getArrival());
                arrival.add(Calendar.YEAR, 1);
                location.setArrival(arrival.getTime());

                Calendar departure = Calendar.getInstance();
                departure.setTime(destination.getDeparture());
                departure.add(Calendar.YEAR, 1);
                location.setDeparture(departure.getTime());

                santaLocationRepository.save(location);
            });
        }
    }

    private SantaTrackerResponse getSantaTrackerResponse() {
        try {
            return SantaTrackerResponse.fromJson(getTrackerHttpResponse().body().toString());
        } catch (IOException e) {
            throw new SantaLookupException(e);
        }
    }

    private HttpResponse getTrackerHttpResponse() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create(TRACKER_URL)).GET().build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new SantaLookupException(e);
        }
    }
}
