/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reservationclient;

import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import entity.RegisteredGuest;
import entity.Reservation;
import entity.RoomType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.GuestEmailExistException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author xqy11
 */
public class MainApp {
    
    // validator for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private GuestSessionBeanRemote guestSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    
    private RegisteredGuest currentGuest;
    
    public MainApp() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    public MainApp(GuestSessionBeanRemote guestSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote) {
        this();
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
    }
    
    
    
    
    public void runApp() {
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            
            System.out.println("*** Welcome to Hotel Reservation System Reservation Client ***");
            System.out.println("1: Guest Login");
            System.out.println("2: Register as guest");
            System.out.println("3: Search Hotel Room");
            System.out.println("4: Exit\n");
            response = 0;
            
            while(response < 1 || response > 2) {
                
                System.out.print("> ");
                
                response = scanner.nextInt();
                
                if(response == 1) {
                    
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");
                        loggedInMenu();
                    } catch(InvalidLoginCredentialException ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                
                } else if (response == 2) {
                    doRegisterAsGuest();
                    
                }else if (response == 3) {
                    doSearchHotelRoom();
      
                }else if (response == 4) {
                    // Exit
                    break;
                    
                } else {
                    System.out.println("Invalid option, please try again!\n");
                
                }               
            } // end while loop to read response
            
            if(response == 4)
            {
                break;
            }
            
        }
        
    }
    
    private void doLogin() throws InvalidLoginCredentialException {
        
        Scanner scanner = new Scanner(System.in);
        String email = "";
        String password = "";
        
        System.out.println("*** Hotel Reservation System Reservation Client :: Login ***");
        System.out.print("Enter email> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(email.length() > 0 && password.length() > 0)
        {
            try{
                currentGuest = guestSessionBeanRemote.registeredGuestLogin(email, password);    
            }catch(InvalidLoginCredentialException ex){
                throw new InvalidLoginCredentialException();
            }
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }
    
    private void loggedInMenu() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Hotel Reservation System Reservation Client ***\n");
            System.out.println("You are login as " + currentGuest.getName()+ " \n");
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
//                else if(response == 2)
//                {
//                    doReserveHotelRoom();
//                }
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

    private void doRegisterAsGuest() {
        
        Scanner scanner = new Scanner(System.in);
        RegisteredGuest newRegisteredGuest = new RegisteredGuest();
        
        System.out.println("*** Hotel Reservation System Reservation Client :: Register As Guest ***\n");
        System.out.print("Enter Name> ");
        newRegisteredGuest.setName(scanner.nextLine().trim());
        System.out.print("Enter Identification Number> ");
        newRegisteredGuest.setIdentificationNumber(scanner.nextLine().trim());
        System.out.print("Enter Email> ");
        newRegisteredGuest.setEmail(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        newRegisteredGuest.setPassword(scanner.nextLine().trim());
        
        Set<ConstraintViolation<RegisteredGuest>>constraintViolations = validator.validate(newRegisteredGuest);
        
        if(constraintViolations.isEmpty()) {
            try {
            
            newRegisteredGuest = guestSessionBeanRemote.registerNewRegisteredGuest(newRegisteredGuest);
            System.out.println("Successfully created new room type " + newRegisteredGuest.getName() + " with ID " + newRegisteredGuest.getGuestId()+ "\n");
        
            } 
//            catch (RoomTypeNotFoundException ex) 
//            {
//                System.out.println("An error occurred: " + ex.getMessage());
//            } 
            catch(GuestEmailExistException ex) 
            {
                System.out.println("An error occurred: room type name already exists!\n");
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
            showInputDataValidationErrorsForRegisteredGuestEntity(constraintViolations);
        }
        
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

        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void doReserveHotelRoom(Date checkinDate, Date checkoutDate, List<RoomType> availableRoomTypes) {
        
        Scanner scanner = new Scanner(System.in);
        String response = "";
        String roomTypeName = "";
        Integer numOfRooms = 0;
        // Reservation newReservation = new Reservation();
        // newReservation.setType(ReservationType.ONLINE);
        // newReservation.setCheckinDate(checkinDate);
        // newReservation.setCheckoutDate(checkoutDate);
        
        System.out.print("Enter Room Type Name you want to book> ");
        roomTypeName = scanner.nextLine().trim();
        System.out.print("Enter number of rooms you want to book> ");
        numOfRooms = Integer.parseInt(scanner.nextLine().trim());
        // newReservation.setNumberOfRooms(numOfRooms);
        
        // call session bean to reserve room
        // newReservation = reservationSessionBeanRemote.reserveNewRoom(checkinDate, checkoutDate, 
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doViewMyReservationDetails(){
        
        // Reservation reservation;
        // how to search view reservation details??? Like search by what
//        String  = "";
        Scanner scanner = new Scanner(System.in);
        Long reservationId = null;
        
        System.out.println("*** Hotel Reservation System Reservation Client :: View My Reservation Details ***\n");
        System.out.print("Enter reservation ID> ");
        reservationId = new Long(scanner.nextLine().trim());
        
        try {
            Reservation r = reservationSessionBeanRemote.retrieveReservationById(reservationId);
        } catch (ReservationNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void doViewAllMyReservations() {
        System.out.println("*** Hotel Reservation System Reservation Client :: View All My Reservations ***\n");
        
        Long guestId = currentGuest.getGuestId();
        
        List<Reservation> reservations = null;
        try {
            reservations = reservationSessionBeanRemote.retrieveAllReservations(guestId);
        } catch (GuestNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        
        System.out.printf("%40s%40s%40s%40s%40s\n", "Reservation Type", "Room Type", "Check-in Date", "Check-out Date", "Rate");
        
        for(Reservation reservation: reservations) 
        {
            if(reservation.getPartner()==null){
            
                System.out.printf("%40s%40s%40s%40s%40s\n",reservation.getType(), reservation.getRoomType().getName(), 
                        reservation.getCheckinDate().toString(), reservation.getCheckoutDate().toString(),"");
            }
        }
    }
    
    
    
    //Bean Validation methods
    private void showInputDataValidationErrorsForRegisteredGuestEntity(Set<ConstraintViolation<RegisteredGuest>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
}
