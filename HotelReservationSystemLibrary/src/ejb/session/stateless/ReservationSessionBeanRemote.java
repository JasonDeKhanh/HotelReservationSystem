/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import java.text.ParseException;
import java.util.List;
import javax.ejb.Remote;
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
@Remote
public interface ReservationSessionBeanRemote {

//    public List<Reservation> retrieveAllReservations(String guestID) throws GuestNotFoundException;

   

    public Reservation retrieveReservationById(Long reservationId) throws ReservationNotFoundException;
    
    public List<Reservation> retrieveAllReservationsByGuestId(Long guestID) throws GuestNotFoundException;

     public Reservation createNewReservation(Reservation reservationEntity, String roomTypeName, Long guestId) throws RoomTypeNotFoundException, UnknownPersistenceException, InputDataValidationException, GuestNotFoundException;

    public Reservation reserveNewReservation(Reservation newReservation, String roomTypeName, Long guestId) throws RoomTypeNotFoundException, GuestNotFoundException, NotEnoughRoomException, UnknownPersistenceException, InputDataValidationException, ParseException, ReservationNotFoundException;
    
   
}
