/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.RegisteredGuest;
import entity.Reservation;
import entity.Room;
import entity.UnregisteredGuest;
import java.util.Date;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.RoomAllocationExceptionType;
import util.exception.GuestEmailExistException;
import util.exception.GuestIdentificationNumberExistException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author msipc
 */
@Stateless
public class GuestSessionBean implements GuestSessionBeanRemote, GuestSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    // validator for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public GuestSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    public Guest retrieveGuestById(Long guestId) throws GuestNotFoundException {
        
        Guest guest = em.find(Guest.class, guestId);
        
        if(guest != null)
        {
            return guest;
        }
        else
        {
            throw new GuestNotFoundException("Guest ID " + guestId + " does not exist!");
        }   
    }

    public RegisteredGuest retrieveRegisteredGuestByEmail(String email) throws GuestNotFoundException {
        Query query = em.createQuery("SELECT rg FROM RegisteredGuest rg WHERE rg.email = :inEmail");
        query.setParameter("inEmail", email);
        
        try {
            return (RegisteredGuest) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new GuestNotFoundException("Guest email " + email + " does not exist!");
        }
    }
    
    @Override
    public Guest retrieveRegisteredGuestByIdentificationNumber(String ID) throws GuestNotFoundException {
        Query query = em.createQuery("SELECT rg FROM RegisteredGuest rg WHERE rg.identificationNumber = :inID");
        query.setParameter("inID", ID);
        
        try {
            return (Guest) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new GuestNotFoundException("Guest ID " + ID + " does not exist!");
        }
    }
    
    @Override
    public RegisteredGuest registeredGuestLogin(String email, String password) throws InvalidLoginCredentialException {
        try
        {
            RegisteredGuest registeredGuest = retrieveRegisteredGuestByEmail(email);
            
            if(registeredGuest.getPassword().equals(password))
            {
//                employee.getSaleTransactionEntities().size();                
                return registeredGuest;
            }
            else
            {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        }
        catch(GuestNotFoundException ex)
        {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }
    
    
    @Override
    public RegisteredGuest registerNewRegisteredGuest(RegisteredGuest newRegisteredGuest) throws GuestEmailExistException, UnknownPersistenceException, InputDataValidationException {
    
        Set<ConstraintViolation<RegisteredGuest>>constraintViolations = validator.validate(newRegisteredGuest);
    
        if(constraintViolations.isEmpty()) {
            
            try {
                em.persist(newRegisteredGuest);
                em.flush();
                
                return newRegisteredGuest;
            } 
            catch(PersistenceException ex)
            {
                
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new GuestEmailExistException();
                    }
                    else
                    {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }

            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public UnregisteredGuest createNewUnregisteredGuestGuest(UnregisteredGuest newGuest) throws GuestIdentificationNumberExistException, UnknownPersistenceException, InputDataValidationException {
    
        Set<ConstraintViolation<UnregisteredGuest>>constraintViolations = validator.validate(newGuest);
    
        if(constraintViolations.isEmpty()) {
            
            try {
                em.persist(newGuest);
                em.flush();
                
                return newGuest;
            } 
            catch(PersistenceException ex)
            {
                
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new GuestIdentificationNumberExistException();
                    }
                    else
                    {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }

            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessageForUnregisteredGuest(constraintViolations));
        }
    }
    
    public String guestCheckin(String guestID) throws GuestNotFoundException{
        Guest guest = retrieveRegisteredGuestByIdentificationNumber(guestID);
        Boolean checkedIn = false;
        String output ="";
        for(Reservation r : guest.getReservations()){
            if(r.getCheckinDate().equals(new Date())){
                if(r.getRooms().size()>0){
                    for(Room room: r.getRooms()){
                        output+="Allocated room: "+room.getRoomNumber()+". \n";
                    }
                }else if(r.getRoomAllocationExceptionReport()!=null){
                    if(r.getRoomAllocationExceptionReport().getType()==RoomAllocationExceptionType.NO_UPGRADE){
                        output = "Sorry, we do not have enough room to allocate to you!";
                    }
                } else{
                    output="Something went wrong.";
                }
                checkedIn = true;
            }
        }
        if(checkedIn == false){
            output="You dont have reservation today.";
        }
        return output;
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RegisteredGuest>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
    
     private String prepareInputDataValidationErrorsMessageForUnregisteredGuest(Set<ConstraintViolation<UnregisteredGuest>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
