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
import javax.ejb.EJB;
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
import util.exception.DeleteRoomRateException;
import util.exception.InputDataValidationException;
import util.exception.RoomNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author xqy11
 */
@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

    @EJB(name = "RoomTypeSessionBeanLocal")
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    //Added for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public RoomRateSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Long createNewRoomRate(RoomRate newRoomRateEntity, String roomTypeName) throws UnknownPersistenceException, InputDataValidationException, RoomTypeNotFoundException{

        RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByName(roomTypeName);

        Set<ConstraintViolation<RoomRate>>constraintViolations = validator.validate(newRoomRateEntity);
         
         if(constraintViolations.isEmpty())
        {
            try
            {
                newRoomRateEntity.setRoomType(roomType);
                roomType.getRoomRates().add(newRoomRateEntity);
                em.persist(newRoomRateEntity);
                em.flush();

                return newRoomRateEntity.getRoomRateId();
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
    public List<RoomRate> retrieveAllRooms()
    {
        Query query = em.createQuery("SELECT r FROM Room r");
        
        return query.getResultList();
    }
    
    @Override
    public RoomRate retrieveRoomRatesByRoomRateId(Long roomRateId) throws RoomRateNotFoundException
    {
        RoomRate room = em.find(RoomRate.class, roomRateId);
        
        if(room != null)
        {
            return room;
        }
        else
        {
            throw new RoomRateNotFoundException("Room Rate ID " + roomRateId + " does not exist!");
        }               
    }
    
    public RoomRate retrieveRoomRateByName(String roomRateName) throws RoomRateNotFoundException {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.name = :inName");
        query.setParameter("inName", roomRateName);
        RoomRate roomRate;
        
        try {
            roomRate = (RoomRate) query.getSingleResult();
            return roomRate;
        } catch (NoResultException ex) {
            throw new RoomRateNotFoundException("Room Rate ID " + roomRateName + " does not exist!");
        }
    }
    
    @Override
    public void updateRoomRate(RoomRate roomRateEntity) throws RoomRateNotFoundException, InputDataValidationException
    {
        if(roomRateEntity != null && roomRateEntity.getRoomRateId()!= null)
        {
            Set<ConstraintViolation<RoomRate>>constraintViolations = validator.validate(roomRateEntity);
        
            if(constraintViolations.isEmpty())
            {
                RoomRate roomEntityToUpdate = retrieveRoomRatesByRoomRateId(roomRateEntity.getRoomRateId());

                
                roomEntityToUpdate.setName(roomRateEntity.getName());
                roomEntityToUpdate.setRateType(roomRateEntity.getRateType());
                roomEntityToUpdate.setRatePerNight(roomRateEntity.getRatePerNight());
                roomEntityToUpdate.setStartDate(roomRateEntity.getStartDate());
                roomEntityToUpdate.setEndDate(roomRateEntity.getEndDate());
                
            }
            else
            {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
        else
        {
            throw new RoomRateNotFoundException("Room Rate ID not provided for room to be updated");
        }
    }
    
    @Override
    public void deleteRoomRate(Long roomRateId) throws RoomRateNotFoundException, DeleteRoomRateException
    {
        RoomRate roomRateEntityToRemove = retrieveRoomRatesByRoomRateId(roomRateId);
        
        

        if(roomRateEntityToRemove.getDisabled())
        {
            em.remove(roomRateEntityToRemove);
        }
        else
        {
            roomRateEntityToRemove.setDisabled(true);
            throw new DeleteRoomRateException("Room Rate ID " + roomRateId + " is associated with existing Room Type and cannot be deleted! It is DISABLED now.");
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomRate>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
