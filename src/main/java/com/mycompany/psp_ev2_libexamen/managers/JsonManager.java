/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.psp_ev2_libexamen.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycompany.psp_ev2_libexamen.models.UserModel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Guame
 */
public class JsonManager {

    //Log4j logger que nos permitirá sacar logs por consola y en un fichero
    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(JsonManager.class);

    //Properties
    private Path jsonFilePath;

    //Constructor
    public JsonManager(Path jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    //Methods
    //Este método trunca el fichero JSON, el cual actúa como el sistema de 
    //persistencia de datos de la aplicación, con los datos almacenados en memoria
    public void truncateFile(List<UserModel> users) {
        try {
            // Serializo el contenido de la lista en formato JSON

            Gson gson = new GsonBuilder().disableHtmlEscaping().create();

            String json = gson.toJson(users);

            // Trunco el fichero con los nuevos datos
            Files.write(this.jsonFilePath, json.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

            LOG.info("Se va ha truncado el fichero " + this.jsonFilePath.getFileName() + " con nueva información");
        } catch (IOException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
            LOG.error("El fichero " + this.jsonFilePath.getFileName() + " no ha podido actualizarse");
        }
    }

    //Sobrecarga para utilizar el método desde fuera. Principalmente para debuggear
    public void truncateFile(Path filePath, List<UserModel> users) {
        try {
            // Serializo el contenido de la lista en formato JSON

            Gson gson = new GsonBuilder().disableHtmlEscaping().create();

            String json = gson.toJson(users);

            // Trunco el fichero con los nuevos datos
            Files.write(filePath, json.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            LOG.info("Se va ha truncado el fichero " + this.jsonFilePath.getFileName() + " con nueva información");
        } catch (IOException ex) {
            Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
            LOG.error("El fichero " + this.jsonFilePath.getFileName() + " no ha podido actualizarse");
        }
    }

    //Añade el usuario a la lista para guardarlo en memoria y trunca el fichero
    //JSON con los nuevos datos
    public void updateUser(List<UserModel> users, UserModel user) {
        users.add(user);
        this.truncateFile(this.getJsonFilePath(), users);
        LOG.info("El usuario/a: " + user.getName() + " ha sido guardado en el fichero " + this.jsonFilePath.getFileName());
    }

    //Sobrecarga para utilizar el método desde fuera. Principalmente para debuggear
    public void updateUser(Path filePath, List<UserModel> users, UserModel user) {
        users.add(user);
        this.truncateFile(filePath, users);
        LOG.info("El usuario/a: " + user.getName() + " ha sido guardado en el fichero " + this.jsonFilePath.getFileName());
    }

    //Getters & Setter
    public Path getJsonFilePath() {
        return jsonFilePath;
    }

}
