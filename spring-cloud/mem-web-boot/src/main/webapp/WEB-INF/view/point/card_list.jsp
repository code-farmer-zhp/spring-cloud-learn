<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<title>积分换券明细</title>
	<link rel="shortcut icon" href="${staticDomain}/images/feiniu_favicon.ico" />
	<link rel="stylesheet" type="text/css" href="${staticDomain}/product/css_build/common.css?v=${version}"/>
	<link rel="stylesheet" href="${staticDomain}/product/css_build/member/point.css?v=${version}">
	<link href="${staticDomain}/js/lib/artdialog/skins/default.css?v=${version}" rel="stylesheet" type="text/css" />
    <link href="${staticDomain}/js/lib/artdialog/skins/art_skin_order.css?v=${version}" rel="stylesheet" type="text/css" />
</head>
<body>

${headerHtml}
<!---导航-->
${navHeaderHtml}

<div class="g-container">
    <div class="g-crumbs">
        <span><a href="${memberUrl}/member/home" class="crumbs_font target_no">我的飞牛</a></span> &gt; <span>我的积分</span> &gt; <span class="color">积分换券明细</span>
    </div>
    
    <div class="g-wrapper fixed">
    ${leftHtml }
	<div class="m-right">
			<div class="main coupon">
				<!-- themes star -->
				<div class="themes_title">
					<h3>积分换券明细</h3>
				</div>
				<!-- themes end -->

				<!-- detail-vouchers -->
				<div class="m-vou">
					<table cellpadding="0" cellspacing="0"  id='couponList'>
						<tr>
							<th class="t-on" class="t-tw" style="border-left:1px solid #ddd;">兑换单号</th>
							<th class="t-on">优惠券</th>
							<th class="t-on">券码</th>
							<th class="t-tw">使用期限</th>
							<th class="t-on">使用积分</th>
							<th class="t-on" style="border-right:1px solid #ddd;">兑换时间</th>
						</tr>
						<script type="text/template">
						<tbody>
						<tr>
							<td>{orderNo}</td>
							<td><img src="${storeUrl}{couponURL}" alt="" class="J_hover" width="60" height="60" data-len="1" data-name="{couponName}"></td>
							<td>{couponNo}</td>
							<td>{availableBeginTime}-{availableEndTime}</td>
							<td>{couponValue}</td>
							<td>{createTime}</td>
						</tr>
						</tbody>
						</script>
					</table>
				</div>
				<!-- End detail-vouchers -->
				<div class="fn_page clearfix">
				<script type="text/pageTemplate">  
                    <ul>
                        <li class="{fn_prve}">
                            <a class="target_no" href="{pre_href}">
                                <i class="arrow_prev"></i>
                                <span>上一页</span>
                            </a>
                        </li>
                        <li>
                            <span class="cur">{pageNo}</span>/<span class="all">{totalpage}</span>
                        </li>
                        <li class="{fn_next}">
                            <a class="target_no" href="{next_href}">
                                <span>下一页</span>
                                <i class="arrow_next"></i>
                            </a>
                        </li>
                        <li>
                            <span>到第</span>
								<input id="pagenum" name="pagenum" style="width:22px;height:14px;*+width:22px;*+height:14px;" type="text">
                            <span>页</span>
                        </li>
                        <li class="goto">
                            <a class="target_no" href="javascript:;" onclick="pageGo('pagenum','{goUrl}','{totalpage}','0')">跳转</a></li></ul>
                        </li>
                    </ul>
				</script>
                </div>
				<!-- End page -->
			</div>
		</div>
</div>
</div>

${footerHtml}

<script src="${staticDomain }/product/js_build/lib/requirejs/2.1.8/require.js"></script>
<script>

	var static_domain = "${staticDomain}",
		myUrl = "${myUrl}",
		ajaxData = "${ajaxData}",
		trigger = '${trigger}';
		
	require([static_domain + "/product/js/controller/member/config.js?version="+time_stamp], function() {
		
		require(["controller/member/pointCard", "controller/member/leftMenu", "controller/shop/cart"],function(){	    
	    	
	    	require([static_domain+'/product/js/lib/upLogger.js', static_domain+'/product/js/lib/idigger.js'], function() {
        	 	//埋点
	    		upLogger.acceptLinkParams('1','7030','7');
	    		idigger.init && idigger.init();
           });
	    	
	    });
	});

</script>
</body>
</html>