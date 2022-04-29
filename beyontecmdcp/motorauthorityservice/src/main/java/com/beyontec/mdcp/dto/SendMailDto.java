package com.beyontec.mdcp.dto;




import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;


@Data
public class SendMailDto {

	private List<String> toEmail;

	@NotNull(message = "Subject Cannot be Empty")
	private String subject;

	@NotNull(message = "Message Cannot be Empty")
	private String message;

	private Integer userId;

	private List<Attachment> attachments;
	
	private String contentType;

	private boolean sendToMailingList = false;

	private boolean displayEmailSignature = false;
	
	public void addAttachment(Attachment attachment) {
		this.attachments.add(attachment);
	}
}
