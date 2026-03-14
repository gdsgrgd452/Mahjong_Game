package com.example.mahjong_game.validator;

import com.example.mahjong_game.model.User;
import com.example.mahjong_game.service.UserService;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class RegisterValidator implements Validator {

    private final UserService userService;
    public RegisterValidator(UserService userService) { //Initialises the user service
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) { //Checks the object is of the correct class type
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target; //Turns the target data into an instance of user

        //The following statements will return an error back if the conditions are met
        if (userService.findUserByUsername(user.getUsername()) != null) {  //The username is already in use
            errors.rejectValue("username", "", "This username is already taken");
        } else if (user.getUsername().isEmpty()) { //The username field is empty
            errors.rejectValue("username", "", "Please enter a username");
        }
        if (user.getPassword().isEmpty()) { //The password field is empty
            errors.rejectValue("password", "", "Please enter a password");
        }
        //else if (user.getPassword().length() < 10) { //The password is too short
//            errors.rejectValue("password", "", "This password is too short");
//        }
    }

}