import model.FoodNutrients;

public interface DataStoreInterface {
    public FoodNutrients fetchNutrients(String name);
}
