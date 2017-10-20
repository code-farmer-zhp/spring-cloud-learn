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
	<link rel="stylesheet" type="text/css" href="${staticDomain }/product/css_build/member/myrating.css?v=${version}"/>
	<link rel="stylesheet" href="${staticDomain }/js/lib/artdialog/skins/default.css?v=${version}">
	<link rel="stylesheet" href="${staticDomain }/js/lib/artdialog/skins/art_skin_order.css?v=${version}">
</head>
<body>
${headerHtml}
<!---导航-->
${navHeaderHtml}
	<div class="g-container">
		<!-- bread crumbs star -->
		 <div class="g-crumbs">
            <span><a href="${memberUrl}/member/home" class="target_no">我的飞牛</a></span> &gt; <span class="color">追加评论</span>
        </div>
		<!-- bread crumbs end -->
		<!-- sideBar nav star -->
		<div class="g-wrapper fixed">
		${leftHtml}
		<!-- sideBar nav end -->
		<!-- col main star -->
		<div class="m-right">
			<div class="main evaluation">
				<!-- themes star -->
				<div class="themes_title">
					<h3>我的追评</h3>
				</div>
				<!-- themes end -->
			<div class="ui_tab">
		
					<div class="ui_tab_content">
	                    <div class="ui_panel" id="commented" style="display:block;">
							<div class="success_mes" id="successMe">
				      			<i class="icon_success"></i>
				      			<p>您已完成<span class="num"></span>件商品的追加评论，感谢您对飞牛网的支持！</p>
				      			<a href="${basePath}comment/hasCommentView">查看我的评价</a>
				      		</div>
		      		
		      		<div class="evaluation_cont">
		      			
		      			
		      			<c:if test='${not empty goodList}'>
					<c:forEach items="${goodList}" var="good" varStatus="status">
	                    	<div class="single_review clearfix">
	                    		<div class="fl goods_img">
	                    		  <c:if test="${good.virtualGood == false}">
				      					<a href="${storeDomainUrl}/${good.skuId}"  class="head_bg" >
				      			  </c:if>
                                  <c:if test="${good.virtualGood == true}">
				      					<a   class="head_bg" >
				      			  </c:if>
                                
									<img src="<picUrl:pic1  picUrl='${good.picUrl}' imgInsideUrl='${imgInsideUrl}' type='${good.storeType}' storeDomainUrl='${storeUrl}' size='80x80' />" alt="${good.name }">
				      				</a>
	                    		</div>
	                    		<div class="fr goods_commented">
	                    			<div class="ui_poptip_arrow poptip_left">
										<em></em>
										<span></span>
									</div>
									<ul>
				      					<li class="frist clearfix">
				      					<input id="id" name="id" type="hidden" value="${good.commentId}">
				      						<span class="fl des_left">商品评分：</span>
				      						<div class="fl des_right">
				      							<div class="star clearfix" data-star="${good.starLevel}">
				      								<i></i>
					      							<i></i>
					      							<i></i>
					      							<i></i>
					      							<i></i>
				      							</div>					
				      						</div>
				      					</li>
				      					<li class="clearfix">
				      					<c:set value="${ fn:split(good.productMark,',') }" var="impress_label" />
				      						<c:if test='${not empty good.productMark}'>
				      						<span class="fl des_left">商品标签：</span>
				      						<div class="fl des_right">
				      							<c:forEach items="${impress_label}" var="impress" >
				      								<span class="tag">${impress}</span>
					      						</c:forEach>
				      						</div>
	                                       </c:if>
				      					</li>
				      					<li class="td_03 clearfix">
				      						<span class="fl des_left">初次评论：</span>
				      						<div class="fl des_right">
				      							<span class="text">${good.commentText}</span>
				      						</div>
				      					</li>
				      					<c:if test="${not empty good.commentPicUrls}">
				      					<li class="clearfix">
                                            <span class="fl des_left">晒&nbsp;图：</span>
                                            <div class="fl des_right">
                                                <ul class="fn-comment-photos">
                                                    <c:set value="${ fn:split(good.commentPicUrls, ';') }" var="commentPicUrls" />
                                                	 <c:forEach  items="${commentPicUrls}" var="commentPicUrl" varStatus="content">
                                                    <li class="">
                                                        <img  src="${commentPicUrl}"  alt=""><b></b>
                                                    </li>
                                                    </c:forEach>
                                                </ul>
                                                <div class="fn-photos-view">
                                                    <img src="../assets/img/s2.jpg" alt="">
                                                    <a class="view-navleft target_no" href="javascript:;"><i></i></a>
                                                    <a class="view-navright target_no" href="javascript:;"><i></i></a>
                                                </div>
                                            </div>
                                        </li>
				      					</c:if>
				      					<li class="clearfix">
				      						<span class="fl des_left">初评时间：</span>
				      						<div class="fl des_right">
				      							<span class="time">${good.commentDate}</span>
				      						</div>
				      					</li>
				      				<c:if test="${not empty good.replyText}">
				      				<c:if test="${good.storeType == 1 }">
				      					<li class="clearfix replay_li">
                                            <span class="fl des_left">客服回复：</span>
                                            <div class="fl des_right">
                                                ${good.replyText}
                                            </div>
                                        </li>
                                        </c:if>
                                        <c:if test="${good.storeType == 2 }">
				      					<li class="clearfix replay_li">
                                            <span class="fl des_left">商家回复：</span>
                                            <div class="fl des_right">
                                                ${good.replyText}
                                            </div>
                                        </li>
                                        </c:if>
				      					</c:if>
				      						<c:if test="${empty good.addCommentDate}">
				      					          <li class="clearfix addcomments">
                                            <span class="fl des_left">追加评论：</span>
                                            <div class="fl des_right">
                                                <div class="textarea_div">
                                                    <textarea class="textarea_text" placeholder="请您分享对商品的使用感受"></textarea>
                                                    <div class="textarea_tip">
                                                        	至少输入<span class="num">1-500</span>个字
                                                    </div>
                                                </div>
                                                <input type="hidden" class="token" value="true" />
                                                <input type="hidden" class="additionalPicUrls" id="picUrls">
                                                <div class="uploadimg">
                                                    <a class="btn_upload fl">
                                                       <input type="file" name="uploadFile" class="fileupload"/>电脑晒图
                                                        <input type="hidden" name="loadedimgurls" imageUrls="" bigImageUrls="" id="hasCommentPicUrls"/>
                                                        </a>
                                                    <span class="uploadtxt fl">限5张哦~</span>
                                                    <span class="loadedimg fl" name="loadedimg_list">
                                                    </span>
                                                    <span class="num fnhide"><em class="numk" name="numk">0</em>/<em class="total">5</em></span>
                                                </div>
                                                <p class="btn_div clearfix">
                                                    <button class="btn_pay fr">提交评论</button><a class="hiderate fr">收起追评↑</a>
                                                </p>
                                            </div>
                                        </li>
                                        <li class="addbtn clearfix">
                                            <span class="fl des_left">&nbsp;</span>
                                            <div class="fl des_right">
                                                <a class="btn-02">追加评论</a>
                                            </div>
                                        </li>
                                        </c:if>
                                        <c:if test="${not empty good.addCommentDate}">
                                        <ul class="add_comments">
                                        <li class="td_03 clearfix">
                                            <span class="fl des_left">追加评论：</span>
                                            <div class="fl des_right">
                                                <span class="text">${good.addCommentText}</span>
                                            </div>
                                        </li>
 										<c:if test="${not empty good.addComnentPicUrls}">
                                        <li class="clearfix">
                                            <span class="fl des_left">晒&nbsp;图：</span>
                                            <div class="fl des_right">
                                                <ul class="fn-comment-photos">
                                                <c:set value="${fn:split(good.addComnentPicUrls,';') }" var="addComnentPicUrls" />
                                                <c:forEach  items="${addComnentPicUrls}" var="addCommentPicUrl" varStatus="content">
                                                    <li class="">
                                                        <img src="${addCommentPicUrl}"  alt=""><b></b>
                                                     </li>
                                                 </c:forEach>
                                                </ul>
                                                <div class="fn-photos-view">
                                                    <img src="../assets/img/s2.jpg" alt="">
                                                    <a class="view-navleft target_no" href="javascript:;"><i></i></a>
                                                    <a class="view-navright target_no" href="javascript:;"><i></i></a>
                                                </div>
                                            </div>
                                        </li>
                                        </c:if>
                                        <li class="td_03 clearfix">
                                            <span class="fl des_left">追评时间：</span>
                                            <div class="fl des_right">
                                                <span class="time">${good.addCommentDate}</span>
                                            </div>
                                        </li>
                                        <c:if test="${not empty good.addCommentReplyText}">
                                       <c:if test="${good.storeType == 1 }">
                                        <li class="clearfix replay_li">
                                            <span class="fl des_left">客服回复：</span>
                                            <div class="fl des_right">
											${good.addCommentReplyText}
                                            </div>
                                        </li>
                                        </c:if>
                                        <c:if test="${good.storeType == 2 }">
                                        <li class="clearfix replay_li">
                                            <span class="fl des_left">商家回复：</span>
                                            <div class="fl des_right">
											${good.addCommentReplyText}
                                            </div>
                                        </li>
                                        </c:if>
                                        </c:if>
                                    </ul>
                                    </c:if>
				      				</ul>
	                    		</div>
	                    	</div>
	                   </c:forEach>
		      			</c:if>
		      			
		      			
		      			
		      		</div>
				</div>
				<div class="explanation">
					<h5 class="explanation_tips">评论说明</h5>
					<div class="explanation_cont">
						<p>1.评论是您对商品质量、服务水平、用后体验等所发表的意见和感受，您的宝贵建议是我们不断改进的动力；</p>
						<p>2.成功评价商品后您可以获得<span class="redtxt">飞牛积分</span>，加精置顶评论还有<span class="redtxt">双倍积分</span>奖励，详
