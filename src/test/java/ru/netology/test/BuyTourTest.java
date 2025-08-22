package ru.netology.test;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;

import ru.netology.helper.DataHelper;
import ru.netology.helper.SQLHelper;
import ru.netology.page.PurchaseFormPage;
import ru.netology.page.TourPurchasePage;

import java.util.List;
import java.util.Optional;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.helper.SQLHelper.cleanDatabase;


public class BuyTourTest {
    TourPurchasePage tourPurchasePage;
    PurchaseFormPage purchaseFormPage;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setUp() {
        tourPurchasePage = open("http://localhost:8080", TourPurchasePage.class);
        SQLHelper.cleanDatabase();
    }

    //Покупка тура с успешно прошедшей оплатой обычной дебетовой картой
    @Test
    void testSuccessfulPurchaseTour() {
        long initialPaymentsCount = SQLHelper.countPayments();
        long initialOrdersCount = SQLHelper.countOrders();

        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getApprovedCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.expectSuccessMessage();
        assertTrue(SQLHelper.countPayments() > initialPaymentsCount,
                "В таблице payment_entity должны появиться новые записи");
        assertTrue(SQLHelper.countOrders() > initialOrdersCount,
                "В таблице order_entity должны появиться новые записи");
        assertTrue(SQLHelper.isPaymentRecorded(45000, "APPROVED"));

        List<SQLHelper.OrderEntity> orders = SQLHelper.getOrders();
        SQLHelper.OrderEntity lastOrder = orders.get(orders.size() - 1);
        assertNotNull(lastOrder.getPayment_id(), "Платёжное поле должно быть заполнено для обычной покупки");
    }


    //Покупка тура с успешно выданным кредитом
    @Test
    void testSuccessfulPurchaseTourOnCredit() {
        long initialCreditCount = SQLHelper.countPayments();
        long initialOrdersCount = SQLHelper.countOrders();

        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getApprovedCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.expectSuccessMessage();
        assertTrue(SQLHelper.countCredit() > initialCreditCount,
                "В таблице credit_request_entity должны появиться новые записи");
        assertTrue(SQLHelper.countOrders() > initialOrdersCount,
                "В таблице order_entity должны появиться новые записи");
        assertTrue(SQLHelper.isCreditRecorded("APPROVED"));

        List<SQLHelper.OrderEntity> orders = SQLHelper.getOrders();
        SQLHelper.OrderEntity lastOrder = orders.get(orders.size() - 1);
        assertNotNull(lastOrder.getCredit_id(), "Платёжное поле должно быть заполнено для кредитной покупки");
    }

    //Покупка тура отклоненной картой
    @Test
    void testTourPurchaseDeclinedCard() {
        long initialPaymentsCount = SQLHelper.countPayments();
        long initialOrdersCount = SQLHelper.countOrders();
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getDeclinedCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.expectFailureMessage();
        assertTrue(SQLHelper.countPayments() > initialPaymentsCount,
                "В таблице payment_entity должны появиться новые записи");
        assertTrue(SQLHelper.countOrders() > initialOrdersCount,
                "В таблице order_entity должны появиться новые записи");
        assertTrue(SQLHelper.isPaymentRecorded(45000, "DECLINED"));

        List<SQLHelper.OrderEntity> orders = SQLHelper.getOrders();
        SQLHelper.OrderEntity lastOrder = orders.get(orders.size() - 1);
        assertNotNull(lastOrder.getPayment_id(), "Платёжное поле должно быть заполнено для обычной покупки");
    }

    //Покука тура отклоненной картой в кредит
    @Test
    void testTourPurchaseDeclinedCardOnCredit() {
        long initialCreditCount = SQLHelper.countPayments();
        long initialOrdersCount = SQLHelper.countOrders();
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getDeclinedCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.expectFailureMessage();
        assertTrue(SQLHelper.countCredit() > initialCreditCount,
                "В таблице credit_request_entity должны появиться новые записи");
        assertTrue(SQLHelper.countOrders() > initialOrdersCount,
                "В таблице order_entity должны появиться новые записи");
        assertTrue(SQLHelper.isCreditRecorded("DECLINED"));

        List<SQLHelper.OrderEntity> orders = SQLHelper.getOrders();
        SQLHelper.OrderEntity lastOrder = orders.get(orders.size() - 1);
        assertNotNull(lastOrder.getCredit_id(), "Платёжное поле должно быть заполнено для кредитной покупки");
    }

