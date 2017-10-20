package com.feiniu.score.exception;

import com.feiniu.score.common.ResultCode;
import com.feiniu.score.dto.Result;
import com.sun.jersey.api.NotFoundException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * 统一异常处理器
 */
@Provider
public class ExceptionMapperSupport implements ExceptionMapper<Exception> {

	/**
	 * 异常处理
	 * 
	 * @param exception
	 * @return 异常处理后的Response对象
	 */
	public Response toResponse(Exception exception) {
		// 处理checked exception
		if (exception instanceof ScoreException) {
			ScoreException scoreException = (ScoreException) exception;
			Integer code = scoreException.getCode();			
			String message = scoreException.getMessage();
			if(code==null){
				code=ResultCode.RESULT_STATUS_EXCEPTION;
			}
			return Response.ok(new Result(code, message), MediaType.APPLICATION_JSON).build();

		} else if (exception instanceof NotFoundException) {
			return Response.ok(new Result(ResultCode.RESULT_API_NOT_FOUND_EXCEPTION, "调用的API不存在！"), MediaType.APPLICATION_JSON).build();
		} 
		
		return Response.ok(new Result(ResultCode.RESULT_RUN_TIME_EXCEPTION, "运行中异常:"+exception.getMessage()), MediaType.APPLICATION_JSON).build();
		
	}
}
