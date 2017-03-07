package com.lc.zy.ball.boss.framework.orders.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jasypt.commons.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.persistence.SearchFilter;

import cmb.netpayment.Settle;

import com.cntaiping.tp.healthy.dto.out.bill.UploadOut;
import com.cntaiping.tp.healthy.dto.out.lemitrade.TradeOut;
import com.jd.jr.pay.gate.signature.util.JdPayUtil;
import com.lc.zy.ball.boss.common.Constants;
import com.lc.zy.ball.boss.common.OrgCodeUtil;
import com.lc.zy.ball.boss.common.Sequence;
import com.lc.zy.ball.boss.common.SessionUtil;
import com.lc.zy.ball.boss.common.Zonemap;
import com.lc.zy.ball.boss.common.pay.alipay.config.AlipayConfig;
import com.lc.zy.ball.boss.common.pay.alipay.util.AlipaySubmit;
import com.lc.zy.ball.boss.common.pay.alipay.util.UtilDate;
import com.lc.zy.ball.boss.common.pay.wxap.util.HttpsPlatRequest;
import com.lc.zy.ball.boss.common.pay.wxap.util.HttpsRequest;
import com.lc.zy.ball.boss.common.pay.wxap.util.MD5Util;
import com.lc.zy.ball.boss.common.service.AbstractCacheService;
import com.lc.zy.ball.boss.framework.activity.vo.ActivityVo;
import com.lc.zy.ball.boss.framework.event.vo.GamesVo;
import com.lc.zy.ball.boss.framework.orders.util.HttpClientUtil;
import com.lc.zy.ball.boss.framework.orders.vo.EnjoyOrderSearch;
import com.lc.zy.ball.boss.framework.orders.vo.OrderItemVo;
import com.lc.zy.ball.boss.framework.orders.vo.OrderLogVo;
import com.lc.zy.ball.boss.framework.orders.vo.OrderParam;
import com.lc.zy.ball.boss.framework.orders.vo.OrderVo;
import com.lc.zy.ball.boss.framework.orders.vo.RefundResponse;
import com.lc.zy.ball.boss.framework.orders.vo.TradeRefundReqDto;
import com.lc.zy.ball.boss.framework.ssouser.vo.CoachVo;
import com.lc.zy.ball.common.data.pageable.Page;
import com.lc.zy.ball.common.data.pageable.PageImpl;
import com.lc.zy.ball.common.data.pageable.PageRequest;
import com.lc.zy.ball.domain.oa.mapper.ActivityMapper;
import com.lc.zy.ball.domain.oa.mapper.CoacherMapper;
import com.lc.zy.ball.domain.oa.mapper.CompanyAccountLogMapper;
import com.lc.zy.ball.domain.oa.mapper.EnjoyMemberMapper;
import com.lc.zy.ball.domain.oa.mapper.GamesCorpsMapper;
import com.lc.zy.ball.domain.oa.mapper.GamesMapper;
import com.lc.zy.ball.domain.oa.mapper.GamesMemberMapper;
import com.lc.zy.ball.domain.oa.mapper.LemiLogMapper;
import com.lc.zy.ball.domain.oa.mapper.MemberListMapper;
import com.lc.zy.ball.domain.oa.mapper.OrderHandleMapper;
import com.lc.zy.ball.domain.oa.mapper.OrderItemMapper;
import com.lc.zy.ball.domain.oa.mapper.OrderLogMapper;
import com.lc.zy.ball.domain.oa.mapper.OrderMapper;
import com.lc.zy.ball.domain.oa.mapper.PayLogMapper;
import com.lc.zy.ball.domain.oa.mapper.QiuyouCardLogMapper;
import com.lc.zy.ball.domain.oa.mapper.SpacePriceMapper;
import com.lc.zy.ball.domain.oa.mapper.SsoUserBonusAccountLogMapper;
import com.lc.zy.ball.domain.oa.mapper.SsoUserMapper;
import com.lc.zy.ball.domain.oa.mapper.StaffAccountLogMapper;
import com.lc.zy.ball.domain.oa.mapper.StatiumDetailMapper;
import com.lc.zy.ball.domain.oa.mapper.StatiumPriceTmplMapper;
import com.lc.zy.ball.domain.oa.mapper.UserAccountLogMapper;
import com.lc.zy.ball.domain.oa.mapper.UserMapper;
import com.lc.zy.ball.domain.oa.po.Activity;
import com.lc.zy.ball.domain.oa.po.ActivityCriteria;
import com.lc.zy.ball.domain.oa.po.Coach;
import com.lc.zy.ball.domain.oa.po.Coacher;
import com.lc.zy.ball.domain.oa.po.CoacherCriteria;
import com.lc.zy.ball.domain.oa.po.CompanyAccount;
import com.lc.zy.ball.domain.oa.po.CompanyAccountLog;
import com.lc.zy.ball.domain.oa.po.CorpsInfo;
import com.lc.zy.ball.domain.oa.po.CouponHistory;
import com.lc.zy.ball.domain.oa.po.CouponInfo;
import com.lc.zy.ball.domain.oa.po.EnjoyGame;
import com.lc.zy.ball.domain.oa.po.EnjoyGameSite;
import com.lc.zy.ball.domain.oa.po.EnjoyMember;
import com.lc.zy.ball.domain.oa.po.EnjoyMemberCriteria;
import com.lc.zy.ball.domain.oa.po.Games;
import com.lc.zy.ball.domain.oa.po.GamesCorps;
import com.lc.zy.ball.domain.oa.po.GamesCorpsCriteria;
import com.lc.zy.ball.domain.oa.po.GamesCriteria;
import com.lc.zy.ball.domain.oa.po.GamesMemberCriteria;
import com.lc.zy.ball.domain.oa.po.GamesPlayer;
import com.lc.zy.ball.domain.oa.po.Holiday;
import com.lc.zy.ball.domain.oa.po.LemiLog;
import com.lc.zy.ball.domain.oa.po.MemberList;
import com.lc.zy.ball.domain.oa.po.MemberListCriteria;
import com.lc.zy.ball.domain.oa.po.Order;
import com.lc.zy.ball.domain.oa.po.OrderCriteria;
import com.lc.zy.ball.domain.oa.po.OrderHandle;
import com.lc.zy.ball.domain.oa.po.OrderHandleCriteria;
import com.lc.zy.ball.domain.oa.po.OrderItem;
import com.lc.zy.ball.domain.oa.po.OrderItemCriteria;
import com.lc.zy.ball.domain.oa.po.OrderLog;
import com.lc.zy.ball.domain.oa.po.OrderLogCriteria;
import com.lc.zy.ball.domain.oa.po.Organization;
import com.lc.zy.ball.domain.oa.po.PayLog;
import com.lc.zy.ball.domain.oa.po.PayLogCriteria;
import com.lc.zy.ball.domain.oa.po.QiuyouCardLog;
import com.lc.zy.ball.domain.oa.po.QiuyouCardUser;
import com.lc.zy.ball.domain.oa.po.SpacePrice;
import com.lc.zy.ball.domain.oa.po.SpacePriceCriteria;
import com.lc.zy.ball.domain.oa.po.SsoUser;
import com.lc.zy.ball.domain.oa.po.SsoUserBonusAccount;
import com.lc.zy.ball.domain.oa.po.SsoUserBonusAccountLog;
import com.lc.zy.ball.domain.oa.po.SsoUserBonusAccountLogCriteria;
import com.lc.zy.ball.domain.oa.po.SsoUserCriteria;
import com.lc.zy.ball.domain.oa.po.StaffAccount;
import com.lc.zy.ball.domain.oa.po.StaffAccountLog;
import com.lc.zy.ball.domain.oa.po.StatiumDetail;
import com.lc.zy.ball.domain.oa.po.StatiumPriceTmpl;
import com.lc.zy.ball.domain.oa.po.StatiumPriceTmplCriteria;
import com.lc.zy.ball.domain.oa.po.StatiumSpace;
import com.lc.zy.ball.domain.oa.po.User;
import com.lc.zy.ball.domain.oa.po.UserAccount;
import com.lc.zy.ball.domain.oa.po.UserAccountLog;
import com.lc.zy.ball.domain.oa.po.UserCriteria;
import com.lc.zy.common.cache.RedisService;
import com.lc.zy.common.coreservice.FreeSpaceCounter;
import com.lc.zy.common.coreservice.OrdersCounter;
import com.lc.zy.common.exception.ServiceException;
import com.lc.zy.common.mq.bean.OrdeNotifyrMessage;
import com.lc.zy.common.taiping.TaipingService;
import com.lc.zy.common.util.CommonOAUtils;
import com.lc.zy.common.util.DateUtil;
import com.lc.zy.common.util.FreeMarkerUtils;
import com.lc.zy.common.util.MessageUtil;
import com.lc.zy.common.util.MyGson;
import com.lc.zy.common.util.OrderNotifyUtil;
import com.lc.zy.common.util.UUID;
import com.lc.zy.common.util.payUtils.PayParameter;
import com.lc.zy.common.zoo.SecurityLock;

@Component
@Transactional(readOnly = true)
public class OrderService extends AbstractCacheService {
	@Autowired
	private MessageUtil messageUtil;
	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private OrderItemMapper orderItemMapper;

	@Autowired
	private MemberListMapper memberListMapper;

	@Autowired
	private OrderLogMapper orderLogMapper;

	@Autowired
	private SsoUserMapper ssoUserMapper;
	@Autowired
	private CoacherMapper coachMapper;

	@Autowired
	private RedisService redisService;

	@Autowired
	private PayLogMapper payLogMapper;

	@Autowired
	private StatiumDetailMapper statiumDetailMapper ;
	
	@Autowired
	private GamesMapper gameMapper;
	
	@Autowired
	private ActivityMapper activityMapper;
	
