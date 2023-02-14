/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.psp_ev2_libexamen.managers;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author Guame
 */
public class JavaxMailManager {

    //Log4j logger que nos permitirá sacar logs por consola y en un fichero
    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(JavaxMailManager.class);

    public static void sendTestMail(String userName, String password, String receiverMail, boolean isBCC) throws RuntimeException {

        Scanner sc = new Scanner(System.in);

        //valora propiedades para construir la sesión con el servidor
        Properties props = new Properties();
        //servidor SMTP
        props.put("mail.smtp.host", "smtp.gmail.com");

        //identificación requerida
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP
        props.put("mail.smtp.port", "587"); //El puerto SMTP seguro de Google

        //abre una nueva sesión contra el servidor basada en:
        //el usuario, la contraseña y las propiedades especificadas
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });

        try {

            System.out.println("Introduce el asunto del mensaje");
            String mailSubject = sc.nextLine();

            System.out.println("Introduce el cuerpo del mensaje a enviar");
            String bodyMessage = sc.nextLine();

            //compone el mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(userName));

            // Aquí se elige como se quiere enviar el correo, en este caso, en copia oculta
            if (isBCC) {
                message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(receiverMail));

            } else {
                message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(receiverMail));
            }

            // Asunto
            message.setSubject(mailSubject);

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(bodyMessage, "text/html");

            // Creo una parte que se añade al mensaje
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            MimeBodyPart attachPart = new MimeBodyPart();

            // Adjuntamos la imagen al correo
            try {
                attachPart.attachFile(Paths.get(System.getProperty("user.dir"), "src", "main", "java", "data", "Portada_KoeNoKatachi.jpg").toString());
                multipart.addBodyPart(attachPart);
            } catch (IOException ex) {
                Logger.getLogger(JavaxMailManager.class.getName()).log(Level.SEVERE, null, ex);
            }

            message.setContent(multipart);

            //envía el mensaje, realizando conexión, transmisión y desconexión
            Transport.send(message);
            //lo da por enviado
            System.out.println("Enviado!");
        } catch (MessagingException e) {
            //tramita la excepción
            throw new RuntimeException(e);
        }
    }

    public static void readAllMails(String userEmail, String password) {
        try {
            Properties properties = new Properties();

            // Deshabilitamos TLS
            properties.setProperty("mail.pop3.starttls.enable", "false");

            // Hay que usar SSL
            properties.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.setProperty("mail.pop3.socketFactory.fallback", "false");

            // Puerto 995 para conectarse.
            properties.setProperty("mail.pop3.port", "995");
            properties.setProperty("mail.pop3.socketFactory.port", "995");

            // Abrimos la sesión
            Session sesion = Session.getInstance(properties);

            // Aquí almacenaremos los correos
            Store store = sesion.getStore("pop3");

            // Nos conectamos a nuestra cuenta
            store.connect("pop.gmail.com", userEmail, password);

            // Abriremos la carpeta (bandeja) que queremos leer
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            Message[] messages = folder.getMessages();

            System.out.println("Número de correos " + messages.length);

            for (int i = 0; i < messages.length; i++) {
                System.out.println("From:" + messages[i].getFrom()[0].toString());
                System.out.println("Subject:" + messages[i].getSubject());

                // Procesamos el contenido del mensaje para poder representarlo
                processMessagePart(messages[i]);

            }
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(JavaxMailManager.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (MessagingException ex) {
            Logger.getLogger(JavaxMailManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void readUnseenMails(String userEmail, String password) {
        try {
            Properties properties = new Properties();

            // Deshabilitamos TLS
            properties.setProperty("mail.pop3.starttls.enable", "false");

            // Hay que usar SSL
            properties.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.setProperty("mail.pop3.socketFactory.fallback", "false");

            // Puerto 995 para conectarse.
            properties.setProperty("mail.pop3.port", "995");
            properties.setProperty("mail.pop3.socketFactory.port", "995");

            // Abrimos la sesión
            Session sesion = Session.getInstance(properties);

            // Aquí almacenaremos los correos
            Store store = sesion.getStore("pop3");

            // Nos conectamos a nuestra cuenta
            store.connect("pop.gmail.com", userEmail, password);

            // Abriremos la carpeta (bandeja) que queremos leer
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            //Filtramos por los mensajes no vistos
            FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);

            Message[] messages = folder.search(ft);

            System.out.println("Número de correos " + messages.length);

            for (int i = 0; i < messages.length; i++) {
                System.out.println("From:" + messages[i].getFrom()[0].toString());
                System.out.println("Subject:" + messages[i].getSubject());

                // Procesamos el contenido del mensaje para poder representarlo
                processMessagePart(messages[i]);

            }
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(JavaxMailManager.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (MessagingException ex) {
            Logger.getLogger(JavaxMailManager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void processMessagePart(Part part) {
        try {
            // En caso de que sea un Multipart, se analiza recursivamente
            if (part.isMimeType("multipart/*")) {

                Multipart multi = (Multipart) part.getContent();

                for (int i = 0; i < multi.getCount(); i++) {
                    processMessagePart(multi.getBodyPart(i));
                }

            } else {
                // En caso de que sea texto plano, se saca por consola
                if (part.isMimeType("text/*")) {

                    System.out.println("Contenido " + part.getContentType());
                    System.out.println(part.getContent());

                } else {
                    // Si es imagen se visualiza en JFrame
                    if (part.isMimeType("image/*")) {

                        System.out.println("Imagen " + part.getContentType());
                        System.out.println("Fichero=" + part.getFileName());

                        renderImages(part);
                    } else {
                        // Si no es ninguna de las anteriores, se escribe en pantalla
                        // el tipo.
                        System.out.println("Recibido " + part.getContentType());

                    }
                }
                System.out.println("******************************");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void renderImages(Part imagePart) throws IOException, MessagingException {
        JFrame v = new JFrame();
        ImageIcon icon = new ImageIcon(ImageIO.read(imagePart.getInputStream()));
        JLabel label = new JLabel(icon);
        v.getContentPane().add(label);
        v.pack();
        v.setVisible(true);
    }

}
