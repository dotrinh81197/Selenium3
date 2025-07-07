package dataFactory;

import data.HotelFilterData;
import data.HotelSearchData;
import utils.DateTimeUtils;

import java.time.LocalDate;

public class TestDataFactory {
    public static HotelSearchData searchData() {
        LocalDate checkInDate = DateTimeUtils.getNextFriday();

        return HotelSearchData.builder()
                .destination("Da Nang")
                .rooms(2)
                .adults(4)
                .checkInDate(checkInDate)
                .checkOutDate(checkInDate.plusDays(3))
                .build();
    }

    public static HotelFilterData priceStarFilter() {
        return HotelFilterData.builder()
                .minPrice(500000)
                .maxPrice(1000000)
                .rating("3")
                .build();
    }

    public static HotelFilterData facilityFilter() {
        return HotelFilterData.builder()
                .facility("Swimming pool")
                .build();
    }
}
