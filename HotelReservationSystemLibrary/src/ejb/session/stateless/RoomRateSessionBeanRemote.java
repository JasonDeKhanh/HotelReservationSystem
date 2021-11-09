/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import java.util.List;
import javax.ejb.Remote;
import util.exception.InputDataValidationException;
import util.exception.RoomRateNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author xqy11
 */
@Remote
public interface RoomRateSessionBeanRemote {

    public Long createNewRoomRate(RoomRate newRoomRateEntity) throws UnknownPersistenceException, InputDataValidationException;

    public List<RoomRate> retrieveAllRooms();

    public RoomRate retrieveRoomRatesByRoomRateId(Long roomRateId) throws RoomRateNotFoundException;
    
}
