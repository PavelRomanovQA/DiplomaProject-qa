package Page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;

public class CreditPage {

    private SelenideElement heading = $(withText("Кредит по данным карты"));

    public CreditPage() {
        heading.shouldBe(visible);
        PaymentCreditDataForm paymentCreditDataForm = new PaymentCreditDataForm();
    }
}
