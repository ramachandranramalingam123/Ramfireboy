package com.beyontec.mdcp.notification.service.mail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.beyontec.mdcp.notification.dto.Attachment;
import com.beyontec.mdcp.notification.dto.SendMailDto;



@Component
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender emailSender;

	@Value("${mailSenderId}")
	private String mailSenderId;

	@Value("${mailSenderName}")
	private String mailSenderName;

	@Value("${mailingList}")
	private String mailingList;

	private static Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
	
	@Override
	public void sendMail(SendMailDto mailDto) {
		try {
			MimeMessage message = emailSender.createMimeMessage();
			// pass 'true' to the constructor to create a multipart message
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			if(mailDto.isSendToMailingList()) {
				helper.setTo(mailingList.split(","));
			} else {
				if(mailDto.getToEmail() != null)
					helper.setTo(mailDto.getToEmail().toArray(new String[mailDto.getToEmail().size()]));
			}
			helper.setSubject(mailDto.getSubject());
			if(mailDto.getCc() != null && !mailDto.getCc().isEmpty())
				helper.setCc(mailDto.getCc().toArray(new String[mailDto.getCc().size()]));
			if(mailDto.getBcc() != null && !mailDto.getBcc().isEmpty())
				helper.setCc(mailDto.getBcc().toArray(new String[mailDto.getBcc().size()]));

			helper.setText(mailDto.getMessage(), true);
			if (mailDto.isDisplayEmailSignature()) {

				URL url = ResourceUtils.getURL("classpath:images/companyLogo.png");
				File file = new File("companyLogo.png");
				FileUtils.copyURLToFile(url, file);
				helper.addInline("companyLogo", file);
			}
			helper.setFrom(mailSenderId, mailSenderName);
			
			List<Attachment> attachments = mailDto.getAttachments();
			if(attachments != null && !attachments.isEmpty()) {
				for(Attachment attachment : attachments) {
					if(attachment.getFilePath() !=null && !attachment.getFilePath().isEmpty()) {
						File file = new File(attachment.getFilePath());
						FileSystemResource resource = new FileSystemResource(file);
						helper.addAttachment(attachment.getAttachmentName(), resource);
					}else if(attachment.getFileByte() !=null && !attachment.getFileByte().equals("")) {
						helper.addAttachment(attachment.getAttachmentName(), new ByteArrayDataSource(attachment.getFileByte(),attachment.getContentType()));
					}
					
				}
			}

			emailSender.send(message);
		} catch (MessagingException | IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
}
