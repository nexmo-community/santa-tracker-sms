package com.nexmo.santa.messaging;

import com.nexmo.santa.phone.Phone;
import com.nexmo.santa.phone.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/incoming")
public class IncomingMessageController {
    private final Map<String, KeywordHandler> keywordHandlers;
    private final PhoneRepository phoneRepository;

    @Autowired
    public IncomingMessageController(PhoneRepository phoneRepository, Map<String, KeywordHandler> keywordHandlers) {
        this.phoneRepository = phoneRepository;
        this.keywordHandlers = keywordHandlers;
    }

    @PostMapping
    public void post(@RequestParam("msisdn") String from,
            @RequestParam("to") String nexmoNumber,
            @RequestParam("keyword") String keyword,
            @RequestParam("text") String text
    ) {
        Phone phone = findOrCreatePhone(from, nexmoNumber);
        findHandler(phone, keyword).handle(phone, text);
    }

    private KeywordHandler findHandler(Phone phone, String keyword) {
        // New users should always go to the default handler
        if (phone.getStage() == null) {
            return keywordHandlers.get("defaultKeywordHandler");
        }

        KeywordHandler handler = keywordHandlers.get(keywordToHandlerName(keyword));
        return (handler != null) ? handler : keywordHandlers.get("defaultKeywordHandler");
    }

    private Phone findOrCreatePhone(String from, String nexmoNumber) {
        Optional<Phone> phoneOptional = phoneRepository.findByNumber(from);
        if (phoneOptional.isPresent()) {
            return phoneOptional.get();
        }

        Phone newPhone = new Phone();
        newPhone.setNumber(from);
        newPhone.setNexmoNumber(nexmoNumber);

        return phoneRepository.save(newPhone);
    }

    private String keywordToHandlerName(String keyword) {
        return keyword.toLowerCase() + "KeywordHandler";
    }
}
