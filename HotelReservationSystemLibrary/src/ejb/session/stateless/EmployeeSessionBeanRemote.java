/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Remote;
import util.exception.EmployeeUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author xqy11
 */
@Remote
public interface EmployeeSessionBeanRemote {

    public Long createNewEmployee(Employee newEmployeeEntity) throws EmployeeUsernameExistException, UnknownPersistenceException, InputDataValidationException;

    public Employee employeeLogin(String username, String password) throws InvalidLoginCredentialException;

    public List<Employee> retrieveAllEmployees();
    
}
