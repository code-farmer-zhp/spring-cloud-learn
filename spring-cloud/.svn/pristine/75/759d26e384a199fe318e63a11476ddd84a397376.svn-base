<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	response.setHeader("Pragma","No-cache");
	response.setHeader("Cache-Control","No-cache");
	response.setDateHeader("Expires", -1);
	response.setHeader("Cache-Control","No-store");
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<title>我的足迹</title>
	<link rel="shortcut icon" href="${staticDomain}/images/feiniu_favicon.ico" />
	<link rel="stylesheet" type="text/css" href="${staticDomain}/product/css_build/common.css?v=${version}"/>
	<link rel="stylesheet" type="text/css" href="${staticDomain}/product/css_build/member/history.css?v=${version}"/>
</head>
<body>
${headerHtml}
<!---导航-->
${navHeaderHtml}

<div class="g-container">
	<!-- bread crumbs star -->
	<div class="g-crumbs">
		<span><a href="${memberUrl}/member/home" class="target_no">我的飞牛</a></span> &gt; <span class="color">我的足迹</span>
	</div>
	
	<div class="g-wrapper fixed">
	${leftHtml}

	<div class="m-right">
		<div class="m-my-history">
			<!-- themes star -->
			<div class="z-title">
				<span>我的足迹</span>
			</div>
			<!-- themes end -->
			<c:choose>
				<c:when test="${typeList == null || typeList.size() == 0}">
				</c:when>
				<c:when test="${typeList.size() > 0}">
					<div class="f-category">
						<div class="u-left">
							<a href="javascript:void(0);" class="red target_no" data-cId="">
								全部(${typeList.counts})</a>
						</div>
						<ul class="u-right">
							<c:forEach items="${typeList.cateCountContent}" var="kindItem">
								<li><a href="javascript:void(0);" class="target_no" id="${kindItem.kindId}" data-cId="${kindItem.kindId}">
										${kindItem.kindName}(${kindItem.footprintCount})</a></li>
							</c:forEach>
						</ul>
						<div class="u-more hide">
							<a href="javascript:;" class="target_no" >更多<i></i></a>
						</div>
					</div>
					<div class="f-tpye-bar">
						<span>
							以下是您最近30天的商品浏览记录
						</span>
						<span class="u-check">
							<ul>
								<li class="z-bar-del"><a href="javascript:;"  class="target_no" ><i></i> &nbsp;清空</a></li>
								<li><input type="checkbox" name="z-drop" id="z-drop" value="0" ids="" nmm ="lowPrice" class="J-Kindgo"/>
									<label for="z-drop">仅显示降价</label></li>
								<li><input type="checkbox" name="z-prom" id="z-prom" value="0" ids="" nmm = "moreAct" class="J-Kindgo" />
									<label for="z-prom">仅显示促销</label></li>
							</ul>
							<!--清空所有确认弹层-->
							<div class="f-del-pop hide">
								<span class="arrow1"></span>
								<span class="arrow2"></span>
								<p>您确认要清空所有浏览记录吗？ </p>
								<p>
								    <input type="button" name="confirm" class="confirm J-alldel" alltype="0" alltext=""/>
									<input type="button" name="cancel" class="cancel"/>
								</p>
							</div>
							<!-- //清空所有确认弹层-->
						</span>
					</div>
				</c:when>
			</c:choose>
			<div class="f-cont-prud" id="detail" pageNo="" pageCount="" type="" ids="" getData="0">

			</div>
			<div class="f-clear" id="clear"></div>
			<div class="f-loading">
				<i class="u-loadimg" id="loadingPic"></i>
				<p id="loadingText">加载中，请稍后...</p>
				<p id="completeShowing" style="display: none">已到最后，只保存最近30天的浏览记录</p>
			</div>
		</div>
	</div>
	<!-- col main end -->
</div>
</div>
${footerHtml}

<script src="${staticDomain }/product/js_build/lib/requirejs/2.1.8/require.js"></script>
<script>
		var static_domain = "${staticDomain}",
			URL_MEMBER = '${basePath}',
			trigger = "${trigger}",
			CSRF_TOKEN='${CSRF_TOKEN}',
			wwwUrl = '${wwwUrl}';
		
		require([static_domain + "/product/js/controller/member/config.js?version="+time_stamp], function() {
		
			require(["controller/member/myhistory", "controller/member/leftMenu", "controller/shop/cart"],function(){
				require([static_domain+'/product/js/lib/upLogger.js', static_domain+'/product/js/lib/idigger.js'], function() {
				//埋点
				upLogger.acceptLinkParams('1', '7057', '7');
				idigger.init && idigger.init();
				});
			});
		});
	
</script>
</body>
</html>