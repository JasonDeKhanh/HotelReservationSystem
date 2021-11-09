/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.util.List;
import java.util.Set;
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
import util.exception.DeleteRoomTypeException;
import util.exception.InputDataValidationException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author msipc
 */
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
            // roomType.getRoomRates().size();
            
            return roomType;
            
        } catch (NoResultException ex) {
            throw new RoomTypeNotFoundException("Room Type " + roomTypeName + " does not exist!");
        }
           
    }
    
    @Override
    public RoomType createNewRoomType(String nextHigherRoomType, RoomType newRoomType) throws RoomTypeNotFoundException, RoomTypeNameExistException, UnknownPersistenceException, InputDataValidationException {
        
        Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(newRoomType);
        
        if(constraintViolations.isEmpty()) {
            try {
                if(!nextHigherRoomType.equals("None")) {
                    newRoomType.setNextHigherRoomType(retrieveRoomTypeByName(nextHigherRoomType));
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
    
    
    public List<RoomType> retrieveAllRoomTypes() {
        
        Query query = em.createQuery("SELECT rt FROM RoomType");
        
        return query.getResultList();
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
