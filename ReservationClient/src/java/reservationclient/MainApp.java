/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reservationclient;

import entity.Guest;
import java.util.Scanner;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author xqy11
 */
public class MainApp {
    public MainApp() {
    }
    
    private Guest currentGuest;
    
    public void runApp() {
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            
            System.out.println("*** Welcome to Hotel Reservation System Management Client ***");
            System.out.println("1: Guest Login");
            System.out.println("2: Register as guest");
            System.out.println("3: Search Hotel Room");
            System.out.println("4: Exit\n");
            response = 0;
            
            while(response < 1 || response > 2) {
                
                System.out.print("> ");
                
                response = scanner.nextInt();
                
                if(response == 1) {
                    
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");
                        loggedInMenu();
                    } catch(InvalidLoginCredentialException ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                
                } else if (response == 2) {
                    doRegisterAsGuest();
                    
                }else if (response == 3) {
                    doSearchHotelRoom();
      
                }else if (response == 4) {
                    // Exit
                    break;
                    
                } else {
                    System.out.println("Invalid option, please try again!\n");
                
                }               
            } // end while loop to read response
            
            if(response == 4)
            {
                break;
            }
            
        }
        
    }
    
    private void doLogin() throws InvalidLoginCredentialException {
        
        Scanner scanner = new Scanner(System.in);
        String email = "";
        String password = "";
        
        System.out.println("*** Hotel Reservation System Reservation Client :: Login ***");
        System.out.print("Enter email> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
//        if(email.length() > 0 && password.length() > 0)
//        {
//            try{
//                currentGuest = guestSessionBeanRemote.guestLogin(email, password);    
//            }catch(InvalidLoginCredentialException ex){
//                throw new InvalidLoginCredentialException();
//            }
//        }
//        else
//        {
//            throw new InvalidLoginCredentialException("Missing login credential!");
//        }
    }

    private void doRegisterAsGuest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doSearchHotelRoom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loggedInMenu() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Retail Core Banking System - Teller Terminal ***\n");
            System.out.println("You are login as " + currentGuest.getName()+ " \n");
            System.out.println("1: Search Hotel Room");
            System.out.println("2: Reserve Hotel Room");
            System.out.println("3: View My Reservation Details");
            System.out.println("4: View All My Reservations");
            System.out.println("5: Logout\n");
            response = 0;
            
            while(response < 1 || response > 5)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doSearchHotelRoom();
                }
                else if(response == 2)
                {
                    doReserveHotelRoom();
                }
                else if (response == 3)
                {
                    doViewMyResrevationDetails();
                }
                else if (response == 4)
                {
                   doViewAllMyReservation();
                }
                else if (response == 5)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 5)
            {
                break;
            }
        }
    }

    private void doReserveHotelRoom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doViewMyResrevationDetails() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doViewAllMyReservation() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
