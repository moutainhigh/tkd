package com.lc.zy.ball.app.service.order;

import com.lc.zy.ball.app.common.AbstractCacheService;
import com.lc.zy.ball.app.common.CheckUtils;
import com.lc.zy.ball.app.common.Constants;
import com.lc.zy.ball.app.service.account.AccountRepository;
import com.lc.zy.ball.app.service.card.CardRepository;
import com.lc.zy.ball.app.service.card.bean.CrmUserCardAccountVo;
import com.lc.zy.ball.app.service.order.bean.OrderCommentVo;
import com.lc.zy.ball.app.service.order.bean.OrderDetailVo;
import com.lc.zy.ball.app.service.order.bean.OrderListVo;
import com.lc.zy.ball.app.service.order.bean.OrderParam;
import com.lc.zy.ball.domain.oa.mapper.*;
import com.lc.zy.ball.domain.oa.po.*;
import com.lc.zy.common.bean.CacheKeys;
import com.lc.zy.common.exception.ServiceException;
import com.lc.zy.common.mq.bean.OrdeNotifyrMessage;
import com.lc.zy.common.util.*;
import com.lc.zy.common.zoo.SEQGenerate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by sl on 16/7/22.
 */
@Repository
@Transactional
public class OrderRepository extends AbstractCacheService implements CacheKeys {

    private static Logger logger = LoggerFactory.getLogger(OrderRepository.class);

    private OrderMapper orderMapper = null;

    private OrderItemMapper orderItemMapper;

    private SEQGenerate seqGenerate = null;

    private StatiumClassMemberMapper statiumClassMemberMapper = null;

    private OrderLogMapper orderLogMapper = null;

    private OrderNotifyUtil orderNotifyUtil = null;

    private StatiumActivityMemberMapper statiumActivityMemberMapper = null;

    private CheckUtils checkUtils = null;

    private StatiumInfosMapper statiumInfosMapper = null;

    private AccountRepository accountRepository = null;
    
    private CrmCardMapper crmCardMapper = null;
    
    private CardRepository cardRepository = null;

    @Autowired
    public OrderRepository(OrderMapper orderMapper, OrderItemMapper orderItemMapper, SEQGenerate seqGenerate,
                           StatiumClassMemberMapper statiumClassMemberMapper, OrderLogMapper orderLogMapper,
                           StatiumActivityMemberMapper statiumActivityMemberMapper, CheckUtils checkUtils,
                           StatiumInfosMapper statiumInfosMapper, AccountRepository accountRepository,CrmCardMapper crmCardMapper,CardRepository cardRepository) {
        super();
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.seqGenerate = seqGenerate;
        this.statiumClassMemberMapper = statiumClassMemberMapper;
        this.orderLogMapper = orderLogMapper;
        this.statiumActivityMemberMapper = statiumActivityMemberMapper;
        this.checkUtils = checkUtils;
        this.statiumInfosMapper = statiumInfosMapper;
        this.accountRepository = accountRepository;
        this.crmCardMapper= crmCardMapper;
        this.cardRepository = cardRepository;
    }

    public OrderRepository(){

    }

