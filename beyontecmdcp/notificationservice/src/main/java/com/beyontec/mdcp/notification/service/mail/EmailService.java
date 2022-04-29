package com.beyontec.mdcp.notification.service.mail;

import com.beyontec.mdcp.notification.dto.SendMailDto;

public interface EmailService {
	
	void sendMail(SendMailDto mailDto);

}