    //Покупка тура с неправильным вводом карт(несуществующий номер карты)
    @Test
    void testTourPurchaseWishAnIncorrectCard() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.expectFailureMessage();
    }

    //Покупка тура в кредит с неправильным вводом карт(несуществующий номер карты)
    @Test
    void testTourPurchaseWishAnIncorrectCardOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.expectFailureMessage();
    }

    //Покупка тура с неправильным вводом карт(1 цифра в поле номер карты)
    @Test
    void testTourPurchaseWishAnIncorrectCardOneDigit() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectCardOneDigitCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);

    }

    /// /Покупка тура в кредит с неправильным вводом карт(1 цифра в поле номер карты)
    @Test
    void testTourPurchaseWishAnIncorrectCardOneDigitOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectCardOneDigitCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильным вводом карт(17 цифр в поле номер карты)
    @Test
    void testTourPurchaseWishAnIncorrectCardSeventeenDigit() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectCardSeventeenDigitCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = null;

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура в кредит с неправильным вводом карт(17 цифр в поле номер карты)
    @Test
    void testTourPurchaseWishAnIncorrectCardSeventeenDigitOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectCardSeventeenDigitCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = null;

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильным вводом карт(пустое поле номер карты)
    @Test
    void testTourPurchaseWishAnIncorrectCardEmptyField() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectCardEmptyFieldCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура в кредит с неправильным вводом карт(пустое поле номер карты)
    @Test
    void testTourPurchaseWishAnIncorrectCardEmptyFieldOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectCardEmptyFieldCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильным вводом карт(спецсимволы в поле номер карты)
    @Test
    void testTourPurchaseWishAnIncorrectCardSpecialCharacters() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectCardSpecialCharactersCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура в кредит с неправильным вводом карт(спецсимволы в поле номер карты)
    @Test
    void testTourPurchaseWishAnIncorrectCardSpecialCharactersOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectCardSpecialCharactersCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильнным вводом данных карт(несуществующий месяц)
    @Test
    void testTourPurchaseWishAnIncorrectMonth() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectMonthCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getExpirationDateIncorrectErrorText();

        String actualErrorText = purchaseFormPage.getExpirationDateIncorrectErrorText();
        String expectedErrorText = "Неверно указан срок действия карты";

        assertEquals(expectedErrorText, actualErrorText);
    }

    /// /Покупка тура в кредит с неправильнным вводом данных карт(несуществующий месяц)
    @Test
    void testTourPurchaseWishAnIncorrectMonthOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectMonthCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getExpirationDateIncorrectErrorText();

        String actualErrorText = purchaseFormPage.getExpirationDateIncorrectErrorText();
        String expectedErrorText = "Неверно указан срок действия карты";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильнным вводом данных карт(одна цифра в поле месяц)
    @Test
    void testTourPurchaseWishAnIncorrectMonthOneDigit() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectMonthOneDigitCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    /// /Покупка тура в кредит с неправильным вводом карт(1 цифра в поле месяц)
    @Test
    void testTourPurchaseWishAnIncorrectMonthOneDigitOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectMonthOneDigitCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильнным вводом данных карт(пустое поле месяц)
    @Test
    void testTourPurchaseWishAnIncorrectMonthEmptyField() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectMonthEmptyFieldCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура в кредит с неправильным вводом карт(пустое поле месяц)
    @Test
    void testTourPurchaseWishAnIncorrectMonthEmptyFieldOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectMonthEmptyFieldCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильнным вводом данных карт(3 цифры в поле месяц)
    @Test
    void testTourPurchaseWishAnIncorrectMonthThreeDigit() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectMonthThreeDigitCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = null;

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура в кредит с неправильным вводом карт(3 цифры в поле месяц)
    @Test
    void testTourPurchaseWishAnIncorrectMonthThreeDigitOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectMonthThreeDigitCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();
        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = null;

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильнным вводом данных карт(введение спецсимволов в поле месяц)
    @Test
    void testTourPurchaseWishAnIncorrectMonthSpecialCharacters() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectMonthSpecialCharactersCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура в кредит с неправильным вводом карт(введение спецсимволов в поле месяц)
    @Test
    void testTourPurchaseWishAnIncorrectMonthSpecialCharactersOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectMonthSpecialCharactersCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильным вводом данных карт(прошедший год)
    @Test
    void testTourPurchaseWishAnIncorrectLastYear() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectLastYearCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getExpirationDateIncorrectErrorText();

        String actualErrorText = purchaseFormPage.getValidityPeriodErrorText();
        String expectedErrorText = "Истёк срок действия карты";

        assertEquals(expectedErrorText, actualErrorText);

    }

    /// /Покупка тура в кредит с неправильнным вводом данных карт(прошедший год)
    @Test
    void testTourPurchaseWishAnIncorrectLastYearOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectLastYearCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getExpirationDateIncorrectErrorText();

        String actualErrorText = purchaseFormPage.getValidityPeriodErrorText();
        String expectedErrorText = "Истёк срок действия карты";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильным вводом данных карт(пустое поле год)
    @Test
    void testTourPurchaseWishAnIncorrectYearEmptyField() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectYearEmptyFieldCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура в кредит с неправильным вводом карт(пустое поле год)
    @Test
    void testTourPurchaseWishAnIncorrectYearEmptyFieldOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectYearEmptyFieldCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильным вводом данных карт(1 цифра в поле год)
    @Test
    void testTourPurchaseWishAnIncorrectYearOneDigit() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectYearOneDigitCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    /// /Покупка тура в кредит с неправильным вводом карт(1 цифра в поле год)
    @Test
    void testTourPurchaseWishAnIncorrectYearOneDigitOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectYearOneDigitCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильным вводом данных карт(3 цифры в поле год)
    @Test
    void testTourPurchaseWishAnIncorrectYearThreeDigit() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectYearThreeDigitCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getValidityPeriodErrorText();
        String expectedErrorText = "Истёк срок действия карты";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура в кредит с неправильным вводом карт(3 цифры в поле год)
    @Test
    void testTourPurchaseWishAnIncorrectYearThreeDigitOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectYearThreeDigitCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getValidityPeriodErrorText();
        String expectedErrorText = "Истёк срок действия карты";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильным вводом данных карт(спецсимволы в поле год)
    @Test
    void testTourPurchaseWishAnIncorrectYearSpecialCharacters() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectYearSpecialCharactersCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура в кредит с неправильным вводом карт(спецсимволы в поле год)
    @Test
    void testTourPurchaseWishAnIncorrectYearSpecialCharactersOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectYearSpecialCharactersCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильным вводом данных карт(поле владелец на кирилице)
    @Test
    void testTourPurchaseWishAnIncorrectOwnerInCyrillic() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectOwnerInCyrillicCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура в кредит с неправильным вводом карт(поле владелец на кирилице)
    @Test
    void testTourPurchaseWishAnIncorrectOwnerInCyrillicOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectOwnerInCyrillicCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильным вводом данных карт(поле владелец не заполнено)
    @Test
    void testTourPurchaseWishAnIncorrectOwnerEmptyField() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectOwnerEmptyFieldCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getFieldIsRequiredErrorText();

        String actualErrorText = purchaseFormPage.getFieldIsRequiredErrorText();
        String expectedErrorText = "Поле обязательно для заполнения";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура в кредит с неправильным вводом карт(поле владелец не заполнено)
    @Test
    void testTourPurchaseWishAnIncorrectOwnerEmptyFieldOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectOwnerEmptyFieldCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getFieldIsRequiredErrorText();

        String actualErrorText = purchaseFormPage.getFieldIsRequiredErrorText();
        String expectedErrorText = "Поле обязательно для заполнения";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильным вводом данных карт(спецсимволы поле владелец)
    @Test
    void testTourPurchaseWishAnIncorrectOwnerSpecialCharacters() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectOwnerSpecialCharactersCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура в кредит с неправильным вводом карт(спецсимволы поле владелец)
    @Test
    void testTourPurchaseWishAnIncorrectOwnerSpecialCharactersOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectOwnerSpecialCharactersCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильным вводом данных карт(одна буква в поле владелец)
    @Test
    void testTourPurchaseWishAnIncorrectOwnerOneLetter() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectOwnerOneLetterCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура в кредит с неправильным вводом карт(одна буква в поле владелец)
    @Test
    void testTourPurchaseWishAnIncorrectOwnerOneLetterOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectOwnerOneLetterCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильным вводом данных карт(много букв в поле владелец)
    @Test
    void testTourPurchaseWishAnIncorrectOwnerManyLetter() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectOwnerManyLetterCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура в кредит с неправильным вводом карт(много букв в поле владелец)
    @Test
    void testTourPurchaseWishAnIncorrectOwnerManyLetterOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectOwnerManyLetterCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильным вводом данных карт(cvc пустое поле)
    @Test
    void testTourPurchaseWishAnIncorrectCvcEmptyField() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectCvcEmptyFieldCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура в кредит с неправильным вводом карт(cvc пустое поле)
    @Test
    void testTourPurchaseWishAnIncorrectCvcEmptyFieldOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectCvcEmptyFieldCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильным вводом данных карт(две цифры в поле cvc)
    @Test
    void testTourPurchaseWishAnIncorrectCvcTwoDigit() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectCvcTwoDigitCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура в кредит с неправильным вводом карт(две цифры в поле cvc)
    @Test
    void testTourPurchaseWishAnIncorrectCvcTwoDigitOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectCvcTwoDigitCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильным вводом данных карт(четыре цифры в поле cvc)
    @Test
    void testTourPurchaseWishAnIncorrectCvcFourDigit() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectCvcFourDigitCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = null;

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура в кредит с неправильным вводом карт(четыре цифры в поле cvc)
    @Test
    void testTourPurchaseWishAnIncorrectCvcFourDigitOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectCvcFourDigitCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = null;

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура с неправильным вводом данных карт(спецсимволы в поле cvc)
    @Test
    void testTourPurchaseWishAnIncorrectCvcSpecialCharacters() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectCvcSpecialCharactersCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура в кредит с неправильным вводом карт(спецсимволы в поле cvc)
    @Test
    void testTourPurchaseWishAnIncorrectCvcSpecialCharactersOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.fillCardDetails(DataHelper.getIncorrectCvcSpecialCharactersCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);
    }

    //Покупка тура без заполнения полей
    @Test
    void testTourPurchaseWishAnEmptyFields() {
        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();
        purchaseFormPage.getFieldIsRequiredErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);

        String actualErrorText2 = purchaseFormPage.getFieldIsRequiredErrorText();
        String expectedErrorText2 = "Поле обязательно для заполнения";

        assertEquals(expectedErrorText2, actualErrorText2);
    }

    //Покупка тура в кредит без заполнения полей
    @Test
    void testTourPurchaseWishAnEmptyFieldsOnCredit() {
        purchaseFormPage = tourPurchasePage.clickBuyOnCreditButton();
        purchaseFormPage.submitForm();
        purchaseFormPage.getInvalidFormatErrorText();
        purchaseFormPage.getFieldIsRequiredErrorText();

        String actualErrorText = purchaseFormPage.getInvalidFormatErrorText();
        String expectedErrorText = "Неверный формат";

        assertEquals(expectedErrorText, actualErrorText);

        String actualErrorText2 = purchaseFormPage.getFieldIsRequiredErrorText();
        String expectedErrorText2 = "Поле обязательно для заполнения";

        assertEquals(expectedErrorText2, actualErrorText2);
    }
}





