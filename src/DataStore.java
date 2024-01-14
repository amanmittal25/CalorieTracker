import model.FoodNutrients;

public class DataStore implements DataStoreInterface{
    public FoodNutrients fetchNutrients(String name) {
        return new FoodNutrients(1250, 10, 15, 30);
    }
}
