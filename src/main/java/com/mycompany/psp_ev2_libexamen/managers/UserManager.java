package com.mycompany.psp_ev2_libexamen.managers;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.mycompany.psp_ev2_libexamen.models.UserModel;
import java.io.IOException;
import java.nio.file.Files;

import java.util.logging.Level;
import org.apache.log4j.Logger;

public class UserManager {

    //Properties
    //Clase que se encargará de ejecutar las operaciones de persistencia sobre 
    //el fichero JSON
    private JsonManager jsonManager;

    //Lista que almacena todos los usuarios en memoria. Los leídos del fichero
    // Y los generados en memoria que se almacenarán en el fichero
    private List<UserModel> users = new ArrayList<UserModel>();

    private final static Logger LOG = Logger.getLogger(UserManager.class);

    // Cosntructor
    public UserManager(JsonManager jsnoManager) {
        this.jsonManager = jsnoManager;

        //Inicializamos la lista con el contenido del fichero
        this.users = getAllUsers();
    }

    //Methods
    public boolean login(String userName, String password) {

        //Valor de retorno que tambien se utilizará para finalizar el bucle 
        //una vez ya haya encontrado al usuario y así sea un poquito más eficiente
        boolean isFound = false;

        //Recorremos la lista de usuarios para comparar si el usuario que intenta
        //Loguearse existe en la lista de usuarios
        for (int i = 0; i < users.size() && !isFound; i++) {

            // Comprobamos si el nombre coincide para el log
            if (userName.equals(users.get(i).getName())) {

                //TODO: Logs bonitos
                // Si el usuario es igual, comprobamos la contraseña
                if (password.equals(users.get(i).getPass())) {

                    System.out.println("El usuario:" + userName + " se ha logueado correctamente");
                    isFound = true;
                }
            }
        }
        if (!isFound) {
            LOG.warn("El usuario/a: " + userName + " no existe");
        }
        else{
            LOG.info("El usuario/a: " + userName + " se ha logueado correctamente");
        }
        
        
        return isFound;
    }

    //Este método comprueba que no haya otro usuarion con el mismo nombre para
    //registrarse. Devuelve un booleano para comprobar si se ha realizado la operacion
    public boolean signin(String userName, String password) {

        //Valor de retorno que cambiará en el caso de que exista un usuario ya
        //ya registrado con el mismo nombre y por tanto no se registrará
        boolean isValid = true;

        //Comprueba si el nombre de usuario ya está registrado
        for (int i = 0; i < this.users.size() & isValid; i++) {
            if (userName.equalsIgnoreCase(this.users.get(i).getName())) {
                System.out.println("El usuario/a: " + userName + " ya existe, inténtalo con otro nombre");
                LOG.warn("El usuario: " + userName + " ya existe");
                isValid = false;
            }
        }

        //Si el usuario que se quiere crear es válido, se almacena en memoria y
        //y se guarda en el JSON
        if (isValid) {

            UserModel newUser = new UserModel(getNextId(), userName, password);
            this.jsonManager.updateUser(this.users, newUser);
            LOG.info("El usuario/a: " + userName + " se ha resgistrado satisfactoriamente");
        }
        return isValid;
    }

    //Calcula el id que se le debe asignar a un nuevo usuario en función del id
    //más alto que exista acutalmente
    private int getNextId() {

        //Valor de retorno
        int maxId = 0;

        for (int i = 0; i < this.users.size(); i++) {

            if (maxId < this.users.get(i).getId()) {
                maxId = this.users.get(i).getId();
            }
        }
        // Se incrementa el id
        maxId++;

        LOG.info("Se ha generado el ID: " + maxId);
        return maxId;

    }

    //Sobrecarga para utilizar el método desde fuera. Principalmente para debuggear
    public List<UserModel> getAllUsers() {
        // Valor de retorno
        UserModel[] usersArray = null;
        List<UserModel> usersList = new ArrayList<UserModel>();

        //Serializador JSON
        Gson gson = new Gson();

        //Leo el contenido del fichero
        String jsonText;
        try {
            jsonText = Files.readString(this.jsonManager.getJsonFilePath());
            //Deserializo el contendio del fichero en objetos tipo UserModel
            usersArray = gson.fromJson(jsonText, UserModel[].class);

            /*Tengo que añadirlo de esta manaera a la lista porque el tipo de 
            lista que devuelve la clase Gson es una fixed-list 
            (Lista de tamaño fijo) al igual que si utilizo el Arrays.toList().
             */
            if (usersArray != null) {
                for (UserModel user : usersArray) {
                    usersList.add(user);
                }
                LOG.info("Se ha deserializado correctamente el fichero " + this.jsonManager.getJsonFilePath().getFileName() + " y se ha obtenido a todos los usuarios");
            }
            else{
                users.add(new UserModel(1,"Pablo","console.log()"));
                LOG.warn("El fichero " + this.jsonManager.getJsonFilePath().getFileName() + " estaba vacío. Se ha generado el usuario por defecto:" + users.get(0).getName());
            }

        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(UserManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return usersList;
    }

    // Getters & Setters
    public List<UserModel> getUsers() {
        return users;
    }
}
