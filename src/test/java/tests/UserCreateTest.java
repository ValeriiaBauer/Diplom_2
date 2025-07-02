package tests;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import models.UserCreateAndEditRequest;
import models.UserLoginRequest;
import org.junit.After;
import org.junit.Test;
import steps.UserSteps;
import static org.hamcrest.CoreMatchers.equalTo;


public class UserCreateTest {

    private static final String BASE_EMAIL = "test.user+%d@example.com";
    private static final String TEST_PASSWORD = "SecurePass123!";
    private static final String TEST_NAME = "Test User";

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
            userSteps.deleteUserAfterLogin(new UserLoginRequest(email, TEST_PASSWORD));
        } catch (Exception e) {
            System.out.println("Ошибка при удалении тестового пользователя: " + e.getMessage());
        }
    }

    private String generateUniqueEmail() {
        currentEmail = String.format(BASE_EMAIL, System.currentTimeMillis());
        return currentEmail;
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка успешного создания нового пользователя с валидными данными")
    public void shouldCreateUniqueUserSuccessfully() {
        String email = generateUniqueEmail();
        UserCreateAndEditRequest userRequest = new UserCreateAndEditRequest(email, TEST_PASSWORD, TEST_NAME);

        new UserSteps().createUser(userRequest)
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email.toLowerCase()))
                .body("user.name", equalTo(TEST_NAME));
    }

    @Test
    @DisplayName("Попытка создания дубликата пользователя")
    @Description("Проверка обработки попытки регистрации уже существующего пользователя")
    public void shouldNotCreateDuplicateUser() {
        String email = generateUniqueEmail();
        UserSteps userSteps = new UserSteps();
        UserCreateAndEditRequest userRequest = new UserCreateAndEditRequest(email, TEST_PASSWORD, TEST_NAME);

        userSteps.createUser(userRequest)
                .assertThat()
                .statusCode(200);

        userSteps.createUser(userRequest)
                .assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Проверка валидации при отсутствии email")
    public void shouldNotCreateUserWithoutEmail() {
        skipCleanup = true;

        new UserSteps().createUser(new UserCreateAndEditRequest(null, TEST_PASSWORD, TEST_NAME))
                .assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Проверка валидации при отсутствии пароля")
    public void shouldNotCreateUserWithoutPassword() {
        skipCleanup = true;

        new UserSteps().createUser(new UserCreateAndEditRequest(generateUniqueEmail(), null, TEST_NAME))
                .assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Проверка валидации при отсутствии имени")
    public void shouldNotCreateUserWithoutName() {
        skipCleanup = true;

        new UserSteps().createUser(new UserCreateAndEditRequest(generateUniqueEmail(), TEST_PASSWORD, null))
                .assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя с некорректным email")
    @Description("Проверка валидации при неверном формате email")
    public void shouldNotCreateUserWithInvalidEmail() {
        skipCleanup = true;

        new UserSteps().createUser(new UserCreateAndEditRequest("invalid-email", TEST_PASSWORD, TEST_NAME))
                .assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Invalid email format"));
    }
}