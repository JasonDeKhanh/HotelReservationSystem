package managementclient;

import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Employee;
import entity.RoomType;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.AccessRight;
import util.exception.DeleteRoomTypeException;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

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
        
        System.out.print("Enter room type number of beds> ");
        Integer numBeds = Integer.parseInt(scanner.nextLine().trim());
        newRoomType.setBeds(numBeds);
        
        System.out.print("Enter room type capacity> ");
        newRoomType.setCapacity(Integer.parseInt(scanner.nextLine().trim()));
        
        System.out.print("Enter room type amenities> ");
        newRoomType.setAmenities(scanner.nextLine().trim());
        
//        while(true) {
//            
//            System.out.println("Would you like to add another amenity? Y/N ");
//            String response = "";
//            System.out.print("> ");
//            response = scanner.nextLine().trim();
//            
//            if (response.equals("Y")) {
//                System.out.print("Enter 1 room type amenity> ");
//                newRoomType.getAmenities().add(scanner.nextLine().trim());
//            } else if (response.equals("N")) {
//                break;
//                
//            } else {
//                System.out.println("Invalid response");
//            }
//            
//        }
        
        // inventory automatically initialized as 0
        
        // read nextHigherRoomType
        System.out.print("Enter next higher room type name. Enter 'None' if there is no higher room type.> ");
        String nextHigherRoomTypeName = scanner.nextLine().trim();
        
        Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(newRoomType);

        if(constraintViolations.isEmpty()) {
            try {
            
            newRoomType = roomTypeSessionBeanRemote.createNewRoomType(nextHigherRoomTypeName, newRoomType);
            System.out.println("Successfully created new room type " + newRoomType.getName() + " with ID " + newRoomType.getRoomTypeId() + "\n");
        
            } 
            catch (RoomTypeNotFoundException ex) 
            {
                System.out.println("An error occurred: " + ex.getMessage());
            } 
            catch(RoomTypeNameExistException ex) 
            {
                System.out.println("An error occurred: " + ex.getMessage());
            }
            catch(UnknownPersistenceException ex)
            {
                System.out.println("An unknown error has occurred while creating the new employee!: " + ex.getMessage() + "\n");
            }
            catch(InputDataValidationException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForRoomTypetEntity(constraintViolations);
        }
        
        
    
    }
    
    public void doViewRoomTypeDetails() {
        // don't forget Update Room Type and Delete Room Type in here
        Scanner scanner = new Scanner(System.in);
        RoomType roomType;
        String roomTypeName = "";
        
        System.out.println("*** Hotel Reservation System Manager Client :: System Administration :: View Room Type Details ***\n");
        
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
            System.out.println("Beds: " + roomType.getBeds());
            System.out.println("Amenities: " + roomType.getAmenities());
            System.out.println("");
            System.out.println("Enabled: " + roomType.getEnabled());
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

                        doUpdateRoomType(roomType);
                        
                    } else if (response == 2) {

                        doDeleteRoomType(roomType);
                        
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
    
    public void doUpdateRoomType(RoomType roomType) {
        
        Scanner scanner = new Scanner(System.in);
        String input;
        
        System.out.println("*** Hotel Reservation System Manager Client :: System Administration :: Update Room Type ***\n");
        
        System.out.print("Enter room type name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0) {
            roomType.setName(input);
        }
        
        System.out.print("Enter room type description (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0) {
            roomType.setDescription(input);
        }
        
        System.out.print("Enter room type size (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0) {
            roomType.setSize(Double.parseDouble(input));
        }
        
        System.out.print("Enter room type number of beds (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0) {
            roomType.setSize(Double.parseDouble(input));
        }
        // how to edit beds? are we allowed to delete or something
        // same for amenities
        
        System.out.print("Enter room type capacity (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0) {
            roomType.setCapacity(Integer.parseInt(input));
        }
        
        System.out.print("Enter room type inventory (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0) {
            roomType.setInventory(Integer.parseInt(input));
        }
        
        System.out.print("Enter next higher room type name. Enter 'None' if there is no higher room type. (blank if no change)> ");
        String nextHigherRoomTypeName = scanner.nextLine().trim();
        
        
        Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(roomType);

        if(constraintViolations.isEmpty()) {
            try {
                
                RoomType nextHigherRoomType = roomTypeSessionBeanRemote.retrieveRoomTypeByName(nextHigherRoomTypeName);
                roomType.setNextHigherRoomType(nextHigherRoomType);
                roomTypeSessionBeanRemote.updateRoomType(roomType);
                System.out.println("Room type updated successfully!\n");
        
            } catch (RoomTypeNotFoundException | UpdateRoomTypeException ex) {
                System.out.println("An error occurred: " + ex.getMessage());
            } catch (InputDataValidationException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showInputDataValidationErrorsForRoomTypetEntity(constraintViolations);
        }
        
    }
    
    public void doDeleteRoomType(RoomType roomType) {
        
        Scanner scanner = new Scanner(System.in);
        String input;
        
        System.out.println("*** Hotel Reservation System Manager Client :: System Administration :: Delete Room Type ***\n");
        System.out.print("Confirm Delete Room Type " + roomType.getName() + " (Room Type ID: " + roomType.getRoomTypeId() + ") (Entry 'Y' to Delete)> ");
        input = scanner.nextLine().trim();
        
        if(input.equals('Y')) {
            
            try {
                
                roomTypeSessionBeanRemote.deleteRoomType(roomType.getRoomTypeId());
                System.out.println("Room Type deleted successfully!\n");
            
            } 
            catch (RoomTypeNotFoundException | DeleteRoomTypeException ex) 
            {
                System.out.println("An error has occurred while deleting room type: " + ex.getMessage() + "\n");
            }
            
        } else {
            System.out.println("Room Type NOT deleted!\n");
        }
        
        
    }
    
    public void doViewAllRoomTypes() {
        
        Scanner scanner = new Scanner(System.in);
        String input;
        
        System.out.println("*** Hotel Reservation System Manager Client :: System Administration :: View All Room Types ***\n");
        
        List<RoomType> roomTypes = roomTypeSessionBeanRemote.retrieveAllRoomTypes();
        System.out.printf("%8s%20s%20s%15s%20s%20s%8s%10s%\n", "RoomType ID", "Name", "Description", "Size", "Beds", "Capacity", "Amenities", "Inventory", "Enabled");
    
        for(RoomType roomType : roomTypes) {
            System.out.printf("%8s%20s%20s%15s%20s%20s%8s%10s%\n", roomType.getRoomTypeId(), roomType.getName(), roomType.getDescription(), roomType.getSize(), roomType.getBeds(), roomType.getCapacity(), roomType.getAmenities(), roomType.getInventory(), roomType.getEnabled());
        }
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
    private void showInputDataValidationErrorsForRoomTypetEntity(Set<ConstraintViolation<RoomType>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
    
}
