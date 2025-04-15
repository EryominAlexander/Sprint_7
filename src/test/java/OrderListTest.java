import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertTrue;
import io.qameta.allure.Step;

public class OrderListTest {
    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private static final String GET_API_V1_ORDERS = "/api/v1/orders";

    @Before
    public void set(){
        RestAssured.baseURI = BASE_URI;
    }

    @Test
    public void orderListTest(){
        Response response = sendGetOrders();
        checkResponseGetOrders(response);
    }
    @Test
    public void checkListInOrderListTest(){
        OrderListData orderListData = getResponseBodyFromOrders();
        checkResponseList(orderListData);
    }

    @Step("Отправка запроса POST /api/v1/orders")
    public Response sendGetOrders(){
        return given().header("Content-Type", "application/json").get(GET_API_V1_ORDERS);
    }
    @Step("Проверка тела ответа POST /api/v1/orders")
    public void checkResponseGetOrders(Response response){
        response.then().assertThat().body("orders", notNullValue())
                .and().statusCode(200);
    }

    @Step("Получение тела ответа POST /api/v1/orders")
    public OrderListData getResponseBodyFromOrders(){
        return given()
                .header("Content-Type", "application/json")
                .get(GET_API_V1_ORDERS)
                .body()
                .as(OrderListData.class);
    }
    @Step("Проверка списка заказов в ответе POST /api/v1/orders")
    public void checkResponseList(OrderListData orderListData){
        List<OrderData> orderList = orderListData.getOrders();
        assertTrue( orderList.size() > 1 );
    }
}
