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

    <title>积分</title>
    <link rel="stylesheet" type="text/css" href="${mStaticUrl}/assets/css/common/common.css?v=${version}">
    <link rel="stylesheet" type="text/css" href="${mStaticUrl}/assets/css/my/point.css?v=${version}">
    <link rel="Shortcut Icon" type="images/x-icon" href="${mStaticUrl}/assets/images/favicon.ico">
    <style>
    .record-list li .l .title_p {
        font-size: .28rem;
        color: #333;
        margin-bottom: .2rem;
    }
    </style>
</head>
<body>
    <div class="fm-container">
        <div class="header-area top_box">
            <header class="header">
                <div class="left back" onclick="javascript:history.go(-1)">
                </div>
                <div class="center title"><h1>积分</h1></div>
                <a href="javascript:void(0);" class="iconfont top-navigation">&#xe684;</a>
            </header>
        </div>

        <div class="body-area">
            <div class="body-area-header">
                <div class="state-info">
                    <p class="point-rule">
                        <a href="${mUrl}/about/jifen.html" style="color:white;font-size: .24rem;" >积分规则<span></span></a><!--<i class="my-icon">&#xe60b;</i>-->
                    </p>
                    <div class="bar bar1">
                        <div class="point_img">
                            <img src="${mStaticUrl}/assets/images/my/point.png">
                        </div>
                        <div class="point-detail">
                            <p style='font-size:.28rem'>可用积分</p>
                            <p id="canUseScore" class="point-num">${allScore}</p>
                            <div class="point-info">
                                <p>待生效积分<span style='font-size:.24rem'>${waitScore}</span></p>
                                <p>${expireScore}积分将于${year}年过期</p>
                            </div>
                        </div>
                    </div>
                    <div class="bar bar2 hide_it">
                        <div>可用积分 <span id="canUseScore2">${allScore}</span></div>
                        <div style="border-right:1px solid white"><img class="my-icon get-score" src="${mStaticUrl}/assets/images/my/icon_earnScore_2x.png"/><a class="get-score-a" href="${weixinUrl}/weixin/signin/index.html">赚积分</a></div>
                        <div><img class="my-icon use-score" src="${mStaticUrl}/assets/images/my/icon_useScore_2x.png"/><a class="use-score-a" href="${mVipUrl}/touch/scoreShop.html">花积分</a></div>
                    </div>
                </div>

                <ul class="operation">
                    <li>
                        <img class="my-icon get-score" src="${mStaticUrl}/assets/images/my/icon_earnScore.png"/><span><a class="get-score-a" href="${weixinUrl}/weixin/signin/index.html" data-href="" >赚积分</a></span>
                    </li>
                    <li>
                        <img class="my-icon use-score" src="${mStaticUrl}/assets/images/my/icon_useScore.png"/><span><a class="use-score-a" href="${mVipUrl}/touch/scoreShop.html" data-href="">花积分</a></span>
                    </li>
                </ul>
                <ul class="ui-tab clearfix" id="J_il_nav">
                    <li class="cur" data-list="1">全部</li>
                    <li data-list="2">收入</li>
                    <li data-list="3">支出</li>
                </ul>
            </div>
            <div class="tab-display">
                <ul class="il_list record-list all_score" id="J_il_list_1">
                    <script id="tpl_list_1"  type="text/x-dot-template">
                        {{~it.pointList :value:index}}
                            {{? value.score_type== 1}}
                                <li class="list_add J_add">
                            {{?? value.score_type == 2}}
                                <li class="list_remove J_remove">
                            {{?}}
                                <div class="l">
                                    <p class='title_p'>{{=value.descr}}</p>
                                    <p class="order-num" {{? !value.orderStr}} style="visibility:hidden;"{{?}}>
                                    订单号 ： {{=value.orderStr}}
                                    </p>
                                </div>
                                <div class="r">
                                    <p class="time">{{=value.time}}</p>
                                    <span class="money-num minus {{? value.score_type == 1}} wait-minus {{?}}">
                                        {{? value.score_type ==  1}}+{{??}}-{{?}}{{=value.score}}
                                    </span>
                                </div>
                            </li>
                        {{~}}
                    </script>
                </ul>
                <ul class="il_list record-list wait_score hide_it" id="J_il_list_2">
                    <script id="tpl_list_2"  type="text/x-dot-template">
                        {{~it.pointList :value:index}}
                        <li class="list_add J_add">
                            <div class="l">
                                <p class='title_p'>{{=value.descr}}</p>
                                <p class="order-num" {{? !value.orderStr}} style="visibility:hidden;"{{?}}>
                                订单号 ： {{=value.orderStr}}
                                </p>
                            </div>
                            <div class="r">
                                <p class="time">{{=value.time}}</p>
                                <span class="money-num minus wait-minus">+{{=value.score}}</span>
                            </div>
                        </li>
                        {{~}}
                    </script>
                </ul>
                <ul class="il_list record-list expire_score hide_it" id="J_il_list_3">
                    <script id="tpl_list_3"  type="text/x-dot-template">
                        {{~it.pointList :value:index}}
                        <li class="list_remove J_remove">

                            <div class="l">
                                <p class='title_p'>{{=value.descr}}</p>
                                <p class="order-num" {{? !value.orderStr}} style="visibility:hidden;"{{?}}>
                                订单号 ： {{=value.orderStr}}
                                </p>
                            </div>
                            <div class="r">
                                <p class="time">{{=value.time}}</p>
                                <span class="money-num minus">-{{=value.score}}</span>
                            </div>
                        </li>
                        {{~}}
                    </script>
                </ul>

                <div class="load_more" style="display:block;" href="####">
                    <p><i class="spin"></i><span>&nbsp;&nbsp;正在加载中...<span></p>
                </div>
                <div class="nomoregoods">
                    <div class="niuniu"><img src="${mStaticUrl}/assets/images/niuniu.png"></div>
               		<p>亲，没有更多了</p>
               	</div>
                <div class="blank hide_it" id="blank">
                    <img src="${mStaticUrl}/assets/images/default/blank_icon.png" class="blank_img" />
                    <div class="marg_tb60"></div>
                    <div class="btnout marg_lr20 padd_top30">
                        <a href="${mUrl}/" class="jbl_com jbl_oran wbd">去逛逛</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <footer>
        <p class="vpage"><a href="${mUrl}" class="cur">触屏版</a><a href="javascript:;" onclick="gopc();">电脑版</a>
        </p>
        <p class="copyright">Copyright © 2013-2016 飞牛网,ALL Rights Reserved.<br>沪ICP备13025776号 营业执照</p>
    </footer>
<script>
        //跳转到电脑版及埋点
        function gopc() {
            //如果访问PC版，种pch5_jump='pc'，防止死循环跳回触屏版
            var date = new Date();
            var expireDays = 1;
            date.setTime(date.getTime() + expireDays * 24 * 3600 * 1000);
            document.cookie = "pch5_jump=pc;path=/;domain=${cookiePath};expires=" + date.toGMTString();
            window.location.href = "${wwwUrl}/?from=h5";
        }
</script>
<%@include file="../../common/touch/script.jsp"%>
</body>
<script>
    require(['${mStaticUrl}/assets/js/config.js?v=${version}'], function () {
        require(['${mStaticUrl}/assets/js/controller/member/point.js?v=${version}']);
    });
</script>
</html>
