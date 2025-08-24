package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.helper.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byCssSelector;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

public class PurchaseFormPage {
    private final SelenideElement cardNumberField = $(".input__box input[type='text'][placeholder='0000 0000 0000 0000']");
    private final SelenideElement expiryMonthField = $(".input__box input[type='text'][maxlength='2'][placeholder='08']");
    private final SelenideElement expiryYearField = $(".input__box input[type='text'][maxlength='2'][placeholder='22']");
    private final SelenideElement holderNameField = $("div.form-field:nth-of-type(3) input.input__control");
    private final SelenideElement cvcField = $(".input__box input[placeholder='999']");
    private final SelenideElement proceedButton = $(".form-field button[type='button']");
    private final SelenideElement successNotification = $(".notification.notification_status_ok .notification__content");
    private final SelenideElement failureNotification = $(".notification.notification_status_error .notification__content");
    private final SelenideElement invalidFormat = $(".input__sub");
    private final SelenideElement expirationDateIsIncorrect = $(".input-group__input-case.input-group__input-case_invalid .input__sub");
    private final SelenideElement fieldIsRequired = $(".form-field:nth-of-type(3) .input__sub");
    private final SelenideElement validityPeriod = $(".form-field:nth-of-type(2) .input__sub");


    public PurchaseFormPage() {
        cardNumberField.shouldBe(visible);
        proceedButton.shouldBe(visible);
    }

    public void fillCardDetails(DataHelper.CardInfo cardInfo) {
        cardNumberField.setValue(cardInfo.getNumber());
        expiryMonthField.setValue(cardInfo.getMonth());
        expiryYearField.setValue(cardInfo.getYear());
        holderNameField.setValue(cardInfo.getUser());
        cvcField.setValue(cardInfo.getCvc());
    }

    public void submitForm() {
        proceedButton.click();
    }

    public void expectSuccessMessage(String expectedText) {
        $("#spinner").should(disappear, Duration.ofSeconds(5));
        successNotification.shouldHave(exactText(expectedText)) // Проверка точного текста
                .shouldBe(visible, Duration.ofSeconds(10));
    }

    public void expectFailureMessage(String expectedText) {
        $("#spinner").should(disappear, Duration.ofSeconds(5));
        failureNotification.shouldHave(exactText(expectedText)) // Проверка точного текста
                .shouldBe(visible, Duration.ofSeconds(10));
    }

    public void checkInvalidFormatError(String expectedText) {
        invalidFormat.shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(exactText(expectedText));
    }

    public void checkExpirationDateIncorrectError(String expectedText) {
        expirationDateIsIncorrect.shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(exactText(expectedText));
    }

    public void checkFieldIsRequiredError(String expectedText) {
        fieldIsRequired.shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(exactText(expectedText));
    }

    public void checkValidityPeriodError(String expectedText) {
        validityPeriod.shouldBe(visible, Duration.ofSeconds(10))
                .shouldHave(exactText(expectedText));
    }

    public String getCardNumberFieldValue() {
        return cardNumberField.val();
    }

    public String getExpiryMonthFieldValue() {
        return expiryMonthField.val();
    }

    public String getExpiryYearFieldValue() {
        return expiryMonthField.val();
    }

    public String getHolderNameFieldValue() {
        return expiryMonthField.val();
    }

    public String getCvcFieldValue() {
        return expiryMonthField.val();
    }
}

