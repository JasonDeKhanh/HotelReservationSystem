/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author msipc
 */
@Stateless
public class RoomTypeSessionBean implements RoomTypeSessionBeanRemote, RoomTypeSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    
    
    public RoomTypeSessionBean() {
    }

    public RoomType retrieveRoomTypeByName(String roomTypeName) throws RoomTypeNotFoundException {
        Query query = em.createQuery("SELECT rt from RoomType rt WHERE rt.name = :inName");
        query.setParameter("inName", roomTypeName);
        
        try {
            return (RoomType) query.getSingleResult();
            
        } catch (NoResultException ex) {
            throw new RoomTypeNotFoundException("Room Type " + roomTypeName + " does not exist!");
        }
           
    }
    
    @Override
    public RoomType createNewRoomType(String nextHigherRoomType, RoomType newRoomType) throws RoomTypeNotFoundException {
        
        try {
            if(!nextHigherRoomType.equals("None")) {
        
            newRoomType.setNextHigherRoomType(retrieveRoomTypeByName(nextHigherRoomType));
        
            }

            em.persist(newRoomType);
            em.flush();

            return newRoomType;
        } catch (RoomTypeNotFoundException ex) {
            throw ex;
        }
    }
    

    
}
