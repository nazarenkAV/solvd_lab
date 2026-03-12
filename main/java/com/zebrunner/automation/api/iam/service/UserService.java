package com.zebrunner.automation.api.iam.service;

import com.zebrunner.automation.api.iam.domain.User;

import java.util.List;
import java.util.Optional;

@Deprecated
public interface UserService {

    Optional<Integer> getUserId(String username);

    User create(User user);

    User getUserByUsername(String username);

    void deactivateUser(User user);

    int createAndGetId(User user);

    List<Integer> getAllUserIds();

    void deleteUserById(int userId);

    void addUserToGroup(int groupId, int userId);

    void deleteUserFromGroup(int groupId, int userId);

    User addRandomUserToTenant();

    User addRandomReadOnlyUserToTenant();

    User generateRandomUser();
}
