<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<title>我的积分</title>
	<link rel="shortcut icon" href="${staticDomain}/images/feiniu_favicon.ico" />
	<link rel="stylesheet" type="text/css" href="${staticDomain}/product/css_build/common.css?v=${version}"/>
	<link rel="stylesheet" href="${staticDomain}/product/css_build/member/point.css?v=${version}">
	<link href="${staticDomain}/product/js/lib/DatePicker/skin/WdatePicker.css?v=${version}" rel="stylesheet" type="text/css" />
	<link href="${staticDomain}/js/lib/artdialog/skins/default.css?v=${version}" rel="stylesheet" type="text/css" />
	<link href="${staticDomain}/js/lib/artdialog/skins/art_skin_order.css?v=${version}" rel="stylesheet" type="text/css" />
</head>
<body>

${headerHtml}
<!---导航-->
${navHeaderHtml}

<div class="g-container">
    <div class="g-crumbs">
         <span><a href="${memberUrl}/member/home" class="crumbs_font target_no">我的飞牛</a></span> &gt; <span class="color">我的积分</span>
    </div>
    
    <div class="g-wrapper fixed">
    
	${leftHtml }
   <div class="m-right">
        <div class="main integral">
            <div class="themes_title">
                <h3>我的积分</h3>
                <a href="http://sale.feiniu.com/help_center/hc-6.html" class="blue">积分规则</a>
            </div>
            <div class="message">
                <ul class="clearfix">
                    <li>
                        <div class="lidiv">
                            <p>目前可用积分：<span class="red num">${userScoreInfo.availabeScore}</span></p>
                            <a class="btn-01" name="goto" href="${vipUrl}/draw.html">积分兑换</a>
                        </div>
                    </li>
                    <li>
                        <div class="lidiv">
                            <p>待生效积分：<span class="num djnum">${userScoreInfo.lockedScore}</span></p>
                            <span class="txt">（待生效期</span><span id="showTip" class="what showTip">?
                            <span class="what_info"><i class=""></i>完成付款10日后转为可用积分</span>
                            </span><span class="txtsec">）</span>
                        </div>
                    </li>
                    <li class="last">
                        <div class="lidiv">
                            <p>即将过期的积分：<span class="num djnum">${userScoreInfo.expiringScore}</span></p>
                            <span class="txt">（${userScoreInfo.expiringTime}过期</span><span id="" class="what showTip">?
                            <span class="what_info"><i></i>过期了就作废了，赶紧去花掉吧</span>
                            </span><span class="txtsec">）</span>
                        </div>
                    </li>
                </ul>
            </div>
            
			<div class="ui_tab">
                <ul class="ui_tab_nav ui_tab_inav">
                    <li class="${activeType0}"><a href="${myUrl}/point/pointlist?type=0" class="target_no">积分明细</a></li>
                    <li class="${activeType1}"><a href="${myUrl}/point/pointlist?type=1" class="target_no">积分累积</a></li>
                    <li class="last ${activeType2}"><a href="${myUrl}/point/pointlist?type=2" class="target_no">积分消费</a></li>                    <li class="no ifilter">
                        <div class="isearch">
                            <input type="text" id="orderno" placeholder="输入订单号查询" value="${orderno}" /><a class="ibtn"><i></i></a>
                        </div>
                        <span>时间：</span><input readonly value="${searchStart}" id="start" type="text" class="Wdate txt" onclick="WdatePicker()" /><span class="gtxt">至</span><input readonly value="${searchEnd}" id="end" type="text" class="Wdate txt" onclick="WdatePicker()" />
                        <a class="iquery">查询</a></li>
                </ul>
                <div class="ui_tab_content">
                    <div class="ui_panel" style="display: block;">
                        <div class="ui_tab">
                            <ul class="fl">
                                <li class="ui_tab_navtit"><a>快捷筛选：</a></li>
                            </ul>
                            <ul class="ui_tab_nav ui_tab_fnav">
                                <li class="${activeFrom0}"><a href="${myUrl}/point/pointlist?type=${type}&from=0" class="target_no">全部</a></li>
                                <li class="${activeFrom1}"><a href="${myUrl}/point/pointlist?type=${type}&from=1" class="target_no">购物</a></li>
                                <c:if test="${type!=2}">
                                <li class="${activeFrom2}"><a href="${myUrl}/point/pointlist?type=${type}&from=2" class="target_no">评论</a></li> 
                                </c:if>  
                                <li class="${activeFrom3} navlayer">
	                                <c:if test="${phoneBindScoreDB==0 &&phoneBindMemberStatus!=0}">
	                                	<p class="layer">送<span class="red num">20积分</span><i></i></p>
	                                </c:if>
	                                <c:if test="${phoneBindScoreDB==1 ||(phoneBindScoreDB==0 &&phoneBindMemberStatus!=0) }">
	                                <a href="${myUrl}/point/pointlist?type=${type}&from=3" class="target_no">绑定手机</a>
	                                </c:if>
                                </li>
                                <li class="${activeFrom4} navlayer">
	                                <c:if test="${emailBindScoreDB==0 &&emailBindMemberStatus!=0 }">
	                                	<p class="layer">送<span class="red num">20积分</span><i></i></p>
	                                </c:if>
	                                <c:if test="${emailBindScoreDB==1 ||(emailBindScoreDB==0 &&emailBindMemberStatus!=0) }">
	                                	<a href="${myUrl}/point/pointlist?type=${type}&from=4" class="target_no">绑定邮箱</a> 
	                                </c:if>  
                                </li> 
                                <c:if test="${type!=2}">
                                <li class="${activeFrom5}"><a href="${myUrl}/point/pointlist?type=${type}&from=5" class="target_no">签到</a></li>                  
                             	</c:if>
                                <li class="${activeFrom6}"><a href="${myUrl}/point/pointlist?type=${type}&from=6" class="target_no">抽奖</a></li> 
                                <c:if test="${type!=1}">
                                <li class="${activeFrom7}"><a href="${myUrl}/point/pointlist?type=${type}&from=7" class="target_no">积分兑换</a></li>                  
                             	</c:if>
                             	 <li class="${activeFrom8}"><a href="${myUrl}/point/pointlist?type=${type}&from=8" class="target_no">飞牛赠送</a></li> 
                                <li class="${activeFrom9}"><a href="${myUrl}/point/pointlist?type=${type}&from=9" class="target_no">其他</a></li>                  
                             </ul>
                            <div class="ui_tab_content ui_tab_fcontent">
                                <div id="loading" style="display: block; text-align: center"><img src="${staticDomain}/img/loading.gif"> &nbsp;&nbsp;载入中，请稍等</div>
                                <div id="dlist" style="display: none">
                                    <div class="details_list">
                                        <div class="details_list_tit">
                                            <ul>
                                                <li class="col_w15">积分</li>
                                                <li class="col_w10">类型</li>
                                                <li class="w370">详情</li>
                                                <li class="col_w18">时间</li>
                                                <li class="col_w18">备注</li>
                                            </ul>
                                        </div>
                                        <div class="details_list_cont"></div>
                                    </div>
                                 </div>
                                <c:if test="${emailBindScoreDB==0 &&emailBindMemberStatus!=0 }">
                                <div class="ui_panelsec" id="mlist" style="display: none">
                                    <div class="ui_panelin">
                                        <p class="tit">您尚未绑定邮箱</p>
                                        <p class="txt">首次绑定邮箱，即送<span class="num red">20</span>积分。绑定邮箱可大大提升账户安全，可用于找回密码，接受订单通知等。</p>
                                        <a href="${safeUrl}/safetySettings/view" class="btn_rel">立即绑定</a>
                                    </div>
                                </div>
                                </c:if>
                                <c:if test="${phoneBindScoreDB==0 &&phoneBindMemberStatus!=0}">
                                <div class="ui_panelsec" id="plist" style="display: none">
                                    <div class="ui_panelin">
                                        <p class="tit">您尚未绑定手机</p>
                                        <p class="txt">首次绑定手机，即送<span class="num red">20</span>积分。您即可享受飞牛丰富的手机服务。</p>
                                        <a href="${safeUrl}/safetySettings/view" class="btn_rel">立即绑定</a>
                                    </div>
                                </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
		<div class="fn_page clearfix">
		</div>
        </div>
    </div>
