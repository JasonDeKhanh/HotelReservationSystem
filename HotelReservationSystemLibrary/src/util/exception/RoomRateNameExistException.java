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
public class RoomRateNameExistException extends Exception {

    /**
     * Creates a new instance of <code>RoomRateNameExistException</code> without
     * detail message.
     */
    public RoomRateNameExistException() {
    }

    /**
     * Constructs an instance of <code>RoomRateNameExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomRateNameExistException(String msg) {
        super(msg);
    }
}
