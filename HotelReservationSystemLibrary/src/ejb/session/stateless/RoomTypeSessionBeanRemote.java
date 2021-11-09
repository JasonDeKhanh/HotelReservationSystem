/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DeleteRoomTypeException;
import util.exception.InputDataValidationException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;


@Remote
public interface RoomTypeSessionBeanRemote {
    
    public RoomType createNewRoomType(String nextHigherRoomType, RoomType newRoomType) throws RoomTypeNotFoundException, RoomTypeNameExistException, UnknownPersistenceException, InputDataValidationException;

    public RoomType retrieveRoomTypeByName(String roomTypeName) throws RoomTypeNotFoundException;
    
    public void updateRoomType(RoomType roomType) throws RoomTypeNotFoundException, UpdateRoomTypeException, InputDataValidationException;

    public void deleteRoomType(Long roomTypeId) throws RoomTypeNotFoundException, DeleteRoomTypeException;

    public List<RoomType> retrieveAllRoomTypes();
}
