
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
    <script>if(/MSIE (6.0|7.0|8.0)/.test(navigator.userAgent)) { location.href = location.protocol + '//m.feiniu.com/nonsupport.html'; }</script>
    <title>飞牛网-评论</title>
    <link rel="stylesheet" type="text/css" href="${mStaticUrl}/assets/css/common/common.css">
    <link rel="stylesheet" type="text/css" href="${mStaticUrl}/assets/css/my/comment/index.css">
    <link rel="Shortcut Icon" type="images/x-icon" href="${mStaticUrl}/assets/images/favicon.ico">
</head>
<body>
    <div class="outbox mycomment">
    <!-- 顶部 -->
    <div class="top_box">
        <span class="top_back J_topback"></span>
        <h1>我的评论</h1>
        <a href="${mUrl}/about/comment_desc.html" class="iconfont">&#xe669;</a>
    </div>
    <ul class="tabs J_tabs">
        <li  class="<c:if test="${pageType==0}">active</c:if>" id="J_dai"><span id="no_num" num="${memHasNoCommentCount}">待评论(${memHasNoCommentCount})</span></li>
        <li class="<c:if test="${pageType==1}">active</c:if>" id="J_over"><span id="has_num" class="dot" num="${memHasCommentCount}">已评论(${memHasCommentCount})</span></li>
    </ul>
    <!-- 待评论 -->
    <div class="order_list no_comment <c:if test="${pageType==1}">hide</c:if>">
     <c:if test='${not empty goodList}'>
        <div class="order_list_o" id="has_no_comment">
         <c:forEach items="${goodList}" var="v">
            <div class="hr1"></div>
            <ul class="order_box">
                <li class="order_id">
                    <p class="shop_info">
                        <img src="${v.icon}" class="shop_icon"/>
                        <span class="shop_name" data-shopid="${shop_id}">${v.shop_name}</span>
                        <c:if test="${v.orderComment == 1}">
                        <a href="${v.order_comment_href}" class="order-btn whole-order-comment-btn" data-orderid="${v.order_id}">整单评论</a>
						</c:if>
                        <a href="${v.order_detail_href}" class="order-btn order-detail-btn" data-orderid="${v.order_real_id}" data-packageid="${v.package_id}">订单详情</a>
                    </p>
                </li>
                <c:forEach items="${v.goods}" var="item">
                <li class="item J_no_item" data-sm_seq="${item.sm_seq}" onclick="location='${item.touch_item_href}'">
                    <div class="item_img">
                        <img src="${item.img}" />
                    </div>
                    <div>${item.title}</div>
                    <div class="order_p J_add" data-addid="${item.add_id}" data-goodsid="${item.goods_id}"><a href="javascript:;" data-href="${item.touch_comment_href}" class="order_p_t" data-ogno="${v.order_id}">发表评论</a></div>
                </li>
                </c:forEach>
            </ul>
          </c:forEach>
          <script id="tpl_listno" type="text/x-dot-template">
			 {{~it:value:index}}
					<div class="hr1"></div>
					<ul class="order_box">
						<li class="order_id">
							<p class="shop_info">
								<img src="{{= value.icon}}" class="shop_icon"/>
								<span class="shop_name" data-shopid="{{= value.shop_id}}">{{= value.shop_name}}</span>
                                 {{? value.orderComment == 1}}
							     <a href="{{= value.order_comment_href}}" class="order-btn whole-order-comment-btn" data-orderid="{{= value.order_id}}">整单评论</a>
                                 {{?}}
								<a href="{{= value.order_detail_href}}" class="order-btn order-detail-btn" data-orderid="{{= value.order_real_id}}" data-packageid="{{= value.package_id}}">订单详情</a>
							</p>
						</li>
                        {{~value.goods:goodvalue:goodindex}}
						<li class="item J_no_item" data-sm_seq="{{=goodvalue.sm_seq}}" onclick="location='{{=goodvalue.touch_item_href}}'">
							<div class="item_img">
								<img src="{{=goodvalue.img}}" />
							</div>
							<div>{{=goodvalue.title}}</div>
							<div class="order_p J_add " data-addid="{{=goodvalue.add_id}}" data-goodsid="{{=goodvalue.goods_id}}"><a href="javascript:;" data-href="{{= goodvalue.touch_comment_href}}" class="order_p_t" data-ogno="${goodvalue.order_id}">发表评论</a></div>
						 </li>
					    {{~}}
					</ul>
			   {{~}}
			</script>

            <script id="tpl_listno_prepending" type="text/x-dot-template">

                {{~it.goods:goodvalue:goodindex}}
                <li class="item J_no_item" data-sm_seq="{{=goodvalue.sm_seq}}" onclick="location='{{=goodvalue.touch_item_href}}'">
                    <div class="item_img">
                        <img src="{{=goodvalue.img}}" />
                    </div>
                    <div>{{=goodvalue.title}}</div>
                    <div class="order_p J_add " data-addid="{{=goodvalue.add_id}}" data-goodsid="{{=goodvalue.goods_id}}"><a href="javascript:;" data-href="{{= goodvalue.touch_comment_href}}" class="order_p_t" data-ogno="${goodvalue.order_id}">发表评论</a></div>
                </li>

                {{~}}
            </script>

        </div>
        </c:if>
        <!-- 无评论内容内容显示 -->
        <c:if test="${empty goodList}">
        <div id="blank_no" class="default">
            <img src="${mStaticUrl}/assets/images/my/comment/fn.png">
            <div class="marg_tb60">
                <p>亲，还没有待评论内容~</p>
                <p>买一个再来看看</p>
            </div>
            <div>
                <a href="${mSeckillUrl}" class="btn btn_red">逛逛秒杀</a>
            </div>
        </div>
        </c:if>
        <!-- 待评论页面滚动时加载样式 -->
        <p class="loading_btm noMore_no hide_it"><i>没有更多数据</i></p>
    </div>
    <!-- 已评论 -->
    <div class="order_list al_comment <c:if test="${pageType==0}">hide</c:if>">
        <div class="hr1"></div>
        <ul class="order_li" id="has_comment">
        <script id="tpl_mylist" type="text/x-dot-template">
           {{~it:value:index}}
            <li class="order_box">
                    <div class="item item_b"  onclick="location='{{=value.touch_item_href}}'">
                        <div class="item_img">
                            <img src="{{= value.img}}" />
                        </div>
                        <div>{{=value.title}}</div>
                        <div class="order_p">
                            <p class="star">
                               {{ for( i=0;i<5;i++){  }}
                               {{? i < value.star}}
	                            <span class="cur"></span>
                               {{??}}
                               <span></span>
                               {{?}}
                               {{ } }}
                            </p>
                        </div>
                    </div>
                   {{? value.impression.length !== 0 && value.impression[0] !== ''}}
                    <div class="item_impress">
                        <p class="percentage">印象:</p>
                        <p class="type clearfix">
                            {{ for(var prop in value.impression) { }}
                                {{? value.impression[prop]!=''}}
									<span>{{=value.impression[prop]}}</span>
								{{?}}
                            {{ } }}
                        </p>
                    </div>
                   {{?}}
                    <div class="experience">
                        <p>{{=value.my_comment}}</p>
                        <p>评论时间：{{=value.time}}</p>
                        <div class="btn-wrap">
                            <!--1:追加评论或 0:已追评-->
	                        {{? value.is_show_append_button == 1}}
								<a href="{{=value.readd_comment_url}}" class="btns btns-add">
									<em class="iconfont">&#xe61f;</em>追加评论
								</a>
							{{?? value.is_show_append_button == 0}}
								<a href="javascript: void(0)" class="btns btns-gray">已追评</a>
							{{??}}

                            {{?}}
                            <!--删除评论-->
	                        {{? value.can_del_comment == 1}}
							<a href="javascript: void(0)" class="btns btns-mix J_del" data-commentid="{{=value.id}}" data-star="{{=value.star}}">删除评论</a>
							{{?}}
                        </div>
                    </div>
	                {{? value.append_comment != "" }}
						<div class="reply"><span>【追评】</span>{{=value.append_comment}}</div>
					{{?}}
					{{? value.service_comment != "" }}
						<div class="reply"><span>【客服回复】</span>{{=value.service_comment}}</div>
					{{?}}
                </li>
              {{~}}
	    </script>
        </ul>
        <!-- 无已评论内容 -->
        <div id="blank_has" class="default hide_it">
            <img src="${mStaticUrl}/assets/images/my/comment/fn.png">
            <div class="marg_tb60">
                <p>亲，还没有已评论内容~</p>
                <p>买一个再来看看</p>
            </div>
            <div>
                <a href="${mSeckillUrl}" class="btn btn_red">逛逛秒杀</a>
            </div>
        </div>
        <!-- 已评论页面滚动时加载样式 -->
        <div class="nomoregoods hide_it">
            <div class="niuniu"><img src="${mStaticUrl}/assets/images/my/niuniu.png"></div>
            <p>亲，没有更多了哦~</p>
        </div>
    </div>
    <!-- 待评论和已评论页面滚动时加载样式 -->
    <p class="loading_btm loadingBtm hide_it"><b></b><i>正在努力加载...</i></p>
</div>
	<jsp:include page="../common/touch/script.jsp" />
    <script>
        var pageType="${pageType}";
        require([static_domain+"/assets/js/config.js?v="+time_stamp], function() {
            require(['controller/member/comment']);
        });
    </script>
</body>
</html>