package com.nexmo.santa.messaging;

import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoClientException;
import com.nexmo.client.sms.SmsClient;
import com.nexmo.client.sms.messages.TextMessage;
import com.nexmo.santa.OutgoingMessageException;
import com.nexmo.santa.location.DistanceCalculationService;
import com.nexmo.santa.location.Location;
import com.nexmo.santa.location.SantaLocation;
import com.nexmo.santa.location.SantaLocationService;
import com.nexmo.santa.phone.Phone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class OutgoingMessageService {
    private static final String BASIC_SANTA_LOCATION = "Santa is handing out presents in %s. Then he'll be on his way to %s.";
    private static final String BASIC_SANTA_LOCATION_TRAVELLING = "Santa is currently flying to %s";

    private static final String SANTA_LOCATION = "Santa is handing out presents in %s. Then he'll be on his way to %s. He's roughly %s from you.";
    private static final String SANTA_LOCATION_TRAVELLING = "Santa is currently flying to %s. That's roughly %s from you.";

    private static final String CANT_LOCATE_SANTA = "I'm not sure where Santa is right now!";

    private static final String LOCATION_PROMPT = "Want to provide us with your postal code for distance information? Reply YES or NO.";
    private static final String LOCATION_NO = "No problem! Feel free to continue to ask WHERE Santa is. Reply LOCATION if you change your mind.";
    private static final String LOCATION_YES = "Great! I see you're messaging from the %s. Reply YES, a 2-character country code, or CANCEL if you've changed your mind.";
    private static final String LOCATION_NULL_YES = "Great! First, tell me your 2-character country code or CANCEL if you've changed your mind.";
    private static final String LOCATION_ACKNOWLEDGE = "Awesome, %s it is. What's your postal code? Reply CANCEL if you've changed your mind.";
    private static final String LOCATION_FINAL = "We've got you down in %s. Reply REMOVE and we'll remove you from the list, or reply WHERE to find out where Santa is.";
    private static final String LOCATION_UNKNOWN_MESSAGE = "I'm not quite sure where that is. Try again or reply CANCEL if you've changed your mind.";

    private static final String REMOVE_MESSAGE = "Your number has been removed. Feel free to reach out again if you want to track Santa.";

    private static final String UNKNOWN_MESSAGE = "I'm not sure what you're asking. Reply WHERE to find Santa, REMOVE to be removed from our list, or HELP to see where you left off.";
    private static final String HELP_MESSAGE = "Reply WHERE to get Santa's location, LOCATION to add/update your current location, or REMOVE to remove yourself from our list.";

    private final SmsClient smsClient;

    private final SantaLocationService santaLocationService;
    private final DistanceCalculationService distanceCalculationService;

    @Autowired
    public OutgoingMessageService(NexmoClient nexmoClient,
            SantaLocationService santaLocationService,
            DistanceCalculationService distanceCalculationService
    ) {
        this.smsClient = nexmoClient.getSmsClient();
        this.santaLocationService = santaLocationService;
        this.distanceCalculationService = distanceCalculationService;
    }

    void sendWelcome(Phone phone) {
        String message = buildBasicLocationMessage() + " " + LOCATION_PROMPT;
        sendMessage(phone, message);
    }

    void sendRemove(Phone phone) {
        sendMessage(phone, REMOVE_MESSAGE);
    }

    void sendUnknown(Phone phone) {
        sendMessage(phone, UNKNOWN_MESSAGE);
    }

    void sendLocationNo(Phone phone) {
        sendMessage(phone, LOCATION_NO);
    }

    void sendUnknownLocation(Phone phone) {
        sendMessage(phone, LOCATION_UNKNOWN_MESSAGE);
    }

    void sendLocationFinal(Phone phone) {
        sendMessage(phone, String.format(LOCATION_FINAL, phone.getLocation().getPlaceName()));
    }

    void sendLocationAcknowledge(Phone phone) {
        sendMessage(phone, String.format(LOCATION_ACKNOWLEDGE, phone.getCountryCode()));
    }

    void sendLocationYes(Phone phone) {
        String message = (phone.getCountryCode() == null)
                ? LOCATION_NULL_YES
                : String.format(LOCATION_YES, phone.getCountryCode());
        sendMessage(phone, message);
    }

    void sendSanta(Phone phone) {
        String message;
        if (phone.getStage() == Phone.Stage.REGISTERED) {
            message = buildSantaLocationMessage(phone.getLocation());
        } else {
            message = buildBasicLocationMessage();
        }
        sendMessage(phone, message);
    }


    void sendInitialHelp(Phone phone) {
        sendMessage(phone, LOCATION_PROMPT);
    }

    void sendCountryHelp(Phone phone) {
        sendLocationYes(phone);
    }

    void sendPostalHelp(Phone phone) {
        sendLocationAcknowledge(phone);
    }

    void sendHelp(Phone phone) {
        sendMessage(phone, HELP_MESSAGE);
    }

    private void sendMessage(Phone phone, String message) {
        TextMessage text = new TextMessage(phone.getNexmoNumber(), phone.getNumber(), message);
        try {
            smsClient.submitMessage(text);
        } catch (IOException | NexmoClientException e) {
            throw new OutgoingMessageException(e);
        }
    }

    private String buildBasicLocationMessage() {
        Optional<SantaLocation> nextLocation = santaLocationService.getSantaNextLocation();
        if (nextLocation.isEmpty()) {
            return CANT_LOCATE_SANTA;
        }

        Optional<SantaLocation> currentLocation = santaLocationService.getSantaCurrentLocation();
        if (currentLocation.isPresent()) {
            return String.format(BASIC_SANTA_LOCATION, currentLocation.get().getName(), nextLocation.get().getName());
        }

        return String.format(BASIC_SANTA_LOCATION_TRAVELLING, nextLocation.get().getName());
    }

    private String buildSantaLocationMessage(Location location) {
        Optional<SantaLocation> nextLocation = santaLocationService.getSantaNextLocation();
        if (nextLocation.isEmpty()) {
            return CANT_LOCATE_SANTA;
        }

        Optional<SantaLocation> currentLocation = santaLocationService.getSantaCurrentLocation();
        if (currentLocation.isPresent()) {
            return String.format(
                    SANTA_LOCATION,
                    currentLocation.get().getName(),
                    nextLocation.get().getName(),
                    buildDistanceString(currentLocation.get(), location)
            );
        }

        return String.format(
                SANTA_LOCATION_TRAVELLING,
                nextLocation.get().getName(),
                buildDistanceString(nextLocation.get(), location)
        );
    }

    private String buildDistanceString(SantaLocation santa, Location user) {
        return (int) Math.floor(distanceCalculationService.getDistanceInMiles(santa, user)) + "mi";
    }
}
