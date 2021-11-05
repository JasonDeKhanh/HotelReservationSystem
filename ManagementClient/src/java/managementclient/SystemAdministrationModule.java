package managementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import entity.Employee;
import entity.Partner;
import java.text.NumberFormat;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.AccessRight;
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;
import util.exception.PartnerUsernameExistException;
import util.exception.UnknownPersistenceException;



public class SystemAdministrationModule {
    
    // validator for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    // private attribute for required remote session bean remote
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    
    private Employee currentEmployee;

    public SystemAdministrationModule() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }
    
    
    // overloaded constructor here
    public SystemAdministrationModule(Employee currentEmployee, EmployeeSessionBeanRemote employeeSessionBeanRemote, PartnerSessionBeanRemote partnerSessionBeanRemote) {
        this();
        this.currentEmployee = currentEmployee;
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
    }
    
    
    // menu
    public void menuSystemAdministration() throws InvalidAccessRightException { // throws invalid access right something
    
        
        if(currentEmployee.getAccessRight() != AccessRight.SYSTEM_ADMIN)
        {
            throw new InvalidAccessRightException("You don't have SYSTEM_ADMINISTRATOR rights to access the system administration module.\n");
        }
        
        
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
        Scanner scanner = new Scanner(System.in);
        Employee newEmployee = new Employee();
        
        System.out.println("*** Hotel Reservation System Manager Client :: System Administration :: Create New Employee ***\n");
        System.out.print("Enter Usesrname> ");
        newEmployee.setUsername(scanner.nextLine().trim());
        System.out.print("Enter password> ");
        newEmployee.setPassword(scanner.nextLine().trim());
        
        while(true)
        {
            System.out.print("Select Access Right (1:System Administrator, 2: Operation Manager, 3: Sales Manager, 4: Guest Relation Officer)> ");
            Integer accessRightInt = scanner.nextInt();
            
            if(accessRightInt >= 1 && accessRightInt <= 4)
            {
                newEmployee.setAccessRight(AccessRight.values()[accessRightInt-1]);
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        Set<ConstraintViolation<Employee>>constraintViolations = validator.validate(newEmployee);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                Long newStaffId = employeeSessionBeanRemote.createNewEmployee(newEmployee);
                System.out.println("New employee created successfully!: " + newStaffId + "\n");
            }
            catch(EmployeeUsernameExistException ex)
            {
                System.out.println("An error has occurred while creating the new employee!: The user name already exist\n");
            }
            catch(UnknownPersistenceException ex)
            {
                System.out.println("An unknown error has occurred while creating the new employee!: " + ex.getMessage() + "\n");
            }
            catch(InputDataValidationException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            showInputDataValidationErrorsForEmployeetEntity(constraintViolations);
        }
    }
    
    
    private void doViewAllEmployees() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** Hotel Reservation System Manager Client :: System Administration :: View All Employees ***\n");
        
        List<Employee> employeeEntities = employeeSessionBeanRemote.retrieveAllEmployees();
        System.out.printf("%10s%40s%40s\n", "Employee Id", "Name", "Access Right");

        for(Employee e: employeeEntities)
        {
            System.out.printf("%10s%40s%40s\n", e.getEmployeeId(), e.getUsername(), e.getAccessRight().toString());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    
    }

    
    private void doCreateNewPartners() {
        Scanner scanner = new Scanner(System.in);
        Partner newPartner = new Partner();
        
        System.out.println("*** Hotel Reservation System Manager Client :: System Administration :: Create New Partner ***\n");
        System.out.print("Enter Usesrname> ");
        newPartner.setUsername(scanner.nextLine().trim());
        System.out.print("Enter password> ");
        newPartner.setPassword(scanner.nextLine().trim());
        
        Set<ConstraintViolation<Partner>>constraintViolations = validator.validate(newPartner);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                Long newPartnerId = partnerSessionBeanRemote.createNewPartner(newPartner);
                System.out.println("New partner created successfully!: " + newPartnerId + "\n");
            }
            catch(PartnerUsernameExistException ex)
            {
                System.out.println("An error has occurred while creating the new partner!: The user name already exist\n");
            }
            catch(UnknownPersistenceException ex)
            {
                System.out.println("An unknown error has occurred while creating the new partner!: " + ex.getMessage() + "\n");
            }
            catch(InputDataValidationException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            showInputDataValidationErrorsForPartnerEntity(constraintViolations);
        }
    
    }
    
    
    private void doViewAllPartners() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** Hotel Reservation System Manager Client :: System Administration :: View All Partners ***\n");
        
        List<Partner> partnerEntities = partnerSessionBeanRemote.retrieveAllPartners();
        System.out.printf("%10s%40s\n", "Partner Id", "Name");

        for(Partner p: partnerEntities)
        {
            System.out.printf("%10s%40s\n", p.getPartnerId(), p.getUsername());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    
    }
    
    
    //Bean Validation methods
    private void showInputDataValidationErrorsForEmployeetEntity(Set<ConstraintViolation<Employee>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
    //Bean Validation methods
    private void showInputDataValidationErrorsForPartnerEntity(Set<ConstraintViolation<Partner>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
}
