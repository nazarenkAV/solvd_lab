package com.zebrunner.automation.api.tcm.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Repository implements Cloneable {

    private String title;
    private Type type;
    private List<Suite> suites;

    public Repository() {
        suites = new ArrayList<>();
    }

    public Suite getSuiteByName(String name) {
        return suites.stream()
                .filter(project -> project.getSuiteName().equals(name))
                .findFirst().get();
    }

    @Override
    public Repository clone() throws CloneNotSupportedException {
        Repository repository = null;
        try {
            repository = (Repository) super.clone();
        } catch (CloneNotSupportedException e) {
            repository = new Repository();
            repository.setTitle(title);
            repository.setType(type);
        }
        repository.setSuites(Collections.unmodifiableList(suites));
        return repository;
    }

    enum Type {
        WEB, API
    }

}
