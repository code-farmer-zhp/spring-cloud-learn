<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:if test='${not empty messageList.messageList}'>
	<c:if test="${type == 'systemList'}">
		<ul class="stys-ul">
	</c:if>
	<c:if test="${type == 'memberList'}">
		<ul class="care-ul">
	</c:if>
	<c:if test="${type == 'activityList'}">
		<ul class="acts-ul">
	</c:if>
	<!--系统消息- start -->
	<c:forEach items="${messageList.messageList}" var="store"
		varStatus="content">
		<c:if test="${type == 'systemList'}">
			<li class="stys-list">
				<div class="stys-time">
					<i></i>${store.createTime}</div>
				<div class="stys-cont">
					<a class="close J-del-li" href="javascript:;"  messageID="${store.messageID}"></a>
					<a class="cont J-cont" href="${store.open_url}" value="${store.messageType}-${store.url_type}" target="_blank">
						<h2>${store.title}</h2>
						<span>${store.content}</span>
					</a>
				</div>
			</li>
		</c:if>
		<!--系统消息- end -->
		<!--会员关怀- start -->
		<c:if test="${type == 'memberList'}">
			<li class="care-list">
				<div class="care-time">
					<i></i>${store.createTime}</div>
				<div class="care-cont">
					<a class="close J-del-li" href="javascript:;"  messageID="${store.messageID}"></a>
					<a class="cont J-cont" href="${store.open_url}" value="${store.messageType}-${store.url_type}" target="_blank">
						<h2>${store.title}</h2>
						<span>${store.content}</span>
					</a>
				</div>
			</li>
		</c:if>
		<!--会员关怀- end -->
		<!--活动通知- start -->
		<c:if test="${type == 'activityList'}">
			<c:if test="${store.showPic ==false}">
			<c:set  value="${store.messageType}-${store.url_type}" var="string1"/>
			<c:set  value="${store.taskId}" var="string2"/>
				<li class="acts-list"><a href="javascript:;" class="close J-del-li" messageID="${store.messageID}"></a>
					<h2>${store.title}</h2>
					<div class="acts-time">
						<i></i>${store.createTime}</div>
					<ul>
						<c:if test='${not empty store.extraField}'>
							<c:forEach items="${store.extraField}" var="store"
								varStatus="content">
								<c:if test="${store.effective ==false}">
									<li class="disabled">
								</c:if>
								<c:if test="${store.effective ==true}">
									<li>
								</c:if>
								<a class="J-cont" href="${store.pcUrl}" target="_blank" value="${string1}" value2="${string2}" value3="${store.id}"> <span class="rg">${store.describe}</span>
								</a></li>
	</c:forEach>
</c:if>
</ul>
</li>
</c:if>
<c:if test="${store.showPic ==true}">
<c:set  value="${store.messageType}-${store.url_type}" var="string1"/>
<c:set  value="${store.taskId}" var="string2"/>
	<li class="acts-list acts-img"><a href="javascript:;"
		class="close J-del-li"  messageID="${store.messageID}"></a>
		<h2>${store.title}</h2>
		<div class="acts-time">
			<i></i>${store.createTime}</div>
		<ul>
			<c:if test='${not empty store.extraField}'>
				<c:forEach items="${store.extraField}" var="store"
					varStatus="content">
					<c:if test="${store.effective ==false}">
						<li class="disabled">
					</c:if>
					<c:if test="${store.effective ==true}">
						<li>
					</c:if>
					<a class="J-cont" href="${store.pcUrl}" target="_blank" value="${string1}" value2="${string2}" value3="${store.id}"> <span class="rg">${store.describe}</span>
						<span class="lf"><img src="${store.Pic}" alt=""><i></i></span>
					</a></li>
	</c:forEach>
</c:if>
</ul>
</li>
</c:if>
</c:if>
<!--活动通知- end -->
</c:forEach>
</ul>
<!--分页 start -->
<div class="fn_page clearfix">
	<input type="hidden" id="prod_page_num"
		current="${pageDataBefore.pageNo}"
		pgcount="${pageDataBefore.totalpage}" />
	<ul>
		<li class="${pageDataBefore.fn_prve}"><a 
			class="J-prev-page" href="javascript:;" pageNo="${pageDataBefore.pageNo}" pageUrl="${pageDataBefore.goUrl}" pageTotal="${pageDataBefore.totalpage}"><i
				class="arrow_prev"></i><span>上一页</span></a></li>
		<li><span class="cur">${pageDataBefore.pageNo}</span>/<span
			class="all">${pageDataBefore.totalpage}</span></li>
		<li class="${pageDataBefore.fn_next}"><a 
			class="J-next-page" href="javascript:;" pageNo="${pageDataBefore.pageNo}" pageUrl="${pageDataBefore.goUrl}" pageTotal="${pageDataBefore.totalpage}"><span>下一页</span><i
				class="arrow_next"></i></a></li>
		<li><span>到第</span><input id="pagenum" name="pagenum"
			maxlength="100" type="text" style="width: 30; height: 22"><span>页</span></li>
		<li class="goto"><a  href="javascript:;"
			class="J-change-page" pageNo="pagenum" pageUrl="${pageDataBefore.goUrl}" pageTotal="${pageDataBefore.totalpage}">跳转</a></li>
	</ul>
</div>
<!--分页 end -->
</c:if>
<c:if test='${empty messageList.messageList}'>
	<p class="mc-no">
		<i></i>您还没有收到消息哦~
	</p>
</c:if>