	@Resource(name = "oaJdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private FreeSpaceCounter freeSpaceCounter;
	
	@Autowired
	private PayParameter payParameter;
	
	@Autowired
	private OrgCodeUtil orgCodeUtil;
	
	@Autowired
	private OrderNotifyUtil orderNotifyUtil;
	
	@Autowired
	private GamesMemberMapper gamesMemberMapper;
	
	@Resource(name="configs")
	private Map<String,String> configs;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	@Autowired
	private SpacePriceMapper spacePriceMapper = null;
	
	@Autowired
	private StatiumPriceTmplMapper statiumPriceTmplMapper = null;
	
	@Autowired
	private OrderHandleMapper handleMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private UserAccountLogMapper userAccountLogMapper;

	private static Logger logger = LoggerFactory.getLogger(OrderService.class);
	
	@Autowired
	private GamesCorpsMapper gamesCorpsMapper;
	
	@Autowired
	private QiuyouCardLogMapper qiuyouCardLogMapper;
	
	@Autowired
	private OrderSendMsgService orderSendMsgService;
	
	@Autowired
	private StaffAccountLogMapper staffAccountLogMapper;
	
	@Autowired
	private CompanyAccountLogMapper companyAccountLogMapper;
	
	@Autowired
	private OrdersCounter ordersCounter;
	
	@Autowired
	private EnjoyMemberMapper enjoyMemberMapper;

	@Autowired
	private SsoUserBonusAccountLogMapper ssoUserBonusAccountLogMapper;
	
	@Autowired
	private TaipingService taipingServie;
	
	@Autowired
	private LemiLogMapper lemiLogMapper;
	
	
	/**
	 * 页面查询对象.
	 * 
	 * @param searchParams
	 *                查询条件.
	 * @param page
	 *                分页页号, 基于0.
	 * @param size
	 *                分页大小.
	 * @param isPage
	 *                是否分页显示.
	 * @return 分页数据.
	 * @throws Exception
	 */
	public Page<OrderVo> find(Map<String, Object> searchParams, int page, int size, boolean isPage, boolean isHasCount) throws Exception {
		logger.debug(searchParams.toString());
		int total = 0;
		// search_EQ_statiumName
		// 根据场馆名称检索场馆内的订单，那么先要得到场馆的id，再 equals 订单表
		String EQ_statiumId = (String)searchParams.get("EQ_statiumId");
		PageRequest pageable = new PageRequest(page, size);
		Map<String, Object> paras = new HashMap<String, Object>();
		if(StringUtils.isNotEmpty(EQ_statiumId)){
        	paras.put("statiumId",EQ_statiumId);
		}
		String EQ_coachName = (String)searchParams.get("EQ_coachName");
		String EQ_activityName = (String)searchParams.get("EQ_activityName");
		String EQ_gameName = (String)searchParams.get("EQ_gameName");
		if(StringUtils.isNotEmpty(EQ_coachName)){
			CoacherCriteria coachCriteria = new CoacherCriteria();
			coachCriteria.createCriteria().andNameEqualTo(EQ_coachName);
			List<Coacher> coachers = coachMapper.selectByExample(coachCriteria);
			if(coachers.size()==0){
        		return new PageImpl<>(new ArrayList<OrderVo>(), pageable, 0);
			}else if(coachers.size()==1){
        		paras.put("statiumId",coachers.get(0).getId());
        	}else if(coachers.size()>1){
        		String[] statiumIds = new String[coachers.size()];
        		for(int i=0;i<coachers.size();i++){
        			statiumIds[i] = coachers.get(i).getId();
        		}
        		paras.put("statiumIdIn",statiumIds);
        	}
		}
		if(StringUtils.isNotEmpty(EQ_activityName)){
			ActivityCriteria acitvityCriteria = new ActivityCriteria();
			acitvityCriteria.createCriteria().andNameEqualTo(EQ_activityName);
			List<Activity> acitvitys = activityMapper.selectByExample(acitvityCriteria);
			if(acitvitys.size()==0){
        		return new PageImpl<>(new ArrayList<OrderVo>(), pageable, 0);
        	}else if(acitvitys.size()==1){
        		paras.put("statiumId",acitvitys.get(0).getId());
        	}else if(acitvitys.size()>1){
        		String[] statiumIds = new String[acitvitys.size()];
        		for(int i=0;i<acitvitys.size();i++){
        			statiumIds[i] = acitvitys.get(i).getId();
        		}
        		paras.put("statiumIdIn",statiumIds);
        	}
		}
		String EQ_handleMan = (String)searchParams.get("EQ_handleMan");
		if(StringUtils.isNotEmpty(EQ_handleMan)){
			UserCriteria userCriteria = new UserCriteria();
			UserCriteria.Criteria userCri = userCriteria.createCriteria();
			userCri.andNicknameEqualTo(EQ_handleMan);
			List<User> users = userMapper.selectByExample(userCriteria);
			if(users.size()==0){
        		return new PageImpl<>(new ArrayList<OrderVo>(), pageable, 0);
        	}else{
        		paras.put("handler",users.get(0).getUserId());
        	}
		}
		if(StringUtils.isNotEmpty(EQ_gameName)){
			GamesCriteria gameCriteria = new GamesCriteria();
			gameCriteria.createCriteria().andNameEqualTo(EQ_gameName);
			List<Games> Games = gameMapper.selectByExample(gameCriteria);
			if(Games.size()==0){
        		return new PageImpl<>(new ArrayList<OrderVo>(), pageable, 0);
        	}else if(Games.size()==1){
        		paras.put("statiumId",Games.get(0).getId());
        	}else if(Games.size()>1){
        		String[] statiumIds = new String[Games.size()];
        		for(int i=0;i<Games.size();i++){
        			statiumIds[i] = Games.get(i).getId();
        		}
        		paras.put("statiumIdIn",statiumIds);
        	}
		}
		if (searchParams.get("EQ_userPhone") != null && CommonUtils.isNotEmpty((String) searchParams.get("EQ_userPhone"))) {
			String phone = (String) searchParams.get("EQ_userPhone");
			SsoUserCriteria criteria = new SsoUserCriteria();
			SsoUserCriteria.Criteria c = criteria.createCriteria();
			c.andPhoneEqualTo(phone);
			List<SsoUser> users = ssoUserMapper.selectByExample(criteria);
			if (users.size() == 0) {
				return new PageImpl<>(new ArrayList<OrderVo>(), pageable, 0);
			} else {
				if(users.size()>1){
					paras.put("customerIdIn", new String[]{users.get(0).getId(),users.get(1).getId()});
				}else{
					paras.put("customerId", users.get(0).getId());
				}
			}
		}
		
		if (searchParams.get("EQ_status") != null && CommonUtils.isNotEmpty((String) searchParams.get("EQ_status"))) {
			paras.put("status", (String) searchParams.get("EQ_status"));
		}
		
		if (searchParams.get("LIKE_id") != null && CommonUtils.isNotEmpty((String) searchParams.get("LIKE_id"))) {
			paras.put("orderId", (String) searchParams.get("LIKE_id"));
		}
		
		if (searchParams.get("EQ_verifyFlag") != null && CommonUtils.isNotEmpty((String) searchParams.get("EQ_verifyFlag"))) {
			if("1".equals((String) searchParams.get("EQ_verifyFlag"))){
				paras.put("verifyFlag", 1);
			}
		}
		
		if (searchParams.get("EQ_ordersType") != null && CommonUtils.isNotEmpty((String) searchParams.get("EQ_ordersType"))) {
			paras.put("ordersType", (String) searchParams.get("EQ_ordersType"));
		}else{
			paras.put("ordersTypeIn", new Integer[]{0,1,2,4,6,7});
		}
		
		if(searchParams.get("LIKE_areaCode") != null && CommonUtils.isNotEmpty((String) searchParams.get("LIKE_areaCode"))){
        	String areaCode = (String) searchParams.get("LIKE_areaCode");
    		if(areaCode.endsWith("0000")){
    			areaCode = areaCode.substring(0,2)+"%";
    		}else if(areaCode.endsWith("00")){
    			areaCode = areaCode.substring(0,4)+"%";
    		}else{
    			
    		}
        	paras.put("areaCode", areaCode);
        }
		
		if (searchParams.get("GTE_ct") != null && CommonUtils.isNotEmpty((String) searchParams.get("GTE_ct"))) {
			paras.put("GTE_createTime", ((String)searchParams.get("GTE_ct"))+" 00:00:00");
		}
		
		if (searchParams.get("LTE_ct") != null && CommonUtils.isNotEmpty((String) searchParams.get("LTE_ct"))) {
			paras.put("LTE_createTime", ((String)searchParams.get("LTE_ct"))+" 23:59:59");
		}
		
		if (searchParams.get("GTE_et") != null && CommonUtils.isNotEmpty((String) searchParams.get("GTE_et"))) {
			paras.put("GTE_editTime", ((String)searchParams.get("GTE_et"))+" 00:00:00");
		}
		
		if (searchParams.get("LTE_et") != null && CommonUtils.isNotEmpty((String) searchParams.get("LTE_et"))) {
			paras.put("LTE_editTime", ((String)searchParams.get("LTE_et"))+" 23:59:59");
		}

		if(searchParams.get("EQ_channel") != null && CommonUtils.isNotEmpty((String)searchParams.get("EQ_channel"))){
			paras.put("EQ_channel",searchParams.get("EQ_channel"));
		}
		
		String sqlCount = FreeMarkerUtils.format("/template/order/order_search_count.ftl", paras);
        logger.debug(sqlCount);
        logger.debug(paras.toString());
        if (isHasCount) {
        	List<Map<String,Object>> countMap = jdbcTemplate.queryForList(sqlCount);
        	total = new BigDecimal((long)countMap.get(0).get("cont")).intValue();
		}
        if (isPage) {
        	if(pageable.getOffset()>= 1000){
            	paras.put("offset", String.valueOf(pageable.getOffset()).replace(",",""));
            }else{
            	paras.put("offset", pageable.getOffset());
            }
            paras.put("pageSize", pageable.getPageSize());
        }
        String sqlList = FreeMarkerUtils.format("/template/order/order_search.ftl", paras);
        List<Map<String,Object>> orders = jdbcTemplate.queryForList(sqlList);
        List<OrderVo> orderList = new ArrayList<OrderVo>();
        OrderVo vo = null;
        for(Map<String, Object> order:orders){
        	vo = new OrderVo();
        	Date ct = (Date)order.get("ct");
			Date et = (Date)order.get("et");
			String id = (String)order.get("id");
			String status = (String)order.get("status");
			Integer payType = (Integer)order.get("payType");
			Integer ordersType = (Integer)order.get("ordersType");
			Integer verifyFlag = (Integer)order.get("verifyFlag");
			Object channel =  order.get("channel");
            if(channel != null){
				vo.setChannel(Integer.valueOf(String.valueOf(channel)));
			}
			vo.setVerifyFlag(verifyFlag);
			vo.setId(id);
			vo.setCt(ct);
			vo.setEt(et);
			vo.setStatus(status);
			vo.setPayType(payType);
			vo.setOrdersType(ordersType);
			String statiumId = (String)order.get("statiumId");
			vo.setStatiumId(statiumId);
			if(StringUtils.isNotBlank(statiumId)){
				if(ordersType==0){//订场
					StatiumDetail detail = this.selectByPrimaryKey(StatiumDetail.class, statiumId);
					if(detail!=null){
						Map<String,String> zoneMap = Zonemap.split(detail.getAreaCode());
						vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city")+zoneMap.get("area"));
						vo.setName(detail.getName());
						vo.setAddress(detail.getAddress());
						vo.setUserPhone(detail.getTel());
					}
				}else if(ordersType==1){//教培
					Coacher coach = this.selectByPrimaryKey(Coacher.class, statiumId);
					if(coach!=null){
						if(StringUtils.isNotEmpty(coach.getArea())){
							Map<String,String> zoneMap = Zonemap.split(coach.getArea());
							vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city")+zoneMap.get("area"));
						}
						vo.setName(coach.getName());
						vo.setAddress(coach.getPhone());
					}
				}else if(ordersType==2){//活动
					Activity activity = this.selectByPrimaryKey(Activity.class, statiumId);
					if(activity!=null){
						vo.setAreaStr(activity.getProvince()+activity.getCity()+activity.getArea());
						vo.setName(activity.getName());
						vo.setAddress(activity.getAddress());
					}
				}else if(ordersType==4){//赛事
					Games game = this.selectByPrimaryKey(Games.class, statiumId);
					if(game!=null){
						Map<String,String> zoneMap = Zonemap.split(game.getAreaCode());
						vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city")+zoneMap.get("area"));
						vo.setName(game.getName());
						vo.setAddress(game.getAddress());
					}
				}else if(ordersType==6){//乐享赛事
                    EnjoyGame enjoyGame = this.selectByPrimaryKey(EnjoyGame.class, statiumId);
                    if(enjoyGame!=null&&StringUtils.isNotBlank(enjoyGame.getGid())){
                    	enjoyGame = this.selectByPrimaryKey(EnjoyGame.class, enjoyGame.getGid());
                    	
                    }
                    if(enjoyGame!=null){
                    	StatiumDetail detail = this.selectByPrimaryKey(StatiumDetail.class, enjoyGame.getStatiumId());
    					if(detail!=null){
    						Map<String,String> zoneMap = Zonemap.split(detail.getAreaCode());
    						vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city")+zoneMap.get("area"));
    						vo.setName(detail.getName());
    						vo.setAddress(detail.getAddress());
    					}
                    }
                }
			}
			if(order.get("finalFee")!=null){
				Double finel = BigDecimal.valueOf((Integer)order.get("finalFee")).multiply(BigDecimal.valueOf(0.01)).doubleValue();
				vo.setFinalFee(finel.intValue());
			}
			if(order.get("qiuyouFee")!=null){
				Double qiuyouFee = BigDecimal.valueOf((Integer)order.get("qiuyouFee")).multiply(BigDecimal.valueOf(0.01)).doubleValue();
                vo.setQiuyouFee(qiuyouFee.intValue());
			}else{
				vo.setQiuyouFee(0);
			}
			if(order.get("customerId")!=null){
    			String customerId = (String)order.get("customerId");
    			SsoUser user = this.selectByPrimaryKey(SsoUser.class,customerId);
    			if(user!=null){
    				String phone = user.getPhone();
            		vo.setPhone(phone);
            		vo.setUsername(user.getNickName());
    			}
    		}
			OrderItemCriteria itemCriteria = new OrderItemCriteria();
			OrderItemCriteria.Criteria itemCri = itemCriteria.createCriteria();
			itemCri.andOrderIdEqualTo(vo.getId());
			List<OrderItem> items = orderItemMapper.selectByExample(itemCriteria);
			if(CollectionUtils.isNotEmpty(items)){
				OrderItem item_ = items.get(0);
				String sportType = item_.getSportType();
				vo.setSportType(CommonOAUtils.sportsEToC(sportType));
				String orderDate = "";
				String orderTime = "";
				for(OrderItem item:items){
					orderDate = DateUtil.formatDate(item.getStartDate(),"yyyy-MM-dd");
					String temp = "";
                 	if(item.getStartTime()!=null){
                 		temp = DateFormatUtils.format(new Date(item.getStartTime()*1000L),"HH:mm:ss");
                 	}
                 	if(item.getEndTime()!=null){
                 		temp += DateFormatUtils.format(new Date(item.getEndTime()*1000L),"~HH:mm:ss ");
                 	}
                 	String spaceName = item.getSpaceName()+"【"+item.getSpaceCode()+"】";
                 	
                 	orderTime+=spaceName+":"+temp;
				}
				vo.setOrderTime(orderTime);
				vo.setOrderDate(orderDate);
			}
			Integer handleStatus = (Integer)order.get("handleStatus");
			if(handleStatus==null){
				if(order.get("handler")!=null){
					vo.setHandleStatusStr("处理中");
				}else{
					
				}
			}else if(handleStatus==1){
				vo.setHandleStatusStr("已处理");
			}else if(handleStatus==2){
				vo.setHandleStatusStr("稍后处理");
			}
			if(order.get("handler")!=null){
				String handler = (String)order.get("handler");
				User user = userMapper.selectByPrimaryKey(handler);
				vo.setHandleName(user==null?null:user.getNickname());
			}
			OrderHandleCriteria handleCriteria = new OrderHandleCriteria();
			OrderHandleCriteria.Criteria handleCri = handleCriteria.createCriteria();
			handleCri.andOrderIdEqualTo(id);
			handleCriteria.setOrderByClause("ct desc");
			handleCriteria.setMysqlOffset(0);
			handleCriteria.setMysqlLength(1);
			List<OrderHandle> orderHandlers = handleMapper.selectByExample(handleCriteria);
			if(CollectionUtils.isNotEmpty(orderHandlers)){
				vo.setReason(orderHandlers.get(0).getComment());
			}
			orderList.add(vo);
        }
		return new PageImpl<>(orderList, pageable, total);
	}
	
	/**
	 * 获取乐享赛订单
	 * @param search
	 * @param page
	 * @param size
	 * @param isPage
	 * @return
	 * @throws Exception 
	 */
	public Page<OrderVo> findEnjoyOrders(EnjoyOrderSearch search, int page, int size, boolean isPage) throws Exception {
		Map<String, Object> paras = new HashMap<String, Object>();
		List<OrderVo> orderList = new ArrayList<OrderVo>();
		if(StringUtils.isNotBlank(search.getCity())){
			paras.put("city", search.getCity());
		}
		if(StringUtils.isNotBlank(search.getEndTime())){
			paras.put("endTime", search.getEndTime()+" 23:59");
		}
		if(StringUtils.isNotBlank(search.getName())){
			paras.put("name", search.getName());
		}
		if(StringUtils.isNotBlank(search.getOrderId())){
			paras.put("orderId", search.getOrderId());
		}
		if(StringUtils.isNotBlank(search.getpCard())){
			paras.put("pCard", search.getpCard());
		}
		if(StringUtils.isNotBlank(search.getPhone())){
			paras.put("phone", search.getPhone());
		}
		if(StringUtils.isNotBlank(search.getpName())){
			paras.put("pName", search.getpName());
		}
		if(StringUtils.isNotBlank(search.getStartTime())){
			paras.put("startTime", search.getStartTime()+" 00:00");
		}
		if(StringUtils.isNotBlank(search.getStatiumId())){
			paras.put("statiumId", search.getStatiumId());
		}
		if(StringUtils.isNotBlank(search.getGameLevel())){
			paras.put("gameLevel", search.getGameLevel());
		}
		if(StringUtils.isNotBlank(search.getGameType())){
			paras.put("gameType", search.getGameType());
		}
		
		if(StringUtils.isNotBlank(search.getCt())){
			paras.put("ct0", search.getCt()+" 00:00:00");
			paras.put("ct1", search.getCt()+" 23:59:59");
		}
		logger.debug(MyGson.getInstance().toJson(search));
		int total = 0;
		PageRequest pageable = null;
		if (isPage) {
			pageable = new PageRequest(page, size);
			String sqlCount = FreeMarkerUtils.format("/template/order/order_game_count.ftl", paras);
        	List<Map<String,Object>> countMap = jdbcTemplate.queryForList(sqlCount);
        	total = new BigDecimal((long)countMap.get(0).get("cont")).intValue();
        	if(total==0){
        		return new PageImpl<>(orderList, pageable, total);
        	}
        	if(pageable.getOffset()>= 1000){
        		paras.put("offset", String.valueOf(pageable.getOffset()).replace(",",""));
        	}else{
        		paras.put("offset", pageable.getOffset());
        	}
        	paras.put("pageSize", pageable.getPageSize());
		}
		
		String sqlList = FreeMarkerUtils.format("/template/order/order_game_list.ftl", paras);
		logger.debug("sql:{}",sqlList);
        List<Map<String,Object>> orders = jdbcTemplate.queryForList(sqlList);
        OrderVo vo = null;
        for(Map<String, Object> order:orders){
        	vo = new OrderVo();
        	Date ct = (Date)order.get("ct");
        	Date et = (Date)order.get("et");
			String id = (String)order.get("id");
			String status = (String)order.get("status");
			Integer payType = (Integer)order.get("payType");
			Integer ordersType = (Integer)order.get("ordersType");
			vo.setId(id);
			vo.setCt(ct);
			vo.setOrderDate(DateUtil.formatDate(ct, "yyyy-MM-dd"));
			vo.setEt(et);
			if(status.equals("ORDER_VERIFY")){
				vo.setStatus("已确认");
			}else if(status.equals("ORDER_PLAYING")){
				vo.setStatus("交易成功");
			}else if(status.equals("ORDER_PAIED")){
				vo.setStatus("已支付");
			}else if(status.equals("ORDER_REFUNDING")){
				vo.setStatus("退款中");
			}else if(status.equals("ORDER_REFUNDED")){
				vo.setStatus("已退款");
			}
			vo.setPayType(payType);
			
			vo.setOrdersType(ordersType);
			String statiumId = (String)order.get("statiumId");
			vo.setStatiumId(statiumId);
			if(order.get("finalFee")!=null){
				Double finel = BigDecimal.valueOf((Integer)order.get("finalFee")).multiply(BigDecimal.valueOf(0.01)).doubleValue();
				vo.setFinalFee(finel.intValue());
			}
			if(order.get("qiuyouFee")!=null){
				Double qiuyouFee = BigDecimal.valueOf((Integer)order.get("qiuyouFee")).multiply(BigDecimal.valueOf(0.01)).doubleValue();
                vo.setQiuyouFee(qiuyouFee.intValue());
			}else{
				vo.setQiuyouFee(0);
			}
			if(payType == Constants.PayType.ACCOUNT){
				//账户支付查看是否有奖金支付
				SsoUserBonusAccountLogCriteria bonusAccountLogC = new SsoUserBonusAccountLogCriteria();
				SsoUserBonusAccountLogCriteria.Criteria bonusAccountLogCri = bonusAccountLogC.createCriteria();
				bonusAccountLogCri.andOrderIdEqualTo(vo.getId());
				bonusAccountLogCri.andDescriptionLike("消费");
				if(ssoUserBonusAccountLogMapper.countByExample(bonusAccountLogC) > 0) {
					SsoUserBonusAccountLog bonusAccountLog = ssoUserBonusAccountLogMapper.selectByExample(bonusAccountLogC).get(0);
					vo.setBounsAccountFee(-BigDecimal.valueOf(bonusAccountLog.getAmount()).multiply(BigDecimal.valueOf(0.01)).intValue());
					logger.debug("奖金额,{}",vo.getBounsAccountFee());
					logger.debug("订单号,{}",vo.getId());
					logger.debug("finalFee,{}",vo.getFinalFee());
					vo.setAcountFee(vo.getFinalFee() - vo.getBounsAccountFee());
				}else {
					vo.setAcountFee(vo.getFinalFee());
				}
			}
			if(order.get("couponId")!=null){
    			CouponHistory couponHistory = this.selectByPrimaryKey(CouponHistory.class,(String)order.get("couponId"));
    			Double couponSum = 0.0d;
    			if(couponHistory!=null){
    				String orderTime = couponHistory.getOrderTime();
    				if(StringUtils.isEmpty(orderTime)){
    					orderTime = "1";
    				}
    				couponSum = (couponHistory.getAmount().multiply(new BigDecimal(Integer.parseInt(orderTime)))).doubleValue();
    			}
    			vo.setSubAmount(couponSum.intValue());
    		}
			if(StringUtils.isNotBlank(statiumId)){
				EnjoyGame enjoyGame = this.selectByPrimaryKey(EnjoyGame.class, statiumId);
				EnjoyGame enjoyGame_ = enjoyGame;
                if(enjoyGame!=null&&StringUtils.isNotBlank(enjoyGame.getGid())){
                	enjoyGame_ = this.selectByPrimaryKey(EnjoyGame.class, enjoyGame.getGid());
                }
                int gameType = enjoyGame.getGameType();
            	String gameTypeStr = "";
            	int base = 1;
            	if(gameType==1){
            		gameTypeStr = "男子单打";
            	}else if(gameType==2){
            		gameTypeStr = "女子单打";
            	}else if(gameType==3){
            		base = 2;
            		gameTypeStr = "男子双打";
            	}else if(gameType==4){
            		base = 2;
            		gameTypeStr = "女子双打";
            	}else if(gameType==5){
            		base = 2;
            		gameTypeStr = "混合双打";
            	}else if(gameType==6){
            		gameTypeStr = "混合单打";
            	}else if(gameType==7){
            		base = 2;
            		gameTypeStr = "无性别限制双打";
            	}
            	vo.setGameType(gameTypeStr);
                if(enjoyGame_!=null){
                	int gameLevel = enjoyGame_.getGameLevel();
                	vo.setCityHoldTimes(enjoyGame_.getCityHoldTimes());
                	String gameLevelStr = "";
                	if(gameLevel==1){
                		gameLevelStr = "乐享一级赛";
                	}else if(gameLevel==2){
                		gameLevelStr = "乐享二级赛";
                	}else if(gameLevel==3){
                		gameLevelStr = "乐享三级赛";
                	}else if(gameLevel==4){
                		gameLevelStr = "乐享四级赛";
                	}
                    vo.setGameLevel(gameLevelStr);
                    String statiumId_ = enjoyGame_.getStatiumId();
                    StatiumDetail statium = this.selectByPrimaryKey(StatiumDetail.class, statiumId_);
                    String siteId = enjoyGame_.getSiteId();
                    EnjoyGameSite site = this.selectByPrimaryKey(EnjoyGameSite.class, siteId);
                    vo.setName(statium.getName());
                    vo.setAddress(statium.getAddress());
                    vo.setAreaStr("市辖区".equals(site.getCity())?site.getProvince():site.getCity());
                    vo.setAreaStr(vo.getAreaStr()+(enjoyGame_.getCityHoldTimes()==null?"":"(第"+enjoyGame_.getCityHoldTimes()+"站)"));
                    vo.setEnjoyName(enjoyGame_.getName()+enjoyGame_.getShortTitle()+"(第"+enjoyGame_.getHoldTimes()+"站)");
                    vo.setPhone(enjoyGame_.getTel());
                    vo.setStartTime(enjoyGame_.getStartTime());
                    vo.setEndTime(enjoyGame_.getEndTime());
                    EnjoyMemberCriteria criteria = new EnjoyMemberCriteria();
                    EnjoyMemberCriteria.Criteria cri = criteria.createCriteria();
                    cri.andOrderIdEqualTo(id);
                    List<EnjoyMember> members = enjoyMemberMapper.selectByExample(criteria);
                    String pName = "";
                    String pCard = "";
                    String pNum = "";
                    String pPhone = "";
                    int bonus = 0;
                    for(EnjoyMember member:members){
                    	String playerId = member.getPlayerId();
                    	GamesPlayer player = this.selectByPrimaryKey(GamesPlayer.class, playerId);
                    	if(player!=null){
                    		pName += player.getName()+",";
                    		pCard += player.getCardNo()+",";
                    		String userid = player.getUserId();
                    		SsoUser user = this.selectByPrimaryKey(SsoUser.class, userid);
                    		pPhone += user.getPhone()+",";
                    	}
                    	if(member.getCt()!=null){
                    		vo.setPct(DateUtil.formatDate(member.getCt(), "yyyy-MM-dd HH:mm:ss"));
                    	}
                    	EnjoyMemberCriteria criteria_ = new EnjoyMemberCriteria();
                    	EnjoyMemberCriteria.Criteria cri_ = criteria_.createCriteria();
                    	cri_.andStateEqualTo(1);
                    	cri_.andPlayerIdEqualTo(playerId);
                    	int hzn = enjoyMemberMapper.countByExample(criteria_);
                    	pNum +=hzn+",";
                    	if(member.getBonus()!=null){
                    		bonus +=member.getBonus();
                    	}
                    }
                    if(StringUtils.isNotBlank(pName)){
                    	vo.setPname(pName.substring(0, pName.length()-1));
                    	vo.setPcard(pCard.substring(0, pCard.length()-1));
                    	vo.setPphone(pPhone.substring(0, pPhone.length()-1));
                    }
                    vo.setPnum(pNum.substring(0, pNum.length()-1));
                    vo.setGameBonus(bonus);
                    Double price = BigDecimal.valueOf(enjoyGame.getPrice()).multiply(BigDecimal.valueOf(0.01)).doubleValue();
                    vo.setBmFee(price.intValue());
                    Double fee = 0.00d;
                    if(order.get("fee")!=null){
        				fee = BigDecimal.valueOf((Integer)order.get("fee")).multiply(BigDecimal.valueOf(0.01)).doubleValue();
        			}
                    vo.setBmFee(fee.intValue());
                    boolean doubleFlag = true;
                    logger.debug("订单金额，{}",fee.intValue());
                    logger.debug("报名费，{}",price.intValue());
                    logger.debug("是否相等，{}",fee.intValue()==price.intValue());
                    logger.debug("基数，{}",base);
                    
                    if(gameType==1){
                    	doubleFlag = false;
                	}else if(gameType==2){
                		doubleFlag = false;
                	}else if(gameType==3){
                		
                	}else if(gameType==4){
                		
                	}else if(gameType==5){
                		
                	}else if(gameType==6){
                		doubleFlag = false;
                	}else if(gameType==7){
                		
                	}
                    
                    if(fee.intValue()!=price.intValue()&&doubleFlag){
                    	doubleFlag = false;
                    	base = 1;
                    }
                    if(enjoyGame_.getGameLevel()==1){
                    	vo.setCdFee(fee.intValue()-40*base);
                    	vo.setSrFee(40*base);
                    }else if(enjoyGame_.getGameLevel()==2){
                    	vo.setCdFee(fee.intValue()-30*base);
                    	vo.setSrFee(30*base);
                    }else if(enjoyGame_.getGameLevel()==3){
                    	vo.setCdFee(fee.intValue()-20*base);
                    	vo.setSrFee(20*base);
                    }else if(enjoyGame_.getGameLevel()==4){
                    	vo.setCdFee(fee.intValue()-10*base);
                    	vo.setSrFee(10*base);
                    }
        			orderList.add(vo);
                }
			}
        }
        return new PageImpl<>(orderList, pageable, total);
	}

	public Page<OrderVo> findNuomiList(Map<String, Object> searchParams, int page, int size, boolean isPage, boolean isHasCount) throws Exception {
		logger.debug(searchParams.toString());
		int total = 0;
		// search_EQ_statiumName
		// 根据场馆名称检索场馆内的订单，那么先要得到场馆的id，再 equals 订单表
		String EQ_statiumId = (String)searchParams.get("EQ_statiumId");
		PageRequest pageable = new PageRequest(page, size);
		Map<String, Object> paras = new HashMap<String, Object>();
		if(StringUtils.isNotEmpty(EQ_statiumId)) {
			paras.put("statiumId", EQ_statiumId);
		}
		String EQ_gameName = (String)searchParams.get("EQ_gameName");
		String EQ_handleMan = (String)searchParams.get("EQ_handleMan");
		if(StringUtils.isNotEmpty(EQ_handleMan)){
			UserCriteria userCriteria = new UserCriteria();
			UserCriteria.Criteria userCri = userCriteria.createCriteria();
			userCri.andNicknameEqualTo(EQ_handleMan);
			List<User> users = userMapper.selectByExample(userCriteria);
			if(users.size()==0){
				return new PageImpl<>(new ArrayList<OrderVo>(), pageable, 0);
			}else{
				paras.put("handler",users.get(0).getUserId());
			}
		}
		if(StringUtils.isNotEmpty(EQ_gameName)){
			GamesCriteria gameCriteria = new GamesCriteria();
			gameCriteria.createCriteria().andNameEqualTo(EQ_gameName);
			List<Games> Games = gameMapper.selectByExample(gameCriteria);
			if(Games.size()==0){
				return new PageImpl<>(new ArrayList<OrderVo>(), pageable, 0);
			}else if(Games.size()==1){
				paras.put("statiumId",Games.get(0).getId());
			}else if(Games.size()>1){
				String[] statiumIds = new String[Games.size()];
				for(int i=0;i<Games.size();i++){
					statiumIds[i] = Games.get(i).getId();
				}
				paras.put("statiumIdIn",statiumIds);
			}
		}
		if (searchParams.get("EQ_userPhone") != null && CommonUtils.isNotEmpty((String) searchParams.get("EQ_userPhone"))) {
			String phone = (String) searchParams.get("EQ_userPhone");
			SsoUserCriteria criteria = new SsoUserCriteria();
			SsoUserCriteria.Criteria c = criteria.createCriteria();
			c.andPhoneEqualTo(phone);
			List<SsoUser> users = ssoUserMapper.selectByExample(criteria);
			if (users.size() == 0) {
				return new PageImpl<>(new ArrayList<OrderVo>(), pageable, 0);
			} else {
				if(users.size()>1){
					paras.put("customerIdIn", new String[]{users.get(0).getId(),users.get(1).getId()});
				}else{
					paras.put("customerId", users.get(0).getId());
				}
			}
		}

		if (searchParams.get("EQ_status") != null && CommonUtils.isNotEmpty((String) searchParams.get("EQ_status"))) {
			paras.put("status", (String) searchParams.get("EQ_status"));
		}

		if (searchParams.get("LIKE_id") != null && CommonUtils.isNotEmpty((String) searchParams.get("LIKE_id"))) {
			paras.put("orderId", (String) searchParams.get("LIKE_id"));
		}

		if (searchParams.get("EQ_verifyFlag") != null && CommonUtils.isNotEmpty((String) searchParams.get("EQ_verifyFlag"))) {
			if("1".equals((String) searchParams.get("EQ_verifyFlag"))){
				paras.put("verifyFlag", 1);
			}
		}

		if (searchParams.get("EQ_ordersType") != null && CommonUtils.isNotEmpty((String) searchParams.get("EQ_ordersType"))) {
			paras.put("ordersType", (String) searchParams.get("EQ_ordersType"));
		}else{
			paras.put("ordersTypeIn", new Integer[]{0,1,2,4,6,7});
		}

		if(searchParams.get("LIKE_areaCode") != null && CommonUtils.isNotEmpty((String) searchParams.get("LIKE_areaCode"))){
			String areaCode = (String) searchParams.get("LIKE_areaCode");
			if(areaCode.endsWith("0000")){
				areaCode = areaCode.substring(0,2)+"%";
			}else if(areaCode.endsWith("00")){
				areaCode = areaCode.substring(0,4)+"%";
			}else{

			}
			paras.put("areaCode", areaCode);
		}

		if (searchParams.get("GTE_ct") != null && CommonUtils.isNotEmpty((String) searchParams.get("GTE_ct"))) {
			paras.put("GTE_createTime", ((String)searchParams.get("GTE_ct"))+" 00:00:00");
		}

		if (searchParams.get("LTE_ct") != null && CommonUtils.isNotEmpty((String) searchParams.get("LTE_ct"))) {
			paras.put("LTE_createTime", ((String)searchParams.get("LTE_ct"))+" 23:59:59");
		}

		if (searchParams.get("GTE_et") != null && CommonUtils.isNotEmpty((String) searchParams.get("GTE_et"))) {
			paras.put("GTE_editTime", ((String)searchParams.get("GTE_et"))+" 00:00:00");
		}

		if (searchParams.get("LTE_et") != null && CommonUtils.isNotEmpty((String) searchParams.get("LTE_et"))) {
			paras.put("LTE_editTime", ((String)searchParams.get("LTE_et"))+" 23:59:59");
		}
		paras.put("EQ_channel",4);
		if (searchParams.get("EQ_externalStatus") != null && CommonUtils.isNotEmpty((String) searchParams.get("EQ_externalStatus"))) {
			paras.put("externalStatus",(String) searchParams.get("EQ_externalStatus"));
		}

		String sqlCount = FreeMarkerUtils.format("/template/order/nuomi_order_search_count.ftl", paras);
		logger.debug(sqlCount);
		if (isHasCount) {
			List<Map<String,Object>> countMap = jdbcTemplate.queryForList(sqlCount);
			total = new BigDecimal((long)countMap.get(0).get("cont")).intValue();
		}
		if (isPage) {
			if(pageable.getOffset()>= 1000){
				paras.put("offset", String.valueOf(pageable.getOffset()).replace(",",""));
			}else{
				paras.put("offset", pageable.getOffset());
			}
			paras.put("pageSize", pageable.getPageSize());
		}
		String sqlList = FreeMarkerUtils.format("/template/order/nuomi_order_search.ftl", paras);
		logger.debug("List={}",sqlList);
		List<Map<String,Object>> orders = jdbcTemplate.queryForList(sqlList);
		List<OrderVo> orderList = new ArrayList<OrderVo>();
		OrderVo vo = null;
		for(Map<String, Object> order:orders){
			vo = new OrderVo();
			Date ct = (Date)order.get("ct");
			vo.setCtStr(DateUtil.formatDate(ct, "yyyy-MM-dd HH:mm:ss"));
			Date et = (Date)order.get("et");
			String id = (String)order.get("id");
			String status = (String)order.get("status");
			Integer payType = (Integer)order.get("payType");
			Integer ordersType = (Integer)order.get("ordersType");
			Integer verifyFlag = (Integer)order.get("verifyFlag");
			Object channel =  order.get("channel");
			if(channel != null){
				vo.setChannel(Integer.valueOf(String.valueOf(channel)));
			}
			Object externalStatus =  order.get("externalStatus");
			if(externalStatus != null){
				vo.setExternalStatus(Integer.valueOf(String.valueOf(externalStatus)));
			}
			vo.setVerifyFlag(verifyFlag);
			vo.setId(id);
			vo.setCt(ct);
			vo.setEt(et);
			vo.setStatus(status);
			vo.setPayType(payType);
			vo.setOrdersType(ordersType);
			String statiumId = (String)order.get("statiumId");
			vo.setStatiumId(statiumId);
			if(StringUtils.isNotBlank(statiumId)){
				if(ordersType==0){//订场
					StatiumDetail detail = this.selectByPrimaryKey(StatiumDetail.class, statiumId);
					if(detail!=null){
						Map<String,String> zoneMap = Zonemap.split(detail.getAreaCode());
						vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city")+zoneMap.get("area"));
						vo.setName(detail.getName());
						vo.setAddress(detail.getAddress());
						vo.setUserPhone(detail.getTel());
					}
				}else if(ordersType==1){//教培
					Coacher coach = this.selectByPrimaryKey(Coacher.class, statiumId);
					if(coach!=null){
						if(StringUtils.isNotEmpty(coach.getArea())){
							Map<String,String> zoneMap = Zonemap.split(coach.getArea());
							vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city")+zoneMap.get("area"));
						}
						vo.setName(coach.getName());
						vo.setAddress(coach.getPhone());
					}
				}else if(ordersType==2){//活动
					Activity activity = this.selectByPrimaryKey(Activity.class, statiumId);
					if(activity!=null){
						vo.setAreaStr(activity.getProvince()+activity.getCity()+activity.getArea());
						vo.setName(activity.getName());
						vo.setAddress(activity.getAddress());
					}
				}else if(ordersType==4){//赛事
					Games game = this.selectByPrimaryKey(Games.class, statiumId);
					if(game!=null){
						Map<String,String> zoneMap = Zonemap.split(game.getAreaCode());
						vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city")+zoneMap.get("area"));
						vo.setName(game.getName());
						vo.setAddress(game.getAddress());
					}
				}else if(ordersType==6){//乐享赛事
					EnjoyGame enjoyGame = this.selectByPrimaryKey(EnjoyGame.class, statiumId);
					if(enjoyGame!=null&&StringUtils.isNotBlank(enjoyGame.getGid())){
						enjoyGame = this.selectByPrimaryKey(EnjoyGame.class, enjoyGame.getGid());

					}
					if(enjoyGame!=null){
						StatiumDetail detail = this.selectByPrimaryKey(StatiumDetail.class, enjoyGame.getStatiumId());
						if(detail!=null){
							Map<String,String> zoneMap = Zonemap.split(detail.getAreaCode());
							vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city")+zoneMap.get("area"));
							vo.setName(detail.getName());
							vo.setAddress(detail.getAddress());
						}
					}
				}
			}
			if(order.get("finalFee")!=null){
				Double finel = BigDecimal.valueOf((Integer)order.get("finalFee")).multiply(BigDecimal.valueOf(0.01)).doubleValue();
				vo.setFinalFee(finel.intValue());
			}
			if(order.get("qiuyouFee")!=null){
				Double qiuyouFee = BigDecimal.valueOf((Integer)order.get("qiuyouFee")).multiply(BigDecimal.valueOf(0.01)).doubleValue();
				vo.setQiuyouFee(qiuyouFee.intValue());
			}else{
				vo.setQiuyouFee(0);
			}
			if(order.get("customerId")!=null){
				String customerId = (String)order.get("customerId");
				SsoUser user = this.selectByPrimaryKey(SsoUser.class,customerId);
				if(user!=null){
					String phone = user.getPhone();
					vo.setPhone(phone);
					vo.setUsername(user.getNickName());
				}
			}
			OrderItemCriteria itemCriteria = new OrderItemCriteria();
			OrderItemCriteria.Criteria itemCri = itemCriteria.createCriteria();
			itemCri.andOrderIdEqualTo(vo.getId());
			List<OrderItem> items = orderItemMapper.selectByExample(itemCriteria);
			if(CollectionUtils.isNotEmpty(items)){
				OrderItem item_ = items.get(0);
				String sportType = item_.getSportType();
				vo.setSportType(CommonOAUtils.sportsEToC(sportType));
				String orderDate = "";
				String orderTime = "";
				for(OrderItem item:items){
					orderDate = DateUtil.formatDate(item.getStartDate(),"yyyy-MM-dd");
					String temp = "";
					if(item.getStartTime()!=null){
						temp = DateFormatUtils.format(new Date(item.getStartTime()*1000L),"HH:mm:ss");
					}
					if(item.getEndTime()!=null){
						temp += DateFormatUtils.format(new Date(item.getEndTime()*1000L),"~HH:mm:ss ");
					}
					String spaceName = item.getSpaceName()+"【"+item.getSpaceCode()+"】";

					orderTime+=spaceName+":"+temp;
				}
				vo.setOrderTime(orderTime);
				vo.setOrderDate(orderDate);
			}
			Integer handleStatus = (Integer)order.get("handleStatus");
			if(handleStatus==null){
				if(order.get("handler")!=null){
					vo.setHandleStatusStr("处理中");
				}else{

				}
			}else if(handleStatus==1){
				vo.setHandleStatusStr("已处理");
			}else if(handleStatus==2){
				vo.setHandleStatusStr("稍后处理");
			}
			
			/**订单用户信息**/
    		if(order.get("customerId")!=null){
    			String customerId = (String)order.get("customerId");
    			SsoUser user = this.selectByPrimaryKey(SsoUser.class,customerId);
    			if(user!=null){
    				String phone = user.getPhone();
            		vo.setPhone(phone);
            		vo.setUsername(user.getName());
            		/**订单用户信息**/
            		
            		/**合作次数**/
            		
            		OrderCriteria criteria = new OrderCriteria();
            		OrderCriteria.Criteria c = criteria.createCriteria();
            		c.andCustomerIdEqualTo(customerId);
            		c.andCtLessThanOrEqualTo((Date)order.get("ct"));
            		List<String> statuss = new ArrayList<String>();
            		statuss.add(Constants.OrderStatus.PLAYING);
            		statuss.add(Constants.OrderStatus.PAIED);
            		statuss.add(Constants.OrderStatus.VERIFY);
            		c.andOrderTypeEqualTo("BOOK_NUOMI");
            		c.andStatusIn(statuss);
            		int count = orderMapper.countByExample(criteria);
            		vo.setCooperateNum(count);
            		/**合作次数**/
    			}
    		}
			
			if(order.get("handler")!=null){
				String handler = (String)order.get("handler");
				User user = userMapper.selectByPrimaryKey(handler);
				vo.setHandleName(user==null?null:user.getNickname());
			}
			OrderHandleCriteria handleCriteria = new OrderHandleCriteria();
			OrderHandleCriteria.Criteria handleCri = handleCriteria.createCriteria();
			handleCri.andOrderIdEqualTo(id);
			handleCriteria.setOrderByClause("ct desc");
			handleCriteria.setMysqlOffset(0);
			handleCriteria.setMysqlLength(1);
			List<OrderHandle> orderHandlers = handleMapper.selectByExample(handleCriteria);
			if(CollectionUtils.isNotEmpty(orderHandlers)){
				vo.setReason(orderHandlers.get(0).getComment());
			}
			orderList.add(vo);
		}
		return new PageImpl<>(orderList, pageable, total);
	}
	
	
	public Page<OrderVo> findTaipingList(Map<String, Object> searchParams, int page, int size, boolean isPage, boolean isHasCount) throws Exception {
		logger.debug(searchParams.toString());
		int total = 0;
		// search_EQ_statiumName
		// 根据场馆名称检索场馆内的订单，那么先要得到场馆的id，再 equals 订单表
		String EQ_statiumId = (String)searchParams.get("EQ_statiumId");
		PageRequest pageable = new PageRequest(page, size);
		Map<String, Object> paras = new HashMap<String, Object>();
		if(StringUtils.isNotEmpty(EQ_statiumId)) {
			paras.put("statiumId", EQ_statiumId);
		}
		String EQ_gameName = (String)searchParams.get("EQ_gameName");
		String EQ_handleMan = (String)searchParams.get("EQ_handleMan");
		if(StringUtils.isNotEmpty(EQ_handleMan)){
			UserCriteria userCriteria = new UserCriteria();
			UserCriteria.Criteria userCri = userCriteria.createCriteria();
			userCri.andNicknameEqualTo(EQ_handleMan);
			List<User> users = userMapper.selectByExample(userCriteria);
			if(users.size()==0){
				return new PageImpl<>(new ArrayList<OrderVo>(), pageable, 0);
			}else{
				paras.put("handler",users.get(0).getUserId());
			}
		}
		if(StringUtils.isNotEmpty(EQ_gameName)){
			GamesCriteria gameCriteria = new GamesCriteria();
			gameCriteria.createCriteria().andNameEqualTo(EQ_gameName);
			List<Games> Games = gameMapper.selectByExample(gameCriteria);
			if(Games.size()==0){
				return new PageImpl<>(new ArrayList<OrderVo>(), pageable, 0);
			}else if(Games.size()==1){
				paras.put("statiumId",Games.get(0).getId());
			}else if(Games.size()>1){
				String[] statiumIds = new String[Games.size()];
				for(int i=0;i<Games.size();i++){
					statiumIds[i] = Games.get(i).getId();
				}
				paras.put("statiumIdIn",statiumIds);
			}
		}
		if (searchParams.get("EQ_userPhone") != null && CommonUtils.isNotEmpty((String) searchParams.get("EQ_userPhone"))) {
			String phone = (String) searchParams.get("EQ_userPhone");
			SsoUserCriteria criteria = new SsoUserCriteria();
			SsoUserCriteria.Criteria c = criteria.createCriteria();
			c.andPhoneEqualTo(phone);
			List<SsoUser> users = ssoUserMapper.selectByExample(criteria);
			if (users.size() == 0) {
				return new PageImpl<>(new ArrayList<OrderVo>(), pageable, 0);
			} else {
				if(users.size()>1){
					paras.put("customerIdIn", new String[]{users.get(0).getId(),users.get(1).getId()});
				}else{
					paras.put("customerId", users.get(0).getId());
				}
			}
		}

		if (searchParams.get("EQ_status") != null && CommonUtils.isNotEmpty((String) searchParams.get("EQ_status"))) {
			paras.put("status", (String) searchParams.get("EQ_status"));
		}

		if (searchParams.get("LIKE_id") != null && CommonUtils.isNotEmpty((String) searchParams.get("LIKE_id"))) {
			paras.put("orderId", (String) searchParams.get("LIKE_id"));
		}

		if (searchParams.get("EQ_verifyFlag") != null && CommonUtils.isNotEmpty((String) searchParams.get("EQ_verifyFlag"))) {
			if("1".equals((String) searchParams.get("EQ_verifyFlag"))){
				paras.put("verifyFlag", 1);
			}
		}

		if (searchParams.get("EQ_ordersType") != null && CommonUtils.isNotEmpty((String) searchParams.get("EQ_ordersType"))) {
			paras.put("ordersType", (String) searchParams.get("EQ_ordersType"));
		}else{
			paras.put("ordersTypeIn", new Integer[]{0,1,2,4,6,7});
		}

		if(searchParams.get("LIKE_areaCode") != null && CommonUtils.isNotEmpty((String) searchParams.get("LIKE_areaCode"))){
			String areaCode = (String) searchParams.get("LIKE_areaCode");
			if(areaCode.endsWith("0000")){
				areaCode = areaCode.substring(0,2)+"%";
			}else if(areaCode.endsWith("00")){
				areaCode = areaCode.substring(0,4)+"%";
			}else{

			}
			paras.put("areaCode", areaCode);
		}

		if (searchParams.get("GTE_ct") != null && CommonUtils.isNotEmpty((String) searchParams.get("GTE_ct"))) {
			paras.put("GTE_createTime", ((String)searchParams.get("GTE_ct"))+" 00:00:00");
		}

		if (searchParams.get("LTE_ct") != null && CommonUtils.isNotEmpty((String) searchParams.get("LTE_ct"))) {
			paras.put("LTE_createTime", ((String)searchParams.get("LTE_ct"))+" 23:59:59");
		}

		if (searchParams.get("GTE_et") != null && CommonUtils.isNotEmpty((String) searchParams.get("GTE_et"))) {
			paras.put("GTE_editTime", ((String)searchParams.get("GTE_et"))+" 00:00:00");
		}

		if (searchParams.get("LTE_et") != null && CommonUtils.isNotEmpty((String) searchParams.get("LTE_et"))) {
			paras.put("LTE_editTime", ((String)searchParams.get("LTE_et"))+" 23:59:59");
		}
		if (searchParams.get("EQ_externalStatus") != null && CommonUtils.isNotEmpty((String) searchParams.get("EQ_externalStatus"))) {
			paras.put("externalStatus",(String) searchParams.get("EQ_externalStatus"));
		}
		String sqlCount = FreeMarkerUtils.format("/template/order/taiping_order_search_count.ftl", paras);
		logger.debug(sqlCount);
		if (isHasCount) {
			List<Map<String,Object>> countMap = jdbcTemplate.queryForList(sqlCount);
			total = new BigDecimal((long)countMap.get(0).get("cont")).intValue();
		}
		if (isPage) {
			if(pageable.getOffset()>= 1000){
				paras.put("offset", String.valueOf(pageable.getOffset()).replace(",",""));
			}else{
				paras.put("offset", pageable.getOffset());
			}
			paras.put("pageSize", pageable.getPageSize());
		}
		String sqlList = FreeMarkerUtils.format("/template/order/taiping_order_search.ftl", paras);
		logger.debug("List={}",sqlList);
		List<Map<String,Object>> orders = jdbcTemplate.queryForList(sqlList);
		List<OrderVo> orderList = new ArrayList<OrderVo>();
		OrderVo vo = null;
		for(Map<String, Object> order:orders){
			vo = new OrderVo();
			Date ct = (Date)order.get("ct");
			Date et = (Date)order.get("et");
			String id = (String)order.get("id");
			String status = (String)order.get("status");
			Integer payType = (Integer)order.get("payType");
			Integer ordersType = (Integer)order.get("ordersType");
			Integer verifyFlag = (Integer)order.get("verifyFlag");
			Object channel =  order.get("channel");
			if(channel != null){
				vo.setChannel(Integer.valueOf(String.valueOf(channel)));
			}
			Object externalStatus =  order.get("externalStatus");
			if(externalStatus != null){
				vo.setExternalStatus(Integer.valueOf(String.valueOf(externalStatus)));
			}
			vo.setVerifyFlag(verifyFlag);
			vo.setId(id);
			vo.setCt(ct);
			vo.setEt(et);
			vo.setStatus(status);
			vo.setPayType(payType);
			vo.setOrdersType(ordersType);
			String statiumId = (String)order.get("statiumId");
			vo.setStatiumId(statiumId);
			if(StringUtils.isNotBlank(statiumId)){
				if(ordersType==0){//订场
					StatiumDetail detail = this.selectByPrimaryKey(StatiumDetail.class, statiumId);
					if(detail!=null){
						Map<String,String> zoneMap = Zonemap.split(detail.getAreaCode());
						vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city")+zoneMap.get("area"));
						vo.setName(detail.getName());
						vo.setAddress(detail.getAddress());
						vo.setUserPhone(detail.getTel());
					}
				}else if(ordersType==1){//教培
					Coacher coach = this.selectByPrimaryKey(Coacher.class, statiumId);
					if(coach!=null){
						if(StringUtils.isNotEmpty(coach.getArea())){
							Map<String,String> zoneMap = Zonemap.split(coach.getArea());
							vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city")+zoneMap.get("area"));
						}
						vo.setName(coach.getName());
						vo.setAddress(coach.getPhone());
					}
				}else if(ordersType==2){//活动
					Activity activity = this.selectByPrimaryKey(Activity.class, statiumId);
					if(activity!=null){
						vo.setAreaStr(activity.getProvince()+activity.getCity()+activity.getArea());
						vo.setName(activity.getName());
						vo.setAddress(activity.getAddress());
					}
				}else if(ordersType==4){//赛事
					Games game = this.selectByPrimaryKey(Games.class, statiumId);
					if(game!=null){
						Map<String,String> zoneMap = Zonemap.split(game.getAreaCode());
						vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city")+zoneMap.get("area"));
						vo.setName(game.getName());
						vo.setAddress(game.getAddress());
					}
				}else if(ordersType==6){//乐享赛事
					EnjoyGame enjoyGame = this.selectByPrimaryKey(EnjoyGame.class, statiumId);
					if(enjoyGame!=null&&StringUtils.isNotBlank(enjoyGame.getGid())){
						enjoyGame = this.selectByPrimaryKey(EnjoyGame.class, enjoyGame.getGid());

					}
					if(enjoyGame!=null){
						StatiumDetail detail = this.selectByPrimaryKey(StatiumDetail.class, enjoyGame.getStatiumId());
						if(detail!=null){
							Map<String,String> zoneMap = Zonemap.split(detail.getAreaCode());
							vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city")+zoneMap.get("area"));
							vo.setName(detail.getName());
							vo.setAddress(detail.getAddress());
						}
					}
				}
			}
			if(order.get("finalFee")!=null){
				Double finel = BigDecimal.valueOf((Integer)order.get("finalFee")).multiply(BigDecimal.valueOf(0.01)).doubleValue();
				vo.setFinalFee(finel.intValue());
			}
			
			if(order.get("lxFee")!=null){
				Double lxFee = BigDecimal.valueOf((Integer)order.get("lxFee")).multiply(BigDecimal.valueOf(0.01)).doubleValue();
				vo.setLxFee(lxFee.intValue());
			}
			
			if(order.get("qiuyouFee")!=null){
				Double qiuyouFee = BigDecimal.valueOf((Integer)order.get("qiuyouFee")).multiply(BigDecimal.valueOf(0.01)).doubleValue();
				vo.setQiuyouFee(qiuyouFee.intValue());
			}else{
				vo.setQiuyouFee(0);
			}
			if(order.get("customerId")!=null){
				String customerId = (String)order.get("customerId");
				SsoUser user = this.selectByPrimaryKey(SsoUser.class,customerId);
				if(user!=null){
					String phone = user.getPhone();
					vo.setPhone(phone);
					vo.setUsername(user.getNickName());
				}
			}
			OrderItemCriteria itemCriteria = new OrderItemCriteria();
			OrderItemCriteria.Criteria itemCri = itemCriteria.createCriteria();
			itemCri.andOrderIdEqualTo(vo.getId());
			List<OrderItem> items = orderItemMapper.selectByExample(itemCriteria);
			if(CollectionUtils.isNotEmpty(items)){
				OrderItem item_ = items.get(0);
				String sportType = item_.getSportType();
				vo.setSportType(CommonOAUtils.sportsEToC(sportType));
				String orderDate = "";
				String orderTime = "";
				for(OrderItem item:items){
					orderDate = DateUtil.formatDate(item.getStartDate(),"yyyy-MM-dd");
					String temp = "";
					if(item.getStartTime()!=null){
						temp = DateFormatUtils.format(new Date(item.getStartTime()*1000L),"HH:mm:ss");
					}
					if(item.getEndTime()!=null){
						temp += DateFormatUtils.format(new Date(item.getEndTime()*1000L),"~HH:mm:ss ");
					}
					String spaceName = item.getSpaceName()+"【"+item.getSpaceCode()+"】";

					orderTime+=spaceName+":"+temp;
				}
				vo.setOrderTime(orderTime);
				vo.setOrderDate(orderDate);
			}
			Integer handleStatus = (Integer)order.get("handleStatus");
			if(handleStatus==null){
				if(order.get("handler")!=null){
					vo.setHandleStatusStr("处理中");
				}else{

				}
			}else if(handleStatus==1){
				vo.setHandleStatusStr("已处理");
			}else if(handleStatus==2){
				vo.setHandleStatusStr("稍后处理");
			}
			if(order.get("handler")!=null){
				String handler = (String)order.get("handler");
				User user = userMapper.selectByPrimaryKey(handler);
				vo.setHandleName(user==null?null:user.getNickname());
			}
			OrderHandleCriteria handleCriteria = new OrderHandleCriteria();
			OrderHandleCriteria.Criteria handleCri = handleCriteria.createCriteria();
			handleCri.andOrderIdEqualTo(id);
			handleCriteria.setOrderByClause("ct desc");
			handleCriteria.setMysqlOffset(0);
			handleCriteria.setMysqlLength(1);
			List<OrderHandle> orderHandlers = handleMapper.selectByExample(handleCriteria);
			if(CollectionUtils.isNotEmpty(orderHandlers)){
				vo.setReason(orderHandlers.get(0).getComment());
			}
			orderList.add(vo);
		}
		return new PageImpl<>(orderList, pageable, total);
	}

	/**
	 * 根据主键查询对象.
	 * 
	 * @param id
	 * @return 订单.
	 * @throws Exception
	 */
	public Order getOrder(String id) throws Exception {
		return this.selectByPrimaryKey(Order.class, id);
	}

	/**
	 * 根据主键查询VO对象.
	 * 
	 * @param id
	 * @return 订单扩展.
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	public OrderVo getOrderVo(String id) throws Exception {
		OrderVo orderVo = new OrderVo();
		if (CommonUtils.isNotEmpty(id)) {
			Order order = this.getOrder(id);
			if (order == null) {
				return null;
			}
			BeanUtils.copyProperties(order, orderVo);
			if(order.getPayType() == Constants.PayType.ACCOUNT){
				//账户支付查看是否有奖金支付
				SsoUserBonusAccountLogCriteria bonusAccountLogC = new SsoUserBonusAccountLogCriteria();
				SsoUserBonusAccountLogCriteria.Criteria bonusAccountLogCri = bonusAccountLogC.createCriteria();
				bonusAccountLogCri.andOrderIdEqualTo(order.getId());
				bonusAccountLogCri.andDescriptionLike("消费");
				if(ssoUserBonusAccountLogMapper.countByExample(bonusAccountLogC) > 0) {
					SsoUserBonusAccountLog bonusAccountLog = ssoUserBonusAccountLogMapper.selectByExample(bonusAccountLogC).get(0);
					orderVo.setBounsAccountFee(-bonusAccountLog.getAmount());
					orderVo.setAcountFee(order.getFinalFee() + bonusAccountLog.getAmount());
				}else {
					orderVo.setAcountFee(order.getFinalFee());
				}
			}

			SsoUser user = null;
			if (CommonUtils.isNotEmpty(orderVo.getCustomerId())) {
				user = this.selectByPrimaryKey(SsoUser.class, orderVo.getCustomerId());
				if (user != null) {
					orderVo.setUserPhone(user.getPhone() != null ? user.getPhone() : "");
					orderVo.setUserName(user.getNickName() != null ? user.getNickName() : "");
				}
			}
			StatiumDetail statiumDetail = null;
			CoachVo coachVo = null;
			ActivityVo activityVo = null;
			MemberList memberList = null;
			GamesVo gameVo = null;
			if (CommonUtils.isNotEmpty(orderVo.getStatiumId())) {
				if (Constants.OrderType.STATIUM == orderVo.getOrdersType()) {
					statiumDetail = this.selectByPrimaryKey(StatiumDetail.class, orderVo.getStatiumId());
					if (statiumDetail != null) {
						orderVo.setAreaStr(Zonemap.toString(statiumDetail.getAreaCode()));
					}
				} else if (Constants.OrderType.COACH == orderVo.getOrdersType()) {
					coachVo = new CoachVo();

					CoacherCriteria criteria = new CoacherCriteria();
					CoacherCriteria.Criteria c = criteria.createCriteria();
					c.andIdEqualTo(orderVo.getStatiumId());
					List<Coacher> mes = coachMapper.selectByExample(criteria);
					// Coach coach =
					// this.selectByPrimaryKey(Coach.class,
					// orderVo.getStatiumId());
					Coacher coach = mes != null && mes.size() != 0 ? mes.get(0) : null;
					BeanUtils.copyProperties(coach != null ? coach : new Coach(), coachVo);
				}else if (Constants.OrderType.EVENT == orderVo.getOrdersType()) {
					gameVo = new GamesVo();
					Games game = this.selectByPrimaryKey(Games.class, order.getStatiumId());
					BeanUtils.copyProperties(game, gameVo);
					//团体
					if(game.getType()==1){
						GamesCorpsCriteria gamesCorpsCriteria = new GamesCorpsCriteria();
						GamesCorpsCriteria.Criteria gamesCorpsCri = gamesCorpsCriteria.createCriteria();
						gamesCorpsCri.andOrderIdEqualTo(order.getId());
						List<GamesCorps> gamesCorps = gamesCorpsMapper.selectByExample(gamesCorpsCriteria);
						if(CollectionUtils.isNotEmpty(gamesCorps)){
							CorpsInfo corps = this.selectByPrimaryKey(CorpsInfo.class, gamesCorps.get(0).getCorpsId());
							String corpName = corps.getName();
							gameVo.setCorpsName(corpName);
						}
					}else{
						if(user!=null){
							gameVo.setUserName(user.getNickName());
						}
					}
					
				}
				else if (Constants.OrderType.ACTIVITY == orderVo.getOrdersType()) {
					Activity activity = this.selectByPrimaryKey(Activity.class, orderVo.getStatiumId());
					activityVo = new ActivityVo();
					BeanUtils.copyProperties(activity != null ? activity : new Activity(), activityVo);

					MemberListCriteria criteria = new MemberListCriteria();
					MemberListCriteria.Criteria c = criteria.createCriteria();
					c.andOrderidEqualTo(orderVo.getId());
					List<MemberList> mes = memberListMapper.selectByExample(criteria);

					memberList = mes != null && mes.size() != 0 ? mes.get(0) : null;
				}else if (Constants.OrderType.BOOKBALL == orderVo.getOrdersType()) {
					statiumDetail = this.selectByPrimaryKey(StatiumDetail.class, orderVo.getStatiumId());
					if (statiumDetail != null) {
						orderVo.setAreaStr(Zonemap.toString(statiumDetail.getAreaCode()));
					}
				}
			}
			if (CommonUtils.isNotEmpty(orderVo.getCouponId())) {
				CouponHistory couponHistory = this.selectByPrimaryKey(CouponHistory.class, orderVo.getCouponId());
				if (couponHistory != null && couponHistory.getAmount() != null) {
	    				String orderTime = couponHistory.getOrderTime();
	    				if(StringUtils.isEmpty(orderTime)){
	    					orderTime = "1";
	    				}
					orderVo.setCouponAmount((couponHistory.getAmount().multiply(new BigDecimal(Integer.parseInt(orderTime)))).doubleValue());
				}
			}

			OrderItemCriteria cc = new OrderItemCriteria();
			OrderItemCriteria.Criteria cri = cc.createCriteria();
			cri.andOrderIdEqualTo(id);
			List<OrderItem> orderItem = orderItemMapper.selectByExample(cc);
			ArrayList<OrderItemVo> arrayList = new ArrayList<OrderItemVo>();
			for (OrderItem item : orderItem) {
				OrderItemVo vo = new OrderItemVo();
				BeanUtils.copyProperties(item, vo);
				String orderTypeStr = sdf.format(new Date(item.getStartTime() * 1000L)).substring(0, 10) + " " + sdf.format(new Date(item.getStartTime() * 1000L)).substring(11);
				if(item.getEndTime()!=null){
					orderTypeStr += "~"
							+ sdf.format(new Date(item.getEndTime() * 1000L)).substring(11);
				}
				vo.setOrderTimeStr(orderTypeStr);
				if (Constants.OrderType.STATIUM == orderVo.getOrdersType()) {
					vo.setStatiumDetail(statiumDetail);
					orderVo.setSportType(item.getSportType());
					orderVo.setOrderDate(DateUtil.formatDate(item.getStartDate(), "yyyy-MM-dd"));
				} else if (Constants.OrderType.COACH == orderVo.getOrdersType()) {
					vo.setCoach(coachVo);
				}else if (Constants.OrderType.EVENT == orderVo.getOrdersType()) {
					vo.setGamesVo(gameVo);
				} else if (Constants.OrderType.ACTIVITY == orderVo.getOrdersType()) {
					activityVo.setStartTime(com.lc.zy.common.util.DateUtils.formatDate(new Date(item.getStartTime()*1000L), "yyyy-MM-dd HH:mm:ss"));
					activityVo.setEndTime(null);
					vo.setActivity(activityVo);
					vo.setMemberList(memberList);
				}else if (Constants.OrderType.BOOKBALL == orderVo.getOrdersType()) {
					vo.setStatiumDetail(statiumDetail);
				}
				arrayList.add(vo);
			}
			orderVo.setOrderItemVoList(arrayList);
			if(Constants.OrderType.STATIUM == orderVo.getOrdersType()&&(Constants.OrderStatus.PAIED.equals(order.getStatus())||Constants.OrderStatus.REFUNDING.equals(order.getStatus()))){
				List<OrderVo> relatedOrders = getRelatedOrders(statiumDetail,order.getId(),order.getCustomerId());
				orderVo.setRelatedOrders(relatedOrders);
			}
			if(Constants.OrderType.BOOKBALL == orderVo.getOrdersType()&&(Constants.OrderStatus.PAIED.equals(order.getStatus())||Constants.OrderStatus.REFUNDING.equals(order.getStatus()))){
				List<OrderVo> relatedOrders = getRelatedOrders(statiumDetail,order.getId(),order.getCustomerId());
				orderVo.setRelatedOrders(relatedOrders);
			}
			if(Constants.OrderType.COACH == orderVo.getOrdersType()&&(Constants.OrderStatus.PAIED.equals(order.getStatus())||Constants.OrderStatus.REFUNDING.equals(order.getStatus()))){
				List<OrderVo> relatedOrders = getCoachRelatedOrders(statiumDetail,order.getId(),order.getCustomerId());
				orderVo.setRelatedOrders(relatedOrders);
			}
		}
		return orderVo;
	}
	/*
	 * 教培相关订单
	 */
	public List<OrderVo> getCoachRelatedOrders(StatiumDetail statiumDetail,String id,String userId){
		List<OrderVo> orders = new ArrayList<OrderVo>();
		OrderCriteria criteria = new OrderCriteria();
		OrderCriteria.Criteria c = criteria.createCriteria();
		c.andCustomerIdEqualTo(userId);
		criteria.setOrderByClause("statium_id");
		String [] status = new String[]{Constants.OrderStatus.PAIED,Constants.OrderStatus.REFUNDING};
		c.andStatusIn(Arrays.asList(status));
		c.andIdNotEqualTo(id);
		c.andOrdersTypeEqualTo(Constants.OrderType.COACH);
		List<Order> orderList = orderMapper.selectByExample(criteria);
		for(Order order : orderList){
			OrderVo orderVo = new OrderVo();
			String orderId = order.getId();
			orderVo.setId(orderId);
			orderVo.setStatus(order.getStatus());
			orderVo.setFinalFee(order.getFinalFee());
			OrderItemCriteria oiCriteria = new OrderItemCriteria();
			OrderItemCriteria.Criteria oic = oiCriteria.createCriteria();
			oic.andOrderIdEqualTo(orderId);
			List<OrderItem> orderItem = orderItemMapper.selectByExample(oiCriteria);
			List<OrderItemVo> orderItemVo = new ArrayList<OrderItemVo>();
			for (OrderItem item : orderItem) {
				OrderItemVo vo = new OrderItemVo();
				BeanUtils.copyProperties(item, vo);
				String orderTypeStr = sdf.format(new Date(item.getStartTime() * 1000L)).substring(0, 10) + " " + sdf.format(new Date(item.getStartTime() * 1000L)).substring(11) + "~"
						+ sdf.format(new Date(item.getEndTime() * 1000L)).substring(11);
				vo.setOrderTimeStr(orderTypeStr);
				vo.setStatiumDetail(statiumDetail);
				orderItemVo.add(vo);
			}
			orderVo.setOrderItemVoList(orderItemVo);
			orders.add(orderVo);
		}
		//同一tradeNo下的相关订单
		Order order1 = orderMapper.selectByPrimaryKey(id);
		OrderCriteria criteria2=new OrderCriteria();
		OrderCriteria.Criteria cri=criteria2.createCriteria();
		cri.andCustomerIdEqualTo(userId);
		cri.andStatusIn(Arrays.asList(status));
		cri.andIdNotEqualTo(id);
		cri.andTradeNoEqualTo(order1.getTradeNo());
		List<Order> tradeOrders = orderMapper.selectByExample(criteria2);
		if (CollectionUtils.isNotEmpty(tradeOrders)&&tradeOrders.size()>0) {
			Order order2=tradeOrders.get(0);
			OrderVo orderVo1=new OrderVo();
			orderVo1.setId(order2.getId());
			orderVo1.setStatus(order2.getStatus());
			orderVo1.setFinalFee(order2.getFinalFee());
			OrderItemCriteria oiCriteria = new OrderItemCriteria();
			OrderItemCriteria.Criteria oic = oiCriteria.createCriteria();
			oic.andOrderIdEqualTo(order2.getId());
			List<OrderItem> orderItem = orderItemMapper.selectByExample(oiCriteria);
			List<OrderItemVo> orderItemVo = new ArrayList<OrderItemVo>();
			for (OrderItem item : orderItem) {
				OrderItemVo vo = new OrderItemVo();
				BeanUtils.copyProperties(item, vo);
				String orderTypeStr = sdf.format(new Date(item.getStartTime() * 1000L)).substring(0, 10) + " " + sdf.format(new Date(item.getStartTime() * 1000L)).substring(11) + "~"
						+ sdf.format(new Date(item.getEndTime() * 1000L)).substring(11);
				vo.setOrderTimeStr(orderTypeStr);
				vo.setStatiumDetail(statiumDetail);
				orderItemVo.add(vo);
			}
			orderVo1.setOrderItemVoList(orderItemVo);
			orders.add(orderVo1);
		}
		return orders;
	}
	/*
	 * 订场相关订单
	 */
	public List<OrderVo> getRelatedOrders(StatiumDetail statiumDetail,String id,String userId){
		List<OrderVo> orders = new ArrayList<OrderVo>();
		OrderCriteria criteria = new OrderCriteria();
		OrderCriteria.Criteria c = criteria.createCriteria();
		c.andCustomerIdEqualTo(userId);
		criteria.setOrderByClause("statium_id");
		String [] status = new String[]{Constants.OrderStatus.PAIED,Constants.OrderStatus.REFUNDING};
		c.andStatusIn(Arrays.asList(status));
		c.andIdNotEqualTo(id);
		List<Integer> ordersTypes = new ArrayList<Integer>();
		ordersTypes.add(Constants.OrderType.BOOKBALL);
		ordersTypes.add(Constants.OrderType.STATIUM);
		c.andOrdersTypeIn(ordersTypes);
		List<Order> orderList = orderMapper.selectByExample(criteria);
		for(Order order : orderList){
			OrderVo orderVo = new OrderVo();
			String orderId = order.getId();
			orderVo.setId(orderId);
			orderVo.setStatus(order.getStatus());
			orderVo.setFinalFee(order.getFinalFee());
			OrderItemCriteria oiCriteria = new OrderItemCriteria();
			OrderItemCriteria.Criteria oic = oiCriteria.createCriteria();
			oic.andOrderIdEqualTo(orderId);
			List<OrderItem> orderItem = orderItemMapper.selectByExample(oiCriteria);
			List<OrderItemVo> orderItemVo = new ArrayList<OrderItemVo>();
			for (OrderItem item : orderItem) {
				OrderItemVo vo = new OrderItemVo();
				BeanUtils.copyProperties(item, vo);
				String orderTypeStr = sdf.format(new Date(item.getStartTime() * 1000L)).substring(0, 10) + " " + sdf.format(new Date(item.getStartTime() * 1000L)).substring(11) + "~"
						+ sdf.format(new Date(item.getEndTime() * 1000L)).substring(11);
				vo.setOrderTimeStr(orderTypeStr);
				vo.setStatiumDetail(statiumDetail);
				orderItemVo.add(vo);
			}
			orderVo.setOrderItemVoList(orderItemVo);
			orders.add(orderVo);
		}
		//同一tradeNo下的相关订单
		Order order1 = orderMapper.selectByPrimaryKey(id);
		OrderCriteria criteria2=new OrderCriteria();
		OrderCriteria.Criteria cri=criteria2.createCriteria();
		cri.andCustomerIdEqualTo(userId);
		cri.andStatusIn(Arrays.asList(status));
		cri.andIdNotEqualTo(id);
		cri.andTradeNoEqualTo(order1.getTradeNo());
		List<Order> tradeOrders = orderMapper.selectByExample(criteria2);
		if (CollectionUtils.isNotEmpty(tradeOrders)&&tradeOrders.size()>0) {
			Order order2=tradeOrders.get(0);
			OrderVo orderVo1=new OrderVo();
			orderVo1.setId(order2.getId());
			orderVo1.setStatus(order2.getStatus());
			orderVo1.setFinalFee(order2.getFinalFee());
			OrderItemCriteria oiCriteria = new OrderItemCriteria();
			OrderItemCriteria.Criteria oic = oiCriteria.createCriteria();
			oic.andOrderIdEqualTo(order2.getId());
			List<OrderItem> orderItem = orderItemMapper.selectByExample(oiCriteria);
			List<OrderItemVo> orderItemVo = new ArrayList<OrderItemVo>();
			for (OrderItem item : orderItem) {
				OrderItemVo vo = new OrderItemVo();
				BeanUtils.copyProperties(item, vo);
				String orderTypeStr = sdf.format(new Date(item.getStartTime() * 1000L)).substring(0, 10) + " " + sdf.format(new Date(item.getStartTime() * 1000L)).substring(11) + "~"
						+ sdf.format(new Date(item.getEndTime() * 1000L)).substring(11);
				vo.setOrderTimeStr(orderTypeStr);
				vo.setStatiumDetail(statiumDetail);
				orderItemVo.add(vo);
			}
			orderVo1.setOrderItemVoList(orderItemVo);
			orders.add(orderVo1);
		}
		return orders;
	}

	/**
	 * 获取订单的所有操作
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<OrderLogVo> getOrderLogVoList(String id) throws Exception {
		OrderLogCriteria cc = new OrderLogCriteria();
		OrderLogCriteria.Criteria cri = cc.createCriteria();
		cri.andOrderIdEqualTo(id);
		cc.setOrderByClause("ct desc");
		List<OrderLog> orderLogList = orderLogMapper.selectByExample(cc);
		ArrayList<OrderLogVo> arrayList = new ArrayList<OrderLogVo>();
		for (OrderLog log : orderLogList) {
			OrderLogVo vo = new OrderLogVo();
			BeanUtils.copyProperties(log, vo);
			if (CommonUtils.isNotEmpty(vo.getUserId())) {
				User cUser = this.selectByPrimaryKey(User.class, vo.getUserId());
				if (cUser != null) {
					vo.setOperatorName(cUser.getNickname());
					vo.setOperatorType("1");
				} else {
					SsoUser sUser = this.selectByPrimaryKey(SsoUser.class, vo.getUserId());
					if (sUser != null) {
						vo.setOperatorName(sUser.getNickName());
						vo.setOperatorType("2");
					}
				}
			}
			arrayList.add(vo);
		}
		OrderHandleCriteria handleCriteria = new OrderHandleCriteria();
		OrderHandleCriteria.Criteria handleCri = handleCriteria.createCriteria();
		handleCri.andOrderIdEqualTo(id);
		handleCriteria.setOrderByClause("ct desc");
		handleCriteria.setMysqlOffset(0);
		handleCriteria.setMysqlLength(1);
		List<OrderHandle> orderHandlers = handleMapper.selectByExample(handleCriteria);
		if(CollectionUtils.isNotEmpty(orderHandlers)){
			OrderLogVo vo = new OrderLogVo();
			Order order = this.selectByPrimaryKey(Order.class, id);
			String handler = order.getHandler();
			User user = userMapper.selectByPrimaryKey(handler);
			if(user!=null){
				vo.setOperatorName(user.getNickname());
			}
			vo.setOperatorType("1");
			vo.setOrderId(id);
			vo.setAction("订单处理");
			vo.setComment(orderHandlers.get(0).getComment());
			vo.setCt(orderHandlers.get(0).getCt());
			arrayList.add(vo);
		}
		return arrayList;
	}

	/**
	 * 确认订单
	 * 
	 * @param id
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	public void orderVerify(String id,boolean handle) throws Exception {
		boolean flag = false;
		try {
			// 修改订单
			Order order = this.selectByPrimaryKey(Order.class, id);
			if(Constants.OrderAction.VERIFY.equals(order.getStatus())){
				return;
			}
			Integer verifyFlag = order.getVerifyFlag();
			if(order.getVerifyFlag()!=null){
				order.setVerifyFlag(null);
			}else{
				order.setStatus(Constants.OrderAction.VERIFY);
			}
			order.setEt(new Date());
			order.setEb(SessionUtil.currentUserId());
			if(handle){
				order.setHandleStatus(1);
			}
			this.updateByPrimaryKey(order, id);
			// 记录订单日志
			OrderLog log = new OrderLog();
			log.setId(UUID.get());
			log.setCt(new Date());
			log.setOrderId(id);
			log.setUserId(SessionUtil.currentUserId());
			log.setAction(Constants.OrderAction.VERIFY);
			log.setComment("订单号: " + id + "已确认!");
			orderLogMapper.insert(log);
			if(verifyFlag!=null){
				
			}else{
				if(handle){
					OrderHandle handleLog = new OrderHandle();
					handleLog.setId(UUID.get());
					handleLog.setComment("已确认");
					handleLog.setOrderId(id);
					handleLog.setStatus(2);
					handleLog.setCt(new Date());
					this.insertSelective(handleLog, handleLog.getId());
				}
				flag = true;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}finally{
			if(flag){
				orderSendMsgService.sendMsg(id);
			}
		}
	}

	/**
	 * 确认订单
	 *
	 * @param id
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	public void orderVerifyNuomi(String id,boolean handle) throws Exception {
		boolean flag = false;
		try {
			// 修改订单
			Order order = this.selectByPrimaryKey(Order.class, id);
			if(Constants.OrderAction.VERIFY.equals(order.getStatus())){
				return;
			}
			Integer verifyFlag = order.getVerifyFlag();
			if(order.getVerifyFlag()!=null){
				order.setVerifyFlag(null);
			}else{
				order.setStatus(Constants.OrderAction.VERIFY);
			}
			order.setEt(new Date());
			order.setEb(SessionUtil.currentUserId());
			if(handle){
				order.setHandleStatus(1);
			}
			this.updateByPrimaryKey(order, id);
			// 记录订单日志
			OrderLog log = new OrderLog();
			log.setId(UUID.get());
			log.setCt(new Date());
			log.setOrderId(id);
			log.setUserId(SessionUtil.currentUserId());
			log.setAction(Constants.OrderAction.VERIFY);
			log.setComment("订单号: " + id + "已确认!");
			orderLogMapper.insert(log);
			if(verifyFlag!=null){

			}else{
				if(handle){
					OrderHandle handleLog = new OrderHandle();
					handleLog.setId(UUID.get());
					handleLog.setComment("已确认");
					handleLog.setOrderId(id);
					handleLog.setStatus(2);
					handleLog.setCt(new Date());
					this.insertSelective(handleLog, handleLog.getId());
				}
				flag = true;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Transactional(readOnly = false)
	public void orderModify(String orderId,List<OrderParam> orderParams,String orderDate) throws Exception{
        OrdeNotifyrMessage notifyMessage = new OrdeNotifyrMessage();
        List<OrdeNotifyrMessage.SpaceBean> spaces = new ArrayList<OrdeNotifyrMessage.SpaceBean>();
        
        OrdeNotifyrMessage notifyMessage_ = new OrdeNotifyrMessage();
        List<OrdeNotifyrMessage.SpaceBean> spaces_ = new ArrayList<OrdeNotifyrMessage.SpaceBean>();
        
		Date now = new Date();
        Integer totalPrice = 0;
        StatiumSpace space = null;
        List<SecurityLock> securityLocks = new ArrayList<SecurityLock>();
        Order order = this.selectByPrimaryKey(Order.class, orderId);
        try {
            for (OrderParam orderParam : orderParams) {
                SecurityLock securityLock = new SecurityLock();
                String lockStr = MD5Util.MD5Encode(orderParam.getSpaceId() + "_" + orderParam.getStartDate() + "_"
                        + orderParam.getStartTime() + "_" + orderParam.getEndTime(), "utf-8");
                if (securityLock.lock(lockStr)) {
                    securityLocks.add(securityLock);
                } else {
                    throw new Exception("预定的场馆时间段已经被占用，请重新选择");
                }
            }
            
            OrderItemCriteria criteria = new OrderItemCriteria();
            OrderItemCriteria.Criteria cri = criteria.createCriteria();
            cri.andOrderIdEqualTo(orderId);
            List<OrderItem> orderItemList = orderItemMapper.selectByExample(criteria);
            String orderTime_ = "";
            OrdeNotifyrMessage.SpaceBean spaceBean_ = null;
            String statiumId = order.getStatiumId();
            if(orderItemList.size()!=0){
            	int periodNum = orderItemList.size();
            	for(int i=0;i<periodNum;i++){
                 	String temp = DateFormatUtils.format(new Date(orderItemList.get(i).getStartTime()*1000L),"HH:mm:ss")+DateFormatUtils.format(new Date(orderItemList.get(i).getEndTime()*1000L),"~HH:mm:ss ");
                 	String spaceName = orderItemList.get(i).getSpaceName();
                 	if(i+1<periodNum){
                 		orderTime_+=spaceName+":"+temp+"</br>";
                 	}else{
                 		orderTime_+=spaceName+":"+temp;
                 	}
                 	Date startTime = new Date(orderItemList.get(i).getStartTime() * 1000L);
                    Double startHour = DateUtils.getFragmentInHours(startTime, Calendar.DATE) / 1d;
                    int startH = startHour.intValue();
                    spaceBean_ = new OrdeNotifyrMessage.SpaceBean();
                    spaceBean_.setSpaceId(orderItemList.get(i).getSpaceId());
                    String orderTime = startH < 10 ? ("0" + startH + ":00") : (startH + ":00");
                    spaceBean_.setTime(orderTime);
                    spaces.add(spaceBean_);
                    spaceBean_ = new OrdeNotifyrMessage.SpaceBean();
                    spaceBean_.setSpaceId(orderItemList.get(i).getSpaceId());
                    orderTime = startH < 10 ? ("0" + startH + ":30") : (startH + ":30");
                    spaceBean_.setTime(orderTime);
                    spaces_.add(spaceBean_);
                    notifyMessage_.setOrderDate(DateUtil.formatDate(orderItemList.get(i).getStartDate(), "yyyy-MM-dd"));
                 }
            	notifyMessage_.setSpaces(spaces_);
                notifyMessage_.setStatiumId(statiumId);
                notifyMessage_.setType("deleteB");
                notifyMessage_.setOrderId(orderId);
            }
            
            orderItemMapper.deleteByExample(criteria);
            
            User user = SessionUtil.currentUser();
            StatiumDetail statium = this.selectByPrimaryKey(StatiumDetail.class, statiumId);
            String sportType = statium.getSportType();
            String sportTypes[] = sportType.split(";;");
            Map<String, Integer> subsidieMap = new HashMap<String, Integer>();
            Map<String, List<Integer>> costMap_work = new HashMap<String, List<Integer>>();
            Map<String, List<Integer>> costMap_noWork = new HashMap<String, List<Integer>>();
            // 场馆按类型的补贴价
            for (String type : sportTypes) {
                StatiumPriceTmplCriteria statiumPriceTmplCriteria = new StatiumPriceTmplCriteria();
                StatiumPriceTmplCriteria.Criteria statiumPriceTmplCri = statiumPriceTmplCriteria.createCriteria();
                statiumPriceTmplCri.andStatiumIdEqualTo(statiumId);
                statiumPriceTmplCri.andSportTypeEqualTo(type);
                List<StatiumPriceTmpl> statiumPriceTmpls = statiumPriceTmplMapper
                        .selectByExample(statiumPriceTmplCriteria);
                int subsidies = statiumPriceTmpls.get(0).getSubsidies();
                subsidieMap.put(type, (new BigDecimal(subsidies).multiply(new BigDecimal(100))).intValue());
            }
            // 场馆按场地的成本价 工作日
            SpacePriceCriteria spacePriceCriteria = new SpacePriceCriteria();
            SpacePriceCriteria.Criteria spacePriceCri = spacePriceCriteria.createCriteria();
            spacePriceCri.andStatiumIdEqualTo(statiumId);
            spacePriceCri.andIsWorkdayEqualTo(1);
            List<SpacePrice> prices_work = spacePriceMapper.selectByExample(spacePriceCriteria);
            for (SpacePrice sp : prices_work) {
                if (StringUtils.isEmpty(sp.getCostPrice())) {
                    continue;
                }
                String[] prices = sp.getCostPrice().split(",", -1);
                List<Integer> newPrice = new ArrayList<Integer>();
                for (int i = 0; i < prices.length; i++) {
                    int price = 0;
                    if (!"".equals(prices[i]) && !"0".equals(prices[i])) {
                        price = Integer.parseInt(prices[i]);
                    }
                    newPrice.add(price);
                }
                costMap_work.put(sp.getSpaceId(), newPrice);
            }

            // 场馆按场地的成本价 非工作日
            spacePriceCriteria = new SpacePriceCriteria();
            SpacePriceCriteria.Criteria spacePriceCri_ = spacePriceCriteria.createCriteria();
            spacePriceCri_.andIsWorkdayEqualTo(0);
            List<SpacePrice> prices_noWork = spacePriceMapper.selectByExample(spacePriceCriteria);
            for (SpacePrice sp : prices_noWork) {
                if (StringUtils.isEmpty(sp.getCostPrice())) {
                    continue;
                }
                String[] prices = sp.getCostPrice().split(",", -1);
                List<Integer> newPrice = new ArrayList<Integer>();
                for (int i = 0; i < prices.length; i++) {
                    int price = 0;
                    if (!"".equals(prices[i]) && !"0".equals(prices[i])) {
                        price = Integer.parseInt(prices[i]);
                    }
                    newPrice.add(price);
                }
                costMap_noWork.put(sp.getSpaceId(), newPrice);
            }
            int workDay = this.checkWorkday(orderDate);
            OrdeNotifyrMessage.SpaceBean spaceBean = null;
            StringBuilder str = new StringBuilder();
            String sportType_ = orderParams.get(0).getSportType();
            for (OrderParam orderParam : orderParams) {
                OrderItem orderItem = null;
                orderParam.setStartDate(orderDate);
                orderItem = validate(orderParam);
                space = selectByPrimaryKey(StatiumSpace.class, orderParam.getSpaceId());
                if (space == null) {
                    throw new RuntimeException("预定的场地不存在，请重新选择");
                }
                orderItem.setSpaceId(orderParam.getSpaceId());
                orderItem.setSportType(orderParam.getSportType());
                orderItem.setId(UUID.get());
                orderItem.setOrderId(orderId);
                orderItem.setSpaceCode(space.getSpaceCode());
                orderItem.setSpaceName(space.getSpaceName());
                orderItem.setCt(now);
                orderItem.setCb(user.getUserId());
                orderItem.setEt(now);
                orderItem.setEb(user.getUserId());
                List<Integer> costs = null;
                Date startT = new Date(orderItem.getStartTime() * 1000L);
                Long start = DateUtils.getFragmentInHours(startT, Calendar.DATE);
                int cost = 0;
                if (workDay == 1) {
                    costs = costMap_work.get(orderItem.getSpaceId());
                } else {
                    costs = costMap_noWork.get(orderItem.getSpaceId());
                }
                if (CollectionUtils.isNotEmpty(costs)) {
                    cost = costs.get(start.intValue());
                } else {

                }
                orderItem.setCostPrice(cost);
                totalPrice += orderItem.getPrice();
                try {
                    this.insertSelective(orderItem, orderItem.getId());
                    freeSpaceCounter.inc(orderParam.getStartDate(), orderParam.getStatiumId(),
                            orderParam.getSpaceId(), orderParam.getSportType(), 1);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException("保存订单详情失败!");
                }
                Date startTime = new Date(orderItem.getStartTime() * 1000L);
                Double startHour = DateUtils.getFragmentInHours(startTime, Calendar.DATE) / 1d;
                int startH = startHour.intValue();
                spaceBean = new OrdeNotifyrMessage.SpaceBean();
                spaceBean.setSpaceId(orderItem.getSpaceId());
                String orderTime = startH < 10 ? ("0" + startH + ":00") : (startH + ":00");
                spaceBean.setTime(orderTime);
                spaces.add(spaceBean);
                spaceBean = new OrdeNotifyrMessage.SpaceBean();
                spaceBean.setSpaceId(orderItem.getSpaceId());
                orderTime = startH < 10 ? ("0" + startH + ":30") : (startH + ":30");
                spaceBean.setTime(orderTime);
                spaces.add(spaceBean);
                notifyMessage.setOrderDate(DateUtil.formatDate(orderItem.getStartDate(), "yyyy-MM-dd"));
                str.append("(");
                str.append(DateUtil.formatDate(new Date(orderItem.getStartTime() * 1000L),"HH:mm"));
                str.append("-");
                str.append(DateUtil.formatDate(new Date(orderItem.getEndTime() * 1000L),"HH:mm"));
                str.append(")");
                str.append(orderItem.getSpaceName());
            }
            Map<String, Object> root = new HashMap<String, Object>();
            root.put(
                    "date",
                    orderParams.get(0).getStartDate().split("-")[1] + "月"
                            + orderParams.get(0).getStartDate().split("-")[2] + "日");
            root.put("week", com.lc.zy.common.util.DateUtils.getWeek(DateUtil.parse(orderParams.get(0).getStartDate(), null)));
            root.put("statiumName", statium.getName());
            root.put("orderItem", str.toString());
            root.put("orderId", order.getId());
            root.put("serviceHotline", Constants.SERVICE_HOTLINE);
            if("BILLIARDS".equals(sportType_)){
            	root.put("hour", "4");
        	}else{
        		root.put("hour", "24");
        	}
            String msg = FreeMarkerUtils.format("/template/sms/paySuccess.ftl", root);
            String userId = order.getCustomerId();
            SsoUser customer = this.selectByPrimaryKey(SsoUser.class, userId);
            if(customer!=null){
            	messageUtil.sendSms(customer.getPhone(), msg);
            }
            notifyMessage.setSpaces(spaces);
            notifyMessage.setStatiumId(statiumId);
            notifyMessage.setType("update");
            notifyMessage.setOrderId(orderId);
            orderNotifyUtil.notifyOrder(MyGson.getInstance().toJson(notifyMessage_));
            orderNotifyUtil.notifyOrder(MyGson.getInstance().toJson(notifyMessage));
            try {
                // 记录订单日志
                OrderLog log = new OrderLog();
                log.setId(UUID.get());
                log.setCt(new Date());
                log.setOrderId(order.getId());
                log.setUserId(SessionUtil.currentUserId());
                log.setAction(Constants.OrderAction.UPDATE);
                log.setComment(orderTime_);
                orderLogMapper.insertSelective(log);
            } catch (Exception e) {
                logger.debug("订单号：{} 订单支付成功日志记录失败！", order.getId());
            }
        }catch(Exception e){
        	logger.error(e.getMessage(), e);
            throw new RuntimeException();
        } finally {
            for (SecurityLock lock : securityLocks) {
                lock.unlock();
            }
        }
	}
	
	/**
     * 校验订单详情
     * 
     * @param orderParam
     * @return
     * @throws Exception
     */
    private OrderItem validate(OrderParam orderParam) throws RuntimeException {
        OrderItem orderItem = new OrderItem();
        // 预订日期是否合法
        Date startDate = DateUtil.parse(orderParam.getStartDate(), null);
        if (startDate == null) {
            throw new RuntimeException("预订日期不能为空!");
        }

        Date now = null;
        try {
            now = org.apache.commons.lang.time.DateUtils.parseDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd"),
                    new String[] { "yyyy-MM-dd" });
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("服务器异常!");
        }

        if (startDate.compareTo(now) == -1) {
            throw new RuntimeException("预订日期不合法!");
        }

        orderItem.setStartDate(startDate);

        // 开始时间是否合法
        Date startTime = DateUtil.parse(orderParam.getStartDate() + " " + orderParam.getStartTime(),
                "yyyy-MM-dd HH:mm", null);
        if (startTime == null) {
            throw new RuntimeException("开始时间不能为空!");
        }

        if (startDate.compareTo(now) == 0) {
            String nowTime = DateFormatUtils.format(new Date(), "HH");
            Date nowHour = null;
            Date start = null;
            try {
                nowHour = DateUtils.parseDate(nowTime, new String[] { "HH" });
                start = DateUtils.parseDate(orderParam.getStartTime().split(":")[0], new String[] { "HH" });
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException("服务器异常!");
            }
            if (DateUtil.compareDate(start, nowHour) == -1) {
                throw new RuntimeException("预定时间不合法!");
            }
        }

        // 结束时间是否合法
        Date endTime = DateUtil.parse(orderParam.getStartDate() + " " + orderParam.getEndTime(), "yyyy-MM-dd HH:mm",
                null);
        if (endTime == null) {
            throw new RuntimeException("结束时间不能为空!");
        }

        // 结束时间是否晚于开始时间
        if (endTime.before(startTime)) {
            throw new RuntimeException("开始时间不能晚于结束时间!");
        }

        orderItem.setStartTime((int) (startTime.getTime() / 1000));
        orderItem.setEndTime((int) (endTime.getTime() / 1000));

        // 判断场地在指定时间段是否被占用
        isFree(orderParam.getSpaceId(), startDate, orderItem.getStartTime(), orderItem.getEndTime());

        // 价格是否大于0
        Double douPrice = Double.parseDouble(orderParam.getDouPrice());
        douPrice = douPrice == null ? 0 : douPrice;
        if (douPrice < 0) {
            throw new RuntimeException("场地价格不能小于0!");
        } else {
            int price = (int) (douPrice * 100);
            orderItem.setPrice(price);
        }
        return orderItem;
    }
    
