package managementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import entity.Employee;
import java.util.Scanner;
import util.exception.InvalidLoginCredentialException;



public class MainApp {
   
    // private attributes here
    // all the needed remote session beans
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    
    // Queue and ConnectionFactory for MDB
    
    // SystemAdministrationModule
    // HotelOperationModule
    
    private Employee currentEmployee;
    
    
    public MainApp() {
    }
    
    // overloaded constructor here
    public MainApp(EmployeeSessionBeanRemote employeeSessionBeanRemote) {
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
    }
    
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
            
            if(response == 2)
            {
                break;
            }
            
        }
        
    }
    
    
    
    private void doLogin() throws InvalidLoginCredentialException {
        
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";
        
        System.out.println("*** Hotel Reservation System Management Client :: Login ***");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(username.length() > 0 && password.length() > 0)
        {
            try{
                currentEmployee = employeeSessionBeanRemote.employeeLogin(username, password);    
            }catch(InvalidLoginCredentialException ex){
                throw new InvalidLoginCredentialException();
            }
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
        
        
    }
}
