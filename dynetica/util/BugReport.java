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
    private String from;
    
    public BugReport(){
        to = "c.murphy.693@gmail.com";
        
        host = "localhost";
        
        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);
        
        // Get the default Session object.
        session = Session.getDefaultInstance(properties);
    }
    
    public void setFrom(String f){
        from = f;
    }
    
    public void setHost(String h){
        host = h;
    }
    
    public boolean sendMessage(String name, String date, String f, String msg){
        try{
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);
         
         from = f;
         
         message.setFrom(new InternetAddress(from));

         message.addRecipient(Message.RecipientType.TO,
                                  new InternetAddress(to));

         message.setSubject("Dyentica Bug Report on" + date + "from" + name);

         message.setText(msg);

         Transport.send(message);
         return true;
         
      }catch (MessagingException mex) {
         mex.printStackTrace();
         return false;
      }
    }
}