    /**
     * 查询指定场地、指定时间是否被占用
     * 
     * @param spaceId
     * @param startDate
     * @param startTime
     * @param endTime
     * @return
     */
    public boolean isFree(String spaceId, Date startDate, int startTime, int endTime) throws RuntimeException {
        String[] statusArray = new String[] { Constants.OrderStatus.PAIED, Constants.OrderStatus.NEW,
                Constants.OrderStatus.VERIFY, Constants.OrderStatus.PLAYING };
        List<String> statuss = Arrays.asList(statusArray);
        OrderItemCriteria criteria = new OrderItemCriteria();
        OrderItemCriteria.Criteria c = criteria.createCriteria();
        c.andSpaceIdEqualTo(spaceId);
        c.andStartDateEqualTo(startDate);
        c.andStartTimeEqualTo(startTime);
        c.andEndTimeEqualTo(endTime);
        List<OrderItem> items = orderItemMapper.selectByExample(criteria);
        for (OrderItem item : items) {
            String orderId = item.getOrderId();
            Order order = null;
            try {
                order = this.selectByPrimaryKey(Order.class, orderId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException("查询订单状态失败!");
            }
            if (order != null && statuss.contains(order.getStatus())) {
                throw new RuntimeException("该时间段已经被占用!");
            }
        }
        return false;
    }
    
    private Integer checkWorkday(String startDate) throws Exception {
        // 是否是工作日0：否，1： 是
        Integer isWorkday = 1;
        // holiday为null是工作日，不为null节假日
        Holiday holiday = this.selectByPrimaryKey(Holiday.class, startDate);
        if (holiday != null) {// 节假日
            isWorkday = 0;
        }
        return isWorkday;
    }

	/**
	 * 申请退款
	 * 
	 * @param orderId
	 * @param reason
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	public void applyRefund(String orderId, String reason) throws Exception {
		boolean result = false;
		Order order = this.selectByPrimaryKey(Order.class, orderId);
		try {
			OrdeNotifyrMessage notifyMessage = new OrdeNotifyrMessage();
	        List<OrdeNotifyrMessage.SpaceBean> spaces = new ArrayList<OrdeNotifyrMessage.SpaceBean>();
			// 修改订单
			order.setStatus(Constants.OrderStatus.REFUNDING);
			order.setEt(new Date());
			order.setEb(SessionUtil.currentUserId());
			order.setReason(reason);
			if(order.getVerifyFlag()!=null){
				order.setVerifyFlag(null);
			}
			this.updateByPrimaryKey(order, orderId);
			Order orderSaved = this.selectByPrimaryKey(Order.class, orderId);
			if(Constants.OrderType.STATIUM==orderSaved.getOrdersType()){
				OrderItemCriteria itemCriteria = new OrderItemCriteria();
		        OrderItemCriteria.Criteria cri = itemCriteria.createCriteria();
		        cri.andOrderIdEqualTo(order.getId());
		        List<OrderItem> orderItems = orderItemMapper.selectByExample(itemCriteria);
		        OrdeNotifyrMessage.SpaceBean spaceBean = null;
		        for(OrderItem orderItem:orderItems){
		        	freeSpaceCounter.inc(DateFormatUtils.format(orderItem.getStartDate(),"yyyy-MM-dd"), order.getStatiumId(), orderItem.getSpaceId(), orderItem.getSportType(), -1);
		        	Date startTime = new Date(orderItem.getStartTime() * 1000L);
		            Double startHour = DateUtils.getFragmentInHours(startTime, Calendar.DATE)/1d;
		            int startH = startHour.intValue();
		        	spaceBean = new OrdeNotifyrMessage.SpaceBean();
		        	spaceBean.setSpaceId(orderItem.getSpaceId());
		        	String orderTime = startH<10?("0"+startH+":00"):(startH+":00");
		        	spaceBean.setTime(orderTime);
		        	spaces.add(spaceBean);
		        	spaceBean = new OrdeNotifyrMessage.SpaceBean();
		        	spaceBean.setSpaceId(orderItem.getSpaceId());
		        	orderTime = startH<10?("0"+startH+":30"):(startH+":30");
		        	spaceBean.setTime(orderTime);
		        	spaces.add(spaceBean);
		        	notifyMessage.setOrderDate(DateUtil.formatDate(orderItem.getStartDate(),"yyyy-MM-dd"));
		        }
				// 记录订单日志
				OrderLog log = new OrderLog();
				log.setId(UUID.get());
				log.setCt(new Date());
				log.setOrderId(orderId);
				log.setUserId(SessionUtil.currentUserId());
				log.setAction(Constants.OrderAction.APPLY_REFUND);
				log.setComment("订单号: " + orderId + "申请退款!退款原因：" + reason);
				orderLogMapper.insert(log);
				notifyMessage.setSpaces(spaces);
		        notifyMessage.setStatiumId(order.getStatiumId());
		        notifyMessage.setType("cancel");
		        notifyMessage.setOrderId(orderId);
		        orderNotifyUtil.notifyOrder(MyGson.getInstance().toJson(notifyMessage));
			}else if(Constants.OrderType.ACTIVITY==orderSaved.getOrdersType()){
				MemberListCriteria memCriteria = new MemberListCriteria();
				MemberListCriteria.Criteria memCri = memCriteria.createCriteria();
				memCri.andOrderidEqualTo(orderId);
				memberListMapper.deleteByExample(memCriteria);
				//减人数
				Activity activity = this.selectByPrimaryKey(Activity.class, orderSaved.getStatiumId());
		        Activity activitySave = new Activity();
		        activitySave.setId(activity.getId());
		        activitySave.setApplicantNumber(activity.getApplicantNumber()-1);
		        this.updateByPrimaryKeySelective(activitySave, activitySave.getId());
			}else if(Constants.OrderType.EVENT==orderSaved.getOrdersType()){
//				GamesMemberCriteria c = new GamesMemberCriteria();
//				GamesMemberCriteria.Criteria cri = c.createCriteria();
//				cri.andOrderIdEqualTo(orderId);
//				gamesMemberMapper.deleteByExample(c);
				
				//2.0版本赛事退款删除战队或个人报名信息
				String gameId = order.getStatiumId();
	            Games game = this.selectByPrimaryKey(Games.class, gameId);
	            GamesCorpsCriteria gamesCorpsCriteria = new GamesCorpsCriteria();
	        	GamesCorpsCriteria.Criteria gameCri = gamesCorpsCriteria.createCriteria();
	        	gameCri.andOrderIdEqualTo(order.getId());
	        	gamesCorpsMapper.deleteByExample(gamesCorpsCriteria);
	            if(game.getType()==1){
	            	
	            }else{
	            	// 删除报名成员
	            	GamesMemberCriteria gamesMemberCriteria = new GamesMemberCriteria();
	            	GamesMemberCriteria.Criteria gamesCri = gamesMemberCriteria.createCriteria();
	            	gamesCri.andOrderIdEqualTo(order.getId());
	            	gamesMemberMapper.deleteByExample(gamesMemberCriteria);            	
	            }
				
				//减人数
				Games games = this.selectByPrimaryKey(Games.class, orderSaved.getStatiumId());
				Games gamesSave = new Games();
				gamesSave.setId(games.getId());
				gamesSave.setApplicantNumber(games.getApplicantNumber()-1);
				this.updateByPrimaryKeySelective(gamesSave, games.getId());
			}
			
			// 当使用优惠券付款是，把优惠券也退还给用户
			if (!StringUtils.isEmpty(order.getCouponId())) {
				logger.debug("退还优惠券  {}", order.getCouponId());
				CouponHistory couponHistory;
				try {
					couponHistory = this.selectByPrimaryKey(CouponHistory.class, order.getCouponId());
					couponHistory.setSubAmount(null);
					Date endTime = couponHistory.getEndTime();
					Date currTime = new Date();

					if (endTime.compareTo(currTime) <= 0) { // 优惠券结束时间小于或者等于当前时间把优惠券状态改为2过期
						couponHistory.setIsUse(2);
					} else {
						couponHistory.setIsUse(0);
					}

					couponHistory.setOrderId(null);
					couponHistory.setUseTime(null);
					couponHistory.setOccName(null);
					couponHistory.setOrderTime(null);
					this.updateByPrimaryKeySelective(couponHistory, order.getCouponId());
					
					String couponInfoId = couponHistory.getCouponId();
	                CouponInfo couponInfo = this.selectByPrimaryKey(CouponInfo.class, couponInfoId);
					if(com.lc.zy.common.util.DateUtils.formatDate(new Date(), "yyyy-MM-dd").equals(com.lc.zy.common.util.DateUtils.formatDate(order.getCt(), "yyyy-MM-dd"))){
						ordersCounter.inc(order.getCustomerId() + "_" + couponInfo.getCouponType() + "_"
								+ com.lc.zy.common.util.DateUtils.formatDate(new Date(), "yyyy-MM-dd"),-1);
					}
				} catch (Exception e) {
					logger.error("退款至账户时，退还优惠券失败！订单ID：" + order.getId(), e);
				}
			}
			if(order.getQiuyouFee()!=null&&order.getQiuyouFee()>0){
				try {
					QiuyouCardUser account = this.selectByPrimaryKey(QiuyouCardUser.class, order.getCustomerId());
					Integer balanceInit = account.getBalance();
					Integer balanceResult = balanceInit+order.getQiuyouFee();
					//余额更新
					account.setBalance(balanceResult);
					
					account.setEt(new Date());
					account.setEb(SessionUtil.currentUserId());
					
					this.updateByPrimaryKeySelective(account, order.getCustomerId());
					
					//创建球友卡消费日志
					QiuyouCardLog qycl = new QiuyouCardLog();
					qycl.setId(UUID.get());
					qycl.setType(2);	//0充值1消费
					qycl.setUserId(order.getCustomerId());
					qycl.setAmount(order.getQiuyouFee());
					qycl.setBalance(balanceResult);
					qycl.setOrderId(order.getId());
					qycl.setDescription("退回");
					qycl.setCt(new Date());
					qiuyouCardLogMapper.insert(qycl);
					result = true;
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
			logger.debug("order.getFinalFee()------------------,{}",order.getFinalFee());
			logger.debug("order.getFee()------------------,{}",order.getFee());
			logger.debug("order.getLxFee()------------------,{}",order.getLxFee());
			
			if(order.getLxFee()!=null&&order.getLxFee()>0){
				logger.debug("order.getFinalFee()------------------,{}",order.getFinalFee());
				logger.debug("order.getFee()------------------,{}",order.getFee());
				logger.debug("order.getLxFee()------------------,{}",order.getLxFee());
				try {
					TradeOut out = taipingServie.refund("orderId",orderId);
		        	if(out.getErrorCode()==0){
		        		LemiLog log_ = new LemiLog();
	                    log_.setId(UUID.get());
	                    log_.setCt(new Date());
	                    log_.setLemiAmount(order.getLxFee());
	            		log_.setOrderId(order.getId());
	            		log_.setStatus("返还");
	            		log_.setTradeId(out.getOutTradeNo());
	            		lemiLogMapper.insert(log_);
	            		
	            		if(order.getFinalFee()==0){
	            			// 修改订单
	            			order.setStatus(Constants.OrderStatus.REFUNDED);
	            			order.setEt(new Date());
	            			order.setEb(SessionUtil.currentUserId());
	            			order.setReason(reason);
	            			if(order.getVerifyFlag()!=null){
	            				order.setVerifyFlag(null);
	            			}
	            			this.updateByPrimaryKey(order, orderId);
	            			// 记录订单日志
	            			OrderLog log = new OrderLog();
	            			log.setId(UUID.get());
	            			log.setCt(new Date());
	            			log.setOrderId(order.getId());
	            			log.setUserId(SessionUtil.currentUserId());
	            			log.setAction(Constants.OrderAction.REFUND);
	            			log.setComment(Constants.PayStatus.REFUND_SUCCESS);
	            			orderLogMapper.insert(log);
	            			StatiumDetail detail = this.selectByPrimaryKey(StatiumDetail.class, order.getStatiumId());
	            			OrderItemCriteria itemCriteria = new OrderItemCriteria();
	                        OrderItemCriteria.Criteria cri = itemCriteria.createCriteria();
	                        cri.andOrderIdEqualTo(order.getId());
	                        List<OrderItem> orderItems = orderItemMapper.selectByExample(itemCriteria);
	                        String goodsSpec = "";
	                        for(OrderItem item:orderItems){
	                        	Date start_ = new Date(item.getStartTime() * 1000L);
	                        	Date end_ = new Date(item.getEndTime() * 1000L);
	                        	goodsSpec+=item.getSpaceName()+":"+DateUtil.formatDate(start_, "yyyy-MM-dd HH:mm")+"-"+DateUtil.formatDate(end_, "yyyy-MM-dd HH:mm")+";";
	                        }
	                        try{
	                        	UploadOut out_ = taipingServie.upload(
		                    			"orderId",order.getId(),
		                    			"orderStatus","2",
		                    			"totalAmount",String.valueOf(new BigDecimal(order.getFee()).divide(new BigDecimal(100))),
		                    			"payAmount",String.valueOf(new BigDecimal(order.getFinalFee()).divide(new BigDecimal(100))),
		                    			"leimiAmount",order.getLxFee()==null?"0":((new BigDecimal(order.getLxFee()).divide(new BigDecimal(100))).multiply(new BigDecimal(500))).toString(),
		                    			"goodsName",detail.getName(),
		                    			"quantity",String.valueOf(orderItems.size()),
		                    			"goodsSpec",goodsSpec);
			                	if(out_.getErrorCode()==0){
			                		LemiLog log__ = new LemiLog();
			                		log__.setId(UUID.get());
			                		log__.setCt(new Date());
			                		log__.setLemiAmount(order.getLxFee());
				                    log__.setOrderId(order.getId());
				                    log__.setStatus("上传成功");
				                    log__.setTradeId(out_.getOutTradeNo());
				            		lemiLogMapper.insert(log__);
			                	}else{
			                		LemiLog log__ = new LemiLog();
			                		log__.setId(UUID.get());
			                		log__.setCt(new Date());
			                		log__.setLemiAmount(order.getLxFee());
			                		log__.setOrderId(order.getId());
			                		log__.setStatus("上传失败");
			                		log__.setTradeId(out_.getOutTradeNo());
				            		lemiLogMapper.insert(log__);
			                	}
	                        }catch(Exception e){
	                        	logger.debug(e.getMessage(),e);
	                        	LemiLog log__ = new LemiLog();
	                        	log__.setId(UUID.get());
	                        	log__.setCt(new Date());
	                        	log__.setLemiAmount(order.getLxFee());
	                        	log__.setOrderId(order.getId());
	                        	log__.setStatus("上传失败");
	                        	log__.setTradeId("");
			            		lemiLogMapper.insert(log__);
	                        }
	            		}
		        	}else{
		        		throw new Exception(out.getErrorDesc());
		        	}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}finally{
			if(result){
				// 给用户发短息
				SsoUser user = this.selectByPrimaryKey(SsoUser.class, order.getCustomerId());
				if(StringUtils.isNotBlank(user.getPhone())){
					BigDecimal decimal1 = new BigDecimal(order.getQiuyouFee());
                    decimal1 = decimal1.divide(new BigDecimal(100));
					StringBuffer msg = new StringBuffer();
					msg.append("尊敬的用户，您的订单"+order.getId()+"支付的球友卡金额"+decimal1.setScale(2, BigDecimal.ROUND_HALF_UP).toString()+"元已退回至您的球友卡账户，请您注意查收！");
					messageUtil.sendSms(user.getPhone(), msg.toString());
				}
			}
		}
		

	}

	/**
	 * 确认退款
	 * 
	 * @param id
	 * @throws Exception 
	 */
	@Transactional(readOnly = false)
	public Map<String, Object> confirmRefund(String id) throws Exception {
		Map<String, Object> result = new HashMap<>();
		result.put(Constants.Result.RESULT, false);
		boolean b = false;
		Order order = null;
		String comment = "";
		// 批次号
		String batch_no = "";
		try {
			order = this.getOrder(id);
			if (order == null) {
				comment = "退款失败，订单不存在！";
			} else {
				if (!order.getStatus().equals(Constants.OrderStatus.REFUNDING)) {
					comment = "退款失败，订单不是退款中的订单！";
				} else {
					// 获取组织账户信息
					String orgCode = "";
					if (order.getOrdersType() == 0){
						orgCode = orgCodeUtil.getOrgCode(order.getStatiumId());
					} else {
						orgCode = "master";
					}
			        Organization organization = payParameter.payKeys(orgCode);
					// 支付宝退款
					if (Constants.PayType.ALI == order.getPayType()) {
						StringBuffer detail = new StringBuffer();
						List<Order> orderList = new ArrayList<Order>();
						orderList.add(order);
						for (Order or : orderList) {
							detail.append(or.getNumber());
							detail.append("^");
							if (or.getFinalFee() != null) {
								detail.append((or.getFinalFee() / 100.0D));
							} else {
								detail.append((or.getFee() / 100.0D));
							}
							if (StringUtils.isEmpty(or.getReason())) {
								detail.append("^协商退款#");
							} else {
								detail.append("^");
								detail.append(or.getReason());
								detail.append("#");
							}
						}
						// 退款详细数据
						String detailData = detail.substring(0, detail.length() - 1);
						batch_no = Sequence.getId();
						Map<String, Object> res = alipayRefund(orderList, detailData, batch_no, organization);
						if (res == null || res.get(Constants.Result.RESULT) == null || false == (Boolean) res.get(Constants.Result.RESULT)) {
							comment = "退款失败，支付宝退款失败！";
						} else {
							order.setRefundBatchNo(batch_no); // 保存退款批次号
							result.put(Constants.Result.RESULT, true);
							b = true;
						}
					} else if (Constants.PayType.WEIXIN == order.getPayType() || Constants.PayType.WEIXIN_PLAT == order.getPayType()) {// 微信退款
						Map<String, Object> res = wxapRefund(order, organization);
						if (res == null || res.get(Constants.Result.RESULT) == null || false == (Boolean) res.get(Constants.Result.RESULT)) {
							comment = "退款失败，微信退款失败！";
						} else {
							batch_no = (String) res.get("out_trade_no");
							b = true;
							result.put(Constants.Result.RESULT, true);
						}
					}else if (Constants.PayType.QIUYOUZONE == order.getPayType()) {
							result.put(Constants.Result.RESULT, true);
							b = true;
	                }else {
						comment = "退款失败，无法找到订单的付款方式！";
					}
				}
			}
		} catch (Exception e) {
			comment = "退款失败，操作发生异常！";
			logger.error("退款失败，操作发生异常！订单ID：" + order.getId(), e);
		} finally {
			result.put(Constants.Result.REASON, comment);
			if (order != null) {
				if (b) {
					if("BOOK_TAIPING".equals(order.getOrderType())){
						StatiumDetail detail = this.selectByPrimaryKey(StatiumDetail.class, order.getStatiumId());
            			OrderItemCriteria itemCriteria = new OrderItemCriteria();
                        OrderItemCriteria.Criteria cri = itemCriteria.createCriteria();
                        cri.andOrderIdEqualTo(order.getId());
                        List<OrderItem> orderItems = orderItemMapper.selectByExample(itemCriteria);
                        String goodsSpec = "";
                        for(OrderItem item:orderItems){
                        	Date start_ = new Date(item.getStartTime() * 1000L);
                        	Date end_ = new Date(item.getEndTime() * 1000L);
                        	goodsSpec+=item.getSpaceName()+":"+DateUtil.formatDate(start_, "yyyy-MM-dd HH:mm")+"-"+DateUtil.formatDate(end_, "yyyy-MM-dd HH:mm")+";";
                        }
                        if(order.getLxFee()==null||order.getLxFee()==0){
                        	try {
                        		TradeOut out_ = taipingServie.precreate("ecsId",order.getEscId(),"lemiAmount","0","subject",detail.getName(),"orderId",order.getId());
                            	if(out_.getErrorCode()==0){
                            		
                            		try {
	                            		TradeOut out = taipingServie.deduct("orderId",order.getId());
	                                	if(out.getErrorCode()==0){
                                            LemiLog log_ = new LemiLog();
                                            log_.setId(UUID.get());
                                            log_.setCt(new Date());
                                            log_.setLemiAmount(0);
                                    		log_.setOrderId(order.getId());
                                    		log_.setStatus("扣减");
                                    		log_.setTradeId(out.getOutTradeNo());
                                    		lemiLogMapper.insert(log_);
	                                	}
                                    }catch (Exception e) {
                                    	LemiLog log_ = new LemiLog();
                                        log_.setId(UUID.get());
                                        log_.setCt(new Date());
                                        log_.setLemiAmount(order.getLxFee());
                                		log_.setOrderId(order.getId());
                                		log_.setStatus("扣减失败");
                                		log_.setTradeId("");
                                		lemiLogMapper.insert(log_);
                                    }
                            		
                            		LemiLog log__ = new LemiLog();
                            		log__.setId(UUID.get());
                            		log__.setCt(new Date());
                            		log__.setLemiAmount(order.getLxFee());
                                    log__.setOrderId(order.getId());
                                    log__.setStatus("冻结");
                                    log__.setTradeId(out_.getOutTradeNo());
                            		lemiLogMapper.insert(log__);
                            	}else{
                            		LemiLog log__ = new LemiLog();
                            		log__.setId(UUID.get());
                            		log__.setCt(new Date());
                            		log__.setLemiAmount(order.getLxFee());
                            		log__.setOrderId(order.getId());
                            		log__.setStatus("冻结失败");
                            		log__.setTradeId(out_.getOutTradeNo());
                            		lemiLogMapper.insert(log__);
                            	}
							} catch (Exception e) {
								LemiLog log__ = new LemiLog();
								log__.setId(UUID.get());
								log__.setCt(new Date());
								log__.setLemiAmount(order.getLxFee());
								log__.setOrderId(order.getId());
								log__.setStatus("冻结失败");
								log__.setTradeId("");
                        		lemiLogMapper.insert(log__);
							}
                        }
                        try{
                        	UploadOut out_ = taipingServie.upload(
	                    			"orderId",order.getId(),
	                    			"orderStatus","2",
	                    			"totalAmount",String.valueOf(new BigDecimal(order.getFee()).divide(new BigDecimal(100))),
	                    			"payAmount",String.valueOf(new BigDecimal(order.getFinalFee()).divide(new BigDecimal(100))),
	                    			"leimiAmount",order.getLxFee()==null?"0":((new BigDecimal(order.getLxFee()).divide(new BigDecimal(100))).multiply(new BigDecimal(500))).toString(),
	                    			"goodsName",detail.getName(),
	                    			"quantity",String.valueOf(orderItems.size()),
	                    			"goodsSpec",goodsSpec);
		                	if(out_.getErrorCode()==0){
		                		LemiLog log__ = new LemiLog();
		                		log__.setId(UUID.get());
		                		log__.setCt(new Date());
		                		log__.setLemiAmount(order.getLxFee());
			                    log__.setOrderId(order.getId());
			                    log__.setStatus("上传成功");
			                    log__.setTradeId(out_.getOutTradeNo());
			            		lemiLogMapper.insert(log__);
		                	}else{
		                		LemiLog log__ = new LemiLog();
		                		log__.setId(UUID.get());
		                		log__.setCt(new Date());
		                		log__.setLemiAmount(order.getLxFee());
		                		log__.setOrderId(order.getId());
		                		log__.setStatus("上传失败");
		                		log__.setTradeId(out_.getOutTradeNo());
			            		lemiLogMapper.insert(log__);
		                	}
                        }catch(Exception e){
                        	logger.debug(e.getMessage(),e);
                        	LemiLog log__ = new LemiLog();
                        	log__.setId(UUID.get());
                        	log__.setCt(new Date());
                        	log__.setLemiAmount(order.getLxFee());
                        	log__.setOrderId(order.getId());
                        	log__.setStatus("上传失败");
                        	log__.setTradeId("");
		            		lemiLogMapper.insert(log__);
                        }
					}
					returnOrderLog(SessionUtil.currentUserId(), order, "退款成功！", true, batch_no, Constants.OrderAction.REFUND, Constants.PayStatus.REFUND_SUCCESS);
					// 退款成功之后的动作
					SsoUser user = ssoUserMapper.selectByPrimaryKey(order.getCustomerId());
					String phone = user.getPhone();

					try {
						order.setStatus(Constants.OrderStatus.REFUNDED);
						order.setEt(new Date());
						this.updateByPrimaryKeySelective(order, order.getId());
						// orderMapper.updateByPrimaryKeySelective(order);

						// 给用户发短息
						StringBuffer msg = new StringBuffer();
						msg.append("尊敬的用户，您的订单号：");
						msg.append(order.getId());
						msg.append(" 已成功退款，请注意查收，最晚三个工作日到账！");
						messageUtil.sendSms(phone, msg.toString());
					} catch (Exception e) {
						logger.error("退款时，退款成功后修改订单状态或者发送短信失败！订单ID：" + order.getId(), e);
					}
				} else {
					returnOrderLog(SessionUtil.currentUserId(), order, comment, false, batch_no, Constants.OrderAction.REFUND, Constants.PayStatus.REFUND_FAIL);
				}
			}
		}
		return result;
	}

	/**
	 * 指定日期的球场预定情况
	 * 
	 * @param strStartDate
	 * @param spaceId
	 * @return
	 * @throws Exception
	 */
	public List<OrderItemVo> find(String strStartDate, String spaceId) throws Exception {
		if (StringUtils.isEmpty(strStartDate)) {
			strStartDate = DateUtil.nowDateString();
		}
		Date startDate = DateUtil.parse(strStartDate, new Date());
		OrderItemCriteria orderItemCriteria = new OrderItemCriteria();
		orderItemCriteria.setOrderByClause(" start_time desc ");
		OrderItemCriteria.Criteria criteria = orderItemCriteria.createCriteria();
		criteria.andSpaceIdEqualTo(spaceId);
		criteria.andStartDateEqualTo(startDate);
		List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemCriteria);
		List<OrderItemVo> resultList = new ArrayList<OrderItemVo>();
		String[] sta = new String[] { Constants.OrderStatus.PAIED, Constants.OrderStatus.NEW, Constants.OrderStatus.PLAYING, Constants.OrderStatus.VERIFY };
		List<String> statusList = Arrays.asList(sta);
		Order order = null;
		OrderItemVo vo = null;
		for (OrderItem itme : orderItemList) {
			order = this.selectByPrimaryKey(Order.class, itme.getOrderId());
			if (statusList.contains(order.getStatus()) && Constants.BookType.APP.equals(order.getOrderType())) {
				vo = new OrderItemVo();
				BeanUtils.copyProperties(itme, vo);
				vo.setStatus(order.getStatus());
				resultList.add(vo);
			}
		}
		return resultList;
	}