    /**
     * <获取未支付订单><功能具体实现>
     *
     * @param userId
     * @param type
     * @return List<Order>
     * @create：2016-07-22 11:51:44
     * @author：sl
     */
    public List<Order> getNotPaid(String userId, int type) {
        try {
            OrderCriteria criteria = new OrderCriteria();
            OrderCriteria.Criteria c = criteria.createCriteria();
            c.andUserIdEqualTo(userId);
            c.andOrdersTypeEqualTo(type);
            c.andStatusEqualTo(Constants.OrderStatus.NEW);
            c.andOrderTypeEqualTo(Constants.BookType.APP);
            return orderMapper.selectByExample(criteria);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * <取消订单><功能具体实现>
     *
     * @param orderId
     * @param userId
     * @return void
     * @create：2016-07-22 12:00:28
     * @author：sl
     */
    public void cancelStatium(String orderId, String userId) throws Exception {
        // 获取订单信息
        Order order = this.selectByPrimaryKey(Order.class, orderId);
        if (order == null) {
            throw new Exception("订单不存在");
        }
        // 订单通知
        OrdeNotifyrMessage notifyMessage = new OrdeNotifyrMessage();
        List<OrdeNotifyrMessage.SpaceBean> spaces = new ArrayList<OrdeNotifyrMessage.SpaceBean>();
        // 订单状态
        String orderStatus = order.getStatus();
        if (Constants.OrderStatus.NEW.equals(orderStatus)) {
            try {
                cancelNew(order, userId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new Exception("取消订单失败");
            }
            if (order.getOrdersType() == Constants.OrderType.STATIUM
                    || order.getOrdersType() == Constants.OrderType.BOOK) {
                OrderItemCriteria itemCriteria = new OrderItemCriteria();
                OrderItemCriteria.Criteria criteria = itemCriteria.createCriteria();
                criteria.andOrderIdEqualTo(orderId);
                List<OrderItem> orderItems = orderItemMapper.selectByExample(itemCriteria);
                for (OrderItem orderItem : orderItems) {
                }
            }
        } else {
            throw new Exception("非待支付订单，不能取消");
        }
        // 订单log
        orderLog(order, Constants.OrderAction.CANCEL);

//        notifyMessage.setSpaces(spaces);
//        notifyMessage.setStatiumId(String.valueOf(order.getStatiumId()));
//        notifyMessage.setType("cancel");
//        notifyMessage.setOrderId(orderId);
//        orderNotifyUtil.notifyOrder(MyGson.getInstance().toJson(notifyMessage));
    }

    /**
     * <取消订单><功能具体实现>
     *
     * @param order
     * @param userId
     * @return void
     * @create：2016-07-22 14:24:54
     * @author：sl
     */
    public void cancelNew(Order order, String userId) throws Exception {
        try {
            order.setStatus(Constants.OrderStatus.CANCELED);
            order.setEb(userId);
            order.setEt(new Date());
            this.updateByPrimaryKeySelective(order, order.getId());
        } catch (Exception e) {
            logger.debug("取消订单", e.getMessage());
        }
    }

    /**
     * <创建订单><功能具体实现>
     *
     * @param orderParams
     * @param userId
     * @param ordersType
     * @return java.lang.String
     * @create：2016-07-24 15:08:17
     * @author：sl
     */
    public String create(List<OrderParam> orderParams, String userId, String ordersType)
            throws Exception {
        Order order = new Order();
        OrdeNotifyrMessage notifyMessage = new OrdeNotifyrMessage();
        List<OrdeNotifyrMessage.SpaceBean> spaces = new ArrayList<OrdeNotifyrMessage.SpaceBean>();
        if (orderParams != null && orderParams.size() > 0) {
            // 获取订单号
            String orderId = seqGenerate.genOrderId();
            Date now = new Date();
            Integer totalPrice = 0;
            Integer finalPrice = 0;
            //活动价格
            Integer activityPrice = 0;
            try {
                for (OrderParam orderParam : orderParams) {
                    if (Integer.valueOf(ordersType) == 0) {
                        if (!checkUtils.checkClass(orderParam.getClassInfoId())) {
                            throw new Exception("预约课程报名已满，请重新选择");
                        } else {
                            if (checkUtils.checkClassSign(orderParam.getClassInfoId(), userId)) {
                                // 保存报名人数
                                insertClassMember(orderId, userId, orderParam.getClassInfoId());
                            } else {
                                throw new Exception("此课程您已报名，请重新选择");
                            }
                        }
                    } else if (Integer.valueOf(ordersType) == 1) {
                        if (!checkUtils.checkActivity(orderParam.getActivityId())) {
                            throw new Exception("活动报名已满，请重新选择");
                        } else {
                            if (checkUtils.checkActivitySign(orderParam.getActivityId(), userId)) {
                                // 保存报名人数
                                insertActivityMember(orderId, userId, orderParam.getActivityId());
                            } else {
                                throw new Exception("此活动您已报名，请重新选择");
                            }
                        }
                    }

                    //modify by zzq create 2016-11-29  下单价格为折扣价  存在finalFee中 原价存在Fee
                    String classId = "";
                    String price = "";
                    String discountPrice = ""; 
                    String classInfoId = orderParam.getClassInfoId();
                    StatiumClassInfo statiumClassInfo = null;
                    if(classInfoId!=null){
                    	statiumClassInfo = this.selectByPrimaryKey(StatiumClassInfo.class, classInfoId);
                    }
                    if(statiumClassInfo!=null){
                    	classId = statiumClassInfo.getClassId();
                    	if(classId!=null){
                    		StatiumClass statiumClass = this.selectByPrimaryKey(StatiumClass.class,classId);
                            if(statiumClass!=null){
                            	//原价
                            	price = statiumClass.getPrice().toString();
                            	//签约价
                            	discountPrice = statiumClass.getDiscountPrice().toString();
                            	 //总价
                                totalPrice += Integer.valueOf(price);
                                //实付价格
                                finalPrice += Integer.valueOf(discountPrice);
                            }
                    	}
                    }
                    //
                    String activityId = orderParam.getActivityId();
                    if(activityId!=null){
                    	StatiumActivity statiumActivity = this.selectByPrimaryKey(StatiumActivity.class, activityId);
                    	if(statiumActivity!=null){
                    		activityPrice +=statiumActivity.getPrice();
                    	}
                    }
                }
                // 道馆id
                String statiumId = orderParams.get(0).getStatiumId();

                OrdeNotifyrMessage.SpaceBean spaceBean = null;
                // 课程ID
                String classId = "";
                // insert订单详情
                classId = insertClassItem(orderParams, orderId, userId, ordersType);
                // 道馆id
                order.setStatiumId(Integer.valueOf(statiumId));
                // 订单类型:0: 课程 、1：活动
                if (StringUtils.isNotEmpty(ordersType) && "0".equals(ordersType)) {
                    order.setOrdersType(Constants.OrderType.STATIUM);
                    // 获取课程
                    StatiumClass statiumClass = this.selectByPrimaryKey(StatiumClass.class, classId);
                    // 课程类型
                    order.setClassType(statiumClass.getType());
                    // 获取用户信息
                    SsoUser user = this.selectByPrimaryKey(SsoUser.class, userId);
                    // 是否是1元体验
                    if (user.getExperience() > 0) {
                    	//modify by zzq 2016-12-12 新人一元体验订单价格显示签约价
                        // 订单价格
                        order.setFee(finalPrice);
                        // 订单实付价格
                        order.setFinalFee(1*100);
                    } else {
                        // 订单价格
                        order.setFee(totalPrice);
                        // 订单实付价格
                        order.setFinalFee(finalPrice);
                        
                        
                    }
                } else {
                    order.setOrdersType(Constants.OrderType.ACTIVITY);
                    // 订单价格
                    order.setFee(activityPrice);
                    // 订单实付价格
                    order.setFinalFee(activityPrice);
                }
                // 保存订单
                // 订单id
                order.setId(orderId);
                // 订单渠道 0:app 1:web 2:微信 3:线下
                order.setChannel(0);
                // 下单方式
                order.setOrderType(Constants.BookType.APP);
                // userId
                order.setUserId(userId);
                // 交易流水号
                order.setTradeNo(seqGenerate.genTradeNo());
                // 创建时间
                order.setCt(now);
                // 创建人
                order.setCb(userId);
                // 更新时间
                order.setEt(now);
                // 更新人
                order.setEb(userId);
                // 订单状态
                order.setStatus(Constants.OrderStatus.NEW);

                try {
                    this.insertSelective(order, order.getId());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException("保存订单信息失败!");
                }
//                ordersCounter.inc(statiumId);
//                userStatiumCounter.inc(userId, statiumId); // 添加该用户对这个场馆的消费次数
                // 记录订单日志
                orderLog(order, Constants.OrderAction.CREATE);
//                notifyMessage.setSpaces(spaces);
//                notifyMessage.setStatiumId(statiumId);
//                notifyMessage.setType("new");
//                notifyMessage.setOrderId(orderId);
//                orderNotifyUtil.notifyOrder(MyGson.getInstance().toJson(notifyMessage));
                return orderId;
            } catch (ServiceException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(Constants.SYSTEM_VALUE);
            }
        } else {
            throw new Exception("请先预约课程或活动");
        }
    }
    
    /**
     * 
     * <创建会员卡充值订单><功能具体实现>
     *
     * @create：2016年11月17日 下午2:18:02
     * @author：zzq
     * @param userId
     * @param cardId
     * @return
     * @throws Exception 
     */
    	
    	public String createRechargeOrder(String userId,String cardId) throws Exception{
    	logger.debug("会员卡id"+cardId);
    	CrmCard crmCard = null;
    	String orderId = "";
    	try {
			crmCard = this.selectByPrimaryKey(CrmCard.class, cardId);
		} catch (Exception e) {
			logger.error("主键查询会员卡"+e.getMessage());
		}
    	if(crmCard!=null){
    		//会员卡金额
    		int cardAmount = crmCard.getCardAmount();
    		//会员卡赠送金额
    		int cardGift = crmCard.getCardGift();
    		//会员卡类型 1储值卡 2期限卡
    		int cardType = crmCard.getCardType();
    		//状态  0未激活 1激活
    		Integer cardStatus = crmCard.getStatus();
    		if(cardType ==1 && cardStatus ==1){
    			Order order = new Order();
    			//订单id
    			orderId = seqGenerate.genOrderId();
    			order.setId(orderId);
    			//userId
    			order.setUserId(userId);
    			//渠道
    			order.setChannel(0);
    			//订单金额  保存*100后的形式
    			Integer fee = new BigDecimal(cardAmount).add(new BigDecimal(cardGift)).intValue();
    			order.setFee(fee);
    			//实收费用
    			Integer finalFee = new BigDecimal(cardAmount).intValue();
    			order.setFinalFee(finalFee);
    			//类型
    			order.setOrderType(Constants.BookType.APP);
    			//订单类型
    			order.setOrdersType(Constants.OrderType.RECHARGE);
    			//状态
    			order.setStatus(Constants.OrderStatus.NEW);
    			//创建时间
    			order.setCt(new Date());
    			//创建人
    			order.setCb(userId);
    			//et eb
    			order.setEt(new Date());
    			order.setEb(userId);
    			//评论  
    			//order.setComment("");
    			//道馆id
    			Integer dgId = crmCard.getStatiumId();
    			order.setStatiumId(dgId);
    			// 交易流水号
				order.setTradeNo(seqGenerate.genTradeNo());
                
                try {
					this.insertSelective(order, orderId);
				} catch (Exception e) {
					e.getMessage();
					throw new RuntimeException("保存订单失败");
				}
                //记录订单日志
                orderLog(order, Constants.OrderAction.CREATE);
                //保存充值订单明细表
                insertRechargeItem(order, crmCard);
    		}
    		return orderId;
    	}
    	else{
    		throw new Exception("请先选择储值卡片");
    	}
    	
    }
    
    /**
     * 
     * <创建充值订单明细><功能具体实现>
     *
     * @create：2016年11月18日 下午4:22:33
     * @author：zzq
     * @param order
     * @param crmCard
     */
    public void insertRechargeItem(Order order,CrmCard crmCard){
    	logger.debug("创建充值订单");
    	OrderItem orderItem = new OrderItem();
    	//主键id
    	String id = UUID.get();
    	orderItem.setId(id);
    	//订单id
    	orderItem.setOrderId(order.getId());
    	//卡片开始时间 卡片结束时间
    	//价格
    	orderItem.setPrice(order.getFinalFee());
    	//ct cb et eb
    	orderItem.setCt(new Date());
    	orderItem.setCb(order.getUserId());
    	orderItem.setEt(new Date());
    	orderItem.setEb(order.getUserId());
    	//订单类型
    	orderItem.setOrdersType(String.valueOf(Constants.OrderType.RECHARGE));
    	//卡片激活  日期
    	orderItem.setActiveDate(new Date());
    	//卡片类型
    	orderItem.setCardType(crmCard.getCardType());
    	//卡片状态 
    	orderItem.setCardStatus(Constants.CardStatus.ACTIVE);
    	//卡片id
    	orderItem.setCardId(crmCard.getId());
    	//保存订单明细
    	try {
			this.insertSelective(orderItem,id);
		} catch (Exception e) {
			logger.error("插入保存订单明细表"+e.getMessage());
		}
    }
    /**
     *
     * <课程预约人员信息保存><功能具体实现>
     *
     * @create：2016-07-28 00:03:18
     * @author：sl
     * @param orderId
     * @param userId
     * @param classInfoId
     * @return void
     */
    private void insertClassMember(String orderId, String userId, String classInfoId) {
        try {
            StatiumClassMember statiumClassMember = new StatiumClassMember();
            // id
            statiumClassMember.setId(UUID.get());
            // 根据用户id获取用户信息
            SsoUser user = this.selectByPrimaryKey(SsoUser.class, userId);
            // 用户id
            statiumClassMember.setUserId(userId);
            // 用户名
            statiumClassMember.setName(user.getNickName());
            // 用户头像
            if (user.getPhoto() != null) {
                statiumClassMember.setPhoto(user.getPhoto());
            }
            // 订单id
            statiumClassMember.setOrderId(orderId);
            // 创建时间
            statiumClassMember.setCt(new Date());
            // 更新时间
            statiumClassMember.setEt(new Date());
            // 课时id
            statiumClassMember.setClassInfoId(classInfoId);

            //  保存
            this.insertSelective(statiumClassMember, statiumClassMember.getId());
        } catch (Exception e) {
            logger.debug("课程预约人员信息保存", e.getMessage());
            
        }
       
    }

    /**
     *
     * <活动报名人员信息保存><功能具体实现>
     *
     * @create：2016-07-28 00:07:06
     * @author：sl
     * @param orderId
     * @param userId
     * @param activityId
     * @return void
     */
    private void insertActivityMember(String orderId, String userId, String activityId) {
        try {
            StatiumActivityMember statiumActivityMember = new StatiumActivityMember();
            // id
            statiumActivityMember.setId(UUID.get());
            // 根据用户id获取用户信息
            SsoUser user = this.selectByPrimaryKey(SsoUser.class, userId);
            // 用户id
            statiumActivityMember.setUserId(userId);
            // 用户名
            statiumActivityMember.setName(user.getNickName());
            // 用户头像
            if (StringUtils.isNotEmpty(user.getPhoto())) {
                statiumActivityMember.setPhoto(user.getPhoto());
            }
            // 订单id
            statiumActivityMember.setOrderId(orderId);
            // 创建时间
            statiumActivityMember.setCt(new Date());
            // 更新时间
            statiumActivityMember.setEt(new Date());
            // 活动ID
            statiumActivityMember.setActivityId(activityId);

            //  保存
            this.insertSelective(statiumActivityMember, statiumActivityMember.getId());
        } catch (Exception e) {
            logger.debug("活动报名人员信息保存", e.getMessage());
        }
    }

    /**
     *
     * <insert课程详情><功能具体实现>
     *
     * @create：2016-07-27 22:23:30
     * @author：sl
     * @param orderParams
     * @param orderId
     * @param userId
     * @param type
     * @return void
     */
    public String  insertClassItem(List<OrderParam> orderParams, String orderId, String userId, String type){
        Integer totalPrice = 0;
        String classId = "";
        Date now = new Date();
        try {
            for (OrderParam orderParam : orderParams) {
                // 订单明细
                OrderItem orderItem = new OrderItem();
                // id
                orderItem.setId(UUID.get());
                // 订单id
                orderItem.setOrderId(orderId);
                // 创建时间
                orderItem.setCt(now);
                // 创建人
                orderItem.setCb(userId);
                // 更新日期
                orderItem.setEt(now);
                // 更新人
                orderItem.setEb(userId);
                if (Integer.valueOf(type) == 0) {
                    // 课时id
                    orderItem.setClassInfoId(orderParam.getClassInfoId());
                    // 根据课时id获取课时信息
                    StatiumClassInfo classInfo = this.selectByPrimaryKey(StatiumClassInfo.class, orderParam.getClassInfoId());
                    // 课程id
                    classId = classInfo.getClassId();
                    // 根据课程id获取课程
                    StatiumClass statiumClass = this.selectByPrimaryKey(StatiumClass.class, classId);
                    // 课程名称
                    orderItem.setClassName(statiumClass.getClassTitle());
                    // 教练id
                    orderItem.setCoachId(classInfo.getCoachId());
                    // 课程id
                    orderItem.setClassId(classInfo.getClassId());
                    // 课程开始时间
                    orderItem.setStartTime(orderParam.getStartTime());
                    // 课程结束时间
                    orderItem.setEndTime(orderParam.getEndTime());
                } else if (Integer.valueOf(type) == 1) {
                    // 获取活动信息
                    StatiumActivity statiumActivity = this.selectByPrimaryKey(StatiumActivity.class, orderParam.getActivityId());
                    // 活动名称
                    orderItem.setActivityName(statiumActivity.getActivityTopic());
                    // 活动id
                    orderItem.setActivityId(orderParam.getActivityId());
                    // 活动开始时间
                    Date start = DateUtils.getDateTime(statiumActivity.getStartTime());
                    String  sDate = DateUtils.formatDate(start, "yyyy-MM-dd HH:mm");
                    orderItem.setStartTime(sDate.split(" ")[1]);
                    // 活动结束时间
                    Date end = DateUtils.getDateTime(statiumActivity.getEndTime());
                    String  eDate = DateUtils.formatDate(end, "yyyy-MM-dd HH:mm");
                    orderItem.setEndTime(eDate.split(" ")[1]);
                }

                // 预定课程日期/预约活动日期
                orderItem.setSignDate(DateUtils.getDate(orderParam.getClassDate()));
                // 价格
                orderItem.setPrice(Integer.valueOf(orderParam.getPrice()) * 100);

                // 总价格
                totalPrice += Integer.valueOf(orderItem.getPrice());
                try {
                    // 保存订单明细
                    this.insertSelective(orderItem, orderItem.getId());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException("保存订单详情失败!");
                }
            }
        } catch (Exception e) {
            logger.debug("insert课程详情", e.getMessage());
        }
        return classId;
    }

    /**
     *
     * <订单log><功能具体实现>
     *
     * @create：2016-07-27 23:09:21
     * @author：sl
     * @param order
     * @return void
     */
    public void orderLog(Order order, String action){
        try {
            OrderLog log = new OrderLog();
            log.setId(UUID.get());
            log.setCt(new Date());
            log.setOrderId(order.getId());
            log.setUserId(order.getUserId());
            log.setAction(action);
            if (action.equals(Constants.OrderAction.REFUNDING)){
                log.setComment("申请退款");
            } else {
                log.setComment(order.getTradeNo());
            }
            orderLogMapper.insert(log);
        } catch (Exception e) {
            logger.debug("订单号：{} ,{} 订单日志记录失败！", order.getId(), action);
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *
     * <订单详情><功能具体实现>
     *
     * @create：2016-07-25 19:39:32
     * @author：sl
     * @param userId
     * @param orderId
     * @return com.lc.zy.ball.app.service.order.bean.OrderDetailVo
     */
    public OrderDetailVo getOrderDetail(String userId, String orderId) {
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        try {
            // 获取订单
            Order order = this.selectByPrimaryKey(Order.class, orderId);
            if (order != null) {
                if (userId.equals(order.getUserId())) {
                	if(order.getOrdersType()==0||order.getOrdersType()==1){
                		orderDetailVo = getOrderSummary(order);
                	}else if(order.getOrdersType()==2){
                    	orderDetailVo = this.getRechargeSummary(order);
            		}
//                    if (Constants.OrderType.STATIUM == order.getOrdersType()) {// 道馆
//                    } else if (Constants.OrderType.COACH == order.getOrdersType()) {
//                        //  data = getOrderCoachDetail(order, data);
//                    } else if (Constants.OrderType.ACTIVITY == order.getOrdersType()) { // 活动订单详情
//                        // data = getOrderActivityDetail(order, data);
//                    }
                } else {
                    throw new RuntimeException("订单不属于该用户");
                }
            }
            return orderDetailVo;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 
     * <馆长登录时查看订单详情><功能具体实现>
     *
     * @create：2016年11月21日 上午11:03:24
     * @author：zzq
     * @param orderId
     * @return
     */
    public OrderDetailVo getCrmOrderDetail(String orderId){
    	OrderDetailVo vo = new OrderDetailVo();
    	Order order = null;
    	try {
			order = this.selectByPrimaryKey(Order.class, orderId);
		} catch (Exception e) {
			logger.error("主键查询订单"+e.getMessage());
			throw new RuntimeException("查无此订单！");
		}
    	if(order!=null){
    		if(order.getOrdersType()==0||order.getOrdersType()==1){
    			vo = this.getOrderSummary(order);
    		}else if(order.getOrdersType()==2){
    			vo = this.getRechargeSummary(order);
    		}
    		
    	}
    	return vo;
    }
    
    /**
     *
     * <订单详情><功能具体实现>
     *
     * @create：2016-07-25 19:38:59
     * @author：sl
     * @param order
     * @return com.lc.zy.ball.app.service.order.bean.OrderDetailVo
     */
    private OrderDetailVo getOrderSummary(Order order) {
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        try {
            // 订单id
            orderDetailVo.setOrderId(order.getId());
            // 获取订单详情
            OrderItemCriteria oicriteria = new OrderItemCriteria();
            OrderItemCriteria.Criteria criteria = oicriteria.createCriteria();
            criteria.andOrderIdEqualTo(order.getId());
            List<OrderItem> orderItemList = orderItemMapper.selectByExample(oicriteria);
            OrderItem ot = orderItemList.get(0);
            // 课程预约日期
            String reserveDate = DateUtil.formatDate(ot.getSignDate(), null); // 订单预定日期
            orderDetailVo.setSignDate(reserveDate);
            if (Constants.OrderType.STATIUM == order.getOrdersType()) {// 道馆
                // 课时id
                orderDetailVo.setClassInfoId(ot.getClassInfoId());
                // 课程名称
                StatiumClass statiumClass = this.selectByPrimaryKey(StatiumClass.class, ot.getClassId());
                orderDetailVo.setClassName(statiumClass.getClassTitle());
                // 教练id
                orderDetailVo.setCoachId(ot.getCoachId());
                // 教练名称
                Coach coach = this.selectByPrimaryKey(Coach.class, ot.getCoachId());
                if(coach!=null){
                	orderDetailVo.setCoachName(coach.getName());
                }
                // 课程时间
                orderDetailVo.setClassTime(DateUtils.formatDate(ot.getSignDate()) + " " + ot.getStartTime() + "~" + ot.getEndTime());
                // 课程类型
                if (statiumClass.getType() == 1) {
                    orderDetailVo.setClassType("私教");
                } else {
                    orderDetailVo.setClassType("大课");
                }
                // 订单图片
                orderDetailVo.setPhoto(statiumInfosById(order.getStatiumId()).getPhotos().split("__")[0]);
                // 判断订单是否需要评价
                if (order.getIsComment() != null) {
                    orderDetailVo.setIsComment(order.getIsComment());
                }
            } else if (Constants.OrderType.ACTIVITY == order.getOrdersType()) { // 活动订单详情
                // 获取活动信息
                StatiumActivity statiumActivity = this.selectByPrimaryKey(StatiumActivity.class, ot.getActivityId());
                	// 活动id
                    orderDetailVo.setActivityId(ot.getActivityId());
                    // 活动名称
                    orderDetailVo.setActivityName(ot.getActivityName());
                    // 活动地点
                    orderDetailVo.setActivityAddr("");
                    // 活动时间
                    orderDetailVo.setActivityTime(DateUtils.formatDate(ot.getSignDate()) + " " + ot.getStartTime() + "~" + ot.getEndTime());
                    // 活动人数
                    if(statiumActivity!= null){
                    orderDetailVo.setActivityNum(statiumActivity.getAmount());
                    // 订单图片
                    orderDetailVo.setPhoto(statiumActivity.getPhoto().split("__")[0]);
                    // 主办方
                    orderDetailVo.setHostName(statiumActivity.getHostName());
                }
            }
            // 价格
            orderDetailVo.setPrice(String.valueOf(order.getFee()/100));
            //折扣价
            orderDetailVo.setDiscountPrice(String.valueOf(order.getFinalFee()/100));
            // 道馆id
            orderDetailVo.setStatiumId(String .valueOf(order.getStatiumId()));
            // 订单类型
            orderDetailVo.setOrderType(order.getOrdersType());

            // 显示日期
            String showTime = DateUtil.formatDate(order.getCt(), "yyyy-MM-dd HH:mm");
            orderDetailVo.setShowTime(showTime);
            // 订单创建时间
            String createTime = DateUtil.formatDate(order.getCt(), "yyyy-MM-dd HH:mm:ss"); // 创建订单日期
            orderDetailVo.setCt(createTime);
            // 当前时间
            String currTime = DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"); // 当前时间
            orderDetailVo.setCurrentTime(currTime);
            // 倒计时
            int restTime = new BigDecimal(new Date().getTime() - order.getCt().getTime()).divide(new BigDecimal(1000))
                    .intValue();
            orderDetailVo.setRestTime(restTime);
            // 订单状态
            orderDetailVo.setStatus(order.getStatus());
            // 订单状态中文展示
            orderDetailVo.setStatusName(CommonOAUtils.transStatusToChineseForApp(order.getStatus()));
            // 是否是1元体验
            orderDetailVo.setIsExperience(checkUtils.checkIsExperience(order.getUserId()));
            // 会员卡余额
            //UserAccountVo userAccountVo = accountRepository.totalAmount(order.getUserId());\
            String userId = order.getUserId();
            SsoUser ssoUser = this.selectByPrimaryKey(SsoUser.class, userId);
            if(ssoUser!=null){
            	CrmUserCardAccountVo userAccountVo = cardRepository.getCrmUserAccount(ssoUser);
            	orderDetailVo.setBalance(userAccountVo.getBalance().toString());
                // 是否设置账户密码
                orderDetailVo.setIsPwd(userAccountVo.getIsPwd());
            }

            return orderDetailVo;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 
     * <会员卡订单详情><功能具体实现>
     *
     * @create：2016年11月22日 上午11:40:06
     * @author：zzq
     * @param order
     * @return
     */
    private OrderDetailVo getRechargeSummary(Order order){
    	OrderDetailVo vo = new OrderDetailVo();
    	try {
			String orderId = order.getId();
			OrderItemCriteria orderItemCriteria = new OrderItemCriteria();
			orderItemCriteria.createCriteria().andOrderIdEqualTo(orderId);
			List<OrderItem> items = orderItemMapper.selectByExample(orderItemCriteria);
			if(items.size()!=0){
				String cardId = items.get(0).getCardId();
				if(cardId!=null){
					CrmCard crmCard = crmCardMapper.selectByPrimaryKey(cardId);
					//赠送金额
					Integer giftFee = new BigDecimal(crmCard.getCardGift()).divide(new BigDecimal(100)).intValue();
					vo.setGiftFee(giftFee);
					//实付金额
					Integer finalFee = new BigDecimal(crmCard.getCardAmount()).divide(new BigDecimal(100)).intValue();
					vo.setPrice(finalFee.toString());
					//描述
					vo.setDescription("面值"+finalFee+"元赠送金额"+giftFee+"元");
					//总价
					vo.setAmount(new BigDecimal(crmCard.getCardGift()).add(new BigDecimal(crmCard.getCardAmount())).divide(new BigDecimal(100)).intValue());
					//订单支付日期
					Date payDate = items.get(0).getCardBuyDate();
					//卡片状态
					Integer cardStatus = items.get(0).getCardStatus();
					vo.setCardStatus(cardStatus);
					String date = DateUtils.formatDate(payDate, "yyyy-MM-dd");
					vo.setPayDate(date);
				}
                // 显示日期
                String showTime = DateUtil.formatDate(order.getCt(), "yyyy-MM-dd HH:mm");
                vo.setShowTime(showTime);
                // 订单创建时间
                String createTime = DateUtil.formatDate(order.getCt(), "yyyy-MM-dd HH:mm:ss"); // 创建订单日期
                vo.setCt(createTime);
                // 当前时间
                String currTime = DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"); // 当前时间
                vo.setCurrentTime(currTime);
                // 倒计时
                int restTime = new BigDecimal(new Date().getTime() - order.getCt().getTime()).divide(new BigDecimal(1000))
                        .intValue();
                vo.setRestTime(restTime);
				//类型
				vo.setCardType(Constants.CardType.PAIEDCARD);
				//订单创建日期
				vo.setCt(DateUtils.forDatetime(order.getCt()));
				//订单号
				vo.setOrderId(order.getId());
				//订单类型
				vo.setOrderType(Constants.OrderType.RECHARGE);
				
				// 会员卡余额
			    //UserAccountVo userAccountVo = accountRepository.totalAmount(order.getUserId());
			    String userId = order.getUserId();
			    SsoUser ssoUser = new SsoUser();
				try {
					ssoUser = this.selectByPrimaryKey(SsoUser.class, userId);
				} catch (Exception e) {
					logger.error("查询用户"+e.getMessage());
				}
				if(ssoUser!=null){
					CrmUserCardAccountVo userAccountVo = cardRepository.getCrmUserAccount(ssoUser);
					vo.setBalance(userAccountVo.getBalance().toString());
				}
				
			}
			vo.setCardName("会员卡");
			//订单状态 订单状态:ORDER_NEW：新订单，ORDER_CANCELED：已撤销订单，已完成订单：ORDER_BILLED，已支付订单：ORDER_PAIED，退款中订单:ORDER_REFUNDING，已退款:ORDER_REFUNDED
			vo.setStatus(order.getStatus());
			vo.setStatusName(CommonOAUtils.transStatusToChineseForApp(order.getStatus()));
			return vo;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

    /**
     *
     * <根据用户信息获取用户订单><功能具体实现>
     *
     * @create：2016-07-25 20:37:08
     * @author：sl
     * @param user
     * @param classType
     * @param orderStatus
     * @return java.util.List<com.lc.zy.ball.domain.oa.po.Order>
     */
    public List<Order> getOrders(SsoUser user, String classType, String orderStatus) {
        List<Order> orders = new ArrayList<Order>();
        try {
            // 根据用户id,用户绑定道馆id获取用户订单
            OrderCriteria orderCriteria = new OrderCriteria();
            OrderCriteria.Criteria criteria = orderCriteria.createCriteria();
            // 用户id
            criteria.andUserIdEqualTo(user.getId());
            // 用户绑定道馆id
            criteria.andStatiumIdEqualTo(user.getStatiumId());
            // 课程类型
            criteria.andClassTypeEqualTo(Integer.valueOf(classType));
            // 课程状态
            criteria.andStatusEqualTo(orderStatus);
            orders = orderMapper.selectByExample(orderCriteria);
        } catch (Exception e) {
            logger.debug("根据用户信息获取用户订单", e.getMessage());
        }
        return orders;
    }

    /**
     *
     * <获取订单list><功能具体实现>
     *
     * @create：2016-07-27 22:04:41
     * @author：sl
     * @param user
     * @param paied
     * @param type
     * @param begin
     * @param size
     * @return java.util.List<com.lc.zy.ball.app.service.order.bean.OrderListVo>
     */
    public List<OrderListVo> getOrderList(SsoUser user, Boolean paied, String type, Integer begin, Integer size, String comment) {
        List<OrderListVo> orderListVos = new ArrayList<OrderListVo>();
        try {
            // 获取订单
            OrderCriteria orderCriteria = new OrderCriteria();
            OrderCriteria.Criteria criteria = orderCriteria.createCriteria();
            // 用户id
            criteria.andUserIdEqualTo(user.getId());
            //线上购买的订单
            criteria.andOrderTypeEqualTo("BOOK_APP");
            // 订单状态 false:未支付,true:已预约,空为全部
            if (paied != null) {
                // 订单状态:ORDER_NEW：新订单，ORDER_PLAYING：已开场 ，ORDER_CANCELED：已撤销订单，已完成订单：ORDER_BILLED，
                // 已删除订单：ORDER_DELETED，已支付订单：ORDER_PAIED，退款中订单:ORDER_REFUNDING，已退款:ORDER_REFUNDED，已确认订单:ORDER_VERIFY
                if (paied) {
                    criteria.andStatusEqualTo("ORDER_PAIED");
                } else {
                    criteria.andStatusEqualTo("ORDER_NEW");
                }
            }
            // 订单类型:0: 课程 、1：活动  2：充值卡订单
            if (type != null) {
                criteria.andOrdersTypeEqualTo(Integer.valueOf(type));
            }
            // 评价
            if ("comment".equals(comment)) {
                criteria.andIsCommentEqualTo(0);
                criteria.andOrdersTypeEqualTo(Constants.OrderType.STATIUM);

            }
            // 分页
            orderCriteria.setMysqlOffset(begin);
            orderCriteria.setMysqlLength(size);
            // 排序
            orderCriteria.setOrderByClause("et desc");
            // 获取订单
            List<Order> orders = orderMapper.selectByExample(orderCriteria);

            // 封装list
            orderListVos = packageOrderList(orders);

        } catch (Exception e) {
            logger.debug("获取订单list", e.getMessage());
        }
        return orderListVos;
    }

    /**
     *
     * <订单list封装><功能具体实现>
     *
     * @create：2016-07-27 21:59:54
     * @author：sl
     * @param orders
     * @return java.util.List<com.lc.zy.ball.app.service.order.bean.OrderListVo>
     */
    public List<OrderListVo> packageOrderList (List<Order> orders){
        List<OrderListVo> orderListVos = new ArrayList<OrderListVo>();
        try {
            for (Order order : orders) {
                OrderListVo orderListVo = new OrderListVo();
                // 获取订单详情信息
                List<OrderItem> orderItems = orderItems(order.getId());
                if (orderItems.size() > 0) {
                    // 订单名称
                    if (order.getOrdersType() == 0) {
                        orderListVo.setOrderName(orderItems.get(0).getClassName());
                        // 道馆图片
                        StatiumInfosCriteria statiumInfosCriteria = new StatiumInfosCriteria();
                        StatiumInfosCriteria.Criteria criteria = statiumInfosCriteria.createCriteria();
                        criteria.andDgIdEqualTo(order.getStatiumId());
                        List<StatiumInfos> statiumInfoses = statiumInfosMapper.selectByExample(statiumInfosCriteria);
                        StatiumInfos statiumInfos = statiumInfoses.get(0);
                        if(statiumInfos!=null){
                        	orderListVo.setPhoto(statiumInfos.getPhotos().split("__")[0]);
                        }
                    } else if (order.getOrdersType() == 1) {
                        orderListVo.setOrderName(orderItems.get(0).getActivityName());
                        // 活动图片
                        StatiumActivity statiumActivity = this.selectByPrimaryKey(StatiumActivity.class, orderItems.get(0).getActivityId());
                        if(statiumActivity!=null){
                        	orderListVo.setPhoto(statiumActivity.getPhoto().split("__")[0]);
                        }
                        }
                    // 订单状态
                    orderListVo.setStatus(order.getStatus());
                    // 订单状态中文
                    orderListVo.setStatusName(CommonOAUtils.transStatusToChineseForApp(order.getStatus()));
                    // 订单类型
                    orderListVo.setOrderType(order.getOrdersType());
                    // 价格
                    orderListVo.setPrice(order.getFinalFee()/100);
                    // 订单日期
                    String orderDate = DateUtils.formatDate(order.getCt());
                    orderListVo.setClassDate(orderDate.replaceAll("-","."));
                    // 订单id
                    orderListVo.setOrderId(order.getId());
                    // 评论状态
                    if (order.getIsComment() != null) {
                        orderListVo.setIsComment(order.getIsComment());
                    }

                    orderListVos.add(orderListVo);
                }
            }
        } catch (Exception e) {
            logger.debug("订单list封装", e.getMessage());
        }
        return orderListVos;
    }

    /**
     *
     * <根据订单id获取订单详情><功能具体实现>
     *
     * @create：2016-07-27 21:33:59
     * @author：sl
     * @param orderId
     * @return java.util.List<com.lc.zy.ball.domain.oa.po.OrderItem>
     */
    public List<OrderItem> orderItems (String orderId) {
        List<OrderItem> orderItems = new ArrayList<OrderItem>();
        try {
            OrderItemCriteria orderItemCriteria = new OrderItemCriteria();
            OrderItemCriteria.Criteria criteria = orderItemCriteria.createCriteria();
            criteria.andOrderIdEqualTo(orderId);
            orderItems = orderItemMapper.selectByExample(orderItemCriteria);
        } catch (Exception e) {
            logger.debug("根据订单id获取订单详情", e.getMessage());
        }
        return orderItems;
    }

    /**
     *
     * <取消订单><功能具体实现>
     *
     * @create：2016-07-30 11:46:57
     * @author：sl
     * @param orderId
     * @param type
     * @param userId
     * @return void
     */
    public void cancel(String orderId, Integer type, String userId) {
        try {
            // 根据订单id获取
            Order order = orderById(orderId);
            // 取消订单
            cancelNew(order, userId);
            // 删除课程/活动报名人
            deleteSign(orderId, type, userId);
            // 订单日志
            orderLog(order, Constants.OrderAction.CANCEL);
        } catch (Exception e) {
            logger.debug("取消订单", e.getMessage());
        }
    }

    /**
     *
     * <删除课程/活动报名><功能具体实现>
     *
     * @create：2016-07-30 11:46:13
     * @author：sl
     * @param orderId
     * @param type
     * @param userId
     * @return void
     */
    private void deleteSign(String orderId, Integer type, String userId) {
        try {
            if (type == 0) {// 课程
                StatiumClassMemberCriteria statiumClassMemberCriteria = new StatiumClassMemberCriteria();
                StatiumClassMemberCriteria.Criteria criteria = statiumClassMemberCriteria.createCriteria();
                criteria.andUserIdEqualTo(userId);
                criteria.andOrderIdEqualTo(orderId);
                statiumClassMemberMapper.deleteByExample(statiumClassMemberCriteria);
            } else if (type == 1) {// 活动
                StatiumActivityMemberCriteria statiumActivityMemberCriteria = new StatiumActivityMemberCriteria();
                StatiumActivityMemberCriteria.Criteria cri = statiumActivityMemberCriteria.createCriteria();
                cri.andUserIdEqualTo(userId);
                cri.andOrderIdEqualTo(orderId);
                statiumActivityMemberMapper.deleteByExample(statiumActivityMemberCriteria);
            }
        } catch (Exception e) {
            logger.debug("删除课程/活动报名", e.getMessage());
        }
    }

    /**
     *
     * <根据订单Id获取订单信息><功能具体实现>
     *
     * @create：2016-07-30 11:37:44
     * @author：sl
     * @param orderId
     * @return com.lc.zy.ball.domain.oa.po.Order
     */
    public Order orderById(String orderId) {
        Order order = new Order();
        try {
            order = this.selectByPrimaryKey(Order.class, orderId);
        } catch (Exception e) {
            logger.debug("根据订单Id获取订单信息", e.getMessage());
        }
        return order;
    }

    /**
     *
     * <根据道馆id获取道馆信息><功能具体实现>
     *
     * @create：2016-08-06 19:52:05
     * @author：sl
     * @param statiumId
     * @return com.lc.zy.ball.domain.oa.po.StatiumInfos
     */
    public StatiumInfos statiumInfosById(Integer statiumId) {
        StatiumInfos statiumInfos = new StatiumInfos();
        try {
            StatiumInfosCriteria statiumInfosCriteria = new StatiumInfosCriteria();
            StatiumInfosCriteria.Criteria criteria = statiumInfosCriteria.createCriteria();
            criteria.andDgIdEqualTo(statiumId);
            List<StatiumInfos> statiumInfoses = statiumInfosMapper.selectByExample(statiumInfosCriteria);
            statiumInfos = statiumInfoses.get(0);
        } catch (Exception e) {
            logger.debug("根据道馆id获取道馆信息", e.getMessage());
        }
        return  statiumInfos;
    }


    /**
     *
     * <教练评论><功能具体实现>
     *
     * @create：
     * @author：sl
     * @return
     */
    public void commentCoach(String orderId, String userId, String coachId, Integer coachGrade) {
        try {
            CoachComment coachComment = new CoachComment();
            coachComment.setCoachId(coachId);
            coachComment.setGrade(coachGrade);
            coachComment.setId(UUID.get());
            coachComment.setOrderId(orderId);
            coachComment.setUid(userId);
            coachComment.setCt(new Date());
            this.insertSelective(coachComment, coachComment.getId());
            // 教练评星
            Coach coach = this.selectByPrimaryKey(Coach.class, coachId);
            int grade = 0;
            if (coach.getGrade() == null){
                grade = 0;
            } else {
                grade = coach.getGrade();
            }
            coach.setGrade(grade + coachGrade);
            this.updateByPrimaryKeySelective(coach, coachId);
        } catch (Exception e) {
            logger.debug("教练评论:{}", e.getMessage());
        }
    }

    /**
     *
     * <道馆评论><功能具体实现>
     *
     * @create：
     * @author：sl
     * @return
     */
    public void commentStatium(String orderId, String userId, Integer statiumGrade) {
        try {
            // 获取订单信息
            Order order = this.selectByPrimaryKey(Order.class, orderId);
            StatiumComment statiumComment = new StatiumComment();
            statiumComment.setId(UUID.get());
            statiumComment.setOrderId(orderId);
            statiumComment.setUid(userId);
            statiumComment.setGrade(statiumGrade);
            statiumComment.setStatiumId(order.getStatiumId());
            statiumComment.setCt(new Date());
            this.insertSelective(statiumComment, statiumComment.getId());
            // 道馆评星
            StatiumInfosCriteria statiumInfosCriteria = new StatiumInfosCriteria();
            StatiumInfosCriteria.Criteria criteria = statiumInfosCriteria.createCriteria();
            criteria.andDgIdEqualTo(order.getStatiumId());
            StatiumInfos statiumInfos = statiumInfosMapper.selectByExample(statiumInfosCriteria).get(0);
            int grade = 0;
            if (statiumInfos.getGrade() == null) {
                grade = 0;
            } else {
                grade = statiumInfos.getGrade();
            }
            statiumInfos.setGrade(grade + statiumGrade);
            this.updateByPrimaryKeySelective(statiumInfos, order.getStatiumId());
        } catch (Exception e) {
            logger.debug("道馆评论:{}", e.getMessage());
        }
    }


    /**
     *
     * <更新订单评论状态><功能具体实现>
     *
     * @create：
     * @author：sl
     * @return
     */
    public void updateOrderComment(String orderId) {
        try {
            Order order = this.selectByPrimaryKey(Order.class, orderId);
            order.setIsComment(1);
            this.updateByPrimaryKeySelective(order, orderId);
        } catch (Exception e) {
            logger.debug("更新订单评论状态:{}", e.getMessage());
        }
    }

    /**
     *
     * <获取评论内容><功能具体实现>
     *
     * @create：16/9/8 14:34
     * @author：sl
     * @param orderId
     * @return com.lc.zy.ball.app.service.order.bean.OrderCommentVo
     */
    public OrderCommentVo orderCommentInfo(String orderId, String userId) {
        OrderCommentVo orderCommentVo = new OrderCommentVo();
        try {
            // 获取订单信息
            Order order = this.selectByPrimaryKey(Order.class, orderId);
            // 获取订单详情
            OrderItemCriteria orderItemCriteria = new OrderItemCriteria();
            OrderItemCriteria.Criteria criteria = orderItemCriteria.createCriteria();
            criteria.andOrderIdEqualTo(orderId);
            List<OrderItem> orderItems = orderItemMapper.selectByExample(orderItemCriteria);
            OrderItem orderItem = orderItems.get(0);
            // 获取教练信息
            Coach coach = this.selectByPrimaryKey(Coach.class, orderItem.getCoachId());
            // 获取道馆信息
            StatiumInfosCriteria statiumInfosCriteria = new StatiumInfosCriteria();
            StatiumInfosCriteria.Criteria cri = statiumInfosCriteria.createCriteria();
            cri.andDgIdEqualTo(order.getStatiumId());
            StatiumInfos statiumInfos = statiumInfosMapper.selectByExample(statiumInfosCriteria).get(0);

            // 订单ID
            orderCommentVo.setOrderId(orderId);
            // 课程名称
            orderCommentVo.setCoachName(orderItem.getClassName());
            // 教练id
            orderCommentVo.setCoachId(orderItem.getCoachId());
            // 教练名称
            orderCommentVo.setCoachName(coach.getName());
            // 教练头像
            orderCommentVo.setCoachPhoto(coach.getLogo());
            // 教练评星
            int coachGrade = 0;
            if (coach.getGrade() !=null) {
                coachGrade = coach.getGrade();
            }
            orderCommentVo.setCoachGrade(coachGrade);
            // 道馆评星
            int statiumGrade = 0;
            if (statiumInfos.getGrade() != null) {
                statiumGrade =statiumInfos.getGrade();
            }
            orderCommentVo.setStatiumGrade(statiumGrade);
            // 教练类型
            if ("0".equals(coach.getType())) {
                orderCommentVo.setCoachType("大课");
            } else if ("1".equals(coach.getType())) {
                orderCommentVo.setCoachType("私教");
            }
            // 道馆id
            orderCommentVo.setStatiumId(String.valueOf(order.getStatiumId()));
            // 评论类型 (0: 评论教练 1:评论道馆和教练)
            // orderCommentVo.setCommentType(checkUtils.checkIsCommentType(userId, statiumInfos.getDgId()));
            orderCommentVo.setCommentType(0);

        } catch (Exception e) {
            logger.debug("获取评论内容:{}", e.getMessage());
        }
        return orderCommentVo;
    }

    /**
     *
     * <获取订单未评论总数><功能具体实现>
     *
     * @create：16/9/8 19:06
     * @author：sl
     * @param userId
     * @return java.lang.Integer
     */
    public Integer commentNum(String userId) {
        int commentNum = 0;
        try {
            OrderCriteria orderCriteria = new OrderCriteria();
            OrderCriteria.Criteria criteria =  orderCriteria.createCriteria();
            criteria.andUserIdEqualTo(userId);
            criteria.andIsCommentEqualTo(0);
            commentNum = orderMapper.countByExample(orderCriteria);
        } catch (Exception e) {
            logger.debug("获取订单未评论总数:{}", e.getMessage());
        }
        return commentNum;
    }

    /**
     *
     * <校验用户是否可以退款><功能具体实现>
     *
     * @create：16/9/9 14:32
     * @author：sl
     * @param orderId
     * @return java.lang.Boolean
     */
    public Boolean checkOrderRefund(String orderId) {
        Boolean flag = true;
        try {
            OrderItemCriteria orderItemCriteria = new OrderItemCriteria();
            OrderItemCriteria.Criteria criteria = orderItemCriteria.createCriteria();
            criteria.andOrderIdEqualTo(orderId);
            List<OrderItem> orderItems = orderItemMapper.selectByExample(orderItemCriteria);
            OrderItem orderItem = orderItems.get(0);
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            logger.debug("hour {}", hour);
            // 订单开始时间
            String signDate = DateUtils.formatDate(orderItem.getSignDate()) + " " + orderItem.getStartTime();
            Date startTime = DateUtils.getDate(signDate, "yyyy-MM-dd HH:mm");
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(startTime);
//            int hour_ = cal.get(Calendar.HOUR_OF_DAY);
//            logger.debug("hour_ {}", hour_);
            int minutes = DateUtils.intervalMinutes(startTime, new Date());
            logger.debug("hour");
            if (minutes/60 < 24){
                flag = false;
            }

            /*if(( DateUtils.formatDate(new Date(), "yyyy-MM-dd")).equals(DateUtils.formatDate(orderItem.getSignDate(), "yyyy-MM-dd"))) {
                if (hour_ - hour < 24) {
                    flag = false;
                }
            } else {
                if (hour_ - hour <= 0) {
                    flag = false;
                }
            }*/
        } catch (Exception e) {
            logger.debug("校验用户是否可以退款:{}", e.getMessage());
        }
        return flag;
    }

    /**
     *
     * <用户订单退款><功能具体实现>
     *
     * @create：16/9/9 14:45
     * @author：sl
     * @param orderId
     * @return void
     */
    public void orderRefund(String orderId) {
        try {
            // 更新订单状态
            Order order = this.selectByPrimaryKey(Order.class, orderId);
            order.setStatus(Constants.OrderStatus.REFUNDING);
            order.setReason("app用户申请退款");
            this.updateByPrimaryKeySelective(order, orderId);
            // 订单日志
            orderLog(order, Constants.OrderAction.REFUNDING);
        } catch (Exception e) {
            logger.debug("用户订单退款:{}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        logger.debug("hour {}", hour);
        // 订单开始时间
        Date startTime = DateUtils.getDate("2016-10-21 13:00", "yyyy-MM-dd HH:mm");
            Calendar cal = Calendar.getInstance();
            cal.setTime(startTime);
            int hour_ = cal.get(Calendar.HOUR_OF_DAY);
            logger.debug("hour_ {}", hour_);
        System.out.println(hour_ - hour);
    }
    
    
}