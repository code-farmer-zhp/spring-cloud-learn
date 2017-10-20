package com.feiniu.member.service.comment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.feiniu.member.dto.PicUploadResult;
import com.feiniu.member.log.CustomLog;
import com.feiniu.member.util.PicUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
public class CommentAPIService {

    private static final CustomLog log = CustomLog.getLogger(CommentAPIService.class);

    @Value("${myComment.api}")
    private String MY_COMMENT;

    @Value("${myCommentRow.api}")
    private String MY_COMMENT_ROW;

    @Value("${myCommentOrder.api}")
    private String MY_COMMENT_ORDER;

    @Value("${toComment.api}")
    private String to_comment;

    @Value("${store.url}")
    private String storeUrl;

    @Value("${addGoodsComment.api}")
    private String addGoodsComment;

    @Value("${getLabelsByGoodsId.api}")
    private String getLabelsByGoodsId;

    @Value("${getStoreInfo.api}")
    private String getStoreInfo;

    @Value("${addStoreComment.api}")
    private String addStoreComment;

    @Value("${addAdditionalGoodsComment.api}")
    private String addAdditionalGoodsComment;

    @Value("${repository.path}")
    private String repositoryPath;

    @Value("${comment.image.maxUploadSize}")
    private long imgMaxUploadSize;

    @Value("${upload.img.url}")
    private String uploadImgUrl;

    @Value("${toPackComment.api}")
    private String To_PACk_COMMENT;

    @Value("${toPackHascomment.api}")
    private String To_PACK_HASCOMMENT;


    @Value("${deleteGoodsComment.api}")
    private String deleteGoodsComment;

    @Value("${requestFactory.readTimeout}")
    private int readTimeout;

    @Value("${requestFactory.connectTimeout}")
    private int connectTimeout;

    @Value("${download.img.url}")
    private String downloadImgUrl;

    @Value("${isSensitiveWordList.api}")
    private String isSensitiveWordListApi;

    @Value("${getBadCause.api}")
    private String getBadCauseApi;

    @Value("${addWholePackageComment.api}")
    private String addWholePackageCommentApi;

    @Value("${addGoodsCommentList.api}")
    private String addGoodsCommentListApi;

    @Value("${member.orders.url}")
    private String memberOrdersUrl;

    @Value("${getMerchantStoreListByMerchantIds.api}")
    private String getMerchantStoreListByMerchantIdsApi;

    @Value("${packageInfoForOrder.api}")
    private String packageInfoForOrderApi;

    @Value("${getSeckillInfo.api}")
    private String getSeckillInfoApi;
    
    @Autowired
    protected RestTemplate restTemplate;

    // 允许上传的图片集合
    private static Set<String> imageTypeSet = new HashSet<>();

    static {
        // 允许上传的格式
        final String[] imageTypeArr = new String[] { "jpg", "jpeg", "gif", "png" };
        imageTypeSet.addAll(Arrays.asList(imageTypeArr));
    }

    /**
     * 查询已评论数量
     */
    public Integer hasCommentNum(String memGuid, int isFastDelivery) {
        try {
            MultiValueMap<String, Object> params1 = new LinkedMultiValueMap<String, Object>();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("memGuid", memGuid);
            jsonObject.put("isFastDelivery",isFastDelivery);
            params1.add("params", jsonObject.toString());
            String memHasCommentCount = restTemplate.postForObject(MY_COMMENT_ROW, params1, String.class);
            JSONObject memHasCommentCountObj = JSONObject.parseObject(memHasCommentCount);
            return memHasCommentCountObj.getInteger("data");
        } catch (Exception e) {
            log.error("查询未评论数量错误", e);
            return 0;
        }

    }

