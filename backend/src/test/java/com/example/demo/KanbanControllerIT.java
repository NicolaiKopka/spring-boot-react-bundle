package com.example.demo;

import com.example.demo.login.LoginData;
import com.example.demo.login.LoginResponse;
import com.example.demo.users.MyUser;
import com.example.demo.users.MyUserRepo;
import com.example.demo.users.RegisterData;
import com.example.demo.users.UserDTO;
import io.jsonwebtoken.Header;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.MultiValueMap;

import java.sql.SQLOutput;
import java.util.List;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class KanbanControllerIT {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private MyUserRepo myUserRepo;

    @AfterEach
    void clearDB() {
        myUserRepo.deleteAll();
    }

    @Test
    @Order(0)
    void shouldRegisterAndLoginUser() {
        //register User
        RegisterData newUser = new RegisterData("testUser", "password", "password");
        ResponseEntity<UserDTO> registerResponse = restTemplate.postForEntity("/api/user", newUser, UserDTO.class);
        Assertions.assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //login user with wrong credentials
        LoginData wrongLoginUser = new LoginData("testUser", "wrongPassword");

        ResponseEntity<LoginResponse> failedLoginResponse = restTemplate.postForEntity("/api/login", wrongLoginUser, LoginResponse.class);
        Assertions.assertThat(failedLoginResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Assertions.assertThat(failedLoginResponse.getBody()).isNull();

        //login user
        // TODO ask why cant login with register user data from above?
        LoginData loginUser = new LoginData("testUser", "password");

        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity("/api/login", loginUser, LoginResponse.class);
        String token = loginResponse.getBody().getToken();
        Assertions.assertThat(loginResponse.getBody().getToken()).isNotBlank();
    }
    @Test
    @Order(1)
    void shouldAddItemsAndReturnListOfAllItemsWithAdminRights() {
        //register User
        RegisterData newUser = new RegisterData("testUser", "password", "password");
        ResponseEntity<UserDTO> registerResponse = restTemplate.postForEntity("/api/user", newUser, UserDTO.class);
        MyUser user = myUserRepo.findByUsername("testUser").orElseThrow();
        user.setRoles(List.of("admin"));
        myUserRepo.save(user);

        //login user
        LoginData loginUser = new LoginData("testUser", "password");

        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity("/api/login", loginUser, LoginResponse.class);
        String token = loginResponse.getBody().getToken();

        //add items
        Item item1 = new Item("Project1", "Project1 description", StatusEnum.OPEN);
        Item item2 = new Item("Project2", "Project2 description", StatusEnum.OPEN);

        ResponseEntity<Item> postResponse1 = restTemplate.exchange("/api/kanban",
                HttpMethod.POST,
                new HttpEntity<>(item1, createHeader(token)),
                Item.class);
        Assertions.assertThat(postResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Item> postResponse2 = restTemplate.exchange("/api/kanban",
                HttpMethod.POST,
                new HttpEntity<>(item2, createHeader(token)),
                Item.class);
        Assertions.assertThat(postResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Item[]> getAllResponse = restTemplate.exchange("/api/admin/all",
                HttpMethod.GET,
                new HttpEntity<>(createHeader(token)),
                Item[].class);

        Assertions.assertThat(getAllResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(getAllResponse.getBody().length).isEqualTo(2);
    }

    @Test
    @Order(2)
    void userIsRegisteredAndLoggedInAndDoesAddMoveEditAndDeleteMethods() {
        //register 2 Users
        RegisterData newUser1 = new RegisterData("testUser", "password", "password");
        ResponseEntity<UserDTO> registerResponse = restTemplate.postForEntity("/api/user", newUser1, UserDTO.class);

        RegisterData newUser2 = new RegisterData("testUser2", "password", "password");
        ResponseEntity<UserDTO> registerResponse2 = restTemplate.postForEntity("/api/user", newUser2, UserDTO.class);

        //login 2 users
        LoginData loginUser1 = new LoginData("testUser", "password");

        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity("/api/login", loginUser1, LoginResponse.class);
        String tokenUser1 = loginResponse.getBody().getToken();

        LoginData loginUser2 = new LoginData("testUser2", "password");

        ResponseEntity<LoginResponse> loginResponse2 = restTemplate.postForEntity("/api/login", loginUser2, LoginResponse.class);
        String tokenUser2 = loginResponse2.getBody().getToken();

        //add items
        //added by user 1
        Item item1 = new Item("Project1", "Project1 description", StatusEnum.OPEN);
        ResponseEntity<Item> postResponse1 = restTemplate.exchange("/api/kanban",
                HttpMethod.POST,
                new HttpEntity<>(item1, createHeader(tokenUser1)),
                Item.class);
        Assertions.assertThat(postResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Item item2 = new Item("Project2", "Project2 description", StatusEnum.OPEN);
        ResponseEntity<Item> postResponse2 = restTemplate.exchange("/api/kanban",
                HttpMethod.POST,
                new HttpEntity<>(item2, createHeader(tokenUser1)),
                Item.class);
        Assertions.assertThat(postResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //added by user2
        Item item3 = new Item("Project3", "Project3 description", StatusEnum.OPEN);
        ResponseEntity<Item> postResponse3 = restTemplate.exchange("/api/kanban",
                HttpMethod.POST,
                new HttpEntity<>(item3, createHeader(tokenUser2)),
                Item.class);
        Assertions.assertThat(postResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        //from here only user1 continues and should have only 2 elements to work with
        //get all user1 items
        ResponseEntity<Item[]> getAllResponse = restTemplate.exchange("/api/kanban",
                HttpMethod.GET,
                new HttpEntity<>(createHeader(tokenUser1)),
                Item[].class);

        Assertions.assertThat(getAllResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(getAllResponse.getBody().length).isEqualTo(2);

        //current working item, get by id
        ResponseEntity<Item> getItemResponse = restTemplate.exchange("/api/kanban/" + postResponse1.getBody().getId(),
                HttpMethod.GET,
                new HttpEntity<>(createHeader(tokenUser1)),
                Item.class);
        Item workingItem = getItemResponse.getBody();

        //move item1 to In_Progress
        ResponseEntity<Item> moveToNextResponse = restTemplate.exchange("/api/kanban/next",
                HttpMethod.PUT,
                new HttpEntity<>(workingItem, createHeader(tokenUser1)),
                Item.class);
        Assertions.assertThat(moveToNextResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(moveToNextResponse.getBody().getStatus()).isEqualTo(StatusEnum.IN_PROGRESS);

        //move item1 back to Open
        ResponseEntity<Item> moveToPrevResponse = restTemplate.exchange("/api/kanban/prev",
                HttpMethod.PUT,
                new HttpEntity<>(workingItem, createHeader(tokenUser1)),
                Item.class);
        Assertions.assertThat(moveToPrevResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(moveToPrevResponse.getBody().getStatus()).isEqualTo(StatusEnum.OPEN);

        //edit item1
        workingItem.setTask("editedTask");
        workingItem.setDescription("editedDescription");
        ResponseEntity<Item> editResponse = restTemplate.exchange("/api/kanban",
                HttpMethod.PUT,
                new HttpEntity<>(workingItem, createHeader(tokenUser1)),
                Item.class);
        Assertions.assertThat(editResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(editResponse.getBody().getTask()).isEqualTo("editedTask");
        Assertions.assertThat(editResponse.getBody().getDescription()).isEqualTo("editedDescription");

        //delete item1
        ResponseEntity<Item> deleteResponse = restTemplate.exchange("/api/kanban/" + workingItem.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(createHeader(tokenUser1)),
                Item.class);
        Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        //get all user1 items, should only be item2
        ResponseEntity<Item[]> getAllResponseAfterDelete = restTemplate.exchange("/api/kanban",
                HttpMethod.GET,
                new HttpEntity<>(createHeader(tokenUser1)),
                Item[].class);
        Assertions.assertThat(getAllResponseAfterDelete.getBody().length).isEqualTo(1);
        Assertions.assertThat(getAllResponseAfterDelete.getBody()[0]).isEqualTo(postResponse2.getBody());
    }
    private HttpHeaders createHeader(String token) {
        String authValue = "Bearer " + token;
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", authValue);
        return header;
    }

}