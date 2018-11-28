package com.nexmo.santa.messaging;

import com.nexmo.santa.phone.Phone;
import com.nexmo.santa.phone.PhoneRepository;
import org.springframework.stereotype.Component;

@Component
public class CancelKeywordHandler implements KeywordHandler {
    private final PhoneRepository phoneRepository;
    private final OutgoingMessageService outgoingMessageService;

    public CancelKeywordHandler(PhoneRepository phoneRepository, OutgoingMessageService outgoingMessageService) {
        this.phoneRepository = phoneRepository;
        this.outgoingMessageService = outgoingMessageService;
    }

    @Override
    public void handle(Phone phone, String text) {
        if (phone.getStage() == Phone.Stage.COUNTRY_PROMPT || phone.getStage() == Phone.Stage.POSTAL_PROMPT) {
            phone.setStage(Phone.Stage.GUEST);
            phoneRepository.save(phone);
            outgoingMessageService.sendLocationNo(phone);
        }
    }
}
