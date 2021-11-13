/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import entity.RoomAllocationExceptionReport;
import entity.RoomRate;
import entity.RoomType;
import java.util.ArrayList;
import java.util.Date;
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
import util.enumeration.RoomAllocationExceptionType;
import util.enumeration.RoomRateType;
import util.enumeration.RoomStatus;
import util.exception.DeleteRoomException;
import util.exception.InputDataValidationException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomHasNoRoomRateException;
import util.exception.RoomNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.RoomTypeDisabledException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;

/**
 *
 * @author xqy11
 */
@Stateless
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal {

    @EJB(name = "RoomAllocationExceptionReportSessionBeanLocal")
    private RoomAllocationExceptionReportSessionBeanLocal roomAllocationExceptionReportSessionBeanLocal;

    @EJB(name = "RoomTypeSessionBeanLocal")
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    //Added for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public RoomSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Room createNewRoom(Room newRoomEntity, String roomTypeName) throws RoomTypeDisabledException, RoomHasNoRoomRateException, 
            RoomNumberExistException, UnknownPersistenceException, InputDataValidationException, RoomTypeNotFoundException{
        RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByName(roomTypeName);
        
        boolean hasPublishedRate = false;
        boolean hasNormalRate = false;
        
        for(RoomRate rr : roomType.getRoomRates()){
            if(rr.getRateType()== RoomRateType.PUBLISHED){
                hasPublishedRate = true;
            }
            
            if(rr.getRateType()== RoomRateType.NORMAL){
                hasNormalRate = true;
            }
        }
        
        if(!hasNormalRate || !hasPublishedRate){
            throw new RoomHasNoRoomRateException("This room type has no room rate.");
        }
        
        if(!roomType.getEnabled()){
            throw new RoomTypeDisabledException("This room type is disabled.");
        }
        
        Set<ConstraintViolation<Room>>constraintViolations = validator.validate(newRoomEntity);
         
        if(constraintViolations.isEmpty())
        {
            try
            {
                newRoomEntity.setRoomType(roomType);
                roomType.getRooms().add(newRoomEntity);
                roomType.setInventory(roomType.getInventory()+1);
                em.persist(newRoomEntity);
                em.flush();

                return newRoomEntity;
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new RoomNumberExistException();
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
    public List<Room> retrieveAllRooms()
    {
        Query query = em.createQuery("SELECT r FROM Room r");
        
        return query.getResultList();
    }
    
    @Override
    public Room retrieveRoomByRoomId(Long roomId) throws RoomNotFoundException
    {
        Room room = em.find(Room.class, roomId);
        
        if(room != null)
        {
            return room;
        }
        else
        {
            throw new RoomNotFoundException("Room ID " + roomId + " does not exist!");
        }               
    }
    
    @Override
    public Room retrieveRoomByRoomNumber(String roomNumber) throws RoomNotFoundException
    {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomNumber = :inRoomNumber");
        query.setParameter("inRoomNumber", roomNumber);
        
        try
        {
            return (Room)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new RoomNotFoundException("Room Number" + roomNumber + " does not exist!");
        }
    }
    
    @Override
    public void updateRoom(Room roomEntity) throws RoomNotFoundException, UpdateRoomException, InputDataValidationException
    {
        if(roomEntity != null && roomEntity.getRoomId()!= null)
        {
            Set<ConstraintViolation<Room>>constraintViolations = validator.validate(roomEntity);
        
            if(constraintViolations.isEmpty())
            {
                Room roomEntityToUpdate = retrieveRoomByRoomId(roomEntity.getRoomId());

//                if(roomEntityToUpdate.getRoomNumber().equals(roomEntity.getRoomNumber()))
//                {
                    roomEntityToUpdate.setRoomNumber(roomEntity.getRoomNumber());
                    roomEntityToUpdate.setRoomStatus(roomEntity.getRoomStatus());
//                }
//                else
//                {
//                    throw new UpdateRoomException("Room Number of the room record to be updated does not match the existing record");
//                }
            }
            else
            {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
        else
        {
            throw new RoomNotFoundException("Room ID not provided for room to be updated");
        }
    }
    
    @Override
    public void deleteRoom(Long roomId) throws RoomNotFoundException, DeleteRoomException
    {
        Room roomEntityToRemove = retrieveRoomByRoomId(roomId);
        
        //!!!!!!!!!!!!!!!-----inventory need to minus one-----
        
        
        //delete only when room not in use
        
        
        if(roomEntityToRemove.getReservations().isEmpty())
        {
            roomEntityToRemove.getRoomType().setInventory(roomEntityToRemove.getRoomType().getInventory()-1);
            roomEntityToRemove.getRoomType().getRooms().remove(roomEntityToRemove);
            roomEntityToRemove.getReservations().clear();
            
            roomEntityToRemove.getRoomType().getRooms().remove(roomEntityToRemove);
//            roomEntityToRemove.setRoomType(null);
            em.remove(roomEntityToRemove);
        }
        else
        {
            roomEntityToRemove.setDisabled(true);
            throw new DeleteRoomException("Room ID " + roomId + " is associated with existing reservation and cannot be deleted. It is Disabled now.");
        }
    }
    
    
    public void allocateRoomToReservation(Date checkinDate) throws ReservationNotFoundException, UnknownPersistenceException, InputDataValidationException {
        
        Query query = em.createQuery("SELECT res FROM Reservation res WHERE res.checkinDate = :inCheckinDate AND res.");
        query.setParameter("inCheckinDate", checkinDate);
        
        List<Reservation> reservations = (List<Reservation>) query.getResultList();
        
        for(Reservation reservation : reservations) {
            
            // Check if reservation is already allocated by checking the size of its rooms list.
            // if size > 0, then it means rooms have been allocated to this reservation, and thus will not go through allocation process again
            Integer currRoomListSize = reservation.getRooms().size();
            if(currRoomListSize == 0) 
            {
                Integer numberOfRooms = reservation.getNoOfRoom();
                List<Room> assignedRooms = new ArrayList<>();
                RoomType roomType = reservation.getRoomType();
                RoomType nextHigherRoomType = roomType.getNextHigherRoomType();

                Query queryRoom = em.createQuery("SELECT r FROM Room r WHERE r.roomType = :inRoomType AND r.roomStatus = :inStatus AND r.disabled = :inDisabled");
                queryRoom.setParameter("inRoomType", roomType);
                queryRoom.setParameter("inStatus", RoomStatus.AVAILABLE);
                queryRoom.setParameter("inDisabled", false);

                // roomsQuery 
                List<Room> roomsTemp = (List<Room>) queryRoom.getResultList();
                List<Room> roomsQuery = new ArrayList<>();
                for(Room room: roomsTemp) {
                    if(isRoomFree(room)) {
                        roomsQuery.add(room);
                    }
                }
                //

                // enough room of desired type for reservation
                if(roomsQuery.size() >= numberOfRooms) 
                {
                    for(Room room: roomsQuery)
                    {
                        assignedRooms.add(room);
                        if(assignedRooms.size() == numberOfRooms) 
                        {
                            break;
                        }
                    }

                } 
                else if(roomsQuery.size() < numberOfRooms && nextHigherRoomType != null) 
                {
                    // obtain list of all free room of the next higher type
                    Query queryRoomHigher = em.createQuery("SELECT r FROM Room r WHERE r.roomType = :inRoomType AND r.roomStatus = :inStatus AND r.disabled = :inDisabled");
                    queryRoomHigher.setParameter("inRoomType", nextHigherRoomType);
                    queryRoomHigher.setParameter("inStatus", RoomStatus.AVAILABLE);
                    queryRoomHigher.setParameter("inDisabled", false);

                    // roomsQuery 
                    List<Room> roomsTemp2 = (List<Room>) queryRoomHigher.getResultList();
                    List<Room> roomsHigherQuery = new ArrayList<>();
                    for(Room room: roomsTemp2) {
                        if(isRoomFree(room)) {
                            roomsHigherQuery.add(room);
                        }
                    }

                    if((roomsQuery.size() + roomsHigherQuery.size()) >= numberOfRooms) // partial upgrade possible
                    {
                        for(Room room: roomsQuery) // add all the free rooms of desired type
                        {
                            assignedRooms.add(room);
                        }

                        // fill in the rest of the number of rooms with rooms of the higher type
                        for(Room roomHigher: roomsHigherQuery)
                        {
                            assignedRooms.add(roomHigher);
                            if(assignedRooms.size() == numberOfRooms) 
                            {
                                break;
                            }
                        }

                        RoomAllocationExceptionReport roomAlloExceptionReport = new RoomAllocationExceptionReport();
                        roomAlloExceptionReport.setType(RoomAllocationExceptionType.FREE_UPGRADE);
                        roomAlloExceptionReport.setReason("Room allocation error, room of higher type was assigned");
                        roomAlloExceptionReport = roomAllocationExceptionReportSessionBeanLocal.createNewRoomAllocationExceptionReport(roomAlloExceptionReport, reservation.getReservationId());

                    }
                    else // nextHigherRoomType exists but not enough total free rooms for reservations
                    {
                        RoomAllocationExceptionReport roomAlloExceptionReport = new RoomAllocationExceptionReport();
                        roomAlloExceptionReport.setType(RoomAllocationExceptionType.NO_UPGRADE);
                        roomAlloExceptionReport.setReason("Room allocation error, no room allocated");
                        roomAlloExceptionReport = roomAllocationExceptionReportSessionBeanLocal.createNewRoomAllocationExceptionReport(roomAlloExceptionReport, reservation.getReservationId());

                    }
                } // when there is a higher room type
                else // higher room type doesn't exist
                {
                    RoomAllocationExceptionReport roomAlloExceptionReport = new RoomAllocationExceptionReport();
                    roomAlloExceptionReport.setType(RoomAllocationExceptionType.NO_UPGRADE);
                    roomAlloExceptionReport.setReason("Room allocation error, no room allocated");
                    roomAlloExceptionReport = roomAllocationExceptionReportSessionBeanLocal.createNewRoomAllocationExceptionReport(roomAlloExceptionReport, reservation.getReservationId());

                }


                // Associating rooms and reservation
                for(Room room: assignedRooms) 
                {
                    reservation.getRooms().add(room);
                    room.getReservations().add(reservation);
                }
            } // else room is allocated, do nothing
            
        } // end for reservations loop
        
    }
    
    private Boolean isRoomFree(Room room){
        List<Reservation> reservations = room.getReservations();
        Boolean isFree = true;
        for(Reservation r : reservations){
            if(r.getCheckoutDate().after(new Date())){
                isFree = false;
                break;
            }
        }
        return isFree;
    }
     
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Room>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }

    public void persist(Object object) {
        em.persist(object);
    }
}
