/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.enumeration.ReservationType;
import util.exception.DeleteRoomTypeException;
import util.exception.InputDataValidationException;
import util.exception.NoRoomTypeAvaiableForReservationException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author msipc
 */
@Local
public interface RoomTypeSessionBeanLocal {

    public RoomType createNewRoomType(String nextHigherRoomType, RoomType newRoomType) throws RoomTypeNotFoundException, RoomTypeNameExistException, UnknownPersistenceException, InputDataValidationException;

    public RoomType retrieveRoomTypeByName(String roomTypeName) throws RoomTypeNotFoundException;
    
    public void updateRoomType(RoomType roomType) throws RoomTypeNotFoundException, UpdateRoomTypeException, InputDataValidationException;

    public void deleteRoomType(Long roomTypeId) throws RoomTypeNotFoundException, DeleteRoomTypeException;

    public List<RoomType> retrieveAllRoomTypes();

    public List<RoomType> searchAvailableRoomTypeForReservation(Date checkinDate, Date checkoutDate) throws NoRoomTypeAvaiableForReservationException, RoomTypeNotFoundException;

    public Integer getTrueInventory(Long roomTypeId) throws RoomTypeNotFoundException;

    public Integer getNumberOfRoomsThisRoomTypeAvailableForReserve(Date checkinDate, Date checkoutDate, Long roomTypeId) throws RoomTypeNotFoundException;

    public BigDecimal getReservationAmount(Date checkinDate, Date checkoutDate, ReservationType reservationType, Long roomTypeId) throws RoomTypeNotFoundException, ParseException;
}
