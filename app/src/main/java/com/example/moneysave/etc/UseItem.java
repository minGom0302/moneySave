package com.example.moneysave.etc;

import java.text.DecimalFormat;

public class UseItem {
    static DecimalFormat commaFormat = new DecimalFormat("#,###");

    public static String comma(String money) {
        int s1 = Integer.parseInt(money);
        return commaFormat.format(s1);
    }

    public static DecimalFormat getCommaFormat() { return commaFormat; }
}
