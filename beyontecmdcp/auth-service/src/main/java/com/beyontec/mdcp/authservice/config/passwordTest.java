package com.beyontec.mdcp.authservice.config;

import com.beyontec.mdcp.authservice.security.password.DesPasswordEncoder;
import com.beyontec.mdcp.authservice.util.GenerationUtils;

public class passwordTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String password = "admin";
		DesPasswordEncoder DesPasswordEncoder = new DesPasswordEncoder();
		String hfske= DesPasswordEncoder.encryptPassword(password);
		System.out.println("Encrypted Password "+DesPasswordEncoder.encryptPassword(password));
		

	}


}
