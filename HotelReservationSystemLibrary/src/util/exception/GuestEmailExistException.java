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
public class GuestEmailExistException extends Exception {

    /**
     * Creates a new instance of <code>GuestEmailExistException</code> without
     * detail message.
     */
    public GuestEmailExistException() {
    }

    /**
     * Constructs an instance of <code>GuestEmailExistException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public GuestEmailExistException(String msg) {
        super(msg);
    }
}
