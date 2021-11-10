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
public class RoomHasNoRoomRateException extends Exception{

    /**
     * Creates a new instance of <code>RoomHasNoRoomRateException</code> without
     * detail message.
     */
    public RoomHasNoRoomRateException() {
    }

    /**
     * Constructs an instance of <code>RoomHasNoRoomRateException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomHasNoRoomRateException(String msg) {
        super(msg);
    }
}
