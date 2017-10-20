<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
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
    <title>飞牛网-发表评论</title>
    <link rel="stylesheet" type="text/css" href="${mStaticUrl}/assets/css/common/common.css">
    <link rel="stylesheet" type="text/css" href="${mStaticUrl}/assets/css/my/comment/comment_add.css">
    <link rel="Shortcut Icon" type="images/x-icon" href="${mStaticUrl}/assets/images/favicon.ico">
</head>
<body>
<div class="outbox comments bgeee">
    <!-- 顶部 -->
    <div class="top_box">
        <span class="top_back J_commentTopBack"></span>
        <h1>发表评论</h1>
    </div>
    <div class="order_box">
    <!-- 商品维度评论显示 -->
    <c:if test="${not empty star}" >
            <div class="order_list order_list_one" data-orderdetailid="${order_detail_id}">
                <div class="item">
                    <div class="item_img">
                        <img src="${img}" />
                    </div>
                    <div style="display: none"></div>
                    <div class="item_impress">
                        <p class="evaluate">评   价</p>
                        <p class="star J_Comment_Star" data-sortid='0'>
                            <c:forEach var="s"  begin="1" end="5">
                            <c:if test="${s <= star}">
                            <span class="cur"></span>
                            </c:if>
                            <c:if test="${s > star}">
                            <span></span>
                            </c:if>
							</c:forEach>
                        </p>
                    </div>
                </div>

                <div class="reason-row hide_it">
                    <p class="hd">牛牛哪里没有服务到位，让您不满意了呢？</p>
                    <div class="choose_reason clearfix">
                        <p class="percentage flex-middle">选择原因</p>
                        <div class="type select" data-reasonid = '0'  reasonid='0'>
                            <p class="txt" >请选择差评原因</p>
                            <i class="iconfont">&#xe621;</i>
                        </div>
                    </div>
                </div>

                <div class="item_impress clearfix">
                    <p class="percentage">印象</p>
                    <p class="type clearfix J_Impress" >
                         <c:forEach items="${impression}" var="im" varStatus="status">
                         <c:if test="${status.count == 1}">
                         <span class="cur">${im}</span>
                         </c:if>
                         <c:if test="${status.count != 1}">
                         <span>${im}</span>
                         </c:if>
                         </c:forEach>
                    </p>
                </div>
                <div class="item_impress clearfix">
                    <p class="percentage">体会</p>
                    <div class="experience">
                        <textarea class="txtComment" placeholder="用力吱一声来表达你的赞美或吐槽（1个字也是种爱）"></textarea>
                        <i class="mc3">至少输入1个字，您还可以输入<span class="mc1 txtNum">500</span>个字</i>
                        <div class="figure clearfix hide"></div>
                        <!-- 红色按钮 -->
                        <a href="javascript: void(0)" class="order_p_t"><span class="needsclick"><em class="iconfont">&#xe61f;</em>添加晒单图片</span></a>
                        <input type="file" name="uploadFile" accept="image/jpg,image/jpeg,image/png,image/gif" class="photo hide"/>
                    </div>
                </div>
            </div>
         <c:if test="${not empty shop}" >
            <div id="J_Shop" data_shopid="${shop_id}" data_packageid="${package_id}" data_orderid="${order_id}">
                <div class="order_list order_list_o">
                    <h2>店铺评价</h2>
                    <div class="item">
                        <div class="item_img">
                            <img src="${shop.img}" />
                        </div>
                        <div>
                            <p class="shop">${shop.name}</p>
                        </div>
                    </div>
                    <div class="item_impress clearfix">
                        <p class="percentage">商品描述</p>
                        <p class="star J_Goods_Star">
                             <c:forEach var="s"  begin="1" end="5">
                             <c:if test="${s <= shop.goods_star}">
                             <span class="cur"></span>
                             </c:if>
                             <c:if test="${s > shop.goods_star}">
                             <span></span>
                             </c:if>
							 </c:forEach>
                        </p>
                    </div>
                    <div class="item_impress clearfix">
                        <p class="percentage">服务态度</p>
                        <p class="star J_Service_Star">
                             <c:forEach var="s"  begin="1" end="5">
                             <c:if test="${s <= shop.service_star}">
                             <span class="cur"></span>
                             </c:if>
                             <c:if test="${s > shop.service_star}">
                             <span></span>
                             </c:if>
							 </c:forEach>
                        </p>
                    </div>
                    <div class="item_impress clearfix">
                        <p class="percentage">物流速度</p>
                        <p class="star J_Speed_Star">
                             <c:forEach var="s"  begin="1" end="5">
                             <c:if test="${s <= shop.speed_star}">
                             <span class="cur"></span>
                             </c:if>
                             <c:if test="${s > shop.speed_star}">
                             <span></span>
                             </c:if>
							 </c:forEach>
                        </p>
                    </div>
                </div>
            </div>
            </c:if>
        <!-- 固定底栏 灰色按钮 -->
	     <div class="fixed-nav" >
	        <div class="anonymity-left">
	            <p class="anonymity"><input class="com_checkbox" id="J_Anonymity" type="checkbox" /><label for="J_Anonymity">匿名评价</label></p>
	        </div>
	        <div class="btnAdd-right">
	            <span id="btnAdd" class="btn btn_de btn_lg">发表评论</span>
	        </div>
	     </div>
    </c:if>
    <!-- 订单维度评论显示 -->
    <c:if test='${not empty packages}'>
      <c:forEach items="${packages}" var="package2"  varStatus="status">
      <div class="every_package" data-packageid="${package2.package_id}">
                <div class="order-shop">
                    <img src="${package2.shop_def_img}" class="shop_icon"/>
                    <span class="shop_name">${package2.shop_name}</span>
                </div>
                <div class="allOrderComment"></div>
                <c:forEach items="${package2.goods}" var="good">
                <div class="order_list order_list_two" data-orderdetailid="${good.order_detail_id}">
                    <div class="item">
                        <div class="item_img">
                            <img src="${good.img}" />
                        </div>
                        <div style="display: none"></div>
                        <div class="item_impress">
                            <p class="evaluate">评 价</p>
                            <p class="star J_Comment_Star" data-sortid='${status.count}'>
                            <c:forEach var="s"  begin="1" end="5">
                            <c:if test="${s <= good.star}">
                            <span class="cur"></span>
                            </c:if>
                            <c:if test="${s > good.star}">
                            <span></span>
                            </c:if>
							</c:forEach>
                            </p>
                        </div>
                    </div>
                    <div class="reason-row hide_it">
                        <p class="hd">牛牛哪里没有服务到位，让您不满意了呢？</p>
                        <div class="choose_reason clearfix">
                            <p class="percentage flex-middle">选择原因</p>
                            <div class="type select" data-reasonid = '${status.count}'>
                                <p class="txt">请选择差评原因</p>
                                <i class="iconfont">&#xe621;</i>
                            </div>
                        </div>
                    </div>
                    <div class="item_impress clearfix">
                        <p class="percentage">印象</p>
                        <p class="type clearfix J_Impress">
                         <c:forEach items="${good.impression}" var="im" varStatus="status">
                         <c:if test="${status.count == 1}">
                         <span class="cur">${im}</span>
                         </c:if>
                         <c:if test="${status.count != 1}">
                         <span>${im}</span>
                         </c:if>
                         </c:forEach>
                        </p>
                    </div>
                    <div class="item_impress clearfix">
                        <p class="percentage">体会</p>
                        <div class="experience">
                            <textarea class="txtComment" placeholder="${good.comment}">${good.comment}</textarea>
                            <i class="mc3">至少输入1个字，您还可以输入
                                <span class="mc1 txtNum">${500 - fn:length(good.comment)}</span>
                                个字
                            </i>
                            <div class="figure clearfix hide"></div>
                            <!-- 红色按钮 -->
                            <a href="javascript: void(0)" class="order_p_t"><span class="needsclick"><em class="iconfont">&#xe61f;</em>添加晒单图片</span></a>
                            <input type="file" name="uploadFile" accept="image/jpg,image/jpeg,image/png,image/gif"   class="photo hide" />
                        </div>
                    </div>
                </div>
               	<div class="hr1"></div>
                </c:forEach>
                <c:if test='${not empty package2.shop}'>
                <div class="J_Shop" data-shopid="${package2.shop.id}"  data_packageid="${package2.shop.package_id}">
                    <div class="order_list order_list_o">
                        <h2>店铺评价</h2>
                        <div class="item">
                            <div class="item_img">
                                <img src="${package2.shop.img}" />
                            </div>
                            <div>
                                <p class="shop">${package2.shop.name}</p>
                            </div>
                        </div>
                        <div class="item_impress clearfix">
                            <p class="percentage">商品描述</p>
                            <p class="star J_Goods_Star" data-goodsstar="${package2.shop.goods_star}">
                             <c:forEach var="s"  begin="1" end="5">
                             <c:if test="${s <= package2.shop.goods_star}">
                             <span class="cur"></span>
                             </c:if>
                             <c:if test="${s > package2.shop.goods_star}">
                             <span></span>
                             </c:if>
							 </c:forEach>
                            </p>
                        </div>
                        <div class="item_impress clearfix">
                            <p class="percentage">服务态度</p>
                            <p class="star J_Service_Star" data-servicestar="${package2.shop.service_star}">
                             <c:forEach var="s"  begin="1" end="5">
                             <c:if test="${s <= package2.shop.service_star}">
                             <span class="cur"></span>
                             </c:if>
                             <c:if test="${s > package2.shop.service_star}">
                             <span></span>
                             </c:if>
							 </c:forEach>
                            </p>
                        </div>
                        <div class="item_impress clearfix">
                            <p class="percentage">物流速度</p>
                            <p class="star J_Speed_Star" data-speedstar="${package2.shop.speed_star}">
                              <c:forEach var="s"  begin="1" end="5">
                             <c:if test="${s <= package2.shop.speed_star}">
                             <span class="cur"></span>
                             </c:if>
                             <c:if test="${s > package2.shop.speed_star}">
                             <span></span>
                             </c:if>
							 </c:forEach>
                            </p>
                        </div>
                    </div>
                </div>
                <div class="hr1"></div>
	            </c:if>
       </div>
       </c:forEach>
	    <!-- 固定底栏 灰色按钮 -->
	   <div class="fixed-nav" data-orderid="${order_id}">
	      <div class="anonymity-left">
	            <p class="anonymity"><input class="com_checkbox" id="J_Anonymity" type="checkbox" /><label for="J_Anonymity">匿名评价</label></p>
	      </div>
	      <div class="btnAdd-right">
	            <span id="orderAdd" class="btn btn_red btn_lg">发表评论</span>
	      </div>
	   </div>
    </c:if>
    </div>
    <div class="reason-shade hide_it">
			<i class="mask"></i>
			<div class="reason-select">
				<dl>
					<dt>差评原因</dt>
					<c:forEach items="${bad_reasons}" var="reason" varStatus="status">
					<dd>
						<input type="radio" id="reason_${reason.id}" name="reason" 
						 <c:if test="${status.count == 1}">
                         checked
                         </c:if>
						data-reasonid="${reason.id}" data-causeText="${reason.causeText}"/>
						<i class="copy-box"></i>
						<label for="reason_${reason.id}">${reason.causeText}</label>
					</dd>
					</c:forEach>
				</dl>
				<a href="javascript:void(0);" class="close">确定</a>
			</div>
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