</div>
</div>
${footerHtml}
<script src="${staticDomain }/product/js_build/lib/requirejs/2.1.8/require.js"></script>
<script>

	var static_domain = "${staticDomain}",
	    trigger = "${trigger}",
	    myurl = "${myUrl}",
	    ajaxData = "${ajaxData}",
	    searchUrl = "${searchUrl}",
	    wwwUrl = "${wwwUrl}",
	    storeDomain = "${storeDomain}",
	    imgBaseUrl = "${imgBaseUrl}",
	    storeUrl = "${storeUrl}",
	    memberUrl = "${memberUrl}";
	
	require([static_domain + "/product/js/controller/member/config.js?version="+time_stamp], function() {
		
	    require(["controller/member/point", "controller/member/leftMenu", "controller/shop/cart"],function(){
	    	
	    	require([static_domain+'/product/js/lib/upLogger.js', static_domain+'/product/js/lib/idigger.js'], function() {
        	 	//埋点
	    		upLogger.acceptLinkParams('1','7017','7');
	    		$("a.btn-01").on("click",function(e) {
	    		    upLogger.acceptEventParams($(this), '', '2', '7039', '7', e);
	    		});
	    		//add by liping 0624 埋点
	    		/**普通页**/
	    	    idigger.init && idigger.init();
           });
	    	
	    });
	});
		
</script>
</body>
</html>