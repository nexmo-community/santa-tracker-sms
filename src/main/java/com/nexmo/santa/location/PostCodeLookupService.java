package com.nexmo.santa.location;

import com.nexmo.santa.LocationLookupException;
import com.nexmo.santa.phone.Phone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Service
public class PostCodeLookupService {

    private static final String GEONAMES_URL = "http://api.geonames.org/postalCodeSearchJSON?formatted=true&postalcode=%s&countryCode=%s&maxRows=1&username=%s";

    @Value("${geonames.user}")
    private String user;
    private final LocationRepository locationRepository;

    @Autowired
    public PostCodeLookupService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Optional<Location> lookup(Phone phone, String postalCode) {
        return getLocation(phone.getCountryCode(), postalCode.replace(" ", "").toUpperCase());
    }

    private Optional<Location> getLocation(String country, String postalCode) {
        Optional<Location> locationOptional = locationRepository.findByPostalCode(postalCode);
        if (locationOptional.isPresent()) {
            return locationOptional;
        }

        LocationResponse response = getLocationResponse(country, postalCode);
        if (response.getPostalCodes().isEmpty()) {
            return Optional.empty();
        }

        Location newLocation = buildLocation(response, postalCode);
        return Optional.of(locationRepository.save(newLocation));
    }

    private LocationResponse getLocationResponse(String country, String postalCode) {
        try {
            return LocationResponse.fromJson(getGeonamesResponse(country, postalCode).body().toString());
        } catch (IOException e) {
            throw new LocationLookupException(e);
        }
    }

    private Location buildLocation(LocationResponse response, String postalCode) {
        LocationResponse.LocationInfo info = response.getPostalCodes().get(0);

        Location newLocation = new Location();
        newLocation.setLatitude(info.getLat());
        newLocation.setLongitude(info.getLng());
        newLocation.setPlaceName(info.getPlaceName());
        newLocation.setPostalCode(postalCode);

        return newLocation;
    }

    private HttpResponse getGeonamesResponse(String country, String postalCode) {
        HttpClient client = HttpClient.newHttpClient();
        String uri = String.format(GEONAMES_URL, postalCode, country, user);
        HttpRequest request = HttpRequest.newBuilder(URI.create(uri)).GET().build();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new LocationLookupException(e);
        }
    }
}
