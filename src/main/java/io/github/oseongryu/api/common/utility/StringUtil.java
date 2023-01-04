package io.github.oseongryu.api.common.utility;

public class StringUtil {

    public static String checkStringNull(Object obj) {
        String rtValue = "";
        String prValue = "";

        if (obj == null) {
            return rtValue;
        } else {
            prValue = (String) obj.toString();

            if (prValue.isEmpty() || prValue.length() < 1 || prValue.equals("null")) {
                return rtValue;
            } else {
                return prValue;
            }
        }
    }
}
