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

	<!-- sideBar nav end -->
	<!-- col main star -->
	<div class="m-right">
		<div class="main favorites">
			<!-- themes star -->
			<div class="themes_title">
				<h3>我的收藏</h3>
			</div>
			<!-- themes end -->
			<div class="ui_tab myfavorites">
				<ul class="ui_tab_nav prod_store_nav">
					<li class="active"><a href="${basePath}favorite/prodsFavorite">商品收藏</a></li>
					<li class="last"><a href="${basePath}favorite/storesFavorite">店铺收藏</a></li>
				</ul>
				<c:if test='${not empty memHasCommentCount.data}'>
				<div class="attrs">
								<div id="attr_452" class="m-tr">
									<div class="g-left">
										<p>商品收藏</p>
									</div>
									<div class="g-right ">
										<div class="g-list">
											<ul class="f-list h76">
												<li><a href="javascript:void(0);" target="_blank" class="target_no J-pageproper" 
												data-page='{"kindId":"","ids":"","type":"prod","url":"${basePath}favorite/propertyFavorite"}'
												>全部(${category.data.cateCounts })
												</a></li>
												<c:set var="index" value="0" />
												<c:forEach items="${category.data.cates}" var="store"
													varStatus="content">
													<li><a href="javascript:void(0);" class="J-pageproper"
														data-page='{"kindId":"${store.kindId }","ids":"","type":"prod","url":"${basePath}favorite/propertyFavorite"}'
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
								<div id="attr_413" class="m-tr">
									<div class="g-left">
										<p>活动商品</p>
									</div>
									<div class="g-right ">
										<div class="g-list">
											<ul class="f-list">
												<li><a href="javascript:void(0);" target="_blank" class="target_no J-pageproper" 
												data-page='{"kindId":"","ids":"","type":"prod","url":"${basePath}favorite/propertyFavorite"}'>
												全部(${category.data.cateCounts })
												</a></li>
												<c:forEach items="${category.data.actcates}" var="store"
													varStatus="content">
													<c:if test='${store.favoriteCount != 0 and store.kindName=="促销"}'>
													<li><a href="javascript:void(0);" class="J-pageproper" data-page='{"kindId":"","ids":"${store.kindId }","type":"prod","url":"${basePath}favorite/propertyFavorite"}'
														ids="${store.kindId }">${store.kindName }(${store.favoriteCount })
													</a></li>
													</c:if>
 												</c:forEach>
											</ul>
										</div>
									</div>
								</div>
							</div>
					<!-- 四级市DOM结构 @japin.pan -start -->
					<div class="collect-sjs">
						    <div class="f-store" id="fourLevelAreas" tp-childarea="flc">
                            <div class="s-text">
                                <p class="n">送货至：</p>
                                <p class="l" id="curSelect"></p>
                            </div>
                            <div class="s-cont">
                                <i class="close" cur-close></i>
                                <div class="s-tab _tab">
                                    <a class="z-select" href="javascript:;">请选择省</a>
                                    <a style="display:none;" href="javascript:;">请选择市</a>
                                    <a style="display:none;" href="javascript:;">请选择区</a>
                                    <a style="display:none;" href="javascript:;">请选择镇</a>
                                </div>
                                <div class="s-lst _tabCont">
                                    <div class="s-panel _cont">
                                    </div>
                                    <div class="s-panel _cont hide">
                                    </div>
                                    <div class="s-panel _cont hide">
                                    </div>
                                    <div class="s-panel _cont hide">
                                    </div>
                                </div>
                            </div>
                        </div>
				     </div>
			        <!-- 四级市DOM结构 @japin.pan -end -->
					<div class="ui_tab_content">
						<!-- Items Collect star -->
						<div class="ui_panel" style="display: block;" id="myFavorites">

							<!-- favorites list star -->
							<div class="favorites_list">
										
								<!-- list title star -->
								<jsp:include page="../favorite/plist.jsp"></jsp:include>
								</div>
								</div>
								</div>
				</c:if>
			
			
			<!-- favorites list end -->
			<c:if test='${empty memHasCommentCount.data}'>
				<!-- No favorites star -->
						<div class="attrs">
								<div id="attr_452" class="m-tr">
									<div class="g-left">
										<p>商品收藏</p>
									</div>
									<div class="g-right ">
										<div class="g-list">
											<ul class="f-list h76">
												<li><a href="javascript:void(0);" target="" class="target_no" >全部(0)
												</a></li>
											</ul>
										</div>
									</div>
								</div>
								<div id="attr_413" class="m-tr">
									<div class="g-left">
										<p>活动商品</p>
									</div>
									<div class="g-right ">
										<div class="g-list">
											<ul class="f-list">
												<li><a href="javascript:void(0);" target="" class="target_no" >
												全部(0)
												</a></li>
											</ul>
										</div>
									</div>
								</div>
			</div>
				<!-- No favorites star -->
			   <div class="no_favorites">
                            	<p>暂无商品收藏</p>
                            	<p>去<a href="${wwwUrl}">飞牛首页</a>逛逛吧</p>
			   </div>
				<!-- No favorites end -->
			</c:if>
		</div>
		<!-- Items Collect end -->



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
<div id="popUp" class="J_popUpBefore" style="display: none;">
	<div class="layout">
		<div class="main">
			<div class="pop_head">
				<h3>温馨提示</h3>
				<span data-x="1">X</span>
			</div>
			<div class="pop_container">
				<div class="pop_message1">部分商品信息异动，将无法加入购物车!</div>
				<div class="pop_message2">继续添加其他有货商品？</div>
				<div class="popUp_operating clearfix">
					<a href="javascript:void(0);" class="target_no del_ok" id="J_popUpBefore_ok">确定</a>
					<a href="javascript:;" class="btn-02 target_no" style="margin-left:5px">取消</a>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="popUp" class="J_popUpFail" style="display: none;">
	<div class="layout">
		<div class="main">
			<div class="pop_head">
				<h3>温馨提示</h3>
				<span data-x="1">×</span>
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

<div id="popUp" class="J_popUpFail" style="display: none;">
	<div class="layout">
		<div class="main">
			<div class="pop_head">
				<h3>温馨提示</h3>
				<span data-x="1">×</span>
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
				<span data-x="1">×</span>
			</div>
			<div class="pop_container">
				<div class="icon_success">添加成功！</div>
				<div class="icon_success_end" style="margin-top:5px">部分商品异动无法添加,过些日子再来看看吧~</div>
				<div class="popUp_operating clearfix">
					<a href="javascript:;" class="btn-02 target_no">继续购物</a> <a
						href="${shopUrl}/cart/index" class="btn_goShopping">去购物车结算</a>
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
			CSRF_TOKEN='${CSRF_TOKEN}';
		
		require([static_domain + "/product/js/controller/member/config.js?version="+time_stamp], function() {
		
			require(["controller/member/prodsFavorite", "controller/member/leftMenu", "controller/shop/cart"],function(){					
				
				require([static_domain+'/product/js/lib/upLogger.js', static_domain+'/product/js/lib/idigger.js'], function() {					
				 	//埋点
					upLogger.acceptLinkParams('1', '7018', '7');
				    idigger.init && idigger.init();
			   });
				
			});
		});		
</script>
</html>