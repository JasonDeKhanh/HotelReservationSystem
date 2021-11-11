/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import ws.client.InvalidLoginCredentialException_Exception;
import ws.client.Partner;
import ws.client.PartnerEntityWebService_Service;

/**
 *
 * @author xqy11
 */
public class MainApp {
    PartnerEntityWebService_Service service = new PartnerEntityWebService_Service();
    
    public MainApp() {
    }
    
    private Partner currentPartner = null;
    
    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            
            System.out.println("*** Welcome to Holiday Reservation System Client ***");
            System.out.println("1: Partner Login");
            System.out.println("2: Search Hotel Room");
            System.out.println("3: Exit\n");
            response = 0;
            
            while(response < 1 || response > 3) {
                
                System.out.print("> ");
                
                response = scanner.nextInt();
                
                if(response == 1) {
                    
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");
                        loggedInMenu();
                    } catch(InvalidLoginCredentialException_Exception ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                
                } else if (response == 2) {
                    doSearchHotelRoom();
                    
                }else if (response == 3) {
                    // Exit
                    break;
                    
                } else {
                    System.out.println("Invalid option, please try again!\n");
                
                }               
            } // end while loop to read response
            
            if(response == 3)
            {
                break;
            }
            
        }
//        PartnerEntityWebService_Service service = new PartnerEntityWebService_Service();
//        service.getPartnerEntityWebServicePort().partnerLogin(username, password);
    }
    
    private void doLogin() throws InvalidLoginCredentialException_Exception {
        
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";
        
        System.out.println("*** Hotel Reservation System Reservation Client :: Login ***");
        System.out.print("Enter email> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
//        if(username.length() > 0 && password.length() > 0)
//        {
        currentPartner = service.getPartnerEntityWebServicePort().partnerLogin(username, password); 
//        }
//        else
//        {
//            throw new InvalidLoginCredentialException_Exception("Missing login credential!");
//        }
    }
    
    private void doSearchHotelRoom() {
        
        try {
            
            System.out.println("*** Hotel Reservation System Reservation Client :: Search Hotel Room ***\n");
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
    }
    
    private void loggedInMenu() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to Holiday Reservation System Client ***\n");
            System.out.println("You are login as " + currentPartner.getUsername()+ " \n");
            System.out.println("1: Search Hotel Room");
            // should be part of search
//            System.out.println("2: Reserve Hotel Room");
            System.out.println("2: View My Reservation Details");
            System.out.println("3: View All My Reservations");
            System.out.println("4: Logout\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doSearchHotelRoom();
                }
                else if (response == 2)
                {
                    doViewMyReservationDetails();
                }
                else if (response == 3)
                {
                   doViewAllMyReservations();
                }
                else if (response == 4)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 4)
            {
                break;
            }
        }
    }
    
    private void doViewMyReservationDetails(){
        
        // Reservation reservation;
        // how to search view reservation details??? Like search by what
//        String  = "";
//        Scanner scanner = new Scanner(System.in);
//        Long reservationId = null;
//        
//        System.out.println("*** Hotel Reservation System Reservation Client :: View My Reservation Details ***\n");
//        System.out.print("Enter reservation ID> ");
//        reservationId = new Long(scanner.nextLine().trim());
//        
//        try {
//            Reservation r = reservationSessionBeanRemote.retrieveReservationById(reservationId);
//        } catch (ReservationNotFoundException ex) {
//            System.out.println(ex.getMessage());
    }
    
    private void doViewAllMyReservations() {
        System.out.println("*** Hotel Reservation System Reservation Client :: View All My Reservations ***\n");
        
//        Long guestId = currentGuest.getGuestId();
//        
//        List<Reservation> reservations = null;
//        try {
//            reservations = reservationSessionBeanRemote.retrieveAllReservationsByGuestId(guestId);
//        } catch (GuestNotFoundException ex) {
//            System.out.println(ex.getMessage());
//        }
//        
//        System.out.printf("%40s%40s%40s%40s%40s\n", "Reservation Type", "Room Type", "Check-in Date", "Check-out Date", "Rate");
//        
//        for(Reservation reservation: reservations) 
//        {
//            if(reservation.getPartner()==null){
//            
//                System.out.printf("%40s%40s%40s%40s%40s\n",reservation.getType(), reservation.getRoomType().getName(), 
//                        reservation.getCheckinDate().toString(), reservation.getCheckoutDate().toString(),"");
//            }
//        }
    }
}
