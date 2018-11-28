package com.nexmo.santa.messaging;

import com.nexmo.santa.phone.Phone;
import com.nexmo.santa.phone.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YesKeywordHandler implements KeywordHandler {
    private final PhoneRepository phoneRepository;
    private final OutgoingMessageService outgoingMessageService;

    @Autowired
    public YesKeywordHandler(PhoneRepository phoneRepository, OutgoingMessageService outgoingMessageService) {
        this.phoneRepository = phoneRepository;
        this.outgoingMessageService = outgoingMessageService;
    }

    @Override
    public void handle(Phone phone, String text) {
        if (phone.getStage() == Phone.Stage.INITIAL) {
            handlePromptForCountryCode(phone);
        } else if (phone.getStage() == Phone.Stage.COUNTRY_PROMPT) {
            handlePromptForPostalCode(phone);
        } else {
            outgoingMessageService.sendUnknown(phone);
        }
    }

    private void handlePromptForCountryCode(Phone phone) {
        phone.setStage(Phone.Stage.COUNTRY_PROMPT);
        phoneRepository.save(phone);
        outgoingMessageService.sendLocationYes(phone);
    }

    private void handlePromptForPostalCode(Phone phone) {
        phone.setStage(Phone.Stage.POSTAL_PROMPT);
        phoneRepository.save(phone);
        outgoingMessageService.sendLocationAcknowledge(phone);
    }
}
