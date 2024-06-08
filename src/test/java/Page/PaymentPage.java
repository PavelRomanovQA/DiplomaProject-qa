package Page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;

public class PaymentPage {

    private SelenideElement heading = $(withText("Оплата по карте"));

    public PaymentPage() {
        heading.shouldBe(visible);
        PaymentCreditDataForm paymentCreditDataForm = new PaymentCreditDataForm();
    }
}
