package data;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HotelFilterData {
    private String facility;
    private Integer minPrice;
    private Integer maxPrice;
    private String rating;

    public HotelFilterData(String facility, Integer minPrice, Integer maxPrice, String rating) {
        this.facility = facility;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.rating = rating;
    }

    @Override
    public String toString() {
        return String.format(
                "Facility: %s | Price Range: %d - %d | Rating: %s",
                facility, minPrice, maxPrice, rating
        );
    }
}
