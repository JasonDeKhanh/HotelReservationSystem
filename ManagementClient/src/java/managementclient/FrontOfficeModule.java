 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managementclient;

import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Employee;
import entity.Reservation;
import entity.RoomType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import util.enumeration.AccessRight;
import util.enumeration.ReservationType;
import util.exception.CheckinCheckoutSameDayException;
import util.exception.InvalidAccessRightException;
import util.exception.NoRoomTypeAvaiableForReservationException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author msipc
 */
public class FrontOfficeModule {
    
    // private attributes for remote session bean here
    // Queue and ConnectionFactory
    private Employee currentEmployee;
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    
    
    public FrontOfficeModule() {
    }

    // overloaded constructor
    public FrontOfficeModule(Employee currentEmployee, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote) {
        this.currentEmployee = currentEmployee;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
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
            
            System.out.println("*** Hotel Reservation System Front Office Client :: Walkin Search Hotel Room ***\n");
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            Date checkinDate;
            Date checkoutDate;
            Integer numberOfRooms;
            
            System.out.print("Enter Check-in Date (dd/mm/yyyy)> ");
            checkinDate = inputDateFormat.parse(scanner.nextLine().trim());
            System.out.print("Enter Check-out Date (dd/mm/yyyy)> ");
            checkoutDate = inputDateFormat.parse(scanner.nextLine().trim());  
            if(checkinDate.equals(checkoutDate)){
                throw new CheckinCheckoutSameDayException("Check-in Date and Check-out Date cannot be the same day");
            }
//            System.out.print("Enter number of rooms to book> ");
//            numberOfRooms = Integer.parseInt(scanner.nextLine().trim());
            // do if checkin is AFTER checkout --> throw exception, catch beneath also
            
            // call session bean here
            //
            
            List<RoomType> availableRoomTypes = roomTypeSessionBeanRemote.searchAvailableRoomTypeForReservation(checkinDate, checkoutDate);
            
            // need to print out Room Type name, the name of the rate to be applied (just one) and the actual rate per night $$
            // Should we show number of rooms left able to be booked also? The inventory of room type
            System.out.printf("%2s", "");
            System.out.printf("%14s%22s   %s\n", "Room Type", "Number of Rooms Available", "Rate Per Night");
            
            Integer number = 0;
            for(RoomType roomType: availableRoomTypes)
            {
                // need to calculate the total rate here
                // rate type applied depends also. Need to do if-else
                // like do a roomType.getRateToBeApplied() <-- if-else inside there
                number += 1;

                System.out.printf("%2s", number);
                System.out.println("yo");
                System.out.printf("%14s%22s   %s\n", roomType.getName(), roomTypeSessionBeanRemote.getNumberOfRoomsThisRoomTypeAvailableForReserve(checkinDate, checkoutDate, roomType.getRoomTypeId()), roomTypeSessionBeanRemote.getReservationAmount(checkinDate, checkoutDate, ReservationType.WALKIN, roomType.getRoomTypeId()));
                System.out.println("yoyo");
            }
            
            System.out.println("------------------------");
            System.out.println("1: Make Reservation. (A room type previously shown as avaiable may become unavailable as you make the reservation");
            System.out.println("2: Back\n");
            System.out.print("> ");
            response = Integer.parseInt(scanner.nextLine().trim());
            
            if(response == 1)
            {
                if(currentEmployee != null)
                {
                    // String response = "";
                    String roomTypeName = "";
                    Integer numOfRooms = 0;
                    Reservation newReservation = new Reservation();
                    newReservation.setType(ReservationType.WALKIN);
                    newReservation.setCheckinDate(checkinDate);
                    newReservation.setCheckoutDate(checkoutDate);

                    System.out.print("Enter Room Type Name you want to book> ");
                    roomTypeName = scanner.nextLine().trim();
                    System.out.print("Enter number of rooms you want to book> ");
                    numOfRooms = Integer.parseInt(scanner.nextLine().trim());
                    
                    newReservation.setNoOfRoom(numOfRooms);
                    
                    //newReservation = reservationSessionBeanRemote.createNewReservation(newReservation, roomTypeName, currentGuest.getGuestId());
                }
                else
                {
                    System.out.println("Please login first before making a reservation!\n");
                }
            }
            
            
            
        } 
        catch(NoRoomTypeAvaiableForReservationException | RoomTypeNotFoundException ex)
        {
            System.out.println("An error occurred: " + ex.getMessage());
        }
        catch (CheckinCheckoutSameDayException ex)
        {
            System.out.println(ex.getMessage() + "\n");
        }
        catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        } 

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