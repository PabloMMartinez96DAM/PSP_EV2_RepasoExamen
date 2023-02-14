/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.psp_ev2_libexamen.managers;

import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class CipherManager {

    //Log4j logger que nos permitirá sacar logs por consola y en un fichero
    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CipherManager.class);

    //**************************************************************************
    //#region: Cifrado Simetrico
    //**************************************************************************
    //Método para generar la clave simetrica
    public static SecretKey generateSecretKey(String algorithm, int keySize) {

        //Valor de retorno
        SecretKey result = null;

        //Generamos la KEY simetrica en función del algoritmo y el tamañodo del 
        //buffer que se le quiera dar
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
            keyGen.init(keySize);
            result = keyGen.generateKey();
            LOG.info("La key simetrica generada es:" + result.toString());
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CipherManager.class.getName()).log(Level.SEVERE, null, ex);
            LOG.warn("La key simetrica no se ha podido generar correctamente");
        }
        return result;

    }

    //Método para cifrar un String con una clave simetrica
    public static byte[] encryptBytesDES(SecretKey secretKey, byte[] clearBuffer, String symmetricAlgorithm) {

        //Valor de retorno
        byte[] cipherBuffer = null;

        //Cifrador
        Cipher cipher = null;

        try {
            //Obtenemos la instancia del cipher
            cipher = Cipher.getInstance(symmetricAlgorithm);
            //Inicializamos el cifrador en modo encriptación pasandole la SecretKey correspondiente
            cipher.init(cipher.ENCRYPT_MODE, secretKey);

            //Cifro el contenido
            cipherBuffer = cipher.doFinal(clearBuffer);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(CipherManager.class.getName()).log(Level.SEVERE, null, ex);
            LOG.warn("No se ha podido encriptar el buffer: " + new String(clearBuffer));
        }

        LOG.info("Se ha cifrado el bufer: " + new String(clearBuffer));
        return cipherBuffer;

    }

    //Método para descifrar un String con una clave simetrica
    public static byte[] decryptBytesDES(SecretKey secretKey, byte[] cipherBuffer, String symmetricAlgorithm) {

        //Valor de retorno
        byte[] clearBuffer = null;

        //Cifrador
        Cipher cipher = null;

        try {
            //Obtenemos la instancia del cipher
            cipher = Cipher.getInstance(symmetricAlgorithm);

            //Inicializamos el cifrador en modo encriptación pasandole la SecretKey correspondiente
            cipher.init(cipher.DECRYPT_MODE, secretKey);

            //Cifro el contenido
            clearBuffer = cipher.doFinal(cipherBuffer);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(CipherManager.class.getName()).log(Level.SEVERE, null, ex);
            LOG.warn("No se ha podido desencriptar de manera simetrica el siguiente buffer: " + new String(cipherBuffer));
        }

        LOG.info("Se ha desencriptado de manera simetrica el buffer: " + new String(clearBuffer));
        return clearBuffer;
    }

    //**************************************************************************
    //#endRegion: Cifrado Simetrico
    //**************************************************************************
    //**************************************************************************
    //#region: Cifrado Asimetrico
    //**************************************************************************
    //Método que genera el par de claves simetricas
    public static KeyPair generateKeyPair(String algorithm, int keySize) {

        //Valor de retorno
        KeyPair keyPair = null;
        try {
            //Crea e inicializa el generador de claves simetricas
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(algorithm);
            keyGenerator.initialize(keySize);
            keyPair = keyGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CipherManager.class.getName()).log(Level.SEVERE, null, ex);
            LOG.warn("No se han podido generar las claves asimetricas con el algoritmo: " + algorithm);
        }
        LOG.info("Se ha generado un par de claves asimetricas con el algoritmo: " + algorithm);
        return keyPair;

    }

    public static KeyPair generateKeyPair(String algorithm, int keySize, byte[] seed) {

        //Valor de retorno
        KeyPair keyPair = null;
        SecureRandom secureRandom = null;
        try {
            //Obtenemos el SecureRandom
            secureRandom = new SecureRandom(seed);

            //Crea e inicializa el generador de claves simetricas
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(algorithm);
            keyGenerator.initialize(keySize, secureRandom);
            keyPair = keyGenerator.generateKeyPair();

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CipherManager.class.getName()).log(Level.SEVERE, null, ex);
            LOG.warn("No se han podido generar las claves asimetricas con el algoritmo: " + algorithm);
        }
        LOG.info("Se ha generado un par de claves asimetricas con el algoritmo: " + algorithm);
        return keyPair;

    }

    //Método para encriptar de manera asimetrica
    public static byte[] encryptBytesRSA(PublicKey publicKey, byte[] clearBufferRSA, String asymmetricAlgorithm) {
        //Valor de retorno - bytes encriptados
        byte[] cipherBuffer = null;

        //Cifrador
        Cipher cipher = null;

        try {
            cipher = Cipher.getInstance(asymmetricAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            cipherBuffer = cipher.doFinal(clearBufferRSA);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(CipherManager.class.getName()).log(Level.SEVERE, null, ex);
            LOG.warn("No se han podido encriptar de manera asimetrica con el algoritmo: " + asymmetricAlgorithm);
        }
        LOG.info("Se ha encriptado de manera asimetrica: " + new String(cipherBuffer) + " con el algoritmo: " + asymmetricAlgorithm);

        return cipherBuffer;
    }

    //Método para desencriptar de manera asimetrica
    public static byte[] decryptBytesRSA(PrivateKey privateKey, byte[] cipherBufferRSA, String asymmetricAlgorithm) {
        //Valor de retorno - bytes encriptados
        byte[] clearBuffer = null;

        //Cifrador
        Cipher cipher = null;

        try {
            cipher = Cipher.getInstance(asymmetricAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            clearBuffer = cipher.doFinal(cipherBufferRSA);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(CipherManager.class.getName()).log(Level.SEVERE, null, ex);
            LOG.warn("No se han podido encriptar de manera asimetrica con el algoritmo: " + asymmetricAlgorithm);
        }
        LOG.info("Se ha encriptado de manera asimetrica: " + new String(cipherBufferRSA) + " con el algoritmo: " + asymmetricAlgorithm);

        return clearBuffer;
    }

    //**************************************************************************
    //#endRegion: Cifrado Asimetrico
    //**************************************************************************
    //**************************************************************************
    //#region: Firma digital
    //**************************************************************************
    //Método para generar la firma digital
    public static byte[] makeSignature(PrivateKey privateKey, byte[] clearBuffer, String asymmetricAlgorithm) {

        //Valor de retorno
        byte[] sign = null;

        try {
            Signature signature = Signature.getInstance(asymmetricAlgorithm);
            signature.initSign(privateKey);
            signature.update(clearBuffer);
            sign = signature.sign();

        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            Logger.getLogger(CipherManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SignatureException ex) {
            Logger.getLogger(CipherManager.class.getName()).log(Level.SEVERE, null, ex);
            LOG.warn("No se ha podido realizar la firma");
        }

        LOG.info("Se ha creado la firma digital satisfactoriamente");
        return sign;

    }

    public static boolean validateSignature(PublicKey publicKey, byte[] clearBuffer, byte[] signBuffer, String asymmetricAlgorithm) {

        //Valor de retorno
        boolean isValid = false;

        try {
            Signature signature = Signature.getInstance(asymmetricAlgorithm);
            signature.initVerify(publicKey);
            signature.update(clearBuffer);
            isValid = signature.verify(signBuffer);
        } catch (SignatureException | InvalidKeyException | NoSuchAlgorithmException ex) {
            Logger.getLogger(CipherManager.class.getName()).log(Level.SEVERE, null, ex);
            LOG.warn("No se ha podido válidar");
        }
        LOG.info("Se ha cerrado correctamente");
        return isValid;

    }
    //**************************************************************************
    //#endRegion: Firma digital
    //**************************************************************************

}
