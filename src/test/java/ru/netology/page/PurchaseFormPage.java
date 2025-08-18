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
    private final SelenideElement holderNameField = $(".input__box input.input__control[autocomplete='on']");
    private final SelenideElement cvcField = $(".input__box input[type='text'][maxlength='3'][placeholder='999']");
    private final SelenideElement proceedButton = $(".form-field .button.button_view_extra.button_size_m.button_theme_alfa-on-white");
    private final SelenideElement successNotification = $(".notification.notification_status_ok .notification__content");
    private final SelenideElement failureNotification = $(".notification.notification_status_error .notification__content");

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
        failureNotification.shouldBe(visible, Duration.ofSeconds(10));
    }
}
