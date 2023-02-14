/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.psp_ev2_libexamen.managers;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Guame
 */
public class PatternManager {

    //Log4j logger que nos permitirá sacar logs por consola y en un fichero
    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CipherManager.class);

    //Método que comprueba si un String coincide con el Pattern
    private static boolean isValidString(String input, Pattern pattern) {

        boolean isValid = false;
        Matcher matcher = null;

        //Inicializamos el comparados
        matcher = pattern.matcher(input);

        //Si el nombre del fichero es válido
        if (matcher.matches()) {
            isValid = true;
        }

        // LOGS
        if (isValid) {
            LOG.info("El input" + input + " válido según la expresión regular " + pattern.toString());
        }else{
            LOG.warn("El input" + input + " no es válido según la expresión regular " + pattern.toString());
        }
        
        return isValid;
    }

}
