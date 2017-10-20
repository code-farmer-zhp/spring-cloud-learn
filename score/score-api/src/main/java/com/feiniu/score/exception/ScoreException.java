package com.feiniu.score.exception;


public class ScoreException extends RuntimeException {

	private Integer code;
	
	public ScoreException() {
		super();
	}


	public ScoreException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ScoreException(Integer code, String message) {
		super(message);
		this.code = code;
	}
	
	public ScoreException(Integer code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public ScoreException(String message) {
		super(message);
	}

	public ScoreException(Throwable cause) {
		super(cause);
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
	
}