    /**
     * 查询未评论内容(订单维度)
     */
    public String hasNoCommentOrderContent(String memGuid, String pageNo, Integer pageSize) {
        // 列表
        try {
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("memberId", memGuid);
            jsonObj.put("orderType", 4);
            jsonObj.put("currPage", pageNo);
            jsonObj.put("pageCount", pageSize);
            jsonObj.put("department", "member");
            jsonObj.put("isHide", 1);
            params.add("request", jsonObj.toJSONString());
            String commentInfo = restTemplate.postForObject(memberOrdersUrl, params, String.class);
            return commentInfo;
        } catch (Exception e) {
            log.error("查询未评论内容(订单维度)错误", e);
            return "{\"code\":\"505\"}";
        }

    }
    /**
     * 查询未评论内容(商品维度)
     * @param string 
     * @param ogSeq 
     * @param string2
     * @param  from 1,来自单个订单点击
     */
    public String hasNoCommentContent(String memGuid, String from, String packNo, String ogSeq, String pageNo, Integer pageSize, int isFastDelivery) {
        // 列表
        try {
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
            JSONObject json1 = new JSONObject();
            json1.put("memGuid", memGuid);
            json1.put("curPage", pageNo);
            json1.put("isFastDelivery",isFastDelivery);
            if(from.equals("1")){
                json1.put("packNo", packNo);
                json1.put("ogSeq", ogSeq);
            }
            json1.put("pageSize", pageSize);
            params.add("params", json1.toString());
            String commentInfo = restTemplate.postForObject(to_comment, params, String.class);
            return commentInfo;
        } catch (Exception e) {
            log.error("查询未评论内容(商品维度)错误", e);
            return "{\"code\":\"505\"}";
        }

    }

    /**
     * 触屏通过sm_seq查询商品信息
     */
    public String getCommodityInfo(String smSeq) {
        try {
                //CS000016-310100-310101
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
            JSONObject js = new JSONObject();
                JSONObject areaCode=new JSONObject();
                areaCode.put("provinceCode", "CS000016");
                areaCode.put("cityCode", "310100");
                areaCode.put("areaCode", "310101");
                js.put("skuSeqs", smSeq);
                js.put("areaCode", areaCode.toJSONString());
            params.add("data", js.toJSONString());
            String commentInfo = restTemplate.postForObject(getSeckillInfoApi, params, String.class);
            return commentInfo;
        } catch (Exception e) {
            log.error("触屏通过sm_seq查询商品信息", e);
            return "{\"code\":\"505\"}";
        }

    }
    /**
     * 查询店铺信息
     */
    public String showShopInfo(String merchantIds) {
        try {
            MultiValueMap<String, Object> formDataBasicInformation = new LinkedMultiValueMap<String, Object>();
            formDataBasicInformation.add("merchantIds", merchantIds);
            String jsonObjectBasicInformation = restTemplate.postForObject(getMerchantStoreListByMerchantIdsApi,
                    formDataBasicInformation, String.class);
            return jsonObjectBasicInformation;
        } catch (Exception e) {
            log.error("查询店铺信息错误", e);
            return "{\"code\":\"505\"}";
        }

    }

    /**
     * 已评评论页面显示内容
     */
    public String hasCommentContent(String memGuid, String pageNo, Integer pageSize, int isFastDelivery) {
        try {
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
            JSONObject json1 = new JSONObject();
            json1.put("memGuid", memGuid);
            json1.put("curPage", pageNo);
            json1.put("pageSize", pageSize);
            json1.put("isFastDelivery",isFastDelivery);
            params.add("params", json1.toString());
            String memHasCommentInfo = restTemplate.postForObject(MY_COMMENT, params, String.class);
            return memHasCommentInfo;
        } catch (Exception e) {
            log.error("追评显示错误", e);
            return "{\"code\":\"505\"}";
        }

    }

