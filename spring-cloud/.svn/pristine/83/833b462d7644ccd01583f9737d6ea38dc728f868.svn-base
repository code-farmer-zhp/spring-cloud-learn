<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/picUrl.tld" prefix="picUrl"%>
<%@ taglib uri="/WEB-INF/strDateTag.tld" prefix="strDateTag"%>
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
</head>
<body>
${headerHtml}
<!---导航-->
${navHeaderHtml}

<div class="g-container">
		<!-- bread crumbs star -->
        <div class="g-crumbs">
            <span><a href="${memberUrl}/member/home" class="target_no">我的飞牛</a></span>&gt; <span class="color">我的评价</span>
        </div>
        <!-- bread crumbs end -->
		<!-- col main star -->
		<div class="g-wrapper fixed">
			${leftHtml}
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
						<li><a class="target_no" href="${basePath}comment/myCommentView">待评论订单</a></li>
						<li class="last active"><a class="target_no" href="${basePath}comment/hasCommentView">已评论<span class="num">(${comment_list.totalRows})</span></a></li>
					</ul>
					<div class="ui_tab_content">
	                    <div class="ui_panel" id="commented" style="display:block;">
							<div class="success_mes" id="successMe">
				      			<i class="icon_success"></i>
				      			<p>您已完成<span class="num"></span>件商品的追加评论，感谢您对飞牛网的支持！</p>
				      			<a href="${basePath}comment/hasCommentView">查看我的评价</a>
				      		</div>
					<c:if test='${not empty comment_list.dataList}'>
					     <div class="evaluation_cont m-bord" id="noComment">
					     <div class="evaluation_title">
                                    <ul class="clearfix">
                                        <li class="col_w15 th_01">商品</li>
                                        <li class="col_w20 th_02">评分</li>
                                        <li class="col_w50 th_02">评价</li>
                                        <li class="col_w15 th_03 m-tion">操作 <i class="J-anwicon"></i> 
                                            <div class="m-boxi J-anwcont" style="display: none;">初评后的三个月内可追加评论，已好评则不可修改，中差评可进行删除。</div>
                                        </li>
                                    </ul>
                          </div>
					<c:forEach  items="${comment_list.dataList}" var="comment" varStatus="content">
			      		<div class="m-lst-pro clearfix J-lt-pro">
                                    <ul>
                                        <li class="col_w15">
                                            <p class="m-lns">
                                             <c:if test="${comment.virtualGood == false}">
				      							 <a href="${storeDomainUrl}/${comment.skuId}">
				      				        </c:if>
                                             <c:if test="${comment.virtualGood == true}">
				      							  <a >
				      				        </c:if>
                                            <img src=" <picUrl:pic1  picUrl='${comment.picUrl}' imgInsideUrl='${imgInsideUrl}' type='${comment.storeType}' storeDomainUrl='${storeUrl}' size='80x80' />" alt="${comment.goodsName}"></a></p>
                                        </li>
                                        <li class="col_w20">
                                            <div class="m-cen">
                                                <div class="star clearfix" data-star="${comment.starLevel}">
                                                    <i class="star_01"></i>
                                                    <i class="star_01"></i>
                                                    <i class="star_01"></i>
                                                    <i class="star_01"></i>
                                                    <i class="star_01"></i>
                                                </div>
                                            </div>
                                        </li>
                                        <li class="col_w50">
                                            <div class="single_review clearfix">
                                                <div class="goods_commented">
                                                    <ul class="first_comments">
                                                        <li class="clearfix">
                                                        <c:set value="${ fn:split(comment.productMark, ',') }" var="impress_label" />
                                                        <c:if test='${not empty comment.productMark}'>
                                                            <div class="des_right">
								      							<c:forEach items="${impress_label}" var="impress" >
								      							<c:if test='${not empty impress}'>
								      							<span class="tag">${impress}</span>
								      							</c:if>
									      						</c:forEach>
                                                            </div>
                                                            </c:if>	
                                                        </li>
                                                        <li class="clearfix">
                                                            <p class="m-hei">${comment.commentText}</p>
                                                        </li>
                                                        <c:if test="${not empty comment.commentPicUrls}">
                                                        <c:set value="${fn:split(comment.commentPicUrls, ';') }" var="commentPicUrls" />
                                                        <li class="clearfix">
                                                               <div class="des_right">
                                                                <ul class="fn-comment-photos">
					                                                	 <c:forEach  items="${commentPicUrls}" var="commentPicUrl" varStatus="content">
					                                                    <li class="">
					                                                        <img src="${commentPicUrl}" alt=""><b></b>
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
                                                            <p>${comment.commentDate}</p>
                                                        </li>
                                                       <c:if test="${not empty comment.replyText}">
									      				<c:if test="${comment.storeType == 1 }">
									      					<li class="clearfix replay_li">
					                                            <span class="fl des_left">客服回复：</span>
					                                            <div class="fl des_right m-w87">
					                                                ${comment.replyText}
					                                            </div>
					                                        </li>
					                                     </c:if>
					                                     <c:if test="${comment.storeType == 2 }">
									      					<li class="clearfix replay_li">
					                                            <span class="fl des_left">商家回复：</span>
					                                            <div class="fl des_right m-w87">
					                                                ${comment.replyText}
					                                            </div>
					                                        </li>
					                                     </c:if>
									      				</c:if>
                                                    </ul>
                                                  
                                                   <c:if test="${not empty comment.addCommentDate}">
                                                    <ul class="add_comments">
                                                        <li class="clearfix">
                                                            <span class="fl des_left">追加评论：</span>
                                                            <p class="fl m-wdss">${comment.addCommentText}</p>
                                                        </li>
                                                        	<c:if test="${not empty comment.addComnentPicUrls}">
                                                            <c:set value="${ fn:split(comment.addComnentPicUrls, ';') }" var="addComnentPicUrls" />
						                                        <li class="clearfix">
						                                            <div class="des_right">
                                                                        <ul class="fn-comment-photos">
						                                                <c:forEach  items="${addComnentPicUrls}" var="addCommentPicUrl" varStatus="content">
						                                                    <li class="">
						                                                        <img src="${addCommentPicUrl}" alt=""><b></b>
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
                                                            <p>${comment.addCommentDate}</p>
                                                        </li>
                                                                 <c:if test="${not empty comment.addCommentReplyText}">
							                                     	<c:if test="${comment.storeType == 1 }">
							                                        <li class="clearfix replay_li">
							                                            <span class="fl des_left">客服回复：</span>
							                                            <div class="fl des_right m-w87">
							                                            dasd
																		${comment.addCommentReplyText}
							                                            </div>
							                                        </li>
							                                        </c:if>
							                                        <c:if test="${comment.storeType == 2 }">
							                                        <li class="clearfix replay_li">
							                                            <span class="fl des_left">商家回复：</span>
							                                            <div class="fl des_right m-w87">
							                                            dasdsa
																		${comment.addCommentReplyText}
							                                            </div>
							                                        </li>
							                                        </c:if>
							                                        </c:if>
                                                    </ul>
                                                    </c:if>
                                                </div>
                                            </div>                                            
                                        </li>
                                        <li class="col_w15">
                                            <div class="m-tion-v">
                                                <input id="id" name="id" type="hidden" value="${comment.commentId}">
                                                <c:if test="${comment.starLevel <= 3}">
                                                <p><a href="javascript:;" class="J-deleval target_no">删除评价</a></p>
                                                </c:if>
                                                 <c:if test="${comment.canAddComment == true and empty comment.addCommentDate}">
                                                 <strDateTag:date  value='${comment.commentDate}'>
                                                <p><a href="javascript:;" class="J_addcomment target_no">追加评论</a></p>
                                                </strDateTag:date>
                                                </c:if>
                                            </div>
                                        </li>
                                    </ul>
                                         <c:if test="${comment.canAddComment == true and empty comment.addCommentDate}">
				      			            <strDateTag:date  value='${comment.commentDate}'>
                                             <li class="clearfix m-ev-tion">
                                             <div class="list_release addcomments clearfix J-lt-pl">
			                                    <div class="top_line">
			                                     <input id="id" name="id" type="hidden" value="${comment.commentId}">
			                                        <div class="ui_poptip_arrow poptip_up">
			                                            <em></em>
			                                            <span></span>
			                                        </div>
			                                    </div>
                                            <span class="fl des_left">追加评论：</span>
                                            <div class="fl des_right">
                                            	<div class="comm_box">
                                            	  <dl class="clearfix">
                                                	<dd class="dd_03 clearfix">
                                                <div class="textarea_div">
                                                    <textarea class="textarea_text" placeholder="请您分享对商品的使用感受" style="width:732px;"></textarea>
                                                    <div class="textarea_tip">
                                                        	至少输入<span class="num">1-500</span>个字
                                                    </div>
                                                </div>
                                                </dd>
                                                </dl>
                                                <input type="hidden" class="token" value="true" />
                                                <input type="hidden" class="additionalPicUrls" id="picUrls">
                                                <div class="uploadimg">
                                                    <a class="btn_upload fl">
                                                        <input type="file" name="uploadFile" class="fileupload"/>电脑晒图
                                                        <input type="hidden" name="loadedimgurls" imageUrls="" bigImageUrls="" id="hasCommentPicUrls"/>
                                                        </a>
                                                    <span class="uploadtxt fl">限5张哦~</span>
                                                    <span class="loadedimg fl" name="loadedimg_list"></span>
                                                    <span class="num fnhide"><em class="numk" name="numk">0</em>/<em class="total">5</em></span>
                                                </div>
                                                <div class="m-pay-pkg btn_div clearfix">
                                                    <button class="btn_pay fr">提交评论</button><a class="J-toggle m-zp fr">收起追评↑</a>
                                                </div> 
                                            </div>
                                            </div>
                                            </div>                                            
                                        </li>
                                          </strDateTag:date>
                                        </c:if>                                        
                                </div>     
                                <div class="d-clear"></div>                           
	                   </c:forEach>
	                   </div>
	                   <!-- page next star -->
                            <div class="fn_page clearfix">
                                	<ul>
							<li class="${pageDataBefore.fn_prve}"><a target="_blank" class="target_no"
								onclick="self.location.href='${pageDataBefore.pre_href}'"><i
									class="arrow_prev"></i><span>上一页</span></a></li>
							<li><span class="cur">${pageDataBefore.pageNo}</span>/<span class="all">${pageDataBefore.totalpage}</span></li>
							<li class="${pageDataBefore.fn_next}"><a target="_blank" class="target_no"
								onclick="self.location.href='${pageDataBefore.next_href}'"><span>下一页</span><i
									class="arrow_next"></i></a></li>
							<li><span>到第</span><input id="pagenum" name="pagenum"
								maxlength="100" type="text" style="width: 30; height: 22"><span>页</span></li>
							<li class="goto"><a target="_blank" href="javascript:;"
								class="target_no"
								onclick="pageGo('pagenum','${pageDataBefore.goUrl}','${GridType}');">跳转</a></li>
						    </ul>
                            </div>
							<!-- page next end -->
		      			</c:if>
							<c:if test='${empty comment_list.dataList}'>
							<p class="text20" style="text-align:center"><br>您目前没有已评论的商品</p><br><br><br><br><br><br>
							</c:if>
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
		</div>
   </div>
