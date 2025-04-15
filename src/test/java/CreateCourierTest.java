import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateCourierTest {
    private final String courierPassword = "TestPassword";
    private final String courierFirstName = "TestFirstName";
    private String courierLogin;
    private CourierId courierId;

    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private static final String POST_API_V1_COURIER = "/api/v1/courier";
    private static final String POST_API_V1_LOGIN = "/api/v1/courier/login";
    private static final String DELETE_API_V1_COURIER = "/api/v1/courier";

    @Before
    public void setUp(){
        RestAssured.baseURI = BASE_URI;
        courierLogin = "TestLogin" + RandomStringUtils.randomAlphabetic(10);
    }
    @Test
    public void createCourier(){

        CourierData courier = new CourierData(courierLogin, courierPassword, courierFirstName);

        Response response = postCreateCourier(courier);
        checkResponseCreateCourier(response);

        CourierAuth courierAuth = new CourierAuth(courier.getLogin(), courier.getPassword());
        courierId = given()
                .header("Content-Type", "application/json")
                .and()
                .body(courierAuth)
                .when()
                .post(POST_API_V1_LOGIN)
               .as(CourierId.class);
    }

    @Test
    public void negativeCreateTwoCouriers(){

        CourierData courier = new CourierData(courierLogin, courierPassword, courierFirstName);
        Response response = postCreateCourier(courier);
        checkResponseCreateCourier(response);
        Response negativeResponse = postCreateCourier(courier);
        checkNegativeResponseCreateCourier(negativeResponse);

        CourierAuth courierAuth = new CourierAuth(courier.getLogin(), courier.getPassword());
        courierId = given()
                .header("Content-Type", "application/json")
                .and()
                .body(courierAuth)
                .when()
                .post(POST_API_V1_LOGIN)
                .as(CourierId.class);
    }

    @After
    public void clearing(){
        given().delete(DELETE_API_V1_COURIER + courierId.getId());
    }

    @Step("Создание курьера POST /api/v1/courier")
    public Response postCreateCourier(CourierData courier){
        return given()
                .header("Content-Type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(POST_API_V1_COURIER);
    }
    @Step("Проверка ответа POST /api/v1/courier")
    public void checkResponseCreateCourier(Response response){
        response.then().assertThat().body("ok", equalTo(true))
                .and().statusCode(201);
    }
    @Step("Проверка негативного ответа POST /api/v1/courier")
    public void checkNegativeResponseCreateCourier(Response response){
        response.then().assertThat().body("code",equalTo(409))
                .and().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой."))
                .and().statusCode(409);
    }
}
