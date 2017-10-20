<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
    <script>if(/MSIE (6.0|7.0|8.0)/.test(navigator.userAgent)) { location.href = location.protocol + '//m.feiniu.com/nonsupport.html'; }   </script>
    <title>飞牛网-添加评论</title>
    <link rel="stylesheet" type="text/css" href="${mStaticUrl}/assets/css/common/common.css">
    <link rel="stylesheet" type="text/css" href="${mStaticUrl}/assets/css/my/comment/comment_add.css">
    <link rel="Shortcut Icon" type="images/x-icon" href="${mStaticUrl}/assets/images/favicon.ico">
</head>
<body>
    <div class="outbox comments bgeee">
    <!-- 顶部 -->
    <div class="top_box">
        <span class="top_back J_topback"></span>
        <h1>发表追评</h1>
    </div>
    <div class="order_box" data-commentid="${comment_id}">
        <div class="order_list">
                <div class="item">
                    <div class="item_img">
                        <img src="${img}" />
                    </div>
                    <div>
                        <p>
                            <img src='${tag.rlink}' alt=''>${title}</p>
                        <p>¥${price}</p>
                    </div>
                </div>
                <div class="item_impress clearfix">
                    <p class="percentage">体会</p>
                    <div class="experience">
                        <textarea id="txtComment" placeholder="用力吱一声来表达你的赞美或吐槽（1个字也是种爱）"></textarea>
                        <i class="mc3">至少输入1个字，您还可以输入<span id="txtNum" class="mc1">500</span>个字</i>
                        <div class="figure clearfix hide"></div>
                        <!-- 红色按钮 -->
                        <a href="javascript: void(0)" class="order_p_t"><span class="needsclick"><em class="iconfont">&#xe61f;</em>添加晒单图片</span></a>
                        <input type="file" name="uploadFile" accept="image/jpg,image/jpeg,image/png,image/gif" class="photo hide"/>
                    </div>
                </div>
        </div>
    </div>
    <!-- 灰色按钮 -->
    <div class="fixed-nav after-review" >
        <span id="readdAdd" class="btn btn_de btn_lg">发表评论</span>
    </div>
</div>
    <jsp:include page="../common/touch/script.jsp" />
    <script>
    require([static_domain+"/assets/js/config.js?v="+time_stamp], function() {
        require(['controller/member/comment']);
    });
    </script>
</body>
</html>