</div>
${footerHtml}
 <!-- 删除评论 -->
    <div id="del-eval" style="display:none;">
        <div class="m-eval-del">是否确认删除评论,该条评论记录删除后不可恢复</div>
    </div>

    <!-- 评论失败 -->
    <div id="fail-pl" style="display:none;">
        <div class="m-fail">
            <h4>评价失败</h4>
            <p>您输入的内容包含敏感词汇“xx”、“xxx”,请重新填写</p>
        </div>
    </div>
    
<script src="${staticDomain }/product/js_build/lib/requirejs/2.1.8/require.js"></script>
<script>

	var static_domain = "${staticDomain}",
		trigger = '${trigger}',
		URL_MEMBER = "${basePath}",
		CSRF_TOKEN = "${CSRF_TOKEN}";
		
	require([static_domain + "/product/js/controller/member/config.js?version="+time_stamp], function() {
		
		require(["controller/member/myCommentView", "controller/member/leftMenu", "controller/shop/cart"],function(){ 	    	    
	    	
	    	require([static_domain+'/product/js/lib/upLogger.js', static_domain+'/product/js/lib/idigger.js'], function() {
        	 	//埋点
	    		upLogger.acceptLinkParams('1', '7016', '7');
	            idigger && idigger.init();
           });
	    	
	    });
	});
	
</script>
</body>
</html>