/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import java.util.List;
import javax.ejb.Remote;
import util.exception.GuestNotFoundException;
import util.exception.ReservationNotFoundException;

/**
 *
 * @author xqy11
 */
@Remote
public interface ReservationSessionBeanRemote {

//    public List<Reservation> retrieveAllReservations(String guestID) throws GuestNotFoundException;

   

    public Reservation retrieveReservationById(Long reservationId) throws ReservationNotFoundException;

    public List<Reservation> retrieveAllReservations(Long guestID) throws GuestNotFoundException;
    
}
