<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
	<meta name="format-detection" content="email=no">
	<meta name="apple-mobile-web-app-capable" content="yes">
	<meta name="apple-touch-fullscreen" content="yes">
	<meta name="apple-mobile-web-app-status-bar-style" content="black">
	<meta name="applicable-device" content="mobile">
	<meta name="format-detection" content="telephone=no">
	<script>if(/MSIE (6.0|7.0|8.0)/.test(navigator.userAgent)) { location.href = location.protocol + '//m.feiniu.com/nonsupport.html'; }   </script>
	<title>飞牛网-我的足迹</title>
	<link rel="Shortcut Icon" type="images/x-icon" href="${mStaticUrl}/assets/images/favicon.ico">
	<link rel="stylesheet" type="text/css" href="${mStaticUrl}/assets/css/common/common.css?v=${version}">
	<link rel="stylesheet" type="text/css" href="${mStaticUrl}/assets/css/my/track.css?v=${version}">
</head>

<body>
<div class="outbox track J_track_warp">
	<!-- 头部 S-->
	<div class="top_box">
		<span class="top_back J_topback"></span>
		<h1>浏览足迹</h1>
		<span class="more J_edit">编辑</span>
	</div>

	<div class="w_list">
		<div class="empty_activity J_blank hide">
			<div class="blank">
				<img src="${mStaticUrl}/assets/images/default/blank_icon.png" class="blank_img">
				<p class="blank_txt">暂无浏览历史</p>
				<a href="${mUrl}/seckill/index2.html" class="blank_btn">逛逛秒杀</a>
			</div>
		</div>
		<div class="date_warp_list J_date_warp_list">
			<div class="date_warp J_date_warp" data-date="" id="detail" start="" dataCount="" getData="0">
				<!-- 日期 S -->
				<script id="tpl_date" type="text/html" data-insert-pos="before">
					<div class="date_warp J_date_warp">
						<div class="date">
							<div class="check_box_div_title">
								<div class="new_copy J_main_package">
								</div>
							</div>
							<div class="context">{{=it.weekName}}</div>
						</div>
						<ul class="list_ul" id="{{=it.groupName}}"></ul>
					</div>
				</script>
				<!-- 日期 E -->
				<!-- 列表 S -->
				<script id="tpl_item" type="text/html" data-insert-pos="before">
					<li class="J_goDetail" data-count="ind" data-smseq="{{=it.sell_no}}">
						<div class="check_box_div">
							<!--加上 ‘acitve’  为选中状态-->
							<div class="new_copy J_main_item" data-id="{{=it.sell_no}}"></div>
						</div>
						<div class="d_img">
							<a class="J_item" href="${mItemUrl}/{{=it.sell_no}}" data-id="{{=it.sell_no}}" data-index="{{=it.index}}">
								<img class="" data-src="{{=it.it_pic}}" alt="{{=it.name}}" title="{{=it.name}}"
									 data-imgsrc="{{=it.it_pic}}" src="{{=it.it_pic}}">
							</a>
							<!--商品已下架-->
							{{? it.off === true }}
								<div class="sold_out">已下架</div>
							{{?}}
						</div>
						<div class="d_adc Btop">
							<a class="J_item" data-id="{{=it.sell_no}}" data-index="{{=it.index}}" href="${mUrl}/item/{{=it.sell_no}}">
								<div class="d_title prefix">
									<i><img alt="{{=it.typeName}}" title="{{=it.typeName}}" src="{{=it.typeNameUrl}}"></i>
									<p> {{=it.name}}</p>
								</div>
							</a>
							<!-- 其他一些杂项 S -->
							<div class="d_mix">
								<div class="dm_1">
									<div class="tg">
									<!-- 至多显示2个标签 -->
									</div>
									<!-- 价格 S -->
									<div class="d_price" id="price_{{=it.sell_no}}" >
										{{? it.isMobilePrice === 1}}
											<em>¥</em>{{=it.mobilePrice}}
										{{??}}
											<em>¥</em>{{=it.price}}
										{{?}}
									</div>
								</div>
								<div class="similarity">
									<a class="J_same" data-id="{{=it.sell_no}}" data-index="{{=it.index}}" href="${mUrl}/cart/sameGoods.html?s={{=it.sell_no}}">找相似</a>
								</div>
							</div>
							<p></p>
							<p class="J_delivery" data-code="1"></p>
						</div>
					</li>
				</script>
				<!-- 列表 E -->

				<script id="tpl_Activity" type="text/html" data-insert-pos="before">
					{{? it}}
						{{~ it:value:index}}
							{{? index < 2}}
								<i style="background-color:#ffffff; color:#e60012; border:1px solid #e60012">{{=value}}</i>
							{{?}}
						{{~}}
					{{?}}
				</script>
			</div>
		</div>
	</div>
	<p class="loading_pop hide_it"><b></b></p>
	<p class="loading_btm"><b></b><i>正在努力加载...</i></p>
	<!--没有更多商品-->
	<div class="nomoregoods hide">
		<div class="niuniu"><img src="${mStaticUrl}/assets/images/my/niuniu.png"></div>
		<p>亲，没有更多了哦~</p>
	</div>
	<!--底部删除-->
	<div class="track_total normal_pattern">
		<div>
			<div class="new_copy J_main_all">
			</div>
			全选
		</div>
		<div class="edit_box">
			<!--btn_red   删除按钮点亮-->
			<div class="btn btn_de btn_lg J_delete">删除</div>
		</div>
	</div>
</div>
<%--${footHtml}--%>
<%@include file="../common/touch/script.jsp"%>
<script>
	require([static_domain + "/assets/js/config.js?v=" + ${version}], function() {
		require(["controller/member/track"]);
	});
</script>
</body>
</html>
