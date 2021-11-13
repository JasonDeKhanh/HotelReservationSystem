/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.client.Guest;
import ws.client.GuestIdentificationNumberExistException_Exception;
import ws.client.GuestNotFoundException_Exception;
import ws.client.InputDataValidationException_Exception;
import ws.client.InvalidLoginCredentialException_Exception;
import ws.client.NoRoomTypeAvaiableForReservationException_Exception;
import ws.client.NotEnoughRoomException_Exception;
import ws.client.ParseException_Exception;
import ws.client.Partner;
import ws.client.PartnerEntityWebService_Service;
import ws.client.Reservation;
import ws.client.ReservationNotFoundException_Exception;
import ws.client.ReservationType;
import ws.client.RoomType;
import ws.client.RoomTypeNotFoundException_Exception;
import ws.client.UnknownPersistenceException_Exception;

/**
 *
 * @author xqy11
 */
public class MainApp {
    PartnerEntityWebService_Service service = new PartnerEntityWebService_Service();
    
    public MainApp() {
    }
    
    private Partner currentPartner = null;
    
    public void runApp() throws GuestNotFoundException_Exception, GuestIdentificationNumberExistException_Exception, InputDataValidationException_Exception, UnknownPersistenceException_Exception, NotEnoughRoomException_Exception, ReservationNotFoundException_Exception, ParseException {
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
                    try{
                        doSearchHotelRoom();
                    }catch( GuestNotFoundException_Exception | NoRoomTypeAvaiableForReservationException_Exception| DatatypeConfigurationException|ParseException_Exception| RoomTypeNotFoundException_Exception ex){
                        System.out.println(ex.getMessage());
                    }
                    
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
    
    private void doSearchHotelRoom() throws NoRoomTypeAvaiableForReservationException_Exception, GuestNotFoundException_Exception, ParseException_Exception, RoomTypeNotFoundException_Exception, DatatypeConfigurationException, GuestIdentificationNumberExistException_Exception, InputDataValidationException_Exception, UnknownPersistenceException_Exception, NotEnoughRoomException_Exception, ReservationNotFoundException_Exception, ParseException {
        
            System.out.println("*** Hotel Reservation System Reservation Client :: Search Hotel Room ***\n");
            Scanner scanner = new Scanner(System.in);
            Integer response = 0;
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            String checkinDate;
            String checkoutDate;
            System.out.print("Enter Check-in Date (dd/mm/yyyy)> ");
            checkinDate = scanner.nextLine().trim();
            System.out.print("Enter Check-out Date (dd/mm/yyyy)> ");
            checkoutDate = scanner.nextLine().trim();
            System.out.print("Enter number of rooms to book> ");
            Integer noOfRoom = Integer.parseInt(scanner.nextLine().trim());

            // do if checkin is AFTER checkout --> throw exception, catch beneath also

            // call session bean here
            //
            System.out.println("HERE=====================");
            List<RoomType> availableRoomTypes = service.getPartnerEntityWebServicePort().partnerSearchRoom(checkinDate, checkoutDate,noOfRoom);
            
            // need to print out Room Type name, the name of the rate to be applied (just one) and the actual rate per night $$
            // Should we show number of rooms left able to be booked also? The inventory of room type
            System.out.printf("%4s", "ID");
            System.out.printf("%30s%28s   %25s\n", "Room Type Name", "Number of Rooms Available", "Total Rate Per Room");
            
            Integer number = 0;
            for(RoomType roomType: availableRoomTypes)
            {
                // need to calculate the total rate here
                // rate type applied depends also. Need to do if-else
                // like do a roomType.getRateToBeApplied() <-- if-else inside there
                number += 1;

                System.out.printf("%4s", roomType.getRoomTypeId());
                System.out.printf("%30s%28s   %25s\n",roomType.getName(), service.getPartnerEntityWebServicePort().getNumberOfRoomsThisRoomTypeAvailableForReserve(checkinDate, checkoutDate, roomType.getRoomTypeId()), service.getPartnerEntityWebServicePort().getReservationAmount(checkinDate, checkoutDate, roomType.getRoomTypeId()));
            }
            
            System.out.println("------------------------");
            System.out.println("1: Make Reservation. (A room type previously shown as avaiable may become unavailable as you make the reservation");
            System.out.println("2: Back\n");
            System.out.print("> ");
            response = Integer.parseInt(scanner.nextLine().trim());
            
            if(response == 1)
            {
                if(currentPartner != null)
                {
                     // String response = "";
                    String roomTypeName = "";
                    Integer numOfRooms = 0;
                    Reservation newReservation = new Reservation();
                    newReservation.setType(ReservationType.ONLINE);
                    
                    Date iDate = inputDateFormat.parse(checkinDate);
//                    XMLGregorianCalendar inDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(iDate);
//                    XMLGregorianCalendar outDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(checkinDate);

                    Date inputDate = inputDateFormat.parse(checkinDate); 
                    Date outputDate = inputDateFormat.parse(checkoutDate); 
                    GregorianCalendar inDate = new GregorianCalendar();
                    inDate.setTime(inputDate);
                    GregorianCalendar outDate = new GregorianCalendar();
                    outDate.setTime(outputDate);
                    
                    XMLGregorianCalendar inDate2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(inDate);
                    XMLGregorianCalendar outDate2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(outDate);

//                    Date inDate = inputDateFormat.parse(checkinDate); 
//                    Date outDate = inputDateFormat.parse(checkoutDate); 
                    
                    newReservation.setCheckinDate(inDate2);
                    newReservation.setCheckoutDate(outDate2);
                    
                     // Ask guest to enter name and identification number
                    String guestName = "";
                    String guestIdentificationNumber = "";
                    System.out.print("Enter Guest Name > ");
                    guestName = scanner.nextLine().trim();
                    System.out.print("Enter Guest Identiication Number > ");
                    guestIdentificationNumber = scanner.nextLine().trim(); 

                    
                    System.out.print("Enter Room Type Name you want to book> ");
                    roomTypeName = scanner.nextLine().trim();
                    System.out.print("Enter number of rooms you want to book> ");
                    numOfRooms = Integer.parseInt(scanner.nextLine().trim());
                    
                    newReservation.setPrice(BigDecimal.ONE);
                    
                    newReservation.setNoOfRoom(numOfRooms);
                    
                    
                    // check if guest already exists, if yes then use him, else create new unregistered guest entity
//                    Guest currentGuest = new UnregisteredGuest(guestName, guestIdentificationNumber);
                    
               
                    //Long guestId = service.getPartnerEntityWebServicePort().createNewUnregisteredGuestGuest(guestName, guestIdentificationNumber);
                    Long guestId = service.getPartnerEntityWebServicePort().createNewUnregisteredGuestGuest(guestName, guestIdentificationNumber);
                    
                    
                    newReservation = service.getPartnerEntityWebServicePort().reserveNewReservation(newReservation, roomTypeName, guestId, currentPartner.getPartnerId());
                    System.out.println("Reservation with ID " + newReservation.getReservationId() + " successfully created!");
                   

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
    
    private void loggedInMenu() throws GuestNotFoundException_Exception, GuestIdentificationNumberExistException_Exception, InputDataValidationException_Exception, UnknownPersistenceException_Exception, NotEnoughRoomException_Exception, ReservationNotFoundException_Exception, ParseException {
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
                    try{
                        doSearchHotelRoom();
                    }catch(NoRoomTypeAvaiableForReservationException_Exception|DatatypeConfigurationException| ParseException_Exception| RoomTypeNotFoundException_Exception ex){
                        System.out.println(ex.getMessage());
                    }
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
        
//        Long guestId = currentPartner.getGuestId();
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
