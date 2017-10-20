<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
                <div class="t-b-b">

                    <p class="t-b-p">已领取礼包</p>
                    <div id="J_layer_yi" class="t-b-c t-b-n">
	                    <c:choose>
	                    <c:when test="${takenResult== null || takenResult.data == null||takenResult.data.totalItems == 0}">
	                        <p class="t-c-p"  style="display:block;"><em></em><span>亲，这里什么都还没有呢~~~</span></p>
	                    </c:when>
	                    <c:otherwise>
	                        <dl class="m-grd m-grd-lq">
	                        	<c:forEach items="${takenResult.data.pkadList}" var="pkad">
	                            <dd>
	                                <a href="#">
	                                    <p class="m-lbg m-lb-${mrstuiForBgimg[pkad.mrstUi]} }"></p>
	                                    <p class="m-grd-f">${pkad.mrstUiDesc}</p>
	                                    <p class="m-grd-d">${fn:substring(pkad.ddTakeF,0,4)}-${fn:substring(pkad.ddTakeF,4,6)}-${fn:substring(pkad.ddTakeF,6,8)}~${fn:substring(pkad.ddTakeT,0,4)}-${fn:substring(pkad.ddTakeT,4,6)}-${fn:substring(pkad.ddTakeT,6,8)}</p>
	                                </a>
	                                <div class="m-grd-ab">
	                                    <p class="m-grd-lt">礼品内容：</p>
	                                    <p class="m-grd-rg">
	                                    <c:forEach items="${pkad.carsContentInfo.cardInfo}" var="content">
	                                     <span>${content}</span>
	                                    </c:forEach>
	                                    <c:if test="${pkad.pointInfo!=null}">
	                                    <c:forEach items="${pkad.pointInfo}" var="point">
	                                     <span>${point.mrdf_point}积分</span>
	                                    </c:forEach>
	                                    </c:if>
	                                    </p>
	                                </div>
	                                <i></i>
	                            </dd>
	                            </c:forEach>
	                        </dl>
	                   </c:otherwise>
	                   </c:choose>
                    </div>

                </div>