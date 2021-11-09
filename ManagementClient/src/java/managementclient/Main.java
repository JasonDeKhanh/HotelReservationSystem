/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author msipc
 */
public class Main {

    @EJB(name = "RoomSessionBeanRemote")
    private static RoomSessionBeanRemote roomSessionBeanRemote;

    @EJB(name = "RoomRateSessionBeanRemote")
    private static RoomRateSessionBeanRemote roomRateSessionBeanRemote;

    @EJB(name = "RoomTypeSessionBeanRemote")
    private static RoomTypeSessionBeanRemote roomTypeSessionBean;

    @EJB(name = "PartnerSessionBeanRemote")
    private static PartnerSessionBeanRemote partnerSessionBeanRemote;

    @EJB(name = "EmployeeSessionBeanRemote")
    private static EmployeeSessionBeanRemote employeeSessionBeanRemote;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainApp mainApp = new MainApp(employeeSessionBeanRemote, partnerSessionBeanRemote, roomTypeSessionBean,
            roomRateSessionBeanRemote, roomSessionBeanRemote);
        mainApp.runApp();
    }
    
}
