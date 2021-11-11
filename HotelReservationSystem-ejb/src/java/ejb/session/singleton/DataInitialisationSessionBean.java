/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Employee;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.AccessRight;
import util.enumeration.RoomRateType;
import util.enumeration.RoomStatus;
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.RoomHasNoRoomRateException;
import util.exception.RoomNumberExistException;
import util.exception.RoomRateNameExistException;
import util.exception.RoomTypeDisabledException;
import util.exception.RoomTypeNameExistException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author msipc
 */
@Singleton
@LocalBean
@Startup

public class DataInitialisationSessionBean {

    @EJB(name = "ReservationSessionBeanLocal")
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @EJB(name = "RoomSessionBeanLocal")
    private RoomSessionBeanLocal roomSessionBeanLocal;

    @EJB(name = "RoomRateSessionBeanLocal")
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;

    @EJB(name = "RoomTypeSessionBeanLocal")
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @EJB(name = "EmployeeSessionBeanLocal")
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;
    
    

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    

    public DataInitialisationSessionBean() {
    }

    @PostConstruct
    public void postConstruct() {
        if(em.find(Employee.class, 1l) == null){
            initialiseData();
        }
        
//        try
//        {
//            employeeSessionBeanLocal.retrieveEmployeeByUsername("sysAdmin");
//        }
//        catch(EmployeeNotFoundException ex)
//        {
//            initialiseData();
//        }
    }
    
