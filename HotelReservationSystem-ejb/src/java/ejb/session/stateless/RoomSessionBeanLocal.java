/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import javax.ejb.Local;
import util.exception.RoomNotFoundException;

/**
 *
 * @author xqy11
 */
@Local
public interface RoomSessionBeanLocal {

    public Room retrieveRoomByRoomId(Long roomId) throws RoomNotFoundException;

    public Room retrieveRoomByRoomNumber(String roomNumber) throws RoomNotFoundException;
    
}
