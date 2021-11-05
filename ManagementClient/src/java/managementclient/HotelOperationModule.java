package managementclient;

import java.util.Scanner;



public class HotelOperationModule {

    
    // private attributes for remote session bean here
    
    // Queue and ConnectionFactory
    
    // private Employee currentEmployee;
    
    
    public HotelOperationModule() {
    }
    
    
    // overloaded constructor
    
    
    // menu
    public void menuHotelOperation() {
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            
            System.out.println("*** Hotel Reservation System Manager Client :: Hotel Operation ***");
            // for Operation Manager
            System.out.println("1: Create New Room Type");
            System.out.println("2: View Room Type Details"); // includes Update Room Type and Delete Room Type
            System.out.println("3: View All Room Types");
            System.out.println("4: Create New Room");
            System.out.println("5: Update Room");
            System.out.println("6: Delete Room");
            System.out.println("7: View All Rooms");
            System.out.println("8: View Room Allocation Exception Report");
            
            // for Sales Manager
            System.out.println("9: Create New Room Rate");
            System.out.println("10: View Room Rate Details");
            System.out.println("11: View All Room Rates");
            
        }
        
    }
    
}
