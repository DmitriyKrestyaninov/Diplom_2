package TestUser;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import diplom2.*;
import org.assertj.core.api.SoftAssertions;

import static constants.TextOfMessagesResponse.*;
import static org.apache.http.HttpStatus.*;

public class TestChangeUserInfo {
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
    @DisplayName("Test change information user /api/auth/user")
    @Description("The test checks the change of information about user: accepted code '200'")
    public void testChangeInformationWithAuthorization() {
        SoftAssertions softAssertion = new SoftAssertions();

        ValidatableResponse response = userClient.createUser(user);
        String bearerTokenStr = response.extract().path("accessToken");
        bearerToken = bearerTokenStr.replace("Bearer ", "");

        User userChange = UserGenerator.getChangedInfo();

        ValidatableResponse responseChangedInfo = userClient.changeUserInfoWithAuthorization(userChange, bearerToken);
        int statusCode = responseChangedInfo.extract().statusCode();
        Assert.assertEquals("Status code is incorrect", SC_OK, statusCode);

        boolean isChanged = responseChangedInfo.extract().path("success");
        softAssertion.assertThat(isChanged).isTrue();

        String email = responseChangedInfo.extract().path("user.email");
        softAssertion.assertThat(email).isNotNull();
        softAssertion.assertThat(email).isEqualTo(userChange.getEmail());

        String name = responseChangedInfo.extract().path("user.name");
        softAssertion.assertThat(name).isNotNull();
        softAssertion.assertThat(name).isEqualTo((userChange.getName()));

        softAssertion.assertAll();
    }

    @Test
    @DisplayName("Test change information user /api/auth/user")
    @Description("The test checks the change of information about user: accepted code '200'")
    public void testChangeInformationWithoutAuthorization() {
        ValidatableResponse response = userClient.createUser(user);
        String bearerTokenStr = response.extract().path("accessToken");
        bearerToken = bearerTokenStr.replace("Bearer ", "");

        User userChange = UserGenerator.getChangedInfo();
        ValidatableResponse responseChangedInfo = userClient.changeUserInfoWithoutAuthorization(userChange);
        int statusCode = responseChangedInfo.extract().statusCode();
        Assert.assertEquals("Status code is incorrect", SC_UNAUTHORIZED, statusCode);

        String messageResponse = responseChangedInfo.extract().path("message");
        Assert.assertEquals(MESS_LOG_CHANGE_INFO_WITHOUT_AUTH, messageResponse);
    }
}

