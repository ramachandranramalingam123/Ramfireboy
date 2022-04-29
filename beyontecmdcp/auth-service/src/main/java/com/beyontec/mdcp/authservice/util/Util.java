package com.beyontec.mdcp.authservice.util;

import java.time.Instant;
import java.time.LocalDateTime;

public class Util {
	
	public static Long getCurrentUnixTimestamp() {
		return Instant.now().getEpochSecond();
	}
	
	public static LocalDateTime getCurrentDateTime() {
		return LocalDateTime.now();
	}

}
