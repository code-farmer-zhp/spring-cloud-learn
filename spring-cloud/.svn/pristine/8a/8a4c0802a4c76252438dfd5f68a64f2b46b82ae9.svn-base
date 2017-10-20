<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <meta name="format-detection" content="email=no">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-touch-fullscreen" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="applicable-device" content="mobile">
    <meta name="format-detection" content="telephone=no">
    <script>if (/MSIE (6.0|7.0|8.0)/.test(navigator.userAgent)) {
        location.href = location.protocol + '//m.feiniu.com/nonsupport.html';
    }   </script>
    <title>飞牛网-我的收藏</title>
    <link rel="stylesheet" type="text/css" href="${mStaticUrl}/assets/css/common/common.css?v=${version}">
    <link rel="stylesheet" type="text/css" href="${mStaticUrl}/assets/css/my/favorites.css?v=${version}">
    <link rel="Shortcut Icon" type="images/x-icon" href="${mStaticUrl}/assets/images/favicon.ico">
</head>
<body>

<div class="outbox">
    <!-- 顶部 -->
    <div class="top_box">
        <span class="top_back J_topback"></span>
        <div class="hide editor_goods">编辑</div>
        <ul class="collect_ul clearfix">
            <li class="goods_scroll" data-list="1"><a href="${mMyUrl}/touch/favorite/index?status=0">商品</a></li>
            <li class="mall_scroll" data-list="2"><a href="${mMyUrl}/touch/favorite/index?status=1">店铺</a></li>
        </ul>
        <div class="search_inp hide">
            <i class="icon iconfont a_icon">&#xe60f;</i>
            <input type="text" class="search_bar autocomplete" value="" placeholder="搜索您收藏的商品" autocomplete="off">
            <span class="X_cancel"><span class="X">X</span></span>
        </div>
        <div class="search_cancel needsclick hide">取消</div>
        <div class="set_list J_search_go hide">搜索</div>
        <i class="icon iconfont search_icon">&#xe60f;</i>
        <span class="right_btn J_editButton">编辑</span>
    </div>

    <!-- 商品收藏 -->
    <div class="tab_1 tab_list">
        <div id="J_category_area">
            <script type="text/x-dot-template" id="tpl_category">
                <ul class="ul_list">
                    <li data-list='0' class="cur_font quanbu first" data-cateType={{=it[0].cateType}}
                        data-favoriteCount={{=it[0].favoriteCount}}>{{=it[0].kindName}}
                    </li>
                    <li data-list='1' class="second" data-cateType={{=it[1].cateType}}
                        data-favoriteCount={{=it[1].favoriteCount}} data-kindId={{=it[1].kindId}}>{{=it[1].kindName}}
                    </li>
                    <li data-list='2' class="second" data-cateType={{=it[2].cateType}}
                        data-favoriteCount={{=it[2].favoriteCount}} data-kindId={{=it[2].kindId}}>{{=it[2].kindName}}
                    </li>
                    <li data-list='3' class="second" data-cateType={{=it[3].cateType}}
                        data-favoriteCount={{=it[3].favoriteCount}} data-kindId={{=it[3].kindId}}>{{=it[3].kindName}}
                    </li>
                    <li class="fl_all"><span class="li_last_span">{{=it[4].kindName}}</span><i
                            class="icon iconfont colect_font"></i></li>
                </ul>
                <div class="drop_list hide">
                    <ul class="clearfix drop_ul">
                        {{~ it[4].cates:value:i}}
                        {{? i == 0}}
                        <li class="cur_font drop_ul_first" data-number="{{=i}}" data-cateType={{=value.cateType}}
                            data-favoriteCount={{=value.favoriteCount}} data-list='5' data-kindId={{=value.kindId}}>
                            {{=value.kindName}}&nbsp;&nbsp;({{=value.favoriteCount}})&nbsp;&nbsp;<i
                                class="icon iconfont">&#xe602;</i></li>
                        {{??}}
                        <li data-number="{{=i}}" data-cateType={{=value.cateType}}
                            data-favoriteCount={{=value.favoriteCount}} data-list='5' data-kindName={{=value.kindName}}
                            data-kindId={{=value.kindId}}>{{=value.kindName}}&nbsp;&nbsp;({{=value.favoriteCount}})&nbsp;&nbsp;<i
                                class="icon iconfont hide">&#xe602;</i></li>
                        {{?}}
                        {{~}}
                    </ul>
                </div>
            </script>
        </div>

        <!-- 类目数据加载时遮罩层 -->
        <div class="mask hide"></div>
        <!-- 大数据搜索时遮罩层 -->
        <div class="search_mask needsclick hide"></div>
        <div class="sclist J_collectList">
            <script type="text/x-dot-template" id="tpl_renderCollect">
                {{~it:value:i}}
                <div data-smseq="{{=value.sell_no}}" data-smname="{{=value.name}}" data-smpic="{{=value.sm_pic}}"
                     class="J_favoritesRow wrapper">
                    <p class="check_box J_check">
                        <input type="checkbox" class="com_checkbox J_checkBox" value={{=value.id}}/>
                    </p>
                    <p id="addCart{{=value.sell_no}}" class="scimg J_gotoDetail">
                        <img src="{{=value.touch_pic}}" alt="{{=value.name}}" title="{{=value.name}}"/>
                        {{? value.priceDifference > 0 }}
                        <span class="off_sale">比收藏时降价{{=value.priceDifference}}元</span>
                        {{?}}
                    </p>
                    <div class="sc_box">
                        <p class="scname J_gotoDetail">
                            <!-- 1 自营,2 商城 ,3 商家直送,4 环球购 -->
                            <span><img src="{{=value.rlink}}"></span>
                            {{=value.name}}
                        </p>
                        <!--  0:加入购物车 1:立即预定 2:立即抢购 3:买立减 4:售完补货中 5:售完补货中 6:预购结束 7:抢购一空 9:立即预约 10:预约结束 11:立即抢购(预约商品)-->
                        {{? !value.off && (value.status == 1 || value.status == 2 || value.status == 11 || value.status
                        == 12 || value.status == 13 || value.status == 14 || value.status==10)}}
                        <div class="cart_box">
                            <div class="ydcondion" style="margin-bottom: .28rem;">
                                <p class="cart use">
                                    <i class="icon iconfont J_normalButton J_addCartShop"
                                       data-smseq="{{=value.sell_no}}" data-ismall="{{=value.type}}"
                                       data-saletype="{{=value.status}}" data-price="{{=value.price}}"
                                       data-isorgiitem="{{=value.is_combine}}" data-ispre="{{=value.isPreSale}}"
                                       data-merchantId="{{=value.merchantId}}">&#xe659;</i>
                                </p>
                                <p class="price" style="display:inline;">
                                    {{? value.isIntegral == 1}}
                                    <span class="z_pric" style="padding-right: 0.1rem;">
                                            &yen;<b style="font-size: 0.40rem;">{{=value.integralProductPrice}}+{{=value.integralProductPoints}}</b>积分
                                        {{??}}
                                        <span class="z_pric" style="padding-right: 0.1rem;">
                                            &yen;<b style="font-size: 0.40rem;">{{=value.price}}</b>
                                        {{?}}
                                        </span>
                                    {{~ value.tags:smallTag:goodindex}}
                                        <span class="va fs5"
                                              style="background-color:#ffffff;color:#e60012;padding:0 0.02rem;font-size:0.2rem;">{{=smallTag}}</span>
                                    {{~}}
                                </p>
                            </div>
                            <div class="depreciate_goods" data-smseq="{{=value.sell_no}}" data-smname="{{=value.name}}"
                                 data-smpic="{{=value.touch_pic}}" data-smprice="{{=value.price}}">降价通知
                            </div>
                        </div>
                        {{?? !value.off && (value.status == 7)}}
                        <div class="qh_box">
                            <div class="ydcondion">
                                <span class="f_price">
                                &yen;<b>{{=value.price}}</b>
                                </span>
                                {{~ value.tags:smallTag:goodindex}}
                                <span class="va fs5"
                                      style="background-color:#ffffff;color:#e60012;padding:0 0.02rem;font-size: 0.2rem;">{{=smallTag}}</span>
                                {{~}}
                            </div>
                            <i class="J_normalButton a_bottom">售完补货中</i>
                            <div class="like_goods" data-smseq="{{=value.sell_no}}">找相似</div>
                            <div class="arrival_goods" data-smseq="{{=value.sell_no}}" data-smname="{{=value.name}}"
                                 data-smpic="{{=value.touch_pic}}" data-smprice="{{=value.price}}">到货通知
                            </div>
                        </div>
                        {{?? !value.off && (value.status == 4 || value.status == 3)}}
                        <div class="qh_box">
                            <div class="ydcondion">
                                <span class="f_price">
                                &yen;<b>{{=value.price}}</b>
                                </span>
                                {{~ value.tags:smallTag:goodindex}}
                                <span class="va fs5"
                                      style="background-color:#ffffff;color:#e60012;padding:0 0.02rem;font-size: 0.2rem;">{{=smallTag}}</span>
                                {{~}}
                            </div>
                            <i class="J_normalButton a_bottom">即将开卖</i>
                            <div class="like_goods" data-smseq="{{=value.sell_no}}">找相似</div>
                            <div class="arrival_goods" data-smseq="{{=value.sell_no}}" data-smname="{{=value.name}}"
                                 data-smpic="{{=value.touch_pic}}" data-smprice="{{=value.price}}">到货通知
                            </div>
                        </div>
                        {{?? value.off}}
                        <div class="qh_box">
                            <div class="ydcondion">
                                <span class="f_price">
                                &yen;<b>{{=value.price}}</b>
                                </span>
                                {{~ value.tags:smallTag:goodindex}}
                                <span class="va fs5"
                                      style="background-color:#ffffff;color:#e60012;padding:0 0.02rem;font-size: 0.2rem;">{{=smallTag}}</span>
                                {{~}}
                            </div>
                            <i class="J_normalButton a_bottom">已下架</i>
                            <div class="like_goods" data-smseq="{{=value.sell_no}}">找相似</div>
                        </div>
                        {{?? !value.off && value.status == 9}}
                        <div class="qh_box">
                            <div class="ydcondion">
                                <span class="f_price">
                                &yen;<b>{{=value.price}}</b>
                                </span>
                                {{~ value.tags:smallTag:goodindex}}
                                <span class="va fs5"
                                      style="background-color:#ffffff;color:#e60012;padding:0 0.02rem;font-size: 0.2rem;">{{=smallTag}}</span>
                                {{~}}
                            </div>
                            <i class="J_normalButton a_bottom">抢购一空</i>
                            <div class="like_goods" data-smseq="{{=value.sell_no}}">找相似</div>
                            <div class="arrival_goods" data-smseq="{{=value.sell_no}}" data-smname="{{=value.name}}"
                                 data-smpic="{{=value.touch_pic}}" data-smprice="{{=value.price}}">到货通知
                            </div>
                        </div>
                        {{?? !value.off && value.status == 8}}
                        <div class="ys_end_box">
                            <div class="ydcondion" style="margin-bottom: 0.2rem;">
                                <span class="f_price">
                                 &yen;<b>{{=value.price}}</b>
                                </span>
                                {{~ value.tags:smallTag:index}}
                                <span class="va fs5"
                                      style="background-color:#ffffff;color:#e60012;padding:0 0.02rem;font-size:0.2rem;">{{=smallTag}}</span>
                                {{~}}
                            </div>
                            <div class="depreciate_goods" data-smseq="{{=value.sell_no}}" data-smname="{{=value.name}}"
                                 data-smpic="{{=value.touch_pic}}" data-smprice="{{=value.price}}">降价通知
                            </div>
                            <i class="J_normalButton a_bottom"
                               style="border:1px solid #e57f98;color:#C20053;background-color: #ffefef;height:0.44rem;font-size:0.22rem;padding-right:0.03rem;padding-left:0.03rem;">立即预定</i>
                        </div>
                        {{?? !value.off && value.status == 15}}
                        <div class="ys_end_box">
                            <div class="ydcondion" style="margin-bottom: 0.2rem;">
                                <span class="f_price">
                                 &yen;<b>{{=value.price}}</b>
                                </span>
                                {{~ value.tags:smallTag:index}}
                                <span class="va fs5"
                                      style="background-color:#ffffff;color:#e60012;padding:0 0.02rem;font-size:0.2rem;">{{=smallTag}}</span>
                                {{~}}
                            </div>
                            <div class="depreciate_goods" data-smseq="{{=value.sell_no}}" data-smname="{{=value.name}}"
                                 data-smpic="{{=value.touch_pic}}" data-smprice="{{=value.price}}">降价通知
                            </div>
                            <i class="J_normalButton a_bottom"
                               style="border:1px solid #e57f98;color:#C20053;background-color: #ffefef;height:0.44rem;font-size:0.22rem;padding-right:0.03rem;padding-left:0.03rem;">查看详情</i>
                        </div>
                        {{?}}
                    </div>
                </div>
                {{~}}
            </script>
        </div>

        <p class="loading_btm J_favoritesLoading hide_it"><b></b><i></i>正在努力加载...</i></p>
        <div class="nomoregoods J_nullPage_goods hide_it">
            <div class="niuniu"><img src="${mStaticUrl}/assets/images/niuniu.png"></div>
            <p>亲，没有更多了哦~</p>
        </div>

        <!--账户没有任何收藏商品时候显示-->
        <div style="display: none;" class="blank J_blank">
            <img src="${mStaticUrl}/assets/images/default/blank_icon.png" class="blank_img" alt=""/>
            <p class="blank_txt">您还没有收藏过任何商品<br/>让牛牛带您去逛逛~</p>
            <a href="${mSeckillUrl}" class="blank_btn">逛逛秒杀</a>
        </div>

        <!--账户本身有商品但点击某个筛选时候没有商品时显示-->
        <div style="display: none;" class="blank click_blank">
            <img src="${mStaticUrl}/assets/images/default/blank_icon.png" class="blank_img" alt=""/>
            <p class="blank_txt">抱歉没有找到符合条件的商品</p>
        </div>

        <div class="footer_nav J_footerNav">
            <span class="J_removeAll"></span><i id="del_btn" class="J_removeFavorites">取消收藏</i>
        </div>
    </div>

    <!-- 店铺收藏 -->
    <div class="tab_2 tab_list hide" style="padding-top:1rem;">
        <div id="J_mall_area">
            <script type="text/x-dot-template" id="tpl_Mallcategory">
                <ul class="mallcategory_ul">
                    {{~ it:value:i}}
                    {{? i == 0}}
                    <li data-mallNum={{=i}} class="cur_font mall_first" data-kindId={{=value.kindId}}>
                        {{=value.kindName}}
                    </li>
                    {{??}}
                    <li data-mallNum={{=i}} class="mall_second" data-kindId={{=value.kindId}}>{{=value.kindName}}</li>
                    {{?}}
                    {{~}}
                </ul>
            </script>
        </div>
        <div class="mall_collectList">
            <script type="text/x-dot-template" id="tpl_collectList">
                {{~it:value:i}}
                <div class="store">
                    <div class="Info" data-storeurl="{{=value.url}}">
                        <div class="storeImg">
                            <img src="{{=value.storeLogoUrl}}"/>
                        </div>
                        <div class="storeList">
                            <p class="storeName">{{=value.storeName}}</p>
                            <p class="storeCollect">
                                {{? value.couponAmount > 0}}<span class="getCode ticket_click"
                                                                  data-merchantid="{{=value.merchantId}}"
                                                                  data-storename="{{=value.storeName}}">领券</span>{{?}}<span
                                    class="cancel_mall" data-idcancel="{{=value.id}}">取消收藏</span>
                            </p>
                        </div>
                    </div>
                    {{? value.activities && value.activities.length > 0}}
                    <div class="storeActivity divcontent">
                        <p class="storeTitle">店铺活动<span>({{=value.activitySize}})</span><i
                                class="icon iconfont arrow arrow_a">&#xe621;</i></p>
                        {{~ value.activities:val_act:k}}
                        <div class="activityContent hide">
                            <p><span>{{=val_act.type_tags[0].name}}</span>{{=val_act.title}}<a
                                    href="{{=val_act.url}}"><em data-seq="{{=val_act.campSeq}}" class="right_a"><i
                                    class="icon iconfont">&#xe60b;</i></em></a></p>
                        </div>
                        {{~}}
                    </div>
                    {{?}}
                    {{? value.newProductSize > 0}}
                    <div class="new">
                        <p class="storeTitle">上新<span>({{=value.newProductSize}})</span><i
                                class="icon iconfont arrow arrow_b">&#xe621;</i></p>
                        <div class="newcontent swiper-container">
                            <div class="swiper-wrapper">
                                {{~ value.newProducts:val_product:m}}

                                <div class="swiper-slide goods_sw" data-urlgoods="{{=val_product.source_url}}">
                                    <a href="{{=val_product.source_url}}">
                                        <div class="newpic">
                                            <img src="{{=val_product.it_pic}}">
                                        </div>
                                        <p class="fd_C" style="text-align: center;">
                                            <em></em>{{=val_product.price}}<b></b></p>
                                    </a>
                                </div>

                                {{~}}
                            </div>
                        </div>
                    </div>
                    {{?}}
                </div>
                {{~}}
            </script>
        </div>
        <p class="loading_btm J_loading hide_it"><b></b><i>正在努力加载...</i></p>
        <div class="nomoregoods hide_it J_mall">
            <div class="niuniu"><img src="${mStaticUrl}/assets/images/niuniu.png"></div>
            <p>亲，没有更多了哦~</p>
        </div>
        <div class="blank J_nullPage hide_it">
            <img src="${mStaticUrl}/assets/images/default/blank_icon.png" class="blank_img"/>
            <p class="blank_txt">您还没有收藏过任何店铺<br/>让牛牛带您去逛逛~</p>
            <a href="${mSeckillUrl}" class="blank_btn J_homepage">逛逛秒杀</a>
        </div>
        <!--账户本身有店铺但点击某个筛选时候没有店铺时显示-->
        <div style="display: none;" class="blank mall_blank">
            <img src="${mStaticUrl}/assets/images/default/blank_icon.png" class="blank_img" alt=""/>
            <p class="blank_txt">抱歉没有找到符合条件的店铺</p>
        </div>
        <!--卡券弹框 S-->
        <div class="popup_coupons hide">
            <script type="text/x-dot-template" id="tpl_mallTicket">
                <div class="tickets_box" data-tpl="tpl_mallTicket">
                    <p class="title">优惠券</p>
                    {{~ it.couponList:val:i}}
                    <div class="ticket_type">
                    <span class="tips_title">
                        <i class="iconfont">&#xe617;</i>
                        {{=val.scope_description}}
                    </span>
                    </div>
                    <ul class="tickets_list business">
                        <li class="business">
                            <div class="placeholder">
                                <div class="lable">
                                    <p style="background-color: {{=val.voucherColor}};"></p>
                                    {{? val.status == -1 }}
                                    <span class="type_lw">全部领完</span>
                                    {{?? val.status == 1 }}
                                    <span class="type_lw">已领取</span>
                                    {{??}}
                                    <span class="type_tick"
                                          style="color: {{=val.voucherColor}};border: 1px solid {{=val.voucherColor}};"
                                          data-statu="{{=val.status}}" data-vaseq="{{=val.id}}">立即领取</span>
                                    <img src="{{=val.picUrl}}" class="receive_status J_receive_status hide"/>
                                    {{?}}
                                </div>
                                <div class="content">
                                    <div class="left">
                                        <div>
                                            <p class="fd_a" style="color: {{=val.voucherColor}};"><em></em>{{=val.amount}}
                                            </p>
                                            <span style="color: {{=val.voucherColor}};">{{=val.title}}</span>
                                        </div>
                                        <p class="l_use">{{=val.threshold}}</p>
                                        <p class="l_apply">{{=val.scope_description}}</p>
                                    </div>
                                </div>
                                <div class="validity">
                                    {{=val.validTime}}
                                </div>
                            </div>
                        </li>
                    </ul>
                    {{~}}
                </div>
            </script>
            <div class="close_btn2">
                <a href="javascript:;">关闭</a>
            </div>
        </div>
        <!--卡券弹框 E-->
    </div>
</div>
<!-- 加载隐藏 -->
<p class="loading_pop all J_loading_pop" style="display:none;"><b></b></p>
<!-- 两个按钮 -->
<div class="popbg J_showRemoveAll" id="one" style="display:none;">
    <div class="popbox">
        <p class="poptxt"></p>
        <div class="popbtnbox">
            <p class="J_removeAll">确定</p>
            <p onclick="$(this).closest('.popbg').hide();">取消</p>
        </div>
    </div>
</div>
<jsp:include page="../common/touch/script.jsp"/>
<script>
    var getCouponApi = "${getCouponApi}";
    var cart_api_url = "${buyFn}";
    require([static_domain + "/assets/js/config.js?v=" + time_stamp], function () {
        require(['controller/member/favorites']);
    });
</script>
</body>
</html>