package Page;

import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;

public class DashboardPage {

    private SelenideElement heading = $(withText("Путешествие"));

    private SelenideElement buyButton = $(withText("Купить"));
    private SelenideElement creditButton = $(withText("Купить в кредит"));

    private final SelenideElement success = $(withText("Успешно"));
    private final SelenideElement assertError = $(withText("Ошибка"));

    public DashboardPage() {
        heading.shouldBe(visible);
    }

    public PaymentPage pay() {
        buyButton.click();
        return new PaymentPage();
    }

    public CreditPage credit() {
        creditButton.click();
        return new CreditPage();
    }

    public void waitNotificationOk() {
        success.shouldBe(visible, Duration.ofMillis(15000));
    }
    public void waitNotificationError() {
        assertError.shouldBe(visible, Duration.ofMillis(15000));
    }
}