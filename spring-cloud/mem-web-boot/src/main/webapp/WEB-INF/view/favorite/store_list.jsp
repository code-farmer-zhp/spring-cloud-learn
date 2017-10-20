<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title>我的收藏</title>
<link rel="shortcut icon" href="${staticDomain}/images/feiniu_favicon.ico" />
<link rel="stylesheet" type="text/css" href="${staticDomain}/product/css_build/common.css?v=${version}"/>
<link rel="stylesheet" type="text/css" href="${staticDomain}/product/css_build/member/favorite.css?v=${version}"/>
<link href="${staticDomain}/js/lib/artdialog/skins/default.css?v=${version}" rel="stylesheet" type="text/css" />
<link href="${staticDomain}/js/lib/artdialog/skins/art_skin_order.css?v=${version}" rel="stylesheet" type="text/css" />
</head>
${headerHtml}
<!---导航-->
${navHeaderHtml}

<div class="g-container">
	<!-- bread crumbs star -->
	<div class="g-crumbs">
		<span><a href="${memberUrl}/member/home" class="target_no">我的飞牛</a></span> &gt; <span class="color">我的收藏</span>
	</div>
	<!-- bread crumbs end -->
	<!-- sideBar nav star -->
	<div class="g-wrapper fixed">
	${leftHtml}
	
	<div class="m-right">
		<div class="main favorites">
			<!-- themes star -->
			<div class="themes_title">
				<h3>我的收藏</h3>
			</div>
			<!-- themes end -->
			<div class="ui_tab myfavorites">
				<ul class="ui_tab_nav prod_store_nav">
					<li><a class="target_no"
						href="${basePath}favorite/prodsFavorite">商品收藏</a></li>
					<li class="last active"><a
						href="${basePath}favorite/storesFavorite">店铺收藏</a></li>
				</ul>
			<c:if test='${not empty memHasCommentCount.data}'>
				<div class="attrs">
								<div id="attr_452" class="m-tr">
									<div class="g-left">
										<p>店铺收藏</p>
									</div>
									<div class="g-right ">
										<div class="g-list">
											<ul class="f-list h76">
												<li><a href="javascript:void(0);" target="_blank" class="target_no J-pageproper"
												data-page='{"kindId":"","ids":"","type":"sto","url":"${basePath}favorite/propertyFavorite"}'
												>全部(${category.data.cateCounts })
												</a></li>
												<c:set var="index" value="0" />
												<c:forEach items="${category.data.cates}" var="store"
													varStatus="content">
													<li><a href="javascript:void(0);" class="J-pageproper"
														data-page='{"kindId":"${store.kindId }","ids":"","type":"sto","url":"${basePath}favorite/propertyFavorite"}'
														kindId="${store.kindId }">${store.kindName }(${store.favoriteCount })
													</a>
													</li>
													<c:set var="index" value="${index+1}" />
												</c:forEach>
											</ul>
											<c:if test="${index > 13}">
											<div class="f-ext">
												<a class="f-more target_no" data-attr="attr_452" href="javascript:;">更多<i></i></a>
											</div>
											</c:if>
										</div>
									</div>
								</div>
							</div>
			
					<div class="ui_tab_content">
						<!-- Items Collect star -->
						<div class="ui_panel" style="display: block;" id="myFavorites">

							<!-- Items Collect end -->
							<div class="ui_panel shop_collection" style="display: block;"
								id="shopCollection">
									<jsp:include page="../favorite/slist.jsp"></jsp:include>
									</div>
									</div>
									</div>

								<!-- shop favorites star -->
								
				</c:if>
			<c:if test='${empty memHasCommentCount.data}'>
				    <div class="attrs">
						<div id="attr_452" class="m-tr">
							<div class="g-left">
								<p>店铺收藏</p>
							</div>
							<div class="g-right ">
								<div class="g-list">
									<ul class="f-list h76">
										<li><a href="javascript:void(0);" target=""
											class="target_no">全部(0) </a></li>
									</ul>
								</div>
							</div>
						</div>
					</div>
					<!-- No favorites star -->
					<div class="no_favorites">
						<p>暂无店铺收藏</p>
						<p>
							去<a href="${mallUrl}">飞牛商城</a>逛逛吧
						</p>
					</div>
			</c:if>
		</div>
<div class="fn-fav ui_poptip ui_poptip_pop" id="leftUiPop" style="display: none;">
	<div class="ui_poptip_container">
		<div class="ui_poptip_arrow poptip_down">
			<em></em> <span></span>
		</div>
		<div class="ui_poptip_content"></div>
	</div>
</div>

<div class="ui_poptip ui_poptip_pop" id="rightUiPop" style="display: none;">
	<div class="ui_poptip_container">
		<div class="ui_poptip_arrow poptip_up">
			<em></em> <span></span>
		</div>
		<div class="ui_poptip_content">
			<p class="text">确认要删除吗？</p>
			<p class="clearfix">
				<a href="javascript:void(0);" class="del_ok target_no">确认</a> <a
					href="javascript:void(0);" class="cancel target_no">取消</a>
			</p>
		</div>
	</div>
</div>
<!-- 加入购物车 成功弹层 -->
<div id="popUp" class="J_popUpFail" style="display: none;">
	<div class="layout">
		<div class="main">
			<div class="pop_head">
				<h3>温馨提示</h3>
				<span data-x="1">X</span>
			</div>
			<div class="pop_container">
				<div class="pop_message"></div>
				<div class="popUp_operating clearfix">
					<a href="javascript:;" class="btn-02 sure target_no">确定</a>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="popUp" class="J_popUpSuccess" style="display: none;">
	<div class="layout">
		<div class="main">
			<div class="pop_head">
				<h3>温馨提示</h3>
				<span data-x="1">X</span>
			</div>
			<div class="pop_container">
				<div class="icon_success">添加成功！</div>
				<div class="popUp_operating clearfix">
					<a href="javascript:;" class="btn-02 target_no">继续购物</a> <a
						href="<?php echo $shopUrl?>/cart/index" class="btn_goShopping">去购物车结算</a>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- 加入购物车 成功弹层 end -->
<!-- col main end -->
</div>
</div>
</div>
</div>

${footerHtml}

<script src="${staticDomain }/product/js_build/lib/requirejs/2.1.8/require.js"></script>
<script>
		var static_domain = "${staticDomain}",
			trigger = '${trigger}',
			URL_MEMBER = '${basePath}',
			receiveCouponJsp = "${receiveCouponJsp}",
			CSRF_TOKEN='${CSRF_TOKEN}';
		
		require([static_domain + "/product/js/controller/member/config.js?version="+time_stamp], function() {
		
			require(["controller/member/prodsFavorite", "controller/member/leftMenu", "controller/shop/cart"],function(){				
				
				require([static_domain+'/product/js/lib/upLogger.js', static_domain+'/product/js/lib/idigger.js'], function() {					
				 	//埋点
					upLogger.acceptLinkParams('1', '7019', '7');
				    idigger.init && idigger.init();
			   });
				
			});
		});
</script>
</html>