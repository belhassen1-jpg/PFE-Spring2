package com.example.pidev.Interfaces;

import com.example.pidev.Entities.User;

import java.util.List;

public interface IUserService {

    List<User> retrieveAllUsers();

    List<User> getLastFiveUsers();

    User addUser(User user);

    User updateUser (User user);

    User updateUser2(Long id, User user);

    User retrieveUser (Long IdUser);

    void deleteUser( Long IdUser);

    User retrieveUserByUsername(String Username);

    public User registerUser(User user);

    User retrieveUserByPhone(Long Phone);



}
