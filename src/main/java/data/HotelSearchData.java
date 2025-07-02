package data;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class HotelSearchData {
    private final String destination;
    private final int rooms;
    private final int adults;
    private final String facility;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private final int minPrice;
    private final int maxPrice;
    private final String starRating;
    private final int expectedResultCount;

    public HotelSearchData(String destination, int rooms, int adults, String facility,
                           LocalDate checkInDate, LocalDate checkOutDate, int minPrice, int maxPrice, String starRating, int expectedResultCount) {
        this.destination = destination;
        this.rooms = rooms;
        this.adults = adults;
        this.facility = facility;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.starRating = starRating;
        this.expectedResultCount = expectedResultCount;
    }

    @Override
    public String toString() {
        return String.format(
                "%s | %d rooms | %d adults | %s | %s to %s | Price: %d - %d | Stars: %s | Expected result count: %s",
                destination, rooms, adults, facility, checkInDate, checkOutDate, minPrice, maxPrice, starRating, expectedResultCount
        );
    }
}