	/**
	 * 支付宝退款
	 * 
	 * @param orderList
	 * @param detail
	 * @return
	 */
	public Map<String, Object> alipayRefund(List<Order> orderList, String detail, String batch_no, Organization organization) {
		Map<String, Object> result = new HashMap<>();
		try {
			// 退款当天日期
			String refund_date = UtilDate.getDateFormatter();
			// 退款笔数
			String batch_num = orderList.size() + "";
			logger.debug("detail_data {}", detail);
			// 把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			// sParaTemp.put("service",
			// "refund_fastpay_by_platform_pwd"); //
			// 有密退款
			sParaTemp.put("service", "refund_fastpay_by_platform_nopwd"); // 无密退款
//			sParaTemp.put("partner", AlipayConfig.partner);
			sParaTemp.put("partner", organization.getPartner());
			sParaTemp.put("_input_charset", AlipayConfig.input_charset);
//			sParaTemp.put("seller_user_id", AlipayConfig.partner);
			sParaTemp.put("seller_user_id", organization.getPartner());
			sParaTemp.put("refund_date", refund_date);
			sParaTemp.put("batch_no", batch_no);
			sParaTemp.put("batch_num", batch_num);
			sParaTemp.put("detail_data", detail);
			sParaTemp.put("private_key", organization.getPrivateKey());
			logger.debug("sParaTemp {}",sParaTemp.toString());
			String data = AlipaySubmit.postRequest(sParaTemp);
			Map<String, String> resultMap = CommonOAUtils.converteToMap(data);
			if ("T".equalsIgnoreCase(resultMap.get("is_success"))) {
				result.put(Constants.Result.RESULT, true);
			} else {
				result.put(Constants.Result.RESULT, false);
				result.put(Constants.Result.REASON, resultMap.get("error"));
				logger.debug("alipayRefund pay error,the error is {}", resultMap.get("error"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 微信退款
	 * 
	 * @param order
	 *                订单对象
	 * @return String
	 */
	public Map<String, Object> wxapRefund(Order order,Organization organization) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(Constants.Result.RESULT, false);
		try {
			SortedMap<String, String> signParams = new TreeMap<String, String>();
			if (Constants.PayType.WEIXIN == order.getPayType()) { // 判断是否为微信退款还是微信公众平台退款
//				signParams.put("appid", WxapConfig.appid);
				signParams.put("appid", organization.getAppid());
//				signParams.put("mch_id", WxapConfig.mch_id);
				signParams.put("mch_id", organization.getMchId());
//				signParams.put("op_user_id", WxapConfig.mch_id); // 操作员帐号,默认为商户号
				signParams.put("op_user_id", organization.getMchId()); // 操作员帐号,默认为商户号
			} else {
//				signParams.put("appid", WxapConfig.WX_APP_ID);
				signParams.put("appid", organization.getWxAppid());
//				signParams.put("mch_id", WxapConfig.WX_MCH_ID);
				signParams.put("mch_id", organization.getWxMchId());
//				signParams.put("op_user_id", WxapConfig.WX_MCH_ID); // 操作员帐号,默认为商户号
				signParams.put("op_user_id", organization.getWxMchId()); // 操作员帐号,默认为商户号
			}
			signParams.put("nonce_str", UUID.get());
			signParams.put("transaction_id", order.getNumber()); // 微信订单号
			String tradeNo = order.getTradeNo();
			if (StringUtils.isEmpty(tradeNo)) {
				tradeNo = order.getId();
			}
			result.put("out_trade_no", tradeNo);
			signParams.put("out_trade_no", tradeNo); // 商户订单号
			signParams.put("out_refund_no", order.getId()); // 商户退款单号
			Integer fee = null;
			if (order.getFinalFee() != null) {
				fee = order.getFinalFee();
			} else {
				fee = order.getFee();
			}
			signParams.put("total_fee", fee + ""); // 总金额
			signParams.put("refund_fee", fee + ""); // 退款金额

			StringBuffer sb = new StringBuffer();
			for (String key : signParams.keySet()) {
				sb.append(key).append("=").append(signParams.get(key)).append("&");
			}
//			sb.append("key=").append(WxapConfig.app_key);
			sb.append("key=").append(organization.getAppKey());
			logger.debug("params : {}", sb);
			String sign = MD5Util.MD5Encode(sb.toString(), "UTF-8");
			signParams.put("sign", sign.toUpperCase());
			logger.debug("sign : {}", sign);
			String requestBody = CommonOAUtils.converteToXml(signParams);
			logger.debug("xml : {}", requestBody);
			String res;
			String url = configs.get("url.refund");
			logger.debug("url.refund:{}",url);
			logger.debug("getCertPassword:{}",organization.getCertPassword());
			logger.debug("getCertlocalPath:{}",organization.getCertlocalPath());
			if (Constants.PayType.WEIXIN == order.getPayType()) {
				HttpsRequest req = new HttpsRequest(organization.getCertPassword(), organization.getCertlocalPath());
				res = req.sendPost(url, requestBody, organization.getCertPassword(), organization.getCertlocalPath());
			} else {
				HttpsPlatRequest req = new HttpsPlatRequest(organization.getWxCertPassword(), organization.getWxCertlocalPath());
				res = req.sendPost(url, requestBody, organization.getCertPassword(), organization.getWxCertlocalPath());
			}
			logger.debug("res {}", res);
			Map<String, String> ret = CommonOAUtils.converteToMap(res);
			logger.debug("return_code {}", ret.get("return_code"));
			if ("SUCCESS".equals(ret.get("return_code"))) {
				logger.debug("result_code {}", ret.get("result_code"));
				if ("SUCCESS".equals(ret.get("result_code"))) {
					result.put(Constants.Result.RESULT, true);
				} else {
					result.put(Constants.Result.REASON, ret.get("err_code_des"));
					logger.debug("wxapRefund pay error,the error is {}", ret.get("err_code_des"));
				}
			} else {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void returnOrderLog(String userId, Order order, String comment, boolean isPayLog, String batch_no, String orderLogAction, String payLogStatus) {
		// 记录订单日志
		OrderLog log = new OrderLog();
		log.setId(UUID.get());
		log.setCt(new Date());
		log.setOrderId(order.getId());
		log.setUserId(userId);
		log.setAction(orderLogAction);
		log.setComment(comment);
		orderLogMapper.insert(log);

		if (isPayLog) {// 是否记录支付日志 成功才记录
			// 记录支付日志
			PayLog payLog = new PayLog();
			payLog.setId(UUID.get());
			payLog.setTradeNo(order.getTradeNo());
			payLog.setOrderId(order.getId());
			payLog.setUserId(order.getCustomerId());
			if (Constants.PayStatus.REFUND_SUCCESS.equals(payLogStatus)) {
				payLog.setFee(-order.getFee());
				payLog.setFinalFee(-order.getFinalFee());
			} else {
				payLog.setFee(order.getFee());
				payLog.setFinalFee(order.getFinalFee());
			}

			payLog.setStatus(payLogStatus);
			payLog.setCreateTime(new Date());
			payLog.setOutTradeNo(batch_no);
			payLog.setPayType(order.getPayType());
			payLogMapper.insertSelective(payLog);
		}
	}
	
	public Page<OrderVo> statistics(PageRequest pageRequest,Map<String,Object> searchParams) throws Exception {
    	int total = 0;
    	Map<String, Object> paras = new HashMap<String, Object>();
    	Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
        if(filters.get("LIKE_areaCode")!=null){
        	SearchFilter filter = filters.get("LIKE_areaCode");
        	if(filter.fieldName.equals("areaCode")){
        		if(((String)filter.value).endsWith("0000")){
        			filter.value = ((String)filter.value).substring(0,2)+"%";
        		}else if(((String)filter.value).endsWith("00")){
        			filter.value = ((String)filter.value).substring(0,4)+"%";
        		}else{
        			
        		}
        	}
        	paras.put("areaCode", filter.value);
        }
        if(filters.get("EQ_statiumId")!=null){
        	SearchFilter filter = filters.get("EQ_statiumId");
        	String statiumId = (String)filter.value;
        	filters.remove("EQ_statiumId");
        	paras.put("statiumId",statiumId);
        }
        if(filters.get("EQ_coachName")!=null){
        	SearchFilter filter = filters.get("EQ_coachName");
        	String coachName = (String)filter.value;
        	CoacherCriteria coachCriteria = new CoacherCriteria();
			coachCriteria.createCriteria().andNameEqualTo(coachName);
			List<Coacher> coachers = coachMapper.selectByExample(coachCriteria);
			if(coachers.size()==0){
        		return new PageImpl<>(new ArrayList<OrderVo>(), pageRequest, 0);
        	}else{
        		paras.put("statiumId",coachers.get(0).getId());
        	}
        	filters.remove("EQ_coachName");
        }
        if(filters.get("EQ_activityName")!=null){
        	SearchFilter filter = filters.get("EQ_activityName");
        	String activityName = (String)filter.value;
        	ActivityCriteria acitvityCriteria = new ActivityCriteria();
			acitvityCriteria.createCriteria().andNameEqualTo(activityName);
			List<Activity> acitvitys = activityMapper.selectByExample(acitvityCriteria);
			if(acitvitys.size()==0){
        		return new PageImpl<>(new ArrayList<OrderVo>(), pageRequest, 0);
        	}else{
        		paras.put("statiumId",acitvitys.get(0).getId());
        	}
        	filters.remove("EQ_activityName");
        }
        if(filters.get("EQ_gameName")!=null){
        	SearchFilter filter = filters.get("EQ_gameName");
        	String gameName = (String)filter.value;
        	GamesCriteria gameCriteria = new GamesCriteria();
			gameCriteria.createCriteria().andNameEqualTo(gameName);
			List<Games> Games = gameMapper.selectByExample(gameCriteria);
			if(Games.size()==0){
        		return new PageImpl<>(new ArrayList<OrderVo>(), pageRequest, 0);
        	}else{
        		paras.put("statiumId",Games.get(0).getId());
        	}
        	filters.remove("EQ_gameName");
        }
        if(filters.get("EQ_status")==null){
        	paras.put("status",Constants.OrderStatus.PLAYING);
        }else{
        	SearchFilter statusFilter = filters.get("EQ_status");
        	if(Constants.OrderStatus.PLAYING.equals((String)statusFilter.value)||Constants.OrderStatus.REFUNDED.equals((String)statusFilter.value)){
        		paras.put("status",statusFilter.value);
        		if(filters.get("GTE_createTime")!=null){
        			paras.put("GTE_editTime", String.valueOf(filters.get("GTE_createTime").value));
            	}
            	if(filters.get("LTE_createTime")!=null){
            		paras.put("LTE_editTime", String.valueOf(filters.get("LTE_createTime").value));
            	}
        	}else if("ALL".equals((String)statusFilter.value)){
        		if(filters.get("GTE_createTime")!=null){
        			paras.put("GTE_createTime", String.valueOf(filters.get("GTE_createTime").value));
        		}
        		if(filters.get("LTE_createTime")!=null){
        			paras.put("LTE_createTime", String.valueOf(filters.get("LTE_createTime").value));
        		}
        		paras.put("statusIn", new String[]{Constants.OrderStatus.PLAYING,Constants.OrderStatus.PAIED,Constants.OrderStatus.VERIFY});
        	}else if(Constants.OrderStatus.PAIED.equals((String)statusFilter.value)){
        		if(filters.get("GTE_createTime")!=null){
        			paras.put("GTE_createTime", String.valueOf(filters.get("GTE_createTime").value));
        		}
        		if(filters.get("LTE_createTime")!=null){
        			paras.put("LTE_createTime", String.valueOf(filters.get("LTE_createTime").value));
        		}
        		paras.put("statusIn", new String[]{Constants.OrderStatus.PAIED,Constants.OrderStatus.VERIFY});
        	}
        }
        String sqlCount = FreeMarkerUtils.format("/template/order/order_statistics_count.ftl", paras);
        logger.debug(sqlCount);
        logger.debug(MyGson.getInstance().toJson(paras));
        List<Map<String,Object>> countMap = jdbcTemplate.queryForList(sqlCount);
        total = new BigDecimal((long)countMap.get(0).get("cont")).intValue();
        if(pageRequest.getOffset()>= 1000){
        	paras.put("offset", String.valueOf(pageRequest.getOffset()).replace(",",""));
        }else{
        	paras.put("offset", pageRequest.getOffset());
        }
        
        paras.put("pageSize", pageRequest.getPageSize());
		String sqlList = FreeMarkerUtils.format("/template/order/order_statistics.ftl", paras);
        logger.debug(sqlList);
        logger.debug(MyGson.getInstance().toJson(paras));
        List<Map<String,Object>> orders = jdbcTemplate.queryForList(sqlList);
        List<OrderVo> orderList = new ArrayList<OrderVo>();
		for(Map<String, Object> order:orders){
			OrderVo vo = new OrderVo();
			Date ct = (Date)order.get("ct");
			Date et = (Date)order.get("et");
			String id = (String)order.get("id");
			String status = (String)order.get("status");
			vo.setId(id);
			vo.setCt(ct);
			vo.setEt(et);
			if(order.get("subsidies")!=null){
				int subsidies = (Integer)order.get("subsidies");
				vo.setSubsidies((new BigDecimal(subsidies).divide(new BigDecimal(100))).intValue());
			}else{
				vo.setSubsidies(0);
			}
			vo.setStatus(status);
			
			Integer ordersType = order.get("ordersType")==null?-1:(Integer)order.get("ordersType");
			String statiumId = (String)order.get("statiumId");
			if(StringUtils.isNotBlank(statiumId)){
				if(ordersType==0||ordersType==3||ordersType==5){//订场
					StatiumDetail detail = this.selectByPrimaryKey(StatiumDetail.class, statiumId);
					if(detail!=null){
						Map<String,String> zoneMap = Zonemap.split(detail.getAreaCode());
						vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city")+zoneMap.get("area"));
						vo.setName(detail.getName());
						vo.setAddress(detail.getAddress());
					}
				}else if(ordersType==1){//教培
					Coacher coach = this.selectByPrimaryKey(Coacher.class, statiumId);
					if(coach!=null){
						if(StringUtils.isNotEmpty(coach.getArea())){
							Map<String,String> zoneMap = Zonemap.split(coach.getArea());
							vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city")+zoneMap.get("area"));
						}
						vo.setName(coach.getName());
						vo.setAddress(coach.getPhone());
					}
				}else if(ordersType==2){//活动
					Activity activity = this.selectByPrimaryKey(Activity.class, statiumId);
					if(activity!=null){
						vo.setAreaStr(activity.getProvince()+activity.getCity()+activity.getArea());
						vo.setName(activity.getName());
						vo.setAddress(activity.getAddress());
					}
				}else if(ordersType==4){//赛事
					Games game = this.selectByPrimaryKey(Games.class, statiumId);
					if(game!=null){
						Map<String,String> zoneMap = Zonemap.split(game.getAreaCode());
						vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city")+zoneMap.get("area"));
						vo.setName(game.getName());
						vo.setAddress(game.getAddress());
					}
				}else if(ordersType==6){
					EnjoyGame game = this.selectByPrimaryKey(EnjoyGame.class, statiumId);
					if(StringUtils.isNotBlank(game.getGid())){
						game = this.selectByPrimaryKey(EnjoyGame.class, game.getGid());
					}
					StatiumDetail detail = this.selectByPrimaryKey(StatiumDetail.class, game.getStatiumId());
					if(detail!=null){
						Map<String,String> zoneMap = Zonemap.split(detail.getAreaCode());
						vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city")+zoneMap.get("area"));
						vo.setName(detail.getName());
						vo.setAddress(detail.getAddress());
					}
				}
			}
			
			if(order.get("finalFee")!=null){
				Double finel = BigDecimal.valueOf((Integer)order.get("finalFee")).multiply(BigDecimal.valueOf(0.01)).doubleValue();
				vo.setFinalFee(finel.intValue());
			}
			if(order.get("qiuyouFee")!=null){
				Double finel = BigDecimal.valueOf((Integer)order.get("qiuyouFee")).multiply(BigDecimal.valueOf(0.01)).doubleValue();
				vo.setQiuyouFee(finel.intValue());
			}
    		/**订单用户信息**/
    		if(order.get("customerId")!=null){
    			String customerId = (String)order.get("customerId");
    			SsoUser user = this.selectByPrimaryKey(SsoUser.class,customerId);
    			if(user!=null){
    				String phone = user.getPhone();
            		vo.setPhone(phone);
            		vo.setUsername(user.getName());
            		/**订单用户信息**/
            		
            		/**合作次数**/
            		
            		OrderCriteria criteria = new OrderCriteria();
            		OrderCriteria.Criteria c = criteria.createCriteria();
            		c.andCustomerIdEqualTo(customerId);
            		c.andCtLessThanOrEqualTo((Date)order.get("ct"));
            		List<String> statuss = new ArrayList<String>();
            		statuss.add(Constants.OrderStatus.PLAYING);
            		statuss.add(Constants.OrderStatus.PAIED);
            		statuss.add(Constants.OrderStatus.VERIFY);
            		c.andStatusIn(statuss);
            		int count = orderMapper.countByExample(criteria);
            		vo.setCooperateNum(count);
            		/**合作次数**/
    			}
    		}
    		
    		
    		/**支付方式**/
    		Integer type = order.get("payType")==null?-1:(Integer)order.get("payType");
    		String payType="";
    		switch (type) {
			case 1:
				payType="支付宝付款";
				break;
			case 2:
				payType="微信付款";
				break;
			case 3:
				payType="公众平台付款";
				break;
			case 4:
				payType="球友圈付款";
				break;
			case 5:
				payType="账户付款";
				break;
			case 6:
				payType="企业基金付款";
				break;
			case 9:
				payType="招行付款";
				break;
			case 11:
				payType="京东付款";
				break;
			default:
				payType="无法确定付款途径";
				break;
			}
    		vo.setPayTypeStr(payType);
			if(type == Constants.PayType.ACCOUNT){
				//账户支付查看是否有奖金支付
				SsoUserBonusAccountLogCriteria bonusAccountLogC = new SsoUserBonusAccountLogCriteria();
				SsoUserBonusAccountLogCriteria.Criteria bonusAccountLogCri = bonusAccountLogC.createCriteria();
				bonusAccountLogCri.andOrderIdEqualTo(vo.getId());
				bonusAccountLogCri.andDescriptionLike("消费");
				if(ssoUserBonusAccountLogMapper.countByExample(bonusAccountLogC) > 0) {
					SsoUserBonusAccountLog bonusAccountLog = ssoUserBonusAccountLogMapper.selectByExample(bonusAccountLogC).get(0);
					vo.setBounsAccountFee(-BigDecimal.valueOf(bonusAccountLog.getAmount()).multiply(BigDecimal.valueOf(0.01)).intValue());
					vo.setAcountFee(vo.getFinalFee() - vo.getBounsAccountFee());
				}else {
					vo.setAcountFee(vo.getFinalFee().intValue());
				}
			}
    		/**支付方式**/
    		/**订单类型**/
    		String ordersTypeStr="";
    		switch (ordersType) {
			case 0:
				ordersTypeStr="订场";
				break;
			case 1:
				ordersTypeStr="教陪练";
				break;
			case 2:
				ordersTypeStr="活动";
				break;
			case 3:
				ordersTypeStr="约球";
				break;
			case 4:
				ordersTypeStr="赛事";
				break;
			case 5:
				ordersTypeStr="约球报名";
				break;
			case 6:
				ordersTypeStr="cta报名";
				break;
			default:
				ordersTypeStr="无法确定订单类型";
				break;
			}
    		vo.setOrdersTypeStr(ordersTypeStr);
    		/**订单类型**/
    		/**优惠券**/
    		if(order.get("couponId")!=null){
    			CouponHistory couponHistory = this.selectByPrimaryKey(CouponHistory.class,(String)order.get("couponId"));
    			Double couponSum = 0.0d;
    			if(couponHistory!=null){
    				String orderTime = couponHistory.getOrderTime();
    				if(StringUtils.isEmpty(orderTime)){
    					orderTime = "1";
    				}
    				couponSum = (couponHistory.getAmount().multiply(new BigDecimal(Integer.parseInt(orderTime)))).doubleValue();
    			}
    			vo.setSubAmount(couponSum.intValue());
    		}
    		/**优惠券**/
    		/**订场明细**/
    		OrderItemCriteria criteriaOrderItem = new OrderItemCriteria();
    		OrderItemCriteria.Criteria cOrderItem = criteriaOrderItem.createCriteria();
            cOrderItem.andOrderIdEqualTo((String)order.get("id"));
            List<OrderItem> orderItemList = orderItemMapper.selectByExample(criteriaOrderItem);
            int periodNum = orderItemList.size();
            vo.setPeriodNum(periodNum);
            if(orderItemList.size()!=0){
            	 String orderDate = DateFormatUtils.format(orderItemList.get(0).getStartDate(),"yyyy-MM-dd");
            	 String orderTime = "";
            	 BigDecimal totalCostPrice = new BigDecimal(0);
                 for(int i=0;i<periodNum;i++){
                	logger.debug("订单ID，{}",id);
                	String temp = "";
                	if(orderItemList.get(i).getStartTime()!=null){
                		temp = DateFormatUtils.format(new Date(orderItemList.get(i).getStartTime()*1000L),"HH:mm:ss");
                	}
                	if(orderItemList.get(i).getEndTime()!=null){
                		temp += DateFormatUtils.format(new Date(orderItemList.get(i).getEndTime()*1000L),"~HH:mm:ss ");
                	}
                 	orderTime+=temp;
                 	if(ordersType==0||ordersType==3){
                 		int costPrice = orderItemList.get(i).getCostPrice();
                 		totalCostPrice = totalCostPrice.add(new BigDecimal(costPrice));
                    }
                 }
                 vo.setCostPrice((totalCostPrice.divide(new BigDecimal(100))).intValue());
                 vo.setOrderDate(orderDate);
                 vo.setOrderTime(orderTime);
                 /**订场明细**/
                 /**场地信息**/
                 String ballType = orderItemList.get(0).getSportType();
                 String sportType="";
                 sportType = CommonOAUtils.sportsEToC(ballType);
                vo.setSportType(sportType);
                orderList.add(vo);
                 /**场地信息**/
            }
    		
		}
		Collections.sort(orderList, new Comparator<OrderVo>() {

			@Override
			public int compare(OrderVo o1, OrderVo o2) {
				if(DateUtil.compareDate(o1.getCt(), o2.getCt())==0){
					return o1.getPhone().compareTo(o2.getPhone());
				}else{
					return DateUtil.compareDate(o2.getCt(),o1.getCt());
				}
			}
		});
    		
    	return new PageImpl<>(orderList, pageRequest, total);
    	
    	
    }
	
	public List<OrderVo> exports(Map<String,Object> searchParams) throws Exception {
    	Map<String, Object> paras = new HashMap<String, Object>();
    	Map<String, SearchFilter> filters = SearchFilter.parse(searchParams);
        if(filters.get("LIKE_areaCode")!=null){
        	SearchFilter filter = filters.get("LIKE_areaCode");
        	if(filter.fieldName.equals("areaCode")){
        		if(((String)filter.value).endsWith("0000")){
        			filter.value = ((String)filter.value).substring(0,2)+"%";
        		}else if(((String)filter.value).endsWith("00")){
        			filter.value = ((String)filter.value).substring(0,4)+"%";
        		}else{
        			
        		}
        	}
        	paras.put("areaCode", filter.value);
        }
        if(filters.get("EQ_coachName")!=null){
        	SearchFilter filter = filters.get("EQ_coachName");
        	String coachName = (String)filter.value;
        	CoacherCriteria coachCriteria = new CoacherCriteria();
			coachCriteria.createCriteria().andNameEqualTo(coachName);
			List<Coacher> coachers = coachMapper.selectByExample(coachCriteria);
			if(coachers.size()==0){
        		return new ArrayList<OrderVo>();
        	}else{
        		paras.put("statiumId",coachers.get(0).getId());
        	}
        	filters.remove("EQ_coachName");
        }
        if(filters.get("EQ_activityName")!=null){
        	SearchFilter filter = filters.get("EQ_activityName");
        	String activityName = (String)filter.value;
        	ActivityCriteria acitvityCriteria = new ActivityCriteria();
			acitvityCriteria.createCriteria().andNameEqualTo(activityName);
			List<Activity> acitvitys = activityMapper.selectByExample(acitvityCriteria);
			if(acitvitys.size()==0){
				return new ArrayList<OrderVo>();
        	}else{
        		paras.put("statiumId",acitvitys.get(0).getId());
        	}
        	filters.remove("EQ_activityName");
        }
        if(filters.get("EQ_gameName")!=null){
        	SearchFilter filter = filters.get("EQ_gameName");
        	String gameName = (String)filter.value;
        	GamesCriteria gameCriteria = new GamesCriteria();
			gameCriteria.createCriteria().andNameEqualTo(gameName);
			List<Games> Games = gameMapper.selectByExample(gameCriteria);
			if(Games.size()==0){
				return new ArrayList<OrderVo>();
        	}else{
        		paras.put("statiumId",Games.get(0).getId());
        	}
        	filters.remove("EQ_gameName");
        }
        if(filters.get("EQ_statiumId")!=null){
        	SearchFilter filter = filters.get("EQ_statiumId");
        	String statiumId = (String)filter.value;
        	filters.remove("EQ_statiumId");
        	paras.put("statiumId",statiumId);
        }
        if(filters.get("EQ_status")==null){
        	paras.put("status",Constants.OrderStatus.PLAYING);
        }else{
        	SearchFilter statusFilter = filters.get("EQ_status");
        	if(Constants.OrderStatus.PLAYING.equals((String)statusFilter.value)||Constants.OrderStatus.REFUNDED.equals((String)statusFilter.value)){
        		paras.put("status",statusFilter.value);
        		if(filters.get("GTE_createTime")!=null){
        			paras.put("GTE_editTime", String.valueOf(filters.get("GTE_createTime").value));
            	}
            	if(filters.get("LTE_createTime")!=null){
            		paras.put("LTE_editTime", String.valueOf(filters.get("LTE_createTime").value));
            	}
        	}else if("ALL".equals((String)statusFilter.value)){
        		if(filters.get("GTE_createTime") != null){
            		paras.put("GTE_createTime", String.valueOf(filters.get("GTE_createTime").value));
        		}
        		if(filters.get("LTE_createTime") != null){
        			paras.put("LTE_createTime", String.valueOf(filters.get("LTE_createTime").value));
        		}
        		paras.put("statusIn", new String[]{Constants.OrderStatus.PLAYING,Constants.OrderStatus.PAIED,Constants.OrderStatus.VERIFY});
        	}else if(Constants.OrderStatus.PAIED.equals((String)statusFilter.value)){
        		if(filters.get("GTE_createTime") != null){
        			paras.put("GTE_createTime", String.valueOf(filters.get("GTE_createTime").value));
        		}
        		if(filters.get("LTE_createTime") != null){
        			paras.put("LTE_createTime", String.valueOf(filters.get("LTE_createTime").value));
        		}
        		paras.put("statusIn", new String[]{Constants.OrderStatus.PAIED,Constants.OrderStatus.VERIFY});
        	}
        }
        String sqlList = FreeMarkerUtils.format("/template/order/order_statistics.ftl", paras);
        List<Map<String,Object>> orders = jdbcTemplate.queryForList(sqlList);
        List<OrderVo> orderList = new ArrayList<OrderVo>();
		for(Map<String, Object> order:orders){
			OrderVo vo = new OrderVo();
			Date ct = (Date)order.get("ct");
			Date et = (Date)order.get("et");
			String id = (String)order.get("id");
			String status = (String)order.get("status");
			if(order.get("subsidies")!=null){
				int subsidies = (Integer)order.get("subsidies");
				vo.setSubsidies((new BigDecimal(subsidies).divide(new BigDecimal(100))).intValue());
			}else{
				vo.setSubsidies(0);
			}
			vo.setId(id);
			vo.setCt(ct);
			vo.setEt(et);
			vo.setStatus(status);
			
			Integer ordersType = order.get("ordersType")==null?-1:(Integer)order.get("ordersType");
			String statiumId = (String)order.get("statiumId");
			if(StringUtils.isNotBlank(statiumId)){
				if(ordersType==0||ordersType==3||ordersType==5){//订场
					StatiumDetail detail = this.selectByPrimaryKey(StatiumDetail.class, statiumId);
					if(detail!=null){
						Map<String,String> zoneMap = Zonemap.split(detail.getAreaCode());
						vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city"));
						vo.setName(detail.getName());
						vo.setAddress(detail.getAddress());
					}
				}else if(ordersType==1){//教培
					Coacher coach = this.selectByPrimaryKey(Coacher.class, statiumId);
					if(coach!=null){
						if(StringUtils.isNotEmpty(coach.getArea())){
							Map<String,String> zoneMap = Zonemap.split(coach.getArea());
							vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city"));
						}
						vo.setName(coach.getName());
						vo.setAddress(coach.getPhone());
					}
				}else if(ordersType==2){//活动
					Activity activity = this.selectByPrimaryKey(Activity.class, statiumId);
					if(activity!=null){
						vo.setAreaStr(activity.getProvince()+activity.getCity()+activity.getArea());
						vo.setName(activity.getName());
						vo.setAddress(activity.getAddress());
					}
				}else if(ordersType==4){//赛事
					Games game = this.selectByPrimaryKey(Games.class, statiumId);
					if(game!=null){
						Map<String,String> zoneMap = Zonemap.split(game.getAreaCode());
						vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city"));
						vo.setName(game.getName());
						vo.setAddress(game.getAddress());
					}
				}else if(ordersType==6){
					EnjoyGame game = this.selectByPrimaryKey(EnjoyGame.class, statiumId);
					if(StringUtils.isNotBlank(game.getGid())){
						game = this.selectByPrimaryKey(EnjoyGame.class, game.getGid());
					}
					StatiumDetail detail = this.selectByPrimaryKey(StatiumDetail.class, game.getStatiumId());
					if(detail!=null){
						Map<String,String> zoneMap = Zonemap.split(detail.getAreaCode());
						vo.setAreaStr(zoneMap.get("province")+zoneMap.get("city")+zoneMap.get("area"));
						vo.setName(detail.getName());
						vo.setAddress(detail.getAddress());
					}
				}
			}
			
			if(order.get("finalFee")!=null){
				Double finel = BigDecimal.valueOf((Integer)order.get("finalFee")).multiply(BigDecimal.valueOf(0.01)).doubleValue();
				vo.setFinalFee(finel.intValue());
			}
			
			if(order.get("qiuyouFee")!=null){
				Double finel = BigDecimal.valueOf((Integer)order.get("qiuyouFee")).multiply(BigDecimal.valueOf(0.01)).doubleValue();
				vo.setQiuyouFee(finel.intValue());
			}
			
    		/**订单用户信息**/
    		if(order.get("customerId")!=null){
    			String customerId = (String)order.get("customerId");
    			SsoUser user = this.selectByPrimaryKey(SsoUser.class,customerId);
    			if(user!=null){
    				String phone = user.getPhone();
            		vo.setPhone(phone);
            		vo.setUsername(user.getName());
            		/**订单用户信息**/
            		
            		/**合作次数**/
            		
            		OrderCriteria criteria = new OrderCriteria();
            		OrderCriteria.Criteria c = criteria.createCriteria();
            		c.andCustomerIdEqualTo(customerId);
            		c.andCtLessThanOrEqualTo((Date)order.get("ct"));
            		List<String> statuss = new ArrayList<String>();
            		statuss.add(Constants.OrderStatus.PLAYING);
            		statuss.add(Constants.OrderStatus.PAIED);
            		statuss.add(Constants.OrderStatus.VERIFY);
            		c.andStatusIn(statuss);
            		int count = orderMapper.countByExample(criteria);
            		vo.setCooperateNum(count);
            		/**合作次数**/
    			}
    		}
    		
    		
    		/**支付方式**/
    		Integer type = order.get("payType")==null?-1:(Integer)order.get("payType");
    		String payType="";
    		switch (type) {
			case 1:
				payType="支付宝付款";
				break;
			case 2:
				payType="微信付款";
				break;
			case 3:
				payType="公众平台付款";
				break;
			case 4:
				payType="球友圈付款";
				break;
			case 5:
				payType="账户付款";
				break;
			case 6:
				payType="企业基金付款";
				break;
			case 9:
				payType="招行付款";
				break;
			case 11:
				payType="京东付款";
				break;
			default:
				payType="无法确定付款途径";
				break;
			}
    		vo.setPayTypeStr(payType);
			if(type == Constants.PayType.ACCOUNT){
				//账户支付查看是否有奖金支付
				SsoUserBonusAccountLogCriteria bonusAccountLogC = new SsoUserBonusAccountLogCriteria();
				SsoUserBonusAccountLogCriteria.Criteria bonusAccountLogCri = bonusAccountLogC.createCriteria();
				bonusAccountLogCri.andOrderIdEqualTo(vo.getId());
				bonusAccountLogCri.andDescriptionLike("消费");
				if(ssoUserBonusAccountLogMapper.countByExample(bonusAccountLogC) > 0) {
					SsoUserBonusAccountLog bonusAccountLog = ssoUserBonusAccountLogMapper.selectByExample(bonusAccountLogC).get(0);
					vo.setBounsAccountFee(-BigDecimal.valueOf(bonusAccountLog.getAmount()).multiply(BigDecimal.valueOf(0.01)).intValue());
					vo.setAcountFee(vo.getFinalFee() - vo.getBounsAccountFee());
				}else {
					vo.setAcountFee(vo.getFinalFee());
				}
			}
    		/**支付方式**/
    		/**订单类型**/
    		String ordersTypeStr="";
    		switch (ordersType) {
			case 0:
				ordersTypeStr="订场";
				break;
			case 1:
				ordersTypeStr="教陪练";
				break;
			case 2:
				ordersTypeStr="活动";
				break;
			case 3:
				ordersTypeStr="约球";
				break;
			case 4:
				ordersTypeStr="赛事";
				break;
			case 5:
				ordersTypeStr="约球报名";
				break;
			case 6:
				ordersTypeStr="cta报名";
				break;
			default:
				ordersTypeStr="无法确定订单类型";
				break;
			}
    		vo.setOrdersTypeStr(ordersTypeStr);
    		/**订单类型**/
    		/**优惠券**/
    		if(order.get("couponId")!=null){
    			CouponHistory couponHistory = this.selectByPrimaryKey(CouponHistory.class,(String)order.get("couponId"));
    			Double couponSum = 0.0d;
    			if(couponHistory!=null){
    				String orderTime = couponHistory.getOrderTime();
    				if(StringUtils.isEmpty(orderTime)){
    					orderTime = "1";
    				}
    				couponSum = (couponHistory.getAmount().multiply(new BigDecimal(Integer.parseInt(orderTime)))).doubleValue();
    			}
    			vo.setSubAmount(couponSum.intValue());
    		}
    		/**优惠券**/
    		/**订场明细**/
    		OrderItemCriteria criteriaOrderItem = new OrderItemCriteria();
    		OrderItemCriteria.Criteria cOrderItem = criteriaOrderItem.createCriteria();
    		logger.debug(order.toString());
            cOrderItem.andOrderIdEqualTo((String)order.get("id"));
            List<OrderItem> orderItemList = orderItemMapper.selectByExample(criteriaOrderItem);
            int periodNum = orderItemList.size();
            vo.setPeriodNum(periodNum);
            if(orderItemList.size()!=0){
            	 String orderDate = DateFormatUtils.format(orderItemList.get(0).getStartDate(),"yyyy-MM-dd");
            	 String orderTime = "";
            	 BigDecimal totalCostPrice = new BigDecimal(0);
                 for(int i=0;i<periodNum;i++){
                	String temp = "";
                 	if(orderItemList.get(i).getStartTime()!=null){
                 		temp = DateFormatUtils.format(new Date(orderItemList.get(i).getStartTime()*1000L),"HH:mm:ss");
                 	}
                 	if(orderItemList.get(i).getEndTime()!=null){
                 		temp += DateFormatUtils.format(new Date(orderItemList.get(i).getEndTime()*1000L),"~HH:mm:ss ");
                 	}
                 	orderTime+=temp;
                 	if(ordersType==0||ordersType==3){
                 		int costPrice = orderItemList.get(i).getCostPrice();
                 		totalCostPrice = totalCostPrice.add(new BigDecimal(costPrice));
                 	}
                 }
                 vo.setCostPrice((totalCostPrice.divide(new BigDecimal(100))).intValue());
                 vo.setOrderDate(orderDate);
                 vo.setOrderTime(orderTime);
                 /**订场明细**/
                 /**场地信息**/
                 String ballType = orderItemList.get(0).getSportType();
                 String sportType="";
                 sportType = CommonOAUtils.sportsEToC(ballType);
                 vo.setSportType(sportType);
                 orderList.add(vo);
                 /**场地信息**/
            }
    		
		}
		Collections.sort(orderList, new Comparator<OrderVo>() {

			@Override
			public int compare(OrderVo o1, OrderVo o2) {
				if(DateUtil.compareDate(o1.getCt(), o2.getCt())==0){
					return o1.getPhone().compareTo(o2.getPhone());
				}else{
					return DateUtil.compareDate(o1.getCt(), o2.getCt());
				}
			}
		});
    		
    	return orderList;
    	
    }
	
	/**
	 * 
	 * <是否允许退款><赛事或活动开场前4个小时不允许退款>
	 *
	 * @create：2015年10月21日 上午10:37:14
	 * @author： liangsh
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	public boolean allownRefund(String orderId) throws Exception {
		Order order = this.selectByPrimaryKey(Order.class,orderId);
		if(order.getOrdersType() == Constants.OrderType.ACTIVITY){ //活动
			//获取活动开始时间
			MemberListCriteria c = new MemberListCriteria();
			MemberListCriteria.Criteria cri = c.createCriteria();
			cri.andOrderidEqualTo(orderId);
			List<MemberList> memberList  = memberListMapper.selectByExample(c);
			Date startDate = memberList.get(0).getDate();
			String now = DateUtil.nowDateTimeString();
			Date nowDate = DateUtil.parse(now, "yyyy-MM-dd HH:mm:ss",null);
			//计算是否在开场前4小时之内
			if(com.lc.zy.common.util.DateUtils.intervalMinutes(startDate,nowDate ) > 4 * 60){
				return true;
			}else{  
				return false;
			}
		}else if(order.getOrdersType() == Constants.OrderType.EVENT){//赛事
			//赛事详情
			Games event = this.selectByPrimaryKey(Games.class,order.getStatiumId()); 
			//赛事开始时
			String statrtTime = event.getStartTime();
			Date startDate = DateUtil.parse(statrtTime,"yyyy-MM-dd HH:mm", null);
			String now = DateUtil.nowDateTimeString();
			Date nowDate = DateUtil.parse(now, "yyyy-MM-dd HH:mm",null);
			//计算是否在开场前4小时之内
			if(com.lc.zy.common.util.DateUtils.intervalMinutes(startDate,nowDate ) > 4 * 60){
				return true;
			}else{  
				return false;
			}
		}else{
			return true;
		}
		
	}
	
	public List<Map<String,Object>> orderDay(String date){
	     Map<String, Object> paras = new HashMap<String, Object>();
	     paras.put("searchDay", date);
		 String sqlList = FreeMarkerUtils.format("/template/statistic/orders_day.ftl", paras);
	     List<Map<String,Object>> orders = jdbcTemplate.queryForList(sqlList);
	     return orders;
	}
	
	public List<Map<String,Object>> orderDays(String from,String to){
	     Map<String, Object> paras = new HashMap<String, Object>();
	     if(StringUtils.isNotBlank(from)){
	    	 paras.put("from", from);
	     }
	     if(StringUtils.isNotBlank(to)){
	    	 paras.put("to", to);
	     }
		 String sqlList = FreeMarkerUtils.format("/template/statistic/orders_days.ftl", paras);
	     List<Map<String,Object>> orders = jdbcTemplate.queryForList(sqlList);
	     return orders;
	}
	
	@Transactional(readOnly = false)
	public List<Map<String,String>> handle(String userId) throws Exception{
		
		SecurityLock securityLock = new SecurityLock();
    	try {
    		String lockStr = MD5Util.MD5Encode("handle_order","utf-8");
    		if(!securityLock.lock(lockStr)){
    			logger.debug("{},等待获取任务！",userId);
    			throw new Exception("not_get_task");
    		}else{
    			
    		}
    		OrderCriteria orderCri = new OrderCriteria();
			OrderCriteria.Criteria cri = orderCri.createCriteria();
			cri.andStatusEqualTo(Constants.OrderStatus.PAIED);
			List<Integer> types = new ArrayList<Integer>();
			types.add(Constants.OrderType.STATIUM);
			types.add(Constants.OrderType.BOOKBALL);
			types.add(Constants.OrderType.COACH);
			cri.andOrdersTypeIn(types);
			cri.andOrderTypeEqualTo(Constants.BookType.APP);
			cri.andHandlerIsNull();
			orderCri.setMysqlOffset(0);
			orderCri.setMysqlLength(1);
			List<Order> orders = orderMapper.selectByExample(orderCri);
			if(CollectionUtils.isEmpty(orders)){
				return new ArrayList<Map<String,String>>();
			}
			Order order_ = orders.get(0);
			String statiumId_ = order_.getStatiumId();
			List<Order> relOrders = getRelationHandle(statiumId_,order_.getId());
			orders.addAll(relOrders);
			List<Map<String,String>> vos = new ArrayList<Map<String,String>>();
			Map<String,String> result = null;
			for(Order order:orders){
				result = new HashMap<String, String>();
				result.put("id", order.getId());
				String statiumId = order.getStatiumId();
				if(order.getOrdersType()==Constants.OrderType.BOOKBALL||order.getOrdersType()==Constants.OrderType.STATIUM){
					StatiumDetail detail = this.selectByPrimaryKey(StatiumDetail.class, statiumId);
					Map<String,String> zoneMap = Zonemap.split(detail.getAreaCode());
					String area = zoneMap.get("province")+zoneMap.get("city");
					result.put("name", detail.getName()+"【"+area+"】"+"【"+detail.getAddress()+"】");
	    		}else if(order.getOrdersType()==Constants.OrderType.COACH){
	    			Coacher coacher = this.selectByPrimaryKey(Coacher.class, statiumId);
	    			if(coacher!=null){
						if(StringUtils.isNotEmpty(coacher.getArea())){
							Map<String,String> zoneMap = Zonemap.split(coacher.getArea());
							String area = zoneMap.get("province")+zoneMap.get("city");
							result.put("name", coacher.getName()+"【"+area+"】");
						}
					}
	    		}
				result.put("statiumId",statiumId);
				vos.add(result);
				Order save_ = new Order();
				save_.setId(order.getId());
				save_.setHandler(userId);
				this.updateByPrimaryKeySelective(save_, order.getId());
			}
			return vos;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if("not_get_task".equals(e.getMessage())){
				throw new Exception("not_get_task");
			}else{
				throw e;
			}
		}finally{
			securityLock.unlock();
		}
	}
	
	@Transactional(readOnly = false)
	public void backHandel(String orderId,String reason)throws Exception{
		Order order = new Order();
		order.setId(orderId);
		order.setHandleStatus(2);
		this.updateByPrimaryKeySelective(order, orderId);
		
		OrderHandle handle = new OrderHandle();
		handle.setId(UUID.get());
		handle.setComment(reason);
		handle.setOrderId(orderId);
		handle.setStatus(2);
		handle.setCt(new Date());
		this.insertSelective(handle, handle.getId());
	}
	
	public List<OrderVo> getHandling(String userId) throws Exception{
		
		OrderCriteria orderCri_ = new OrderCriteria();
		OrderCriteria.Criteria cri_ = orderCri_.createCriteria();
		cri_.andHandleStatusIsNull();
		cri_.andHandlerEqualTo(userId);
		cri_.andStatusEqualTo(Constants.OrderStatus.PAIED);
		
		OrderCriteria _orderCri = new OrderCriteria();
		OrderCriteria.Criteria _cri = _orderCri.createCriteria();
		_cri.andHandleStatusEqualTo(2);
		_cri.andHandlerEqualTo(userId);
		_cri.andStatusEqualTo(Constants.OrderStatus.PAIED);
		orderCri_.or(_cri);
		List<Order> orders = orderMapper.selectByExample(orderCri_);
		List<OrderVo> vos = new ArrayList<OrderVo>();
		OrderVo vo = null;
		for(Order order:orders){
			vo = new OrderVo();
			vo.setId(order.getId());
			String statiumId = order.getStatiumId();
			if(order.getOrdersType()==Constants.OrderType.BOOKBALL||order.getOrdersType()==Constants.OrderType.STATIUM){
				StatiumDetail detail = this.selectByPrimaryKey(StatiumDetail.class, statiumId);
				Map<String,String> zoneMap = Zonemap.split(detail.getAreaCode());
				String area = zoneMap.get("province")+zoneMap.get("city");
				vo.setName(detail.getName()+"【"+area+"】"+"【"+detail.getAddress()+"】");
    		}else if(order.getOrdersType()==Constants.OrderType.COACH){
    			Coacher coacher = this.selectByPrimaryKey(Coacher.class, statiumId);
    			if(coacher!=null){
					if(StringUtils.isNotEmpty(coacher.getArea())){
						Map<String,String> zoneMap = Zonemap.split(coacher.getArea());
						String area = zoneMap.get("province")+zoneMap.get("city");
						vo.setName(coacher.getName()+"【"+area+"】");
					}
				}
    		}
			vo.setStatiumId(statiumId);
			vo.setHandleStatus(order.getHandleStatus());
			vos.add(vo);
		}
		return vos;
	}
	
	public List<Order> getRelationHandle(String statiumId,String orderId)throws Exception{
		OrderCriteria orderCri = new OrderCriteria();
		OrderCriteria.Criteria cri = orderCri.createCriteria();
		cri.andStatusEqualTo(Constants.OrderStatus.PAIED);
		List<Integer> types = new ArrayList<Integer>();
		types.add(Constants.OrderType.STATIUM);
		types.add(Constants.OrderType.BOOKBALL);
		types.add(Constants.OrderType.COACH);
		cri.andOrdersTypeIn(types);
		cri.andOrderTypeEqualTo(Constants.BookType.APP);
		cri.andHandlerIsNull();
		cri.andStatiumIdEqualTo(statiumId);
		cri.andIdNotEqualTo(orderId);
		List<Order> orders = orderMapper.selectByExample(orderCri);
		return orders;
	}
	
	public Map<String,Object> item(String orderId){
		Map<String,Object> result = new HashMap<String, Object>();
    	try {
    		String[] orderIds = orderId.split("-");
    		orderId = orderIds[0];
    		Order order = this.selectByPrimaryKey(Order.class, orderId);
    		result.put("id", orderId);
    		String status = order.getStatus();
    		Integer handleStatus = order.getHandleStatus();
    		result.put("handleStatus", handleStatus);
    		if(status.equals(Constants.OrderStatus.PAIED)){
    			status = "已支付";
			}else if(status.equals(Constants.OrderStatus.PLAYING)){
				status = "已完成";
			}else if(status.equals(Constants.OrderStatus.VERIFY)){
				status = "已确认";
			}else if(status.equals(Constants.OrderStatus.REFUNDING)){
				status = "退款中";
			}else if(status.equals(Constants.OrderStatus.REFUNDED)){
				status = "已退款";
			}
    		result.put("status", status);
    		if(order.getOrdersType()==Constants.OrderType.BOOKBALL){
    			result.put("orderType", "约球订单");
    		}else if(order.getOrdersType()==Constants.OrderType.STATIUM){
    			result.put("orderType", "订场订单");
    		}else if(order.getOrdersType()==Constants.OrderType.COACH){
    			result.put("orderType", "教培练订单");
    		}
    		String couponId = order.getCouponId();
    		if(StringUtils.isNotBlank(couponId)){
    			CouponHistory history  = this.selectByPrimaryKey(CouponHistory.class, couponId);
    			BigDecimal couponAmount = history.getAmount();
    			result.put("amount", couponAmount.toString());
    		}
    		result.put("fee", (new BigDecimal(order.getFee()).divide(new BigDecimal(100))).toString());
    		result.put("finalFee", (new BigDecimal(order.getFinalFee()).divide(new BigDecimal(100))).toString());
    		result.put("ct", DateUtil.formatDate(order.getCt(), "yyyy-MM-dd HH:mm:ss"));
    		String customerId = order.getCustomerId();
    		SsoUser user = this.selectByPrimaryKey(SsoUser.class, customerId);
    		if(user!=null){
    			result.put("username", user.getNickName());
    			result.put("userphone", user.getPhone());
    		}
    		String statiumId = order.getStatiumId();
    		if(order.getOrdersType()==Constants.OrderType.BOOKBALL||order.getOrdersType()==Constants.OrderType.STATIUM){
    			StatiumDetail detail = this.selectByPrimaryKey(StatiumDetail.class, statiumId);
    			if(detail!=null){
    				result.put("tel",detail.getTel());
    			}
    		}else if(order.getOrdersType()==Constants.OrderType.COACH){
    			Coacher coacher = this.selectByPrimaryKey(Coacher.class, statiumId);
    			if(coacher!=null){
    				result.put("tel",coacher.getPhone());
    			}
    		}
    		
    		Integer payType = order.getPayType();
    		if(payType!=null){
    			String payTypeStr = "";
    			if(payType==1){
    				payTypeStr = "支付宝";
    			}else if(payType==2){
    				payTypeStr = "微信";
    			}else if(payType==3){
    				payTypeStr = "微信公众平台";
    			}else if(payType==4){
    				payTypeStr = "球友圈";
    			}else if(payType==5){
    				payTypeStr = "账户";
    			}else if(payType==9){
    				payTypeStr = "招行";
    			}
    			result.put("payType", payTypeStr);
    		}
    		OrderItemCriteria itemCri = new OrderItemCriteria();
    		OrderItemCriteria.Criteria cri = itemCri.createCriteria();
    		cri.andOrderIdEqualTo(orderId);
    		List<OrderItem> items = orderItemMapper.selectByExample(itemCri);
    		List<Map<String,String>> itemObj = new ArrayList<Map<String,String>>();
    		Map<String,String> temp = null;
    		int totalCost = 0;
    		for(OrderItem item:items){
    			temp = new HashMap<String, String>();
    			String orderTypeStr = sdf.format(new Date(item.getStartTime() * 1000L)).substring(0, 10) + " " + sdf.format(new Date(item.getStartTime() * 1000L)).substring(11);
				if(item.getEndTime()!=null){
					orderTypeStr += "~"
							+ sdf.format(new Date(item.getEndTime() * 1000L)).substring(11);
				}
				if(item.getCostPrice()!=null){
					totalCost+=item.getCostPrice();
				}
				temp.put("orderTime", orderTypeStr);
				temp.put("spaceName", item.getSpaceName());
				temp.put("spaceCode", item.getSpaceCode());
				itemObj.add(temp);
    		}
    		result.put("costPrice", ((new BigDecimal(totalCost).divide(new BigDecimal(100)))).toString());
    		result.put("item", itemObj);
    		if(order.getHandleStatus()!=null){
    			OrderHandleCriteria handleCriteria = new OrderHandleCriteria();
    			OrderHandleCriteria.Criteria handleCri = handleCriteria.createCriteria();
    			handleCri.andOrderIdEqualTo(order.getId());
    			handleCriteria.setOrderByClause("ct desc");
    			handleCriteria.setMysqlOffset(0);
    			handleCriteria.setMysqlLength(1);
    			List<OrderHandle> orderHandlers = handleMapper.selectByExample(handleCriteria);
    			if(CollectionUtils.isNotEmpty(orderHandlers)){
    				result.put("reason", orderHandlers.get(0).getComment());
    			}
    		}
    		List<Map<String,Object>> relItem = new ArrayList<Map<String,Object>>();
    		Map<String,Object> relTemp = null;
    		for(int i=1;i<orderIds.length;i++){
    			totalCost = 0;
    			relTemp = new HashMap<String, Object>();
    			String relId = orderIds[i];
    			Order relOrder = this.selectByPrimaryKey(Order.class, relId);
    			relTemp.put("id", relId);
    			String status_ = relOrder.getStatus();
        		if(status_.equals(Constants.OrderStatus.PAIED)){
        			status_ = "已支付";
    			}else if(status_.equals(Constants.OrderStatus.PLAYING)){
    				status_ = "已完成";
    			}else if(status_.equals(Constants.OrderStatus.VERIFY)){
    				status_ = "已确认";
    			}else if(status_.equals(Constants.OrderStatus.REFUNDING)){
    				status_ = "退款中";
    			}else if(status_.equals(Constants.OrderStatus.REFUNDED)){
    				status_ = "已退款";
    			}
        		Integer handleStatus_ = relOrder.getHandleStatus();
        		relTemp.put("handleStatus", handleStatus_);
        		relTemp.put("status", status_);
        		if(relOrder.getOrdersType()==Constants.OrderType.BOOKBALL){
        			relTemp.put("orderType", "约球订单");
        		}else if(relOrder.getOrdersType()==Constants.OrderType.STATIUM){
        			relTemp.put("orderType", "订场订单");
        		}else if(relOrder.getOrdersType()==Constants.OrderType.COACH){
        			relTemp.put("orderType", "教培练订单");
        		}
        		String couponId_ = relOrder.getCouponId();
        		if(StringUtils.isNotBlank(couponId_)){
        			CouponHistory history  = this.selectByPrimaryKey(CouponHistory.class, couponId_);
        			BigDecimal couponAmount = history.getAmount();
        			relTemp.put("amount", couponAmount.toString());
        		}
        		relTemp.put("fee", (new BigDecimal(relOrder.getFee()).divide(new BigDecimal(100))).toString());
        		relTemp.put("finalFee", (new BigDecimal(relOrder.getFinalFee()).divide(new BigDecimal(100))).toString());
        		relTemp.put("ct", DateUtil.formatDate(relOrder.getCt(), "yyyy-MM-dd HH:mm:ss"));
        		String customerId_ = relOrder.getCustomerId();
        		SsoUser user_ = this.selectByPrimaryKey(SsoUser.class, customerId_);
        		if(user_!=null){
        			relTemp.put("username", user_.getNickName());
        			relTemp.put("userphone", user_.getPhone());
        		}
        		
        		String statiumId_ = relOrder.getStatiumId();
        		if(relOrder.getOrdersType()==Constants.OrderType.BOOKBALL||relOrder.getOrdersType()==Constants.OrderType.STATIUM){
        			StatiumDetail detail = this.selectByPrimaryKey(StatiumDetail.class, statiumId_);
        			if(detail!=null){
        				relTemp.put("tel",detail.getTel());
        			}
        		}else if(relOrder.getOrdersType()==Constants.OrderType.COACH){
        			Coacher coacher = this.selectByPrimaryKey(Coacher.class, statiumId_);
        			if(coacher!=null){
        				relTemp.put("tel",coacher.getPhone());
        			}
        		}
        		
        		Integer payType_ = relOrder.getPayType();
        		if(payType_!=null){
        			String payTypeStr = "";
        			if(payType_==1){
        				payTypeStr = "支付宝";
        			}else if(payType_==2){
        				payTypeStr = "微信";
        			}else if(payType_==3){
        				payTypeStr = "微信公众平台";
        			}else if(payType_==4){
        				payTypeStr = "球友圈";
        			}else if(payType_==5){
        				payTypeStr = "账户";
        			}else if(payType_==9){
        				payTypeStr = "招行";
        			}
        			relTemp.put("payType", payTypeStr);
        		}
        		
    			
    			OrderItemCriteria itemCri_ = new OrderItemCriteria();
        		OrderItemCriteria.Criteria cri_ = itemCri_.createCriteria();
        		cri_.andOrderIdEqualTo(relId);
        		List<OrderItem> items_ = orderItemMapper.selectByExample(itemCri_);
        		List<Map<String,String>> itemObj_ = new ArrayList<Map<String,String>>();
        		for(OrderItem item:items_){
        			temp = new HashMap<String, String>();
        			String orderTypeStr = sdf.format(new Date(item.getStartTime() * 1000L)).substring(0, 10) + " " + sdf.format(new Date(item.getStartTime() * 1000L)).substring(11);
    				if(item.getEndTime()!=null){
    					orderTypeStr += "~"
    							+ sdf.format(new Date(item.getEndTime() * 1000L)).substring(11);
    				}
    				temp.put("orderTime", orderTypeStr);
    				if(relOrder.getOrdersType()==Constants.OrderType.BOOKBALL||relOrder.getOrdersType()==Constants.OrderType.STATIUM){
    					temp.put("spaceName", item.getSpaceName());
        				temp.put("spaceCode", item.getSpaceCode());
            		}else if(relOrder.getOrdersType()==Constants.OrderType.COACH){
            			Coacher coacher = this.selectByPrimaryKey(Coacher.class, statiumId_);
            			temp.put("spaceName", coacher.getName());
            		}
    				if(item.getCostPrice()!=null){
    					totalCost+=item.getCostPrice();
    				}
    				itemObj_.add(temp);
        		}
        		relTemp.put("costPrice", ((new BigDecimal(totalCost).divide(new BigDecimal(100)))).toString());
        		relTemp.put("item", itemObj_);
        		if(relOrder.getHandleStatus()!=null){
        			OrderHandleCriteria handleCriteria = new OrderHandleCriteria();
        			OrderHandleCriteria.Criteria handleCri = handleCriteria.createCriteria();
        			handleCri.andOrderIdEqualTo(relOrder.getId());
        			handleCriteria.setOrderByClause("ct desc");
        			handleCriteria.setMysqlOffset(0);
        			handleCriteria.setMysqlLength(1);
        			List<OrderHandle> orderHandlers = handleMapper.selectByExample(handleCriteria);
        			if(CollectionUtils.isNotEmpty(orderHandlers)){
        				relTemp.put("reason", orderHandlers.get(0).getComment());
        			}
        		}
    			relItem.add(relTemp);
    		}
    		result.put("relItem", relItem);
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	/**
	 * <退款至账户><功能具体实现>
	 * @param id
	 * @return
	 * @author yankefei
	 * @date 2015年12月4日 下午4:11:53
	 */
	@Transactional(readOnly = false)
	public Map<String, Object> confirmRefundNew(String id) {
		Map<String, Object> result = new HashMap<>();
		result.put(Constants.Result.RESULT, false);
		boolean b = false;
		Order order = null;
		String comment = "";
		// 批次号
		String batch_no = "";
		try {
			order = this.getOrder(id);
			if (order == null) {
				comment = "退款至账户失败，订单不存在！";
			} else {
				if (!order.getStatus().equals(Constants.OrderStatus.REFUNDING)) {
					comment = "退款至账户失败，订单不是退款中的订单！";
				} else {
					if(order.getFinalFee()==0){
						
					}else{
						UserAccount account = this.selectByPrimaryKey(UserAccount.class, order.getCustomerId());
						SsoUserBonusAccount bonusAccount = this.selectByPrimaryKey(SsoUserBonusAccount.class, order.getCustomerId());

						if(account==null){
							account = new UserAccount();
							account.setUserId(order.getCustomerId());
							account.setBalance(0);
							account.setFrozen(0);	//冻结金额为不能消费且不能体现
							account.setIsfreeze(0);	//0为正常账户，1为冻结账户
							account.setCt(new Date());
							account.setEt(new Date());
							account.setCb(SessionUtil.currentUserId());
							account.setEb(SessionUtil.currentUserId());
							this.insertSelective(account, order.getCustomerId());
						}
                        Integer bonusUse = 0;
						Integer bonusBlance = 0;
						if(bonusAccount != null){
							//从支付记录查看是否使用了奖金支付
							SsoUserBonusAccountLogCriteria bonusAccountLogC = new SsoUserBonusAccountLogCriteria();
							SsoUserBonusAccountLogCriteria.Criteria bonusAccountLogCri = bonusAccountLogC.createCriteria();
							bonusAccountLogCri.andOrderIdEqualTo(order.getId());
							bonusAccountLogCri.andUserIdEqualTo(order.getCustomerId());
							if(ssoUserBonusAccountLogMapper.countByExample(bonusAccountLogC) >0 ){
								bonusUse = ssoUserBonusAccountLogMapper.selectByExample(bonusAccountLogC).get(0).getAmount();
								bonusBlance = bonusAccount.getBalance();
								bonusAccount.setBalance(bonusBlance - bonusUse);
								account.setEt(new Date());
								account.setEb(SessionUtil.currentUserId());
								this.updateByPrimaryKey(bonusAccount, order.getCustomerId());
								//退款记入账户日志
								SsoUserBonusAccountLog ualB = new SsoUserBonusAccountLog();
								ualB.setId(UUID.get());
								ualB.setAmount(-bonusUse);
								ualB.setBalance(bonusAccount.getBalance());
								ualB.setDescription("退款至奖金账户");
								ualB.setOrderId(order.getId());
								ualB.setStatus(1);
								ualB.setType(Constants.AccountUseType.REFOUND);
								ualB.setUserId(order.getCustomerId());
								ualB.setCt(new Date());
								ssoUserBonusAccountLogMapper.insert(ualB);
							}
						}

						Integer balance = account.getBalance();
						//退的金额
						Integer balance_new = balance+order.getFinalFee();
						//退到账户的金额= 退款总额 - 奖金账户支付总额
						account.setBalance(balance_new + bonusUse);
						account.setEt(new Date());
						account.setEb(SessionUtil.currentUserId());
						this.updateByPrimaryKey(account, order.getCustomerId());
						
						//退款记入账户日志
						UserAccountLog ual = new UserAccountLog();
						ual.setId(UUID.get());
						ual.setAmount(order.getFinalFee());
						ual.setBalance(balance_new+bonusBlance);
						ual.setDescription("退款至账户");
						ual.setOrderId(order.getId());
						ual.setStatus(1);
						ual.setType(Constants.AccountUseType.REFOUND);
						ual.setUserId(order.getCustomerId());
						ual.setCt(new Date());
						userAccountLogMapper.insert(ual);
					}
					b = true;
					result.put(Constants.Result.RESULT, true);
				}
			}
		} catch (Exception e) {
			comment = "退款至账户失败，操作发生异常！";
			logger.error("退款至账户失败，操作发生异常！订单ID：" + order.getId(), e);
		} finally {
			result.put(Constants.Result.REASON, comment);
			if (order != null) {
				if (b) {
					returnOrderLog(SessionUtil.currentUserId(), order, "退款至账户成功！", false, batch_no, Constants.OrderAction.REFUND, Constants.PayStatus.REFUND_SUCCESS);
					// 退款成功之后的动作
					SsoUser user = ssoUserMapper.selectByPrimaryKey(order.getCustomerId());
					String phone = user.getPhone();

					try {
						order.setStatus(Constants.OrderStatus.REFUNDED);
						order.setEt(new Date());
						this.updateByPrimaryKeySelective(order, order.getId());
						// orderMapper.updateByPrimaryKeySelective(order);

						// 给用户发短息
						StringBuffer msg = new StringBuffer();
						msg.append("尊敬的用户，您的订单号：");
						msg.append(order.getId());
						msg.append(" 已成功退款至球友圈账户，请登录App查看！");
						messageUtil.sendSms(phone, msg.toString());
					} catch (Exception e) {
						logger.error("退款至账户成功后修改订单状态或者发送短信失败！订单ID：" + order.getId(), e);
					}
				} else {
					returnOrderLog(SessionUtil.currentUserId(), order, comment, false, batch_no, Constants.OrderAction.REFUND, Constants.PayStatus.REFUND_FAIL);
				}
			}
		}
		return result;
	}
	
/**
 * <退款至账户><功能具体实现>
 * @param id
 * @return
 * @author yankefei
 * @date 2015年12月4日 下午4:11:53
 */
@Transactional(readOnly = false)
public Map<String, Object> confirmRefundCompany(String id) {
	Map<String, Object> result = new HashMap<>();
	result.put(Constants.Result.RESULT, false);
	Order order = null;
	String comment = "";
	boolean b = false;
	try {
		order = this.getOrder(id);
		if (order == null) {
			comment = "运动基金退款失败，订单不存在！";
		} else {
			if (!order.getStatus().equals(Constants.OrderStatus.REFUNDING)) {
				comment = "运动基金退款失败，订单不是退款中的订单！";
			} else {
				if(order.getFinalFee()==0){
					
				}else{
					StaffAccount account = this.selectByPrimaryKey(StaffAccount.class, order.getCustomerId());
					Date begin = account.getBegin();
					Date ct = order.getCt();
					//判断是否需要修改公司员工运动基金已消费额度
					if(ct.compareTo(begin)>=0){
						Integer useAmout = account.getUseAmout();
						Integer useAmout_new = useAmout-order.getFinalFee();
						account.setUseAmout(useAmout_new);
						account.setEt(new Date());
						account.setEb(SessionUtil.currentUserId());
						this.updateByPrimaryKey(account, order.getCustomerId());
						
						StaffAccountLog accountLog = new StaffAccountLog();
						accountLog.setCt(new Date());
						accountLog.setFee(account.getUseAmout());
						accountLog.setId(UUID.get());
						accountLog.setOrderId(order.getId());
						accountLog.setTransType(1);
						accountLog.setUserId(SessionUtil.currentUserId());
						staffAccountLogMapper.insert(accountLog);
					}else{
						
					}
					//退款公司账户
					CompanyAccount companyAccount = this.selectByPrimaryKey(CompanyAccount.class, account.getCompanyAccountId());
					int balance = companyAccount.getBalance();
					companyAccount.setBalance(balance+order.getFinalFee());
					companyAccount.setEb(SessionUtil.currentUserId());
					companyAccount.setEt(new Date());
					this.updateByPrimaryKey(companyAccount, companyAccount.getCompanyId());
					
					CompanyAccountLog companyAccountLog = new CompanyAccountLog();
					companyAccountLog.setId(UUID.get());
					companyAccountLog.setAmount(order.getFinalFee());
					companyAccountLog.setBalance(order.getFinalFee()+balance);
					companyAccountLog.setCompanyId(companyAccount.getCompanyId());
					companyAccountLog.setCt(new Date());
					companyAccountLog.setTransType(8);
					companyAccountLogMapper.insert(companyAccountLog);
				}
				result.put(Constants.Result.RESULT, true);
			}
		}
		b = true;
	} catch (Exception e) {
		comment = "运动基金退款失败，操作发生异常！";
		logger.error("运动基金退款失败，操作发生异常！订单ID：" + order.getId(), e);
	} finally {
		comment = "退款至运动基金成功";
		result.put(Constants.Result.REASON, comment);
		if (b) {
			// 退款成功之后的动作
			SsoUser user = ssoUserMapper.selectByPrimaryKey(order.getCustomerId());
			String phone = user.getPhone();

			try {
				order.setStatus(Constants.OrderStatus.REFUNDED);
				order.setEt(new Date());
				this.updateByPrimaryKeySelective(order, order.getId());
				OrderLog log = new OrderLog();
				log.setId(UUID.get());
				log.setCt(new Date());
				log.setOrderId(order.getId());
				log.setUserId(user.getId());
				log.setAction(Constants.OrderAction.REFUND);
				log.setComment(comment);
				orderLogMapper.insert(log);
				// orderMapper.updateByPrimaryKeySelective(order);

				/*// 给用户发短息
				StringBuffer msg = new StringBuffer();
				msg.append("尊敬的用户，您的订单号：");
				msg.append(order.getId());
				msg.append(" 已成功退款至球友圈账户，请登录App查看！");
				messageUtil.sendSms(phone, msg.toString());*/
			} catch (Exception e) {
				logger.error("退款至账户成功后修改订单状态或者发送短信失败！订单ID：" + order.getId(), e);
				OrderLog log = new OrderLog();
				log.setId(UUID.get());
				log.setCt(new Date());
				log.setOrderId(order.getId());
				log.setUserId(user.getId());
				log.setAction(Constants.OrderAction.REFUND);
				log.setComment("退款失败");
				orderLogMapper.insert(log);
			}
		} else {
			
		}
	}
	return result;
}

@Transactional(readOnly = false)
public Map<String, Object> confirmRefundCmb(String id) {
	Map<String, Object> result = new HashMap<String, Object>();
	result.put(Constants.Result.RESULT, true);
	String outTradeNo = "";
	Settle settle = null;
	Order order = null;
    try {
    	settle=new Settle();
    	//settle.testit();
    	//settle.SetOptions("payment.ebank.cmbchina.com");
    	settle.SetOptions("payment.ebank.cmbchina.com");
    	int iRet = settle.LoginC("0021","006288","847135");
    	if(iRet==0){
    		logger.debug("招行退款登录成功");
    	}else{
    		result.put(Constants.Result.RESULT, false);
    		result.put(Constants.Result.REASON, settle.GetLastErr(iRet));
    	}
    	order = this.selectByPrimaryKey(Order.class, id);
    	if(order==null){
    		result.put(Constants.Result.RESULT, false);
    		result.put(Constants.Result.REASON, "订单号不存在！");
    		return result;
    	}
    	PayLogCriteria criteria = new PayLogCriteria();
    	PayLogCriteria.Criteria cri = criteria.createCriteria();
    	cri.andTradeNoEqualTo(order.getTradeNo());
    	List<PayLog> payLogs = payLogMapper.selectByExample(criteria);
    	PayLog log = payLogs.get(0);
    	outTradeNo = log.getOutTradeNo();
    	int fee = log.getFinalFee();
    	BigDecimal feeB = new BigDecimal(fee);
    	BigDecimal base = new BigDecimal(100);
    	BigDecimal feeB_ = feeB.divide(base);
    	double fee_ = feeB_.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue(); 
    	Date ct = log.getCreateTime();
    	iRet = settle.RefundOrder(DateUtil.formatDate(ct, "yyyyMMdd"),order.getTradeNo(),String.valueOf(fee_),"退款","9a86c6d5649CD5ab");
    	
    	if(iRet==0){
    		if (order != null) {
				returnOrderLog(SessionUtil.currentUserId(), order, "退款成功！", true, outTradeNo, Constants.OrderAction.REFUND, Constants.PayStatus.REFUND_SUCCESS);
				// 退款成功之后的动作
				SsoUser user = ssoUserMapper.selectByPrimaryKey(order.getCustomerId());
				String phone = user.getPhone();
				try {
					order.setStatus(Constants.OrderStatus.REFUNDED);
					order.setEt(new Date());
					this.updateByPrimaryKeySelective(order, order.getId());
					// orderMapper.updateByPrimaryKeySelective(order);
					// 给用户发短息
					StringBuffer msg = new StringBuffer();
					msg.append("尊敬的用户，您的订单号：");
					msg.append(order.getId());
					msg.append(" 已成功退款，请注意查收，最晚三个工作日到账！");
					messageUtil.sendSms(phone, msg.toString());
				} catch (Exception e) {
					logger.error("退款时，退款成功后修改订单状态或者发送短信失败！订单ID：" + order.getId(), e);
				}
			}
    	}else{
    		String comment = settle.GetLastErr(iRet);
    		returnOrderLog(SessionUtil.currentUserId(), order, comment, false, outTradeNo, Constants.OrderAction.REFUND, Constants.PayStatus.REFUND_FAIL);
    		result.put(Constants.Result.RESULT, false);
    		result.put(Constants.Result.REASON, comment);
    	}
	} catch (Exception e) {
		logger.error(e.getMessage(),e);
	}finally{
		if(settle!=null){
			settle.Logout();
		}
	}
	return result;
}
@Transactional(readOnly = false)
public Map<String, Object> confirmRefundJd(String id) {
	Map<String, Object> result = new HashMap<String, Object>();
	result.put(Constants.Result.RESULT, true);
	String outTradeNo = "";
	Settle settle = null;
	Order order = null;
	try {
		order = this.selectByPrimaryKey(Order.class, id);
		if(order==null){
			result.put(Constants.Result.RESULT, false);
			result.put(Constants.Result.REASON, "订单号不存在！");
			return result;
		}
		PayLogCriteria criteria = new PayLogCriteria();
		PayLogCriteria.Criteria cri = criteria.createCriteria();
		cri.andTradeNoEqualTo(order.getTradeNo());
		List<PayLog> payLogs = payLogMapper.selectByExample(criteria);
		PayLog log = payLogs.get(0);
		outTradeNo = log.getOutTradeNo();
		int fee = log.getFinalFee();
		
		TradeRefundReqDto tradeRefundReqDto = new TradeRefundReqDto();
		tradeRefundReqDto.setVersion("V2.0");
		tradeRefundReqDto.setMerchant("110230776002");
		tradeRefundReqDto.setTradeNum(UUID.get());
		tradeRefundReqDto.setoTradeNum(outTradeNo);
		tradeRefundReqDto.setAmount(fee);
		tradeRefundReqDto.setCurrency("CNY");
		tradeRefundReqDto.setTradeTime(DateUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
		tradeRefundReqDto.setNotifyUrl(null);
		String priKey = configs.get("wepay.merchant.rsaPrivateKey");
        logger.debug("priKey={}",priKey);
		String desKey = configs.get("wepay.merchant.desKey");
		logger.debug("desKey={}",desKey);
		String pubKey = configs.get("wepay.jd.rsaPublicKey");
		logger.debug("pubKey={}",pubKey);
		
		String tradeXml = JdPayUtil.genReqXml(tradeRefundReqDto, priKey, desKey);
		logger.debug("tradeXml:{}",tradeXml);
		String refundUrl = configs.get("jdrefundurl");

		String resultJsonData = HttpClientUtil.sendRequest(refundUrl, tradeXml, "application/xml");

		logger.debug("resultJsonData:{}",resultJsonData);

		RefundResponse refundResponse = JdPayUtil.parseResp(pubKey, desKey, resultJsonData, RefundResponse.class);
		logger.debug("refundResponse:,{}",refundResponse);
		
		if("1".equals(refundResponse.getStatus())){
			if (order != null) {
				returnOrderLog(SessionUtil.currentUserId(), order, "退款成功！", true, outTradeNo, Constants.OrderAction.REFUND, Constants.PayStatus.REFUND_SUCCESS);
				// 退款成功之后的动作
				SsoUser user = ssoUserMapper.selectByPrimaryKey(order.getCustomerId());
				String phone = user.getPhone();
				try {
					order.setStatus(Constants.OrderStatus.REFUNDED);
					order.setEt(new Date());
					this.updateByPrimaryKeySelective(order, order.getId());
					// orderMapper.updateByPrimaryKeySelective(order);
					// 给用户发短息
					StringBuffer msg = new StringBuffer();
					msg.append("尊敬的用户，您的订单号：");
					msg.append(order.getId());
					msg.append(" 已成功退款，请注意查收，最晚三个工作日到账！");
					messageUtil.sendSms(phone, msg.toString());
				} catch (Exception e) {
					logger.error("退款时，退款成功后修改订单状态或者发送短信失败！订单ID：" + order.getId(), e);
				}
			}
		}else{
			returnOrderLog(SessionUtil.currentUserId(), order, refundResponse.getResult().getDesc(), false, outTradeNo, Constants.OrderAction.REFUND, Constants.PayStatus.REFUND_FAIL);
			result.put(Constants.Result.RESULT, false);
			result.put(Constants.Result.REASON, refundResponse.getResult().getDesc());
		}
	} catch (Exception e) {
		logger.error(e.getMessage(),e);
	}finally{
		if(settle!=null){
			settle.Logout();
		}
	}
	return result;
}
public static void main(String[] args) {
	Map<String, Object> result = new HashMap<String, Object>();
	result.put(Constants.Result.RESULT, true);
	Settle settle = null;
    try {
    	settle=new Settle();
    	settle.SetOptions("payment.ebank.cmbchina.com");
    	int iRet = settle.LoginC("0021","006288","847135");
    	if(iRet==0){
    		logger.debug("招行登录成功");
    	}else{
    		result.put(Constants.Result.RESULT, false);
    		result.put(Constants.Result.REASON, settle.GetLastErr(iRet));
    	}
    	StringBuffer ret = new StringBuffer();
    	//iRet = settle.QuerySingleOrder("20160705", "3000000217", ret);
    	iRet = settle.QueryRefundByDate("20160705", "20160705", ret);
    	if(iRet==0){
    		System.out.println(ret.toString());
    	}else{
    		result.put(Constants.Result.RESULT, false);
    		result.put(Constants.Result.REASON, settle.GetLastErr(iRet));
    	}
	} catch (Exception e) {
		logger.error(e.getMessage(),e);
	}finally{
		if(settle!=null){
			settle.Logout();
		}
	}
    System.out.println(result.toString());
}

}
