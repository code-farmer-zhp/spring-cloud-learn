<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <meta name="format-detection" content="email=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="applicable-device" content="mobile">
    <meta name="format-detection" content="telephone=no">
    <script>if(/MSIE (6.0|7.0|8.0)/.test(navigator.userAgent)) { location.href = location.protocol + '${mUrl}/nonsupport.html'; }   </script>

    <title>意见反馈</title>
    <link rel="stylesheet" type="text/css" href="${mStaticUrl}/assets/css/common/common.css?v=${version}">
    <link rel="stylesheet" type="text/css" href="${mStaticUrl}/assets/css/my/feedback.css?v=${version}">
    <link rel="Shortcut Icon" type="images/x-icon" href="${mStaticUrl}/assets/images/favicon.ico">
</head>


<body>
<div class="outbox comments bgeee">
     <!-- 顶部 -->
    <div class="top_box">
        <span class="top_back J_topback"></span>
        <h1>意见反馈</h1>
        <!--<span class="more J_submit">提交</span>-->
    </div>
    <!--tab 切换-->
    <ul class="switch_tab J_lable">
        <!--切换控制 cur-->
        <li class="lable_tab tab_exper cur">体验问题</li>
        <li class="lable_tab tab_con">咨询投诉</li>
    </ul>
    <div class="feedback_tab">
        <div class="feedback_exper">
            <div class="feedback_type J_feedback_type">
                <p>反馈类型</p>
                <p class="feedback_type_desc">功能操作</p>
                <span class="iconfont">&#xe60b;</span>
            </div>
            <div class="experience">
                <textarea maxlength="500" placeholder="请说明一下客户端让你觉得不满，或者有待优化的内容，我们会尽快在新版本中改动，也感谢您对我们产品的支持！"></textarea>

                <div class="add_img" id="uploadphoto">
                    <p class="button_add_img">
                        <em class="iconfont">&#xe61f;</em><span class="J_add_img_desc">点击添加图片</span>
                        <input type="file" accept="image/jpg,image/jpeg,image/png,image/gif" id="photo">
                    </p>
                    <span class="character_remain">还可以输入<em>500</em>字</span>
                </div>
                <div class="figure clearfix">
                </div>
            </div>
            <div class="contact_info">
                <input type="text" maxlength="50" name="contact_info" placeholder="请留下您的手机号、邮箱或其它联系方式（选填）" />
            </div>
            <!-- 灰色按钮 -->
            <div class="button hide_it"><a href="#" class="btn btn_de btn_lg">提交</a></div>
            <!-- 红色按钮 -->
            <div class="button "><a href="#" class="btn btn_lg btn_de J_submit">提交</a></div>
        </div>
        <div class="feedback_con hide_it">
            <div class="tousu">
                <i class="iconfont">&#xe63d;</i>
                    <span>
                        <img src="${mStaticUrl}/assets/images/my/samall_horn.png">
                        商品/商家投诉,请于9:00~22:00联系客服
                    </span>
            </div>
            <div class="tel_phone J_tel">
                <div class="tel_place">
                    <i class="iconfont">&#xe607;</i><span>400-920-6565</span>
                </div>
            </div>
            <div class="contact">
                您还可以通过邮箱cs@feiniu.com随时与我们联系
            </div>
        </div>
    </div>
</div>
<div class="popbg J_feedback_type_box hide_it">
    <div class="feedback_type_box">
        <h3>请选择反馈类型</h3>
         <%@include file="feedbacktype.jsp"%>
    </div>
</div>
<!-- 页面遮罩部分 -->
<!-- <p class="loading_pop all" data-load-name="v2"><b></b></p> -->
<!-- 底部导航 -->
<div></div>
<!-- uniclickTracking埋点 -->
${footHtml}
</body>
</html>
<%@include file="../common/touch/script.jsp"%>
<script>
    require(["${mStaticUrl}/assets/js/config.js?v=${version}"], function() {
         require(['${mStaticUrl}/assets/js/controller/member/feedback.js?v=${version}']);
    });
</script>
