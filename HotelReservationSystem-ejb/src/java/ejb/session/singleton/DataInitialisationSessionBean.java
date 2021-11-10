/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Employee;
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
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.RoomRateNameExistException;
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
        
//        try {
//            roomTypeSessionBeanLocal.createNewRoomType(null,new RoomType("Grand Suite","Some description..", new Double(5), 5,5,"Some amenities.."));
//        } catch(RoomTypeNameExistException | UnknownPersistenceException | InputDataValidationException | RoomTypeNotFoundException ex){
//             ex.printStackTrace();
//        }
//        
//         try {
//             roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Grand Suite Normal",RoomRateType.NORMAL, new BigDecimal(100), null,null),"Grand Suite");
//        } catch(RoomRateNameExistException | UnknownPersistenceException | InputDataValidationException | RoomTypeNotFoundException ex){
//             ex.printStackTrace();
//        }
    }

}
