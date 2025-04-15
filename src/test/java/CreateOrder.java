import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrder{
    private final String colorBlack;
    private final String colorGrey;

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private static final String POST_API_V1_ORDERS = "/api/v1/orders";

    public CreateOrder(String colorBlack, String colorGrey){
        this.colorBlack = colorBlack;
        this.colorGrey = colorGrey;
    }

    @Parameterized.Parameters
    public static Object[][] getData(){
        return new Object[][]{
                { "BLACK", null },
                { null, "GREY" },
                { "BLACK", "GREY" },
                { null, null }
        };
    }
    @Before
    public void set(){
        RestAssured.baseURI = BASE_URI;
    }
    @Test
    public void createOrder(){

        List<String> colorList = new ArrayList<>();
        String[] colors = new String[2];
        int i = 0;
        if ( colorBlack != null ){
            colorList.add(colorBlack);
        }
        if ( colorGrey != null ){
            colorList.add(colorGrey);
        }
        OrderData orderData = new OrderData(
                "firstName" + RandomStringUtils.randomAlphabetic(10),
                "secondName" + RandomStringUtils.randomAlphabetic(10),
                "address" + RandomStringUtils.randomAlphabetic(10),
                "metro" + RandomStringUtils.randomAlphabetic(10),
                "89998887766",
                5,
                "5",
                "deliveryDate" + RandomStringUtils.randomAlphabetic(10),
                colorList
        );

        Response response =  sendPostOrder(orderData);
        checkResponsePostOrder(response);
    }
    @Step("Отправка запроса POST /api/v1/orders")
    public Response sendPostOrder(OrderData orderData ){
        return given()
                .header("Content-Type", "application/json")
                .when()
                .body(orderData)
                .and()
                .post(POST_API_V1_ORDERS);
    }
    @Step("Проверка тела ответа POST /api/v1/orders")
    public void checkResponsePostOrder(Response response){
        response.then().assertThat().body("track", notNullValue()).and().statusCode(201);
    }
}
