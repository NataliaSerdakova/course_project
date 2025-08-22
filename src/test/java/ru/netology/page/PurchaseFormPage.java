package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.helper.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byCssSelector;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

public class PurchaseFormPage {
    private final SelenideElement cardNumberField = $(".input__box input[type='text'][placeholder='0000 0000 0000 0000']");
    private final SelenideElement expiryMonthField = $(".input__box input[type='text'][maxlength='2'][placeholder='08']");
    private final SelenideElement expiryYearField = $(".input__box input[type='text'][maxlength='2'][placeholder='22']");
    private final SelenideElement holderNameField = $("div.form-field:nth-of-type(3) input.input__control");
    private final SelenideElement cvcField = $(".input__box input[type='text'][maxlength='3'][placeholder='999']");
    private final SelenideElement proceedButton = $(".form-field .button.button_view_extra.button_size_m.button_theme_alfa-on-white");
    private final SelenideElement successNotification = $(".notification.notification_status_ok .notification__content");
    private final SelenideElement failureNotification = $(".notification.notification_status_error .notification__content");
    private final SelenideElement invalidFormat = $(".input__sub");
    private final SelenideElement expirationDateIsIncorrect = $(".form-field:nth-of-type(2) .input-group__input-case_invalid:nth-of-type(1) .input__sub");
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
        sleep(5000);
    }

    public String getResultMessage() {
        return successNotification.exists() ?
                successNotification.shouldBe(visible, Duration.ofSeconds(10)).getText() :
                failureNotification.shouldBe(visible, Duration.ofSeconds(10)).getText();
    }

    public void expectSuccessMessage() {
        $("#spinner").should(disappear, Duration.ofSeconds(10));
        successNotification.shouldBe(visible, Duration.ofSeconds(15));
    }

    public void expectFailureMessage() {
        $("#spinner").should(disappear, Duration.ofSeconds(10));
        failureNotification.shouldBe(visible, Duration.ofSeconds(15));
    }

    public String getInvalidFormatErrorText() {
        if (invalidFormat.exists() && invalidFormat.isDisplayed()) {
            return invalidFormat.getText();
        }
        return null;
    }

    public String getExpirationDateIncorrectErrorText() {
        if (expirationDateIsIncorrect.exists() && expirationDateIsIncorrect.isDisplayed()) {
            return expirationDateIsIncorrect.getText();
        }
        return null;
    }

    public String getFieldIsRequiredErrorText() {
        if (fieldIsRequired.exists() && fieldIsRequired.isDisplayed()) {
            return fieldIsRequired.getText();
        }
        return null;
    }

    public String getValidityPeriodErrorText() {
        if (validityPeriod.exists() && validityPeriod.isDisplayed()) {
            return validityPeriod.getText();
        }
        return null;
    }
}

