package managementclient;

import java.util.Scanner;
import util.exception.InvalidLoginCredentialException;



public class MainApp {
   
    // private attributes here
    // all the needed remote session beans
    
    // Queue and ConnectionFactory for MDB
    
    // SystemAdministrationModule
    // HotelOperationModule
    
    // private Employee currentEmployee // like current staff uk
    
    
    public MainApp() {
    }
    
    // overloaded constructor here
    
    public void runApp() {
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            
            System.out.println("*** Welcome to Hotel Reservation System Management Client ***");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;
            
            while(response < 1 || response > 2) {
                
                System.out.print("> ");
                
                response = scanner.nextInt();
                
                if(response == 1) {
                    
                    // doLogin()
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");
                        
                        // systemAdministrationModule
                        
                        // hotelOperationModule
                        
                        // frontOfficeModule
                        
                    } catch(InvalidLoginCredentialException ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                
                } else if (response == 2) {
                    
                    break;
                    
                } else {
                    
                    System.out.println("Invalid option, please try again!\n");
                
                }               
            } // end while loop to read response
            
        }
        
    }
    
    
    
    private void doLogin() throws InvalidLoginCredentialException {
        
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";
        
        System.out.println("*** Hotel Reservation System Management Client :: Login ***");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.println("Enter password> ");
        password = scanner.nextLine().trim();
        
        /*
        if(username.length() > 0 && password.length() > 0)
        {
            currentStaffEntity = staffEntitySessionBeanRemote.staffLogin(username, password);      
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
        */
        
        
    }
}
