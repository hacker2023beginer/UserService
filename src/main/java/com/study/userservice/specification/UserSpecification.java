package com.study.userservice.specification;

import com.study.userservice.entity.User;
import org.springframework.data.jpa.domain.Specification;


public class UserSpecification {

    public static Specification<User> hasName(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.equal(root.get("name"), name);
    }

    public static Specification<User> hasSurname(String surname) {
        return (root, query, cb) ->
                surname == null ? null : cb.equal(root.get("surname"), surname);
    }
}
