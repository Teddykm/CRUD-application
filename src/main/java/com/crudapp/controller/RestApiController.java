package com.crudapp.controller;

import com.crudapp.model.User;
import com.crudapp.service.UserService;
import com.crudapp.util.CustomErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * This is for handling the REST API calls
 * coming from the AngularJS based frontend
 */
@RestController
@RequestMapping("/api")
public class RestApiController {

    public static final Logger LOG = LoggerFactory.getLogger(RestApiController.class);

    @Autowired
    UserService userService;

    @RequestMapping(value = "/user/", method = RequestMethod.GET)
    public ResponseEntity<List<User>> listAllUsers() {
        LOG.info("Fetching all users");
        List<User> users = userService.findAllUsers();
        if(users.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable("id") long id) {
        LOG.info("Fetching user with id {}", id);
        User user = userService.findById(id);
        if(user == null) {
            LOG.error("User with id {} not found", id);
            return new ResponseEntity(new CustomErrorType("User with id: " + id + " not found" ),
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
        LOG.info("Creating user: {}", user);

        if(userService.isUserExist(user)) {
            LOG.error("Unable to create. A user with name {} already exist", user.getName());
            return new ResponseEntity(new CustomErrorType(
                    "unable to create. A user with name " + user.getName() + " already exists"),
                    HttpStatus.CONFLICT);
        }

        userService.saveUser(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/user/{id}").buildAndExpand(user.getId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@PathVariable("id") long id, @RequestBody User user) {
        LOG.info("Updating user with id {}", id);

        User currentUser = userService.findById(id);

        if(currentUser == null) {
            LOG.error("Unable to update. User with id {} not found", id);
            return new ResponseEntity(new CustomErrorType(
                    "Unable to update. User with id "+ id + " not found"), HttpStatus.NOT_FOUND);
        }

        currentUser.setName(user.getName());
        currentUser.setAge(user.getAge());
        currentUser.setSalary(user.getSalary());

        userService.updateUser(currentUser);

        return new ResponseEntity<User>(currentUser, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        LOG.info("Fetching and deleting user with id {}", id);

        User user = userService.findById(id);

        if(user == null) {
            LOG.error("Unable to delete. User with id {} not found", id);
            return new ResponseEntity(new CustomErrorType(
                    "Unable to delete. User with id "+ id + " not found"), HttpStatus.NOT_FOUND);
        }

        userService.deleteUserById(id);

        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/user/", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAllUsers() {
        LOG.info("Deleing all users");

        userService.deleteAllUsers();
        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
    }
}
