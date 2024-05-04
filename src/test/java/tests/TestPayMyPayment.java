package tests;

import data.*;
import java.sql.SQLException;
import org.junit.jupiter.api.*;
import io.qameta.allure.selenide.AllureSelenide;
import com.codeborne.selenide.logevents.SelenideLogger;

public class TestPayMyPayment {
    private FormPage formPage;

    @BeforeEach
    void setUpPage() {
        formPage = new FormPage();
    }

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterEach
    void clearAll() throws SQLException {
        DBUtils.clearAllData();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Оплата тура по активной карте, валидные данные.")
    void shouldPayByApprovedCard() throws SQLException {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("05");
        formPage.setCardYear("24");
        formPage.setCardOwner("Alex Ivanov");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageSuccess();
    }

    @Test
    @DisplayName("Проверка ввода в поле (Владелец) значения с тире, валидные данные.")
    void shouldPayOwnerFieldDash() throws SQLException {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("05");
        formPage.setCardYear("24");
        formPage.setCardOwner("Alex-Franc Ivanov");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageSuccess();
    }

    @Test
    @DisplayName("Проверка ввода в поле (Владелец) имени с сокращением, валидные данные.")
    void shouldPayOwnerFieldAbbreviation() throws SQLException {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("05");
        formPage.setCardYear("24");
        formPage.setCardOwner("A. Ivanov");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageSuccess();
    }

    @Test
    @DisplayName("Оплата по неактивной карте из списка, валидные данные")
    void shouldNoPayByDeclinedCard() throws SQLException {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444442");
        formPage.setCardMonth("05");
        formPage.setCardYear("24");
        formPage.setCardOwner("Alex Ivanov");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageError();
    }

    @Test
    @DisplayName("Оплата по неизвестной карте, валидные данные")
    void shouldNoPayByUnknownCard() throws SQLException {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444444");
        formPage.setCardMonth("05");
        formPage.setCardYear("24");
        formPage.setCardOwner("Alex Ivanov");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageError();
    }

    @Test
    @DisplayName("Оплата по карте с истекшим сроком (месяцем) действия")
    void shouldNoPayInvalidMonthField() throws SQLException {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("04");
        formPage.setCardYear("24");
        formPage.setCardOwner("Alex Ivanov");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongDate();
    }

    @Test
    @DisplayName("Оплата по карте с истекшим сроком (годом) действия")
    void shouldNoPayInvalidYearField() throws SQLException {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("05");
        formPage.setCardYear("18");
        formPage.setCardOwner("Alex Ivanov");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageOverDate();
    }

    @Test
    @DisplayName("Оплата по карте с неверным форматом поля (месяц)")
    void shouldNoPayInvalidPeriodMonthField() throws SQLException {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("00");
        formPage.setCardYear("24");
        formPage.setCardOwner("Alex Ivanov");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongDate();
    }

    @Test
    @DisplayName("Оплата по карте c невалидным полем (владелец)")
    void shouldNoPayInvalidCardOwnerField() throws SQLException {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("05");
        formPage.setCardYear("24");
        formPage.setCardOwner("Алексей Иванов");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageError();
    }

    @Test
    @DisplayName("Оплата по карте c невалидным полем владелец")
    void shouldNoPayInvalidOwnerFieldManyValues() throws SQLException {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("05");
        formPage.setCardYear("24");
        formPage.setCardOwner("Alex IvanovIvanovIvanovIvanovIvanovIvanovIvanovIvanovIvanovIvanovIvanovIvanovIvanovIvanov");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageError();
    }

    @Test
    @DisplayName("Оплата по карте c невалидным полем CVV")
    void shouldNoPayInvalidCVVField() throws SQLException {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("05");
        formPage.setCardYear("24");
        formPage.setCardOwner("Alex Ivanov");
        formPage.setCardCVV("10F");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата по карте c пустым номером карты")
    void shouldNoPayEmptyCardNumberField() throws SQLException {
        formPage.buyForYourMoney();
        formPage.setCardNumber("");
        formPage.setCardMonth("05");
        formPage.setCardYear("24");
        formPage.setCardOwner("Alex Ivanov");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата по карте c пустым номером месяца")
    void shouldNoPayEmptyMonthField() throws SQLException {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("");
        formPage.setCardYear("24");
        formPage.setCardOwner("Alex Ivanov");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата по карте c пустым номером года")
    void shouldNoPayEmptyYearField() throws SQLException {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("05");
        formPage.setCardYear("");
        formPage.setCardOwner("Alex Ivanov");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }

    @Test
    @DisplayName("Оплата по карте c пустым полем владелец")
    void shouldNoPayEmptyCardOwnerField() throws SQLException {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("05");
        formPage.setCardYear("24");
        formPage.setCardOwner("");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageRequiredField();
    }

    @Test
    @DisplayName("Оплата по карте c пустым полем CVV")
    void shouldNoPayEmptyCVVField() throws SQLException {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("05");
        formPage.setCardYear("24");
        formPage.setCardOwner("Alex Ivanov");
        formPage.setCardCVV("");
        formPage.pushСontinueButton();
        formPage.checkMessageWrongFormat();
    }


    @Test
    @DisplayName("Оплата по активной карте, валидные данные, проверка записи в БД")
    void shouldPayByApprovedCardStatusInDB() throws SQLException {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444441");
        formPage.setCardMonth("05");
        formPage.setCardYear("24");
        formPage.setCardOwner("Alex Ivanov");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageSuccess();
        DBUtils.checkPaymentStatus(Status.APPROVED);
    }

    @Test
    @DisplayName("Оплата по неактивной карте,валидные данные, проверка записи в БД")
    void shouldNoPayByDeclinedCardStatusInDB() throws SQLException {
        formPage.buyForYourMoney();
        formPage.setCardNumber("4444444444444442");
        formPage.setCardMonth("05");
        formPage.setCardYear("24");
        formPage.setCardOwner("Alex Ivanov");
        formPage.setCardCVV("999");
        formPage.pushСontinueButton();
        formPage.checkMessageSuccess();
        DBUtils.checkPaymentStatus(Status.DECLINED);
    }
}
