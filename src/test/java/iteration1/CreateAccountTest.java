package iteration1;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class CreateAccountTest {
    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.filters(
                List.of(new ResponseLoggingFilter(),
                        new RequestLoggingFilter())
        );

    }

    @Test
    public void userCanCreateAccountTest() {
        // создание пользователя
        given()
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "username": "kate2004",
                          "password": "Kate2000#",
                          "role": "USER"
                        }
                        """)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED);
//Получаем токен
        String userAuthHeder = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "username": "kate2004",
                          "password": "Kate2000#"
                        }
                        """)
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .header("Authorization");

        //создаем аккаунт(счет)
        given()
                .header("Authorization", userAuthHeder)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .post("http://localhost:4111/api/v1/accounts")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED);
    }
    //Запросить все аккаунты пользователя и проверить, что наш аккаунт там
    //Параметризировать кейс на создание юзера с корректными данными
    //Параметризировать Password и роль в негативном тесте

}
