package dev.techdozo.ride_sharing.api;

import dev.techdozo.ride_sharing.application.domain.model.RideData;
import dev.techdozo.ride_sharing.application.domain.repository.TripRepository;
import dev.techdozo.ride_sharing.application.domain.service.TripSummaryService;
import dev.techdozo.ride_sharing.trip.Service.TripDataRequest;
import dev.techdozo.ride_sharing.trip.Service.TripSummaryResponse;
import dev.techdozo.ride_sharing.trip.TripServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class RideSharingAPI extends TripServiceGrpc.TripServiceImplBase {

  private final TripRepository tripRepository;
  private final TripSummaryService tripSummaryService;

  @Override
  public StreamObserver<TripDataRequest> sendTripData(
      StreamObserver<TripSummaryResponse> responseObserver) {

    return new StreamObserver<TripDataRequest>() {
      private String rideId;

      @Override
      public void onNext(TripDataRequest request) {
        // Save Ride data
        tripRepository.saveTripData(
            new RideData(
                request.getRideType().getDriverId(),
                request.getRideType().getRideId(),
                request.getLatitude(),
                request.getLongitude()));
        log.info("Driver Id {} - object {}", request.getRideType().getDriverId(), this);
        this.rideId = request.getRideType().getRideId();
      }

      @Override
      public void onError(Throwable t) {
        log.error("Error while processing request ");
      }

      @Override
      public void onCompleted() {
        // Once Trip is completed then generate Trip summary
        var tripSummary = tripSummaryService.getTripSummary(rideId);
        responseObserver.onNext(
            TripSummaryResponse.newBuilder()
                .setDistance(tripSummary.getDistance())
                .setCharge(tripSummary.getCharge())
                .build());
        log.info("Request completed");
      }
    };
  }
}
