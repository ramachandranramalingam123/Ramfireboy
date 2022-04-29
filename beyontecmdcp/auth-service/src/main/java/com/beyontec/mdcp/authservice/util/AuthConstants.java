package com.beyontec.mdcp.authservice.util;

public class AuthConstants {
	public static final String AVAILABLE = "Data Retrieved Successfully";

	public static final String SUCCESS = "SUCCESS";

	public static final String NEW_USER_ADDED = "New User Added";

	public static final String USER = "User";

	public static final String NOT_AVAILABLE = "No Records Found";

	public static final String USER_ADD_ERROR = "Error in Adding New User";

	public static final String BAD_CREDENTIALS = "User not Available ->  Please enter valid credentials";

	public static final String INVALID_APP_LOGIN = "User Not Having Access";

	public static final String USER_NEED_APPROVAL = "User Approval Still Pending";

	public static final String USER_REJECTED = "User Has Been Rejected";

	public static final String USER_DELETED = "User Deleted";

	public static final String USER_DISABLED = "User DeActivated";

	public static final String LOGIN_SUCCESS = "Logged In Successfully";

	public static final String LOG_OUT_SUCCESS = "Log out Successfully";

	public static final String OTP_SUCCESS = "Otp Sent Successfully";

	public static final String MOBILE_NO_VALID = "Mobile Not Valid";

	public static final String TOKEN_EXPIRED = "Token Expired";

	public static final String TOKEN_NOT_AVAILABLE = "Token Not Available";

	public static final String AUTHORIZATION = "authorization";

	public static final String X_REQUEST_TYPE = "X-REQUEST-TYPE";

	public static final String MOBILE = "mobile";

	public static final String WEB = "web";

	public static final String FORGOT_TEMPLATE = "reset-pwd";

	public static final String CONTENT_TEXT_HTML = "text/html";

	public static final String CONTENT_TEXT_PLAIN = "text/plain";

	public static final String UNAUTHORIZED = "User UnAuthorized";

	public static final String INVALID_OTP = "Invalid OTP";

	public static final String FORGOT_PASS_SUCCESS = "Reset Password Email Sent Successfully";

	public static final String EMAIL_NOT_VALID = "Please enter your registered Email address";

	public static final String USER_INACTIVE = "User Not Active , Contact Admin for More Details";

	public static final String PASS_CHANGE_SUCCESS = "Password Successfully Changed";

	public static final String PASS_NOT_MATCHED = "Enter Proper Current Password ";

	public static final String USER_ADD_SUCCESS = "User Registered Successfully";

	public static final String USER_UPDATE_SUCCESS = "User Successfully Updated";

	public static final String EMAIL_NOT_AVAIL = "User email id is already registered";

	public static final String OTP_EXPIRED = "Otp Expired";

	public static final String CONTACT_NOT_AVAIL = "Mobile No Already Exist";

	public static final String USER_NOT_AVAIL = "Entered username or password is incorrect";
	
	public static final String REGISTERED_EMAIL_NOT_AVAIL = "Please enter your registered Email address";
	
	public static final String USER_ACCOUNT_INACTIVE = "Your user account is InActive";

	public static final String USER_NOT_FOUND = "User not found";

	public static final String FORGOT_PASS_LINK = "Password has been reset and sent to registered email id";

	public static final String PASS_CONFIRMATION = "Confirmation new password are not same";

	public static final String PASS_CONFIRMATION_SUCCESS = "Password has been changed successfully";

	public static final String ADD = "Add";

	public static final String WELCOME_MAIL_TEMPLATE = "login-success";
	public static final String USER_ALREADY_EXISTS = "User Already Exists";
	public static final String PASSWORD_SENT = "UserName and Password sent to your Email Adress";
	public static final String SIGNUP_FAILED = "Signup Failed";
	public static final String SMS_SENT = "SMS Sent Successfully";

	public static final String USER_BLOCKED = "Sorry Your Account is Blocked as You have "
			+ "exceeded the number of attempts";

	public static final String LOGGED_OUT = "Successfully logged out";

	public static final String PASSWORD_INAVLD = "Password must be 8 or more characters in length, Password must contain 1 or more uppercase characters and Password must contain 1 or more special characters";
	public static final String PASSWORDS_DONT_MATCH = "Invalid User Name or Password";

	public static final String ACCOUNT_LOCKED = "Account is locked";
	public static final String INVALID_OLD_PASSWORD = "Please enter your valid Username or Password";
	public static final String USER_ALREADY_LOGGED_IN = "Oops! Seems user is already logged in";

	public static final String USER_ALREADY_REGISTERED = "Sorry, the user is already registered";
	public static final String SMS = "SMS";

	public static final String LANGUAGE = "language";

	public static final int MAX_INVALID_LOGIN_ATTEMPT_COUNT = 10;

	public static final String PASSWORDS_DONT_MATCH_ERROR = "Invalid User Name or Password. No of attempt left ";

	public static final String PASSWORD_ATTEMPTS_EXCEEDED = "Password attempts exceeded. Please contact Admin";

	public static final String INVALID_CERT_NO = "Invalid Certificate Number";
	
	public static final String CUSTOMER_ID_ALREADY_EXIST = "User id already exist, Try with different userId";
	
	public static final String NO_CERTFICATES_AVAILBLE_MAIL = "There is no certificates for given mailId";

	public static final String NO_CERTFICATE_AVAILBLE_POLICYNO = "There is no certificate for given policyNo";
	
	public static final String CUSTOMER_CREATED_SUCCESSFULLY = "End customer created successfully";
	
	public static final String[] ONLINE_XLSX_SAMPLE_CERTIFICATE_HEADER = { "Policy holder", "Policy Number",
			"Inception Date(dd/MM/yyyy HH:mm)", "Expiry Date(dd/MM/yyyy)", "Registration", "Chassis", "Mark & Type", "Licensed To Carry", "Usage",
			"Email", "Issued By", "Approved By" };
	
	public static final String USER_ACTIVATED = "User has been activated, kindly check your mail and reset password";

	public static final String MAIL_QUOTA_EXCEED = "Email quota is exceeded for the day.";
	
	private AuthConstants() {
	}
}
