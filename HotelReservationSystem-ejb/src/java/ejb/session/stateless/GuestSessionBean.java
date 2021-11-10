/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RegisteredGuest;
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
import util.exception.GuestEmailExistException;
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

    public RegisteredGuest retrieveRegisteredGuestByEmail(String email) throws GuestNotFoundException {
        Query query = em.createQuery("SELECT rg FROM RegisteredGuest rg WHERE rg.email = :inEmail");
        query.setParameter("inEmail", email);
        
        try {
            return (RegisteredGuest) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new GuestNotFoundException("Guest email " + email + " does not exist!");
        }
    }
    
    public RegisteredGuest retrieveRegisteredGuestByID(String ID) throws GuestNotFoundException {
        Query query = em.createQuery("SELECT rg FROM RegisteredGuest rg WHERE rg.identificationNumber = :inID");
        query.setParameter("inID", ID);
        
        try {
            return (RegisteredGuest) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new GuestNotFoundException("Guest ID " + ID + " does not exist!");
        }
    }
    
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
    
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RegisteredGuest>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
