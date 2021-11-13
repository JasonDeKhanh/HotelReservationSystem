/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.RegisteredGuest;
import entity.UnregisteredGuest;
import javax.ejb.Local;
import util.exception.GuestEmailExistException;
import util.exception.GuestIdentificationNumberExistException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author msipc
 */
@Local
public interface GuestSessionBeanLocal {

    public RegisteredGuest registeredGuestLogin(String email, String password) throws InvalidLoginCredentialException;

    public RegisteredGuest registerNewRegisteredGuest(RegisteredGuest newRegisteredGuest) throws GuestEmailExistException, UnknownPersistenceException, InputDataValidationException;
    public Guest retrieveRegisteredGuestByIdentificationNumber(String ID) throws GuestNotFoundException;

    public Guest retrieveGuestById(Long guestId) throws GuestNotFoundException;
    public UnregisteredGuest createNewUnregisteredGuestGuest(UnregisteredGuest newGuest) throws GuestIdentificationNumberExistException, UnknownPersistenceException, InputDataValidationException;
    
}
