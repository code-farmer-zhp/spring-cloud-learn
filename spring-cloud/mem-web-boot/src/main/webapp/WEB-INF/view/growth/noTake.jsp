<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>		
		
                <div class="t-b-b">
                    <p class="t-b-p">未领取礼包</p>
                    <div id="J_layer_wei" class="t-b-c t-b-h">
                    <c:choose>
       					<c:when test="${noTakeResult== null || noTakeResult.data == null||noTakeResult.data.totalItems == 0}">
                        <p class="t-c-p"  style="display:block;"><em></em><span>好礼不在，敬请期待...</span></p>
                        </c:when>
                        <c:otherwise>
                        <dl class="m-grd">
	                        <c:forEach items="${noTakeResult.data.pkadList}" var="pkad">
		                        <c:choose>
										<c:when
											test="${fn:length(pkad.pointInfo)==1 && fn:length(pkad.cardInfo)==0}">
											<c:set var="dataType" value="1"></c:set>
											<c:set var="withPoint" value="1"></c:set>
											<c:set var="withCard" value="0"></c:set>
										</c:when>
										<c:when
											test="${fn:length(pkad.pointInfo)==0 && fn:length(pkad.cardInfo)==1 && (empty pkad.cardInfo[0].card_list||pkad.cardInfo[0].mrdf_type=='C1')}">
											<c:set var="dataType" value="2"></c:set>
											<c:set var="withPoint" value="0"></c:set>
											<c:set var="withCard" value="1"></c:set>
										</c:when>
										<c:when
											test="${fn:length(pkad.pointInfo)==1 && fn:length(pkad.cardInfo)==1 && (empty pkad.cardInfo[0].card_list||pkad.cardInfo[0].mrdf_type=='C1')}">
											<c:set var="dataType" value="4"></c:set>
											<c:set var="withPoint" value="1"></c:set>
											<c:set var="withCard" value="1"></c:set>
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${(not empty pkad.cardInfo[0]) && (pkad.cardInfo[0].mrdf_type=='C3')}">
													<c:set var="dataType" value="3"></c:set>
													<c:set var="withPoint" value="0"></c:set>
													<c:set var="withCard" value="1"></c:set>
												</c:when>
												<c:otherwise>
													<c:choose>
													<c:when test="${fn:length(pkad.pointInfo)>0}">
														<c:choose>
	                                                		<c:when test="${fn:length(pkad.cardInfo)>0}">
																<c:set var="dataType" value="5"></c:set>
																<c:set var="withPoint" value="1"></c:set>
																<c:set var="withCard" value="1"></c:set>
															</c:when>
															<c:otherwise>
															<c:set var="dataType" value="5"></c:set>
															<c:set var="withPoint" value="1"></c:set>
															<c:set var="withCard" value="0"></c:set>
	                                                        </c:otherwise>
														</c:choose>
													</c:when>
													<c:otherwise>
														<c:choose>
	                                                         <c:when test="${fn:length(pkad.cardInfo)>0}">
	                                                         <c:set var="dataType" value="5"></c:set>
															<c:set var="withPoint" value="0"></c:set>
															<c:set var="withCard" value="1"></c:set>
	                                                    	 </c:when>
	                                                    	 <c:otherwise>
	                                                    	 <c:set var="dataType" value="5"></c:set>
															<c:set var="withPoint" value="0"></c:set>
															<c:set var="withCard" value="0"></c:set>
	                                                         </c:otherwise>
	                                                    </c:choose>
													</c:otherwise>
													</c:choose>
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
	                            <dd>
	                                <a href="#" class="J_g_layer target_no"  data-type="${dataType}" withPoint="${withPoint }" withCard="${ withCard}" pkadSeq="${pkad.pkadSeq}">
	                                    <p class="m-lbg m-lb-${mrstuiForNotakenBgimg[pkad.mrstUi]}"></p>
	                                    <p class="m-grd-f">${pkad.mrstUiDesc}</p>
	                                    <p class="m-grd-d">${fn:substring(pkad.ddTakeF,0,4)}-${fn:substring(pkad.ddTakeF,4,6)}-${fn:substring(pkad.ddTakeF,6,8)}~${fn:substring(pkad.ddTakeT,0,4)}-${fn:substring(pkad.ddTakeT,4,6)}-${fn:substring(pkad.ddTakeT,6,8)}</p>
	                                </a>
	                            </dd>
                            </c:forEach>
                        </dl>
                        </c:otherwise>
                    </c:choose>
                    </div>
        </div>
        
