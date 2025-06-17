package utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class MoneyUtils {

    /**
     * Formats an integer amount to VND style: 500000 -> "500,000 ₫"
     *
     * @param amount the amount in VND
     * @return formatted string
     */
    public static String formatVND(int amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');

        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        return formatter.format(amount) + " ₫";
    }

}
