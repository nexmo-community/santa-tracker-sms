package com.nexmo.santa.phone;

import com.nexmo.santa.location.Location;

import javax.persistence.*;

@Entity
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;
    private String nexmoNumber;
    private String countryCode;

    @Enumerated(EnumType.STRING)
    private Stage stage;

    @OneToOne
    private Location location;

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNexmoNumber() {
        return nexmoNumber;
    }

    public void setNexmoNumber(String nexmoNumber) {
        this.nexmoNumber = nexmoNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public enum Stage {
        INITIAL, COUNTRY_PROMPT, POSTAL_PROMPT, GUEST, REGISTERED
    }
}
