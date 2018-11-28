package com.nexmo.santa.messaging;

import com.nexmo.santa.phone.Phone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HelpKeywordHandler implements KeywordHandler {
    private final OutgoingMessageService outgoingMessageService;

    @Autowired
    public HelpKeywordHandler(OutgoingMessageService outgoingMessageService) {
        this.outgoingMessageService = outgoingMessageService;
    }

    @Override
    public void handle(Phone phone, String text) {
        switch (phone.getStage()) {
            case INITIAL:
                outgoingMessageService.sendInitialHelp(phone);
                break;
            case COUNTRY_PROMPT:
                outgoingMessageService.sendCountryHelp(phone);
                break;
            case POSTAL_PROMPT:
                outgoingMessageService.sendPostalHelp(phone);
                break;
            case GUEST:
            case REGISTERED:
                outgoingMessageService.sendHelp(phone);
                break;
        }
    }
}
