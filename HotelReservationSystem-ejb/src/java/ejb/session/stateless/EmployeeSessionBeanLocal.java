/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import javax.ejb.Local;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author xqy11
 */
@Local
public interface EmployeeSessionBeanLocal {
    public Long createNewEmployee(Employee newEmployeeEntity) throws EmployeeUsernameExistException, UnknownPersistenceException, InputDataValidationException;

    public Employee retrieveEmployeeByUsername(String username) throws EmployeeNotFoundException;
    
}
