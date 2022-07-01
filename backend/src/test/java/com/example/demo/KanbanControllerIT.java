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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
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
//    @Order(0)
    void shouldRegisterAndLoginUser() {
        // TODO fails
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
        MyUser newUser = new MyUser("testUser", "password", "password");
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
    void userIsRegisteredAndLoggedInAndDoesAddMoveEditAndDeleteMethods() {
        //register 2 Users
        MyUser newUser1 = new MyUser("testUser", "password", "password");
        ResponseEntity<UserDTO> registerResponse = restTemplate.postForEntity("/api/user", newUser1, UserDTO.class);

        MyUser newUser2 = new MyUser("testUser2", "password", "password");
        ResponseEntity<UserDTO> registerResponse2 = restTemplate.postForEntity("/api/user", newUser2, UserDTO.class);

        //login 2 users
        LoginData loginUser1 = new LoginData("testUser", "password");

        ResponseEntity<LoginResponse> loginResponse = restTemplate.postForEntity("/api/login", loginUser1, LoginResponse.class);
        String tokenUser1 = loginResponse.getBody().getToken();

        LoginData loginUser2 = new LoginData("testUser2", "password");

        ResponseEntity<LoginResponse> loginResponse2 = restTemplate.postForEntity("/api/login", loginUser2, LoginResponse.class);
        String tokenUser2 = loginResponse2.getBody().getToken();

        //add items
        Item item1 = new Item("Project1", "Project1 description", StatusEnum.OPEN);
        Item item2 = new Item("Project2", "Project2 description", StatusEnum.OPEN);
        Item item3 = new Item("Project3", "Project3 description", StatusEnum.OPEN);

        ResponseEntity<Item> postResponse1 = restTemplate.exchange("/api/kanban",
                HttpMethod.POST,
                new HttpEntity<>(item3, createHeader(tokenUser2)),
                Item.class);
        Assertions.assertThat(postResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Item> postResponse2 = restTemplate.exchange("/api/kanban",
                HttpMethod.POST,
                new HttpEntity<>(item1, createHeader(tokenUser1)),
                Item.class);
        Assertions.assertThat(postResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Item> postResponse3 = restTemplate.exchange("/api/kanban",
                HttpMethod.POST,
                new HttpEntity<>(item2, createHeader(tokenUser1)),
                Item.class);
        Assertions.assertThat(postResponse3.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Item[]> getAllResponse = restTemplate.exchange("/api/kanban",
                HttpMethod.GET,
                new HttpEntity<>(createHeader(tokenUser1)),
                Item[].class);

        Assertions.assertThat(getAllResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(getAllResponse.getBody().length).isEqualTo(2);
    }

    @Test
    void shouldChangeStatusOfItemAndReturnCorrectNewStatusPrev() {
        Item item1 = new Item("Project1", "Project1 description", StatusEnum.OPEN);
        ResponseEntity<Void> response1 = restTemplate.postForEntity("/api/kanban", item1, Void.class);
        Assertions.assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);

        Item initialItem = Objects.requireNonNull(restTemplate.getForEntity("/api/kanban", Item[].class).getBody())[0];

        restTemplate.put("/api/kanban/next", initialItem, Void.class);

        ResponseEntity<Item[]> resultListProgress = restTemplate.getForEntity("/api/kanban", Item[].class);
        Assertions.assertThat(resultListProgress.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(Objects.requireNonNull(resultListProgress.getBody())[0].getStatus()).isEqualTo(StatusEnum.IN_PROGRESS);

        restTemplate.put("/api/kanban/prev", initialItem, Void.class);

        ResponseEntity<Item[]> resultListDone = restTemplate.getForEntity("/api/kanban", Item[].class);
        Assertions.assertThat(resultListDone.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(Objects.requireNonNull(resultListDone.getBody())[0].getStatus()).isEqualTo(StatusEnum.OPEN);
    }

    @Test
    void shouldAddItemsReturnCorrectItemByIdAndEditThisItem() {
        Item item1 = new Item("Project1", "Project1 description", StatusEnum.OPEN);
        Item item2 = new Item("Project2", "Project2 description", StatusEnum.OPEN);
        restTemplate.postForEntity("/api/kanban", item1, Void.class);
        restTemplate.postForEntity("/api/kanban", item2, Void.class);

        Item initialItem = Objects.requireNonNull(restTemplate.getForEntity("/api/kanban", Item[].class).getBody())[0];

        ResponseEntity<Item> resultItem = restTemplate.getForEntity("/api/kanban/" + initialItem.getId(), Item.class);
        Assertions.assertThat(resultItem.getStatusCode()).isEqualTo(HttpStatus.OK);

        Objects.requireNonNull(resultItem.getBody()).setTask("Project1Edited");

        restTemplate.put("/api/kanban", resultItem, Void.class);

        ResponseEntity<Item> editedItem = restTemplate.getForEntity("/api/kanban/" + initialItem.getId(), Item.class);
        Assertions.assertThat(editedItem.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(Objects.requireNonNull(editedItem.getBody()).getTask()).isEqualTo("Project1Edited");
    }

    @Test
    void shouldAddAndDeleteItems() {
        Item item1 = new Item("Project1", "Project1 description", StatusEnum.OPEN);
        Item item2 = new Item("Project2", "Project2 description", StatusEnum.OPEN);
        restTemplate.postForEntity("/api/kanban", item1, Void.class);
        restTemplate.postForEntity("/api/kanban", item2, Void.class);

        ResponseEntity<Item[]> resultListFull = restTemplate.getForEntity("/api/kanban", Item[].class);

        Assertions.assertThat(resultListFull.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(resultListFull.getBody().length).isEqualTo(2);

        Item initialItem = Objects.requireNonNull(restTemplate.getForEntity("/api/kanban", Item[].class).getBody())[0];
        Item initialItemPos2 = Objects.requireNonNull(restTemplate.getForEntity("/api/kanban", Item[].class).getBody())[1];

        restTemplate.delete("/api/kanban/" + initialItem.getId());

        ResponseEntity<Item[]> resultList = restTemplate.getForEntity("/api/kanban", Item[].class);

        Assertions.assertThat(resultList.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(resultList.getBody()).contains(initialItemPos2);

    }

    @Test
    void shouldGetMultipleNotFoundForDifferentMethods() {

        ResponseEntity<Item[]> result = restTemplate.getForEntity("/api/kanban/unknown", Item[].class);
        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    private HttpHeaders createHeader(String token) {
        String authValue = "Bearer " + token;
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", authValue);
        return header;
    }

}