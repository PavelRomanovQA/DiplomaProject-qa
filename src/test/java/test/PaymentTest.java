package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.*;
import data.DataHelper;
import data.SQLHelper;
import Page.DashboardPage;
import Page.PaymentCreditDataForm;

import java.lang.String;
import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;

class PaymentTest {

    private static SQLHelper sqlHelper;
    private DataHelper dataHelper = new DataHelper();
    private final static String APPROVED = "APPROVED";
    private final static String DECLINED = "DECLINED";

    @BeforeAll
    static void setUp() throws SQLException {
        String urlMySQL = "jdbc:mysql://localhost:3306/app";
        sqlHelper = new SQLHelper(urlMySQL);
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void setBack() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void openPage() {
        open("http://localhost:8080");
    }

    @AfterEach
    void cleanUp() {
        sqlHelper.cleanAll();
    }


    @Test
    @DisplayName("Оплата тура по активной карте, валидные данные.")
    void shouldPayByApprovedCard() {
        val dashboardPage = new DashboardPage();
        String cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        dashboardPage.waitNotificationOk();
        Assertions.assertNull(sqlHelper.getLastPaymentStatus());
    }

    @Test
    @DisplayName("Оплата тура c сокращенным вводом имя в поле (владелец) валидные данные.")
    void shouldPayDoubleNameCard() {
        val dashboardPage = new DashboardPage();
        String cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.getDoubleName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        dashboardPage.waitNotificationOk();
        Assertions.assertNull(sqlHelper.getLastPaymentStatus());
    }

    @Test
    @DisplayName("Оплата тура c сокращенным вводом имя в поле (владелец) валидные данные.")
    void shouldPayByAbbreviatedCard() {
        val dashboardPage = new DashboardPage();
        String cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.getAbbreviatedName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        dashboardPage.waitNotificationOk();
        Assertions.assertNull(sqlHelper.getLastPaymentStatus());
    }


    @Test
    @DisplayName("Оплата по неактивной карте из списка, валидные данные")
    void shouldNoPayByDeclinedCard() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getDeclined();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        dashboardPage.waitNotificationError();
        Assertions.assertNull(sqlHelper.getLastPaymentStatus());
    }

