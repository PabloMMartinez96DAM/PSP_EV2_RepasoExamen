/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.psp_ev2_libexamen.managers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 *
 * @author Guame
 */
public class FTPClientManager {

    //Log4j logger que nos permitirá sacar logs por consola y en un fichero
    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FTPClientManager.class);

    //Saca por consola el nombre del contenido del directorio en el que estamos situados
    public static void printContent(FTPClient ftpClient) throws IOException {
        FTPFile[] files = ftpClient.listFiles();

        System.out.println("Directorios:");
        //Printea primero los directorios
        for (FTPFile element : files) {
            if (element.isDirectory()) {
                System.out.println("- " + element.getName());
            }
        }
        System.out.println("Ficheros:");
        //Y después printea los ficheros
        for (FTPFile element : files) {
            if (element.isFile()) {
                System.out.println("+ " + element.getName());
            }
        }
    }

    //Saca por consola el noombre de los ficheros del directorio en el que estamos situados
    public static void printFiles(FTPClient ftpClient) throws IOException {
        System.out.println("Ficheros Disponibles:");
        FTPFile[] files = ftpClient.listFiles();

        for (FTPFile element : files) {
            if (element.isFile()) {
                System.out.println("+ " + element.getName());
            }
        }
    }

    // Método para subir un fichero al servidor ftp y capturar la excepción 
    // en caso de que no exista el fichero
    public static void uploadFile(FTPClient ftpClient, String localPath, String remotePath) {
        try {
            InputStream input = new BufferedInputStream(new FileInputStream(localPath));
            if (ftpClient.storeFile(remotePath, input)) {
                System.out.println("El fichero " + remotePath + " se ha subido satisfactoriamente");
            } else {
                System.out.println("Ha ocurrido un error, el fichero " + remotePath + " no ha podido subirse");
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.err.println("El fichero " + localPath + " no existe");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void downloadFile(String fileName, FTPClient fptClient) {
        FileOutputStream fos = null;
        try {
            System.out.println("Escribe el nombre del archivo que quieras descargar");
            fos = new FileOutputStream(fileName);
            File file = new File(fileName);
            if (file.exists()) {

                if (fptClient.retrieveFile(fileName, fos)) {
                    System.out.println("Fichero descargado correctamente");
                } else {
                    System.out.println("No se pudo descargar el fichero");
                }
            } else {
                System.out.println("El fichero no existe");
            }
            fos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FTPClientManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FTPClientManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(FTPClientManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
