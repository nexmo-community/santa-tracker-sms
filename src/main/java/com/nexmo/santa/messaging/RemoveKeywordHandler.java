package com.nexmo.santa.messaging;

import com.nexmo.santa.phone.Phone;
import com.nexmo.santa.phone.PhoneRepository;
import org.springframework.stereotype.Component;

@Component
public class RemoveKeywordHandler implements KeywordHandler {
    private final PhoneRepository phoneRepository;
    private final OutgoingMessageService outgoingMessageService;

    public RemoveKeywordHandler(PhoneRepository phoneRepository, OutgoingMessageService outgoingMessageService) {
        this.phoneRepository = phoneRepository;
        this.outgoingMessageService = outgoingMessageService;
    }

    @Override
    public void handle(Phone phone, String text) {
        phoneRepository.delete(phone);
        outgoingMessageService.sendRemove(phone);
    }
}
