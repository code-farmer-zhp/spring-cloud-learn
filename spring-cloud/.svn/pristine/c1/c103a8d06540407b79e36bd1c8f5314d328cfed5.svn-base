<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test='${not empty memHasCommentCount.data}'>

						<div class="favorites_shop">
									<div class="mt">
										<label class="J_select_all fl"> <input type="checkbox"
											name="alldataselect" />全选
										</label> <a data-attr="1" class="delete deleteSelect fl target_no">删除</a>
										<!-- page next star -->
									<div class="fn_page">
										<ul>
											<li class="${pageDataBefore.fn_prve}"><a target="_blank"
												class="target_no J-pageGo-j" href="javascript:;"
												data-pgo='{"pagenum":"${pageDataBefore.pageNo}","pageurl":"${pageDataBefore.goUrl}","totalpage":"${pageDataBefore.totalpage}","kindId":"${kindId }","ids":"${ids }","type":"sto"}'><i
													class="arrow_prev"></i><span>上一页</span></a></li>
											<li><span class="cur">${pageDataBefore.pageNo}</span>/<span
												class="all">${pageDataBefore.totalpage}</span></li>
											<li class="${pageDataBefore.fn_next}"><a target="_blank"
												class="target_no J-pageGo" href="javascript:;"
												data-pgo='{"pagenum":"${pageDataBefore.pageNo}","pageurl":"${pageDataBefore.goUrl}","totalpage":"${pageDataBefore.totalpage}","kindId":"${kindId }","ids":"${ids }","type":"sto"}'><span>下一页</span><i
													class="arrow_next"></i></a></li>
										</ul>
									</div>
									<!-- page next end -->
									</div>
									<div class="mc" id="shopFavorites">

										<c:forEach items="${memHasCommentCount.data}" var="store"
											varStatus="content">
											<div class="shop_item clearfix">
												<div class="iteml fl clearfix">
													<input type="checkbox" class="J_goodsfav_cb fl"
														name="singledataselect" value="${store.id }" />
													<div class="shoptxt fl">
														<a class="img" href="${store.url }"> <img
															src="${store.storeLogoUrl}" /> <strong>${store.storeName }</strong>
														</a>
														<div class="shopdes">
															<p class="tit">
																<span class="fl">${store.storeGrades[0].label }：</span>
																<strong class="num fl">${store.storeGrades[0].score }</strong>
																<i class="triangle fr"></i>
															</p>
															<div class="shopdes_detail">
																<div class="in fixed">
																	<div class="fl">
																		<p class="hd">评分明细</p>
																		<p>${store.storeGrades[0].label }：
																			<span class="num">${store.storeGrades[0].score }</span>
																		</p>
																		<p>${store.storeGrades[1].label }：
																			<span class="num">${store.storeGrades[1].score }</span>
																		</p>
																		<p class="bd">${store.storeGrades[2].label }：
																			<span class="num">${store.storeGrades[2].score }</span>
																		</p>
																	</div>
																	<div class="fr">
																		<p class="hd">与同行业相比</p>
																		<p>
																			<c:if test='${store.storeGrades[0].offset > 0}'>
																				<span class="tips icon-up"> <i></i>
																			</c:if>
																			<c:if test='${store.storeGrades[0].offset < 0}'>
																				<span class="tips icon-down"> <i></i>
																			</c:if>
																			<c:if test='${store.storeGrades[0].offset == 0}'>
																				<span class="tips"> <i></i>
																			</c:if>
																			${store.storeGrades[0].offset } </span>
																		</p>
																		<p>
																			<c:if test='${store.storeGrades[1].offset > 0}'>
																				<span class="tips icon-up"> <i></i>
																			</c:if>
																			<c:if test='${store.storeGrades[1].offset < 0}'>
																				<span class="tips icon-down"> <i></i>
																			</c:if>
																			<c:if test='${store.storeGrades[1].offset == 0}'>
																				<span class="tips"> <i></i>
																			</c:if>
																			${store.storeGrades[1].offset }</span>
																		</p>
																		<p class="bd">

																			<c:if test='${store.storeGrades[2].offset > 0}'>
																				<span class="tips icon-up"> <i></i>
																			</c:if>
																			<c:if test='${store.storeGrades[2].offset < 0}'>
																				<span class="tips icon-down"> <i></i>
																			</c:if>
																			<c:if test='${store.storeGrades[2].offset == 0}'>
																				<span class="tips"> <i></i>
																			</c:if>
																			${store.storeGrades[2].offset }</span>
																		</p>
																	</div>
																</div>
															</div>
														</div>
														<a class="btn_enter" href="${store.url }">进入店铺</a> <a
															class="delete deleteSingle target_no" data-attr="0">删除</a>
													</div>
												</div>
												<div class="itemr fr">
													<ul class="ui_tab_nav">
												<c:if test='${not empty store.newProducts}'>
														<li class="active"><a>新品<i class="triangle"></i>
														</a></li>
											    </c:if>
													</ul>
													<div class="ui_tab_content">
														<div class="ui_panel newproduct" style="display: block">
															<c:if test='${not empty store.newProducts}'>
																<div class="in">
																	<c:forEach items="${store.newProducts}" var="newProduct" varStatus="content">
																		<c:if test="${content.count<=5}">
																			<div class="shop_detail fl">
																				<a class="" href="${newProduct.source_url}"
																				   target="_blank"> <img
																						src="${newProduct.it_pic}" /> <span>${newProduct.name}</span>
																				</a>
																				<p>
																					<strong class="price">${newProduct.price}</strong>
																						<%-- 	<span class="price_discount">${newProduct.market_price}</span> --%>
																				</p>
																			</div>
																		</c:if>
																	</c:forEach>

																</div>


															</c:if>
															   <div class="u-dis-act fixed">
			           											<div class="u-txt">促销活动</div>
			           											<div class="JDisSel u-sel">
			           											</div>
			           											<input type="hidden" class="merchantId" value="${store.merchantId}" />
					                                        	<ul class=" clearfix slider-main1" value=${store.merchantId}>
					                                        	</ul>
			           											</div>
															<c:if test='${empty store.newProducts}'>
																<p class="noproduct">暂无新品</p>
															</c:if>
														</div>

													</div>
															<div class="m-coupon slide JStoreCoupon">
												<input type="hidden" class="storeId" value="${store.merchantId}" />
	                                        	<ul class=" clearfix slider-main" value=${store.merchantId}>
	                                        	</ul>
	                                        </div>
												</div>
											</div>
										</c:forEach>

									</div>



								<div class="mb">
									<label class="J_select_all fl"> <input type="checkbox"
										name="alldataselect" />全选
									</label> <a data-attr="1" class="delete deleteSelect fl target_no">删除</a>
										<!-- page next star -->
									<div class="fn_page clearfix">
									<input type="hidden" id="prod_page_num" current="${pageDataBefore.pageNo}" pgcount="${pageDataBefore.totalpage}" />
										<ul>
											<li class="${pageDataBefore.fn_prve}"><a target="_blank"
												class="target_no J-pageGo-j" href="javascript:;"
												data-pgo='{"pagenum":"${pageDataBefore.pageNo}","pageurl":"${pageDataBefore.goUrl}","totalpage":"${pageDataBefore.totalpage}","kindId":"${kindId }","ids":"${ids }","type":"sto"}'><i
													class="arrow_prev"></i><span>上一页</span></a></li>
											<li><span class="cur">${pageDataBefore.pageNo}</span>/<span
												class="all">${pageDataBefore.totalpage}</span></li>
											<li class="${pageDataBefore.fn_next}"><a target="_blank"
												class="target_no J-pageGo" href="javascript:;"
												data-pgo='{"pagenum":"${pageDataBefore.pageNo}","pageurl":"${pageDataBefore.goUrl}","totalpage":"${pageDataBefore.totalpage}","kindId":"${kindId }","ids":"${ids }","type":"sto"}'
												><span>下一页</span><i
													class="arrow_next"></i></a></li>
											<li><span>到第</span><input id="pagenum" name="pagenum"
												maxlength="100" type="text" style="width: 30; height: 22"><span>页</span></li>
											<li class="goto"><a target="_blank" href="javascript:;"
												class="target_no J-pageGo"
												data-pgo='{"pagenum":"pagenum","pageurl":"${pageDataBefore.goUrl}","totalpage":"${pageDataBefore.totalpage}","kindId":"${kindId }","ids":"${ids }","type":"sto"}'>跳转</a></li>
										</ul>
									</div>
									<!-- page next end -->
								</div>
								</div>

								</c:if>
							