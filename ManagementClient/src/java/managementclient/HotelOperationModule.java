package managementclient;

import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Employee;
import entity.Partner;
import entity.RoomType;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.AccessRight;
import util.exception.InvalidAccessRightException;
import util.exception.RoomTypeNotFoundException;

public class HotelOperationModule {

    // validator for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    // private attributes for remote session bean here
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    
    
    
    // Queue and ConnectionFactory
    private Employee currentEmployee;

    public HotelOperationModule() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    // overloaded constructor
    public HotelOperationModule(Employee currentEmployee, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote) {
        this();
        this.currentEmployee = currentEmployee;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;

    }

    // menu
    public void menuHotelOperation() throws InvalidAccessRightException {
        
        if(!(currentEmployee.getAccessRight() == AccessRight.OPERATION_MANAGER || currentEmployee.getAccessRight() == AccessRight.SALES_MANAGER))
        {
            throw new InvalidAccessRightException("You don't have OPERATION_MANAGER OR SALES_MANAGER rights to access the hotel operation module.\n");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {

            System.out.println("*** Hotel Reservation System Manager Client :: Hotel Operation ***");

            if (currentEmployee != null && currentEmployee.getAccessRight() == AccessRight.OPERATION_MANAGER) {
                // for Operation Manager
                System.out.println("1: Create New Room Type");
                System.out.println("2: View Room Type Details"); // includes Update Room Type and Delete Room Type
                System.out.println("3: View All Room Types");
                System.out.println("4: Create New Room");
                System.out.println("5: Update Room");
                System.out.println("6: Delete Room");
                System.out.println("7: View All Rooms");
                System.out.println("8: View Room Allocation Exception Report");
                System.out.println("---------------------------------------");
                System.out.println("9: Back");
                response = 0;

                while (response < 1 || response > 9) {

                    System.out.print("> ");

                    response = scanner.nextInt();

                    if (response == 1) {

                        doCreateNewRoomType();
                        
                    } else if (response == 2) {

                        doViewRoomTypeDetails();
                        
                    } else if (response == 3) {

                        doViewAllRoomTypes();
                        
                    } else if (response == 4) {

                        doCreateNewRoom();
                        
                    } else if (response == 5) {

                        doUpdateRoom();
                        
                    } else if (response == 6) {

                        doDeleteRoom();
                        
                    } else if (response == 7) {

                        doViewAllRooms();
                        
                    } else if (response == 8) {

                        doViewRoomAllocationExceptionReport();
                        
                    } else if (response == 9) {
                        // Back
                        break;

                    } else {

                        System.out.println("Invalid option, please try again!\n");

                    }
                }

                if (response == 9) { // same number as the Back option
                    // back
                    break;
                }

            } else if (currentEmployee != null && currentEmployee.getAccessRight() == AccessRight.SALES_MANAGER) {
                // for Sales Manager
                System.out.println("1: Create New Room Rate");
                System.out.println("2: View Room Rate Details"); // includes Update Room Rate and Delete Room Rate
                System.out.println("3: View All Room Rates");
                System.out.println("---------------------------------------");
                System.out.println("4: Back");
                response = 0;

                while (response < 1 || response > 4) {

                    System.out.print("> ");

                    response = scanner.nextInt();

                    if (response == 1) {

                        doCreateNewRoomRate();
                        
                    } else if (response == 2) {

                        doViewRoomRateDetails();
                        
                    } else if (response == 3) {

                        doViewAllRoomRates();
                        
                    } else if (response == 4) {
                        // Back
                        break;

                    } else {

                        System.out.println("Invalid option, please try again!\n");

                    }
                }

                if (response == 4) { // same number as the Back option
                    // back
                    break;
                }

            }

        }
    }
    

    public void doCreateNewRoomType() {
        
        Scanner scanner = new Scanner(System.in);
        RoomType newRoomType = new RoomType();
        
        System.out.println("*** Hotel Reservation System Manager Client :: System Administration :: Create New Room Type ***\n");
        
        System.out.print("Enter room type name> ");
        newRoomType.setName(scanner.nextLine().trim());
        System.out.print("Enter room type description> ");
        newRoomType.setDescription(scanner.nextLine().trim());
        System.out.print("Enter room type size> ");
        newRoomType.setSize(Double.parseDouble(scanner.nextLine().trim()));
        
        //
        System.out.print("Enter room type bed name> ");
        String bedName = scanner.nextLine().trim();
        System.out.print("Enter room type number of " + bedName + " beds> ");
        Integer numBeds = Integer.parseInt(scanner.nextLine().trim());
        newRoomType.getBeds().put(bedName, numBeds);
        
        System.out.print("Enter room type capacity> ");
        newRoomType.setCapacity(Integer.parseInt(scanner.nextLine().trim()));
        
        System.out.print("Enter 1 room type amenity> ");
        newRoomType.getAmenities().add(scanner.nextLine().trim());
        
        while(true) {
            
            System.out.println("Would you like to add another amenity? Y/N ");
            String response = "";
            System.out.print("> ");
            response = scanner.nextLine().trim();
            
            if (response.equals("Y")) {
                System.out.print("Enter 1 room type amenity> ");
                newRoomType.getAmenities().add(scanner.nextLine().trim());
            } else if (response.equals("N")) {
                break;
                
            } else {
                System.out.println("Invalid response");
            }
            
        }
        
        // inventory automatically initialized as 0
        
        // read nextHigherRoomType
        System.out.print("Enter next higher room type name. Enter 'None' if there is no higher room type.> ");
        String nextHigherRoomTypeName = scanner.nextLine().trim();
        
        // call 
        try {
            
            newRoomType = roomTypeSessionBeanRemote.createNewRoomType(nextHigherRoomTypeName, newRoomType);
            System.out.println("Successfully created new room type " + newRoomType.getName() + " with ID " + newRoomType.getRoomTypeId() + "\n");
        
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error occurred: " + ex.getMessage());
        }
        
    
    }
    
    public void doViewRoomTypeDetails() {
        // don't forget Update Room Type and Delete Room Type in here
        Scanner scanner = new Scanner(System.in);
        RoomType roomType;
        String roomTypeName = "";
        
        System.out.print("Enter room type name to be viewed> ");
        roomTypeName = scanner.nextLine().trim();
        
        try {
            roomType = roomTypeSessionBeanRemote.retrieveRoomTypeByName(roomTypeName);
            // add to show room rates here later!!!////////////////////////////////////////////////////////////////////////
            System.out.println("---------------------");
            System.out.println("Room Type Name: " + roomType.getName());
            System.out.println("Description: " + roomType.getDescription());
            System.out.println("Size: " + roomType.getSize()); // put metres square here???
            System.out.println("Room capacity: " + roomType.getCapacity() + " pax");
            // show beds
            System.out.println("");
            System.out.println("Beds: ");
            for(String bedName : roomType.getBeds().keySet()) {
                System.out.println(" - " + roomType.getBeds().get(bedName) + " " + bedName);
            }
            // show amenities
            System.out.println("Amenities:");
            for(String amenity : roomType.getAmenities()) {
                System.out.println(" - " + amenity);
            }
            System.out.println("");
            System.out.println("Inventory: " + roomType.getInventory());
            if (roomType.getNextHigherRoomType() != null) {
                System.out.println("Next Higher Room Type: " + roomType.getNextHigherRoomType().getName());
            } else {
                System.out.println("Next Higher Room Type: This room currently the highest room type or has no higher room type indicated");
            }
            // show room rates
            System.out.println("Rates: ");
            /*
            for ( RoomRate roomRate : roomType.getRoomRates()) {
                System.out.println(" - " + roomRate.getName() + ": " + roomRate.getRatePerNight() + " dollars/night");
            }
            */
            System.out.println("---------------------");
            
            // ask to update roomType or delete roomType
            while(true) {
                System.out.println("1: Update Room Type");
                System.out.println("2: Delete Room Type"); 
                System.out.println("--------");
                System.out.println("3: Back");
                Integer response = 0;

                while (response < 1 || response > 3) {

                    System.out.print("> ");

                    response = scanner.nextInt();

                    if (response == 1) {

                        doUpdateRoomType();
                        
                    } else if (response == 2) {

                        doDeleteRoomType();
                        
                    } else if (response == 3) {
                        // Back
                        break;

                    } else {

                        System.out.println("Invalid option, please try again!\n");

                    }
                }

                if (response == 3) { // same number as the Back option
                    // back
                    break;
                }
            }
                    
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error occurred: " + ex.getMessage());
        }
        
        
    }
    
    public void doUpdateRoomType() {
        
    }
    
    public void doDeleteRoomType() {
        
    }
    
    public void doViewAllRoomTypes() {
        
    }
    
    public void doCreateNewRoom() {
        
    }
    
    public void doUpdateRoom() {
        
    }
    
    public void doDeleteRoom() {
    
    }

    public void doViewAllRooms() {
        
    }
    
    public void doViewRoomAllocationExceptionReport() {
        
    }
    
    ////
    ////
    public void doCreateNewRoomRate() {
    
    }
    
    public void doViewRoomRateDetails() {
        
    }
    
    // doUpdateRoomRate() 
    
    // doDeleteRooMRate()
    
    public void doViewAllRoomRates() {
        
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
