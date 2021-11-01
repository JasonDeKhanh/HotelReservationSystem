package managementclient;

import java.util.Scanner;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;



public class SystemAdministrationModule {
    
    // validator for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    // private attribute for required remote session bean remote
    
    // private Employee currentEmployee;

    public SystemAdministrationModule() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }
    
    
    // overloaded constructor here
    
    
    
    // menu
    public void menuSystemAdministration() { // throws invalid access right something
    
        /*
        if(currentEmployee.getAccessRightEnum() != AccessRightEnum.SYSTEM_ADMINISTRATOR)
        {
            throw new InvalidAccessRightException("You don't have SYSTEM_ADMINISTRATOR rights to access the system administration module.");
        }
        */
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            
            System.out.println("*** Hotel Reservation System Manager Client :: System Administration ***");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employees");
            System.out.println("-------------------------");
            System.out.println("3: Create New Partner");
            System.out.println("4: View All Partners");
            System.out.println("-------------------------");
            System.out.println("5: Back\n");
            response = 0;
            
            while(response < 1 || response > 5) {
                
                System.out.print("> ");
                
                response = scanner.nextInt();
                
                if (response == 1) {
                    
                    doCreateNewEmployee();
                    
                } else if (response == 2) {
                    
                    doViewAllEmployees();
                    
                } else if (response == 3) {
                    
                    doCreateNewPartners();
                    
                } else if (response == 4) {
                    
                    doViewAllPartners();
                    
                } else if (response == 5) {
                    // Back
                    break;
                    
                } else {
                    
                    System.out.println("Invalid option, please try again!\n");
                
                }                
            }
            
            if (response == 5) { // same number as the Back option
                break;
            }
            
        }
        
        
    } // end menuSystemAdministration
    
    
    private void doCreateNewEmployee() {
    
    }
    
    
    private void doViewAllEmployees() {
    
    }

    
    private void doCreateNewPartners() {
    
    }
    
    
    private void doViewAllPartners() {
    
    }
    
    
    // I think need to put the 2 Bean Validation methods here
    // see POS v4.2
    
}
