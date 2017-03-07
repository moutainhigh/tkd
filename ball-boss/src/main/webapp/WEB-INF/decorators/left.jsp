<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="org.apache.shiro.SecurityUtils"%>
<%@page import="com.lc.zy.ball.boss.common.security.ShiroDbRealm.ShiroUser"%>
<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jspf/common.jsp" %>
<div class="panel panel-default">

<div class="panel-heading" style="overflow:hidden;">
	
	<span style="float:left; margin-top:2px;" class="glyphicon glyphicon-th-large"></span>
	<strong style="float:left; margin-left:5px;">功能菜单</strong>
	<a class="leftBtn" id="leftBtn" href="javascript:;">收起</a>
	
</div>

<div class="panel-body leftmenu"><!-- 左侧菜单 -->
  <ul class="list-group leftmenu-list">
  
    
    <li>
      <a ><span></span> 用户管理</a>
      <ul class="list-group">
      <shiro:hasPermission name="ssouser:man">
        <li><a href="${ctx}/ssouser/user" class="list-group-item" id="ssouser-man">用户管理</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="coach:man">
        <li><a href="${ctx}/ssouser/coach" class="list-group-item" id="coach-man">教陪管理</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="feedback:man">
      	<li><a href="${ctx}/ssouser/feedback"  class="list-group-item" id="feedback-man">反馈管理</a></li>
      </shiro:hasPermission>
       <shiro:hasPermission name="cta:man">
      	<li><a href="${ctx}/ssouser/cta"  class="list-group-item" id="cta-man">网协账户管理</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="company:man">
      	<li><a href="${ctx}/company/list"  class="list-group-item" id="company-man">企业用户</a></li>
      </shiro:hasPermission>
      </ul>
   </li>
   
   <li>
      <a id="" ><span></span> 场馆管理</a>
      <ul class="list-group">
      <shiro:hasPermission name="statium:man">
        <li><a href="${ctx}/statium" class="list-group-item" id="statium-man">签约场馆管理</a></li>
        <li><a href="${ctx}/statiumFree" class="list-group-item" id="statium-man-free">免费场馆管理</a></li>
      </shiro:hasPermission>
      <!--  
      <shiro:hasPermission name="space:man">
        <li><a href="${ctx}/statium/space" class="list-group-item" id="space-man">场地管理</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="price:man">
        <li><a href="${ctx}/statium/price" class="list-group-item" id="price-man">价格管理</a></li>
      </shiro:hasPermission>
      -->
      </ul>
   </li>
   <li>
      <a id="" ><span></span> 场馆抽奖</a>
      <ul class="list-group">
        <li><a href="${ctx}/prize" class="list-group-item" id="prize-set-man">抽奖设置</a></li>
        <li><a href="${ctx}/prize/getUserPrizeList" class="list-group-item" id="prize-list-man">抽奖列表</a></li>
      </ul>
   </li>
  
   <li>
      <a ><span></span> 订单管理</a>
      <ul class="list-group">
      <shiro:hasPermission name="orders:man">
        <li><a href="${ctx}/orders/list" class="list-group-item" id="orders-man">订单管理</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="orders:man">
        <li><a href="${ctx}/orders/enjoylist" class="list-group-item" id="enjoyorders-man">乐享赛订单</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="orders:man">
              <li><a href="${ctx}/orders/nuomiList" class="list-group-item" id="nuomiorders-man">糯米订单</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="orders:man">
              <li><a href="${ctx}/orders/taipingList" class="list-group-item" id="taipingorders-man">太平订单</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="book:man">
      <li><a href="${ctx}/book/list" class="list-group-item" id="book-man">约球管理</a></li>
        </shiro:hasPermission>
      <shiro:hasPermission name="bill:man">
        <li><a href="${ctx}/orders/bill" class="list-group-item" id="bill-man">账户流水</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="orderBill:man">
      	<li><a href="${ctx}/orders/bill/billList" class="list-group-item" id="orders-bill">场馆结账</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="orderBill:man">
      <li><a href="${ctx}/orders/handleList" class="list-group-item" id="orders-handle">订单处理</a></li>
      </shiro:hasPermission>  
       <shiro:hasPermission name="companyOrder:man">
      <li><a href="${ctx}/company/orderList" class="list-group-item" id="companyOder-man">企业订单</a></li>
      </shiro:hasPermission>
      </ul>
   </li>
  <li>
      <a ><span></span> 商城管理</a>
      <ul class="list-group">
      <shiro:hasPermission name="mallOrders:man">
      <li><a href="${ctx}/goods" class="list-group-item" id="goods">商品管理</a></li>
      </shiro:hasPermission>
       <shiro:hasPermission name="mallOrders:man">
              <li><a href="${ctx}/mall/orderList" class="list-group-item" id="mallOrders-man">订单管理</a></li>
       </shiro:hasPermission>
          <shiro:hasPermission name="mallctivity:man">
              <li><a href="${ctx}/mall/activityList" class="list-group-item" id="mallctivity-man">活动管理</a></li>
          </shiro:hasPermission>
       </ul>
  </li>
   <li>
      <a ><span></span> 球友管理</a>
	      <ul class="list-group">
	      	  <shiro:hasPermission name="qiuyouzone:man">
		        <li><a href="${ctx}/qiuyouzone/list" class="list-group-item" id="qiuyouzone-man">球友圈管理</a></li>
		      </shiro:hasPermission>
		      
		      <shiro:hasPermission name="qiuyouzone:group">
		        <li><a href="${ctx}/group" class="list-group-item" id="qiuyouzone-group">群组管理</a></li>
		      </shiro:hasPermission>
		      
		      <shiro:hasPermission name="qyLable:man">
		      	<li><a href="${ctx}/qiuyouzoneLabel/list" class="list-group-item" id="qiuyoulabel-list">球友标签管理</a></li>
		      </shiro:hasPermission>
		      <shiro:hasPermission name="qiuyouzone_market:man">
		        <li><a href="${ctx}/qiuyouzone/market/list" class="list-group-item" id="qiuyouzone_market-list">营销管理</a></li>
		        <li><a href="${ctx}/qiuyouzone/market/user/list" class="list-group-item" id="qiuyouzone_market-user">营销账户列表</a></li>
		      </shiro:hasPermission>
	      </ul>
   </li>
   <li>
      <a ><span></span> 账户管理</a>
	      <ul class="list-group">
		      <shiro:hasPermission name="accounts:man">
		      	<li><a href="${ctx}/accounts/list" class="list-group-item" id="accounts-list">账户列表</a></li>
		      </shiro:hasPermission>
              <shiro:hasPermission name="bonusAccounts:man">
                  <li><a href="${ctx}/accounts/bonusList" class="list-group-item" id="bonusAccounts-list">奖金账户列表</a></li>
              </shiro:hasPermission>
		      <shiro:hasPermission name="accounts:audit">
		      	<li><a href="${ctx}/accounts/withdrawCashList" class="list-group-item" id="withdrawCash-list">提现审核</a></li>
		      </shiro:hasPermission>
		      <shiro:hasPermission name="accounts:handle">
		      	<li><a href="${ctx}/accounts/withdrawCashFinance" class="list-group-item" id="withdrawCash-finance">提现处理</a></li>
		      </shiro:hasPermission>
	      </ul>
   </li>
   
   <li>
      <a  ><span></span> 运营管理</a>
      <ul class="list-group">
      <shiro:hasPermission name="coupon:man">
        <li><a href="${ctx}/coupon/list" class="list-group-item" id="coupon-man">优惠券管理</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="carousel:man">
        <li><a href="${ctx}/carousel/list" class="list-group-item" id="carousel-man">轮播图管理</a></li>
      </shiro:hasPermission>
	  <!--  
      <shiro:hasPermission name="coupon:statistic">
        <li><a href="${ctx}/coupon/statistic" class="list-group-item" id="coupon-statistic">优惠券统计</a></li>
      </shiro:hasPermission>
		-->

      <shiro:hasPermission name="activity:man">
        <li><a href="${ctx}/activity/list" class="list-group-item" id="activity-man">活动管理</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="event:man">
	    <li><a href="${ctx}/corps" class="list-group-item" id="corps-man">战队管理</a></li>
        <li><a href="${ctx}/event/list" class="list-group-item" id="event-man">赛事管理</a></li>
        </shiro:hasPermission>
        <shiro:hasPermission name="enjoyrace:man">
        <li><a href="${ctx}/enjoyRace/list" class="list-group-item" id="enjoyrace-man">乐享赛事</a></li>
        <li><a href="${ctx}/statium/accredits" class="list-group-item" id="statium-accredit">CTA-Open授权管理</a></li>
        </shiro:hasPermission>
         <shiro:hasPermission name="bonus:man">
        <li><a href="${ctx}/enjoyRace/bonusList" class="list-group-item" id="bonus-man">奖金管理</a></li>
        </shiro:hasPermission>
        <shiro:hasPermission name="enjoyraceSite:man">
        <li><a href="${ctx}/enjoyRace/siteList" class="list-group-item" id="enjoyraceSite-man">站点管理</a></li>
        </shiro:hasPermission>
        <shiro:hasPermission name="players:man">
              <li><a href="${ctx}/enjoyRace/playersList" class="list-group-item" id="playersList-man">乐享历史参赛人员</a></li>
        </shiro:hasPermission>
          <shiro:hasPermission name="platinumMatch:man">
              <li><a href="${ctx}/platinumMatch/list" class="list-group-item" id="platinumMatch-man">女子白金赛</a></li>
          </shiro:hasPermission>
      <shiro:hasPermission name="label:man">
        <li><a href="${ctx}/label/list" class="list-group-item" id="label-man">标签管理</a></li>
      </shiro:hasPermission>
       <shiro:hasPermission name="push:man">
      <li><a href="${ctx}/push" class="list-group-item" id="push-man">推送管理</a></li>
      </shiro:hasPermission>
     <shiro:hasPermission name="statistics:activity">
      <li><a href="${ctx}/activity/statisticsList" class="list-group-item" id="activityStatistics-man">活动统计</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="statistics:event">
      <li><a href="${ctx}/event/statisticsList" class="list-group-item" id="eventStatistics-man">赛事统计</a></li>
      </shiro:hasPermission>
     <shiro:hasPermission name="vip:main">
      <li><a href="${ctx}/vip" class="list-group-item" id="vip-man">球馆会员卡</a></li>
      </shiro:hasPermission>
     <shiro:hasPermission name="comment:main">
      <li><a href="${ctx}/comment/list/0" class="list-group-item" id="comment-man">评论管理</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="company:services">
      <li><a href="${ctx}/code/codeList" class="list-group-item" id="code-man">合作管理</a></li>
      <li><a href="${ctx}/company/services" class="list-group-item" id="company-services">企业服务</a></li>
      <li><a href="${ctx}/company/integraLlist" class="list-group-item" id="companyIntegral-man">企业积分</a></li>
      <li><a href="${ctx}/company/companyActivityList"  class="list-group-item" id="companyActivity-man">企业提交的活动、赛事列表</a> </li>
      </shiro:hasPermission>
      <shiro:hasPermission name="bigcity:man">
      <li><a href="${ctx}/bigcitygame" class="list-group-item" id="bigcitygame-man">城市赛报名管理</a></li>
      </shiro:hasPermission>
  <shiro:hasPermission name="vote:man">
          <li><a href="${ctx}/vote/list" class="list-group-item" id="voteTheme-man">投票管理</a></li>
          <li><a href="${ctx}/vote/downLoadNewUser" class="list-group-item" id="voteTheme-man">导出投票手机号</a></li>
   </shiro:hasPermission>
  <shiro:hasPermission name="vote:man">
          <li><a href="${ctx}/info/list" class="list-group-item" id="info-man">资讯管理</a></li>
   </shiro:hasPermission>
   <shiro:hasPermission name="vote:man">
          <li><a href="${ctx}/assessment/list" class="list-group-item" id="assessment-man">测评管理</a></li>
   </shiro:hasPermission>
          <li><a href="${ctx}/gift" class="list-group-item" id="gift-man">奖品</a></li>
      </ul>
   </li>
	  
    <li>
      <a  ><span></span> 系统管理</a>
      <ul class="list-group">
      <shiro:hasPermission name="user:list">
        <li><a href="${ctx}/admin/user" class="list-group-item" id="user-man">员工管理</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="channel:list">
      	<li><a href="${ctx }/admin/user/channelList" class="list-group-item" id="channel-man">渠道用户管理</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="role:list">
        <li><a href="${ctx}/admin/role" class="list-group-item" id="role-man">角色管理</a></li>
      </shiro:hasPermission>
      
      <shiro:hasPermission name="func:list">
        <li><a href="${ctx}/admin/func" class="list-group-item" id="func-man">功能管理</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="org">
        <li><a href="${ctx}/admin/org" class="list-group-item" id="org-man">组织管理</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="dic">
        <li><a href="${ctx}/admin/dic" class="list-group-item" id="dic-man">字典管理</a></li>
      </shiro:hasPermission>
      <li><a href="${ctx}/admin/userinfo" class="list-group-item" id="userinfo-man">个人信息</a></li>
      <shiro:hasPermission name="holiday">
      	<li><a href="${ctx}/admin/holiday" class="list-group-item" id="holiday-man">节假日设置</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="orderSms">
      	<li><a href="${ctx}/admin/orderSms" class="list-group-item" id="orderSms-man">订单短信自动发送设定</a></li>
      </shiro:hasPermission>
       <shiro:hasPermission name="smslog">
      <li><a href="${ctx}/admin/smslog" class="list-group-item" id="smslog-man">短信查询</a></li>
      </shiro:hasPermission>
       <shiro:hasPermission name="emsg">
      <li><a href="${ctx}/admin/emsg/server/get" class="list-group-item" id="emsg-man">Emsg管理</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="callRecord">
      <li><a href="${ctx}/cph/callRecord" class="list-group-item" id="callRecord-man">通话记录</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="badWords:man">
      <li><a href="${ctx}/badWords/list" class="list-group-item" id="badWords-man">禁词管理</a></li>
      </shiro:hasPermission>
      <shiro:hasPermission name="log:man">
      <li><a href="http://180.76.186.123/accesslog/" target="_blank" class="list-group-item" id="badWords-man">ngix日志(180.76.186.123)</a></li>
      <li><a href="http://180.76.148.252/accesslog/" target="_blank" class="list-group-item" id="badWords-man">ngix日志(180.76.148.252)</a></li>
      </shiro:hasPermission>
      </ul>
    </li>
    <shiro:hasPermission name="sysstatistic">
    <li>
    	<a ><span></span> 系统统计</a>
    	<ul class="list-group">
		    <shiro:hasPermission name="statistic:order">
	      		<li><a href="${ctx}/statistic/order" class="list-group-item" id="statistic-order">订单查询</a></li>
	      	</shiro:hasPermission>
	      	<shiro:hasPermission name="statistic:regUser">
	      		<li><a href="${ctx}/statistic/orderStatistic" class="list-group-item" id="orderStatisticCharts">订单统计</a></li>
	      		<li><a href="${ctx}/statisticRegisterUser/registerList" class="list-group-item" id="statisticRegUser-regUserList">注册用户统计</a></li>
	      		<li><a href="${ctx}/statisticRegisterUser/registerListEx" class="list-group-item" id="statisticRegUser-regUserExList">7日/30日注册用户统计</a></li>
	      		<li><a href="${ctx}/statisticRegisterUser/activityAreaStatistic" class="list-group-item" id="statisticRegUser-activityUserArea">用户激活区域统计</a></li>
	      		<li><a href="${ctx}/statisticRegisterUser/statisticChannelOrders" class="list-group-item" id="statisticOrders-channelOrders">渠道订单量统计</a></li>
	      		<li><a href="${ctx}/statisticUalog/ualogList" class="list-group-item" id="statisticUalog-ualogList">PV统计</a></li>
	      		<li><a href="${ctx}/statisticSms/smsList" class="list-group-item" id="statisticSms-smsList">短信统计</a></li>
	      		<li><a href="${ctx}/statisticRegisterUser/queryByCityAll" class="list-group-item" id="statisticRegUser-queryByCityAll">按城市统计注册用户</a></li>
	      	</shiro:hasPermission>
      	</ul>
    </li>
   </shiro:hasPermission>
   <shiro:hasPermission name="websiteadmin">
   	<li>
      <a><span></span> 网站管理</a>
     <ul class="list-group">
      <shiro:hasPermission name="notice:man">
      	<li><a href="${ctx}/notice/list" class="list-group-item" id="notice-man">公告管理</a></li>
      </shiro:hasPermission>
       <shiro:hasPermission name="seo:man">
      	<li><a href="${ctx}/seo/list/0" class="list-group-item" id="seo-man">SEO管理</a></li>
      </shiro:hasPermission>
    </ul>
     </li>
   </shiro:hasPermission>
     

  </ul>
</div><!-- / 左侧菜单 -->

</div>
<%--    <iframe src="${ctx }/static/edb_bar/phoneBar/login.html" height="180px" width="290px" style="border: 0;margin-left: 20px;"></iframe>
 --%>
<%
if (SecurityUtils.getSubject().isAuthenticated()) {
	if (StringUtils.isNotBlank(((ShiroUser)SecurityUtils.getSubject().getPrincipal()).getAgent())) {%>
	         <iframe src="${ctx }/static/edb_bar/phoneBar/phonebar.html?loginName=<%=((ShiroUser)SecurityUtils.getSubject().getPrincipal()).getAgent() %>&password=<%=((ShiroUser)SecurityUtils.getSubject().getPrincipal()).getAgentPwd() %>&loginType=gateway" height="180px" width="290px" style="border: 0;margin-left: 20px;"></iframe>
<%} } %>



 