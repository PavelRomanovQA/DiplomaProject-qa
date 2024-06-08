package Page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import java.lang.String;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class PaymentCreditDataForm {

    private SelenideElement form = $(By.tagName("form"));

    private SelenideElement cardNumberField = $("[placeholder='0000 0000 0000 0000']");
    private SelenideElement monthField = $("[placeholder='08']");
    private SelenideElement yearField = $("[placeholder='22']");
    private SelenideElement ownerField = $$(".input__inner").findBy(text("Владелец")).$(".input__control");
    private SelenideElement codeField = $("[placeholder='999']");

    private SelenideElement confirmButton = $(withText("Продолжить"));

    private ElementsCollection formErrorInvalidFormat = $$(withText("Неверный формат"));
    private ElementsCollection formErrorRequiredField = $$(withText("Поле обязательно для заполнения"));
    private ElementsCollection formErrorInvalidDate = $$(withText("Неверно указан срок действия карты"));
    private ElementsCollection formErrorExpiredDate = $$(withText("Истёк срок действия карты"));

    public PaymentCreditDataForm() {
        form.shouldBe(visible);
    }

    public void fillIn(String cardNumber, String month, String year, String owner, String code) {
        cardNumberField.setValue(cardNumber);
        monthField.setValue(month);
        yearField.setValue(year);
        ownerField.setValue(owner);
        codeField.setValue(code);
        confirmButton.click();
    }

    public void assertErrorExpiredDate(int quantity) {
        formErrorExpiredDate.shouldHave(CollectionCondition.size(quantity));
    }

    public void assertErrorInvalidDate(int quantity) {
        formErrorInvalidDate.shouldHave(CollectionCondition.size(quantity));
    }

    public void assertErrorInvalidFormat(int quantity) {
        formErrorInvalidFormat.shouldHave(CollectionCondition.size(quantity));
    }

    public void assertErrorRequiredField(int quantity) {
        formErrorRequiredField.shouldHave(CollectionCondition.size(quantity));
    }
}
