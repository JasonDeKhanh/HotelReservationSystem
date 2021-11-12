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
public class GuestIdentificationNumberExistException extends Exception{

    /**
     * Creates a new instance of
     * <code>GuestIdentificationNumberExistException</code> without detail
     * message.
     */
    public GuestIdentificationNumberExistException() {
    }

    /**
     * Constructs an instance of
     * <code>GuestIdentificationNumberExistException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public GuestIdentificationNumberExistException(String msg) {
        super(msg);
    }
}
