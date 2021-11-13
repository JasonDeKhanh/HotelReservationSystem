/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import util.exception.InputDataValidationException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author msipc
 */
@Stateless
public class TimerSessionBean implements TimerSessionBeanRemote, TimerSessionBeanLocal {

    @EJB(name = "RoomSessionBeanLocal")
    private RoomSessionBeanLocal roomSessionBeanLocal;

    @Schedule(hour = "2", info = "AllocationTimer")
    public void allocateRoomToCurrentDayReservationTimer() throws ReservationNotFoundException, UnknownPersistenceException, InputDataValidationException {
        Date curDate = new Date();
        Date todayDate = new Date(curDate.getYear(), curDate.getMonth(), curDate.getDate());
        
        roomSessionBeanLocal.allocateRoomToReservation(todayDate);
    }
        
}
