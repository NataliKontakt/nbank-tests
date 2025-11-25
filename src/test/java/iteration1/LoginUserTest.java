package iteration1;

import generators.RandomData;
import models.CreateUserRequest;
import models.LoginUserRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequest;
import requests.AdminLoginUserRequest;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import static models.UserRole.USER;

public class LoginUserTest extends BaseTest{

    @Test
    public void adminCanGenerateAuthUserTest() {
        LoginUserRequest loginUserRequest = LoginUserRequest.builder()
                .username("admin")
                .password("admin")
                .build();
        new AdminLoginUserRequest(
                RequestSpecs.unauthSpec(),
                ResponseSpecs.requestReturnsOK())
                .post(loginUserRequest);

    }

    @Test
    public void userCanGenerateAuthTokenTest() {
        // создание пользователя

        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username(RandomData.getUsername())
                .password(RandomData.getPassword())
                .role(USER.toString())
                .build();

        new AdminCreateUserRequest(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityWasCreated())
                .post(userRequest);


        new AdminLoginUserRequest(RequestSpecs.unauthSpec(),
                ResponseSpecs.requestReturnsOK())
                .post(LoginUserRequest.builder().username(userRequest.getUsername()).password(userRequest.getPassword()).build())
                .header("Authorization", Matchers.notNullValue());
    }
}
