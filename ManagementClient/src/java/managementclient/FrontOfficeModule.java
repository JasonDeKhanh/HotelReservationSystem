 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managementclient;

import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Employee;
import entity.Guest;
import entity.Reservation;
import entity.RoomType;
import entity.UnregisteredGuest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.AccessRight;
import util.enumeration.ReservationType;
import util.exception.CheckinCheckoutSameDayException;
import util.exception.GuestIdentificationNumberExistException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidAccessRightException;
import util.exception.NoRoomTypeAvaiableForReservationException;
import util.exception.NotEnoughRoomException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author msipc
 */
public class FrontOfficeModule {
    
    // bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    // private attributes for remote session bean here
    // Queue and ConnectionFactory
    private Employee currentEmployee;
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    private GuestSessionBeanRemote guestSessionBeanRemote;
    
    
    public FrontOfficeModule() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    // overloaded constructor
    public FrontOfficeModule(Employee currentEmployee, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, 
            ReservationSessionBeanRemote reservationSessionBeanRemote, GuestSessionBeanRemote guestSessionBeanRemote) {
        this();
        this.currentEmployee = currentEmployee;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.guestSessionBeanRemote = guestSessionBeanRemote;
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
            System.out.print("Enter number of rooms to book> ");
            numberOfRooms = Integer.parseInt(scanner.nextLine().trim());
            // do if checkin is AFTER checkout --> throw exception, catch beneath also
            
            // call session bean here
            //
            
            List<RoomType> availableRoomTypes = roomTypeSessionBeanRemote.searchAvailableRoomTypeForReservation(checkinDate, checkoutDate, numberOfRooms);
            
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
                System.out.printf("%30s%28s   %25s\n", roomType.getName(), roomTypeSessionBeanRemote.getNumberOfRoomsThisRoomTypeAvailableForReserve(checkinDate, checkoutDate, roomType.getRoomTypeId()), roomTypeSessionBeanRemote.getReservationAmount(checkinDate, checkoutDate, ReservationType.ONLINE, roomType.getRoomTypeId()));
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

                    // Ask guest to enter name and identification number
                    String guestName = "";
                    String guestIdentificationNumber = "";
                    System.out.print("Enter guest name> ");
                    guestName = scanner.nextLine().trim();
                    System.out.print("Enter guest identification number> ");
                    guestIdentificationNumber = scanner.nextLine().trim(); 
                    
                    // check if guest already exists, if yes then use him, else create new unregistered guest entity
                    Guest currentGuest = new UnregisteredGuest();
                    try {
                        currentGuest = guestSessionBeanRemote.retrieveRegisteredGuestByIdentificationNumber(guestIdentificationNumber);
                    } catch (GuestNotFoundException ex) {
                        currentGuest = guestSessionBeanRemote.createNewUnregisteredGuestGuest(new UnregisteredGuest(guestName, guestIdentificationNumber));
                    }
                    
                    
                    System.out.print("Enter Room Type Name you want to book> ");
                    roomTypeName = scanner.nextLine().trim();
                    System.out.print("Enter number of rooms you want to book> ");
                    numOfRooms = Integer.parseInt(scanner.nextLine().trim());
                    
                    newReservation.setPrice(BigDecimal.ONE);
                    
                    newReservation.setNoOfRoom(numOfRooms);
                    
                    Set<ConstraintViolation<Reservation>>constraintViolations = validator.validate(newReservation);
                    
                    if(constraintViolations.isEmpty()) {
                        // total amount is set inside the method
                        newReservation = reservationSessionBeanRemote.reserveNewReservation(newReservation, roomTypeName, currentGuest.getGuestId());
                        System.out.println("Reservation with ID " + newReservation.getReservationId() + " successfully created!");
                    } else {
                        showInputDataValidationErrorsForReservation(constraintViolations);
                    }
                }
                else
                {
                    System.out.println("Please login first before making a reservation!\n");
                }
            }
            
            
            
        } 
        catch(NoRoomTypeAvaiableForReservationException | RoomTypeNotFoundException | GuestIdentificationNumberExistException | GuestNotFoundException | NotEnoughRoomException | ReservationNotFoundException ex)
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
        catch(UnknownPersistenceException ex)
        {
            System.out.println("An unknown error has occurred while creating the new employee!: " + ex.getMessage() + "\n");
        }
        catch(InputDataValidationException ex)
        {
            System.out.println(ex.getMessage() + "\n");
        }

    }
    
//    public void doWalkinReserveRoom() {
//        
//    }
    
    public void doCheckinGuest() {
        System.out.println("*** Hotel Reservation System Management Client :: Front Office :: Check-in Guest ***");
        Scanner scanner = new Scanner(System.in);
            
        System.out.print("Enter Guest Identification Number)> ");
        String guestID = scanner.nextLine().trim();
        try{
            String output = guestSessionBeanRemote.guestCheckin(guestID);
            System.out.println(output);
        }catch(GuestNotFoundException ex){
            System.out.println(ex.getMessage().toString());
        }
    }
    
    public void doCheckoutGuest() {
        
        System.out.println("*** Hotel Reservation System Management Client :: Front Office :: Check-out Guest ***");
        System.out.println("Bye, have a nice day :)");
        
    }
    
    //Bean Validation methods
    private void showInputDataValidationErrorsForReservation(Set<ConstraintViolation<Reservation>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
    //Bean Validation methods
    private void showInputDataValidationErrorsForUnregisteredGuest(Set<ConstraintViolation<UnregisteredGuest>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
}