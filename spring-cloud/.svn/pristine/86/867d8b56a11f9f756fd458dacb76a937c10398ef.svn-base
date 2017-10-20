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
<link rel="stylesheet" href="${staticDomain}/product/js/lib/artdialog/skins/default.css">
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
			<li><a href="${basePath}subscribe/systemList" title="系统消息">
					<i class="stys"></i> <span>系统消息</span> <b>物流、商品、资产</b>
			</a></li>
			<li><a href="${basePath}subscribe/memberList" title="会员关怀">
					<i class="care"></i> <span>会员关怀<em id="carenum">0</em></span> <b>礼包、成长值</b>
			</a></li>
			<li><a href="${basePath}subscribe/activityList" title="活动通知">
					<i class="acts"></i> <span>活动通知<em id="actsnum">0</em></span> <b>优惠、促销</b>
			</a></li>
			<li class="last-child"><a class="selected"
				href="${basePath}subscribe/msgSets" title="设置"> <i class="sets"></i>
					<span>设置</span> <b></b>
			</a></li>
		</ul>
	</div>
	<!--消息中心菜单 end -->
	<!--消息设置-right start -->
	<div class="mc-sets m-fixed">
		<h1>消息订阅设置</h1>
		<div class="sets-cont m-fixed">
			<div class="sets-list">
				<h2>系统消息</h2>
				<ul>
					<li><a class="J-itemselect J-checkbox" type="checkbox"
						href="javascript:;" value="1-1"></a>积分消息</li>
					<li><a class="J-itemselect J-checkbox" type="checkbox"
						href="javascript:;" value="1-2"></a>卡券信息</li>
					<li><a class="J-itemselect J-checkbox" type="checkbox"
						href="javascript:;" value="1-3"></a>货到通知</li>
					<li><a class="J-itemselect J-checkbox" type="checkbox"
						href="javascript:;" value="1-4"></a>降价通知</li>
					<li><a class="J-itemselect J-checkbox" type="checkbox"
						href="javascript:;" value="1-5"></a>物流通知</li>
					<li><a class="J-itemselect J-checkbox" type="checkbox"
						href="javascript:;" value="1-6"></a>账户余额</li>
				</ul>
			</div>
			<div class="sets-list">
				<h2>会员关怀</h2>
				<ul>
					<li><a class="J-itemselect J-checkbox" type="checkbox"
						href="javascript:;" value="2-1"></a>礼包消息</li>
					<li><a class="J-itemselect J-checkbox" type="checkbox"
						href="javascript:;" value="2-2"></a>成长记录</li>
					<li><a class="J-itemselect J-checkbox" type="checkbox"
						href="javascript:;" value="2-3"></a>系统审核</li>
				</ul>
			</div>
			<div class="sets-list">
				<h2>活动通知</h2>
				<ul>
					<li><a class="J-itemselect J-checkbox" type="checkbox"
						href="javascript:;" value="3-1"></a>活动推荐</li>
				</ul>
			</div>
		</div>
		<div class="sets-btn">
			<a href="javascript:;" class="btn_ok J-save-btn">保存</a>
		</div>
	</div>
	<!--消息设置-right end -->

</div>
<!-- col main end -->
${footerHtml}
<script src="${staticDomain }/product/js_build/lib/requirejs/2.1.8/require.js"></script>
<script>
	var static_domain = "${staticDomain}", CSRF_TOKEN = '${CSRF_TOKEN}', URL_MEMBER = '${basePath}';

	require([static_domain+ "/product/js/controller/member/config.js?version="+ time_stamp], function() {

			require([ "lib/jquery/1.11.1/jquery-1.11.1.min",'controller/member/msgSets', "controller/shop/cart"], function() {
				
					require([ static_domain + '/product/js/lib/upLogger.js',static_domain + '/product/js/lib/idigger.js'],function() {
								//埋点
								upLogger.acceptLinkParams('1', '7041', '7', '','4');

								setTimeout(function(){
									 idigger && idigger.init();
				                },2000);
					});

			});
	});
</script>
</html>
