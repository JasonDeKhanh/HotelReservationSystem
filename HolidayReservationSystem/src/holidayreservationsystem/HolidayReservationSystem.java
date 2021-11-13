/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import ws.client.GuestIdentificationNumberExistException_Exception;
import ws.client.GuestNotFoundException_Exception;
import ws.client.InputDataValidationException_Exception;
import ws.client.InvalidLoginCredentialException;
import ws.client.InvalidLoginCredentialException_Exception;
import ws.client.NotEnoughRoomException_Exception;
import ws.client.Partner;
import ws.client.PartnerEntityWebService_Service;
import ws.client.ReservationNotFoundException_Exception;
import ws.client.UnknownPersistenceException_Exception;

/**
 *
 * @author xqy11
 */
public class HolidayReservationSystem {
    
//    private PartnerEntityWebService_Service service = new PartnerEntityWebService_Service();
//    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws GuestNotFoundException_Exception, GuestIdentificationNumberExistException_Exception, UnknownPersistenceException_Exception, InputDataValidationException_Exception, NotEnoughRoomException_Exception, ReservationNotFoundException_Exception {
        
        MainApp mainApp = new MainApp();
        mainApp.runApp();
        
    }
    
    
}
