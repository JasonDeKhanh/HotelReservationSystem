/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author msipc
 */
public class NoRoomTypeAvaiableForReservationException extends Exception {

    /**
     * Creates a new instance of
     * <code>NoRoomTypeAvaiableForReservationException</code> without detail
     * message.
     */
    public NoRoomTypeAvaiableForReservationException() {
    }

    /**
     * Constructs an instance of
     * <code>NoRoomTypeAvaiableForReservationException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public NoRoomTypeAvaiableForReservationException(String msg) {
        super(msg);
    }
}