    private void initialiseData() {
        try {
            employeeSessionBeanLocal.createNewEmployee(new Employee("sysadmin", "password", AccessRight.SYSTEM_ADMIN));
            employeeSessionBeanLocal.createNewEmployee(new Employee("opmanager", "password", AccessRight.OPERATION_MANAGER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("salesmanager", "password", AccessRight.SALES_MANAGER));
            employeeSessionBeanLocal.createNewEmployee(new Employee("guestrelo", "password", AccessRight.GUEST_RELATION_OFFICER));
        } catch(EmployeeUsernameExistException | UnknownPersistenceException | InputDataValidationException ex){
             ex.printStackTrace();
        }
        
        try {
            roomTypeSessionBeanLocal.createNewRoomType("None",new RoomType("Grand Suite","This is a Grand Suite.", new Double(50), 6,6,"Some amenities.."));
            roomTypeSessionBeanLocal.createNewRoomType("Grand Suite",new RoomType("Junior Suite","This is a Junior Suitee.", new Double(40), 5,5,"Some amenities.."));
            roomTypeSessionBeanLocal.createNewRoomType("Junior Suite",new RoomType("Family Room","This is a Family Room.", new Double(30), 4,4,"Some amenities.."));
            roomTypeSessionBeanLocal.createNewRoomType("Family Room",new RoomType("Premier Room","This is a Premier Room.", new Double(20), 3,3,"Some amenities.."));
            roomTypeSessionBeanLocal.createNewRoomType("Premier Room",new RoomType("Deluxe Room","This is a Deluxe Room.", new Double(15), 2,2,"Some amenities.."));
            
        } catch(RoomTypeNameExistException | UnknownPersistenceException | InputDataValidationException | RoomTypeNotFoundException ex){
             ex.printStackTrace();
        }
        
        try {
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Grand Suite Normal",RoomRateType.NORMAL, new BigDecimal(250), null,null),"Grand Suite");
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Grand Suite Published",RoomRateType.PUBLISHED, new BigDecimal(500), null,null),"Grand Suite");
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Junior Suite Normal",RoomRateType.NORMAL, new BigDecimal(200), null,null),"Junior Suite");
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Junior Suite Published",RoomRateType.PUBLISHED, new BigDecimal(400), null,null),"Junior Suite");
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Family Room Normal",RoomRateType.NORMAL, new BigDecimal(150), null,null),"Family Room");
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Family Room Published",RoomRateType.PUBLISHED, new BigDecimal(300), null,null),"Family Room");
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Premier Room Normal",RoomRateType.NORMAL, new BigDecimal(100), null,null),"Premier Room");
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Premier Room Published",RoomRateType.PUBLISHED, new BigDecimal(200), null,null),"Premier Room");
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Deluxe Room Normal",RoomRateType.NORMAL, new BigDecimal(50), null,null),"Deluxe Room");
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Deluxe Room Published",RoomRateType.PUBLISHED, new BigDecimal(100), null,null),"Deluxe Room");
             
        } catch(RoomRateNameExistException | UnknownPersistenceException | InputDataValidationException | RoomTypeNotFoundException ex){
             ex.printStackTrace();
        }
         
        try {
            roomSessionBeanLocal.createNewRoom(new Room("0101",RoomStatus.AVAILABLE), "Deluxe Room");
            roomSessionBeanLocal.createNewRoom(new Room("0201",RoomStatus.AVAILABLE), "Deluxe Room");
            roomSessionBeanLocal.createNewRoom(new Room("0301",RoomStatus.AVAILABLE), "Deluxe Room");
            roomSessionBeanLocal.createNewRoom(new Room("0401",RoomStatus.AVAILABLE), "Deluxe Room");
            roomSessionBeanLocal.createNewRoom(new Room("0501",RoomStatus.AVAILABLE), "Deluxe Room");
            roomSessionBeanLocal.createNewRoom(new Room("0102",RoomStatus.AVAILABLE), "Premier Room");
            roomSessionBeanLocal.createNewRoom(new Room("0202",RoomStatus.AVAILABLE), "Premier Room");
            roomSessionBeanLocal.createNewRoom(new Room("0302",RoomStatus.AVAILABLE), "Premier Room");
            roomSessionBeanLocal.createNewRoom(new Room("0402",RoomStatus.AVAILABLE), "Premier Room");
            roomSessionBeanLocal.createNewRoom(new Room("0502",RoomStatus.AVAILABLE), "Premier Room");
            roomSessionBeanLocal.createNewRoom(new Room("0103",RoomStatus.AVAILABLE), "Family Room");
            roomSessionBeanLocal.createNewRoom(new Room("0203",RoomStatus.AVAILABLE), "Family Room");
            roomSessionBeanLocal.createNewRoom(new Room("0303",RoomStatus.AVAILABLE), "Family Room");
            roomSessionBeanLocal.createNewRoom(new Room("0403",RoomStatus.AVAILABLE), "Family Room");
            roomSessionBeanLocal.createNewRoom(new Room("0503",RoomStatus.AVAILABLE), "Family Room");
            roomSessionBeanLocal.createNewRoom(new Room("0104",RoomStatus.AVAILABLE), "Junior Suite");
            roomSessionBeanLocal.createNewRoom(new Room("0204",RoomStatus.AVAILABLE), "Junior Suite");
            roomSessionBeanLocal.createNewRoom(new Room("0304",RoomStatus.AVAILABLE), "Junior Suite");
            roomSessionBeanLocal.createNewRoom(new Room("0404",RoomStatus.AVAILABLE), "Junior Suite");
            roomSessionBeanLocal.createNewRoom(new Room("0504",RoomStatus.AVAILABLE), "Junior Suite");
            roomSessionBeanLocal.createNewRoom(new Room("0105",RoomStatus.AVAILABLE), "Grand Suite");
            roomSessionBeanLocal.createNewRoom(new Room("0205",RoomStatus.AVAILABLE), "Grand Suite");
            roomSessionBeanLocal.createNewRoom(new Room("0305",RoomStatus.AVAILABLE), "Grand Suite");
            roomSessionBeanLocal.createNewRoom(new Room("0405",RoomStatus.AVAILABLE), "Grand Suite");
            roomSessionBeanLocal.createNewRoom(new Room("0505",RoomStatus.AVAILABLE), "Grand Suite");
            
        } catch(RoomHasNoRoomRateException | UnknownPersistenceException | InputDataValidationException | RoomTypeNotFoundException 
                |RoomTypeDisabledException | RoomNumberExistException ex){
             ex.printStackTrace();
        }
        
//        try {
//            reservationSessionBeanLocal.createNewReservation(reservationEntity, "Deluxe Room", guestID)
//        } catch(EmployeeUsernameExistException | UnknownPersistenceException | InputDataValidationException ex){
//             ex.printStackTrace();
//        }
    }

}