    @Test
    @DisplayName("Оплата по неизвестной карте, валидные данные")
    void shouldNoPayByUnknownCard() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getTooShort();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        dashboardPage.waitNotificationError();
        Assertions.assertNull(sqlHelper.getLastPaymentStatus());
    }

    @Test
    @DisplayName("Оплата по карте с истекшим сроком (месяцем) действия")
    void shouldNoPayInvalidMonthField() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.expiredOneMonth().getMonth();
        val year = dataHelper.expiredhMonthAndYear().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        form.assertErrorInvalidDate(1);
        Assertions.assertNull(sqlHelper.getLastPaymentStatus());
    }

    @Test
    @DisplayName("Оплата по карте с неверным форматом поля (месяц)")
    void shouldNoPayInvalidPeriodMonthField() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.getField();
        val year = dataHelper.expiredhMonthAndYear().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        form.assertErrorInvalidDate(1);
        Assertions.assertNull(sqlHelper.getLastPaymentStatus());
    }

    @Test
    @DisplayName("Оплата по карте с истекшим сроком (годом) действия")
    void shouldNoPayInvalidYearField() {
        val dashboardPage = new DashboardPage();
        String cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.expiredhMonthAndYear().getMonth();
        val year = dataHelper.randomMonthAndYearInThePast().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        form.assertErrorExpiredDate(1);
        Assertions.assertNull(sqlHelper.getLastPaymentStatus());
    }

    @Test
    @DisplayName("Оплата по карте с истекшим сроком (годом) действия")
    void shouldNoPayInvalidYear() {
        val dashboardPage = new DashboardPage();
        String cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.expiredhMonthAndYear().getMonth();
        val year = dataHelper.randomMonthAndYearInThe().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        form.assertErrorInvalidDate(1);
        Assertions.assertNull(sqlHelper.getLastPaymentStatus());
    }

    @Test
    @DisplayName("Оплата по карте c невалидным полем (владелец)")
    void shouldNoPayInvalidCardOwnerField() {
        val dashboardPage = new DashboardPage();
        String cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.expiredhMonthAndYear().getMonth();
        val year = dataHelper.expiredhMonthAndYear().getYear();
        val owner = dataHelper.generateNameRussian();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        dashboardPage.waitNotificationError();
        Assertions.assertNull(sqlHelper.getLastPaymentStatus());
    }

    @Test
    @DisplayName("Оплата по карте c невалидным полем (владелец более 50 символов)")
    void shouldNoPayInvalidOwnerFieldManyValues() {
        val dashboardPage = new DashboardPage();
        String cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.expiredhMonthAndYear().getMonth();
        val year = dataHelper.expiredhMonthAndYear().getYear();
        val owner = dataHelper.getOwner();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        dashboardPage.waitNotificationError();
        Assertions.assertNull(sqlHelper.getLastPaymentStatus());
    }


    @Test
    @DisplayName("Оплата по карте c невалидным полем CVV")
    void shouldNoPayInvalidCVVField() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.expiredhMonthAndYear().getMonth();
        val year = dataHelper.expiredhMonthAndYear().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.getField();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        form.assertErrorInvalidFormat(1);
        Assertions.assertNull(sqlHelper.getLastPaymentStatus());
    }

    @Test
    @DisplayName("Оплата по карте c пустым номером карты")
    void shouldNoPayEmptyCardNumberField() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getEmptyField();
        val month = dataHelper.expiredhMonthAndYear().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        form.assertErrorInvalidFormat(1);
        Assertions.assertNull(sqlHelper.getLastPaymentStatus());
    }

    @Test
    @DisplayName("Оплата по карте c пустым номером месяца")
    void shouldNoPayEmptyMonthField() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.getEmptyField();
        val year = dataHelper.expiredhMonthAndYear().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        form.assertErrorInvalidFormat(1);
        Assertions.assertNull(sqlHelper.getLastPaymentStatus());
    }

    @Test
    @DisplayName("Оплата по карте c пустым номером года")
    void shouldNoPayEmptyYearField() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.getEmptyField();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        form.assertErrorInvalidFormat(1);
        Assertions.assertNull(sqlHelper.getLastPaymentStatus());
    }


    @Test
    @DisplayName("Оплата по карте c пустым полем владелец")
    void shouldNoPayEmptyCardOwnerField() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.expiredhMonthAndYear().getYear();
        val owner = dataHelper.getEmptyField();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        form.assertErrorRequiredField(1);
        Assertions.assertNull(sqlHelper.getLastPaymentStatus());
    }


    @Test
    @DisplayName("Оплата по карте c пустым полем CVV")
    void shouldNoPayEmptyCVVField() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.expiredhMonthAndYear().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.getEmptyField();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        form.assertErrorInvalidFormat(1);
        Assertions.assertNull(sqlHelper.getLastPaymentStatus());
    }


    @Test
    @DisplayName("Оплата по активной карте, валидные данные, проверка записи в БД")
    void shouldPayByApprovedCardStatusInDB() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        dashboardPage.waitNotificationOk();
        Assertions.assertEquals(APPROVED, sqlHelper.getLastPaymentStatus());
    }

    @Test
    @DisplayName("Оплата по неактивной карте,валидные данные, проверка записи в БД")
    void shouldNoPayByDeclinedCardStatusInDB() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getDeclined();
        val month = dataHelper.expiredhMonthAndYear().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new PaymentCreditDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        dashboardPage.waitNotificationError();
        Assertions.assertEquals(DECLINED, sqlHelper.getLastPaymentStatus());
    }
}
