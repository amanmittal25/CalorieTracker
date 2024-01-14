import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import model.DailyFoodReport;
import model.FoodEntryMessages;
import model.FoodNutrients;
import model.MessageType;

public class CalorieTrackerTest {
    CalorieTracker tracker;
    Date sampleDate;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    
    @Before
    public void setup() {
        this.tracker = new CalorieTracker();
        this.tracker.setDataStore(new TestDataStore());
        tracker.setNutritionGoal("gain", (float)2000.0, (float)50, (float)40, (float)30.5);
        this.sampleDate = new GregorianCalendar(2024, Calendar.JANUARY, 14).getTime();
    }

    @Test
    public void setNutritionGoalForWeightUnsupportedTest() {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Incorrect goal set by the customer");

        tracker.setNutritionGoal("something", 0, 0, 0, 0);
    }

    @Test
    public void setTwoFoodForADayTest() {
        tracker.foodEntry(sampleDate, "Pizza");
        tracker.foodEntry(sampleDate, "Pasta");

        DailyFoodReport actualDailyReport = tracker.getDailyReport(sampleDate);
        assertEquals(actualDailyReport.getFoodNutrients().calorie, 2000.0, 0);
        assertEquals(actualDailyReport.getFoodNutrients().carbs, 80, 0);
        assertEquals(actualDailyReport.getFoodNutrients().protein, 40.0, 0);
        assertEquals(actualDailyReport.getFoodNutrients().fat, 20, 0);
    }


    @Test
    public void getDailyReportWhenNoGoalSet() {
        CalorieTracker newTracker = new CalorieTracker();
        DailyFoodReport actualDailyReport = newTracker.getDailyReport(sampleDate);
        assertEquals(actualDailyReport.getFoodNutrients().calorie, 0, 0);
        assertEquals(actualDailyReport.getFoodNutrients().carbs, 0, 0);
        assertEquals(actualDailyReport.getFoodNutrients().protein, 0, 0);
        assertEquals(actualDailyReport.getFoodNutrients().fat, 0, 0);
    }

    @Test
    public void getFoodEntryTestMessagesWhenNutrientsOverEaten() {
        tracker.setNutritionGoal("gain", 2000f, (float)50, (float)40, (float)30.5);
        FoodEntryMessages message1 = tracker.foodEntry(sampleDate, "Pizza");
        FoodEntryMessages message2 = tracker.foodEntry(sampleDate, "Pasta");
        assertEquals(message1.message, "Going strong, keep it up, keep it healthy");
        assertEquals(message1.type, MessageType.ENCOURAGEMENT);
        
        assertEquals(message2.message, "Hey, you have taken more Carbohydrates, ");
    }

    public static class TestDataStore implements DataStoreInterface{

        @Override
        public FoodNutrients fetchNutrients(String name) {
            return new FoodNutrients(1000, 20, 40, 10);
        }
    }
}
