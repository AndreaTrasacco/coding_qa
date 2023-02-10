package it.unipi.lsmsd.coding_qa.view;

import it.unipi.lsmsd.coding_qa.dto.UserLoginDTO;
import it.unipi.lsmsd.coding_qa.dto.UserRegistrationDTO;
import it.unipi.lsmsd.coding_qa.utils.Constants;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class AuthenticationView {
    private Scanner scanner = new Scanner(System.in);

    public int initialMenu() {
        System.out.println("############################################### MENU ###############################################");
        int choice;
        do {
            System.out.println("\t[1] Sign up");
            System.out.println("\t[2] Log in");
            System.out.println("\t[3] Go to main menu without logging in");
            System.out.println("\t[4] Exit");
            System.out.print("Input: ");
            choice = scanner.nextInt();
        } while (choice < 1 && choice > 4);
        if (choice == 4)
            System.out.println("############################################### EXIT ###############################################");
        return choice;
    }

    public UserRegistrationDTO signUp() { // We assume the user inputs the correct data, so minimal data validation
        UserRegistrationDTO userDTO = new UserRegistrationDTO();
        System.out.println("############################################## SIGN UP #############################################");
        System.out.print("\t* Nickname: ");
        userDTO.setNickname(scanner.nextLine().replaceAll(" ", "_"));
        System.out.print("\t* Password: ");
        userDTO.setEncPassword(scanner.nextLine());
        System.out.print("\t* Full Name: ");
        userDTO.setFullName(scanner.nextLine());
        System.out.print("\t* Year of birth date: ");
        int year = scanner.nextInt();
        System.out.print("\t* Month of birth date: ");
        int month = scanner.nextInt();
        System.out.print("\t* Day of birth date: ");
        int day = scanner.nextInt();
        userDTO.setBirthdate(new Date(year - 1900, month - 1, day));
        scanner.nextLine(); // to read '\n' leaved in buffer after nextInt() TODO ATTENZIONE
        System.out.println("\tPossible Countries: " + Arrays.toString(Constants.COUNTRIES.toArray()));
        String country;
        do {
            System.out.print("\t* Country: ");
            country = scanner.nextLine();
        } while (!Constants.COUNTRIES.contains(country));
        userDTO.setCountry(country);
        System.out.print("\t* Your website URL (Press Enter if you don't have any): ");
        userDTO.setWebsite(scanner.nextLine());
        return userDTO;
    }

    public UserLoginDTO login() {
        System.out.println("############################################## LOG IN ##############################################");
        System.out.print("\t* Nickname: ");
        String nickname = scanner.nextLine().replaceAll(" ", "_");
        System.out.print("\t* Password: ");
        String password = scanner.nextLine();
        return new UserLoginDTO(nickname, password);
    }

    public void showLogoutMessage() {
        System.out.println("############################################## LOG OUT #############################################");
    }

    public static void main(String [] args){
        AuthenticationView authenticationView = new AuthenticationView();
        authenticationView.signUp();
    }
}
