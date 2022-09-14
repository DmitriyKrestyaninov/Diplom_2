package TestUser;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import diplom2.*;

import static constants.TextOfMessagesResponse.MESS_REG_SAME_LOGIN;
import static constants.TextOfMessagesResponse.MESS_REG_WITHOUT_LOG_PASS_EMAIL;
import static org.apache.http.HttpStatus.*;

public class TestCreateUser {
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
    @DisplayName("Test create user /api/auth/register")
    @Description("The test checks the creation of a user: accepted code '200'," +
            " the body of the response when the user is successfully created 'ok', " +
            "the ability to login in to verify the creation of the user")
    public void testCreateUser() {
        ValidatableResponse response = userClient.createUser(user);
        int statusCode = response.extract().statusCode();
        Assert.assertEquals("Status code is incorrect", SC_OK, statusCode);

        boolean isCreated = response.extract().path("success");
        Assert.assertTrue("User is not created", isCreated);

        String bearerTokenStr = response.extract().path("accessToken");
        Assert.assertNotNull("The response does not contain accesToken", bearerTokenStr);

        bearerToken = bearerTokenStr.replace("Bearer ", "");

        String refreshToken = response.extract().path("refreshToken");
        Assert.assertNotNull("The response does not contain refreshToken", refreshToken);

        String email = response.extract().path("user.email");
        Assert.assertNotNull("The response does not contain email", email);
        Assert.assertEquals("The email in response and email in request not to match",user.getEmail(),email);

        String name = response.extract().path("user.name");
        Assert.assertNotNull("The response does not contain name", name);
        Assert.assertEquals("The name in response and name in request not to match",user.getName(),name);

        ValidatableResponse loginResponse = userClient.loginUser(UserCredentials.from(user));
        boolean success = loginResponse.extract().path("success");
        Assert.assertTrue("Such user not exists", success);
    }

    @Test
    @DisplayName("Test the impossibility of creating two identical users")
    @Description("The test checks that it is impossible to create two users with the same login: " +
            "status code '403', message of response: 'User already exists")
    public void testCheckCreateSameUser() {
        userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(UserCredentials.from(user));
        String bearerTokenStr = loginResponse.extract().path("accessToken");
        bearerToken = bearerTokenStr.replace("Bearer ", "");

        ValidatableResponse responseSameUser = userClient.createUser(user);
        int statusCode = responseSameUser.extract().statusCode();
        Assert.assertEquals("The same user is created", SC_FORBIDDEN, statusCode);

        String messageResponse = responseSameUser.extract().path("message");
        Assert.assertEquals(MESS_REG_SAME_LOGIN, messageResponse);
    }

    @Test
    @DisplayName("Test get error on attempt to create user without  name, email or password")
    @Description("The test check that if you try to create a user without a name, email,  or password, " +
            "an error with the status 403 and the body of the response will come in response:'Email, password and name are required fields'")
    public void testCheckGetErrorCreateUserWithoutLoginPasswordEmail() {
        ValidatableResponse responseWithoutName = userClient.createUser(UserGenerator.getWithoutName());
        ValidatableResponse responseWithoutEmail = userClient.createUser(UserGenerator.getWithoutEmail());

        int statusCodeWithoutLogin = responseWithoutName.extract().statusCode();
        Assert.assertEquals("Status code for request without login incorrect", SC_FORBIDDEN, statusCodeWithoutLogin);

        String messageResponseWithoutLogin = responseWithoutName.extract().path("message");
        Assert.assertEquals(MESS_REG_WITHOUT_LOG_PASS_EMAIL, messageResponseWithoutLogin);

        int statusCodeWithoutPassword = responseWithoutEmail.extract().statusCode();
        Assert.assertEquals("Status code for request without passward incorrect", SC_FORBIDDEN, statusCodeWithoutPassword);

        String messageResponseWithoutPassword = responseWithoutEmail.extract().path("message");
        Assert.assertEquals(MESS_REG_WITHOUT_LOG_PASS_EMAIL, messageResponseWithoutPassword);
    }
}

