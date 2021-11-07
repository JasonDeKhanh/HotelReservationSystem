/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import javax.ejb.Remote;
import util.exception.RoomTypeNotFoundException;

/**
 *
 * @author msipc
 */
@Remote
public interface RoomTypeSessionBeanRemote {
    
    public RoomType createNewRoomType(String nextHigherRoomType, RoomType newRoomType) throws RoomTypeNotFoundException;
    
}
