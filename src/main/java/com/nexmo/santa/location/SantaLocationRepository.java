package com.nexmo.santa.location;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface SantaLocationRepository extends JpaRepository<SantaLocation, Long> {

    Optional<SantaLocation> findFirstByArrivalAfter(Date date);

    Optional<SantaLocation> findFirstByArrivalBeforeAndDepartureAfter(Date arrival, Date departure);
}
