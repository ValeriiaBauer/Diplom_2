package tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import models.OrderCreateRequest;
import models.UserCreateAndEditRequest;
import models.UserLoginRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.OrderSteps;
import steps.UserSteps;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;

public class OrderCreateTest {

    private static final String TEST_EMAIL = "test.user+" + System.currentTimeMillis() + "@example.com";
    private static final String TEST_PASSWORD = "TestPassword123";
    private static final String TEST_NAME = "Тестовый Пользователь";
    private static final String VALID_INGREDIENT = "61c0c5a71d1f82001bdaaa6d";
    private static final String INVALID_INGREDIENT = "wrongIngredient123";

    private final List<String> ingredients = new ArrayList<>();
    private boolean skipCleanup = false;
    private UserSteps userSteps;
    private OrderSteps orderSteps;

    @Before
    public void setUp() {
        userSteps = new UserSteps();
        orderSteps = new OrderSteps();
        userSteps.createUser(new UserCreateAndEditRequest(TEST_EMAIL, TEST_PASSWORD, TEST_NAME));
    }

    @After
    public void cleanup() {
        if (!skipCleanup) {
            deleteTestUser();
            ingredients.clear();
        }
    }

    private void deleteTestUser() {
        try {
            UserLoginRequest loginRequest = new UserLoginRequest(TEST_EMAIL, TEST_PASSWORD);
            userSteps.deleteUserAfterLogin(loginRequest);
        } catch (Exception e) {
            System.out.println("Failed to delete test user: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Создание заказа после авторизации")
    @Description("Проверка возможности создания заказа авторизованным пользователем")
    public void orderCreateWithAuthorization() {
        ingredients.add(VALID_INGREDIENT);
        OrderCreateRequest orderRequest = new OrderCreateRequest(ingredients);

        ValidatableResponse response = orderSteps
                .createOrderWithAuth(new UserLoginRequest(TEST_EMAIL, TEST_PASSWORD), orderRequest);

        response.assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.owner.email", equalTo(TEST_EMAIL));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка возможности создания заказа неавторизованным пользователем")
    public void orderCreateWithoutAuthorization() {
        ingredients.add(VALID_INGREDIENT);
        OrderCreateRequest orderRequest = new OrderCreateRequest(ingredients);

        ValidatableResponse response = orderSteps.createOrderWithoutAuth(orderRequest);

        response.assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", isA(Integer.class));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка обработки попытки создания заказа без ингредиентов")
    public void orderCreateWithoutIngredients() {
        skipCleanup = true;

        ValidatableResponse response = orderSteps
                .createOrderWithAuth(new UserLoginRequest(TEST_EMAIL, TEST_PASSWORD),
                        new OrderCreateRequest(ingredients));

        response.assertThat()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным ингредиентом")
    @Description("Проверка обработки невалидного ингредиента")
    public void orderCreateWithInvalidIngredient() {
        ingredients.add(VALID_INGREDIENT);
        ingredients.add(INVALID_INGREDIENT);

        ValidatableResponse response = orderSteps
                .createOrderWithAuth(new UserLoginRequest(TEST_EMAIL, TEST_PASSWORD),
                        new OrderCreateRequest(ingredients));

        response.assertThat().statusCode(500);
    }
}