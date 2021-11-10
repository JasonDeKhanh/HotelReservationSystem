 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managementclient;

import entity.Employee;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import util.enumeration.AccessRight;
import util.exception.InvalidAccessRightException;

/**
 *
 * @author msipc
 */
public class FrontOfficeModule {
    
    // private attributes for remote session bean here
    // Queue and ConnectionFactory
    private Employee currentEmployee;

    
    
    public FrontOfficeModule() {
    }

    // overloaded constructor
    public FrontOfficeModule(Employee currentEmployee) {
        this.currentEmployee = currentEmployee;
    }
    
    
    
    public void menuFrontOffice() throws InvalidAccessRightException {
        
        if(currentEmployee.getAccessRight() != AccessRight.GUEST_RELATION_OFFICER)
        {
            throw new InvalidAccessRightException("You don't have GUEST_RELATION_OFFICER rights to access the front office module.\n");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            
            System.out.println("*** Hotel Reservation System Management Client :: Front Office ***");
            System.out.println("1: Walk-in Search Room");
            System.out.println("2: Check-in Guest");
            System.out.println("3: Check-out Guest");
            System.out.println("-------------------------");
            System.out.println("4: Back\n");
            response = 0;
            
            while(response < 1 || response > 4) {
                
                System.out.print("> ");
                
                response = scanner.nextInt();
                
                if (response == 1) {
                    
                    doWalkinSearchRoom();
                    
                } else if (response == 2) {
                    
                    doCheckinGuest();
                    
                } else if (response == 3) {
                    
                    doCheckoutGuest();
                    
                } else if (response == 4) {
                    // Back
                    break;
                    
                } else {
                    
                    System.out.println("Invalid option, please try again!\n");
                
                }                
            }
            
            if (response == 4) { // same number as the Back option
                break;
            }
            
        }
    }
    
    
    ///
    public void doWalkinSearchRoom() {
        
        
        try {
            
            System.out.println("*** Hotel Reservation System Management Client :: Front Office :: Walk-in Search Room ***");
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            Date checkinDate;
            Date checkoutDate;
            
            System.out.print("Enter Check-in Date (dd/mm/yyyy)> ");
            checkinDate = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter Check-out Date (dd/mm/yyyy)> ");
            checkoutDate = inputDateFormat.parse(scanner.nextLine().trim());  
            
            // do if checkin is AFTER checkout --> throw exception, catch beneath also
            
            // call session bean here
            //
            
            /*
            List<RoomType> availableRoomTypes = roomTypeSessionBeanRemote.searchAvailableRoomType(checkinDate, checkoutDate);
            
            // need to print out Room Type name, the name of the rate to be applied (just one) and the actual rate per night $$
            // Should we show number of rooms left able to be booked also? The inventory of room type
            System.out.printf("%9s%22s   %s\n", "Room Type", "Rate Type Applied", "Rate Per Night");
            
            Integer number = 0;
            for(RoomType roomType: availableRoomTypes)
            {
                number += 1;
                System.out.print(number + " ");
                System.out.printf("%14s%22s   %s\n", "Room Type Name", "Rate Type Applied", "Rate Per Night($)");
            }
            
            System.out.println("------------------------");
            System.out.println("1: Make Reservation");
            System.out.println("2: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();
            
            if(response == 1)
            {
                if(currentCustomer != null)
                {
                    doReserveHotelRoom(checkinDate, checkoutDate, availableRoomTypes);
                }
                else
                {
                    System.out.println("Please login first before making a reservation!\n");
                }
            }
            
            */
            
        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        }
        // call doWalkinReserveRoom() somewhere in here
    }
    
    public void doWalkinReserveRoom() {
        
    }
    
    public void doCheckinGuest() {
        
        System.out.println("*** Hotel Reservation System Management Client :: Front Office :: Check-in Guest ***");
        
    }
    
    public void doCheckoutGuest() {
        
        System.out.println("*** Hotel Reservation System Management Client :: Front Office :: Check-out Guest ***");
        
    }
}