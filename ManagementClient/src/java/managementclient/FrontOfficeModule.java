 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managementclient;

import entity.Employee;
import java.util.Scanner;
import util.enumeration.AccessRight;
import util.exception.InvalidAccessRightException;

/**
 *
 * @author msipc
 */
public class FrontOfficeModule {
    
    // private attributes for remote session bean here
    // Queue and ConnectionFactory
    private Employee currentEmployee;

    
    
    public FrontOfficeModule() {
    }

    // overloaded constructor
    public FrontOfficeModule(Employee currentEmployee) {
        this.currentEmployee = currentEmployee;
    }
    
    
    
    public void menuFrontOffice() throws InvalidAccessRightException {
        
        if(currentEmployee.getAccessRight() != AccessRight.GUEST_RELATION_OFFICER)
        {
            throw new InvalidAccessRightException("You don't have GUEST_RELATION_OFFICER rights to access the front office module.\n");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            
            System.out.println("*** Hotel Reservation System Manager Client :: Front Office ***");
            System.out.println("1: Walk-in Search Room");
            System.out.println("2: Check-in Guest");
            System.out.println("3: Check-out Guest");
            System.out.println("-------------------------");
            System.out.println("4: Back\n");
            response = 0;
            
            while(response < 1 || response > 4) {
                
                System.out.print("> ");
                
                response = scanner.nextInt();
                
                if (response == 1) {
                    
                    doWalkinSearchRoom();
                    
                } else if (response == 2) {
                    
                    doCheckinGuest();
                    
                } else if (response == 3) {
                    
                    doCheckoutGuest();
                    
                } else if (response == 4) {
                    // Back
                    break;
                    
                } else {
                    
                    System.out.println("Invalid option, please try again!\n");
                
                }                
            }
            
            if (response == 4) { // same number as the Back option
                break;
            }
            
        }
    }
    
    
    ///
    public void doWalkinSearchRoom() {
        
    }
    
    public void doCheckinGuest() {
        
    }
    
    public void doCheckoutGuest() {
        
    }
}