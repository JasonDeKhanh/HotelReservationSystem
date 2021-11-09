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
public class RoomTypeNameExistException extends Exception {

    /**
     * Creates a new instance of <code>RoomTypeNameExistException</code> without
     * detail message.
     */
    public RoomTypeNameExistException() {
    }

    /**
     * Constructs an instance of <code>RoomTypeNameExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomTypeNameExistException(String msg) {
        super(msg);
    }
}
