<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>   
				<c:if test='${not empty memHasCommentCount.data}'>
								<!-- list title star -->
								<div class="favorites_title">
									<ul>
										<li class="th_01">商品</li>
										<li class="th_02">单价</li>
										<li class="th_03">库存</li>
										<li class="th_04">操作</li>
									</ul>
								</div>
								<!-- list title end -->

								<div id="top" class="operating fixed">
									<label class="J_select_all"> <input type="checkbox"
										value="" name="">全选
									</label> <a href="javascript:void(0);"
										class="j_add_mult btn_add target_no">加入购物车</a> <a
										href="javascript:void(0);"
										class="d_del_mult btn_delete target_no" data-attr="1">删除</a>
									<!-- page next star -->
									<div class="fn_page clearfix">
									<input type="hidden" id="prod_page_num" current="${pageDataBefore.pageNo}" pgcount="${pageDataBefore.totalpage}" />
										<ul>
											<li class="${pageDataBefore.fn_prve}"><a target="_blank"
												class="target_no J-pageGo-j" href="javascript:;"
												data-pgo='{"pagenum":"${pageDataBefore.pageNo}","pageurl":"${pageDataBefore.goUrl}","totalpage":"${pageDataBefore.totalpage}","kindId":"${kindId }","ids":"${ids }","type":"prod"}'><i
													class="arrow_prev"></i><span>上一页</span></a></li>
											<li><span class="cur">${pageDataBefore.pageNo}</span>/<span
												class="all">${pageDataBefore.totalpage}</span></li>
											<li class="${pageDataBefore.fn_next}"><a target="_blank"
												class="target_no J-pageGo" href="javascript:;"
												data-pgo='{"pagenum":"${pageDataBefore.pageNo}","pageurl":"${pageDataBefore.goUrl}","totalpage":"${pageDataBefore.totalpage}","kindId":"${kindId }","ids":"${ids }","type":"prod"}'
												><span>下一页</span><i
													class="arrow_next"></i></a></li>										
										</ul>
									</div>
									<!-- page next end -->
								</div>
								<div class="favorites_content" id="favList">
									<c:forEach items="${memHasCommentCount.data}" var="store" varStatus="content">
										<c:if test='${store.off == false}'>
											<ul class="clearfix">
												<li class="td_01">
													<div class="item clearfix">
														<!-- 预售 组合 低价 -->
														<input type="checkbox" value="${store.id}" name="" class="check J_goodsfav_cb"
															<c:if test='${store.isPreSale == 1 or store.is_combine == 1}'>disabled="disabled"</c:if>
															<c:if test='${store.is_combine == 1 }'>combine="1"</c:if>
															<c:if test='${store.isPreSale != 1  and store.is_combine != 1 and store.avl_qty >0}'>market=${store.sell_no}</c:if>
															prod_type=${store.type} _price=${store.price} />
															
														<div class="item_img">
														    <!-- 自营商品 -->
															<c:if test='${store.type == 0 }'>
																<c:set var="string1" value="${store.it_pic}" />
																<a href="${store.source_url}" target="_blank"><img
																	src="${imgInside }${fn:replace(string1, '.', '_80x80.')}"
																	height="80" width="80" alt=""></a>
															</c:if>
															<!-- 商城商品 -->
															<c:if test='${store.type == 1 }'>
																<a href="${store.source_url}" target="_blank"><img src="${store.it_pic}" height="80" width="80" alt=""></a>
															</c:if>
														</div>
														<div class="item_des">
															<a href="${store.source_url}" class="order_name"
																target="_blank"><c:if test = '${store.isGroup == 1 }'><span class="m-bkicn">团购</span></c:if> ${store.name}</a>
															   	<c:if test='${not empty store.activitys.activity_url}'>
																	<p class="order_active" style="display: block; white-space: nowrap; text-overflow: ellipsis; overflow: hidden;width:250px">
																		<a href="${store.activitys.activity_url }" target="_blank">${store.activitys.activity_name}</a>
																	</p>
																</c:if>
																<c:if test='${store.priceDifference > 0}'>
																<span class="order_discount"> <span>降</span>比收藏时降¥<em>${store.priceDifference}</em>元 </span>
																</c:if>
														</div>
													</div>
												</li>
												<c:if test="${store.isPreSale == 1}">
													<li class="td_02 num">
														<em class="rmb">¥</em>${store.price}
													</li>
													<li class="td_03">预售</li>
													<li class="td_04 last"><span class="btn_no_add">加入购物车</span>
														<a href="javascript:void(0);"
														class="d_del_sing btn_delete target_no" data-attr="0">删除</a></li>
												</c:if>
												<c:if test="${store.isPreSale !=  1}">
													<c:if test="${store.price <= '0'}">
														<li class="td_02 num">
														    <em class="rmb">¥</em>${store.price}
														</li>
														<li class="td_03">0元商品</li>
														<li class="td_04 last"><span class="btn_no_add">加入购物车</span>
															<a href="javascript:void(0);"
															class="d_del_sing btn_delete target_no" data-attr="0">删除</a>
														</li>
													</c:if>
													<c:if test="${store.price > '0'}">
														<c:if test="${store.avl_qty > 3}">
															<li class="td_02 available">
															<c:if test='${not empty store.tag}'>
																	<p class="num price">
																	<em class="rmb">¥</em><fmt:formatNumber value="${store.price}" pattern="##.##" minFractionDigits="0" /> 
																	</p>
																	<p class="price_des">${store.tag}</p>
															</c:if> 
															<c:if test='${empty store.tag}'>
																  <em class="rmb">¥</em><fmt:formatNumber value="${store.price}" pattern="##.##" minFractionDigits="0" /> 
															</c:if>
															</li>
															<li class="td_03">有货</li>
															<li class="td_04 last">
															<c:choose>
														     <c:when test="${store.isPreSale == 1}">
								                                <a href="javascript:void(0);" class="j_add_sing btn_no_add target_no">加入购物车</a> 
								                             </c:when>
								                             <c:otherwise>
								                              <a href="javascript:void(0);" class="j_add_sing btn_add target_no">加入购物车</a> 
								                             </c:otherwise>
								                            </c:choose>
															  <a href="javascript:void(0);" class="d_del_sing btn_delete target_no" data-attr="0">删除</a>
															</li>
														</c:if>
														<c:if test="${store.avl_qty <= 3 and store.avl_qty > 0}">
															<li class="td_02 available">
															<c:if test='${not empty store.tag}'>
																	<p class="num price">
																	   <em class="rmb">¥</em><fmt:formatNumber value="${store.price}" pattern="##.##" minFractionDigits="0" /> 
																	</p>
																	<p class="price_des">${store.tag}</p>
															</c:if> 
															<c:if test='${empty store.tag}'>
																  <em class="rmb">¥</em><fmt:formatNumber value="${store.price}" pattern="##.##" minFractionDigits="0" /> 
															</c:if>
															</li>
															<li class="td_03">
																<p class="tip">有货</p>
																<p class="tip_des">仅剩${store.avl_qty }件</p>
															</li>
															<li class="td_04 last">
														    <c:choose>
															 <c:when test="${store.isPreSale == 1}">
															 <a href="javascript:void(0);" class="j_add_sing btn_no_add target_no">加入购物车</a>
														     </c:when>
								                             <c:otherwise>
								                             <a href="javascript:void(0);" class="j_add_sing btn_add target_no">加入购物车</a> 
								                             </c:otherwise>
								                            </c:choose>
															<a href="javascript:void(0);" class="d_del_sing btn_delete target_no" data-attr="0">删除</a>
															</li>
														</c:if>
														<c:if test="${store.avl_qty <= 0}">
															<li class="td_02 num">
																 <em class="rmb">¥</em>${store.price}
															</li>
															<li class="td_03">无货</li>
															<li class="td_04 last"><span class="btn_no_add">加入购物车</span>
																<a href="javascript:void(0);" class="d_del_sing btn_delete target_no" data-attr="0">删除</a>
															</li>
														</c:if>
													</c:if>
												</c:if>
											</ul>
										</c:if>
                                        <!-- 已下架 -->
										<c:if test="${store.off == true}">
											<ul class="last goods_shelves clearfix">
												<li class="td_01">
													<div class="item clearfix">
														<input type="checkbox" value="${store.id }" name="" class="check J_goodsfav_cb"
															   disabled="disabled"
															<c:if test='${store.is_combine == 1 }'>combine="1"</c:if>
															prod_type=${store.type } _price=${store.price } />
															
														<div class="item_img">
														    <!-- 自营商品 -->
															<c:if test='${store.type == 0 }'>
																<c:set var="string1" value="${store.it_pic}" />
																<a href="${store.source_url}" target="_blank"><img
																	src="${imgInside }${fn:replace(string1, '.', '_80x80.')}"
																	height="80" width="80" alt=""></a>
															</c:if>
															<!-- 商城商品 -->
															<c:if test='${store.type == 1 }'>
																<a href="${store.source_url}" target="_blank"><img src="${store.it_pic}" height="80" width="80" alt=""></a>
															</c:if>
														</div>
														
														<div class="item_des">
															<span class="order_name">${store.name}</span>
															<c:if test='${store.priceDifference >0}'>
															<span class="order_discount"> <span>降</span>比收藏时降¥<em>${store.priceDifference}</em>元 </span>
															</c:if>
														</div>
													</div>
												</li>
												<li class="td_02 num"><em>¥</em>${store.price}</li>
												<li class="td_03"><span class="shelves_tip">已下架</span></li>
												<li class="td_04 last"><span class="btn_no_add">加入购物车</span>
													<a href="javascript:void(0);" class="d_del_sing btn_delete target_no" data-attr="0">删除</a>
												</li>
											</ul>
										</c:if>
									</c:forEach>

								</div>

								<div id="bottom" class="operating fixed">
									<label class="J_select_all"> <input type="checkbox"
										value="" name="">全选
									</label> <a href="javascript:void(0);"
										class="j_add_mult btn_add target_no">加入购物车</a> <a
										href="javascript:void(0);"
										class="d_del_mult btn_delete target_no" data-attr="1">删除</a>
									<div class="ui_poptip ui_poptip_pop">
										<div class="ui_poptip_container">
											<div class="ui_poptip_arrow">
												<em></em> <span></span>
											</div>
											<div class="ui_poptip_content"></div>
										</div>
									</div>
								<!-- page next star -->
									<div class="fn_page clearfix">
									<input type="hidden" id="prod_page_num" current="${pageDataBefore.pageNo}" pgcount="${pageDataBefore.totalpage}" />
										<ul>
											<li class="${pageDataBefore.fn_prve}"><a target="_blank"
												class="target_no J-pageGo-j" href="javascript:;"
												data-pgo='{"pagenum":"${pageDataBefore.pageNo}","pageurl":"${pageDataBefore.goUrl}","totalpage":"${pageDataBefore.totalpage}","kindId":"${kindId }","ids":"${ids }","type":"prod"}'><i
													class="arrow_prev"></i><span>上一页</span></a></li>
											<li><span class="cur">${pageDataBefore.pageNo}</span>/<span
												class="all">${pageDataBefore.totalpage}</span></li>
											<li class="${pageDataBefore.fn_next}"><a target="_blank"
												class="target_no J-pageGo" href="javascript:;"
												data-pgo='{"pagenum":"${pageDataBefore.pageNo}","pageurl":"${pageDataBefore.goUrl}","totalpage":"${pageDataBefore.totalpage}","kindId":"${kindId }","ids":"${ids }","type":"prod"}'
												><span>下一页</span><i
													class="arrow_next"></i></a></li>
											<li><span>到第</span><input id="pagenum" name="pagenum"
												maxlength="100" type="text" style="width: 30; height: 22"><span>页</span></li>
											<li class="goto"><a target="_blank" href="javascript:;"
												class="target_no J-pageGo"
												data-pgo='{"pagenum":"pagenum","pageurl":"${pageDataBefore.goUrl}","totalpage":"${pageDataBefore.totalpage}","kindId":"${kindId }","ids":"${ids }","type":"prod"}'>跳转</a></li>
										</ul>
									</div>
									<!-- page next end -->
								</div>
				</c:if>