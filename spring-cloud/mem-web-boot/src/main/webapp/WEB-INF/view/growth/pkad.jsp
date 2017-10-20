<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<title>会员权益</title> 
	<base target="_self" />
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
    	<span>我的会员权益</span>
    </div>
    <div class="g-wrapper fixed">
    	${leftHtml}
    	<div class="m-right">
			<div class="g-g-c">
				<div class="m-g clearfix">
					<div class="m-g-cont">

						<div class="m-g-info">

							<div class="m-head">
								<a class="head-pos" href="${safeUrl}/personalInfo/infoShow"> <c:if
										test="${empty headImage}">
										<img src="${defaultHeadImg}">
									</c:if> <c:if test="${not empty headImage}">
										<img src="${headImgUrl}${headImage}">
									</c:if>
								</a>
							</div>

							<div class="m-info">

								<p class="m-ninm">${memName}</p>

								<p class="m-class">
									会员等级：
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
										<li class="m-rela-1"><a <c:if test="${memResult.memLevel!=levelP}">href="${basePath}growth/list/0/1"</c:if> class="target_no">当前成长值：<br />
											<i>${memResult.growthValue}</i></a><em></em></li>
										<li class="line m-rela-2"><a class="target_no">已超过<br />
											<i>${memResult.overPercent}</i>的会员
										</a><em></em></li>
										<li class="m-rela-3"><a class="target_no">等级有效期：<br />
												<i> <c:choose>
														<c:when
															test="${memResult.memLevel==level0 ||memResult.memLevel==level1||memResult.memLevel==levelP||memResult.memLevel==null || memResult.memLevel=='null'|| memResult.memLevel==''}">
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

							 <p class="m-q-title"><span class="q-title">我的会员特权</span>

							<div class="m-g-s J_slide_parent" data-scrollnum="1"
								data-maxnum="4" data-controller="1" data-speed="400"
								data-index="0">

								<a href="javascript:;" class="m-g-prev roll_btn J_prev target_no"></a>

								<div class="m-g-s-b J_slide_box">

									<ul class="clearfix">
										<li><c:choose>
												<c:when test="${mrstUiMap.T5==\"1\"}">
													<a data-status="1" class="nr-q nr-q-s target_no" cur-type='nr' href="javascript:;"></a>
												</c:when>
												<c:otherwise>
													<a data-status="0" class="nr-q target_no" cur-type='nr' href="javascript:;"></a>
												</c:otherwise>
											</c:choose> <span>新人礼包</span></li>
										<li><c:choose>
												<c:when test="${mrstUiMap.T2==\"1\"}">
													<a data-status="1" class="ug-q ug-q-s target_no" cur-type='ug' href="javascript:;"></a>
												</c:when>
												<c:otherwise>
													<a data-status="0" class="ug-q target_no"  cur-type='ug' href="javascript:;"></a>
												</c:otherwise>
											</c:choose> <span>升级礼包</span></li>

										<li><c:choose>
												<c:when test="${mrstUiMap.T3==\"1\"}">
													<a data-status="1" class="ms-q ms-q-s target_no" cur-type='ms' href="javascript:;"></a>
												</c:when>
												<c:otherwise>
													<a data-status="0" class="ms-q target_no" cur-type='ms' href="javascript:;"></a>
												</c:otherwise>
											</c:choose> <span>神秘礼包</span></li>
									</ul>

								</div>

								<a href="javascript:;" class="m-g-next roll_btn J_next target_no"></a>

							</div>

						</div>
						</c:if>
						<div class="m-g-b">

							<p class="m-b-title"><span class="q-title"></span>未领取礼包</p>

							<div class="m-b-cont" id="m-b-cont-pk">
								<c:choose>
									<c:when
										test="${noTakeResult== null || noTakeResult.data == null||noTakeResult.data.totalItems == 0}">
										<p>
											好礼不在，<br />敬请期待！
										</p>
									</c:when>
									<c:otherwise>
										<ul class="clearfix">
											<li><a class="no-q no-q-s target_no" href="javascript:;"> <i>${noTakeResult.data.totalItems}</i>
											</a> <span></span></li>
										</ul>
									</c:otherwise>
								</c:choose>
							</div>

						</div>

					</div>
				</div>
				<div class="m-t-b clearfix">

					<p id="J_CK_obj" class="t-b-t">

						<span class="b-title">会员礼包</span> <span class="b-btn">
							<c:if test="${memResult.memLevel!='TP'&&memResult.memLevel!='TU'}"><a class="cur target_no" href="javascript:;" id="J_CK_tq">会员特权说明</a></c:if>
							<a href="javascript:;" id="J_CK_pk" class="target_no <c:if test="${memResult.memLevel=='TP'||memResult.memLevel=='TU'}">cur</c:if>">会员礼包</a></span>

					</p>
					<c:if test="${memResult.memLevel!='TP'&&memResult.memLevel!='TU'}">
					<div class="t-b-t-cont J_CK_cont">

						<p id="J_MH_obj" class="t-b-oper">
							<a class="cur target_no" href="javascript:;" id="J_MH_nr">新人大礼包</a>
							<a href="javascript:;" id="J_MH_ug" class="target_no">升级礼包</a>
						</p>

						<div class="t-b-m">

							<div class="t-b-m-cont t-b-m-nr clearfix J_MH_cont">

								<p class="b-m-p">特权一：新人大礼包</p>
								<div class="t-b-m-c clearfix">
									<div class="b-m-c-l"></div>
									<div class="b-m-c-r">
									<p>Q：什么是新人礼包？</p>
									<p class="n-a">A：首单在飞牛网购买自营商品时未使用卡券（抵用券、购物金、优惠券）且现金支付（货到付款、银行卡及第三方平台支付）金额满<i>100元</i>，交易成功后7天即赠送<i>30元</i>抵扣券两张；</p>
									<p class="n-a">以上权益计算和发放起始时间为2016年2月1日，（会员首单在2016年1月31日后）</p>
									<p class="n-a n-m">注：新人礼包计算和发放起始时间为2015年9月15日，原有权益发放至2016年1月31日止。</p>
									</div>
								</div>
							</div>
							<div class="t-b-m-cont t-b-m-ug clearfix J_MH_cont"
								style="display: none;">

								<p class="b-m-p">特权二：升级礼包</p>
								<div class="t-b-m-c clearfix">
									<div class="b-m-c-l"></div>
									<div class="b-m-c-r">
										<p>
											银卡会员升级到金卡会员，赠送<i>50元等额礼物</i>；
										</p>
										<p>
											金卡会员升级到白金卡会员，赠送<i>100元等额礼物</i>；
										</p>
										<p>会员在达到相应等级的10天后(含当天),升级礼包将发送至您的账户。</p>
										<p>以上权益从2016年2月1日起计算并发放。</p>
										<p>注:原升级礼包计算和发放起始时间为2015年8月25日，截止时间为2016年1月31日。</p>
									</div>
								</div>

							</div>
						</div>

					</div>
					</c:if>
					<div class="t-b-t-cont J_CK_cont"
						<c:if test="${memResult.memLevel!='TP'&&memResult.memLevel!='TU'}">style="display: none"</c:if>
					>
						<jsp:include page="../growth/noTake.jsp"></jsp:include>
						<jsp:include page="../growth/taken.jsp"></jsp:include>
						<jsp:include page="../growth/expired.jsp"></jsp:include>

					</div>

				</div>

			</div>

			<!-- 未领取的礼包 -->
			<div id="g-layer-wei" style="display: none;">
				<div class="g-tip g-layer-tip g-layer-wei">
					<p class="g-tip-title">
						<em></em><span>领取成功！</span>
					</p>
				</div>
			</div>
		</div>
	</div>
</div>
		
${footerHtml}
</body>
</html>
<script src="${staticDomain }/product/js_build/lib/requirejs/2.1.8/require.js"></script>
<script>
	var CSRF_TOKEN='${CSRF_TOKEN}';
	var myUrl='${myUrl}';
	var curTab='${curTab}';
	
	var static_domain = "${staticDomain}";

	require([static_domain + "/product/js/controller/member/config.js?v=" + time_stamp], function() {
	  require(["lib/common", 'controller/member/common', 'controller/member/gradePkad', 'controller/member/leftMenu', "controller/shop/cart"],function(){
	  	  require([static_domain+'/product/js/lib/upLogger.js', static_domain+'/product/js/lib/idigger.js'], function() {
       		//埋点
        	upLogger.acceptLinkParams('1', '7058', '7');
        	idigger.init && idigger.init();
          });
	  });
	});
	
</script>