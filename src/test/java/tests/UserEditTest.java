package tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import models.UserCreateAndEditRequest;
import models.UserLoginRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserSteps;
import static org.hamcrest.CoreMatchers.equalTo;


public class UserEditTest {

    private static final String BASE_EMAIL = "test.user+%d@example.com";
    private static final String INITIAL_PASSWORD = "InitialPass123!";
    private static final String INITIAL_NAME = "Тестовый Пользователь";
    private static final String UPDATED_EMAIL = "updated.user+%d@example.com";
    private static final String UPDATED_PASSWORD = "UpdatedPass123!";
    private static final String UPDATED_NAME = "Обновленное Имя";
    private String currentEmail;
    private UserSteps userSteps;
    private boolean skipCleanup = false; // Добавляем недостающую переменную

    @Before
    public void setup() {
        userSteps = new UserSteps();
        currentEmail = String.format(BASE_EMAIL, System.currentTimeMillis());
    }

    @After
    public void cleanup() {
        if (!skipCleanup) { // Теперь переменная доступна
            try {
                userSteps.deleteUserAfterLogin(new UserLoginRequest(currentEmail, INITIAL_PASSWORD));
                userSteps.deleteUserAfterLogin(new UserLoginRequest(
                        String.format(UPDATED_EMAIL, System.currentTimeMillis()),
                        UPDATED_PASSWORD
                ));
            } catch (Exception e) {
                System.out.println("Ошибка при очистке: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("Обновление email авторизованного пользователя")
    @Description("Проверка успешного обновления email с авторизацией")
    public void shouldUpdateEmailWithAuthorization() {
        userSteps.createUser(new UserCreateAndEditRequest(
                currentEmail, INITIAL_PASSWORD, INITIAL_NAME
        ));
        String newEmail = String.format(UPDATED_EMAIL, System.currentTimeMillis());
        userSteps.editUserWithAuth(
                        new UserLoginRequest(currentEmail, INITIAL_PASSWORD),
                        new UserCreateAndEditRequest(newEmail, INITIAL_PASSWORD, INITIAL_NAME)
                )
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail.toLowerCase()));
    }

    @Test
    @DisplayName("Попытка обновления email без авторизации")
    @Description("Проверка отказа в обновлении без авторизации")
    public void shouldNotUpdateEmailWithoutAuthorization() {
        skipCleanup = true; // Используем переменную
        userSteps.createUser(new UserCreateAndEditRequest(
                currentEmail, INITIAL_PASSWORD, INITIAL_NAME
        ));
        userSteps.editUserWithoutAuth(new UserCreateAndEditRequest(
                        String.format(UPDATED_EMAIL, System.currentTimeMillis()),
                        INITIAL_PASSWORD,
                        INITIAL_NAME
                ))
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Обновление пароля авторизованного пользователя")
    @Description("Проверка успешного обновления пароля с последующей авторизацией")
    public void shouldUpdatePasswordWithAuthorization() {
        userSteps.createUser(new UserCreateAndEditRequest(
                currentEmail, INITIAL_PASSWORD, INITIAL_NAME
        ));
        userSteps.editUserWithAuth(
                        new UserLoginRequest(currentEmail, INITIAL_PASSWORD),
                        new UserCreateAndEditRequest(
                                currentEmail, UPDATED_PASSWORD, INITIAL_NAME
                        )
                )
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
        userSteps.login(new UserLoginRequest(currentEmail, UPDATED_PASSWORD))
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Обновление имени авторизованного пользователя")
    @Description("Проверка успешного обновления имени пользователя")
    public void shouldUpdateNameWithAuthorization() {
        userSteps.createUser(new UserCreateAndEditRequest(
                currentEmail, INITIAL_PASSWORD, INITIAL_NAME
        ));
        userSteps.editUserWithAuth(
                        new UserLoginRequest(currentEmail, INITIAL_PASSWORD),
                        new UserCreateAndEditRequest(
                                currentEmail, INITIAL_PASSWORD, UPDATED_NAME
                        )
                )
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.name", equalTo(UPDATED_NAME));
    }

    @Test
    @DisplayName("Попытка обновления данных несуществующего пользователя")
    @Description("Проверка обработки попытки обновления несуществующего пользователя")
    public void shouldNotUpdateNonExistingUser() {
        skipCleanup = true;
        userSteps.editUserWithAuth(
                        new UserLoginRequest("nonexisting@example.com", "password"),
                        new UserCreateAndEditRequest(
                                "new@example.com", "newpass", "New Name"
                        )
                )
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false));
    }
}