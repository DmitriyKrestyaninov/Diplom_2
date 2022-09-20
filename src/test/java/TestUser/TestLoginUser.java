package TestUser;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import diplom2.User;
import diplom2.UserClient;
import diplom2.UserCredentials;
import diplom2.UserGenerator;
import org.assertj.core.api.SoftAssertions;

import static org.apache.http.HttpStatus.*;
import static constants.TextOfMessagesResponse.*;

public class TestLoginUser {
    private User user;
    private UserClient userClient;
    private String bearerToken = "";

    @Before
    public void setUp() {
        user = UserGenerator.getDeffault();
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        if (!bearerToken.isEmpty()) {
            userClient.deleteUser(bearerToken);
        }
    }

    @Test
    @DisplayName("TEST login user /api/auth/login")
    @Description("Test checks ability to login user: status  code  '200'")
    public void testCheckLoginUser() {
        SoftAssertions softAssertion = new SoftAssertions();

        userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(UserCredentials.from(user));
        int statusCode = loginResponse.extract().statusCode();
        Assert.assertEquals("User not login", SC_OK, statusCode);

        boolean success = loginResponse.extract().path("success");
        softAssertion.assertThat(success).isTrue();

        String bearerTokenStr = loginResponse.extract().path("accessToken");
        softAssertion.assertThat(bearerTokenStr).isNotNull();

        bearerToken = bearerTokenStr.replace("Bearer ", "");

        String refreshToken = loginResponse.extract().path("refreshToken");
        softAssertion.assertThat(refreshToken).isNotNull();

        String email = loginResponse.extract().path("user.email");
        softAssertion.assertThat(email).isNotNull();
        softAssertion.assertThat(email).isEqualTo(user.getEmail());

        String name = loginResponse.extract().path("user.name");
        softAssertion.assertThat(name).isNotNull();
        softAssertion.assertThat(name).isEqualTo(user.getName());
        softAssertion.assertAll();
    }

    @Test
    @DisplayName("Test get error with not valid login")
    @Description("The test checks getting error an attempt log in with a non-existent login:" +
            "status code 401, message of response : 'email or password are incorrect'")
    public void testCheckGetErrorNotValidLogin() {
        ValidatableResponse loginResponse = userClient.loginUser(UserCredentials
                .from(UserGenerator.getNotExistsLogin()));
        int statusCode = loginResponse.extract().statusCode();
        Assert.assertEquals("User with incorrect login accepted", SC_UNAUTHORIZED, statusCode);

        String messageResponse = loginResponse.extract().path("message");
        Assert.assertEquals("Message on request with non-existent login not correct",
                MESS_LOG_WITH_INCORECT_LOGIN_PASSWORD, messageResponse);
    }

    @Test
    @DisplayName("Checking get error to login without login, email or password")
    @Description("The test checks getting error an attempt to log  without a username or password: status code '400'")
    public void testCheckGetErrorWithoutLoginOrPassword() {
        ValidatableResponse responseWithoutName = userClient.loginUser(UserCredentials
                .from(UserGenerator.getWithoutName()));
        ValidatableResponse responseWithoutPassword = userClient.loginUser(UserCredentials
                .from(UserGenerator.getWithoutEmail()));

        int statusCodeWithoutLogin = responseWithoutName.extract().statusCode();
        Assert.assertEquals("Status code for login without login_name incorrect", SC_UNAUTHORIZED, statusCodeWithoutLogin);

        String messageResponseWithoutLogin = responseWithoutName.extract().path("message");
        Assert.assertEquals(MESS_LOG_WITH_INCORECT_LOGIN_PASSWORD, messageResponseWithoutLogin);

        int statusCodeWithoutPassword = responseWithoutPassword.extract().statusCode();
        Assert.assertEquals("Status code for login without password incorrect", SC_UNAUTHORIZED, statusCodeWithoutPassword);

        String messageResponseWithoutPassword = responseWithoutPassword.extract().path("message");
        Assert.assertEquals(MESS_LOG_WITH_INCORECT_LOGIN_PASSWORD, messageResponseWithoutPassword);
    }
}
