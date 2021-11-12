/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomAllocationExceptionReport;
import java.util.List;
import javax.ejb.Remote;
import util.exception.InputDataValidationException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author xqy11
 */
@Remote
public interface RoomAllocationExceptionReportSessionBeanRemote {

    public RoomAllocationExceptionReport createNewRoomAllocationExceptionReport(RoomAllocationExceptionReport rEntity, Long reservationId) throws ReservationNotFoundException, UnknownPersistenceException, InputDataValidationException;

    public List<RoomAllocationExceptionReport> retriveRoomAllocationExceptionReport();
    
}
