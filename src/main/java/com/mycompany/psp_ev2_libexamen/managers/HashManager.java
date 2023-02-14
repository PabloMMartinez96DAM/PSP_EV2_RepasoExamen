/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.psp_ev2_libexamen.managers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HashManager {
    
    //Log4j logger que nos permitirá sacar logs por consola y en un fichero
    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UIManager.class);

    // Algoritmo de Hash a utilizar
    private static final String HASH_ALGORITHM = "SHA512";

    
    //Método que hashe un String y lo devuelve en base64. Este método llama a los
    //dos métodos siguientes
    public static String stringToHashedBase64(String input) {

        LOG.info("Se va a hashear el input " + input);
        byte[] hashedInput = hashInput(input);
        
        
        String hashedString = bytesToBase64(hashedInput);
        
        LOG.info("El Hash del String: " + input + " en Base64 es: " + hashedString);
        
        return hashedString;

    }

    // Método que hashea un String y devuelve un array de bytes
    private static byte[] hashInput(String input) {

        //Valor de retorno
        byte[] hashedInput = null;

        //Hasheador de strings
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(input.getBytes());

            hashedInput = md.digest();
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(HashManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return hashedInput;
    }

    public static String bytesToBase64(byte[] hashedInput) {

        return new String(Base64.getEncoder().encodeToString(hashedInput));

    }

}
