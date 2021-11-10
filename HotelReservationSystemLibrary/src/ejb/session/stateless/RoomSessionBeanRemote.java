/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DeleteRoomException;
import util.exception.InputDataValidationException;
import util.exception.RoomHasNoRoomRateException;
import util.exception.RoomNotFoundException;
import util.exception.RoomNumberExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;

/**
 *
 * @author xqy11
 */
@Remote
public interface RoomSessionBeanRemote {

    public List<Room> retrieveAllRooms();
    
    public Room retrieveRoomByRoomId(Long roomId) throws RoomNotFoundException;
    
    public Room retrieveRoomByRoomNumber(String roomNumber) throws RoomNotFoundException;

    public void updateRoom(Room roomEntity) throws RoomNotFoundException, UpdateRoomException, InputDataValidationException;

    public void deleteRoom(Long roomId) throws RoomNotFoundException, DeleteRoomException;

    public Room createNewRoom(Room newRoomEntity, String roomTypeName) throws RoomHasNoRoomRateException, RoomNumberExistException, UnknownPersistenceException, InputDataValidationException, RoomTypeNotFoundException;
    
}
