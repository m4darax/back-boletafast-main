package com.boletafast.main.app.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class SendMailServiceImpl implements SendMailService {

	private static final Logger LOG = LoggerFactory.getLogger(SendMailServiceImpl.class);
	
	@Autowired
	private Session mailSession;
	
	public Mono<Void> sendMail () {
		
		String to = "clever.rivera07@gmail.com"; // Dirección de correo electrónico del destinatario
	    String from = "clever.rivera07@gmail.com"; // Dirección de correo electrónico del remitente
	    String host = "smtp.gmail.com"; // Servidor SMTP de Gmail

	    // Configuración de propiedades SMTP
	    Properties props = new Properties();
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.host", host);
	    props.put("mail.smtp.port", "587");

	    // Autenticación de correo electrónico
	  //  Session session = mailSession;
	    
	    try {
	      // Creación de mensaje
	      Message message = new MimeMessage(mailSession);
	      message.setFrom(new InternetAddress(from));
	      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
	      message.setSubject("Asunto asincrono del correo");
	      String htmlBody = "<!DOCTYPE html>\r\n"
	      		+ "<html lang=\"en\">\r\n"
	      		+ "<head>\r\n"
	      		+ "    <meta charset=\"UTF-8\">\r\n"
	      		+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
	      		+ "    <title>Bienvenido a nuestra comunidad</title>\r\n"
	      		+ "    <style>\r\n"
	      		+ "        /* Estilos para el cuerpo del email */\r\n"
	      		+ "        body {\r\n"
	      		+ "            font-family: Arial, sans-serif;\r\n"
	      		+ "            font-size: 16px;\r\n"
	      		+ "            line-height: 1.5;\r\n"
	      		+ "            margin: 0;\r\n"
	      		+ "            padding: 0;\r\n"
	      		+ "            color: #333333;\r\n"
	      		+ "            background-color: #f9f9f9;\r\n"
	      		+ "        }\r\n"
	      		+ "        /* Estilos para el encabezado del email */\r\n"
	      		+ "        header {\r\n"
	      		+ "            background-color: #007bff;\r\n"
	      		+ "            color: #ffffff;\r\n"
	      		+ "            padding: 20px;\r\n"
	      		+ "        }\r\n"
	      		+ "        header h1 {\r\n"
	      		+ "            margin: 0;\r\n"
	      		+ "            font-size: 32px;\r\n"
	      		+ "            font-weight: bold;\r\n"
	      		+ "        }\r\n"
	      		+ "        /* Estilos para el contenido del email */\r\n"
	      		+ "        .content {\r\n"
	      		+ "            max-width: 600px;\r\n"
	      		+ "            margin: auto;\r\n"
	      		+ "            padding: 20px;\r\n"
	      		+ "            background-color: #ffffff;\r\n"
	      		+ "            border-radius: 10px;\r\n"
	      		+ "            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);\r\n"
	      		+ "        }\r\n"
	      		+ "        .content p {\r\n"
	      		+ "            margin-bottom: 20px;\r\n"
	      		+ "        }\r\n"
	      		+ "        .content button {\r\n"
	      		+ "            display: inline-block;\r\n"
	      		+ "            padding: 10px 20px;\r\n"
	      		+ "            background-color: #007bff;\r\n"
	      		+ "            color: #ffffff;\r\n"
	      		+ "            border: none;\r\n"
	      		+ "            border-radius: 5px;\r\n"
	      		+ "            text-decoration: none;\r\n"
	      		+ "            font-size: 16px;\r\n"
	      		+ "            font-weight: bold;\r\n"
	      		+ "            cursor: pointer;\r\n"
	      		+ "        }\r\n"
	      		+ "        .content button:hover {\r\n"
	      		+ "            background-color: #0069d9;\r\n"
	      		+ "        }\r\n"
	      		+ "    </style>\r\n"
	      		+ "</head>\r\n"
	      		+ "<body>\r\n"
	      		+ "    <header>\r\n"
	      		+ "        <h1>Bienvenido a nuestra comunidad</h1>\r\n"
	      		+ "    </header>\r\n"
	      		+ "    <div class=\"content\">\r\n"
	      		+ "        <p>¡Hola [nombre]!</p>\r\n"
	      		+ "        <p>Gracias por unirte a nuestra comunidad. Esperamos que disfrutes de los beneficios de ser parte de ella.</p>\r\n"
	      		+ "        <p>No dudes en contactarnos si tienes alguna pregunta o comentario.</p>\r\n"
	      		+ "        <button>Ir a la comunidad</button>\r\n"
	      		+ "    </div>\r\n"
	      		+ "</body>\r\n"
	      		+ "</html>";
	      message.setContent(htmlBody, "text/html; charset=utf-8");

	      // Envío de mensaje
	      //Transport.send(message);
	      
	      // Envío de mensaje de forma asíncrona
	      return Mono.fromRunnable(() -> {
	          try {
	              Transport.send(message);
	              LOG.info("CORREO ENVIADO");
	          } catch (MessagingException e) {
	              throw new RuntimeException(e);
	          }
	      });

	    } catch (MessagingException e) {
	      throw new RuntimeException(e);
	    }
	  }
	
}
