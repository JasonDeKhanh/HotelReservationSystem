/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.RoomAllocationExceptionReport;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.InputDataValidationException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author xqy11
 */
@Stateless
public class RoomAllocationExceptionReportSessionBean implements RoomAllocationExceptionReportSessionBeanRemote, RoomAllocationExceptionReportSessionBeanLocal {

    @EJB(name = "ReservationSessionBeanLocal")
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

     //Added for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public RoomAllocationExceptionReportSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public RoomAllocationExceptionReport createNewRoomAllocationExceptionReport(RoomAllocationExceptionReport rEntity, Long reservationId) throws ReservationNotFoundException, UnknownPersistenceException, InputDataValidationException{
        Reservation reservation = em.find(Reservation.class, reservationId);
        
        if(reservation == null)
        {
            throw new ReservationNotFoundException("Reservation ID " + reservationId + " does not exist!");
        }   
        
        Set<ConstraintViolation<RoomAllocationExceptionReport>>constraintViolations = validator.validate(rEntity);
         
        if(constraintViolations.isEmpty())
        {
            try
            {
                rEntity.setReservation(reservation);
                reservation.setRoomAllocationExceptionReport(rEntity);
                em.persist(rEntity);
                em.flush();

                return rEntity;
            }
            catch(PersistenceException ex)
            {
                
                throw new UnknownPersistenceException(ex.getMessage());
                
            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    public List<RoomAllocationExceptionReport> retriveRoomAllocationExceptionReport(){
        Query query = em.createQuery("SELECT e FROM RoomAllocationExceptionReport e");
        
        return query.getResultList();
    }
    

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomAllocationExceptionReport>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
    
}
