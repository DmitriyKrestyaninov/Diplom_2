package diplom2;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends RestClient {
    private static final String USER_REGISTER_PATH = "/api/auth/register";
    private static final String USER_LOGIN_PATH = "/api/auth/login";
    private static final String USER_PATH = "/api/auth/user";

    @Step("Create new user {user}")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(USER_REGISTER_PATH)
                .then();
    }

    @Step("Login user {credentials}")
    public ValidatableResponse loginUser(UserCredentials credentials) {
        return given()
                .spec(getBaseSpec())
                .body(credentials)
                .when()
                .post(USER_LOGIN_PATH)
                .then();
    }

    @Step("Change user {credentials}")
    public ValidatableResponse changeUserInfoWithAuthorization(User user, String bearer) {
        return given()
                .spec(getBaseSpec())
                .auth()
                .oauth2(bearer)
                .body(user)
                .when()
                .patch(USER_PATH)
                .then();
    }

    @Step("Change user {credentials}")
    public ValidatableResponse changeUserInfoWithoutAuthorization(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .patch(USER_PATH)
                .then();
    }

    @Step("Delete user {credentials}")
    public ValidatableResponse deleteUser(String bearer) {
        return given()
                .spec(getBaseSpec())
                .auth()
                .oauth2(bearer)
                .and()
                .when()
                .delete(USER_PATH)
                .then();
    }
}

