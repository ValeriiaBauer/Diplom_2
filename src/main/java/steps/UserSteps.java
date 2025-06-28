package steps;

import api.ApiEndpoint;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import models.UserCreateAndEditRequest;
import models.UserLoginRequest;
import models.UserLoginResponse;
import static io.restassured.RestAssured.given;


public class UserSteps {
    private static final String USER_CREATE_ENDPOINT = ApiEndpoint.Auth.REGISTER;
    private static final String USER_LOGIN_ENDPOINT = ApiEndpoint.Auth.LOGIN;
    private static final String USER_ENDPOINT = ApiEndpoint.Auth.USER;

    @Step("Создание нового пользователя")
    public ValidatableResponse createUser(UserCreateAndEditRequest userRequest) {
        return getAuthenticatedRequest()
                .body(userRequest)
                .when()
                .post(USER_CREATE_ENDPOINT)
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse login(UserLoginRequest loginRequest) {
        return getAuthenticatedRequest()
                .body(loginRequest)
                .when()
                .post(USER_LOGIN_ENDPOINT)
                .then();
    }

    @Step("Изменение данных пользователя без авторизации")
    public ValidatableResponse editUserWithoutAuth(UserCreateAndEditRequest userRequest) {
        return getAuthenticatedRequest()
                .body(userRequest)
                .when()
                .patch(USER_ENDPOINT)
                .then();
    }

    @Step("Изменение данных пользователя с авторизацией")
    public ValidatableResponse editUserWithAuth(UserLoginRequest loginRequest,
                                                UserCreateAndEditRequest userRequest) {
        String token = extractTokenFromLogin(loginRequest);
        return editUserWithToken(token, userRequest);
    }

    @Step("Изменение данных пользователя с токеном")
    public ValidatableResponse editUserWithToken(String token,
                                                 UserCreateAndEditRequest userRequest) {
        return getAuthenticatedRequest()
                .header("Authorization", token)
                .body(userRequest)
                .when()
                .patch(USER_ENDPOINT)
                .then();
    }

    @Step("Удаление пользователя с токеном")
    public void deleteUser(String token) {
        getAuthenticatedRequest()
                .header("Authorization", token)
                .when()
                .delete(USER_ENDPOINT)
                .then();
    }

    @Step("Удаление пользователя после авторизации")
    public void deleteUserAfterLogin(UserLoginRequest loginRequest) {
        String token = extractTokenFromLogin(loginRequest);
        deleteUser(token);
    }

    private String extractTokenFromLogin(UserLoginRequest loginRequest) {
        Response response = login(loginRequest).extract().response();
        return response.as(UserLoginResponse.class).getAccessToken();
    }

    private RequestSpecification getAuthenticatedRequest() {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .baseUri(ApiEndpoint.BASE_URL);
    }
}