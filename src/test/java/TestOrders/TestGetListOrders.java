package TestOrders;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import diplom2.*;
import org.assertj.core.api.SoftAssertions;

import java.util.ArrayList;
import java.util.Arrays;

import static constants.TextOfMessagesResponse.MESS_LOG_CHANGE_INFO_WITHOUT_AUTH;
import static org.apache.http.HttpStatus.*;

public class TestGetListOrders {
    private User user;
    private UserClient userClient;
    private String bearerToken = "";

    private Order order;
    private OrderClient orderClient;

    @Before
    public void setUp() {
        user = UserGenerator.getDeffault();
        userClient = new UserClient();
        order = OrderGenerator.getDefault();
        orderClient = new OrderClient();
        bearerToken = getAuthorizationToken(user);
        orderClient.createOrder(order, bearerToken);
    }

    @After
    public void tearDown() {
        if (!bearerToken.isEmpty()) {
            userClient.deleteUser(bearerToken);
        }
    }

    @Test
    @DisplayName("Test get list orders get /api/orders")
    @Description("Test checking the receipt of a list of user orders")
    public void testGetOrdersUser() {
        ValidatableResponse response = orderClient.getlistOrders(bearerToken);
        SoftAssertions softAssertion = new SoftAssertions();

        int statusCode = response.extract().statusCode();
        Assert.assertEquals("Status code is incorrect", SC_OK, statusCode);

        boolean success = response.extract().path("success");
        softAssertion.assertThat(success).isTrue();
        ArrayList<String> orders = response.extract().path("orders");
        softAssertion.assertThat(orders).isNotNull();
        softAssertion.assertAll();
    }

    @Test
    @DisplayName("Test get list orders without authorization")
    @Description("Test get error on request without authorization")
    public void testGetOrdersWithoutAuthorizaton() {
        ValidatableResponse response = orderClient.getlistOrders();

        int statusCode = response.extract().statusCode();
        Assert.assertEquals("Status code is incorrect", SC_UNAUTHORIZED, statusCode);

        String messageResponseWithoutPassword = response.extract().path("message");
        Assert.assertEquals(MESS_LOG_CHANGE_INFO_WITHOUT_AUTH, messageResponseWithoutPassword);
    }

    public String getAuthorizationToken(User user) {
        ValidatableResponse response = userClient.createUser(user);
        String bearerTokenStr = response.extract().path("accessToken");
        return bearerTokenStr.replace("Bearer ", "");
    }
}
