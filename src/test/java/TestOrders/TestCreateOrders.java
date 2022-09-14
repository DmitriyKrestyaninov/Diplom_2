package TestOrders;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import diplom2.*;

import static constants.TextOfMessagesResponse.MESS_CREATE_ORDER_WITHOUT_INGREDIENTS;
import static org.apache.http.HttpStatus.*;

public class TestCreateOrders {
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
    }

    @After
    public void tearDown() {
        if (!bearerToken.isEmpty()) {
            userClient.deleteUser(bearerToken);
        }
    }

    @Test
    @DisplayName("Test create order /api/orders")
    @Description("Test checks to ability create order of burger")
    public void testCreateOrder() {
        ValidatableResponse responseOrder = orderClient.createOrder(order,bearerToken);

        int statusCode = responseOrder.extract().statusCode();
        Assert.assertEquals("Status code is incorrect",SC_OK, statusCode);

        boolean success = responseOrder.extract().path("success");
        Assert.assertTrue("Such order not exists",success);

        String name = responseOrder.extract().path("name");
        Assert.assertNotNull("TC name", name);

        int orderNumber = responseOrder.extract().path("order.number");
        Assert.assertNotNull("The response does not contain order number", orderNumber);
    }

    @Test
    @DisplayName("Test create order without ingredients")
    @Description("Test check get error on request without ingredients")
    public void testCreateOrderWithoutIngredients(){
        ValidatableResponse responseOrder = orderClient.createOrder(OrderGenerator.getOrderWithoutOngredients(),bearerToken);

        int  statusCode = responseOrder.extract().statusCode();
        Assert.assertEquals("Status code is incorrect",SC_BAD_REQUEST,statusCode);

        boolean success = responseOrder.extract().path("success");
        Assert.assertFalse("Field 'success' is incorrect",success);

        String messageResponse = responseOrder.extract().path("message");
        Assert.assertEquals("The request message without ingredients is incorrect",MESS_CREATE_ORDER_WITHOUT_INGREDIENTS,messageResponse);
    }
    @Test
    @DisplayName("Test create order with incorrect hashcode ingredients")
    @Description("Test check get error on request with incorrect hashcode ingredients")
    public void testCreateOrderWithIncorrectHashCodeIngredients(){
        ValidatableResponse responseOrder = orderClient.createOrder(OrderGenerator.getOrderWithIncorrectHashCodeIngredients(),bearerToken);

        int  statusCode = responseOrder.extract().statusCode();
        Assert.assertEquals("Status code is incorrect",SC_INTERNAL_SERVER_ERROR,statusCode);
    }

    @Test
    @DisplayName("Test create order without authorization")
    public void testCreateOrderWithoutAuthorization(){
        ValidatableResponse responseOrder = orderClient.createOrderWithoutAuthorization(order);
        int statusCode = responseOrder.extract().statusCode();
        Assert.assertEquals("Status code is incorrect",SC_OK, statusCode);

        boolean success = responseOrder.extract().path("success");
        Assert.assertTrue("Such order not exists",success);

        String name = responseOrder.extract().path("name");
        Assert.assertNotNull("The response does not contain name", name);

        int orderNumber = responseOrder.extract().path("order.number");
        Assert.assertNotNull("The response does not contain order number", orderNumber);
    }

    public String getAuthorizationToken(User user) {
        ValidatableResponse response = userClient.createUser(user);
        String bearerTokenStr = response.extract().path("accessToken");
        return bearerTokenStr.replace("Bearer ", "");
    }
}
