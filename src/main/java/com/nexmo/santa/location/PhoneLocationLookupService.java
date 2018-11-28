package com.nexmo.santa.location;

import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoClientException;
import com.nexmo.client.insight.InsightClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PhoneLocationLookupService {
    private final InsightClient insightClient;

    @Autowired
    public PhoneLocationLookupService(NexmoClient nexmoClient) {
        this.insightClient = nexmoClient.getInsightClient();
    }

    public String lookupCountryCode(String number) {
        try {
            return this.insightClient.getBasicNumberInsight(number).getCountryCode();
        } catch (IOException | NexmoClientException e) {
            return null;
        }
    }
}
