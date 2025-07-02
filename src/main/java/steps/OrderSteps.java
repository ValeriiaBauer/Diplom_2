package steps;
import api.ApiEndpoint;
import io.qameta.allure.Step;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import models.OrderCreateRequest;
import models.UserCreateAndEditRequest;
import models.UserLoginRequest;
import models.UserLoginResponse;
import static io.restassured.RestAssured.given;


public class OrderSteps {
    private static final String ORDERS_ENDPOINT = ApiEndpoint.Orders.ORDERS;
    private final UserSteps userSteps = new UserSteps();

    @Step("Создание нового заказа без авторизации")
    public ValidatableResponse createOrderWithoutAuth(OrderCreateRequest orderCreateRequest) {
        return given()
                .spec(getRequestSpecification())
                .body(orderCreateRequest)
                .when()
                .post(ORDERS_ENDPOINT)
                .then();
    }

    @Step("Создание нового заказа с авторизацией")
    public ValidatableResponse createOrderWithAuth(UserLoginRequest userLoginRequest,
                                                   OrderCreateRequest orderCreateRequest) {
        String accessToken = getAccessToken(userLoginRequest);

        return given()
                .spec(getRequestSpecification())
                .header("Authorization", accessToken)
                .body(orderCreateRequest)
                .when()
                .post(ORDERS_ENDPOINT)
                .then();
    }

    @Step("Получение списка заказов без авторизации")
    public ValidatableResponse getOrdersWithoutAuth() {
        return given()
                .spec(getRequestSpecification())
                .when()
                .get(ORDERS_ENDPOINT)
                .then();
    }

    @Step("Получение списка заказов с авторизацией")
    public ValidatableResponse getOrdersWithAuth(UserLoginRequest userLoginRequest) {
        String accessToken = getAccessToken(userLoginRequest);

        return given()
                .spec(getRequestSpecification())
                .header("Authorization", accessToken)
                .when()
                .get(ORDERS_ENDPOINT)
                .then();
    }

    private String getAccessToken(UserLoginRequest userLoginRequest) {
        Response loginResponse = userSteps.login(userLoginRequest)
                .extract()
                .response();

        return loginResponse.as(UserLoginResponse.class)
                .getAccessToken();
    }

    private RequestSpecification getRequestSpecification() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(ApiEndpoint.BASE_URL)
                .build();
    }
}