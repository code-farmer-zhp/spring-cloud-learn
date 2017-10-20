package com.feiniu.member.common;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@Component("requestDataValueProcessor")
public class CSRFRequestDataValueProcessor implements RequestDataValueProcessor {


    @Override
    public String processAction(HttpServletRequest request, String action, String httpMethod) {
        return action;
    }

    @Override
	public String processFormFieldValue(HttpServletRequest request,
                                        String name, String value, String type) {
		// TODO 暂时原样返回value
		return value;
	}

	@Override
	public Map<String, String> getExtraHiddenFields(HttpServletRequest request) {
		// 此处是当使用spring的taglib标签<form:form>创建表单时候，增加的隐藏域参数
		Map<String, String> hiddenFields = new HashMap<String, String>();
		hiddenFields.put(CSRFTokenManager.CSRF_PARAM_NAME,
				CSRFTokenManager.getTokenForSession(request.getSession()));

		return hiddenFields;
	}

	@Override
	public String processUrl(HttpServletRequest request, String url) {
		// TODO 暂时原样返回url
		return url;
	}

}