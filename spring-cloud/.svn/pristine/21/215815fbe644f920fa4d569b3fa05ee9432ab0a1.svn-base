<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title>我的消息</title>
<link rel="shortcut icon" href="${staticDomain}/images/feiniu_favicon.ico" />
<link rel="stylesheet" type="text/css"
	href="${staticDomain}/product/css_build/common.css?v=${version}" />
<link rel="stylesheet" type="text/css"
	href="${staticDomain}/product/css_build/member/msgcenter.css?version=${version}">

</head>
${headerHtml}
<!---导航-->
${navHeaderHtml}

<!--消息中心主体 start -->
<div class="mc-main m-fixed">

	<!--消息中心菜单 start -->
	<div class="mc-menu">
		<ul>
			<li><a class="selected" href="${basePath}subscribe/systemList"
				title="系统消息"> <i class="stys"></i><span>系统消息</span> <b>物流、商品、资产</b>
			</a></li>
			<li><a href="${basePath}subscribe/memberList" title="会员关怀">
					<i class="care"></i> <span>会员关怀<em id="carenum">0</em></span> <b>礼包、成长值</b>
			</a></li>
			<li><a href="${basePath}subscribe/activityList" title="活动通知">
					<i class="acts"></i> <span>活动通知<em id="actsnum">0</em></span> <b>优惠、促销</b>
			</a></li>
			<li class="last-child"><a href="${basePath}subscribe/msgSets"
				title="设置"> <i class="sets"></i> <span>设置</span> <b></b>
			</a></li>
		</ul>
	</div>
	<!--消息中心菜单 end -->
	<!--系统消息-right start -->
	<div class="mc-stys m-fixed">
		<jsp:include page="../subscribe/sublist.jsp"></jsp:include>
	</div>

</div>
<!-- col main end -->
${footerHtml}
<script src="${staticDomain }/product/js_build/lib/requirejs/2.1.8/require.js"></script>
<script>
	var static_domain = "${staticDomain}", CSRF_TOKEN = '${CSRF_TOKEN}', URL_MEMBER = '${basePath}';
	require([static_domain + "/product/js/controller/member/config.js?version="+ time_stamp ], function() {

				require([ "lib/jquery/1.11.1/jquery-1.11.1.min", 'controller/member/systemList', "controller/shop/cart"], function() {

					require([ static_domain + '/product/js/lib/upLogger.js', static_domain + '/product/js/lib/idigger.js' ], function() {
							//埋点
							upLogger.acceptLinkParams('1', '7041', '7', '','1');
							
							$(document).on('click', '.J-cont', function(e) {
					            var backVal = $.trim($(this).attr("value"));
					            upLogger.acceptEventParams($(this), '', '2', '7052', '7', e, backVal);
					        });
							
							setTimeout(function(){
								 idigger && idigger.init();
			                },2000);

						});

				});
			});
</script>
</html>
