package com.nexmo.santa.messaging;

import com.nexmo.santa.location.PhoneLocationLookupService;
import com.nexmo.santa.phone.Phone;
import com.nexmo.santa.phone.PhoneRepository;
import org.springframework.stereotype.Component;

@Component
public class LocationKeywordHandler implements KeywordHandler {
    private final PhoneRepository phoneRepository;
    private final OutgoingMessageService outgoingMessageService;
    private final PhoneLocationLookupService phoneLocationLookupService;

    public LocationKeywordHandler(PhoneRepository phoneRepository,
            OutgoingMessageService outgoingMessageService,
            PhoneLocationLookupService phoneLocationLookupService
    ) {
        this.phoneRepository = phoneRepository;
        this.outgoingMessageService = outgoingMessageService;
        this.phoneLocationLookupService = phoneLocationLookupService;
    }

    @Override
    public void handle(Phone phone, String text) {
        if (phone.getStage() == Phone.Stage.REGISTERED || phone.getStage() == Phone.Stage.GUEST) {
            phone.setStage(Phone.Stage.COUNTRY_PROMPT);
            phone.setCountryCode(phoneLocationLookupService.lookupCountryCode(phone.getNumber()));
            phoneRepository.save(phone);
            outgoingMessageService.sendLocationYes(phone);
        }
    }
}
