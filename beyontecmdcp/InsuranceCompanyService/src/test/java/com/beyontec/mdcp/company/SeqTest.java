package com.beyontec.mdcp.company;

public class SeqTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String emails = "san.@gmail.com,kan@gmail.com,lan@gmail.com";
		
		String email = "san.@gmail.com";
		StringBuffer sf = new StringBuffer();
		if(email.contains(",")) {
		String[] emailData =	emails.split(",");
		System.out.println(emailData[0]);
	for(int i = 1; i<emailData.length; i++) {
		
		sf.append(emailData[i]);
		sf.append(",");
	}
	System.out.println("SF>>>>>>>>>>full>>>>>>"+sf);
	sf.deleteCharAt(sf.length()-1);
	System.out.println("SF>>>>>>>>>>>>>>>>"+sf);
	}else {
		
	}
	}
}
