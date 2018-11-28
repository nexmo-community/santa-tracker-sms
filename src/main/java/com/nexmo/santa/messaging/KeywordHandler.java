package com.nexmo.santa.messaging;

import com.nexmo.santa.phone.Phone;

public interface KeywordHandler {
    void handle(Phone phone, String text);
}
