
package com.beyontec.mdcp.authservice.exception;

import java.io.FileNotFoundException;

import javax.validation.ConstraintViolationException;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.beyontec.mdcp.authservice.response.Response;

import lombok.extern.slf4j.Slf4j;

/**
 * This GlobalExceptionHandler takes care of the exceptions arise on the run
 * time. Here Exceptions are categorized into multiple types to catch the
 * respective error and to report it. The Exceptions are logged by log
 * Properties with proper message.
 */
@Slf4j
@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
	
	

	/**
	 * Function to catch Exception by checking the argument is valid or not
	 * 
	 * @param ex       exception message
	 * @param response HttpServletResponse
	 * @return Exception Status and Message
	 */
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public Response<String> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		String errorMsg = ex.getBindingResult().getFieldErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage).findFirst().orElse(ex.getMessage());
		log.error(ex.getLocalizedMessage() + "{}", errorMsg);
		return setStatusAndMessage(HttpStatus.BAD_REQUEST.value(), errorMsg);
	}

	/**
	 * Method to catch Exception to find Zuul Connection Excpetions while
	 * configuring
	 * 
	 * @param ex       exception message
	 * @param response HttpServletResponse
	 * @return Exception Status and Message
	 */

	/*
	 * @ExceptionHandler(value = ZuulException.class) public Response<String>
	 * zuulException(ZuulException ex) { log.error(ex.getLocalizedMessage()); return
	 * setStatusAndMessage(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()); }
	 */
	/**
	 * Method to catch File Not Found exception on runtime execution
	 * 
	 * @param ex       exception message
	 * @param response HttpServletResponse
	 * @return Exception Status and Message
	 */
	@ExceptionHandler(value = FileNotFoundException.class)
	public Response<String> fileNotFoundException(FileNotFoundException ex) {
		log.error(ex.getLocalizedMessage());
		return setStatusAndMessage(HttpStatus.NOT_FOUND.value(), ex.getMessage());
	}

	/**
	 * Method to catch Array index bound exception on runtime
	 * 
	 * @param ex       exception message
	 * @param response HttpServletResponse
	 * @return Exception Status and Message
	 */
	@ExceptionHandler(value = ArrayIndexOutOfBoundsException.class)
	public Response<String> arrayIndexOutOfBoundsException(ArrayIndexOutOfBoundsException ex) {
		log.error(ex.getLocalizedMessage());
		return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
	}

	/**
	 * Method to catch Class Cast Exception while converting DataTypes from one to
	 * other.
	 * 
	 * @param ex       exception message
	 * @param response HttpServletResponse
	 * @return Exception Status and Message
	 */

	@ExceptionHandler(value = ClassCastException.class)
	public Response<String> classCastException(ClassCastException ex) {
		log.error(ex.getLocalizedMessage());
		return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
	}

	/**
	 * Method to catch Illegal Argument Exception it occurs when an illegal
	 * arguments passed.
	 * 
	 * @param ex       exception message
	 * @param response HttpServletResponse
	 * @return Exception Status and Message
	 */

	@ExceptionHandler(value = IllegalArgumentException.class)
	public Response<String> illegalArgumentException(IllegalArgumentException ex) {
		log.error(ex.getLocalizedMessage());
		return setStatusAndMessage(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
	}

	/**
	 * Method to catch Null pointer exception when null value passed to the
	 * response.
	 * 
	 * @param ex       exception message
	 * @param response HttpServletResponse
	 * @return Exception Status and Message
	 */
	@ExceptionHandler(value = NullPointerException.class)
	public Response<String> nullPointerException(NullPointerException ex) {
		log.error(ex.getLocalizedMessage());
		return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
	}

	/**
	 * Method to catch number Format exception it occurs, when an input passed as a
	 * string to DataType integer format this will result this NumberFormatException
	 * 
	 * @param ex       exception message
	 * @param response HttpServletResponse
	 * @return Exception Status and Message
	 */

	@ExceptionHandler(value = NumberFormatException.class)
	public Response<String> numberFormatException(NumberFormatException ex) {
		log.error(ex.getLocalizedMessage());
		return setStatusAndMessage(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
	}

	/**
	 * Method to catch Assertion Error it occurs, when an invalid assignment takes
	 * place.
	 * 
	 * @param ex       exception message
	 * @param response HttpServletResponse
	 * @return Exception Status and Message
	 */
	@ExceptionHandler(value = AssertionError.class)
	public Response<String> assertionError(AssertionError ex) {
		log.error(ex.getLocalizedMessage());
		return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
	}

	/**
	 * Method to catch Exception In Initializer Error it occurs, on Class
	 * initialization with constructors, variables and Functions
	 * 
	 * @param ex       exception message
	 * @param response HttpServletResponse
	 * @return Exception Status and Message
	 */

	@ExceptionHandler(value = ExceptionInInitializerError.class)
	public Response<String> exceptionInInitializerError(ExceptionInInitializerError ex) {
		log.error(ex.getLocalizedMessage());
		return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
	}

	/**
	 * Method to catch Stack Overflow Error on runtime
	 * 
	 * @param ex       exception message
	 * @param response HttpServletResponse
	 * @return Exception Status and Message
	 */
	@ExceptionHandler(value = StackOverflowError.class)
	public Response<String> stackOverflowError(StackOverflowError ex) {
		log.error(ex.getLocalizedMessage());
		return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
	}

	/**
	 * Method to catch No Class Def Found Error on runtime
	 * 
	 * @param ex       exception message
	 * @param response HttpServletResponse
	 * @return Exception Status and Message
	 */
	@ExceptionHandler(value = NoClassDefFoundError.class)
	public Response<String> noClassDefFoundError(NoClassDefFoundError ex) {
		log.error(ex.getLocalizedMessage());
		return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
	}

	/**
	 * Method to catch Runtime Exception.
	 * 
	 * @param ex       exception message
	 * @param response HttpServletResponse
	 * @return Exception Status and Message
	 */
	@ExceptionHandler(value = RuntimeException.class)
	public Response<String> resourceNotFoundException(RuntimeException ex) {
		log.error(ex.getLocalizedMessage());
		return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
	}

	/**
	 * Method to catch Constraint Violation Exception on runtime
	 * 
	 * @param ex       exception message
	 * @param response HttpServletResponse
	 * @return Exception Status and Message
	 */
	@ExceptionHandler(value = ConstraintViolationException.class)
	public Response<String> resourceNotFoundException(ConstraintViolationException ex) {
		log.error(ex.getLocalizedMessage());
		return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
	}

	/**
	 * Method to catch Exception on runtime This Exception is parent of all above
	 * Exception classes
	 * 
	 * @param ex       exception message
	 * @param response HttpServletResponse
	 * @return Exception Status and Message
	 */

	@ExceptionHandler(value = Exception.class)
	public Response<String> exception(Exception ex) {
		log.error(ex.getLocalizedMessage());
		return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
	}

	/**
	 * Method to set Status and Message in a response JSON format
	 * 
	 * @param status  Exception code
	 * @param message Exception description
	 * @return JSON format response
	 */

	@ExceptionHandler(value = BadDataExceptionHandler.class)
	public Response<String> badDataExceptionHandler(BadDataExceptionHandler ex) {
		log.error(ex.getLocalizedMessage() + "{}", ex.toString());
		return setStatusAndMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
	}

	@ExceptionHandler(value = NoDataFoundException.class)
	public Response<String> noDataFoundException(NoDataFoundException ex) {
		log.error(ex.getLocalizedMessage() + "{}", ex.toString());
		return setStatusAndMessage(HttpStatus.NO_CONTENT.value(), ex.getMessage());
	}

	private Response<String> setStatusAndMessage(int status, String message) {
		Response<String> reponseMap = new Response<>();
		reponseMap.setStatus(status);
		reponseMap.setData("");
		reponseMap.setMessage(message);
		return reponseMap;
	}
}
