package it.unipi.lsmsd.coding_qa.view;

import it.unipi.lsmsd.coding_qa.dto.PageDTO;

import java.util.Scanner;

public class MainView {
    private Scanner scanner = new Scanner(System.in);

    public static void main(String [] args)
    {
        MainView mainView = new MainView();
        mainView.mainMenuLoggedIn();
        mainView.mainMenuNotLoggedIn();
    }

    public int mainMenuNotLoggedIn() {
        System.out.println("#################################### MAIN MENU (NOT LOGGED IN) #####################################");
        int choice;
        do {
            System.out.println("\t[1] Browse questions");
            System.out.println("\t[2] Search question");
            System.out.println("\t[3] Exit");
            System.out.println("Input: ");
            choice = Integer.parseInt(scanner.nextLine());;
        } while (choice < 1 || choice > 3);
        if(choice == 3)
            System.out.println("############################################### EXIT ###############################################");
        return choice;
    }

    public int mainMenuLoggedIn() {
        System.out.println("############################################# MAIN MENU ############################################");
        int choice;
        do {
            System.out.println("\t[1] Browse questions");
            System.out.println("\t[2] Search question");
            System.out.println("\t[4] Publish new question");
            System.out.println("\t[5] Browse suggested questions to answer");
            System.out.println("\t[6] Browse suggested questions to read");
            System.out.println("\t[7] Open your profile menu");
            System.out.println("\t[8] Go to analytics menu");
            System.out.println("\t[9] Exit");
            System.out.println("Input: ");
            choice = Integer.parseInt(scanner.nextLine());;
        } while (choice < 1 || choice > 9);
        if(choice == 9)
            System.out.println("############################################### EXIT ###############################################");
        return choice;
    }

    public int inputMessageWithPaging(String message, int size){ // TODO CANCELLARE SE NON USATA
        int number;
        do {
            System.out.println("\t* " + message + ": ");
            System.out.println("Input: ");
            number = Integer.parseInt(scanner.nextLine());;
        } while (number < 1 || number > size);
        return number;
    }

    public void showMessage(String message){
        System.out.println(message);
    }

    public <T> void viewPage(PageDTO<T> pageDTO){
        System.out.println(pageDTO);
    }

    public void view(Object o){
        System.out.println(o.toString());
    }
}
