package api;

public class ApiEndpoint {
    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site/";

    public static class Auth {
        public static final String REGISTER = "api/auth/register";
        public static final String LOGIN = "api/auth/login";
        public static final String USER = "api/auth/user";
    }

    public static class Orders {
        public static final String ORDERS = "api/orders";
    }

    public static String getFullUrl(String endpoint) {
        return BASE_URL + endpoint;
    }
}