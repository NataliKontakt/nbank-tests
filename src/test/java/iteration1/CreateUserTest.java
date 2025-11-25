package iteration1;

import generators.RandomData;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.UserRole;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import requests.AdminCreateUserRequest;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class CreateUserTest extends BaseTest{

    @Test
    public void adminCanCreateUserWithCorrectData() {

        // создание объекта пользователя
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                        .username(RandomData.getUsername())
                        .password(RandomData.getPassword())
                        .role(UserRole.USER.toString())
                        .build();

        // создание пользователя
        CreateUserResponse createUserResponse = new AdminCreateUserRequest(RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(createUserRequest)
                .extract().as(CreateUserResponse.class);

        softly.assertThat(createUserRequest.getUsername()).isEqualTo(createUserResponse.getUsername());
        softly.assertThat(createUserRequest.getPassword()).isNotEqualTo(createUserResponse.getPassword());
        softly.assertThat(createUserRequest.getRole()).isEqualTo(createUserResponse.getRole());
    }

    public static Stream<Arguments> userInvalidData() {
        //username field validation
        return Stream.of(
                Arguments.of(" ", "Password23#", "USER", "username",
                        List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots",
                        "Username must be between 3 and 15 characters",
                        "Username cannot be blank"
                )),
                Arguments.of("ad", "Password23#", "USER", "username", List.of(
                        "Username must be between 3 and 15 characters"
                )),
                Arguments.of("ad1!", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1@", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1$", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1%", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1^", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1&", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1*", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1(", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1)", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1=", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1+", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                ))

        );
    }

    @MethodSource("userInvalidData")
    @ParameterizedTest
    public void adminCanNotCreateUserWithInvalidData(String username, String password, String role, String errorKey, List<String>  errorValue) {
        String requestBody = String.format("""
                {
                  "username": "%s",
                   "password": "%s",
                   "role": "%s"
                }
                """, username, password, role);
        // создание пользователя
        given()
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(requestBody)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(errorKey, Matchers.containsInAnyOrder(errorValue.toArray()));
    }
}
