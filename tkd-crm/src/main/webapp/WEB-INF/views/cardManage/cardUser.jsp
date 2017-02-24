<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/jspf/common.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>会员卡列表</title>
</head>
<body>
<!-- 导航 -->
<%@include file="cardNav.jsp"%>

<form  id="cardUserFrom" class="form-horizontal" action="${ctx }/cardManage/cardUser" method="post" name="id">
    <div class="orderSearch myVipOrderSearch">
        <ul>
            <li class="timeLi subSearchLi1">
                <span>手机号</span>
                <input style="width: 200px" type="text" id="search_LIKE_phone" name="search_LIKE_phone" value="${param.search_LIKE_phone }">
            </li>
            <li class="timeLi subSearchLi1">
                <span>购卡日期</span>
                <input type="text" id="search_EQ_et" name="search_EQ_et" value="${param.search_EQ_et }"
                       onClick="WdatePicker({dateFmt:'yyyy-MM-dd'})">
            </li>
            <li class="subSearch subSearchLi1">
                <a class="reset" type="reset" href="javascript:;">重置</a>
            </li>
            <li class="subSearch subSearchLi1">
                <a class="submit" href="javascript:cardUserSubmit()" >查询</a>
            </li>
            <li class="subSearch subSearchLi">
            </li>
            <li class="subSearch subSearchLi">
                <a class="submit" href="${ctx }/cardManage/cardTurnList">转卡记录</a>
            </li>
            <li class="subSearch subSearchLi">
                <a class="submit" href="${ctx }/cardManage/initCardBuy">购买会员卡 </a>
            </li>

        </ul>
    </div>
</form>

<div class="orderResult">
    <table>
        <tr>
            <th>No.</th>
            <th>姓名</th>
            <th>手机号</th>
            <th>卡片类型</th>
            <th>余额/期限</th>
            <th>操作</th>
        </tr>

        <c:forEach items="${data.content }" var="cardUser" varStatus="stat">
            <tr>
                <td>${stat.count }</td>
                <td>${cardUser.name }</td>
                <td>${cardUser.phone}</td>
                <c:choose>
                    <c:when test="${cardUser.cardType == 1}">
                        <td>储值卡</td>
                    </c:when>
                    <c:when test="${cardUser.cardType == 2}">
                        <td>期限卡</td>
                    </c:when>
                </c:choose>
                <c:choose>
                    <c:when test="${cardUser.cardType == 1}">
                        <td><fmt:formatNumber type="number" value="${cardUser.balance/100 }" maxFractionDigits="0"/></td>
                    </c:when>
                    <c:when test="${cardUser.cardType == 2}">
                        <td>${cardUser.cardTime }</td>
                    </c:when>
                </c:choose>
                <td>
                    <a class="btn btn-default btn-sm" href="${ctx }/cardManage/cardUserDetail/${cardUser.accountId }"><i class="glyphicon glyphicon-list"></i> 详情</a>
                    <a class="btn btn-default btn-sm" href="${ctx }/cardManage/initTurnCard/${cardUser.accountId }"><i class="glyphicon glyphicon-edit"></i> 转卡</a>
                    <c:choose>
                        <c:when test="${cardUser.cardType == 1}">
                            <a class="btn btn-default btn-sm" href="${ctx }/cardManage/initRechargeCard/${cardUser.accountId }"><i class="glyphicon glyphicon-edit"></i> 充值</a>
                        </c:when>
                        <c:when test="${cardUser.cardType == 2}">
                            <a class="btn btn-default btn-sm" href="${ctx }/cardManage/initRechargeCard/${cardUser.accountId }"><i class="glyphicon glyphicon-edit"></i> 延期</a>
                        </c:when>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
<!-- 分页 -->
<tags:pagination page="${data}" />
<tags:errors />

<script src="${ctx}/static/lib/reset.js"></script>
<script type="text/javascript">
    $(function() {
        // 样式
        $('#card-man').addClass("active");
        $('#CARD_USER').addClass("active");
    });

    // 查询
    function cardUserSubmit() {
        $('#cardUserFrom').submit();
    }

</script>

</body>
</html>