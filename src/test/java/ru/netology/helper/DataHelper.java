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
        return String.format("%02d", new Faker().number().numberBetween(9, 12));
    }

    private static String generateRandomYear() {
        return String.format("%02d", new Faker().number().numberBetween(25, 26));
    }

    private static String generateRandomUser() {
        return FAKER.name().fullName();
    }

    private static String generateRandomCvc() {
        return new Faker().number().digits(3);
    }

    public static CardInfo getIncorrectCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                generateRandomMonth(),
                generateRandomYear(),
                generateRandomUser(),
                generateRandomCvc());
    }

    public static CardInfo getIncorrectCardOneDigitCardInfo() {
        return new CardInfo(
                "8",
                generateRandomMonth(),
                generateRandomYear(),
                generateRandomUser(),
                generateRandomCvc());
    }

    public static CardInfo getIncorrectCardSeventeenDigitCardInfo() {
        return new CardInfo(
                "8451 1584 4687 4654 4",
                generateRandomMonth(),
                generateRandomYear(),
                generateRandomUser(),
                generateRandomCvc());
    }

    public static CardInfo getIncorrectCardEmptyFieldCardInfo() {
        return new CardInfo(
                "",
                generateRandomMonth(),
                generateRandomYear(),
                generateRandomUser(),
                generateRandomCvc());
    }

    public static CardInfo getIncorrectCardSpecialCharactersCardInfo() {
        return new CardInfo(
                "%$#&",
                generateRandomMonth(),
                generateRandomYear(),
                generateRandomUser(),
                generateRandomCvc());
    }

    public static CardInfo getIncorrectMonthCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                "15",
                generateRandomYear(),
                generateRandomUser(),
                generateRandomCvc());
    }

    public static CardInfo getIncorrectMonthOneDigitCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                "9",
                generateRandomYear(),
                generateRandomUser(),
                generateRandomCvc());
    }

    public static CardInfo getIncorrectMonthEmptyFieldCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                "",
                generateRandomYear(),
                generateRandomUser(),
                generateRandomCvc());
    }

    public static CardInfo getIncorrectMonthThreeDigitCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                "098",
                generateRandomYear(),
                generateRandomUser(),
                generateRandomCvc());
    }

    public static CardInfo getIncorrectMonthSpecialCharactersCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                "$%#",
                generateRandomYear(),
                generateRandomUser(),
                generateRandomCvc());
    }

    public static CardInfo getIncorrectLastYearCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                generateRandomMonth(),
                "22",
                generateRandomUser(),
                generateRandomCvc());
    }

    public static CardInfo getIncorrectYearEmptyFieldCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                generateRandomMonth(),
                "",
                generateRandomUser(),
                generateRandomCvc());
    }

    public static CardInfo getIncorrectYearOneDigitCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                generateRandomMonth(),
                "2",
                generateRandomUser(),
                generateRandomCvc());
    }


    public static CardInfo getIncorrectYearThreeDigitCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                generateRandomMonth(),
                "022",
                generateRandomUser(),
                generateRandomCvc());
    }
    public static CardInfo getIncorrectYearSpecialCharactersCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                generateRandomMonth(),
                "^%$#",
                generateRandomUser(),
                generateRandomCvc());
    }

    public static CardInfo getIncorrectOwnerInCyrillicCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                generateRandomMonth(),
                generateRandomYear(),
                "Наталья",
                generateRandomCvc());
    }

    public static CardInfo getIncorrectOwnerEmptyFieldCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                generateRandomMonth(),
                generateRandomYear(),
                "",
                generateRandomCvc());
    }

    public static CardInfo getIncorrectOwnerSpecialCharactersCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                generateRandomMonth(),
                generateRandomYear(),
                "%$#$",
                generateRandomCvc());
    }

    public static CardInfo getIncorrectOwnerOneLetterCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                generateRandomMonth(),
                generateRandomYear(),
                "N",
                generateRandomCvc());
    }

    public static CardInfo getIncorrectOwnerManyLetterCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                generateRandomMonth(),
                generateRandomYear(),
                "Nanananananananananananananananananananananananananananananananananana",
                generateRandomCvc());
    }

    public static CardInfo getIncorrectCvcEmptyFieldCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                generateRandomMonth(),
                generateRandomYear(),
                generateRandomUser(),
                "");
    }

    public static CardInfo getIncorrectCvcTwoDigitCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                generateRandomMonth(),
                generateRandomYear(),
                generateRandomUser(),
                "56");
    }

    public static CardInfo getIncorrectCvcFourDigitCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                generateRandomMonth(),
                generateRandomYear(),
                generateRandomUser(),
                "5656");
    }

    public static CardInfo getIncorrectCvcSpecialCharactersCardInfo() {
        return new CardInfo(
                generateRandomNumber(),
                generateRandomMonth(),
                generateRandomYear(),
                generateRandomUser(),
                "^%$#");
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
