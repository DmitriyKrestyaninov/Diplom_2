package diplom2;

import java.util.Arrays;

public class OrderGenerator {
    public static Order getDefault(){
        return  new Order(Arrays.asList("61c0c5a71d1f82001bdaaa6d","61c0c5a71d1f82001bdaaa6f","61c0c5a71d1f82001bdaaa75"));
    }
    public static Order getOrderWithoutOngredients(){
        return  new Order();
    }
    public static Order getOrderWithIncorrectHashCodeIngredients(){
        return new Order(Arrays.asList("666666666665555555555fff","777777777778888888888a6f","111111111112222222222a75"));
    }
}
