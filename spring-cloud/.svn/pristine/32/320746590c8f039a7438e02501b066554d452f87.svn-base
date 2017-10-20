package com.feiniu.member.controller.feedback.touch;

import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.controller.comment.CommentController;
import com.feiniu.member.controller.common.TouchCommonController;
import com.feiniu.member.dto.PicUploadResult;
import com.feiniu.member.dto.TouchPostDto;
import com.feiniu.member.dto.TouchResultDto;
import com.feiniu.member.log.CustomLog;
import com.feiniu.member.service.FeedBackService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/touch/feedback")
public class FeedBackController  extends TouchCommonController {
    public static final CustomLog log = CustomLog.getLogger(FeedBackController.class);

    @Autowired
    private FeedBackService feedBackService;
    @Autowired
    private CommentController commentController;
    @Value("${m.login.url}")
    private String mLoginUrl;

    private int COMMOM_ERROR_CODE=1000;
    @RequestMapping(value="",method = RequestMethod.GET)
    public ModelAndView feedback(HttpServletRequest request) throws Exception {
        ModelAndView mav=getModel(request, "feedback/feedback");
        if(mav==null||mav.getModel().isEmpty()){
            return new ModelAndView("redirect:" + mLoginUrl);
        }
        if(mav.getViewName().contains("redirect"))
        {
            return mav;
        }
        return mav;
    }

    /**
     *
     * addFeedback  提交意见反馈信息
     * @param data 请求数据
     * @return
     * @throws Exception
     *Object
     * @exception
     */
    @RequestMapping(value="/addFeedback",method = RequestMethod.POST)
    @ResponseBody
    public Object addFeedback(HttpServletRequest request, @RequestParam("data") String data) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            String dataBackXss = data.replace("&amp;quot;", "\"");
            TouchPostDto touchPostDto = JSONObject.parseObject(dataBackXss, TouchPostDto.class);
            JSONObject paramBody = touchPostDto.getBody();
            String token = touchPostDto.getToken();
            // 获取guid，并判断是否登录
            String memberGuid = getGuid(request);
            if (StringUtils.isBlank(memberGuid)) {
                    return new TouchResultDto(startTime, System.currentTimeMillis(), COMMOM_ERROR_CODE,
                            "用户未登录", null);
            }

            //系统版本
            String sysVersion;
            if (StringUtils.isNotBlank(paramBody.getString("sys_version"))) {
                sysVersion = paramBody.getString("sys_version");
            } else {
                sysVersion = paramBody.getString("isdeviceInfo");
            }
            //API版本
            String apiVersion = touchPostDto.getApiVersion();
            //省市编号 "areaCode":"CS000016-0-0-0"
            String cityCode = "";
            if (touchPostDto.getAreaCode() != null) {
                cityCode = touchPostDto.getAreaCode().split("-")[0];
            }


            // 获取前端传入的
            // 图片路径以分号；分割
            String picUrls = paramBody.getString("picUrls");
            String urlsAfterPic = "";
            if (StringUtils.isNotBlank(picUrls)) {
                String[] picUrlsArray = picUrls.split(";");
                for (int i = 0; i < picUrlsArray.length; i++) {
                    String picUrlsStr = picUrlsArray[i];
//			picUrlsStr = picUrlsStr.replaceAll("http://imgsvr01.beta1.fn", "");
                    if (picUrlsStr.indexOf("/pic") > 0) {
                        picUrlsStr = picUrlsStr.substring(picUrlsStr.indexOf("/pic"));
                    }
                    if (i == picUrlsArray.length - 1) {
                        urlsAfterPic += picUrlsStr + "";
                    } else {
                        urlsAfterPic += picUrlsStr + ";";
                    }
                }

            }
            String content = paramBody.getString("content");

            Integer type = paramBody.getInteger("type");
            String contact = paramBody.getString("contact");

            String phoneType = paramBody.getString("phoneType");

            if (StringUtils.isBlank(content) && StringUtils.isBlank(urlsAfterPic)) {
                return new TouchResultDto(startTime, System.currentTimeMillis(), COMMOM_ERROR_CODE,
                        "客官对于我们哪里不满意，可以填写说明或者上传图片哦", new JSONObject());
            }

            if (StringUtils.isNotBlank(content)) {
                String comment = FeedBackService.filterStrNew(content);
                if (!content.equals(comment)) {
                    return new TouchResultDto(startTime, System.currentTimeMillis(), COMMOM_ERROR_CODE,
                            "意见反馈不能输入特殊符号和表情，请您检查后重新输入", new JSONObject());
                }
            }
            //cleanXSS中有html替换，且部分字符替换了两次，反解也需要两次。
            //反解的只用来判断长度，千万不能提交！！！
            //数据库字段1200。有至少200的富余用来存转义的字符。
            String unescapeStr= HtmlUtils.htmlUnescape(HtmlUtils.htmlUnescape(content));
            if (unescapeStr.length() > 500) {
                log.error("意见反馈输入字符太多,unescapeStr="+unescapeStr);
                return new TouchResultDto(startTime, System.currentTimeMillis(), COMMOM_ERROR_CODE,
                        "客官说得太多了，小的记不下了啦", new JSONObject());
            }
            if (contact!=null && contact.length() > 50) {
                return new TouchResultDto(startTime, System.currentTimeMillis(), COMMOM_ERROR_CODE,
                        "联系方式不得超过50字", new JSONObject());
            }
            JSONObject result = feedBackService.addFeedback(urlsAfterPic, content, type, contact, phoneType, memberGuid, sysVersion, cityCode, token);
            String code = result.getString("code");
            if (!"200".equals(code)) {
                return new TouchResultDto(startTime, System.currentTimeMillis(), COMMOM_ERROR_CODE,
                        result.getString("msg"), new JSONObject());
            }
            result.put("msg", "您的意见我们已经收到，感谢一路陪伴我们变得更好！");
            result.put("code", 200);
            return new TouchResultDto(startTime, System.currentTimeMillis(), 0, "您的意见我们已经收到，感谢一路陪伴我们变得更好！", result);
        }catch (Exception e){
            log.error("发表反馈失败",e);
            return new TouchResultDto(startTime, System.currentTimeMillis(), COMMOM_ERROR_CODE,"发表反馈失败，请稍后重试", new JSONObject());
        }
    }

    /**
     * 图片上传
     *
     * @param userfile
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/uploadPic")
    @ResponseBody
    public Object uploadPic(@RequestParam("userfile") CommonsMultipartFile userfile,
                            HttpServletRequest request) {
        long startTime = System.currentTimeMillis();
        try {
            String uploadStr = commentController.uploadPic(userfile, request);
            PicUploadResult uploadJson = JSONObject.parseObject(uploadStr, PicUploadResult.class);

            JSONObject returnBody = new JSONObject();
            if (uploadJson.isSuccess()) {
                returnBody.put("img", uploadJson.getOriginalUrl());
                return new TouchResultDto(startTime, System.currentTimeMillis(), 0, "", returnBody);
            } else {
                return new TouchResultDto(startTime, System.currentTimeMillis(), COMMOM_ERROR_CODE, uploadJson.getMsg(), returnBody);
            }
        }catch (Exception e){
                return new TouchResultDto(startTime, System.currentTimeMillis(), COMMOM_ERROR_CODE,"上传图片失败", new JSONObject());
        }
    }
}
