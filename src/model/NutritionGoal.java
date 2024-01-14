package model;

public class NutritionGoal {
    WeightGoal goal;
    FoodNutrients nutrients;

    public NutritionGoal(WeightGoal goal, FoodNutrients nutrients) {
        this.goal = goal;
        this.nutrients = nutrients;
    }

    public WeightGoal getWeightGoal() {
        return this.goal;
    }

    public FoodNutrients getNutrients() {
        return this.nutrients;
    }
}
