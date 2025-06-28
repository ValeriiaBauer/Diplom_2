package models;

import java.util.List;
import java.util.Objects;

public class OrderCreateRequest {
    private List<String> ingredients;

    public OrderCreateRequest() {
    }

    public OrderCreateRequest(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderCreateRequest that = (OrderCreateRequest) o;
        return Objects.equals(ingredients, that.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredients);
    }

    @Override
    public String toString() {
        return "OrderCreateRequest{" +
                "ingredients=" + ingredients +
                '}';
    }
}