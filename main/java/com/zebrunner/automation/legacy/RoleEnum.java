package com.zebrunner.automation.legacy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated
@Getter
@RequiredArgsConstructor
public enum RoleEnum {

    ADMINISTRATOR("Administrator", 0, "Admins can do most things, like update project settings and add other admins."),
    MANAGER("Manager", 1, "Managers can add people to the project, and collaborate on all work."),
    ENGINEER("Engineer", 2, "Engineers are part of the team and can collaborate on all work."),
    GUEST("Guest", 3, "Guests can search through, view, and comment on your team's work, but not much else.");

    private final String name;
    private final int accessLvl;
    private final String description;

    public static List<String> getNames() {
        return Arrays.stream(RoleEnum.values())
                     .map(RoleEnum::getName)
                     .collect(Collectors.toList());
    }

    public static List<String> getDescriptions() {
        return Arrays.stream(RoleEnum.values())
                     .map(RoleEnum::getDescription)
                     .collect(Collectors.toList());
    }

}
