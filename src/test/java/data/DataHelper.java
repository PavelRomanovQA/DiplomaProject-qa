package data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class DataHelper {
    private static final String[] myMonthList = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private Faker faker;
    private LocalDate today = LocalDate.now();
    private DateTimeFormatter formatterYears = DateTimeFormatter.ofPattern("yy");
    private DateTimeFormatter formatterMonth = DateTimeFormatter.ofPattern("MM");

    public DataHelper() {
    }

    public CardNumber getCardNumber() {
        return new CardNumber("4444 4444 4444 4441", "4444 4444 4444 4442",  "4444 4444 4444 4444");
    }

    public Date randomMonthAndYearInFuture() {
        int n = (int) Math.floor(Math.random() * myMonthList.length);
        LocalDate newDate = today.plusYears(1);
        return new Date(formatterYears.format(newDate), myMonthList[n]);
    }

    public Date randomMonthAndYearInThePast() {
        int n = (int) Math.floor(Math.random() * myMonthList.length);
        LocalDate newDate = today.minusYears(6);
        return new Date(formatterYears.format(newDate), myMonthList[n]);
    }

    public Date randomMonthAndYearInThe() {
        int n = (int) Math.floor(Math.random() * myMonthList.length);
        LocalDate newDate = today.plusYears(6);
        return new Date(formatterYears.format(newDate), myMonthList[n]);
    }


    public Date expiredOneMonth() {
        LocalDate newDate = today.minusMonths(1);
        return new Date(formatterYears.format(newDate), formatterMonth.format(newDate));
    }

    public Date expiredhMonthAndYear() {
        LocalDate newDate = LocalDate.now();
        return new Date(formatterYears.format(newDate), formatterMonth.format(newDate));
    }

    public String generateName() {
        faker = new Faker(new Locale("en", "RU"));
        return faker.name().lastName() + " " + faker.name().firstName();
    }

    public String generateNameRussian() {
        faker = new Faker(new Locale("ru", "Russia"));
        return faker.name().lastName() + " " + faker.name().firstName();
    }

    public String generateRandomCode() {
        Random random = new Random();
        return Integer.toString(random.nextInt(900) + 100);
    }

    public String getEmptyField() {
        return "";
    }

    public String getAbbreviatedName() {
        return "A. Petrov";
    }

    public String getDoubleName() {
        return "Alex-Franc Ivanov";
    }

    public String getField() {
        return "00";
    }

    public String getOwner() {
        return "Ivan IvanovIvanovIvanovIvanovIvanovIvanovIvanovIvanovIvanovIvanovIvanovIvanovIvanovIvanovIvanovIvanovIvanov";
    }

    @Value
    public static class CardNumber {
        private String approved;
        private String declined;
        private String tooShort;
    }

    @Value
    public static class Date {
        private String year;
        private String month;
    }

}
