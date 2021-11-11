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
public class CheckinCheckoutSameDayException extends Exception {

    /**
     * Creates a new instance of <code>CheckinCheckoutSameDayException</code>
     * without detail message.
     */
    public CheckinCheckoutSameDayException() {
    }

    /**
     * Constructs an instance of <code>CheckinCheckoutSameDayException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public CheckinCheckoutSameDayException(String msg) {
        super(msg);
    }
}
