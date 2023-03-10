package it.unipi.lsmsd.coding_qa.controller;

import it.unipi.lsmsd.coding_qa.dto.*;
import it.unipi.lsmsd.coding_qa.service.ServiceLocator;
import it.unipi.lsmsd.coding_qa.service.UserService;
import it.unipi.lsmsd.coding_qa.service.exception.BusinessException;
import it.unipi.lsmsd.coding_qa.utils.Constants;
import it.unipi.lsmsd.coding_qa.view.MainView;
import it.unipi.lsmsd.coding_qa.view.QuestionView;
import it.unipi.lsmsd.coding_qa.view.UserView;

public class UserController {
    private static UserService userService = ServiceLocator.getUserService();
    private static UserView userView = new UserView();
    private static QuestionView questionView = new QuestionView();
    private static MainView mainView = new MainView();

    public static void openSelfProfile() {
        try {
            UserDTO loggedUser = AuthenticationController.getLoggedUser();
            mainView.showMessage("########################################### YOUR PROFILE ###########################################");
            mainView.view(loggedUser);
            do {
                switch (userView.selfUserProfileMenu()) {
                    case 1: // browse your questions
                        QuestionController.browseCreatedOrAnsweredQuestions(loggedUser.getNickname(), true);
                        break;
                    case 2: // browse your answered questions
                        QuestionController.browseCreatedOrAnsweredQuestions(loggedUser.getNickname(), false);
                        break;
                    case 3: // update your profile
                        updateYourProfile();
                        break;
                    case 4: // browse followed user
                        browseFollowedUser();
                        break;
                    case 5: // go back
                        return;
                }
            } while (true);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    private static void browseFollowedUser() {
        try {
            int page = 1;
            do {
                UserDTO loggedUser = AuthenticationController.getLoggedUser();
                PageDTO<String> pageDTO = userService.getFollowerList(loggedUser.getNickname(), page);
                if (pageDTO.getCounter() == 0) return;
                switch (userView.browseFollowedUsers(pageDTO)) {
                    case 1: // choose a user to view
                        int number = mainView.inputMessageWithPaging("Specify the user number", pageDTO.getCounter());
                        openProfile(pageDTO.getEntries().get(number - 1));
                        break;
                    case 2: // go to the next page
                        if (pageDTO.getCounter() == Constants.PAGE_SIZE)
                            page++;
                        else
                            mainView.showMessage("!!!! THIS IS THE LAST PAGE !!!!");
                        break;
                    case 3: // go to the previous page
                        if (page > 1)
                            page--;
                        else
                            mainView.showMessage("!!!! THIS IS THE FIRST PAGE !!!!");
                        break;
                    case 4:
                        return;
                }
            } while (true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void updateYourProfile() {
        try {
            UserDTO userDTO = AuthenticationController.getLoggedUser();
            UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();

            userRegistrationDTO.setNickname(userDTO.getNickname());
            userRegistrationDTO.setFullName(userDTO.getFullName());
            userRegistrationDTO.setCountry(userDTO.getCountry());
            userRegistrationDTO.setEncPassword("");
            userRegistrationDTO.setWebsite(userDTO.getWebsite());

            userView.updateProfile(userRegistrationDTO);
            userService.updateInfo(userRegistrationDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void openProfileIfAdmin(UserDTO userDTO) {
        try {
            mainView.view(userDTO);
            switch (userView.adminUserProfile()) {
                case 1: // delete user
                    userService.delete(userDTO.getNickname());
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void openUserProfile(UserDTO userDTO) {
        try {
            UserDTO loggedUser = AuthenticationController.getLoggedUser();
            mainView.view(userDTO);
            if (loggedUser == null) {
                userView.waitInputInUserProfileNotLogged();
                return;
            }
            switch (userView.otherUserProfileMenu()) {
                case 1: // follow user
                    userService.follow(loggedUser.getNickname(), userDTO.getNickname());
                    break;
                case 2: // unfollow user
                    userService.unfollow(loggedUser.getNickname(), userDTO.getNickname());
                    break;
                case 3: // browse created q
                    QuestionController.browseCreatedOrAnsweredQuestions(userDTO.getNickname(), true);
                    break;
                case 4: // browse answered q
                    QuestionController.browseCreatedOrAnsweredQuestions(userDTO.getNickname(), false);
                    break;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void openProfile(String nickname) throws BusinessException {
        UserDTO userDTO = userService.getInfo(nickname);
        if (userDTO == null) {
            mainView.showMessage("!!!! THE USER HAS BEEN DELETED BY THE ADMIN !!!!");
            return;
        }
        if (AuthenticationController.getLoggedUser() != null && AuthenticationController.getLoggedUserNickname().equals("admin")) // admin
            openProfileIfAdmin(userDTO);
        else if (AuthenticationController.getLoggedUser() != null && nickname.equals(AuthenticationController.getLoggedUserNickname())) // self profile
            openSelfProfile();
        else // other user profile
            openUserProfile(userDTO);
    }
}
