/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author xqy11
 */
public class RoomTypeDisabledException extends Exception{

    /**
     * Creates a new instance of <code>RoomTypeDisabledException</code> without
     * detail message.
     */
    public RoomTypeDisabledException() {
    }

    /**
     * Constructs an instance of <code>RoomTypeDisabledException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomTypeDisabledException(String msg) {
        super(msg);
    }
}
