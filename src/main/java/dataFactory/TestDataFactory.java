package dataFactory;

import data.HotelSearchData;
import utils.DateTimeUtils;

import java.time.LocalDate;

public class TestDataFactory {
    public static HotelSearchData daNangWithPool() {
        LocalDate checkInDate = DateTimeUtils.getNextFriday();

        return HotelSearchData.builder()
                .destination("Da Nang")
                .rooms(2)
                .adults(4)
                .facility("Swimming pool")
                .checkInDate(checkInDate)
                .checkOutDate(checkInDate.plusDays(3))
                .minPrice(500000)
                .maxPrice(1000000)
                .starRating("3")
                .expectedResultCount(5)
                .build();
    }
}
