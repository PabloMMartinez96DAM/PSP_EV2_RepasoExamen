/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.mycompany.psp_ev2_libexamen;

import com.mycompany.psp_ev2_libexamen.enums.Commands;
import com.mycompany.psp_ev2_libexamen.managers.CipherManager;
import com.mycompany.psp_ev2_libexamen.managers.FTPClientManager;
import com.mycompany.psp_ev2_libexamen.managers.HashManager;
import com.mycompany.psp_ev2_libexamen.managers.JavaxMailManager;
import com.mycompany.psp_ev2_libexamen.managers.JsonManager;
import com.mycompany.psp_ev2_libexamen.managers.UIManager;
import com.mycompany.psp_ev2_libexamen.managers.UserManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;
import java.util.logging.Level;
import javax.crypto.SecretKey;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Main {

    // Referencia al cliente FTP
    private static FTPClient ftpClient;

    // Stream de escritura del Servidor FTP
    private static FileOutputStream FTPfileOutputStream;

    // URL del servidor
    private static String UrlFtpServer = "127.0.0.1";

    // Usuario
    private static String userFTP = "";

    // Contraseña
    private static String passFTP = "";

    //Comando que representa el estado actual en el que se encuentra la alpicación
    //y será modificada por el input del usuario
    private static Commands command = Commands.NONE;

    //Ruta al directorio del proyecto DATA
    private static Path dataFilePath = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "data");

    //Ruta del fichero JSON que será utilizado como sistema de persistencia
    private static Path usersFilePath = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "data", "users.json");

    //Log4j logger que nos permitirá sacar logs por consola y en un fichero
    private final static Logger LOG = Logger.getLogger(Main.class);

    //Constantes con los valores en formato String de los algoritmos a utilizar
    private static final String asymmetricAlgorithmRSA = "RSA";
    private static final String asymmetricAlgorithmDSA = "DSA";
    private static final String symmetricAlgorithm = "DES";

    //Constantes del tamaño de las claves
    private static final int KEY_SIZE_DSA = 512;
    private static final int KEY_SIZE_RSA = 1024;
    private static final int KEY_SIZE_DES = 56;

    public static void main(String[] args) {

        //Indicamos donde está el fichero de configuración para el logger
        PropertyConfigurator.configure("log4j.properties");

        //Comprobamos si el paso de argumentos es válido
        if (args.length != 3) {
            System.out.println("Se deben introducir como parametro el host del servidor FTP, el usuario y la contraseña");
            LOG.error("No se ha  pasado un número de argumentos válido");
            System.exit(0);
        }

        // Asignamos el valor de los argumentos a variables más legibles
        UrlFtpServer = args[0];
        userFTP = args[1];
        passFTP = args[2];

        // Logs para confirmar que todo está asignado correctamente
        System.out.println("La ip del servidor es: " + UrlFtpServer);
        System.out.println("El usuario es: " + userFTP);
        System.out.println("La contraseña es: " + passFTP);

        System.out.println("Conectando...");

        //**********************************************************************
        //#region: Inicializamos todas las dependencias
        //**********************************************************************
        //Clase que contiene las operaciones sobre ficheros JSON
        JsonManager jsonManager = new JsonManager(usersFilePath);

        //Clase que contiene los métodos para operar con los usuarios
        UserManager userManager = new UserManager(jsonManager);

        //Lectura del input del usuario/a
        Scanner sc = new Scanner(System.in);

        //**********************************************************************
        //#endregion: Inicializamos todas las dependencias
        //**********************************************************************
        System.out.println("Bienvenida al Examen de PSP de la segunda evaluación");

        try {
            // Respuesta del servidor FTP
            int reply;

            // Creamos la referencia al cliente FTP que utilizaremos
            ftpClient = new FTPClient();

            // Nos conectamos al servidor FTP
            ftpClient.connect(UrlFtpServer);

            // Obtenemos la respuesta del servidor
            reply = ftpClient.getReplyCode();

            // Comprobamos que nos hemos conectado correctamente
            if (FTPReply.isPositiveCompletion(reply)) {

                // Logs
                System.out.println("Nos hemos conectado corectamente con el servidor FTP: " + UrlFtpServer);
                System.out.println("Vamos a intentar loggearnos con el usuario: " + userFTP + " y la contraseña: " + passFTP);

                // Intentamos loggearnos
                if (ftpClient.login(userFTP, passFTP)) {
                    System.out.println("¡Nos hemos loggeado satisfactoriamente! \nUsuario: " + userFTP + " \nContraseña: " + passFTP);

                    // Comprobación del estado del servidor
                    System.out.println("Status del servidor:\n" + ftpClient.getStatus());
                }
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

                while (command != Commands.SALIR) {

                    // Esperamos aquí hasta que el usuario haya introducido un comando válido
                    command = Commands.values()[UIManager.getInput()];
                    LOG.info("Se ha introducido la opción" + command.name());

                    switch (command) {
                        
                        
                        case LISTAR_CONTENIDO:
                            
                            FTPClientManager.printContent(ftpClient);
                            break;
                            
                            
                            case DESCARGAR_FICHERO:
                                break;
                        case HASH_STRING:

                            System.out.println("Introduce un texto a hashear");
                            String hashTest = sc.nextLine();

                            System.out.println("Resultado en Base64:");
                            System.out.println(HashManager.stringToHashedBase64(hashTest));

                            break;

                            
                        case CIFRADO_SIMETRICO_DES:

                            //Ruta al fichero sin encriptar
                            Path clearDESFilePath = Paths.get(dataFilePath.toString(), "ficheroSinEncriptarDES.txt");

                            //Ruta al fichero cifrado
                            Path encryptedDESFilePath = Paths.get(dataFilePath.toString(), "ficheroEncriptadoDES.txt");

                            //Array de bytes que almacena el contenido cifrado
                            byte[] cipherBufferDES = null;

                            //Array de bytes que almacena el contenido del fichero
                            byte[] clearBufferDES = null;

                             {
                                try {

                                    //Leemmos el contenido del fichero
                                    String clearFileContent = Files.readString(clearDESFilePath);
                                    System.out.println("El contenido del fichero es: \n" + clearFileContent);

                                    //Genereamos la clave simetricas
                                    SecretKey secretKey = CipherManager.generateSecretKey(symmetricAlgorithm, KEY_SIZE_DES);

                                    cipherBufferDES = CipherManager.encryptBytesDES(secretKey, clearFileContent.getBytes(), symmetricAlgorithm);

                                    System.out.println("El contenido cifrado de manera simetrica es:");
                                    System.out.write(cipherBufferDES);
                                    System.out.println(System.lineSeparator());

                                    clearBufferDES = CipherManager.decryptBytesDES(secretKey, cipherBufferDES, symmetricAlgorithm);

                                    System.out.println("El contenido descifrado de manera simetrica es:");
                                    System.out.write(clearBufferDES);
                                    System.out.println(System.lineSeparator());

                                } catch (IOException ex) {
                                    java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                             {
                                try {
                                    String cipherBufferDESBase64 = HashManager.bytesToBase64(cipherBufferDES);

                                    Files.write(encryptedDESFilePath, cipherBufferDESBase64.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                                } catch (IOException ex) {
                                    java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            break;

                        case CIFRADO_SIMETRICO_RSA:
                            break;

                        case FIRMA_DIGITAL:

                            //Ruta del fichero en claro
                            Path clearDSAFilePath = Paths.get(dataFilePath.toString(), "ficheroSinEncriptarDSA.txt");

                            //Ruta del fichero cifrado
                            Path encryptedDSAFilePath = Paths.get(dataFilePath.toString(), "ficheroEncriptadoDSA.txt");

                            //Buffer del contenido cifrado
                            byte[] cipherBufferDSA = null;

                            //Buffer del contenido en claro
                            byte[] clearBufferDSA = null;

                            //Buffer que contendrá la firma digital
                            byte[] signBuffer = null;

                            //Generamos el par claves
                            KeyPair keyPair = CipherManager.generateKeyPair(asymmetricAlgorithmDSA, KEY_SIZE_DSA);

                            PrivateKey privateKey = keyPair.getPrivate();
                            PublicKey publicKey = keyPair.getPublic();

                            //Leemos el contenido del fichero
                             {
                                try {
                                    clearBufferDSA = Files.readAllBytes(clearDSAFilePath);
                                } catch (IOException ex) {
                                    java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            //Obtenemos la firma digital
                            signBuffer = CipherManager.makeSignature(privateKey, clearBufferDSA, asymmetricAlgorithmDSA);

                            if (CipherManager.validateSignature(publicKey, clearBufferDSA, signBuffer, asymmetricAlgorithmDSA)) {
                                //Pasamos a base 64 el array de bytes cifrado para poder verlo en netbeans

                                {
                                    try {
                                        //Escribimos el contenido cifrado en el fichero
                                        Files.write(encryptedDSAFilePath, clearBufferDSA, StandardOpenOption.TRUNCATE_EXISTING);
                                    } catch (IOException ex) {
                                        java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }

                            } else {
                                System.out.println("La firma digital no es válida, así que no haremos nada con el contenido descifrado");
                                LOG.warn("La firma digital no es válida");
                            }
                            break;

                        case ENVIAR_CORREO:
                            JavaxMailManager.sendTestMail("pablommartinezpsp@gmail.com", "fjffgnbkalinutlm", "pruebapspdomingo@gmail.com", true);
                            break;

                        case CIFRARDO_ASIMETRICO_CONTRASEÑA_CIFRADA_SIMETRICA:

                            //Clave del cifrado simetrico
                            SecretKey symmKey = null;

                            //Claves ya generadas del cifrado asimetrico
                            KeyPair asymKeyPair = null;

                            //Clave pública del cifrado asimetrico
                            PublicKey publicK = null;

                            //Clave privada del cifrado asimetrico
                            PrivateKey privateK = null;

                            //Buffer con los datos en claro
                            byte[] clearBuffer = null;

                            //Buffer con los datos en claro cifrados de manera simetrica
                            byte[] symmEncryptBuffer = null;

                            //Buffer con los datos cifrados de manera simetrica ahora cifrados
                            //tambien de manera asimetrica
                            byte[] asymmEncryptBuffer = null;

                            //Buffer asimetrico desencriptado
                            byte[] asymmDecryptBuffer = null;

                            //Input a cifrar
                            clearBuffer = "Hola.txt".getBytes();

                            //1.Ciframos el contenido de manera simetrica
                            //Generamos la clave simetrica
                            symmKey = CipherManager.generateSecretKey(symmetricAlgorithm, KEY_SIZE_DES);

                            //Ciframos el contenido de manera simetrica
                            symmEncryptBuffer = CipherManager.encryptBytesDES(symmKey, clearBuffer, symmetricAlgorithm);

                            //Lo sacamos por consola
                             {
                                try {
                                    System.out.println("El cifrado simetrico es el siguiente:");
                                    System.out.write(symmEncryptBuffer);
                                    System.out.println(System.lineSeparator());

                                } catch (IOException ex) {
                                    java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            //2.Ciframos el contenido ya encriptado de manera simetrica ç
                            // ahora de manera asimetrica
                            //Generamos el par de claves
                            asymKeyPair = CipherManager.generateKeyPair(asymmetricAlgorithmRSA, KEY_SIZE_RSA);

                            //Generamos la clave pública
                            publicK = asymKeyPair.getPublic();

                            //Generamos la clave privada
                            privateK = asymKeyPair.getPrivate();

                            //Ciframos la clave simetrica de manera asimetrica
                            asymmEncryptBuffer = CipherManager.encryptBytesRSA(publicK, symmEncryptBuffer, asymmetricAlgorithmRSA);

                            //Lo sacamos por consola
                             {
                                try {
                                    System.out.println("El cifrado asimetrico es el siguiente:");
                                    System.out.write(asymmEncryptBuffer);
                                    System.out.println(System.lineSeparator());
                                } catch (IOException ex) {
                                    java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            //Desciframos de manera asimetroca
                            asymmDecryptBuffer = CipherManager.decryptBytesRSA(privateK, asymmEncryptBuffer, asymmetricAlgorithmRSA);

                            //Lo sacamos por consola
                             {
                                try {
                                    System.out.println("Descifrado asimetrico");
                                    System.out.write(asymmDecryptBuffer);
                                    System.out.println(System.lineSeparator());

                                    System.out.println("cifrado simetrico");
                                    System.out.write(symmEncryptBuffer);
                                    System.out.println(System.lineSeparator());
                                } catch (IOException ex) {
                                    java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            //Desciframos de manera simetrica
                            byte[] symmDecryptBuffer = CipherManager.decryptBytesDES(symmKey, asymmDecryptBuffer, symmetricAlgorithm);

                            //Lo sacamos por consola
                             {
                                try {
                                    System.out.println("Texto original");
                                    System.out.write(clearBuffer);
                                    System.out.println(System.lineSeparator());

                                    System.out.println("Texto descifrado");
                                    System.out.write(symmDecryptBuffer);
                                    System.out.println(System.lineSeparator());

                                } catch (IOException ex) {
                                    java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                            break;

                        case SALIR:
                            LOG.info("Cerramos la apliación");
                            System.out.println("Cerrando aplicación...");
                            break;

                        default:
                            System.out.println("Ha ocurrido un error inesperado");
                            LOG.error("Ha ocurrido un error en el menú principal");
                            break;

                    }

                }

            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}