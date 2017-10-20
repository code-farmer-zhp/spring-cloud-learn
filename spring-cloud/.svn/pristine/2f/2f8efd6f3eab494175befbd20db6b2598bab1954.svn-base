package com.feiniu.member.controller.comment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.controller.common.TouchCommonController;
import com.feiniu.member.log.CustomLog;
import com.feiniu.member.service.comment.CommentAPIService;
import com.feiniu.member.util.PicRandomUtil;
import com.feiniu.member.util.PicUtil;
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

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.*;

@Controller
@RequestMapping(value = "/touch/comment")
public class TouchCommentController extends TouchCommonController {

    private static CustomLog log = CustomLog.getLogger(TouchCommentController.class);

    @Value("${m.login.url}")
    private String mLoginUrl;

    @Value("${imgInside.url}")
    private String imgInsideUrl;

    @Value("${store.url}")
    private String storeUrl;

    @Value("${m.staticDomain.url}")
    private String mStaticUrl;

    @Value("${m.feiniu.url}")
    private String mUrl;

    @Value("${m.item.feiniu.url}")
    private String itemUrl;

    @Value("${m.home.url}")
    private String mHomeUrl;

    @Value("${m.my.url}")
    private String mMyUrl;

    @Autowired
    protected CommentAPIService commentAPIService;

    private static DecimalFormat df = new DecimalFormat("#.00");

