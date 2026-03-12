package com.zebrunner.automation.api.iam.service;

import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.iam.domain.request.v1.UpdateUserRequest;
import com.zebrunner.automation.api.iam.method.v1.DeleteUserByIdV1Method;
import com.zebrunner.automation.api.iam.method.v1.DeleteUserFromGroupV1Method;
import com.zebrunner.automation.api.iam.method.v1.GetUserByCriteriaV1Method;
import com.zebrunner.automation.api.iam.method.v1.GetUserByUsernameV1Method;
import com.zebrunner.automation.api.iam.method.v1.PatchUserV1Method;
import com.zebrunner.automation.api.iam.method.v1.PostUserMethodV1;
import com.zebrunner.automation.api.iam.method.v1.PutAddUserToGroupV1Method;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public Optional<Integer> getUserId(String username) {
        GetUserByUsernameV1Method getUserByUsernameV1Method = new GetUserByUsernameV1Method(username);
        Response response = getUserByUsernameV1Method.callAPI();
        if (response.getStatusCode() == 200) {
            String response1 = response.asString();
            return Optional.of(JsonPath.from(response1).getInt("id"));
        }
        return Optional.empty();
    }

    @Override
    public User create(User user) {
        user.setGroupId(3L);//Users group
        PostUserMethodV1 postUserMethodV1 = new PostUserMethodV1(user);
        postUserMethodV1.getRequest().expect().statusCode(HttpStatus.SC_CREATED);
        String response = postUserMethodV1.callAPI().asString();
        return JsonPath.from(response).getObject("", User.class);
    }

    @Override
    public User getUserByUsername(String username) {
        GetUserByUsernameV1Method getUserByUsernameV1Method = new GetUserByUsernameV1Method(username);
        getUserByUsernameV1Method.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String rs = getUserByUsernameV1Method.callAPI().asString();
        return JsonPath.from(rs).getObject("", User.class);
    }

    @Override
    public void deactivateUser(User user) {
        UpdateUserRequest updateUserRequest = UpdateUserRequest.with(UpdateUserRequest.Status.INACTIVE);

        PatchUserV1Method patchUserV1Method = new PatchUserV1Method(user.getId(), updateUserRequest);
        patchUserV1Method.getRequest().expect().statusCode(HttpStatus.SC_NO_CONTENT);
        patchUserV1Method.callAPI();
    }

    @Override
    public int createAndGetId(User user) {
        user.setGroupId(3L);//Users group
        PostUserMethodV1 postUserMethodV1 = new PostUserMethodV1(user);
        postUserMethodV1.getRequest().expect().statusCode(HttpStatus.SC_CREATED);
        String response = postUserMethodV1.callAPI().asString();
        return JsonPath.from(response).getInt("id");
    }

    @Override
    public List<Integer> getAllUserIds() {
        GetUserByCriteriaV1Method getUserByCriteriaV1Method = new GetUserByCriteriaV1Method("", "");
        getUserByCriteriaV1Method.getRequest().expect().statusCode(HttpStatus.SC_OK);
        String rs = getUserByCriteriaV1Method.callAPI().asString();
        List<Integer> userIds = JsonPath.from(rs).getList("results.id");
        userIds.sort(Comparator.naturalOrder());
        LOGGER.info("Existing userIds: " + userIds);
        return userIds;
    }

    @Override
    public void deleteUserById(int userId) {
        DeleteUserByIdV1Method getUserByCriteriaV1Method = new DeleteUserByIdV1Method(userId);
        getUserByCriteriaV1Method.callAPI();
    }

    @Override
    public void addUserToGroup(int groupId, int userId) {
        PutAddUserToGroupV1Method putAddUserToGroupV1Method = new PutAddUserToGroupV1Method(groupId, userId);
        putAddUserToGroupV1Method.callAPI();
        putAddUserToGroupV1Method.getRequest().expect().statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Override
    public void deleteUserFromGroup(int groupId, int userId) {
        DeleteUserFromGroupV1Method deleteUserFromGroupV1Method = new DeleteUserFromGroupV1Method(groupId, userId);
        deleteUserFromGroupV1Method.callAPI();
    }

    @Override
    public User addRandomUserToTenant() {
        User user = User.builder()
                .username(RandomStringUtils.randomAlphabetic(6).concat(".user"))
                .email(RandomStringUtils.randomAlphabetic(6).concat("@gfggf.com"))
                .password(RandomStringUtils.randomAlphabetic(15))
                .groupId(3L)
                .build();

        PostUserMethodV1 postUserMethodV1 = new PostUserMethodV1(user);
        postUserMethodV1.getRequest().expect().statusCode(HttpStatus.SC_CREATED);
        String response = postUserMethodV1.callAPI().asString();

        User randomUser = JsonPath.from(response).getObject("", User.class);
        randomUser.setPassword(user.getPassword());

        return randomUser;
    }

    @Override
    public User addRandomReadOnlyUserToTenant() {
        User user = User.builder()
                .username(RandomStringUtils.randomAlphabetic(6).concat("_user"))
                .email(RandomStringUtils.randomAlphabetic(6).concat("@gfggf.com"))
                .password(RandomStringUtils.randomAlphabetic(6))
                .hasReadOnlyAccess(true)
                .build();

        PostUserMethodV1 postUserMethodV1 = new PostUserMethodV1(user);
        postUserMethodV1.getRequest().expect().statusCode(HttpStatus.SC_CREATED);
        String response = postUserMethodV1.callAPI().asString();

        User createdUser = JsonPath.from(response).getObject("", User.class);
        createdUser.setPassword(user.getPassword());

        return createdUser;
    }

    @Override
    public User generateRandomUser() {
        return User.builder()
                .username(RandomStringUtils.randomAlphabetic(6).concat(".user"))
                .email(RandomStringUtils.randomAlphabetic(6).concat("@gfggf.com"))
                .password(RandomStringUtils.randomAlphabetic(10))
                .firstName(RandomStringUtils.randomAlphabetic(5))
                .lastName(RandomStringUtils.randomAlphabetic(5))
                .build();
    }
}
