package com.boletafast.main.app.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import com.boletafast.main.app.templateshtml.TemplatesHtmlBoleta;

import reactor.core.publisher.Mono;

@Service
public class SendMailServiceImpl implements SendMailService {

	private static final Logger LOG = LoggerFactory.getLogger(SendMailServiceImpl.class);
	
	@Autowired
	private Session mailSession;
	
	public Mono<Void> sendMail (String mail, ByteArrayResource byteArrayResource) {
		
		String to = "clever.rivera07@gmail.com"; // Dirección de correo electrónico del destinatario
	    String from = mail; // Dirección de correo electrónico del remitente
	 
	    try {
	      // Creación de mensaje
	      Message message = new MimeMessage(mailSession);
	      message.setFrom(new InternetAddress(from));
	      message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
	      message.setSubject("Asunto asincrono del correo");
	      String htmlBody = TemplatesHtmlBoleta.TEMPLATE_BOLETA_MES;
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
