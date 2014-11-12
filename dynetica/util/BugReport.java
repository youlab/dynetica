/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dynetica.util;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
/**
 *
 * @author chrismurphy
 */
public class BugReport {
    
    private Session session;
    private String host;
    private String to;
    private String user;
    private String auth;
    
    public BugReport(){
        to = "chris.murphy@duke.edu";
        
        host = "smtp.gmail.com";
        user = "";
        auth = "";
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", user);
        props.put("mail.smtp.SocketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.fallback", "false");
        // Get the default Session object.
        session = Session.getDefaultInstance(props, null);
        session.setDebug(true);
    }

    
    public boolean sendMessage(String name, String date, String from, String msg){
        try{
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);
         
         message.setFrom(new InternetAddress(from));

         message.addRecipient(Message.RecipientType.TO,
                                  new InternetAddress(to));

         message.setSubject("Dyentica Bug Report on " + date + " from " + name + " at " + from);

         message.setText(msg);
           
         Transport transport = session.getTransport("smtp");
         transport.connect(host, user, auth);
         transport.sendMessage(message, message.getAllRecipients());
         transport.close();
         return true;
         
      }catch (MessagingException mex) {
         mex.printStackTrace();
         return false;
      }
    }
}
