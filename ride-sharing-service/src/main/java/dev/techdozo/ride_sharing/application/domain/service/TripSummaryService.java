package dev.techdozo.ride_sharing.application.domain.service;

import dev.techdozo.ride_sharing.application.domain.model.TripSummary;

public class TripSummaryService {
    public TripSummary getTripSummary(String rideId) {
        return new TripSummary(5, 20);
    }
}
