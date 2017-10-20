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
	<link rel="stylesheet" type="text/css" href="${staticDomain}/product/css_build/member/mycomments.css?v=${version}"/>
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
            <span><a href="${memberUrl}/member/home" class="target_no">我的飞牛</a></span>&gt;<span class="color">我的评价</span>
        </div>
        <!-- bread crumbs end -->
        <div class="g-wrapper fixed">
            <!--会员中心导航菜单-->
         ${leftHtml}
            <!--会员中心导航菜单 /-->
            <div class="m-right">
            	<div class="m-main">
            		<div class="m-tle">我的评价</div>
	           <c:if test='${not empty goodList}'>
	              <c:forEach items="${goodList}" var="good" varStatus="status">
                    <!-- cmtProduct -->
                    <div class="m-cmtpro m-tp10">
                        <div class="m-cmtpic m-fl">
                        <c:if test="${good.virtualGood == false}">
				      	  <a target="_blank" href="${storeDomainUrl}/${good.skuSeq}"  >
				      	</c:if>
				      	<c:if test="${good.virtualGood == true}">
				          <a target="_blank">
				      	</c:if>
                            <img src="<picUrl:pic1  picUrl='${good.picUrl}' imgInsideUrl='${imgInsideUrl}' type='${good.supplierType}' storeDomainUrl='${storeUrl}' size='200x200' />" alt="">
                          </a>
                            <p>${good.name}</p>
                        </div>
                        <div class="m-cmtchk m-fl">
                            <div class="fixed m-btm15">
                                <p class="m-t m-fl"><i>*</i> 评分：</p>
                                <div class="m-c m-fl">
                                    <p class="m-xx J-plxj" data-num="0">
                                        <i></i>
                                        <i></i>
                                        <i></i>
                                        <i></i>
                                        <i></i>
                                    </p>
                                    <p class="m-tt J-pltt J-pltt-comment" style="visibility: hidden;">你的评分对我很重要哟</p>
                                </div>
                                <div class="m-cpsl J-cpsl" ></div>
                            </div>
                            <div class="fixed m-btm15">
                                <p class="m-t m-fl">印象：</p>
                                <div class="m-c m-fl">
                                    <div class="m-tag fixed J-taglist">
                                        <ul>
                                        <c:forEach items="${good.labels}" var="goodLabels" varStatus="statusLabels">
                                            <c:if test="${statusLabels.count ==1}">
                                            <li class="active">
                                            </c:if>
                                             <c:if test="${statusLabels.count !=1}">
                                            <li>
                                            </c:if>
                                                <i></i> ${goodLabels}
                                                <input type="hidden" value="${goodLabels}"/>
                                            </li>
                                         </c:forEach>
                                            <li class="m-nt J-mnt">
                                                <i></i>自定义
                                            </li>
                                            <li class="m-ntt J-mntt">
                                                <input type="text" value="1~5个字" maxlength="5">
                                                <span class="J-ntsbm">提交</span>
                                                <span class="J-ntcel">取消</span>
                                            </li>
                                        </ul>
                                    </div>
                                    <p class="m-tt J-pltt">给商品选个印象嘛~</p>
                                </div>
                            </div>
                            <div class="fixed m-btm15">
                                <p class="m-t m-fl"><i>*</i> 评价：</p>
                                <div class="m-c m-fl">
                                    <div class="m-area">
                                        <textarea class="J-textare">商品是否给力？快分享你的购买心得吧~</textarea>
                                        <p class="m-areat J-size">请输入<i>1-500</i>字</p>
                                    </div>
                                   <div class="m-upload J-delpic"  next="${status.count}">
                                        <ul>
                                        </ul>
                                        <p>
                                            <label>
                                            <input type="file" class="J-upload" name="uploadFile">
                                            </label>
                                        </p>
                                        <p><span>0</span>/5</p>
                                    </div>
                                </div>
                            </div>
                            <div class="m-opcmt">
                                <p class="m-tt J-pltt" style="visibility: hidden;">请至少输入1-500个字</p>
                                <label><input type="checkbox" class="anonymous"> 匿名评价</label>
                            </div>
                        </div>
                           <div class="submit_review" >
                                    <input type="hidden" name="loadedimgurls" imageUrls="" />
									<input type="hidden" class="market_id" value="${good.skuSeq}" />
									<input type="hidden" class="order_id" value="${good.ono}" />
									<input type="hidden" class="ol_seq" value="${good.itemId}" />
									<input type="hidden" class="type" value="${good.supplierType}" />
									<input type="hidden" class="package_no" value="${good.packageId}" />
									<input type="hidden" class="sup_seq" value="${good.supSeq}" />
		      		       </div>
                    </div>
                    </c:forEach>
            	</c:if>
            	</div>
                <!-- store_comment start-->
                <div class="m-main m-tp10" style="display:none;" id="store_comment">
                    <h4 class="m-ttbld"><b>满意度评价</b> 为了让我们做得更好，请评价我们的服务哦~</h4>
                    <div class="m-mydpj fixed">
                        <div class="m-spnm m-fl">
                        </div>
                        <div class="m-pxprs m-fl J-pxprs">
                            <ul>
                                <li>
                                    <span>商品描述</span>
                                    <p class="J-plxj" data-num="0" data-str="pd">
                                        <i></i>
                                        <i></i>
                                        <i></i>
                                        <i></i>
                                        <i></i>
                                    </p>
                                </li>
                                <li>
                                    <span>服务态度</span>
                                    <p class="J-plxj" data-num="0" data-str="dv">
                                        <i></i>
                                        <i></i>
                                        <i></i>
                                        <i></i>
                                        <i></i>
                                    </p>
                                </li>
                                <li>
                                    <span>物流速度</span>
                                    <p class="J-plxj" data-num="0" data-str="db">
                                        <i></i>
                                        <i></i>
                                        <i></i>
                                        <i></i>
                                        <i></i>
                                    </p>
                                </li>
                            </ul>
                            <div class="m-uxps J-uxps">
                                <h4>5分 很满意</h4>
                                <i class="i1"></i>
                                <i class="i2"></i>
                            </div>
                        </div>
                        <div class="m-pjmtx m-fl" style="display:none;">请对满意度进行评价</div>
                    </div>
                </div>
                <!-- store_comment end-->
                <div class="m-btnmit">
                    <button>发表评论</button>
                    <label>
                        <input type="checkbox" class="anonymity"/>
                                                            一键好评
                    </label>
                </div>
				<div class="m-tips m-main m-tp10">
                    <h4>评论说明</h4>
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
  <div id="o-onekey" style="display: none;">亲，使用一键好评可助你轻松获取积分和成长值，商品默认五星，评论内容已帮您预设，确认使用请点击“提交”</div>
   <!-- 评论失败 -->
    <div id="fail-pl" style="display:none;">
        <div class="m-fail">
            <h4>评价失败</h4>
            <p>您输入的内容包含敏感词汇“xx”、“xxx”,请重新填写</p>
        </div>
    </div>
${footerHtml}

<script src="${staticDomain}/product/js_build/lib/requirejs/2.1.8/require.js"></script>
    <script>
    var static_domain = "${staticDomain}",
	trigger = '${trigger}',
	URL_MEMBER = "${basePath}",
	CSRF_TOKEN = '${CSRF_TOKEN}';
	
        require([static_domain + "/product/js/controller/member/config.js?version="+time_stamp], function() {
        	require(["controller/member/leftMenu", "controller/shop/cart", "controller/member/myComments"],function(){
        
    	    	require([static_domain+'/product/js/lib/upLogger.js', static_domain+'/product/js/lib/idigger.js'], function() {
            	 	//埋点
    	    		upLogger.acceptLinkParams('1', '7031', '7');
    	    		idigger && idigger.init();
               });
    	    	
    	    });
    	    	
    	  });
    </script>
</body>    
</html>