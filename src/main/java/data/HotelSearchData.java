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
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;

    public HotelSearchData(String destination, int rooms, int adults,
                           LocalDate checkInDate, LocalDate checkOutDate) {
        this.destination = destination;
        this.rooms = rooms;
        this.adults = adults;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    @Override
    public String toString() {
        return String.format(
                "%s | %d rooms | %d adults | %s to %s ",
                destination, rooms, adults, checkInDate, checkOutDate
        );
    }
}
