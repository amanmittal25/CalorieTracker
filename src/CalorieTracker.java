import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import model.DailyFoodReport;
import model.FoodEntryMessages;
import model.FoodNutrients;
import model.MessageType;
import model.NutritionGoal;
import model.WeightGoal;

public class CalorieTracker {
    NutritionGoal customerNutritionGoal;
    HashMap<Date, FoodNutrients> dailyFoodData = new HashMap<>();
    DataStoreInterface store;

    public CalorieTracker() {
        this.store = new DataStore();
    }

    public void setDataStore(DataStoreInterface store) {
        this.store = store;
    }

    public void setNutritionGoal(String weightGoalInput, float calorie, float protein, float carbs, float fat) {
        WeightGoal weightGoal = WeightGoal.WEIGHT_LOSS;
        if (weightGoalInput.equals("gain")) {
            weightGoal = WeightGoal.WEIGHT_GAIN; 
        } else if (weightGoalInput.equals("loss")) {
            weightGoal = WeightGoal.WEIGHT_LOSS;
        } else if (weightGoalInput.equals("maintain")) {
            weightGoal = WeightGoal.WEIGHT_MAINTAIN;
        } else {
            throw new RuntimeException("Incorrect goal set by the customer");
        }

        synchronized (this) {
            NutritionGoal goal = new NutritionGoal(weightGoal, new FoodNutrients(calorie, protein, carbs, fat));
            this.customerNutritionGoal = goal;
        }

        return;
    }

    public synchronized FoodEntryMessages foodEntry(Date date, String name) {
        FoodNutrients nutrients = this.store.fetchNutrients(name);
        return foodEntryInternal(date, nutrients);
    }

    private FoodEntryMessages foodEntryInternal(Date date, FoodNutrients nutrients) {
        FoodNutrients currentNutrients;
        if(!dailyFoodData.containsKey(date)) {
            currentNutrients = new FoodNutrients(0, 0, 0, 0);
        } else {
            currentNutrients = dailyFoodData.get(date);
        }

        if(this.customerNutritionGoal == null) {
            throw new RuntimeException("Customer has not set their nutrition goal");
        }

        FoodNutrients updatedNutrients = new FoodNutrients(currentNutrients.calorie + nutrients.calorie, 
        currentNutrients.protein + nutrients.protein,                                     
        currentNutrients.carbs + nutrients.carbs, 
        currentNutrients.fat + nutrients.fat);
        dailyFoodData.put(date, updatedNutrients);

        return constructMessageForEntry(updatedNutrients);
    }

    public DailyFoodReport getDailyReport(Date date) {
        if(!dailyFoodData.containsKey(date)) {
            return new DailyFoodReport(new FoodNutrients(0, 0, 0, 0), null);
        }

        return new DailyFoodReport(dailyFoodData.get(date), null);
    }

    private FoodEntryMessages constructMessageForEntry(FoodNutrients currFoodNutrients) {
        String messageString = "Going strong, keep it up, keep it healthy";
        MessageType type = MessageType.ENCOURAGEMENT;
        List<String> gonePastTheGoal = new ArrayList<>();
        if(this.customerNutritionGoal.getWeightGoal() != WeightGoal.WEIGHT_LOSS) {
            if(currFoodNutrients.calorie > this.customerNutritionGoal.getNutrients().calorie) {
                gonePastTheGoal.add("Calories");
            }

            if(currFoodNutrients.carbs > this.customerNutritionGoal.getNutrients().carbs) {
                gonePastTheGoal.add("Carbohydrates");
            }

            if(currFoodNutrients.fat > this.customerNutritionGoal.getNutrients().fat) {
                gonePastTheGoal.add("Fats");
            }

            if(currFoodNutrients.protein > this.customerNutritionGoal.getNutrients().protein) {
                gonePastTheGoal.add("Proteins");
            }

            if(gonePastTheGoal.size() > 0) {
                messageString = "Hey, you have taken more ";
                for (String macroNutrient : gonePastTheGoal) {
                    messageString = messageString + macroNutrient + ", ";
                }

                type = MessageType.ALERT;
            }
        }

        return new FoodEntryMessages(messageString, type);
    }
}
