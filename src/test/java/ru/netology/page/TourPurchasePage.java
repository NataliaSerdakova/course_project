package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byCssSelector;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class TourPurchasePage {
    private final SelenideElement buyTourButton = $$(".button").findBy(text("Купить"));
    private final SelenideElement buyOnCreditButton = $$(".button").findBy(text("Купить в кредит"));

    public TourPurchasePage() {
        buyTourButton.shouldBe(visible);
        buyOnCreditButton.shouldBe(visible);
    }

    public PurchaseFormPage clickBuyButton() {
        buyTourButton.click();
        return new PurchaseFormPage();
    }

    public PurchaseFormPage clickBuyOnCreditButton() {
        buyOnCreditButton.click();
        return new PurchaseFormPage();
    }
}
