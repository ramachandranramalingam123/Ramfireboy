package com.beyontec.mdcp.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

	private String attachmentName;
	
	private String filePath;
	
	private byte[] fileByte;
	
	private String contentType;
	
}
