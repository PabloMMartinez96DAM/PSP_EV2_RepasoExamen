/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.psp_ev2_libexamen.managers;

import com.mycompany.psp_ev2_libexamen.enums.Commands;
import java.util.Scanner;
import org.apache.log4j.Logger;




public class UIManager {
     //Log4j logger que nos permitirá sacar logs por consola y en un fichero
    private final static Logger LOG = Logger.getLogger(UIManager.class);

    // Método que obtiene el input del usuario y lo procesa para comprobar
    // si es un comando válido
    public static int getInput() {

        // Valor de retorno
        int input = -1;

        // Condición del bucle que será true cuando el input del usuario/a sea 
        // un número entre el 1 y el 6 (el número que se corresponde con el
        // último valor de la enumeración Commands)
        boolean isValid = false;
        while (!isValid) {

            // Obtenemos el input del usuario,  si es un número
            input = nextInput();
            
            // Comprobamos si ese número pertenece a un comando válido
            isValid = validateInput(input);
            
            // log para debuggear
            if (!isValid) {
                System.out.println("El comando introducido no es válido");
            }
        }
        System.out.println("El comando introducido es " + Commands.values()[input]);
        return input;
    }

    // Obtenemos el input del usuario y controlamos la excepción en caso de que 
    // no sea un número
    private static int nextInput() {

        // Valor de retorno
        int input = -1;
        // Clase que utilizaremos para la lectura del input del usuario por consola
        Scanner sc = new Scanner(System.in);

        try {
            // Parte de la UI que le dice al usuario/a los comandos disponibles
            System.out.println("Introudce un número para seleccionar uno de los siguientes comandos");
            for (int i = 1; i < Commands.values().length; i++) {
                System.out.println(i + ". " + Commands.values()[i]);
            }

            // Obtiene el input del usuario/a
            input = sc.nextInt();
        } catch (Exception e) {
            LOG.error("El input del usuario no es un número y por tanto no ha elegido un opción válida");
        }

        return input;
    }

    // Comprobamos que el input del usuario se corresponde con un comando 
    // de la enumeración
    private static boolean validateInput(int input) {
        boolean result = false;

        if (input > 0 && input < Commands.values().length) {
            result = true;
        }
        
        if(!result){
            LOG.error("El número " + input+" no representa un comando válido");
        }
        return result;
    }
    
}