<c:choose>   
	<c:when
		test="${noTakeResult== null || noTakeResult.data == null||fn:length(noTakeResult.data.pkadList)==0}">
	</c:when>
	<c:otherwise>
		<c:forEach items="${noTakeResult.data.pkadList}" var="pkad">
			<c:choose>
				<c:when
					test="${fn:length(pkad.pointInfo)==1 && fn:length(pkad.cardInfo)==0}">
					<div id="g-layer-${pkad.pkadSeq}" style="display: none;">
						<div class="g-layer g-layer-1">
							<p class="g-title">亲爱的会员,送您${pkad.mrstUiDesc}，祝您购物愉快!</p>
							<div class="g-l-cont clearfix" id="g-l-cont-${pkad.pkadSeq}">
								<c:forEach items="${pkad.pointInfo}" var="point">
									<div class="g-jifen">
										<i>${point.mrdf_point}</i>积分
									</div>
								</c:forEach>
							</div>
						</div>
					</div>
				</c:when>
				<c:when
					test="${fn:length(pkad.pointInfo)==0 && fn:length(pkad.cardInfo)==1 && (empty pkad.cardInfo[0].card_list||pkad.cardInfo[0].mrdf_type=='C1')}">
					<div id="g-layer-${pkad.pkadSeq}" style="display: none;">
						<div class="g-layer g-layer-2">
							<p class="g-title">亲爱的会员,送您${pkad.mrstUiDesc}，祝您购物愉快!</p>
							<div class="g-l-cont clearfix" id="g-l-cont-${pkad.pkadSeq}">
								<c:forEach items="${pkad.pointInfo}" var="point">
									<div class="g-jifen">
										<i>${point.mrdf_point}</i>积分
									</div>
								</c:forEach>
							</div>
						</div>
					</div>
				</c:when>
				<c:when test="${fn:length(pkad.pointInfo)==1 && fn:length(pkad.cardInfo)==1 && (empty pkad.cardInfo[0].card_list||pkad.cardInfo[0].mrdf_type=='C1')}">
					<div id="g-layer-${pkad.pkadSeq}" style="display: none;">
						<div class="g-layer g-layer-4">
							<p class="g-title">亲爱的会员,送您${pkad.mrstUiDesc}，祝您购物愉快!</p>
							<div class="g-l-cont clearfix" id="g-l-cont-${pkad.pkadSeq}">
								<c:forEach items="${pkad.pointInfo}" var="point">
									<div class="g-jifen">
										<i>${point.mrdf_point}</i>积分
									</div>
								</c:forEach>
							</div>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${(not empty pkad.cardInfo[0]) && (pkad.cardInfo[0].mrdf_type=='C3')}">
						<div id="g-layer-${pkad.pkadSeq}" style="display: none;">
							<div class="g-layer g-layer-3 aui_buttons">
								<p class="g-title">亲爱的会员：恭喜您获得${pkad.mrstUiDesc}，以下三张优惠券任领其一（其余二张即刻失效），祝您购物愉快！</p>
								<div class="g-l-cont clearfix" id="g-l-cont-${pkad.pkadSeq}">
									<c:forEach items="${pkad.pointInfo}" var="point">
										<div class="g-jifen">
											<i>${point.mrdf_point}</i>积分
										</div>
									</c:forEach>
								</div>
							</div>
						</div>
						</c:when>
						<c:otherwise>
						<div id="g-layer-${pkad.pkadSeq}" style="display: none;">
							<div class="g-layer g-layer-5 aui_buttons">
								<p class="g-title">亲爱的会员：恭喜您获得${pkad.mrstUiDesc}，祝您购物愉快!</p>
								<div class="g-l-cont clearfix" id="g-l-cont-${pkad.pkadSeq}">
									<c:forEach items="${pkad.pointInfo}" var="point">
										<c:choose>
											<c:when test="${pkad.mrstUi=='T5'}">
												<div class="g-jifen g-margin"><i>${point.mrdf_point}</i>积分</div>
											</c:when>
										<c:otherwise>
											<div class="g-jifen"><i>${point.mrdf_point}</i>积分</div>
										</c:otherwise>
										</c:choose>
									</c:forEach>
								</div>
							</div>
						</div>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>

		</c:forEach>
	</c:otherwise>
</c:choose>