/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import entity.Guest;
import entity.Reservation;
import entity.RoomType;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
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
import util.exception.EmployeeNotFoundException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author xqy11
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB(name = "GuestSessionBeanLocal")
    private GuestSessionBeanLocal guestSessionBeanLocal;

    @EJB(name = "roomTypeSessionBeanLocal")
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    //Added for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public ReservationSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Reservation createNewReservation(Reservation reservationEntity, String roomTypeName, String guestID) throws RoomTypeNotFoundException, UnknownPersistenceException, InputDataValidationException, GuestNotFoundException{
        RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByName(roomTypeName);
        Guest guest = guestSessionBeanLocal.retrieveRegisteredGuestByID(guestID);
        
        Set<ConstraintViolation<Reservation>>constraintViolations = validator.validate(reservationEntity);
         
        if(constraintViolations.isEmpty())
        {
            try
            {
                reservationEntity.setRoomType(roomType);
                reservationEntity.setGuest(guest);
                guest.getReservations().add(reservationEntity);
                em.persist(reservationEntity);
                em.flush();

                return reservationEntity;
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
    
    @Override
    public List<Reservation> retrieveAllReservationsByGuestId(Long guestID) throws GuestNotFoundException
    {
//        Guest guest = guestSessionBeanLocal.retrieveRegisteredGuestByID(guestID);
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.guest.guestId = :inGuestId");
        query.setParameter("inGuestId", guestID);
        
        
        return query.getResultList();
    }
//    
//    @Override
//    public Reservation retrieveReservationsByReservationId(Long reservationId) throws ReservationNotFoundException
//    {
//        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.reservationId = :inReservationId");
//        query.setParameter("inReservationId", reservationId);
//        
//        try
//        {
//            return (Reservation) query.getSingleResult();
//        }
//        catch(NoResultException | NonUniqueResultException ex)
//        {
//            throw new ReservationNotFoundException("Reservation ID " + reservationId + " does not exist!");
//        }
//    }
    
    @Override
    public Reservation retrieveReservationById(Long reservationId) throws ReservationNotFoundException
    {
        Reservation reservation = em.find(Reservation.class, reservationId);
        
        if(reservation != null)
        {
            return reservation;
        }
        else
        {
            throw new ReservationNotFoundException("Reservation ID " + reservationId + " does not exist!");
        }               
    }
    
    

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Reservation>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
    
}