    /**
     * 包裹评价显示
     */
    public String commentView(String memGuid, String ogSeq, String packageNo, String supplierType) {
        try {
            MultiValueMap<String, Object> params1 = new LinkedMultiValueMap<String, Object>();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("memGuid", memGuid);
            jsonObject.put("supplierType", supplierType);
            jsonObject.put("packageNo", packageNo);
            jsonObject.put("ogSeq", ogSeq);
            jsonObject.put("version", "1.0");
            params1.add("params", jsonObject.toString());
            String commentInfo = restTemplate.postForObject(To_PACk_COMMENT, params1, String.class);
            return commentInfo;
        } catch (Exception e) {
            log.error("评价按钮显示错误", e);
            return "{\"code\":\"505\"}";
        }

    }
    /**
     * 订单查询可评价包裹
     */
    public String canPackageCommentView(String memGuid, String ogSeq) {
        try {
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ogSeq", ogSeq);
            jsonObject.put("memGuid", memGuid);
            params.add("params", jsonObject.toString());
            // packageList解析可凭数量包裹信息（packageNo包裹序号，packId包裹ID,supplierType包裹类型）
            String packageInfo = restTemplate.postForObject(packageInfoForOrderApi, params, String.class);
            return packageInfo;
        } catch (Exception e) {
            log.error("订单查询可评价包裹错误", e);
            return "{\"code\":\"505\"}";
        }

    }
    /**
     * 店铺信息显示
     * 
     */
    public String getStoreComment(String merchantId, String ogSeq, String packagNo) {
        try {
            MultiValueMap<String, Object> params1 = new LinkedMultiValueMap<String, Object>();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchantId);
            jsonObject.put("ogSeq", ogSeq);
            jsonObject.put("packageNo", packagNo);
            params1.add("params", jsonObject.toString());
            String result = restTemplate.postForObject(getStoreInfo, params1, String.class);
            JSONObject resJsonObject = JSONObject.parseObject(result);
            JSONObject data = (JSONObject) resJsonObject.get("data");
            if (data != null && data.size() > 0) {
                String storeLogoUrl = data.getString("storeLogoUrl");
                String url = data.getString("url").replace("beta1", "beta2");
                data.put("url", url);
                if (StringUtils.isNotBlank(storeLogoUrl)) {
                    String transformPic = PicUtil.picTransform(storeLogoUrl, storeUrl);
                    data.put("storeLogoUrl", transformPic);
                }
                    resJsonObject.put("data", data);
                }
            return resJsonObject.toJSONString();
        } catch (Exception e) {
            log.error("查询店铺信息并处理店铺信息中图片处理出错", e);
            return "{\"code\":\"505\"}";
        }
    }

    /**
     * 获取商品标签
     * 
     */
    public String getGoodsTags(String marketId, String type) {
        try {
            MultiValueMap<String, Object> params1 = new LinkedMultiValueMap<String, Object>();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("supplierType", type);
            jsonObject.put("goodsId", marketId);
            params1.add("params", jsonObject.toString());
            String result = restTemplate.postForObject(getLabelsByGoodsId, params1, String.class);
            return result;
        } catch (Exception e) {
            log.error("获取商品标签出错", e);
            return "{\"code\":\"505\"}";
        }

    }

    /**
     * 评价商品信息
     * 
     */
    public String addMartComment(String memGuid, String olSeq, String isAnonymous, String commentStar,
                                 String productMark, String commentText, String commentPicUrls, Integer badCause) {
        try {
            MultiValueMap<String, Object> paramsMap = new LinkedMultiValueMap<String, Object>();
            JSONObject params1 = new JSONObject();
            params1.put("memGuid", memGuid);
            params1.put("olSeq", olSeq);
            if ("false".equals(isAnonymous)) {
                params1.put("isAnonymous", "0");
            }
            if ("true".equals(isAnonymous)) {
                params1.put("isAnonymous", "1");
            }
            params1.put("commentStar", commentStar);
            params1.put("productMark", productMark);
            if (Integer.parseInt(commentStar) < 3) {
                params1.put("badCause", badCause);
            }
            params1.put("commentText", commentText);
            params1.put("commentPicUrls", commentPicUrls);
            params1.put("commentSource", "p");
            paramsMap.add("params", params1.toString());
            String result = restTemplate.postForObject(addGoodsComment, paramsMap, String.class);
            return result;
        } catch (Exception e) {
            log.error("评价商品信息出错", e);
            return "{\"code\":\"505\"}";
        }

    }

    /**
     * 评价店铺信息
     */
    public String addStoreComment(String memGuid, String ogSeq, String packageNo, String merchantId,
                                  String scoreInfo) {
        try {
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
            JSONObject jsonObject = new JSONObject();
            scoreInfo = scoreInfo.replace("&amp;quot;", "\"");
            jsonObject.put("ogSeq", ogSeq);
            jsonObject.put("packageNo", packageNo);
            jsonObject.put("merchantId", merchantId);
            jsonObject.put("scoreInfo", JSONObject.parseArray(scoreInfo));
            jsonObject.put("memGuid", memGuid);
            jsonObject.put("commentSource", "p");
            params.add("params", jsonObject.toString());
            String result = restTemplate.postForObject(addStoreComment, params, String.class);
            return result;
        } catch (Exception e) {
            log.error("评价店铺信息出错", e);
            return "{\"code\":\"505\"}";
        }
    }

    /**
     * 追评信息提交
     * 
     */
    public String addAdditionalGoodsComment(String memGuid, String commentId, String additionalCommentText,
                                            String additionalPicUrls) {
        try {
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("commentId", commentId);
            jsonObject.put("addCommentText", additionalCommentText);
            jsonObject.put("addCommentPicUrls", additionalPicUrls);
            jsonObject.put("memGuid", memGuid);
            params.add("params", jsonObject.toString());
            String result = restTemplate.postForObject(addAdditionalGoodsComment, params, String.class);
            return result;
        } catch (Exception e) {
            log.error("追评信息提交出错", e);
            return "{\"code\":\"505\"}";
        }

    }

    /**
     * 自标签校验
     * 
     */
    public Boolean addTagGoods(String addTagGoods) {
        try {
            MultiValueMap<String, Object> params1 = new LinkedMultiValueMap<String, Object>();
            params1.add("sensitiveWord", addTagGoods);
            String result = restTemplate.postForObject(isSensitiveWordListApi, params1, String.class);
            Boolean sensitiveWord2 = JSONObject.parseObject(result).getJSONObject("body").getBoolean("isSensitiveWord");
            return sensitiveWord2;
        } catch (Exception e) {
            log.error("自标签校验出错", e);
            return false;
        }
    }

    /**
     * 图片上传
     * 
     */
    public String uploadPic(CommonsMultipartFile uploadFile) {
        // 封装Result对象
        PicUploadResult picUploadResult = new PicUploadResult(true);

        if (uploadFile.getSize() == 0) {
            picUploadResult.setSuccess(false);
            picUploadResult.setMsg("您上传的图片大小为0，请重新上传图片！");
            return JSONObject.toJSONString(picUploadResult);
        } else if (imgMaxUploadSize * 2 < uploadFile.getSize()) { // 判断文件的大小是否超过2M
            picUploadResult.setSuccess(false);
            picUploadResult.setMsg("您上传的图片过大，请上传2M以内的图片！");
            return JSONObject.toJSONString(picUploadResult);
        }

        // 文件扩展名
        String fileExtension = StringUtils.substringAfterLast(uploadFile.getOriginalFilename(), ".");
        fileExtension = StringUtils.isBlank(fileExtension) ? "" : fileExtension.toLowerCase();
        // 验证图片格式
        if (picUploadResult.isSuccess() && !imageTypeSet.contains(fileExtension)) {
            picUploadResult.setSuccess(false);
            picUploadResult.setMsg("您上传的图片格式不正确，请上传JPG/JPEG/PNG/GIF格式文件！");
            return JSONObject.toJSONString(picUploadResult);
        }

        if (picUploadResult.isSuccess()) {
            try {
                String filePath = repositoryPath + File.separator + getUUID() + "." + fileExtension;
                File file = new File(filePath);
                if (!file.getParentFile().exists()) {
                    // 目录不存在从新建目录
                    file.getParentFile().mkdirs();
                }
                // 写文件到磁盘
                uploadFile.transferTo(file);
                FileInputStream fis = new FileInputStream(file);
                BufferedImage sourceImg = ImageIO.read(fis);
                if (sourceImg.getWidth() < 100 || sourceImg.getHeight() < 100) {
                    picUploadResult.setSuccess(false);
                    picUploadResult.setMsg("请上传图片尺寸大于100*100的图片！");
                    return JSONObject.toJSONString(picUploadResult);
                }
                upload(file, fileExtension, picUploadResult);
                fis.close();
                file.delete();
            } catch (Exception e) {
                log.error("",e);
                picUploadResult.setSuccess(false);
                picUploadResult.setMsg("您上传的图片格式不正确，请上传JPG/JPEG/PNG格式文件！");
                return JSONObject.toJSONString(picUploadResult);
            }

        }
        return JSONObject.toJSONString(picUploadResult);

    }

    public PicUploadResult upload(File file, String fileExtension, PicUploadResult retJsonObj)
            throws Exception {
        // 获取图片实体
        // 构造HTTP请求
        // 设置参数
        String imageContentForBytes = getImageContentForBytes(file);
        JSONObject json = new JSONObject();
        json.put("fileName", file.getName());
        json.put("category", "member_head");
        json.put("content", imageContentForBytes);
        String param = "param=" + json.toString();
        // 请求
        URL url = new URL(uploadImgUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setRequestMethod("POST");
        connection.setReadTimeout(readTimeout);
        connection.setConnectTimeout(connectTimeout);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Content-type",
                "multipart/form-data;boundary=---------------------------7d318fd100112");
        PrintWriter dout = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), "utf-8"));
        dout.write(param);
        dout.flush();
        dout.close();
        // 响应
        StringBuffer sbf = new StringBuffer();
        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            sbf.append(line);
        }
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            JSONObject resultJson = JSONObject.parseObject(sbf.toString());
            String result = resultJson.getString("result");
            if (StringUtils.isNotBlank(result)) {
                retJsonObj.setSuccess(true);
                String[] split = downloadImgUrl.split(";");
                retJsonObj.setOriginalUrl(split[new Random().nextInt(split.length)] + result);
                retJsonObj.setThumbUrl(split[new Random().nextInt(split.length)] + result);
            }
            return retJsonObj;
        } else {
            throw new Exception("返回码:" + responseCode);
        }
    }

    public String getImageContentForBytes(File file) {
        // 读取本地文件
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            byte[] buffer = new byte[fileInputStream.available()];
            fileInputStream.read(buffer);
            out.write(buffer);
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Base64 encoder = new Base64();
        String str = new String(encoder.encode(out.toByteArray()));
        return str;
    }

    /**
     * 追加评价按钮显示
     */
    public String addCommentView(String ogSeq, String packagNo, String supplierType, String memGuid) {
        try {
            MultiValueMap<String, Object> params1 = new LinkedMultiValueMap<String, Object>();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("memGuid", memGuid);
            jsonObject.put("supplierType", supplierType);
            jsonObject.put("packageNo", packagNo);
            jsonObject.put("ogSeq", ogSeq);
            params1.add("params", jsonObject.toString());
            String commentInfo = restTemplate.postForObject(To_PACK_HASCOMMENT, params1, String.class);
            return commentInfo;
        } catch (Exception e) {
            log.error("追评按钮显示错误", e);
            return "{\"code\":\"505\"}";
        }

    }

    /**
     * 异步获取其他包裹信息并展示
     */
    public String orderView(String memGuid, String ogSeq, String packageNo, String supplierType) {
        try {
            MultiValueMap<String, Object> params1 = new LinkedMultiValueMap<String, Object>();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("memGuid", memGuid);
            jsonObject.put("supplierType", supplierType);
            jsonObject.put("packageNo", packageNo);
            jsonObject.put("ogSeq", ogSeq);
            params1.add("params", jsonObject.toString());
            String commentInfo = restTemplate.postForObject(To_PACk_COMMENT, params1, String.class);
            return commentInfo;
        } catch (Exception e) {
            log.error("异步获取其他包裹信息并展示错误", e);
            return "{\"code\":\"505\"}";
        }

    }

    /**
     * 整单评论提交
     * 
     */
    public String orderComment(String memGuid, String itemList, String ogSeq, Boolean isAnonymous,
                               String commentType, String shopList) {
        try {
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
            itemList = itemList.replace("&amp;quot;", "\"");
            shopList = shopList.replace("&amp;quot;", "\"");
            JSONObject json1 = new JSONObject();
            JSONArray items = JSONArray.parseArray(itemList);
            JSONArray itemsShop = JSONArray.parseArray(shopList);
            json1.put("itemList", items);
            if (items.size() > 0) {
                commentType = "1";
            }
            json1.put("shopList", itemsShop);
            if (isAnonymous) {
                json1.put("isAnonymous", "1");

            } else {
                json1.put("isAnonymous", "0");
            }

            json1.put("memGuid", memGuid);
            json1.put("ogSeq", ogSeq);
            json1.put("commentType", commentType);
            json1.put("commentSource", "p");
            params.add("params", json1.toString());
            String memHasCommentCount = restTemplate.postForObject(MY_COMMENT_ORDER, params, String.class);
            return memHasCommentCount;
        } catch (Exception e) {
            log.error("整单评论提交出错", e);
            return "{\"code\":\"505\"}";
        }
    }

    /**
     * 删除差评
     * 
     */
    public String deleteComment(String memGuid, String commentId) {

        try {
            MultiValueMap<String, Object> params1 = new LinkedMultiValueMap<String, Object>();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("memGuid", memGuid);
            jsonObject.put("commentId", commentId);
            params1.add("params", jsonObject.toString());

            String result = restTemplate.postForObject(deleteGoodsComment, params1, String.class);
            return result;
        } catch (Exception e) {
            log.error("删除差评提交出错", e);
            return "{\"code\":\"505\"}";
        }
    }

    /**
     * 评价内容校验
     */
    public String check(String context) {
        try {
            MultiValueMap<String, Object> params1 = new LinkedMultiValueMap<String, Object>();
            params1.add("sensitiveWord", context);
            String result = restTemplate.postForObject(isSensitiveWordListApi, params1, String.class);
            return result;
        } catch (Exception e) {
            log.error("评价内容校验提交出错", e);
            return "{\"code\":\"505\"}";
        }
    }

    /**
     * 获取差评原因
     * 
     */
    public String badReasons() {
        try {
            MultiValueMap<String, Object> params1 = new LinkedMultiValueMap<String, Object>();
            String result = restTemplate.postForObject(getBadCauseApi, params1, String.class);
            return result;
        } catch (Exception e) {
            log.error("获取差评原因提交出错", e);
            return "{\"code\":\"505\"}";
        }
    }

    /**
     * 包裹一键好评
     */
    public String addWholePackageComment(String memGuid, String ogSeq, String packageNo, String supplierType) {
        try {
            MultiValueMap<String, Object> paramsMap = new LinkedMultiValueMap<String, Object>();
            JSONObject params1 = new JSONObject();
            params1.put("memGuid", memGuid);
            params1.put("ogSeq", ogSeq);
            params1.put("supplierType", supplierType);
            params1.put("packageNo", packageNo);
            params1.put("commentSource", "p");
            paramsMap.add("params", params1.toString());

            String result = restTemplate.postForObject(addWholePackageCommentApi, paramsMap, String.class);
            return result;
        } catch (Exception e) {
            log.error("包裹一键好评信息出错", e);
            return "{\"code\":\"505\"}";
        }

    }

    /**
     * 整包裹评论
     */
    public String packageComment(String itemList, String memGuid) {
        try {
            MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
            JSONObject json1 = new JSONObject();
            itemList = itemList.replace("&amp;quot;", "\"");
            JSONArray itemLists = JSONObject.parseArray(itemList);
            json1.put("itemList", itemLists);
            json1.put("memGuid", memGuid);
            json1.put("commentSource", "p");
            params.add("params", json1.toString());
            String memHasCommentCount = restTemplate.postForObject(addGoodsCommentListApi, params,
                    String.class);
            return memHasCommentCount;
        } catch (Exception e) {
            log.error("包裹评论提交出错", e);
            return "{\"code\":\"505\"}";
        }
    }

    public String getUUID() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid;
    }

    
}