    /**
     * 待评论页面显示内容,默认为第一页
     *@param  pageType 0，未评论   1，已评论
     *@param  isKuaiPei 0,普通订单  2，快配订单
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "index")
    public ModelAndView myTouchCommentView(HttpServletRequest request,
                                           @RequestParam(value = "from", defaultValue = "0") String from,
                                           @RequestParam(value = "packNo", defaultValue = "0") String packNo,
                                           @RequestParam(value = "ogSeq", defaultValue = "0") String ogSeq,
                                           @RequestParam(value = "pageType", defaultValue = "0") String pageType,
                                           @RequestParam(value = "isKuaiPei", defaultValue = "0") String isKuaiPei) {
        String[] split = mStaticUrl.split(",");
        String mStaticUrlGo = split[new Random().nextInt(split.length)];
        ModelAndView mav = getModel(request, "comment/touch_comment_index");
        if (mav == null || mav.getModel().isEmpty()) {
            return new ModelAndView("redirect:" + mLoginUrl + "?gotourl=" + mMyUrl + "/touch/comment/index");
        }
        if (mav.getViewName().contains("redirect")) {
            return mav;
        }
        String memGuid = "";
        if (mav.getModel().get("memGuid") != null) {
            memGuid = mav.getModel().get("memGuid").toString();
        }
        // 查询已评论数量
        Integer hasCommentNum = commentAPIService.hasCommentNum(memGuid,(isKuaiPei!=null&&!"null".equals(isKuaiPei))? Integer.parseInt(isKuaiPei):0);
        mav.addObject("memHasCommentCount", hasCommentNum);
        // 查询待评论数据
        String commentInfo = commentAPIService.hasNoCommentContent(memGuid, from, packNo, ogSeq, "1", 10,(isKuaiPei!=null&&!"null".equals(isKuaiPei))? Integer.parseInt(isKuaiPei):0);
        JSONObject commentInfoReslut = JSONObject.parseObject(commentInfo);
        if (commentInfoReslut.getInteger("code") != 200) {
            log.error("查询评论接口报错" + commentInfoReslut.getInteger("code"));
            return mav;
        }
        // 获取显示列表
        Integer totalsum = commentInfoReslut.getJSONObject("data").getInteger("totalRows");
        JSONArray goodList = commentInfoReslut.getJSONObject("data").getJSONArray("dataList");
        // 查询未评论数量
        mav.addObject("memHasNoCommentCount", totalsum);
        StringBuilder sb = new StringBuilder();
        Map<String, ArrayList<JSONObject>> existence = new HashMap<String, ArrayList<JSONObject>>();
        Map<String, List<Integer>> supSeqList = new HashMap<String, List<Integer>>();
        if (null != goodList && goodList.size() > 0) {
            for (int i = 0, len = goodList.size(); i < len; i++) {
                JSONObject good = goodList.getJSONObject(i);
                String supplierType = good.getString("supplierType");
                String ono = good.getString("ono");
                String repOgSeq = ono.replace("CO", "CP");
                good.put("repOgSeq", repOgSeq);
                String order_detail_id = good.getString("itemId");
                String picUrl = good.getString("picUrl");
                String packageId = good.getString("packageId");
                String shop_id = good.getString("supSeq");
                if (StringUtils.isBlank(shop_id)) {
                    shop_id = "0";
                }
                if (StringUtils.isNotBlank(picUrl)) {
                    picUrl = PicUtil.picTransform(picUrl, storeUrl, "400x400", supplierType, PicRandomUtil.random(imgInsideUrl),
                            true);
                }
                good.put("picUrl", picUrl);
                if (existence.containsKey(ono)) {
                    List<JSONObject> arrayList = existence.get(ono);
                    JSONObject good1 = new JSONObject();
                    good1.put("title", good.getString("name"));
                    good1.put("add_id", good.getString("itemId"));
                    good1.put("goods_id", good.getString("skuSeq"));
                    good1.put("img", good.getString("picUrl"));
                    String skuSeq = good.getString("skuSeq");
                    good1.put("sm_seq", skuSeq);
                    good1.put("touch_item_href", !"1".equals(isKuaiPei) ? itemUrl + "/" + skuSeq : itemUrl + "/quick/" + skuSeq);
                    good1.put("touch_comment_href",
                            mMyUrl + "/touch/comment/comment_add?goods_id=" + good.getString("spuSeq") + "&supplier_type="
                                    + supplierType + "&order_id=" + ono + "&order_detail_id="
                                    + order_detail_id + "&img_url=" + good.getString("picUrl") + "&shop_id="
                                    + shop_id + "&package_id=" + packageId+("1".equals(isKuaiPei) ? "&isKuaiPei=1":""));
                    arrayList.add(good1);
                } else {
                    ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
                    JSONObject good1 = new JSONObject();
                    good1.put("title", good.getString("name"));
                    good1.put("add_id", good.getString("itemId"));
                    good1.put("goods_id", good.getString("skuSeq"));
                    good1.put("img", good.getString("picUrl"));
                    String skuSeq = good.getString("skuSeq");
                    good1.put("sm_seq", skuSeq);
                    good1.put("touch_item_href", !"1".equals(isKuaiPei) ? itemUrl + "/" + skuSeq : itemUrl + "/quick/" + skuSeq);
                    good1.put("touch_comment_href",
                            mMyUrl + "/touch/comment/comment_add?goods_id=" + good.getString("spuSeq") + "&supplier_type="
                                    + supplierType + "&order_id=" + ono + "&order_detail_id="
                                    + order_detail_id + "&img_url=" + good.getString("picUrl") + "&shop_id="
                                    + shop_id + "&package_id=" + packageId+("1".equals(isKuaiPei) ? "&isKuaiPei=1":""));
                    arrayList.add(good1);
                    existence.put(ono, arrayList);
                }
                if (good.getBooleanValue("virtualGood")) {
                    good.put("shopName", "手机充值");
                    good.put("icon", mStaticUrlGo + "/assets/images/my/member/icon_ziying_3x.png");
                } else if ("2".equals(supplierType)) {
                    //商城 提取供货商编号
                    String supSeq = good.getString("supSeq");
                    sb.append(supSeq).append(",");
                    if (supSeqList.containsKey(supSeq)) {
                        List<Integer> arrayList = supSeqList.get(supSeq);
                        arrayList.add(i);
                    } else {
                        List<Integer> li = new ArrayList<Integer>();
                        li.add(i);
                        supSeqList.put(supSeq, li);
                    }

                    good.put("icon", mStaticUrlGo + "/assets/images/my/member/icon_shoppingcart_shop_3x2.png");
                } else {
                    good.put("shopName", "飞牛网自营");
                    good.put("icon", mStaticUrlGo + "/assets/images/my/member/icon_ziying_3x.png");
                }
            }
        }
        //供货商编号长>0，递归遍历店铺信息
        if (sb.length() > 0) {
            // 请求店铺基础信息,数据实例
            String jsonObjectBasicInformation = commentAPIService.showShopInfo(sb.toString());
            JSONObject parseObject = JSONObject.parseObject(jsonObjectBasicInformation);
            JSONArray jsonArray = parseObject.getJSONArray("datas");
            if (null != jsonArray && jsonArray.size() > 0) {
                for (int i = 0, len = jsonArray.size(); i < len; i++) {
                    JSONObject good = jsonArray.getJSONObject(i);
                    String merchantId = good.getString("merchantId");
                    String storeName = good.getString("storeName");
                    List<Integer> list = supSeqList.get(merchantId);
                    for (Integer integer2 : list) {
                        JSONObject jsonObject3 = goodList.getJSONObject(integer2);
                        jsonObject3.put("shopName", storeName);
                    }
                }
            }
        }
        Set<String> ogSeqs = new HashSet<String>();
        JSONArray orderList = new JSONArray();

        //遍历existence （<ono,goodsList>）,组合封装order 并添加到list
        if (null != goodList && goodList.size() > 0) {
            for (int i = 0, len = goodList.size(); i < len; i++) {
                JSONObject jsonObject = goodList.getJSONObject(i);
                String ono = jsonObject.getString("ono");
                if (ogSeqs.contains(ono)) {
                    continue;
                } else {
                    ogSeqs.add(ono);
                }
                ArrayList<JSONObject> arrayList = existence.get(ono);
                JSONObject order = new JSONObject();
                if (arrayList.size() > 1) {
                    order.put("orderComment", 1);
                } else {
                    order.put("orderComment", 0);
                }
                String supSeq = jsonObject.getString("supSeq");
                if (StringUtils.isNotBlank(supSeq)) {
                    order.put("shop_id", supSeq);
                } else {
                    order.put("shop_id", 0);
                }
                order.put("icon", jsonObject.getString("icon"));
                order.put("time", jsonObject.getString("orderDate"));
                order.put("goods", arrayList);
                order.put("order_detail_id", jsonObject.getString("itemId"));
                order.put("package_id", jsonObject.getString("packageId"));
                order.put("order_id", ono);
                order.put("order_real_id", jsonObject.getString("repOgSeq"));
                order.put("shop_name", jsonObject.getString("shopName"));
                order.put("order_comment_href", mMyUrl
                        + "/touch/comment/comment_add?allOrderComment=1&order_id=" + ono);
                order.put("order_detail_href",
                        mHomeUrl + "/m/orderDetail/" + jsonObject.getString("repOgSeq"));
                orderList.add(order);
            }
        }
        mav.addObject("goodList", orderList);
        mav.addObject("pageType", pageType);
        return mav;
    }

    /**
     * 待评论页面显示内容,默认为第一页
     *
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "doTMycomment")
    @ResponseBody
    public String doTMycomment(HttpServletRequest request,
                               @RequestParam(value = "pageNo", defaultValue = "1") String pageNo,
                               @RequestParam(value = "from", defaultValue = "0") String from,
                               @RequestParam(value = "packNo", defaultValue = "0") String packNo,
                               @RequestParam(value = "ogSeq", defaultValue = "0") String ogSeq,
                               @RequestParam(value = "isKuaiPei", defaultValue = "0") String isKuaiPei) {
        String[] split = mStaticUrl.split(",");
        String mStaticUrlGo = split[new Random().nextInt(split.length)];
        String memGuid = getGuid(request);
        // 查询待评论数据
        String commentInfo = commentAPIService.hasNoCommentContent(memGuid, from, packNo, ogSeq, pageNo, 10,(isKuaiPei!=null&&!"null".equals(isKuaiPei))? Integer.parseInt(isKuaiPei):0);
        JSONObject commentInfoReslut = JSONObject.parseObject(commentInfo);
        if (commentInfoReslut.getInteger("code") != 200) {
            log.error("查询评论接口报错" + commentInfoReslut.getInteger("code"));
            return commentInfo;
        }
        // 获取显示列表
        JSONArray goodList = commentInfoReslut.getJSONObject("data").getJSONArray("dataList");
        StringBuilder sb = new StringBuilder();
        Map<String, ArrayList<JSONObject>> existence = new HashMap<String, ArrayList<JSONObject>>();
        Map<String, List<Integer>> supSeqList = new HashMap<String, List<Integer>>();
        if (null != goodList && goodList.size() > 0) {
            for (int i = 0, len = goodList.size(); i < len; i++) {
                JSONObject good = goodList.getJSONObject(i);
                String supplierType = good.getString("supplierType");
                String ono = good.getString("ono");
                String repOgSeq = ono.replace("CO", "CP");
                good.put("repOgSeq", repOgSeq);
                String picUrl = good.getString("picUrl");
                String order_detail_id = good.getString("itemId");
                String packageId = good.getString("packageId");
                String shop_id = good.getString("supSeq");
                picUrl = PicUtil.picTransform(picUrl, storeUrl, "400x400", supplierType, PicRandomUtil.random(imgInsideUrl), true);
                good.put("picUrl", picUrl);
                if (existence.containsKey(ono)) {
                    List<JSONObject> arrayList = existence.get(ono);
                    JSONObject good1 = new JSONObject();
                    good1.put("title", good.getString("name"));
                    good1.put("add_id", good.getString("itemId"));
                    good1.put("goods_id", good.getString("skuSeq"));
                    good1.put("img", good.getString("picUrl"));
                    String skuSeq = good.getString("skuSeq");
                    good1.put("sm_seq", skuSeq);
                    good1.put("touch_item_href", !"1".equals(isKuaiPei) ? itemUrl + "/" + skuSeq : itemUrl + "/quick/" + skuSeq);
                    good1.put("touch_comment_href",
                            mMyUrl + "/touch/comment/comment_add?goods_id=" + good.getString("spuSeq") + "&supplier_type="
                                    + supplierType + "&order_id=" + ono + "&order_detail_id="
                                    + order_detail_id + "&img_url=" + good.getString("picUrl") + "&shop_id="
                                    + shop_id + "&package_id=" + packageId);
                    arrayList.add(good1);
                } else {
                    ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
                    JSONObject good1 = new JSONObject();
                    good1.put("title", good.getString("name"));
                    good1.put("add_id", good.getString("itemId"));
                    good1.put("goods_id", good.getString("skuSeq"));
                    good1.put("img", good.getString("picUrl"));
                    String skuSeq = good.getString("skuSeq");
                    good1.put("sm_seq", skuSeq);
                    good1.put("touch_item_href", !"1".equals(isKuaiPei) ? itemUrl + "/" + skuSeq : itemUrl + "/quick/" + skuSeq);
                    good1.put("touch_comment_href",
                            mMyUrl + "/touch/comment/comment_add?goods_id=" + good.getString("spuSeq") + "&supplier_type="
                                    + supplierType + "&order_id=" + ono + "&order_detail_id="
                                    + order_detail_id + "&img_url=" + good.getString("picUrl") + "&shop_id="
                                    + shop_id + "&package_id=" + packageId);
                    arrayList.add(good1);
                    existence.put(ono, arrayList);
                }
                if (good.getBooleanValue("virtualGood")) {
                    good.put("shopName", "手机充值");
                    good.put("icon", mStaticUrlGo + "/assets/images/my/member/icon_ziying_3x.png");
                } else if ("2".equals(supplierType)) {
                    String supSeq = good.getString("supSeq");
                    sb.append(supSeq).append(",");
                    if (supSeqList.containsKey(supSeq)) {
                        List<Integer> arrayList = supSeqList.get(supSeq);
                        arrayList.add(i);
                    } else {
                        List<Integer> li = new ArrayList<Integer>();
                        li.add(i);
                        supSeqList.put(supSeq, li);
                    }
                    good.put("icon", mStaticUrlGo + "/assets/images/my/member/icon_shoppingcart_shop_3x2.png");
                } else {
                    good.put("shopName", "飞牛网自营");
                    good.put("icon", mStaticUrlGo + "/assets/images/my/member/icon_ziying_3x.png");
                }
            }
        }
        if (sb.length() > 0) {
            // 请求店铺基础信息,数据实例
            String jsonObjectBasicInformation = commentAPIService.showShopInfo(sb.toString());
            JSONObject parseObject = JSONObject.parseObject(jsonObjectBasicInformation);
            JSONArray jsonArray = parseObject.getJSONArray("datas");
            if (null != jsonArray && jsonArray.size() > 0) {
                for (int i = 0, len = jsonArray.size(); i < len; i++) {
                    JSONObject good = jsonArray.getJSONObject(i);
                    String merchantId = good.getString("merchantId");
                    String storeName = good.getString("storeName");
                    List<Integer> list = supSeqList.get(merchantId);
                    for (Integer integer2 : list) {
                        JSONObject jsonObject3 = goodList.getJSONObject(integer2);
                        jsonObject3.put("shopName", storeName);
                    }
                }
            }
        }
        Set<String> ogSeqs = new HashSet<String>();
        JSONArray orderList = new JSONArray();
        if (null != goodList && goodList.size() > 0) {
            for (int i = 0, len = goodList.size(); i < len; i++) {
                JSONObject jsonObject = goodList.getJSONObject(i);
                String ono = jsonObject.getString("ono");
                if (ogSeqs.contains(ono)) {
                    continue;
                } else {
                    ogSeqs.add(ono);
                }
                ArrayList<JSONObject> arrayList = existence.get(ono);
                JSONObject good = new JSONObject();
                if (arrayList.size() > 1) {
                    good.put("orderComment", 1);
                } else {
                    good.put("orderComment", 0);
                }
                String supSeq = jsonObject.getString("supSeq");
                if (StringUtils.isNotBlank(supSeq)) {
                    good.put("shop_id", supSeq);
                } else {
                    good.put("shop_id", 0);
                }
                good.put("icon", jsonObject.getString("icon"));
                good.put("time", jsonObject.getString("orderDate"));
                good.put("goods", arrayList);
                good.put("order_detail_id", jsonObject.getString("itemId"));
                good.put("package_id", jsonObject.getString("packageId"));
                good.put("order_id", ono);
                good.put("order_real_id", jsonObject.getString("repOgSeq"));
                good.put("shop_name", jsonObject.getString("shopName"));
                good.put("order_comment_href", mMyUrl
                        + "/touch/comment/comment_add?allOrderComment=1&order_id=" + ono);
                good.put("order_detail_href",
                        mHomeUrl + "/m/orderDetail/" + jsonObject.getString("repOgSeq"));
                orderList.add(good);
            }
        }
        commentInfoReslut.getJSONObject("data").put("dataList", orderList);
        return commentInfoReslut.toJSONString();
    }

    /**
     * 已评评论页面显示内容，默认为第一页
     *
     * @param request
     * @param pageNo
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "hasCommentView")
    @ResponseBody
    public String hasTouchCommentView(HttpServletRequest request,
                                      @RequestParam(value = "pageNo", defaultValue = "1") String pageNo,
                                      @RequestParam(value = "isKuaiPei", defaultValue = "0") String isKuaiPei) {
        String memGuid = getGuid(request);
        String hasCommentContent = commentAPIService.hasCommentContent(memGuid, pageNo, 10,"1".equals(isKuaiPei.trim())?1:0);
        JSONObject commentInfoReslut = JSONObject.parseObject(hasCommentContent);
        if (commentInfoReslut.getInteger("code") != 200) {
            log.error("查询评论借口报错" + commentInfoReslut.getInteger("code"));
            return hasCommentContent;
        }
        JSONArray orderList = new JSONArray();
        JSONArray dataList = commentInfoReslut.getJSONObject("data").getJSONArray("dataList");
        if (null != dataList && dataList.size() > 0) {
            for (int i = 0, len = dataList.size(); i < len; i++) {
                JSONObject js = new JSONObject();
                JSONObject jsonObject = dataList.getJSONObject(i);
                if (jsonObject.getBooleanValue("canAddComment")
                        && StringUtils.isBlank(jsonObject.getString("addCommentDate"))
                        && getMonth(jsonObject.getDate("commentDate"), new Date()) <= 90) {
                    js.put("is_show_append_button", 1);
                } else if (StringUtils.isNotBlank(jsonObject.getString("addCommentDate"))) {
                    js.put("is_show_append_button", 0);
                } else {
                    js.put("is_show_append_button", 2);
                }
                js.put("my_comment_img", jsonObject.getString("commentPicUrls").split(";"));
                js.put("impression", jsonObject.getString("productMark").split(","));
                String picUrl = jsonObject.getString("picUrl");
                String storeType = jsonObject.getString("storeType");
                String picTransform = PicUtil.picTransform(picUrl, storeUrl, "400x400", storeType,
                        PicRandomUtil.random(imgInsideUrl), true);
                js.put("img", picTransform);
                Integer starLevel = jsonObject.getInteger("starLevel");
                js.put("star", starLevel);
                js.put("service_comment", jsonObject.getString("replyText"));
                js.put("append_comment", jsonObject.getString("addCommentText"));
                js.put("service_append_comment", jsonObject.getString("addCommentReplyText"));
                js.put("id", jsonObject.getString("commentId"));
                js.put("title", jsonObject.getString("goodsName"));
                js.put("time", jsonObject.getString("commentDate"));
                js.put("append_comment_img", jsonObject.getString("addComnentPicUrls").split(";"));
                String[] sourceUrl = jsonObject.getString("skuId").split("/");
                String smSeq = sourceUrl[sourceUrl.length - 1];
                js.put("sm_seq", smSeq);
                js.put("touch_item_href", !"1".equals(isKuaiPei.trim()) ? itemUrl + "/" + smSeq : itemUrl + "/quick/" + smSeq);
                js.put("my_comment", jsonObject.getString("commentText"));
                js.put("readd_comment_url", mMyUrl + "/touch/comment/comment_readd?sm_seq=" + smSeq
                        + "&comment_id=" + jsonObject.getString("commentId") + "&img=" + picTransform
                        + "&price=" + df.format(jsonObject.getDouble("price")) + "&supplier_type="
                        + jsonObject.getString("storeType")+("1".equals(isKuaiPei.trim())?"&isKuaiPei=1":""));
                if (starLevel <= 3) {
                    js.put("can_del_comment", 1);
                } else {
                    js.put("can_del_comment", 0);
                }
                orderList.add(js);
            }
        }
        commentInfoReslut.getJSONObject("data").put("dataList", orderList);
        return commentInfoReslut.toJSONString();
    }

    /**
     * 包裹评论页面显示内容，默认为第一页
     */
    @RequestMapping(method = RequestMethod.GET, value = "comment_add")
    public ModelAndView allOrderComment(HttpServletRequest request,
                                        @RequestParam(value = "allOrderComment", defaultValue = "0") String allOrderComment,
                                        @RequestParam(value = "order_id") String orderId,
                                        @RequestParam(value = "goods_id", required = false) String goodsId,
                                        @RequestParam(value = "shop_id", required = false) String shopId,
                                        @RequestParam(value = "package_id", required = false) String packageId,
                                        @RequestParam(value = "img_url", required = false) String imgUrl,
                                        @RequestParam(value = "supplier_type", required = false) String supplier_Type,
                                        @RequestParam(value = "order_detail_id", required = false) String orderDetailId) {
        String[] split = mStaticUrl.split(",");
        String mStaticUrlGo = split[new Random().nextInt(split.length)];
        ModelAndView mav = getModel(request, "comment/touch_comment_add");
        if (mav == null || mav.getModel().isEmpty()) {
            return new ModelAndView("redirect:" + mLoginUrl);
        }
        if (mav.getViewName().contains("redirect")) {
            return mav;
        }
        String memGuid = "";
        if (mav.getModel().get("memGuid") != null) {
            memGuid = mav.getModel().get("memGuid").toString();
        }
        if ("1".equals(allOrderComment)) {
            String canPackageCommentView = commentAPIService.canPackageCommentView(memGuid, orderId);
            JSONObject packageInfoReslut = JSONObject.parseObject(canPackageCommentView);
            if (packageInfoReslut.getInteger("code") != 200) {
                log.error("查询包裹可凭数据接口报错" + packageInfoReslut.getInteger("code"));
                return mav;
            }
            JSONArray packLists = new JSONArray();
            JSONArray packageLists = packageInfoReslut.getJSONArray("data");
            if (null != packageLists && packageLists.size() > 0) {
                for (int i = 0, len = packageLists.size(); i < len; i++) {
                    JSONObject jo = packageLists.getJSONObject(i);
                    String packId = jo.getString("packageId");
                    String supplierType = jo.getString("supplierType");
                    String commentView = commentAPIService
                            .commentView(memGuid, orderId, packId, supplierType);
                    JSONObject commentInfoReslut = JSONObject.parseObject(commentView);
                    if (commentInfoReslut.getInteger("code") != 200) {
                        log.error("查询评论借口报错" + commentInfoReslut.getInteger("code"));
                        return mav;
                    }
                    JSONArray jsonArray = commentInfoReslut.getJSONArray("data");
                    String merchantId = jsonArray.getJSONObject(0).getString("supSeq");
                    String ogSeq = jsonArray.getJSONObject(0).getString("ono");
                    JSONObject json = new JSONObject();
                    if ("1".equals(supplierType)) {
                        json.put("shop_def_img", mStaticUrlGo + "/assets/images/my/member/icon_ziying_3x.png");
                        json.put("shop", "");
                        json.put("shop_name", "飞牛网自营");
                    } else {
                        String storeComment = commentAPIService.getStoreComment(merchantId, ogSeq, packId);
                        JSONObject storeCommentInfoReslut = JSONObject.parseObject(storeComment);
                        if (storeCommentInfoReslut.getInteger("code") != 200) {
                            log.error("查询店铺信息接口报错" + storeCommentInfoReslut.getInteger("code"));
                        } else {
                            JSONObject storeCommentInfoReslutData = storeCommentInfoReslut
                                    .getJSONObject("data");
                            JSONObject shop = new JSONObject();
                            String shop_name = storeCommentInfoReslutData.getString("storeName");
                            shop.put("id", storeCommentInfoReslutData.getString("merchantId"));
                            shop.put("service_star", 5);
                            shop.put("speed_star", 5);
                            shop.put("goods_star", 5);
                            shop.put("name", shop_name);
                            shop.put("img", storeCommentInfoReslutData.getString("storeLogoUrl"));
                            shop.put("package_id", packId);
                            json.put("shop", shop);
                            json.put("shop_name", shop_name);
                        }
                        json.put("shop_def_img", mStaticUrlGo
                                + "/assets/images/my/member/icon_shoppingcart_shop_3x2.png");
                    }
                    JSONArray js = new JSONArray();
                    for (int i1 = 0, len1 = jsonArray.size(); i1 < len1; i1++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i1);
                        JSONObject json1 = new JSONObject();
                        json1.put("impression", jsonObject.getJSONArray("labels"));
                        json1.put("star", 5);
                        json1.put("order_detail_id", jsonObject.getString("itemId"));
                        json1.put("img", PicUtil.picTransform(jsonObject.getString("picUrl"), storeUrl,
                                "400x400", supplierType, PicRandomUtil.random(imgInsideUrl), true));
                        json1.put("comment", "飞牛服务很不错，商品很喜欢，满意的一次购物体验，下次还来光顾哦！");
                        js.add(json1);
                    }
                    json.put("goods", js);
                    json.put("package_id", packId);
                    json.put("shop_type", supplierType);
                    packLists.add(json);
                }
            }
            String badReasons = commentAPIService.badReasons();
            JSONObject badReasonsInfoReslut = JSONObject.parseObject(badReasons);
            if (badReasonsInfoReslut.getInteger("code") != 200) {
                log.error("查询差评接口报错" + badReasonsInfoReslut.getInteger("code"));
            } else {
                mav.addObject("bad_reasons", badReasonsInfoReslut.getJSONArray("data"));
            }
            mav.addObject("order_id", orderId);
            mav.addObject("order_real_id", orderId.replace("CO", "CP"));
            mav.addObject("packages", packLists);
        } else {
            mav.addObject("star", 0);
            mav.addObject("img", imgUrl);
            mav.addObject("order_id", orderId);
            mav.addObject("order_real_id", orderId.replace("CO", "CP"));
            mav.addObject("order_detail_id", orderDetailId);
            mav.addObject("package_id", packageId);
            mav.addObject("shop_id", shopId);
            String badReasons = commentAPIService.badReasons();
            JSONObject badReasonsInfoReslut = JSONObject.parseObject(badReasons);
            if (badReasonsInfoReslut.getInteger("code") != 200) {
                log.error("查询差评接口报错" + badReasonsInfoReslut.getInteger("code"));
            } else {
                mav.addObject("bad_reasons", badReasonsInfoReslut.getJSONArray("data"));
            }
            String goodsTags = commentAPIService.getGoodsTags(goodsId, supplier_Type);
            JSONObject goodsTagsInfoReslut = JSONObject.parseObject(goodsTags);
            if (goodsTagsInfoReslut.getInteger("code") != 200) {
                log.error("查询商品标签接口报错" + goodsTagsInfoReslut.getInteger("code"));
            } else {
                mav.addObject("impression", goodsTagsInfoReslut.getJSONArray("data"));
            }
            JSONObject shop = new JSONObject();
            if ("2".equals(supplier_Type)) {
                String storeComment = commentAPIService.getStoreComment(shopId, orderId, packageId);
                JSONObject storeCommentInfoReslut = JSONObject.parseObject(storeComment);
                if (storeCommentInfoReslut.getInteger("code") != 200) {
                    log.error("查询店铺信息接口报错" + storeCommentInfoReslut.getInteger("code"));
                } else {
                    JSONObject storeCommentInfoReslutData = storeCommentInfoReslut.getJSONObject("data");
                    String shop_name = storeCommentInfoReslutData.getString("storeName");
                    shop.put("id", storeCommentInfoReslutData.getString("merchantId"));
                    shop.put("service_star", 0);
                    shop.put("speed_star", 0);
                    shop.put("goods_star", 0);
                    shop.put("name", shop_name);
                    shop.put("img", storeCommentInfoReslutData.getString("storeLogoUrl"));
                }
            }
            mav.addObject("shop", shop);
        }

