/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
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
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.NotEnoughRoomException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author xqy11
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB(name = "PartnerSessionBeanLocal")
    private PartnerSessionBeanLocal partnerSessionBeanLocal;

    @EJB(name = "RoomSessionBeanLocal")
    private RoomSessionBeanLocal roomSessionBeanLocal;

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
    public Reservation createNewReservation(Reservation reservationEntity, String roomTypeName, Long guestID) throws RoomTypeNotFoundException, UnknownPersistenceException, InputDataValidationException, GuestNotFoundException{
        RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByName(roomTypeName);
        Guest guest = guestSessionBeanLocal.retrieveGuestById(guestID);

        // also delete the roomTypeName and guestId parameter up there later
        
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
    public Reservation reserveNewReservation(Reservation newReservation, String roomTypeName, Long guestId) throws RoomTypeNotFoundException, GuestNotFoundException, NotEnoughRoomException, UnknownPersistenceException, InputDataValidationException, ParseException, ReservationNotFoundException {
        
        RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByName(roomTypeName);
        Guest guest = guestSessionBeanLocal.retrieveGuestById(guestId);
        
        Date checkinDate = newReservation.getCheckinDate();
        Date checkoutDate = newReservation.getCheckoutDate();
        
        
                
        Integer numberOfRoomsAvailable = roomTypeSessionBeanLocal.getNumberOfRoomsThisRoomTypeAvailableForReserve(checkinDate, checkoutDate, guestId);
        
        if(numberOfRoomsAvailable < newReservation.getNoOfRoom()) 
        {
            throw new NotEnoughRoomException("There is not enough number of rooms available for booking!");
        }
        
        BigDecimal totalAmountPerRoom = roomTypeSessionBeanLocal.getReservationAmount(checkinDate, checkoutDate, newReservation.getType(), roomType.getRoomTypeId());
        newReservation.setPrice(totalAmountPerRoom.multiply(new BigDecimal(newReservation.getNoOfRoom())));
//        newReservation.setPrice(new BigDecimal(17891789));
        
        // associate
        newReservation = createNewReservation(newReservation, roomTypeName, guestId);
        
        // if reservation made after 2 am, then call allocation method;
        //
        // if curDate = check in date && curTime after 2am
        // call allocation
        
        Date resDate = newReservation.getCheckinDate();
        Date curDate = new Date();
        if((curDate.getDate()==resDate.getDate() && curDate.getMonth()==resDate.getMonth() && curDate.getYear()==resDate.getYear()) // same date
                && ((curDate.getHours() > 2) // time hour is later than 2o clock
                    || (curDate.getHours()==2 && curDate.getMinutes() > 0))) 
        {
//            System.out.println("hello");
            roomSessionBeanLocal.allocateRoomToReservation(checkinDate);
        }
        
        return newReservation;
    }
    
    @Override
    public Reservation reserveNewReservationThruPartner(Reservation newReservation, String roomTypeName, Long guestId, Long partnerId) throws RoomTypeNotFoundException, GuestNotFoundException, NotEnoughRoomException, UnknownPersistenceException, InputDataValidationException, ParseException, ReservationNotFoundException {
        
        RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByName(roomTypeName);
        Guest guest = guestSessionBeanLocal.retrieveGuestById(guestId);
        
        Date checkinDate = newReservation.getCheckinDate();
        Date checkoutDate = newReservation.getCheckoutDate();
        
        
                
        Integer numberOfRoomsAvailable = roomTypeSessionBeanLocal.getNumberOfRoomsThisRoomTypeAvailableForReserve(checkinDate, checkoutDate, guestId);
        
        if(numberOfRoomsAvailable < newReservation.getNoOfRoom()) 
        {
            throw new NotEnoughRoomException("There is not enough number of rooms available for booking!");
        }
        
        BigDecimal totalAmountPerRoom = roomTypeSessionBeanLocal.getReservationAmount(checkinDate, checkoutDate, newReservation.getType(), roomType.getRoomTypeId());
        newReservation.setPrice(totalAmountPerRoom.multiply(new BigDecimal(newReservation.getNoOfRoom())));
        
        Partner partner = partnerSessionBeanLocal.retrievePartnerById(partnerId);
        newReservation.setPartner(partner);
        partner.getReservations().add(newReservation);
        
        
//        newReservation.setPrice(new BigDecimal(17891789));
        
        // associate
        newReservation = createNewReservation(newReservation, roomTypeName, guestId);
        
        // if reservation made after 2 am, then call allocation method;
        //
        // if curDate = check in date && curTime after 2am
        // call allocation
        
        Date resDate = newReservation.getCheckinDate();
        Date curDate = new Date();
        if((curDate.getDate()==resDate.getDate() && curDate.getMonth()==resDate.getMonth() && curDate.getYear()==resDate.getYear()) // same date
                && ((curDate.getHours() > 2) // time hour is later than 2o clock
                    || (curDate.getHours()==2 && curDate.getMinutes() > 0))) 
        {
//            System.out.println("hello");
            roomSessionBeanLocal.allocateRoomToReservation(checkinDate);
        }
        
        return newReservation;
    }
    
    @Override
    public List<Reservation> retrieveAllReservationsByGuestId(Long guestID) throws GuestNotFoundException
    {
//        Guest guest = guestSessionBeanLocal.retrieveRegisteredGuestByID(guestID);
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.guest.guestId = :inGuestId AND r.partner := inPartner");
        query.setParameter("inPartner", null);
        query.setParameter("inGuestId", guestID);
        
        List<Reservation> reservations = query.getResultList();
        
        reservations.size();
        
        return reservations;
        
//        try
//        {
//            return query.getResultList();
//        }
//        catch(NoResultException | NonUniqueResultException ex)
//        {
//            throw new ReservationNotFoundException("Guest ID " + guestID + " does not exist!");
//        }
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
