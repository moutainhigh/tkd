<%@page import="org.apache.shiro.SecurityUtils"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/jspf/common.jsp" %>
<!DOCTYPE html>
<html>

<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>球友圈CRM登录</title>
<link href="static/css/pages/login.css?ver=2345" rel="stylesheet">
<link href="static/css/pages/login2.css?ver=2345" rel="stylesheet">
<link href="static/css/plugins/sweetalert/sweetalert.css" rel="stylesheet">
</head>
<%
if (SecurityUtils.getSubject().isAuthenticated()) {
    response.sendRedirect(request.getContextPath());
}
%>
<div class="loginBody">
	<div class="loginCont">
		<form id="loginForm" class="form-horizontal" role="form" action="${ctx}/login" method="post">
		<dl>
			<dt>登录</dt>
			<dd class="ddName"><em></em><input name="username" type="text" placeholder="请输入登录账户" value=""/></dd>
			<dd class="ddPassword"><em></em><input name="password" type="password" placeholder="请输入登录密码" value=""/></dd>
			<dd class="ddTip">
				<span data-tip="错误信息提示">
					<c:if test="${!empty shiroLoginFailure }">
						<c:choose>
							<c:when
								test="${fn:containsIgnoreCase(shiroLoginFailure, 'DisabledAccountException') }">
								<c:out value="该账号已被禁用,请联系管理员或使用其他账号登录." />
							</c:when>
							<c:when
								test="${fn:containsIgnoreCase(shiroLoginFailure, 'IncorrectCaptchaException') }">
								<c:out value="验证码输入不正确." />
							</c:when>
							<c:otherwise>
								<c:out value="登录失败，请检查用户名和密码后重新输入！" />
							</c:otherwise>
						</c:choose>
					</c:if>
				</span>
			</dd>
			<dd class="ddLoginBtn"><a id="loginBtn" href="javascript:;">登录</a></dd>
			<%--<dd class="ddDes">北京乾正隆文化交流有限公司   　&copy;2016</dd>
			<dd class="ddDesTel">服务热线：010-57198965 </dd>
			<dd class="ddDesTel"><tags:version /></dd>--%>
		</dl>
		</form>
	</div>
</div>
<%-- <script src="${ctx}/static/lib/jquery-2.1.1.js"></script> --%>
<script src="${ctx }/static/lib/jquery.min.js"></script>
<script src="${ctx}/static/lib/bootstrap.min.js"></script>
<script src="${ctx}/static/lib/plugins/bootstrap-validation/validate.js" type="text/javascript"></script>
<script src="${ctx}/static/lib/plugins/bootstrap-validation/messages_zh.js" type="text/javascript"></script>
<script src="static/lib/plugins/sweetalert/sweetalert.min.js"></script>
<script type="text/javascript">

$(function() {
  //为表单注册validate函数
  /* $("#loginForm").validate({
	  rules : {
		  username: {
			  required: true,
			  rangelength: [2, 60]
		  }
	  },
    messages: {
      username: {
        required: "请输入用户名"
      },
      password: {
    	  required: "请输入密码"
      }
    }
  }); */
  	var webkit = /webkit/.test(navigator.userAgent.toLowerCase());
  	var ua = navigator.userAgent; 
  
	$('#username').focus();
	$("#loginBtn").click(function(){
	if(ua.indexOf("Windows NT 5.1")!=-1) { 
	  	//如果是win xp系统
	  	if(!webkit){
	  		//如果不是谷歌内核的浏览器
	  		alert('您使用的是兼容模式！ 为了您的体验，请更换到极速模式下使用！');
	  	}
	  }
	if(webkit){
		$("#loginForm").submit();
		}else{
		swal({
            title: "为了您的体验，请更换使用或下载谷歌chrome浏览器",
            type: "warning",
            showCancelButton: false,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "下载谷歌chrome浏览器",
            closeOnConfirm: false,
            closeOnCancel: false
        }, function (isConfirm) {
        	if (isConfirm) {
        		window.open("http://211.136.65.145/cache/sw.bos.baidu.com/sw-search-sp/software/1b5bc4ffa7d9b/ChromeStandalone_57.0.2987.133_Setup.exe?ich_args2=127-14134110013172_957c8e3714c7a43386e3b394693f99b1_10068001_9c886d2ed6c7f9d59133518939a83798_9fc43c9940f828326e568568371d0401");
        	}
        });
	}
	});
});  
</script>
</html>