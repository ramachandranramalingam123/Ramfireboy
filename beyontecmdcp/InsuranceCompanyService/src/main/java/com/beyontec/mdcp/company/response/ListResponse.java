package com.beyontec.mdcp.company.response;




import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * This is a generic class, that acts as a container for any list of data, that needs to be sent in the response.
 * This is a class used for the send the response in json format
 * @param <T> the parameter of the class
 */
@JsonInclude(value = Include.NON_EMPTY)
public class ListResponse<T> {
	/**
     * Constructor of List Response which can invoke the method directly to activity
     *@param status get the status message
     *@param message get processing message
     * @param error get the error process
     * @param count get the number of result
     * @param totalRecords get the total number of results
     */
	public ListResponse(Integer status, String message, Boolean error, Long count, Integer totalRecords) {
		super();
		this.status = status;
		this.message = message;
		this.error = error;
		this.count = count;
		this.totalRecords = totalRecords;
	}
	/**
     * Constructor of List Response  which can invoke the method directly to activity
     *@param data get the data in json format
     *@param message get the message of process
     *@param error get the error value in boolean format
     * @param status get the status value of process
     * @param count get the number of results
     * @param totalRecords get the total number of records
     */
	public ListResponse(Integer status, T data, String message, Boolean error, Long count, Integer totalRecords) {
		super();
		this.status = status;
		this.data = data;
		this.message = message;
		this.error = error;
		this.count = count;
		this.totalRecords = totalRecords;
	}
	/**
     * Constructor of List Response  which can invoke the method directly to activity
     *@param data get the data in json format
     */
	public ListResponse(T data) {
		super();
		this.data = data;
	}
	/**
     * Constructor of List Response  which can invoke the method directly to activity
     */
	public ListResponse() {
	}

	private Integer status;
	private T data;
	private String message;
	private Boolean error;
	private Long count;
	private Integer totalRecords;
	
	/**
	 * to return number of records
	 * @return count records
	 */
	public Long getCount() {
		return count;
	}
	/**
	 * to return total records
	 * @return count total records
	 */
	public Integer getTotalRecords() {
		return totalRecords;
	}
	/**
	 * Method to use set the count 
	 * @param count results
	 */
	public void setCount(Long count) {
		this.count = count;
	}
	/**
	 * set the total records
	 * @param totalRecords
	 */
	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}
	/**
	 * return the boolean value
	 * @return true/false response
	 */
	public Boolean getError() {
		return error;
	}
	/**
	 * set the boolean value
	 * @param error
	 */
	public void setError(Boolean error) {
		this.error = error;
	}
	/**
	 * to return status 
	 * @return return the status of processing
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * return the data 
	 * @return return the data json format
	 */
	public T getData() {
		return data;
	}
	/**
	 * return the message success/failed
	 * @return return found/or not found message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * set the processing status
	 * @param status is processing value
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * set the date to send in json format
	 * @param data in json format
	 */
	public void setData(T data) {
		this.data = data;
	}
	/**
	 * set the message to send
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
