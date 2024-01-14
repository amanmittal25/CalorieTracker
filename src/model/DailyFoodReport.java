package model;

public class DailyFoodReport {
    FoodNutrients nutrients;
    FoodEntryMessages messages;

    public DailyFoodReport(FoodNutrients nutrients, FoodEntryMessages messages) {
        this.nutrients = nutrients;
        this.messages = messages;
    }

    public FoodNutrients getFoodNutrients() {
        return this.nutrients;
    }

    public MessageType getReportMessageType() {
        return this.messages.type;
    }
}
