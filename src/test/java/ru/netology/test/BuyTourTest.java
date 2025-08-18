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

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    }

    // Тест 1: Положительная покупка обычным способом
    @Test
    void testSuccessfulPurchaseTour() {

        purchaseFormPage = tourPurchasePage.clickBuyButton();
        purchaseFormPage.fillCardDetails(DataHelper.getApprovedCardInfo());
        purchaseFormPage.submitForm();
        purchaseFormPage.expectSuccessMessage();
        Assertions.assertTrue(SQLHelper.getPayments().isEmpty(), "Ожидалось появление записи о платеже в базе данных");
    }
}




