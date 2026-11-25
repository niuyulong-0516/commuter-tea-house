package com.commutertea.common;

import java.util.Locale;

public final class GuestNames {

    private GuestNames() {
    }

    /**
     * 从访客标识生成一个稳定的茶馆昵称，例如 guest_a7k2 -> 茶客A7K2。
     */
    public static String fromGuestId(String guestId) {
        if (guestId == null || guestId.isBlank()) {
            return "茶客";
        }
        String cleaned = guestId.replaceAll("[^A-Za-z0-9]", "");
        if (cleaned.isEmpty()) {
            return "茶客";
        }
        String suffix = cleaned.length() > 4 ? cleaned.substring(cleaned.length() - 4) : cleaned;
        return "茶客" + suffix.toUpperCase(Locale.ROOT);
    }
}
