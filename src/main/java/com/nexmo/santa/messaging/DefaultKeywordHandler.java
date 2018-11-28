package com.nexmo.santa.messaging;

import com.nexmo.santa.location.Location;
import com.nexmo.santa.location.PhoneLocationLookupService;
import com.nexmo.santa.location.PostCodeLookupService;
import com.nexmo.santa.phone.Phone;
import com.nexmo.santa.phone.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultKeywordHandler implements KeywordHandler {
    private final PhoneRepository phoneRepository;
    private final OutgoingMessageService outgoingMessageService;
    private final PostCodeLookupService postCodeLookupService;
    private final PhoneLocationLookupService phoneLocationLookupService;

    @Autowired
    public DefaultKeywordHandler(PhoneRepository phoneRepository,
            OutgoingMessageService outgoingMessageService,
            PostCodeLookupService postCodeLookupService,
            PhoneLocationLookupService phoneLocationLookupService
    ) {
        this.phoneRepository = phoneRepository;
        this.outgoingMessageService = outgoingMessageService;
        this.phoneLocationLookupService = phoneLocationLookupService;
        this.postCodeLookupService = postCodeLookupService;
    }

    @Override
    public void handle(Phone phone, String text) {
        // User is either new (and doesn't have a stage) or already knows about our system.
        if (phone.getStage() == null) {
            handleNewUser(phone);
        } else {
            handleExistingUser(phone, text);
        }
    }

    private void handleNewUser(Phone phone) {
        outgoingMessageService.sendWelcome(phone);
        phone.setStage(Phone.Stage.INITIAL);
        phone.setCountryCode(phoneLocationLookupService.lookupCountryCode(phone.getNumber()));
        phoneRepository.save(phone);
    }

    private void handleExistingUser(Phone phone, String text) {
        switch (phone.getStage()) {
            case COUNTRY_PROMPT:
                handleCountryPrompt(phone, text);
                break;
            case POSTAL_PROMPT:
                handlePostalPrompt(phone, text);
                break;
            default:
                outgoingMessageService.sendUnknown(phone);
        }
    }

    private void handleCountryPrompt(Phone phone, String text) {
        phone.setCountryCode(text.toUpperCase().substring(0, 2));
        phone.setStage(Phone.Stage.POSTAL_PROMPT);
        phoneRepository.save(phone);
        outgoingMessageService.sendLocationAcknowledge(phone);
    }

    private void handlePostalPrompt(Phone phone, String text) {
        Optional<Location> locationOptional = postCodeLookupService.lookup(phone, text);
        if (locationOptional.isPresent()) {
            phone.setLocation(locationOptional.get());
            phone.setStage(Phone.Stage.REGISTERED);
            phoneRepository.save(phone);
            outgoingMessageService.sendLocationFinal(phone);
        } else {
            outgoingMessageService.sendUnknownLocation(phone);
        }
    }
}
