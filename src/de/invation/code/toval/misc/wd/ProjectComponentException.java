/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.invation.code.toval.misc.wd;

/**
 *
 * @author stocker
 */
public class ProjectComponentException extends Exception {

    public ProjectComponentException() {}

    public ProjectComponentException(String message) {
        super(message);
    }

    public ProjectComponentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProjectComponentException(Throwable cause) {
        super(cause);
    }
    
}
