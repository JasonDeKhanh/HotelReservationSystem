/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.ReservationType;
import util.enumeration.RoomRateType;
import util.enumeration.RoomStatus;
import util.exception.DeleteRoomTypeException;
import util.exception.InputDataValidationException;
import util.exception.NoRoomTypeAvaiableForReservationException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;


@Stateless
public class RoomTypeSessionBean implements RoomTypeSessionBeanRemote, RoomTypeSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    //Added for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public RoomTypeSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    public RoomType retrieveRoomTypeById(Long roomTypeId) throws RoomTypeNotFoundException {
        
        RoomType roomType = em.find(RoomType.class, roomTypeId);
        
        if(roomType != null) {
            return roomType;
        } else {
            throw new RoomTypeNotFoundException("Room Type ID " + roomTypeId + " does not exist!");
        }
        
    }

    @Override
    public RoomType retrieveRoomTypeByName(String roomTypeName) throws RoomTypeNotFoundException {
        Query query = em.createQuery("SELECT rt from RoomType rt WHERE rt.name = :inName");
        query.setParameter("inName", roomTypeName);
        RoomType roomType;
        
        try {
            roomType = (RoomType) query.getSingleResult();
            
            roomType.getNextHigherRoomType();
            // LAZY LOAD THE ROOMRATES HERE TOO ONCE YOU'VE FINISHED ROOM RATES
            roomType.getRoomRates().size();
            
            return roomType;
            
        } catch (NoResultException ex) {
            throw new RoomTypeNotFoundException("Room Type " + roomTypeName + " does not exist!");
        }
           
    }
    
    @Override
    public RoomType createNewRoomType(String nextHigherRoomTypeName, RoomType newRoomType) throws RoomTypeNotFoundException, RoomTypeNameExistException, UnknownPersistenceException, InputDataValidationException {
        
        Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(newRoomType);
        
        if(constraintViolations.isEmpty()) {
            try {
                if(!nextHigherRoomTypeName.equals("None")) {
                    newRoomType.setNextHigherRoomType(retrieveRoomTypeByName(nextHigherRoomTypeName));
                }

                em.persist(newRoomType);
                em.flush();

                return newRoomType;
                
            } 
            catch (RoomTypeNotFoundException ex) {
                throw ex;
            } 
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new RoomTypeNameExistException();
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
            
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    
    
    public void updateRoomType(RoomType roomType) throws RoomTypeNotFoundException, UpdateRoomTypeException, InputDataValidationException {
        
        if(roomType != null && roomType.getRoomTypeId() != null) {
            
            Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(roomType);
            
            if(constraintViolations.isEmpty()) {
                
                RoomType roomtypeToUpdate = retrieveRoomTypeById(roomType.getRoomTypeId());
                
                if(roomtypeToUpdate.getName().equals(roomType.getName())) {
                    roomtypeToUpdate.setName(roomType.getName());
                    roomtypeToUpdate.setDescription(roomType.getDescription());
                    roomtypeToUpdate.setSize(roomType.getSize());
                    roomtypeToUpdate.setBeds(roomType.getBeds());
                    roomtypeToUpdate.setCapacity(roomType.getCapacity());
                    roomtypeToUpdate.setAmenities(roomType.getAmenities());
                    roomtypeToUpdate.setInventory(roomType.getInventory());
                } 
                else 
                {
                    throw new UpdateRoomTypeException("Username of room type record to be updated does not match the existing record");
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
            
        } else {
            throw new RoomTypeNotFoundException("Room Type ID not provided for room type to be updated");
        }
        
    }

    public void deleteRoomType(Long roomTypeId) throws RoomTypeNotFoundException, DeleteRoomTypeException {
        
        RoomType roomTypeToRemove = retrieveRoomTypeById(roomTypeId);
        
        Query query = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.nextHigherRoomType.roomTypeId = :inRoomTypeId");
        query.setParameter("inRoomTypeId", roomTypeToRemove.getRoomTypeId());
        
        try {
            RoomType roomTypeRankBelow = (RoomType) query.getSingleResult();
            throw new DeleteRoomTypeException("Room Type ID " + roomTypeId + " is the next higher level room type for another room type and cannot be deleted! It is now disabled");
        } catch (NoResultException ex) { // if no roomTypeRankBelow then can delete
            if(roomTypeToRemove.getRooms().isEmpty()) {
                for(RoomRate roomRate : roomTypeToRemove.getRoomRates()) {
                em.remove(roomRate);
                }

                em.remove(roomTypeToRemove);
            } else {
                roomTypeToRemove.setEnabled(false);
                throw new DeleteRoomTypeException("Room Type ID " + roomTypeId + " is associated with existing Room(s) and cannot be deleted!");
            }
        }
    }
    
    
    public List<RoomType> retrieveAllRoomTypes() {
        
        Query query = em.createQuery("SELECT rt FROM RoomType rt");
        
        List<RoomType> roomTypes = query.getResultList();
        
        for(RoomType roomType: roomTypes) {
            roomType.getRoomRates().size();
        }
        
        return roomTypes;
    }
    
    
    @Override
    public List<RoomType> searchAvailableRoomTypeForReservation(Date checkinDate, Date checkoutDate) throws NoRoomTypeAvaiableForReservationException, RoomTypeNotFoundException {
        
        /*
            for each Room Type that is enabled, go through all reservations with that room type
            check which one affects the current room reservation
            if that (roomType.inventory - count of room affect reservation) >= numberOfRooms, then
            this roomType is available for booking.
            **need to check again during reservation
            
        */
        List<RoomType> roomTypeToReturn = new ArrayList<>();
        
        Query queryRoomType = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.enabled = :inEnabled");
        queryRoomType.setParameter("inEnabled", true);
        
        List<RoomType> roomTypes = (List<RoomType>) queryRoomType.getResultList();
        
        if(roomTypes.isEmpty()) {
            throw new NoRoomTypeAvaiableForReservationException("There is no available room type available");
        }
        
        for(RoomType roomType: roomTypes) {
//            Query query = em.createQuery("SELECT res FROM Reservation res WHERE res.roomType = :inRoomType");
//            query.setParameter("inRoomType", roomType);
//        
//            List<Reservation> reservations = (List<Reservation>) query.getResultList();
//
////            if (reservations.isEmpty()) {
////                throw new NoRoomTypeAvaiableForReservationException("There is no avaialble room type available for reserving!");
////            } 
//
//            Integer countReservationsAffecting = 0;
//
//            for(Reservation otherReservation: reservations) {
//                Date otherCheckinDate = otherReservation.getCheckinDate();
//                Date otherCheckoutDate = otherReservation.getCheckoutDate();
//
//                if ( (otherCheckinDate.before(checkinDate) || otherCheckinDate.equals(checkinDate)) 
//                        && (otherCheckoutDate.after(checkinDate))) {
//
//                    countReservationsAffecting += 1; // affect reservation
//
//                } else if ( (otherCheckinDate.after(checkinDate) || otherCheckinDate.equals(checkinDate))
//                            && (otherCheckoutDate.before(checkoutDate)||otherCheckoutDate.equals(checkoutDate))) {
//
//                    countReservationsAffecting += 1; // affect reservation
//
//                } else if ( (otherCheckinDate.before(checkinDate) && 
//                        (otherCheckoutDate.before(checkinDate) || otherCheckoutDate.equals(checkinDate)))) {
//
//                    // do nothing
//
//                } else if ( (otherCheckinDate.after(checkoutDate) || otherCheckinDate.equals(checkoutDate)) 
//                        && (otherCheckoutDate.after(checkoutDate))) {
//
//                    // do nothing
//
//                } else if ( (otherCheckinDate.before(checkoutDate)) 
//                        && ( otherCheckoutDate.after(checkoutDate) || otherCheckoutDate.equals(checkoutDate) )) {
//
//                    countReservationsAffecting += 1; // affect reservation
//
//                }
//                
//                
//            }
            
            Integer numberOfRoomsThisRoomTypeAvailable = getNumberOfRoomsThisRoomTypeAvailableForReserve(checkinDate, checkoutDate, roomType.getRoomTypeId());
//            System.out.println("Roomtype: " + roomType.getName() + " with numberAvailable: " + numberOfRoomsThisRoomTypeAvailable);
//            if(numberOfRoomsThisRoomTypeAvailable >= numberOfRooms) {
//                roomTypeToReturn.add(roomType);
//            }
            if(numberOfRoomsThisRoomTypeAvailable > 0) {
                roomTypeToReturn.add(roomType);
            }
        }
        
        if(roomTypeToReturn.isEmpty()) {
            throw new NoRoomTypeAvaiableForReservationException("There is no avaialble room type available for reserving!");
        } else {
            return roomTypeToReturn;
        }
    }
    
    
    public Integer getNumberOfRoomsThisRoomTypeAvailableForReserve(Date checkinDate, Date checkoutDate, Long roomTypeId) throws RoomTypeNotFoundException {
        
        RoomType roomType = retrieveRoomTypeById(roomTypeId);
        
        Query query = em.createQuery("SELECT res FROM Reservation res WHERE res.roomType = :inRoomType");
        query.setParameter("inRoomType", roomType);

        List<Reservation> reservations = (List<Reservation>) query.getResultList();

//            if (reservations.isEmpty()) {
//                throw new NoRoomTypeAvaiableForReservationException("There is no avaialble room type available for reserving!");
//            } 

        Integer countReservationsAffecting = 0;

        for(Reservation otherReservation: reservations) {
            Date otherCheckinDate = otherReservation.getCheckinDate();
            Date otherCheckoutDate = otherReservation.getCheckoutDate();

            if ( (otherCheckinDate.before(checkinDate) || otherCheckinDate.equals(checkinDate)) 
                    && (otherCheckoutDate.after(checkinDate))) {

                countReservationsAffecting += 1; // affect reservation

            } else if ( (otherCheckinDate.after(checkinDate) || otherCheckinDate.equals(checkinDate))
                        && (otherCheckoutDate.before(checkoutDate)||otherCheckoutDate.equals(checkoutDate))) {

                countReservationsAffecting += 1; // affect reservation

            } else if ( (otherCheckinDate.before(checkinDate) && 
                    (otherCheckoutDate.before(checkinDate) || otherCheckoutDate.equals(checkinDate)))) {

                // do nothing

            } else if ( (otherCheckinDate.after(checkoutDate) || otherCheckinDate.equals(checkoutDate)) 
                    && (otherCheckoutDate.after(checkoutDate))) {

                // do nothing

            } else if ( (otherCheckinDate.before(checkoutDate)) 
                    && ( otherCheckoutDate.after(checkoutDate) || otherCheckoutDate.equals(checkoutDate) )) {

                countReservationsAffecting += 1; // affect reservation

            }


        }

        Integer numberOfRoomsThisRoomTypeAvailable = getTrueInventory(roomType.getRoomTypeId()) - countReservationsAffecting;
//        System.out.println("Roomtype: " + roomType.getName() + " with numberAvailable: " + numberOfRoomsThisRoomTypeAvailable);
        
        return numberOfRoomsThisRoomTypeAvailable;
//        if(numberOfRoomsThisRoomTypeAvailable >= numberOfRooms) {
//            roomTypeToReturn.add(roomType);
//        }
    }
    
    public Integer getTrueInventory(Long roomTypeId) throws RoomTypeNotFoundException {
        RoomType roomType = retrieveRoomTypeById(roomTypeId);
        
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomType = :inRoomType AND r.roomStatus = :inStatus AND r.disabled = :inDisabled");
        query.setParameter("inRoomType", roomType);
        query.setParameter("inStatus", RoomStatus.AVAILABLE);
        query.setParameter("inDisabled", false);
        
        List<Room> rooms = (List<Room>) query.getResultList();
        
        return rooms.size(); //roomType.getInventory() - rooms.size();
    }
    
    
    @Override
    public BigDecimal getReservationAmount(Date checkinDate, Date checkoutDate, ReservationType reservationType, Long roomTypeId) throws RoomTypeNotFoundException, ParseException {
        
        RoomType roomType = retrieveRoomTypeById(roomTypeId);
        
        BigDecimal finalAmount = new BigDecimal(0);
//        List<RoomRate> roomRates = roomType.getRoomRates();

//        Query query 

        Date tempDate = checkinDate;

        if(reservationType.equals(ReservationType.ONLINE)) 
        {
            
            while(tempDate.before(checkoutDate)) {
                
                Boolean addedToday = false;
                List<RoomRate> roomRates;
                Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.roomType = :inRoomType AND rr.rateType = :inRateType");
                query.setParameter("inRoomType", roomType);
                query.setParameter("inRateType", RoomRateType.PROMOTION);
                roomRates = (List<RoomRate>) query.getResultList();
                
                if(!roomRates.isEmpty()) {
                    
                    for(RoomRate roomRate: roomRates) {
                        if((roomRate.getStartDate().before(tempDate)||roomRate.getStartDate().equals(tempDate)) 
                                && (roomRate.getEndDate().after(tempDate)||roomRate.getEndDate().equals(tempDate))) {
                            finalAmount = finalAmount.add(roomRate.getRatePerNight());
                            addedToday = true;
                            break;
                        }
                    }
                    
                } 
                if (!addedToday) {
                    Query query2 = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.roomType = :inRoomType AND rr.rateType = :inRateType");
                    query2.setParameter("inRoomType", roomType);
                    query2.setParameter("inRateType", RoomRateType.PEAK);
                    roomRates = (List<RoomRate>) query2.getResultList();
                    
                    for(RoomRate roomRate: roomRates) {
                        if((roomRate.getStartDate().before(tempDate)||roomRate.getStartDate().equals(tempDate)) 
                                && (roomRate.getEndDate().after(tempDate)||roomRate.getEndDate().equals(tempDate))) {
                            finalAmount = finalAmount.add(roomRate.getRatePerNight());
                            addedToday = true;
                            break;
                        }
                    }
                    
                } 
                
                if (!addedToday) {
                    Query query2 = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.roomType = :inRoomType AND rr.rateType = :inRateType");
                    query2.setParameter("inRoomType", roomType);
                    query2.setParameter("inRateType", RoomRateType.NORMAL);
                    RoomRate normalRate = (RoomRate) query2.getSingleResult();
                    finalAmount = finalAmount.add(normalRate.getRatePerNight());
                }
                
                tempDate = addDays(tempDate, 1);
            }
        } 
        else 
        { // Walk-in Reservation
            
            Query query2 = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.roomType = :inRoomType AND rr.rateType = :inRateType");
            query2.setParameter("inRoomType", roomType);
            query2.setParameter("inRateType", RoomRateType.PUBLISHED);
            RoomRate publishedRate = (RoomRate) query2.getSingleResult();
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Date firstDate = sdf.parse(checkinDate.toString());
            Date secondDate = sdf.parse(checkoutDate.toString());

            long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) - 1;
            
            finalAmount = new BigDecimal(diff).multiply(publishedRate.getRatePerNight());
            
        }
        
        return finalAmount;
    }
    
    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
    
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomType>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
