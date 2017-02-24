<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/jspf/common.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>app订单列表</title>
</head>
<body>
<!-- 导航 -->
<%@include file="orderNav.jsp"%>

<form  id="orderForm" class="form-horizontal" action="${ctx }/order/list" method="post" name="id">
    <div class="orderSearch">
        <input type="hidden" id="search_EQ_orderType" name="search_EQ_orderType"/>
        <ul>
            <li><span>订单号</span>
                <input type="text" id="search_LIKE_id" name="search_LIKE_id" value="${param.search_LIKE_id }">
            </li>

            <li class="timeLi"><span>查询时间</span>
                <input type="text" id="search_GTE_et" name="search_GTE_et" value="${param.search_GTE_et }"
                       onClick="WdatePicker({dateFmt:'yyyy-MM-dd 00:00:00',maxDate:'#F{$dp.$D(\'search_LTE_et\')}'})">
                <em>到</em>
                <input type="text" id="search_LTE_et" name="search_LTE_et" value="${param.search_LTE_et }"
                       onClick="WdatePicker({dateFmt:'yyyy-MM-dd 23:59:59',minDate:'#F{$dp.$D(\'search_GTE_et\')}'})">
            </li>

           <%-- <li>
                <span>支付方式</span>
                <select id="search_EQ_payType" name="search_EQ_payType">
                    <option value="">请选择</option>
                    <option value="1">支付宝</option>
                    <option value="2">微信</option>
                    <option value="3">会员卡</option>
                </select>
            </li>--%>

            <li>
                <span>订单状态</span>
                <select id="search_EQ_status" name="search_EQ_status">
                    <option value="">所有状态</option>
                    <option value="ORDER_NEW">待付款</option>
                    <option value="ORDER_VERIFY">已确认</option>
                    <option value="ORDER_BILLED">交易成功</option>
                    <option value="ORDER_PAIED">已付款</option>
                    <option value="ORDER_REFUNDING">退款中</option>
                    <option value="ORDER_REFUNDED">已退款</option>
                    <option value="ORDER_CANCELED">交易关闭</option>
                </select>
            </li>

            <li>
                <span>联系电话</span>
                <input type="text" id="search_LIKE_phone" name="search_LIKE_phone" value="${param.search_LIKE_phone }">
            </li>

            <li class="subSearch">
                <a class="reset" type="reset" href="javascript:;">重置</a>
                <a class="submit" href="javascript:orderSubmit('BOOK_LIVE')">查询</a>
               <!--  <a class="exp" href="/admin/order/exp?type={{searcher.orderType}}&startDate={{searcher.startDate}}&endDate={{searcher.endDate}}&status={{searcher.orderStatus}}&sportType={{searcher.sportType}}">导出</a> -->
            	<a class="exp" href = "javascript:exportOrder('BOOK_LIVE')">导出</a>
            </li>
        </ul>
    </div>
</form>

<div class="orderResult">
    <table>
        <tr>
            <th>订单号</th>
            <th>订单名称</th>
            <th>类型</th>
            <th>教练</th>
            <th>下单日期</th>
            <th>完成日期</th>
            <th>用户手机号</th>
            <th>预约日期/开始时间</th>
            <th>预约时间/结束日期</th>
            <th>订单金额</th>
            <th>状态</th>
        </tr>
        <c:forEach items="${data.content }" var="order" varStatus="stat">
            <tr>
                <td>${order.id }</td>
                <td>${order.className}</td>
                <td>
                    <c:choose>
                        <c:when test="${order.ordersType == 0}">
                            课程
                        </c:when>
                        <c:when test="${order.ordersType == 1}">
                            活动
                        </c:when>
                        <c:when test="${order.ordersType == 2}">
                            会员卡
                        </c:when>
                    </c:choose>
                </td>
                <td>${order.coachName }</td>
                <td><fmt:formatDate value="${order.ct}"
                                    pattern="yyyy-MM-dd HH:mm:ss" /></td>
                <td><fmt:formatDate value="${order.et}"
                                    pattern="yyyy-MM-dd HH:mm:ss" /></td>
                <td>${order.phone }</td>
                <td>
                    <c:choose>
                        <c:when test="${order.ordersType == 0}">
                            <fmt:formatDate value="${order.signDate }" pattern="yyyy/MM/dd"/>
                        </c:when>
                        <c:when test="${order.ordersType == 1}">
                            <fmt:formatDate value="${order.asTime }" pattern="yyyy/MM/dd HH:mm"/>
                        </c:when>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${order.ordersType == 0}">
                            ${order.signTime }
                        </c:when>
                        <c:when test="${order.ordersType == 1}">
                            <fmt:formatDate value="${order.aeTime }" pattern="yyyy/MM/dd HH:mm"/>
                        </c:when>
                    </c:choose>
                </td>
                <td><fmt:formatNumber type="number" value="${order.finalFee }" maxFractionDigits="0"/></td>
                <c:choose>
                    <c:when test="${order.status == 'ORDER_NEW'}">
                        <td class="warning">待付款</td>
                    </c:when>
                    <c:when test="${order.status == 'ORDER_VERIFY'}">
                        <td class="info">已确认</td>
                    </c:when>
                    <c:when test="${order.status == 'ORDER_BILLED'}">
                        <td class="">交易成功</td>
                    </c:when>
                    <c:when test="${order.status == 'ORDER_PAIED'}">
                        <td class="success">已付款</td>
                    </c:when>
                    <c:when test="${order.status == 'ORDER_REFUNDING'}">
                        <td class="">退款中</td>
                    </c:when>
                    <c:when test="${order.status == 'ORDER_REFUNDED'}">
                        <td class="">已退款</td>
                    </c:when>
                    <c:when test="${order.status == 'ORDER_CANCELED'}">
                        <td class="">交易关闭</td>
                    </c:when>
                </c:choose>
            </tr>
        </c:forEach>
    </table>
</div>
<tags:pagination page="${data}" />
<tags:errors />

<script type="text/javascript">
    $(function() {
        // 样式
        $('#order-man').addClass("active");
        $('#BOOK_LIVE').addClass("active");

        // select状态
        // 支付方式
        var payType = '${param.search_EQ_payType}';
        if(payType){
            $("#search_EQ_payType option[value="+payType+"]").attr("selected","selected");
        }
        // 订单状态
        var status = '${param.search_EQ_status}';
        if(status){
            $("#search_EQ_status option[value="+status+"]").attr("selected","selected");
        }

        // 重置
        $("a[type='reset']").click(function(){
            $(this).closest("form").find("input").attr("value", "");
            $(this).closest("form").find("select option:selected").attr("selected", false);
            $(this).closest("form").find("select option:first").attr("selected",true);
        });
    });

    // 表单提交
    function orderSubmit(v) {
        // 渠道
        $("#search_EQ_orderType").attr("value",v);
        $("#orderForm").submit();
        form.attr("action","");
    }
	
    function exportOrder(v){
    	 $("#search_EQ_orderType").attr("value","BOOK_LIVE");
    	var form = $("#orderForm");
     	form.attr("action","${ctx}/order/exportQueryData");
     	form[0].submit();
     	form.attr("action","");
    }
</script>

</body>
</html>