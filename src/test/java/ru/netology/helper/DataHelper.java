package ru.netology.helper;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Locale;

public class DataHelper {
    private static final Faker FAKER = new Faker(new Locale("en"));

    private DataHelper() {
    }

    public static CardInfo getApprovedCardInfo() {
        return new CardInfo("1111 2222 3333 4444", "08", "25", "Natalia", "123");
    }

    public static CardInfo getDeclinedCardInfo() {
        return new CardInfo("5555 6666 7777 8888", "08", "25", "Natalia Serdakova", "123");
    }

    private static String generateRandomNumber() {
        return new Faker().number().digits(16);
    }

    private static String generateRandomMonth() {
        return String.format("%02d", new Faker().number().numberBetween(1, 12));
    }

    private static String generateRandomYear() {
        return String.format("%02d", new Faker().number().numberBetween(0, 99));
    }

    private static String generateRandomUser() {
        return FAKER.name().fullName();
    }

    private static String generateRandomCvc() {
        return new Faker().number().digits(3);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardInfo {
        private String number;
        private String month;
        private String year;
        private String user;
        private String cvc;
    }
}
