/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.ReservationType;
import util.exception.InvalidLoginCredentialException;
import util.exception.NoRoomTypeAvaiableForReservationException;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author xqy11
 */
@WebService(serviceName = "PartnerEntityWebService")
@Stateless()
public class PartnerEntityWebService {

    @EJB(name = "RoomTypeSessionBeanLocal")
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @EJB(name = "PartnerSessionBeanLocal")
    private PartnerSessionBeanLocal partnerSessionBeanLocal;
    
    
    
    

    /**
     * This is a sample web service operation
     * @param username
     * @param password
     * @return 
     */
    @WebMethod(operationName = "partnerLogin")
    public Partner partnerLogin(@WebParam(name = "username") String username, @WebParam(name = "password") String password) throws InvalidLoginCredentialException {
        Partner partner = partnerSessionBeanLocal.partnerLogin(username, password);
        
        em.detach(partner);
        
        for(Reservation r : partner.getReservations()){
            em.detach(r);
            r.setPartner(null);
        }
       
        return partner;
    }


    /**
     * Web service operation
     * @param checkinDate
     * @param checkoutDate
     * @return 
     * @throws java.text.ParseException
     * @throws util.exception.NoRoomTypeAvaiableForReservationException
     * @throws util.exception.RoomTypeNotFoundException
     */
    @WebMethod(operationName = "partnerSearchRoom")
    public List<RoomType> partnerSearchRoom(@WebParam(name = "checkinDate") String checkinDate, @WebParam(name = "checkoutDate") String checkoutDate) throws ParseException, NoRoomTypeAvaiableForReservationException, RoomTypeNotFoundException {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
  
        Date inDate = inputDateFormat.parse(checkinDate);

        Date outDate = inputDateFormat.parse(checkoutDate); 
        
        List<RoomType> availableRoomTypes = roomTypeSessionBeanLocal.searchAvailableRoomTypeForReservation(inDate, outDate);
        
        for(RoomType roomType : availableRoomTypes){
            em.detach(roomType);
//            roomType.getRooms().clear();
//            roomType.getRoomRates().clear();
            for(RoomRate rateRate: roomType.getRoomRates()){
                em.detach(rateRate);
                rateRate.setRoomType(null);
            }
            
            for(Room room: roomType.getRooms()){
                em.detach(room);
                room.setRoomType(null);
            }
        }
        
        
        return availableRoomTypes;
    }
    
    @WebMethod(operationName = "getNumberOfRoomsThisRoomTypeAvailableForReserve")
    public Integer getNumberOfRoomsThisRoomTypeAvailableForReserve(@WebParam(name = "checkinDate")String checkinDate, 
            @WebParam(name = "checkoutDate") String checkoutDate, @WebParam(name = "roomTypeId") Long roomTypeId) throws ParseException, RoomTypeNotFoundException{
        
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
  
        Date inDate = inputDateFormat.parse(checkinDate);

        Date outDate = inputDateFormat.parse(checkoutDate); 
        
        Integer result = roomTypeSessionBeanLocal.getNumberOfRoomsThisRoomTypeAvailableForReserve(inDate, outDate, roomTypeId);
        
        return result;
    }
            
    
    //getReservationAmount
    @WebMethod(operationName = "getReservationAmount")
    public BigDecimal getReservationAmount(@WebParam(name = "checkinDate")String checkinDate, 
            @WebParam(name = "checkoutDate") String checkoutDate, @WebParam(name = "roomTypeId") Long roomTypeId) throws ParseException, RoomTypeNotFoundException{
        
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
  
        Date inDate = inputDateFormat.parse(checkinDate);

        Date outDate = inputDateFormat.parse(checkoutDate); 
        
        BigDecimal result = roomTypeSessionBeanLocal.getReservationAmount(inDate, outDate, ReservationType.ONLINE, roomTypeId);
        
        return result;
    }
    
    //create unregistred guest
    

}
