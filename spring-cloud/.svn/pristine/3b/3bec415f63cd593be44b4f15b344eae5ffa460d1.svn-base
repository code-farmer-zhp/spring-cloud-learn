<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<title>会员等级</title>
	<link rel="shortcut icon" href="${staticDomain}/images/feiniu_favicon.ico" />
	<link rel="stylesheet" type="text/css" href="${staticDomain }/product/css_build/common.css?v=${version}"/>
	<link rel="stylesheet" type="text/css" href="${staticDomain}/product/css_build/member/grade.css?v=${version}" />
	<link href="${staticDomain }/product/js_build/lib/artdialog/skins/default.css?v=${version}" rel="stylesheet" type="text/css" />
	<link href="${staticDomain }/product/js_build/lib/artdialog/skins/art_skin_order.css?v=${version}" rel="stylesheet" type="text/css" />
</head>
<body>
    ${headerHtml}
	${navHeaderHtml}
	
<div class="g-container">
    <!-- bread crumbs star -->
    <div class="g-crumbs">
    	<span><a href="${memberUrl}/member/home">我的飞牛</a></span> &gt;
    	<span>我的等级</span>
    </div>
    <div class="g-wrapper fixed">
    	${leftHtml}
		<div class="g-g-c">
			<div class="m-g clearfix">
				<div class="m-g-cont">
					<div class="m-g-info">

						<div class="m-head"> <a class="head-pos" href="${safeUrl}/personalInfo/infoShow">
						<c:if test="${empty headImage}">
							<img src="${defaultHeadImg}" >
						</c:if>
						<c:if test="${not empty headImage}">
							<img src="${headImgUrl}${headImage}">
						</c:if>
						</a>
						</div>

						<div class="m-info">

							<p class="m-ninm">${memName}</p>

							<p class="m-class">会员等级：
							<i>
							 <c:choose>
								<c:when test="${memResult.memLevel!=null && memResult.memLevel!='null'&& memResult.memLevel!=''}">
									${memResult.memLevelDesc}
								</c:when>
							<c:otherwise>
								普通会员
							</c:otherwise>
							</c:choose>
							</i>
							</p>

							<div class="m-rela">

								<ul>
									<li class="m-rela-1" ><a href="javascript:;" class="target_no">当前成长值：<br /><i>${memResult.growthValue}</i></a><em></em></li>
									<li class="line m-rela-2"><a href="javascript:;" class="target_no">已超过<br /><i>${memResult.overPercent}</i>的会员</a><em></em></li>
									<li class="m-rela-3"><a href="javascript:;" class="target_no">等级有效期：<br />
									<i>
									<c:choose>
									<c:when test="${memResult.memLevel==level0 ||memResult.memLevel==level1||memResult.memLevel==null || memResult.memLevel=='null'|| memResult.memLevel==''}">
										永久
									</c:when>
									<c:otherwise>
										${memResult.expiryDate}
									</c:otherwise>
									</c:choose>
									</i></a><em></em></li>

								</ul>

							</div>

						</div>

					</div>
					<c:if test="${memResult.memLevel!='TP'&&memResult.memLevel!='TU'}">
					<div class="m-g-q">

						<p class="m-q-title"><span class="q-title">我的会员特权</span></p>

						<div class="m-g-s J_slide_parent" data-scrollnum="1" data-maxnum="4" data-controller="1" data-speed="400" data-index="0">

							<a href="javascript:;" class="m-g-prev roll_btn J_prev target_no"></a>

							<div class="m-g-s-b J_slide_box">

								<ul class="clearfix">
									<li><c:choose>
											<c:when test="${mrstUiMap.T5==\"1\"}">
												<a data-status="1" class="nr-q nr-q-s" target="_blank" href="${basePath}growth/pkad?cur=nr"></a>
											</c:when>
											<c:otherwise>
												<a data-status="0" class="nr-q" target="_blank" href="${basePath}growth/pkad?cur=nr"></a>
											</c:otherwise>
										</c:choose> <span>新人礼包</span></li>
									<li><c:choose>
											<c:when test="${mrstUiMap.T2==\"1\"}">
												<a data-status="1" class="ug-q ug-q-s" target="_blank" href="${basePath}growth/pkad?cur=ug"></a>
											</c:when>
											<c:otherwise>
												<a data-status="0" class="ug-q" target="_blank" href="${basePath}growth/pkad?cur=ug"></a>
											</c:otherwise>
										</c:choose> <span>升级礼包</span></li>

									<li><c:choose>
											<c:when test="${mrstUiMap.T3==\"1\"}">
												<a data-status="1" class="ms-q ms-q-s" target="_blank" href="${basePath}growth/pkad?cur=ms"></a>
											</c:when>
											<c:otherwise>
												<a data-status="0" class="ms-q" target="_blank" href="${basePath}growth/pkad?cur=ms"></a>
											</c:otherwise>
										</c:choose> <span>神秘礼包</span></li>
								</ul>

							</div>

							<a href="javascript:;" class="m-g-next roll_btn J_next target_no"></a>

						</div>

					</div>
					</c:if>
					<div class="m-g-b">

						<p class="m-b-title">未领取礼包</p>

						<div class="m-b-cont">
							<c:choose>
								<c:when test="${noTakeResult== null || noTakeResult.data == null||noTakeResult.data.totalItems == 0}">
									<p>好礼不在，<br />敬请期待！</p>
								</c:when>
								<c:otherwise>
									<ul class="clearfix">
										<li>
											<a class="no-q no-q-s" href="${basePath}growth/pkad?cur=pk">
												<i>${noTakeResult.data.totalItems}</i>
											</a>
											<span></span>
										</li>
									</ul>
								</c:otherwise>
							</c:choose>
						</div>

					</div>

				</div>

				<div class="m-g-l">

					<p class="m-l-t">

						<span class="l-title">我的升级进度</span>
						<span class="l-info"><a href="${instructionUrl}">成长值说明　></a></span>

					</p>

					<span id="grow_val" class="g-a-icon">我的成长值：<i>${memResult.growthValue}</i><em></em></span>

					<div class="m-l-s">

						<span class="g-name">普通会员</span>
						<span class="g-t g-t-one"><em></em>1</span>
						<span class="g-t g-t-two"><em></em>1001</span>
						<span class="g-t g-t-three"><em></em>3001</span>

						<div class="g-a g-one J_loading" data-minVal="0" data-maxVal="1000">
							<span class="g-bg g-one-bg"></span>
							<span class="g-icon icon-one"></span>
							<span class="g-txt">银卡会员</span>
						</div>
						<div class="g-a g-two J_loading" data-minVal="1000" data-maxVal="3000">
							<span class="g-bg g-two-bg"></span>
							<span class="g-icon icon-two"></span>
							<span class="g-txt">金卡会员</span>
						</div>
						<div class="g-a g-three J_loading" data-minVal="3000" data-maxVal="6000">
							<span class="g-bg g-three-bg"></span>
							<span class="g-icon icon-three"></span>
							<span class="g-txt">白金卡会员</span>
						</div>
						<div class="g-a g-four J_loading" data-minVal="6000" data-maxVal="8000">
							<span class="g-bg g-four-bg"></span>
						</div>

					</div>

				</div>

			</div>

			<div class="m-g-tab clearfix">

				<p id="J_CK_obj_not" class="m-t-t">

					<span class="t-title">我的成长值</span>
					<span class="t-info">
					<a href="javascript:;" class="J-view-recent cur target_no" id="t-info_this" data-url="${goUrl}" data-tp="0" onclick="contGo('${goUrl}','0')" >最近一个月</a>
					<a href="javascript:;" class="J-view-recent target_no" id="t-info_before" data-url="${goUrl}" data-tp="1" onclick="contGo('${goUrl}','1')" >一个月之前</a></span>

				</p>

				<div class="m-t-c J_CK_cont" <c:if test="${gridType==1}">style="display: none;"</c:if>>

					<table>

						<thead>
							<tr>
								<th width="170" scope="col">获得\扣减时间</th>
								<th width="170" scope="col">成长值</th>
								<th width="170" scope="col">类型</th>
								<th width="443" scope="col">详情</th>
							</tr>
						</thead>

						<tbody>
								<c:forEach items="${detailResult.growthDetailList}"
									var="growthDetail">
									<tr>
										<td>${growthDetail.growthChangeDate}</td>
										<td>${growthDetail.growthValue}</td>
										<td>${growthDetail.growthType}</td>
										<c:choose>
										<c:when test="${not empty growthDetail.orderNo}">
											<td>
											订单号：<a target="_blank" href="${memberUrl}/order/orderDetail/${growthDetail.orderNo}" class="num">${growthDetail.orderNo}</a>
											<c:if test="${not empty growthDetail.smSeq and growthDetail.smSeq!='_'}">
											<c:choose>
											<c:when test="${empty growthDetail.ogsSeq or growthDetail.ogsSeq=='_'}">
												商品：<a target="_blank" href="${storeDomain}/${growthDetail.smSeq}">${growthDetail.smSeq}</a>
											</c:when>
											<c:otherwise>
												商品：<a target="_blank" href="${storeDomain}/${growthDetail.smSeq}">${growthDetail.smSeq}</a>
											</c:otherwise>
											</c:choose>
											</c:if>
											</td>
										</c:when>
										<c:otherwise>
											<td>${growthDetail.growthDesc}</td>
										</c:otherwise>
										</c:choose>
									</tr>
								</c:forEach>
								<c:if test="${empty detailResult.growthDetailList}">
									<tr>
										<td colspan="4">您最近没有获得成长值哦，赶快去<a href="${wwwUrl}" target="_blank" style="color: #F00;">购物</a>获得成长值吧！</td>
									</tr>
								</c:if>
						</tbody>

					</table>
					<c:if test="${not empty detailResult.growthDetailList}">
						<div class="fn_page clearfix">
							<ul>
								<li class="fn_prve <c:if test='${pageData.fn_prve==0}'> off</c:if>"><a class="target_no"
									<c:if test='${pageData.fn_prve!=0}'> onclick="self.location.href='${pageData.pre_href}'" </c:if>><i
										class="arrow_prev"></i><span>上一页</span></a></li>
								<li><span class="cur">${pageData.pageNo}</span>/<span class="all">${pageData.totalpage}</span></li>
								<li class="fn_next <c:if test='${pageData.fn_next==0}'> off</c:if>"><a class="target_no"
									<c:if test='${pageData.fn_next!=0}'> onclick="self.location.href='${pageData.next_href}'" </c:if>><span>下一页</span><i
										class="arrow_next"></i></a></li>
								<li><span>到第</span><input id="pagenum" name="pagenum" style="width:22px;height:14px;*+width:22px;*+height:14px;"
									type="text"><span>页</span></li>
								<li class="goto"><a href="javascript:;" class="J-page-go target_no" data-num="pagenum" data-url="${pageData.goUrl}" 
													data-total="${pageData.totalpage}" data-type="0">跳转</a></li>
							</ul>
						</div>
						</c:if>
				</div>

				<div class="m-t-c J_CK_cont" <c:if test="${gridType!=1}">style="display: none;"</c:if>>

					<table>

						<thead>
							<tr>
								<th width="170" scope="col">获得\扣减时间</th>
								<th width="170" scope="col">成长值</th>
								<th width="170" scope="col">类型</th>
								<th width="443" scope="col">详情</th>
							</tr>
						</thead>

						<tbody>
							<c:forEach items="${detailResultBefore.growthDetailList}"
									var="growthDetailBeforeMonth">
									<tr>
										<td>${growthDetailBeforeMonth.growthChangeDate}</td>
										<td>${growthDetailBeforeMonth.growthValue}</td>
										<td>${growthDetailBeforeMonth.growthType}</td>
										
										<c:choose>
										<c:when test="${not empty growthDetailBeforeMonth.orderNo}">
											<td>
											订单号：<a target="_blank" href="${memberUrl}/order/orderDetail/${growthDetailBeforeMonth.orderNo}" class="num">${growthDetailBeforeMonth.orderNo}</a>

											<c:if test="${not empty growthDetailBeforeMonth.smSeq and growthDetailBeforeMonth.smSeq!='_'}">
											<c:choose>
											<c:when test="${empty growthDetailBeforeMonth.ogsSeq or growthDetailBeforeMonth.ogsSeq=='_'}">
												商品：<a target="_blank" href="${storeDomain}/${growthDetailBeforeMonth.smSeq}">${growthDetailBeforeMonth.smSeq}</a>
											</c:when>
											<c:otherwise>
												商品：<a target="_blank" href="${storeDomain}/${growthDetailBeforeMonth.smSeq}">${growthDetailBeforeMonth.smSeq}</a>
											</c:otherwise>
											</c:choose>
											</c:if>
											</td>
										</c:when>
											<c:otherwise>
											<td>${growthDetailBeforeMonth.growthDesc}</td>
											</c:otherwise>
										</c:choose>
									</tr>
								</c:forEach>
								<c:if test="${empty detailResultBefore.growthDetailList}">
									<tr>
										<td colspan="4">您最近没有获得成长值哦，赶快去<a href="${wwwUrl}" target="_blank" style="color: #F00;">购物</a>获得成长值吧！</td>
									</tr>
								</c:if>
						</tbody>

					</table>
						<c:if test="${not empty detailResultBefore.growthDetailList}">
						<div class="fn_page clearfix">
							<ul>
								<li class="fn_prve <c:if test='${pageDataBefore.fn_prve==0}'> off</c:if>"><a  class="target_no"
									<c:if test='${pageDataBefore.fn_prve!=0}'> onclick="self.location.href='${pageDataBefore.pre_href}'"</c:if>><i
										class="arrow_prev"></i><span>上一页</span></a></li>
								<li><span class="cur">${pageDataBefore.pageNo}</span>/<span class="all">${pageDataBefore.totalpage}</span></li>
								<li class="fn_next <c:if test='${pageDataBefore.fn_next==0}'> off</c:if>"><a class="target_no"
									<c:if test='${pageDataBefore.fn_next!=0}'>  onclick="self.location.href='${pageDataBefore.next_href}'"</c:if>><span>下一页</span><i
										class="arrow_next"></i></a></li>
								<li><span>到第</span><input id="pagenumbefore" name="pagenumbefore" style="width:22px;height:14px;*+width:22px;*+height:14px;"
									type="text"><span>页</span></li>
								<li class="goto"><a href="javascript:;" class="J-page-go target_no" data-num="pagenumbefore" 
										data-url="${pageDataBefore.goUrl}" data-total="${pageDataBefore.totalpage}" data-type="1">跳转</a></li>
							</ul>
						</div>
						</c:if>
				</div>
			</div>
						<!-- page next end -->
		</div>
	</div>
</div>
${footerHtml}

<script src="${staticDomain }/product/js_build/lib/requirejs/2.1.8/require.js"></script>
<script type="text/javascript">	

	var gridType="${gridType}";
	var static_domain = "${staticDomain}";
	
	require([static_domain + "/product/js/controller/member/config.js?v=" + time_stamp], function() {
	  require(["lib/common", 'controller/member/common', 'controller/member/gradeList', 'controller/member/leftMenu', "controller/shop/cart"],function(){
		   require([static_domain+'/product/js/lib/upLogger.js', static_domain+'/product/js/lib/idigger.js'], function() {
	   	 		//埋点
				upLogger.acceptLinkParams('1', '7014', '7');
				idigger.init && idigger.init();
	      });
	  });
	});
</script>
</body>
</html>