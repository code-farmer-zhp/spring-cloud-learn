package com.feiniu.member.controller.comment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.controller.common.CommonController;
import com.feiniu.member.log.CustomLog;
import com.feiniu.member.service.comment.CommentAPIService;
import com.feiniu.member.util.IsNumberUtil;
import com.feiniu.member.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "comment")
public class CommentController extends CommonController {

    private static final CustomLog log = CustomLog.getLogger(CommentController.class);

    @Autowired
    protected CommentAPIService commentAPIService;

    @Value("${storefront.url}")
    private String storefrontUrl;

    /**
     * 待评论页面显示内容，默认为第一页
     * 
     * @param request
     * @param pageNo
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "myCommentView")
    public ModelAndView myCommentView(HttpServletRequest request,
                                      @RequestParam(value = "pageno", defaultValue = "1") String pageNo) {
        ModelAndView mav = getModel(request, "comment/my_comment_view");
        if (mav.getViewName().equals("redirect:" + loginUrl)) {
            return mav;
        }
        String memGuid = "";
        if (mav.getModel().get("memGuid") != null) {
            memGuid = mav.getModel().get("memGuid").toString();
        }
        String basePath = "";
        if (mav.getModel().get("basePath") != null) {
            basePath = mav.getModel().get("basePath").toString();
        }
        String url = basePath + "comment/myCommentView";
        mav.addObject("storefrontUrl", storefrontUrl);
        // 列表
        /**
         * 查询已评数量（商品维度）
         */
        //没有新需求 按老逻辑 显示所有已评论数据（不考虑是否是快配）
        Integer hasCommentNum = commentAPIService.hasCommentNum(memGuid,0);
        mav.addObject("memHasCommentCount", hasCommentNum);
        /**
         * 查询待评论内容(订单维度)
         */
        String commentInfo = commentAPIService.hasNoCommentOrderContent(memGuid, pageNo, 10);
        JSONObject commentInfoReslut = JSONObject.parseObject(commentInfo);
        if (commentInfoReslut.getInteger("code") != 200) {
            log.error("查询待评论(订单维度)接口口报错" + commentInfoReslut.getInteger("code"));
            return mav;
        }
        /**
         * 获取显示列表
         */
        Integer totalsum = commentInfoReslut.getJSONObject("data").getInteger("totalRows");
        JSONArray orderList = commentInfoReslut.getJSONObject("data").getJSONArray("pageVoList");
        mav.addObject("orderList", orderList);
        Map<String, Object> beforeMonthMap = validateAndPaging(pageNo, 10, url, totalsum);
        mav.addObject("pageDataBefore", beforeMonthMap.get("pageData"));
        return mav;
    }

    /**
     * 已评评论页面显示内容，默认为第一页
     * 
     * @param request
     * @param pageNo
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "hasCommentView")
    public ModelAndView hasCommentView(HttpServletRequest request,
                                       @RequestParam(value = "pageno", defaultValue = "1") String pageNo) {
        ModelAndView mav = getModel(request, "comment/has_comment_view");
        String memGuid = "";
        if (mav.getViewName().equals("redirect:" + loginUrl)) {
            return mav;
        }
        if (mav.getModel().get("memGuid") != null) {
            memGuid = mav.getModel().get("memGuid").toString();
        }
        String basePath = "";
        if (mav.getModel().get("basePath") != null) {
            Map<String, Object> mo = mav.getModel();
            basePath = mo.get("basePath").toString();
        }
        String url = basePath + "comment/hasCommentView";
        /**
         * 获取显示列表及评论总数(商品维度)
         */
        String memHasCommentInfo = commentAPIService.hasCommentContent(memGuid, pageNo, 10,0);
        JSONObject commentInfoReslut = JSONObject.parseObject(memHasCommentInfo);
        if (commentInfoReslut.getInteger("code") != 200) {
            log.error("查询追评接口报错" + commentInfoReslut.getInteger("code"));
            return mav;
        }
        /**
         * 获取显示列表
         */
        JSONObject comment_list = commentInfoReslut.getJSONObject("data");
        Integer totalSum = commentInfoReslut.getJSONObject("data").getInteger("totalRows");
        mav.addObject("comment_list", comment_list);
        Map<String, Object> beforeMonthMap = validateAndPaging(pageNo, 10, url, totalSum);
        mav.addObject("pageDataBefore", beforeMonthMap.get("pageData"));
        return mav;
    }

    /**
     * 包裹初评价显示
     * 
     * @param ogSeq
     * @param packageNo
     * @param supplierType
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "commentView/{ogSeq}/{packageNo}/{supplierType}")
    public ModelAndView commentView(@PathVariable("ogSeq") String ogSeq,
                                    @PathVariable("packageNo") String packageNo, @PathVariable("supplierType") String supplierType,
                                    HttpServletRequest request) {
        ModelAndView mav = getModel(request, "comment/my_comment_list");
        if (mav.getViewName().equals("redirect:" + loginUrl)) {
            return mav;
        }
        String memGuid = "";
        if (mav.getModel().get("memGuid") != null) {
            memGuid = mav.getModel().get("memGuid").toString();
        }
        String commentInfo = commentAPIService.commentView(memGuid, ogSeq, packageNo, supplierType);
        JSONObject commentInfoReslut = JSONObject.parseObject(commentInfo);
        if (commentInfoReslut.getInteger("code") != 200) {
            log.error("查询包裹初评价接口报错" + commentInfoReslut.getInteger("code"));
            return mav;
        }
        /**
         * 获取显示列表
         */
        JSONArray goodList = commentInfoReslut.getJSONArray("data");
        mav.addObject("goodList", goodList);
        return mav;
    }
    
    /**
     * 追加包裹评价显示
     * 
     * @param ogSeq
     * @param packagNo
     * @param supplierType
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "addCommentView/{ogSeq}/{packagNo}/{supplierType}")
    public ModelAndView addCommentView(@PathVariable("ogSeq") String ogSeq,
                                       @PathVariable("packagNo") String packagNo, @PathVariable("supplierType") String supplierType,
                                       HttpServletRequest request) {

        ModelAndView mav = getModel(request, "comment/add_comment_view");
        if (mav.getViewName().equals("redirect:" + loginUrl)) {
            return mav;
        }
        String memGuid = "";
        if (mav.getModel().get("memGuid") != null) {
            memGuid = mav.getModel().get("memGuid").toString();
        }
        
        String commentInfo = commentAPIService.addCommentView(ogSeq, packagNo, supplierType, memGuid);
        JSONObject commentInfoReslut = JSONObject.parseObject(commentInfo);
        if (commentInfoReslut.getInteger("code") != 200) {
            log.error("查询追评包裹评价接口报错" + commentInfoReslut.getInteger("code"));
            return mav;
        }
        /**
         * 追评数据显示
         */
        JSONArray string = commentInfoReslut.getJSONArray("data");
        mav.addObject("goodList", string);

        return mav;
    }

    /**
     * 店铺信息显示
     * 
     * @param merchantId
     * @param ogSeq
     * @param packagNo
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "getStoreComment")
    @ResponseBody
    public String getStoreComment(@RequestParam("merchantId") String merchantId,
                                  @RequestParam("ogSeq") String ogSeq, @RequestParam("packagNo") String packagNo) {
        String storeComment = commentAPIService.getStoreComment(merchantId, ogSeq, packagNo);
        return storeComment;
    }

    /**
     * 获取商品标签
     * 
     * @param market_id
     * @param type
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "getGoodsTags")
    @ResponseBody
    public String getGoodsTags(@RequestParam("market_id") String market_id, @RequestParam("type") String type) {
        String goodsTags = commentAPIService.getGoodsTags(market_id, type);
        return goodsTags;
    }

    /**
     * 评价商品信息
     * 
     * @param olSeq
     * @param is_anonymous
     * @param comment_star
     * @param product_mark
     * @param comment_text
     * @param commentPicUrls
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "AddMartComment")
    @ResponseBody
    public String addMartComment(@RequestParam("ol_seq") String olSeq,
                                 @RequestParam("is_anonymous") String is_anonymous,
                                 @RequestParam("comment_star") String comment_star,
                                 @RequestParam(value = "product_mark", defaultValue = "") String product_mark,
                                 @RequestParam("comment_text") String comment_text,
                                 @RequestParam(value = "commentPicUrls", defaultValue = "") String commentPicUrls,
                                 @RequestParam(value = "badCause", defaultValue = "") Integer badCause, HttpServletRequest request) {
        String memGuid = getMemGuid(request);
        String addMartComment = commentAPIService.addMartComment(memGuid, olSeq, is_anonymous, comment_star,
                product_mark, comment_text, commentPicUrls, badCause);
        return addMartComment;
    }

    /**
     * 评价店铺信息
     * 
     * @param ogSeq
     * @param packageNo
     * @param merchantId
     * @param scoreInfo
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "AddStoreComment")
    @ResponseBody
    public String addStoreComment(@RequestParam("ogSeq") String ogSeq,
                                  @RequestParam("packageNo") String packageNo, @RequestParam("merchantId") String merchantId,
                                  @RequestParam("scoreInfo") String scoreInfo, HttpServletRequest request) {
        String memGuid = getMemGuid(request);
        String addStoreComment = commentAPIService.addStoreComment(memGuid, ogSeq, packageNo, merchantId,
                scoreInfo);
        return addStoreComment;
    }

    /**
     * 追评信息提交
     * 
     * @param commentId
     * @param additionalCommentText
     * @param additionalPicUrls
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "addAdditionalGoodsComment")
    @ResponseBody
    public String addAdditionalGoodsComment(@RequestParam("commentId") String commentId,
                                            @RequestParam("additionalCommentText") String additionalCommentText,
                                            @RequestParam(value = "additionalPicUrls", defaultValue = "") String additionalPicUrls,
                                            HttpServletRequest request) {
        String memGuid = getMemGuid(request);
        String addAdditionalGoodsComment2 = commentAPIService.addAdditionalGoodsComment(memGuid, commentId,
                additionalCommentText, additionalPicUrls);
        return addAdditionalGoodsComment2;
    }

    /**
     * 自标签校验
     * 
     * @param addTagGoods
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "addTagGoods")
    @ResponseBody
    public JSONObject addTagGoods(@RequestParam("addTagGoods") String addTagGoods) {
        JSONObject retJson=new JSONObject();
        Boolean addTagGoods2 = commentAPIService.addTagGoods(addTagGoods);
        retJson.put("code",200);
        retJson.put("data",addTagGoods2);
        retJson.put("msg",addTagGoods2?"是敏感字！":"不是敏感字");
        return retJson;
    }

    /**
     * 图片上传
     * 
     * @param uploadFile
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "uploadPic", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String uploadPic(@RequestParam("uploadFile") CommonsMultipartFile uploadFile,
                            HttpServletRequest request) {
        String uploadPic = commentAPIService.uploadPic(uploadFile);
        return uploadPic;
    }


    /**
     * 删除差评
     * 
     * @param request
     * @param commentId
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "deleteComment")
    @ResponseBody
    public String deleteComment(HttpServletRequest request, @RequestParam("commentId") String commentId) {
        String memGuid = getMemGuid(request);
        String deleteComment = commentAPIService.deleteComment(memGuid, commentId);
        return deleteComment;
    }

    /**
     * 评价内容校验
     * 
     * @param request
     * @param context
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "checkComment")
    @ResponseBody
    public String check(HttpServletRequest request, @RequestParam("context") String context) {
        String check = commentAPIService.check(context);
        return check;
    }

    /**
     * 获取差评原因
     * 
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "badReasons")
    @ResponseBody
    public String badReasons(HttpServletRequest request) {
        String badReasons = commentAPIService.badReasons();
        return badReasons;
    }

    /**
     * 包裹一键好评
     * 
     * @param ogSeq
     * @param packageNo
     * @param supplierType
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "addWholePackageComment")
    @ResponseBody
    public String addWholePackageComment(@RequestParam("ogSeq") String ogSeq,
                                         @RequestParam("packageNo") String packageNo, @RequestParam("supplierType") String supplierType,
                                         HttpServletRequest request) {
        String memGuid = getMemGuid(request);
        String addWholePackageComment = commentAPIService.addWholePackageComment(memGuid, ogSeq, packageNo,
                supplierType);
        return addWholePackageComment;

    }

    /**
     * 整包裹评论
     * 
     * @param itemList
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "packageComment")
    @ResponseBody
    public String packageComment(@RequestParam(value = "itemList") String itemList, HttpServletRequest request) {
        String memGuid = getMemGuid(request);
        String packageComment = commentAPIService.packageComment(itemList, memGuid);
        return packageComment;
    }

    /**
     * 评价显示列表分页,验证参数和分页
     * 
     * @param memGuid
     * @param pageNo
     * @param pageSize
     * @param url
     * @param totalSum
     * @return
     */
    public Map<String, Object> validateAndPaging(String pageNo, Integer pageSize, String url, Integer totalSum) {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        if (totalSum > 0) {
            if (StringUtils.isEmpty(pageNo) || (!IsNumberUtil.isInteger(pageNo))) {
                pageNo = "1";
            }
            if (Integer.parseInt(pageNo) < 1) {
                pageNo = "1";
            }
            Double totalPage = Math.ceil(totalSum.doubleValue() / Double.valueOf(pageSize));
            Integer total = totalPage.intValue();
            if (Integer.parseInt(pageNo) > total) {
                pageNo = total.toString();
            }
            PageUtil pagedata = new PageUtil(Integer.parseInt(pageNo), totalSum, pageSize,
                    url);
            returnMap.put("pageData", paging(pagedata));
        }
        return returnMap;
    }

    /**
     *
     * @param data PageUtil 包含pageIndex，totalSize，totalRows.前台样式和跳转链接的设置
     * @return
     */
    public Map<String, Object> paging(PageUtil data) {
        Map<String, Object> pageMap = new HashMap<String, Object>();
        if (data.getPageSize() != 0 && data.getTotalSum() > 0) {
            if (data.getPageNo() == null) {
                data.setPageNo(1);
            }
            if (data.getPageSize() == null) {
                data.setPageSize(10);
            }
            Double totalPage = Math.ceil(data.getTotalSum().doubleValue() / data.getPageSize().doubleValue());
            data.setTotalPage(totalPage.intValue());

            if (data.getPageNo() > totalPage.intValue()) {
                data.setPageNo(totalPage.intValue());
            }
            if (data.getPageNo() == 1) {
                pageMap.put("fn_prve", "off");
            } else {
                pageMap.put("fn_prve", "on");
            }

            if (data.getPageNo() > 1) {
                pageMap.put("pre_href", data.getUrl() + "?pageno=" + (data.getPageNo() - 1));
            } else {
                pageMap.put("pre_href", "");
            }

            pageMap.put("pageNo", data.getPageNo());
            pageMap.put("totalpage", data.getTotalPage());

            if (data.getPageNo() >= data.getTotalPage()) {
                pageMap.put("fn_next", "off");
            } else {
                pageMap.put("fn_next", "on");
            }
            if (data.getPageNo() < data.getTotalPage()) {
                pageMap.put("next_href", data.getUrl() + "?pageno=" + (data.getPageNo() + 1));
            } else {
                pageMap.put("next_href", "");
            }
            pageMap.put("goUrl", data.getUrl());
        }
        return pageMap;
    }

    
    public String getMemGuid(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String loginCookie = "";
        String memGuid = "";
        try {
            for (Cookie c : cookies) {
                if (c.getName().equals(loginCookieName)) {
                    loginCookie = c.getValue();
                    break;
                }
            }
            String userInfo = restTemplate.postForObject(userApi + "?cookie=" + loginCookie, null, String.class);
            JSONObject userInfoJson = JSONObject.parseObject(userInfo);
            String json = userInfoJson.getString("data");
            JSONObject userJson = JSONObject.parseObject(json);
            memGuid = userJson.getString("MEM_GUID");
        } catch (Exception e) {
            log.error("查询用户memGuid接口报错",e);
        }
        return memGuid;
    }
}
