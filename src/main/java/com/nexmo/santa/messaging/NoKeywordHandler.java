package com.nexmo.santa.messaging;

import com.nexmo.santa.phone.Phone;
import com.nexmo.santa.phone.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NoKeywordHandler implements KeywordHandler {
    private final OutgoingMessageService outgoingMessageService;
    private final PhoneRepository phoneRepository;

    @Autowired
    public NoKeywordHandler(OutgoingMessageService outgoingMessageService, PhoneRepository phoneRepository) {
        this.outgoingMessageService = outgoingMessageService;
        this.phoneRepository = phoneRepository;
    }

    @Override
    public void handle(Phone phone, String text) {
        if (phone.getStage() == Phone.Stage.INITIAL) {
            outgoingMessageService.sendLocationNo(phone);
            phone.setStage(Phone.Stage.GUEST);
            phoneRepository.save(phone);

        } else {
            outgoingMessageService.sendUnknown(phone);
        }
    }
}
