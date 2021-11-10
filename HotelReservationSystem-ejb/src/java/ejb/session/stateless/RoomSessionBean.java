/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomRate;
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
import util.enumeration.RoomRateType;
import util.exception.DeleteRoomException;
import util.exception.InputDataValidationException;
import util.exception.RoomHasNoRoomRateException;
import util.exception.RoomNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;

/**
 *
 * @author xqy11
 */
@Stateless
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal {

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
    public Room createNewRoom(Room newRoomEntity, String roomTypeName) throws RoomHasNoRoomRateException, RoomNumberExistException, UnknownPersistenceException, InputDataValidationException, RoomTypeNotFoundException{
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
        
        Set<ConstraintViolation<Room>>constraintViolations = validator.validate(newRoomEntity);
         
        if(constraintViolations.isEmpty())
        {
            try
            {
                newRoomEntity.setRoomType(roomType);
                roomType.getRooms().add(newRoomEntity);
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
        
        //delete only when room not in use
        
//        List<SaleTransactionLineItemEntity> saleTransactionLineItemEntities = saleTransactionEntitySessionBeanLocal.retrieveSaleTransactionLineItemsByProductId(productId);
//        
//        if(saleTransactionLineItemEntities.isEmpty())
//        {
//            em.remove(productEntityToRemove);
//        }
//        else
//        {
//            throw new DeleteProductException("Product ID " + productId + " is associated with existing sale transaction line item(s) and cannot be deleted!");
//        }
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
}
