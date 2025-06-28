package tests;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import models.UserCreateAndEditRequest;
import models.UserLoginRequest;
import org.junit.After;
import org.junit.Test;
import steps.OrderSteps;
import steps.UserSteps;
import java.util.List;
import static org.hamcrest.CoreMatchers.*;


public class OrderListTest {

    private static final String BASE_EMAIL = "test.user+%d@example.com";
    private static final String VALID_PASSWORD = "SecurePass123!";
    private static final String USER_NAME = "Тестовый Пользователь";
    private String currentEmail;
    private boolean skipCleanup = false;

    @After
    public void cleanup() {
        if (!skipCleanup && currentEmail != null) {
            deleteTestUser(currentEmail);
        }
    }

    private void deleteTestUser(String email) {
        try {
            UserSteps userSteps = new UserSteps();
            userSteps.deleteUserAfterLogin(new UserLoginRequest(email, VALID_PASSWORD));
        } catch (Exception e) {
            System.out.println("Ошибка при удалении пользователя: " + e.getMessage());
        }
    }

    private String generateUniqueEmail() {
        currentEmail = String.format(BASE_EMAIL, System.currentTimeMillis());
        return currentEmail;
    }

    @Test
    @DisplayName("Получение списка заказов без авторизации")
    @Description("Проверка невозможности получения списка заказов без авторизации")
    public void getOrdersWithoutAuthorizationShouldFail() {
        skipCleanup = true;
        OrderSteps orderSteps = new OrderSteps();
        orderSteps.getOrdersWithoutAuth()
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Получение списка заказов с авторизацией")
    @Description("Проверка успешного получения списка заказов авторизованного пользователя")
    public void getOrdersWithAuthorizationShouldSucceed() {
        // Создаем тестового пользователя
        String email = generateUniqueEmail();
        UserSteps userSteps = new UserSteps();
        userSteps.createUser(new UserCreateAndEditRequest(email, VALID_PASSWORD, USER_NAME));
        OrderSteps orderSteps = new OrderSteps();
        orderSteps.getOrdersWithAuth(new UserLoginRequest(email, VALID_PASSWORD))
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", instanceOf(List.class));
    }
}