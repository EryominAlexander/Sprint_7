import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CourierLoginTest {
    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private static final String POST_API_V1_COURIER = "/api/v1/courier";
    private static final String POST_API_V1_LOGIN = "/api/v1/courier/login";
    private static final String DELETE_API_V1_COURIER = "/api/v1/courier";

    private final String courierPassword = "TestPassword";
    private final String courierFirstName = "TestFirstName";
    private String courierLogin;
    private CourierId courierId;

    @Before
    public void setUp(){
        RestAssured.baseURI = BASE_URI;
        courierLogin = "TestLogin" + RandomStringUtils.randomAlphabetic(10);
    }
    @Test
    public void createCourier(){

        CourierData courier = new CourierData(courierLogin, courierPassword, courierFirstName);

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(POST_API_V1_COURIER);

        CourierAuth courierAuth = new CourierAuth( courier.getLogin(), courier.getPassword());

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(courierAuth)
                .when()
                .post(POST_API_V1_LOGIN)
                .then().assertThat().body("id", notNullValue())
                .and().statusCode(200);



        courierId = given()
                .header("Content-Type", "application/json")
                .and()
                .body(courierAuth)
                .when()
                .post(POST_API_V1_LOGIN)
                .as(CourierId.class);

        given().delete(DELETE_API_V1_COURIER + courierId.getId());
    }
    @Test
    public void negativeAuthorizationWithWrongLogin(){
        CourierData courier = new CourierData(courierLogin, courierPassword, courierFirstName);
        given()
                .header("Content-Type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(POST_API_V1_COURIER);

        CourierAuth courierAuth = new CourierAuth(courier.getLogin() + "test", courier.getPassword());
        given()
                .header("Content-Type", "application/json")
                .and()
                .body(courierAuth)
                .when()
                .post(POST_API_V1_LOGIN)
                .then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and().assertThat().body("code", equalTo(404))
                .and().statusCode(404);

        courierId = given()
                .header("Content-Type", "application/json")
                .and()
                .body(courierAuth)
                .when()
                .post(POST_API_V1_LOGIN)
                .as(CourierId.class);

        given().delete(DELETE_API_V1_COURIER + courierId.getId());
    }
    @Test
    public void negativeNonExistentCourier(){

        CourierAuth courierAuth = new CourierAuth(RandomStringUtils.randomAlphabetic(10) + courierLogin + RandomStringUtils.randomAlphabetic(10), courierPassword);
        given()
                .header("Content-Type", "application/json")
                .and()
                .body(courierAuth)
                .when()
                .post(POST_API_V1_LOGIN)
                .then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and().assertThat().body("code", equalTo(404))
                .and().statusCode(404);
    }
}
