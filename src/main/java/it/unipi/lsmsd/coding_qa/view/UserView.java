package it.unipi.lsmsd.coding_qa.view;

import it.unipi.lsmsd.coding_qa.controller.AuthenticationController;
import it.unipi.lsmsd.coding_qa.dto.PageDTO;
import it.unipi.lsmsd.coding_qa.dto.UserRegistrationDTO;
import it.unipi.lsmsd.coding_qa.utils.Constants;

import java.util.Arrays;
import java.util.Scanner;

public class UserView {
    private Scanner scanner = new Scanner(System.in);

    public int otherUserProfileMenu(){
        System.out.println("######################################## USER PROFILE MENU #########################################");
        int choice;
        do {
            System.out.println("\t[1] Follow user");
            System.out.println("\t[2] Unfollow user");
            System.out.println("\t[3] Browse user's created questions");
            System.out.println("\t[4] Browse user's answered questions");
            System.out.println("\t[5] Exit");
            System.out.println("Input: ");
            choice = Integer.parseInt(scanner.nextLine());
        } while (choice < 1 || choice > 5);
        if (choice == 5)
            System.out.println("############################################### EXIT ###############################################");
        return choice;
    }

    public int selfUserProfileMenu(){
        System.out.println("######################################### YOUR PROFILE MENU ########################################");
        int choice;
        do {
            System.out.println("\t[1] Browse your questions");
            System.out.println("\t[2] Browse your answered questions");
            System.out.println("\t[3] Update your profile");
            System.out.println("\t[4] Browse followed users");
            System.out.println("\t[5] Exit");
            System.out.println("Input: ");
            choice = Integer.parseInt(scanner.nextLine());
        } while (choice < 1 || choice > 5);
        if (choice == 5)
            System.out.println("############################################### EXIT ###############################################");
        return choice;
    }

    public void updateProfile(UserRegistrationDTO userRegistrationDTO){
        System.out.println("############################################### UPDATE #############################################");
        System.out.println("* Full name (Press Enter if you don't want to change it): ");
        String fullName = scanner.nextLine();
        if(!fullName.equals("")){
            userRegistrationDTO.setFullName(fullName);
        }
        System.out.println("* Password (Press Enter if you don't want to change it): ");
        String password = scanner.nextLine();
        if(!password.equals("")){
            userRegistrationDTO.setEncPassword(AuthenticationController.encryptPassword(password));
        }
        System.out.println("Possible Countries: " + Arrays.toString(Constants.COUNTRIES.toArray()));
        String country;
        do {
            System.out.println("* Country (Press Enter if you don't want to change it): ");
            country = scanner.nextLine();
            if(country.equals("")){
                break;
            }
        } while (!Constants.COUNTRIES.contains(country));
        if(!country.equals("")){
            userRegistrationDTO.setCountry(country);
        }
        System.out.println("* Website (Press Enter if you don't want to change it): ");
        String website = scanner.nextLine();
        if(!website.equals("")){
            userRegistrationDTO.setWebsite(website);
        }
    }

    public int browseFollowedUsers(PageDTO<String> users){
        System.out.println("########################################## FOLLOWED USERS ##########################################");
        System.out.println(users);
        int choice;
        do {
            System.out.println("\t[1] Choose a user to view");
            System.out.println("\t[2] Go to the next page");
            System.out.println("\t[3] Go to the previous page");
            System.out.println("\t[4] Exit");
            System.out.println("Input: ");
            choice = Integer.parseInt(scanner.nextLine());
        } while (choice < 1 || choice > 4);
        if (choice == 4)
            System.out.println("############################################### EXIT ###############################################");
        return choice;
    }

    public int adminUserProfile(){
        System.out.println("#################################### USER PROFILE MENU (ADMIN) #####################################");
        int choice;
        do {
            System.out.println("\t[1] Delete user");
            System.out.println("\t[2] Exit");
            System.out.println("Input: ");
            choice = Integer.parseInt(scanner.nextLine());
        } while (choice < 1 || choice > 2);
        if (choice == 2)
            System.out.println("############################################### EXIT ###############################################");
        return choice;
    }

    public void waitInputInUserProfileNotLogged(){
        System.out.println("\t Press ENTER two times to go back");
        scanner.nextLine();
    }
}
