package com.nexmo.santa.messaging;

import com.nexmo.santa.phone.Phone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WhereKeywordHandler implements KeywordHandler {
    private final OutgoingMessageService outgoingMessageService;

    @Autowired
    public WhereKeywordHandler(OutgoingMessageService outgoingMessageService) {
        this.outgoingMessageService = outgoingMessageService;
    }

    @Override
    public void handle(Phone phone, String text) {
        outgoingMessageService.sendSanta(phone);
    }
}
