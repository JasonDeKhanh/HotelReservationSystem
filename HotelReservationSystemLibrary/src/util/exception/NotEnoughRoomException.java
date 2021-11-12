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
public class NotEnoughRoomException extends Exception {

    /**
     * Creates a new instance of <code>NotEnoughRoomException</code> without
     * detail message.
     */
    public NotEnoughRoomException() {
    }

    /**
     * Constructs an instance of <code>NotEnoughRoomException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NotEnoughRoomException(String msg) {
        super(msg);
    }
}
