<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/picUrl.tld" prefix="picUrl"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>我的评价</title>
	<link rel="shortcut icon" href="${staticDomain}/images/feiniu_favicon.ico" />
	<link rel="stylesheet" type="text/css" href="${staticDomain}/product/css_build/common.css?v=${version}"/>
	<link rel="stylesheet" type="text/css" href="${staticDomain}/product/css_build/member/myrating.css?v=${version}"/>
	<link rel="stylesheet" href="${staticDomain}/js/lib/artdialog/skins/default.css?v=${version}">
	<link rel="stylesheet" href="${staticDomain}/js/lib/artdialog/skins/art_skin_order.css?v=${version}">
    <link rel="stylesheet" href="${staticDomain}/product/css_build/member/order.css?v=${version}"></link>
    <link rel="stylesheet" type="text/css" href="${staticDomain}/product/css_build/member/mycomments.css?v=${version}"/>
</head>
<body>
${headerHtml}
<!---导航-->
${navHeaderHtml}
	<div class="g-container">
		<!-- bread crumbs star -->
		 <div class="g-crumbs">
            <span><a href="${memberUrl}/member/home" class="target_no">我的飞牛</a></span>&gt;<span class="color">我的评价</span>
        </div>
		<!-- bread crumbs end -->
		<div class="g-wrapper fixed">
		<!-- sideBar nav star -->
		${leftHtml}
		<!-- sideBar nav end -->
		<!-- col main star -->
		<div class="m-right">
			<div class="main evaluation">
				<!-- themes star -->
				<div class="themes_title">
					<h3>我的评价</h3>
				</div>
				<!-- themes end -->	
				<div class="ui_tab">
					<ul class="ui_tab_nav">
						<li class="active"><a class="target_no" href="${basePath}comment/myCommentView">待评论订单</a></li>
						
						<li class="last"><a class="target_no" href="${basePath}comment/hasCommentView">已评论<span class="num">(${memHasCommentCount})</span></a></li>
					</ul>
					<div class="ui_tab_content">
						<div class="ui_panel" id="noComment" style="display:block;">
					      <c:if test='${not empty orderList}'>
				      		<div class="evaluation_cont">
				      			<div class="evaluation_title">
				      				<ul class="clearfix">
				      					<li class="col_w60 th_01">商品信息</li>
				      					<li class="col_w20 th_03"><span>操作</span></li>
				      				</ul>
				      			</div>
				      			<div class="order_list J_order_list" > 
		                        			<c:forEach items="${orderList}" var="order">
		                        				<table class="">
		                        					<tr class="list_top">
														<td colspan="2">
															<div class="f_left order_info">
																<span class="time num">${order.orderDate}</span>
																<span class="order_No">订单号：<small class="num">${order.ogNo}</small><c:if test="${order.isVirtual == 1}">(虚拟商品)</c:if></span>	
															</div>
														</td>
													</tr>
													<c:if test="${fn:length(order.packages) > 0}">
														<c:forEach items="${order.packages}" var="packageOrder">
															<tr class="list_cont">
																<td class="td_01">
																	<div class='list_title <c:if test="${packageOrder.supplierType != 1}">chat-panel</c:if>' >
					                                            		<c:if test="${fn:length(order.packages) > 1}"><span class="package_number"><span style='font-size: 13px;font-weight: 700;'>
					                                            		包裹 ${packageOrder.packOrder}
					                                            		</span></span></c:if>
					                                            		<c:choose>
																			<c:when test="${packageOrder.supplierType == 1}">
																			<c:if test="${packageOrder.packType == 0}">
																				<a href="${wwwUrl}"  class="shop_name" title="商家直送-${packageOrder.brand}">商家直送-${packageOrder.brand}</a>
																			</c:if>
																			<c:if test="${packageOrder.packType != 0}">
																				<a href="${wwwUrl}"  class="shop_name" title="飞牛配送">飞牛配送</a>
																			</c:if>
																			</c:when>
																			<c:otherwise>
																			<c:if test="${packageOrder.overseasMode == 1}">
																				<a  href="${storefrontUrl}${packageOrder.vondorSeq}.html"
																					class="shop_name"  title="[${packageOrder.overseasCustoms}]${packageOrder.vondorName}">[${packageOrder.overseasCustoms}]${packageOrder.vondorName}</a>
																			</c:if>
																			<c:if test="${packageOrder.overseasMode != 1}">
																				<a  <c:choose>
																						<c:when test="${order.isVirtual == 1}">
																						</c:when>
																						<c:otherwise>
																							href="${storefrontUrl}${packageOrder.vondorSeq}.html"
																						</c:otherwise>
																					</c:choose>
																					class="shop_name"  title="${packageOrder.vondorName}">${packageOrder.vondorName}</a>
																			</c:if>
																			</c:otherwise>
																		</c:choose>
							                                        </div>
							                                        
							                                        <c:if test="${fn:length(packageOrder.itemList) > 0}">
							                                        	<div class="list_main clearfix">
							                                        	<c:forEach items="${packageOrder.itemList}" var="orderItem">
																			<c:set var="isService"  value="${(packageOrder.supplierType == 1 and orderItem.kind==15) or(packageOrder.supplierType == 2 and orderItem.kind==5)}"/>
																			<c:choose>
																				<c:when test="${isService}">
																					<c:set var="href" value="http://sale.feiniu.com/activity-532583782501710.html"></c:set>
																				</c:when>
																				<c:otherwise>
																					<c:set var="href" value="${storeDomainUrl}/${orderItem.skuSeq}"></c:set>
																				</c:otherwise>
																			</c:choose>
																				<c:if test="${(orderItem.kind >= 0 and orderItem.kind != 10)}">
																				<a class="J_hover" data-name="<c:if test="${isService}">[服务]</c:if>${orderItem.name}" data-price="${orderItem.unitPrice}" data-num="${orderItem.qty}"
																					<c:if test="${order.isVirtual == 0}">href="${href}"</c:if> title="${orderItem.name}" target="_blank">
																					<c:choose>
																						<c:when test="${isService}">
																							<img src="${staticDomain}/images/member/img_yb.jpg">
																						</c:when>
																						<c:otherwise>
																							<img src="<picUrl:pic1  picUrl='${orderItem.picUrl}' imgInsideUrl='${imgInsideUrl}' type='${packageOrder.supplierType}' storeDomainUrl='${storeUrl}' size='80x80' />">
																						</c:otherwise>
																					</c:choose>
																				</a>
																			</c:if>
																		</c:forEach>
							                                        	</div>
							                                        </c:if>
							                                    </td>
							                                    <td class="td_03">
							                                    	   <input type="hidden" class="order_id" value="${order.ogSeq}" />
																	   <input type="hidden" class="type" value="${packageOrder.supplierType}" />
																	   <c:if test="${packageOrder.supplierType == 1}">
																	   <input type="hidden" class="package_no" value="${packageOrder.packOrder}" />
							                                    		<p><a target="_blank" class="btn-02" href="${basePath}comment/commentView/${order.ogSeq}/${packageOrder.packOrder}/${packageOrder.supplierType}">评论</a></p>
					                                            	   </c:if>
																	   <c:if test="${packageOrder.supplierType != 1}">
																	   <input type="hidden" class="package_no" value="${packageOrder.packNo}" />
							                                    		<p><a target="_blank" class="btn-02" href="${basePath}comment/commentView/${order.ogSeq}/${packageOrder.packNo}/${packageOrder.supplierType}">评论</a></p>
					                                            	   </c:if>
							                                    		<p class="J-oky"><span>一键好评</span></p>
							                                    </td>
															</tr>
														</c:forEach>
													</c:if>
		                        				</table>
		                        			</c:forEach>
		                        		</div>
				      			</div>
							<!-- page next star -->
						  <div class="fn_page clearfix">
                                	<ul>
							<li class="fn_prve <c:if test="${pageDataBefore.fn_prve=='off'}">off</c:if>"><a target="_blank" class="target_no"
								onclick="self.location.href='<c:choose><c:when test="${pageDataBefore.prve_href!=''}">${pageDataBefore.pre_href}</c:when><c:otherwise>javascript:void(0);</c:otherwise></c:choose>'"><i
									class="arrow_prev"></i><span>上一页</span></a></li>
							<li><span class="cur">${pageDataBefore.pageNo}</span>/<span class="all">${pageDataBefore.totalpage}</span></li>
							<li class="fn_next <c:if test="${pageDataBefore.fn_next=='off'}">off</c:if>"><a target="_blank" class="target_no"
								onclick="self.location.href='<c:choose><c:when test="${pageDataBefore.next_href!=''}">${pageDataBefore.next_href}</c:when><c:otherwise>javascript:void(0);</c:otherwise></c:choose>'"><span>下一页</span><i
									class="arrow_next"></i></a></li>
							<li><span>到第</span><input id="page_num" name="pagenum"
								maxlength="5" type="text" style="width: 30; height: 22"><span>页</span></li>
							<li class="goto"><a id="go_page" href="javascript:;"
								class="target_no"">确定</a></li>
						    </ul>
                            </div>
							<!-- page next end -->
							  </c:if>		
							  <c:if test="${empty orderList}">
								<p class="text20" style="text-align:center"><br>您目前没有待评论的商品</p><br><br><br><br><br><br>
							  </c:if>		      			
				      		</div>
						</div>
					</div>
				</div>
				<div class="explanation">
					<h5 class="explanation_tips">评论说明</h5>
					<div class="explanation_cont">
					<p>1.评论是您对商品质量、服务水平、用后体验等所发表的意见和感受，您的宝贵建议是我们不断改进的动力；</p>
						<p>2.成功评价商品后您可以获得<span class="redtxt">飞牛积分</span>，加精置顶评论还有<span class="redtxt">双倍积分</span>奖励，详见 <a class="jflink" href="http://sale.feiniu.com/help_center/hc-6.html" target="_blank">【积分规则】</a>；</p>
						<p>3.为了及时收到您的宝贵建议，请您在收到商品后的三个月之内发表评论，同一商品一次只能发表一条评论；</p>
						<p>4.订购商城商品，确认收货后三十日内若无评价记录，系统将默认好评；</p>
						<p>5.您可以在初次评论后的三个月内对已评论的商品追加评论，对同一个商品只能追加一条评论；</p>
						<p>6.您可对自营和商城商品做出的中评（2星和3星）、差评（1星）进行删除；</p>
						<p>7.删除评论后，评论记录无法恢复，也无法进行追评。</p>
						</div>
				</div>
			</div>
		<!-- col main end -->
	</div>
</div>  	
<div id="o-onekey" style="display: none;">亲，使用一键好评可助你轻松获取积分和成长值，商品默认五星，评论内容已帮您预设，确认使用请点击“提交”</div>
${footerHtml}

<script src="${staticDomain}/product/js_build/lib/requirejs/2.1.8/require.js"></script>
<script>

	var static_domain = "${staticDomain}",
		trigger = '${trigger}',
		URL_MEMBER = "${basePath}",
		CSRF_TOKEN = '${CSRF_TOKEN}';
		
	require([static_domain + "/product/js/controller/member/config.js?version="+time_stamp], function() {
		
		require(["controller/member/myCommentView", "controller/member/leftMenu", "controller/shop/cart","controller/member/myComments"],function(){ 	    
	    	
	    	require([static_domain+'/product/js/lib/upLogger.js', static_domain+'/product/js/lib/idigger.js'], function() {
        	 	//埋点
	    		upLogger.acceptLinkParams('1', '7015', '7');
	    		
	    		idigger && idigger.init();
           });
	    	
	    });
	});
	
</script>
</body>
</html>