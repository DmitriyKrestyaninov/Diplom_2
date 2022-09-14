package diplom2;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestClient{
    private static final String ORDER_PATH = "/api/orders";

    @Step("Create new order {order}")
    public ValidatableResponse createOrder(Order order,String bearer) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(bearer)
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }
    @Step("Create order without authorization {order}")
    public ValidatableResponse createOrderWithoutAuthorization(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }
    @Step("Get list order {bearer}")
    public ValidatableResponse getlistOrders(String bearer) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(bearer)
                .when()
                .get(ORDER_PATH)
                .then();
    }
    @Step("Get list order without authorization")
    public ValidatableResponse getlistOrders() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDER_PATH)
                .then();
    }
}
