package managementclient;

import entity.Employee;
import java.util.Scanner;
import util.enumeration.AccessRight;
import util.exception.InvalidAccessRightException;

public class HotelOperationModule {

    // private attributes for remote session bean here
    // Queue and ConnectionFactory
    private Employee currentEmployee;

    public HotelOperationModule() {
    }

    // overloaded constructor
    public HotelOperationModule(Employee currentEmployee) {

        this.currentEmployee = currentEmployee;

    }

    // menu
    public void menuHotelOperation() throws InvalidAccessRightException {
        
        if(currentEmployee.getAccessRight() != AccessRight.OPERATION_MANAGER || currentEmployee.getAccessRight() != AccessRight.SALES_MANAGER)
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
        
    }
    
    public void doViewRoomTypeDetails() {
        // don't forget Update Room Type and Delete Room Type in here
    }
    
    // doUpdateRoomType()
    
    // doDeleteRoomType()
    
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
    public void doCreateNewRoomRate() {
    
    }
    
    public void doViewRoomRateDetails() {
        
    }
    
    // doUpdateRoomRate() 
    
    // doDeleteRooMRate()
    
    public void doViewAllRoomRates() {
        
    }
    
}
