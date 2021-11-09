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
public class DeleteRoomException extends Exception{

    /**
     * Creates a new instance of <code>DeleteRoomException</code> without detail
     * message.
     */
    public DeleteRoomException() {
    }

    /**
     * Constructs an instance of <code>DeleteRoomException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteRoomException(String msg) {
        super(msg);
    }
}