见 <a class="jflink" href="http://sale.feiniu.com/help_center/hc-6.html" target="_blank">【积分规则】</a>；</p>
						<p>3.为了及时收到您的宝贵建议，请您在收到商品后的三个月之内发表评论，同一商品一次只能发表一条评论；</p>
						<p>4.订购商城商品，确认收货后三十日内若无评价记录，系统将默认好评；</p>
						<p>5.您可以在初次评论后的三个月内对已评论的商品追加评论，对同一个商品只能追加一条评论；</p>
						<p>6.您可对自营和商城商品做出的中评（2星和3星）、差评（1星）进行删除；</p>
						<p>7.删除评论后，评论记录无法恢复，也无法进行追评。</p>
						</div>
				</div>
			</div>
		</div>
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
		URL_MEMBER = "${basePath}",
		CSRF_TOKEN = '${CSRF_TOKEN}';
		
	require([static_domain + "/product/js/controller/member/config.js?version="+time_stamp], function() {
		
		require(["controller/member/myCommentView", "controller/member/leftMenu", "controller/shop/cart"],function(){ 	    		 
	    	
	    	require([static_domain+'/product/js/lib/upLogger.js', static_domain+'/product/js/lib/idigger.js'], function() {
        	 	//埋点
	    		upLogger.acceptLinkParams('1', '7032', '7');
	    		
	    		idigger && idigger.init();
           });
	    	
	    });
	});
	
</script>
</body>	
</html>