        return mav;
    }

    /**
     * 商品追评显示
     *
     */
    @RequestMapping(method = RequestMethod.GET, value = "comment_readd")
    @ResponseBody
    public ModelAndView commentReadd(HttpServletRequest request,
                                     @RequestParam(value = "sm_seq") String sm_seq,
                                     @RequestParam(value = "comment_id") String comment_id,
                                     @RequestParam(value = "supplier_type") String supplier_type,
                                     @RequestParam(value = "price") String price, @RequestParam(value = "img") String img) {
        String[] split = mStaticUrl.split(",");
        String mStaticUrlGo = split[new Random().nextInt(split.length)];
        ModelAndView mav = getModel(request, "comment/touch_comment_readd");
        if (mav == null || mav.getModel().isEmpty()) {
            return new ModelAndView("redirect:" + mLoginUrl);
        }
        if (mav.getViewName().contains("redirect")) {
            return mav;
        }
        JSONObject js = new JSONObject();
        if ("1".equals(supplier_type)) {
            js.put("rlink", mStaticUrlGo + "/assets/images/my/member/icon_selfsupport2_2x.png");
            js.put("name", "自营");
        } else {
            js.put("rlink", mStaticUrlGo + "/assets/images/my/member/icon_directbusiness2_2x.png");
            js.put("name", "商家直送");
        }
        mav.addObject("tag", js);
        mav.addObject("img", img);
        mav.addObject("comment_id", comment_id);
        mav.addObject("price", price);
        String commodityInfo = commentAPIService.getCommodityInfo(sm_seq);
        JSONObject commodityInfoReslut = JSONObject.parseObject(commodityInfo);
        if (!commodityInfoReslut.getString("success").equals("1")) {
            log.error("商品追评接口报错" + commodityInfoReslut.getInteger("code"));
            return mav;
        }
        String title = commodityInfoReslut.getJSONArray("data").getJSONObject(0)
                .getString("title");
        mav.addObject("title", title);
        return mav;
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
        String memGuid = getGuid(request);
        String deleteComment = commentAPIService.deleteComment(memGuid, commentId);
        return deleteComment;
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
    @RequestMapping(method = RequestMethod.POST, value = "addItemComment")
    @ResponseBody
    public String addItemComment(@RequestParam("ol_seq") String olSeq,
                                 @RequestParam("is_anonymous") String is_anonymous,
                                 @RequestParam("comment_star") String comment_star,
                                 @RequestParam(value = "product_mark", defaultValue = "") String product_mark,
                                 @RequestParam("comment_text") String comment_text,
                                 @RequestParam(value = "commentPicUrls", defaultValue = "") String commentPicUrls,
                                 @RequestParam(value = "badCause", defaultValue = "") Integer badCause, HttpServletRequest request) {
        String check = commentAPIService.check(comment_text);
        JSONObject parseObject = JSONObject.parseObject(check);
        Boolean sensitiveWord2 = parseObject.getJSONObject("body").getBoolean("isSensitiveWord");
        if (sensitiveWord2) {
            String containSensitiveWord = parseObject.getJSONObject("body").getString("containSensitiveWord");
            JSONObject js = new JSONObject();
            js.put("code", 506);
            js.put("msg", "评价失败！您输入的内容包含敏感词\"" + containSensitiveWord + "\",请重新填写！");
            return js.toJSONString();
        }
        String memGuid = getGuid(request);
        String addMartComment = commentAPIService.addMartComment(memGuid, olSeq, is_anonymous, comment_star,
                product_mark, comment_text, commentPicUrls, badCause);
        return addMartComment;
    }

    /**
     * 图片上传
     *
     * @param uploadFile
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "uploadPic")
    @ResponseBody
    public String uploadPic(@RequestParam("uploadFile") CommonsMultipartFile uploadFile,
                            HttpServletRequest request) {
        String uploadPic = commentAPIService.uploadPic(uploadFile);
        return uploadPic;
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
    @RequestMapping(method = RequestMethod.POST, value = "addStoreComment")
    @ResponseBody
    public String addStoreComment(@RequestParam("ogSeq") String ogSeq,
                                  @RequestParam("packageNo") String packageNo, @RequestParam("merchantId") String merchantId,
                                  @RequestParam("scoreInfo") String scoreInfo, HttpServletRequest request) {
        String memGuid = getGuid(request);
        String addStoreComment = commentAPIService.addStoreComment(memGuid, ogSeq, packageNo, merchantId,
                scoreInfo);
        return addStoreComment;
    }

    /**
     * 整单评论提交
     *
     * @param itemList
     * @param ogSeq
     * @param is_anonymous
     * @param commentType
     * @param shopList
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "orderComment")
    @ResponseBody
    public String orderComment(@RequestParam(value = "itemList", defaultValue = "") String itemList,
                               @RequestParam("ogSeq") String ogSeq, @RequestParam("is_anonymous") Boolean is_anonymous,
                               @RequestParam("commentType") String commentType,
                               @RequestParam(value = "shopList", defaultValue = "") String shopList, HttpServletRequest request) {
        String memGuid = getGuid(request);
        return commentAPIService.orderComment(memGuid, itemList, ogSeq, is_anonymous, commentType, shopList);
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
        String memGuid = getGuid(request);
        String addAdditionalGoodsComment2 = commentAPIService.addAdditionalGoodsComment(memGuid, commentId,
                additionalCommentText, additionalPicUrls);
        return addAdditionalGoodsComment2;
    }

    public static int getMonth(Date start, Date end) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(start);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(end);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) // 同一年
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) // 闰年
                {
                    timeDistance += 366;
                } else // 不是闰年
                {
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else // 不同年
        {
            return day2 - day1;
        }
    }

}
