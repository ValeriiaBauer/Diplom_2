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
import static org.hamcrest.CoreMatchers.notNullValue;


public class UserLoginTest {
    private static final String BASE_EMAIL = "test.user+%d@example.com"; // Базовый шаблон email
    private static final String VALID_PASSWORD = "SecurePass123!"; // Валидный пароль
    private static final String USER_NAME = "Тестовый Пользователь"; // Имя пользователя
    private static final String WRONG_EMAIL = "wrong.email@example.com"; // Неверный email
    private static final String WRONG_PASSWORD = "WrongPass123!"; // Неверный пароль
    private String currentEmail; // Текущий email для теста
    private UserSteps userSteps; // Экземпляр шагов для работы с пользователем

    @Before
    public void setUp() {
        userSteps = new UserSteps();
        currentEmail = String.format(BASE_EMAIL, System.currentTimeMillis());
        userSteps.createUser(new UserCreateAndEditRequest(
                currentEmail, VALID_PASSWORD, USER_NAME
        ));
    }

    @After
    public void cleanUp() {
        // Очистка после каждого теста
        try {
            userSteps.deleteUserAfterLogin(new UserLoginRequest(currentEmail, VALID_PASSWORD));
        } catch (Exception e) {
            System.out.println("Ошибка при очистке: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Успешный логин с валидными данными")
    @Description("Проверка успешной авторизации с корректными email и паролем")
    public void shouldLoginSuccessfullyWithValidCredentials() {
        userSteps.login(new UserLoginRequest(currentEmail, VALID_PASSWORD))
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Логин с неверным email")
    @Description("Проверка ошибки при авторизации с некорректным email")
    public void shouldFailLoginWithIncorrectEmail() {
        userSteps.login(new UserLoginRequest(WRONG_EMAIL, VALID_PASSWORD))
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email или password некорректны"));
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверка ошибки при авторизации с некорректным паролем")
    public void shouldFailLoginWithIncorrectPassword() {
        userSteps.login(new UserLoginRequest(currentEmail, WRONG_PASSWORD))
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email или password некорректны"));
    }

    @Test
    @DisplayName("Логин с пустым email")
    @Description("Проверка ошибки при авторизации с пустым email")
    public void shouldFailLoginWithEmptyEmail() {
        userSteps.login(new UserLoginRequest("", VALID_PASSWORD))
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email или password некорректны"));
    }

    @Test
    @DisplayName("Логин с пустым паролем")
    @Description("Проверка ошибки при авторизации с пустым паролем")
    public void shouldFailLoginWithEmptyPassword() {
        userSteps.login(new UserLoginRequest(currentEmail, ""))
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email или password некорректны"));
    }

    @Test
    @DisplayName("Логин несуществующего пользователя")
    @Description("Проверка ошибки при авторизации несуществующего пользователя")
    public void shouldFailLoginForNonExistingUser() {
        String nonExistingEmail = "nonexisting.user@example.com";
        userSteps.login(new UserLoginRequest(nonExistingEmail, VALID_PASSWORD))
                .assertThat()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email или password некорректны"));
    }
}