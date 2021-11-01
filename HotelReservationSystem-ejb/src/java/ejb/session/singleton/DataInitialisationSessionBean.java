/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;

/**
 *
 * @author msipc
 */
@Singleton
@LocalBean
@Startup

public class DataInitialisationSessionBean {

    public DataInitialisationSessionBean() {
    }

    @PostConstruct
    public void postConstruct() {
//        try
//        {
//            staffEntitySessionBeanLocal.retrieveStaffByUsername("manager");
//        }
//        catch(StaffNotFoundException ex)
//        {
//            initializeData();
//        }
    }
    
    private void initialiseData() {
//        try {
//            // try to initialize data
//        } catch () {
//            // from prof: catch(StaffUsernameExistException | ProductSkuCodeExistException | UnknownPersistenceException | InputDataValidationException ex)
//            // ex.printStackTrace();
//        }
    }
}
