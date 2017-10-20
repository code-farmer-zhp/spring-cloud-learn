package com.feiniu.score.exception;

public class BizException extends RuntimeException {
	
	private static final long serialVersionUID = 7971701169700247915L;
	private Integer code;

	public BizException() {
		super();
	}

	public BizException(String message, Throwable cause) {
		super(message, cause);
	}

	public BizException(Integer code, String message) {
		super(message);
		this.code = code;
	}

	public BizException(Integer code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public BizException(String message) {
		super(message);
	}

	public BizException(Throwable cause) {
		super(cause);
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}
}
