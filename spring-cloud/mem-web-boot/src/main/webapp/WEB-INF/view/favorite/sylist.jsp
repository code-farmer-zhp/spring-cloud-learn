﻿<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
								<div id="attr_452" class="m-tr">
									<div class="g-left">
										<p>店铺收藏</p>
									</div>
									<div class="g-right ">
										<div class="g-list">
											<ul class="f-list h76">
												<li><a href="javascript:void(0);" target="_blank" class="target_no"
												onclick="pagePropertyGo('','','sto','${basePath}favorite/propertyFavorite');"
												>全部(${category.data.cateCounts })
												</a></li>
												<c:set var="index" value="0" />
												<c:forEach items="${category.data.cates}" var="store"
													varStatus="content">
													<li><a href="javascript:void(0);"
														onclick="pagePropertyGo('${store.kindId }','','sto','${basePath}favorite/propertyFavorite');"
														kindId="${store.kindId }">${store.kindName }(${store.favoriteCount })
													</a>
													</li>
													<c:set var="index" value="${index+1}" />
												</c:forEach>
											</ul>
											<c:if test="${index > 13}">
											<div class="f-ext">
												<a class="f-more" data-attr="attr_452" href="javascript:;">更多<i></i></a>
											</div>
											</c:if>
										</div>
									</div>
								</div>								