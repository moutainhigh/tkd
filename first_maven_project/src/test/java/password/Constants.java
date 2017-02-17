package password;

import java.util.Arrays;
import java.util.List;


public class Constants {
	
    /**
     * ����Ĭ������
     */
    public static final String DEFAULT_PASSWORD = "qiuyouzone";

    /**
     * ��Ϣ��������Ϣ
     */
    public static final String EMSG_SERVER = "emsg_server";
	
	/**
	 * ͬ��solr��Ϣ��all��ʾͬ����������
	 */
	public static String ALL = "all";
	
	public static String SALT = "b15382cf42802197";
	
	public static String  MINPRICE = "_min_price";
	
	public static String  COSTPRICE = "_cost_price";
	
	public static String MAXPRICE = "_max_price";

	// ֱϽ��
	public static List<String> MUNICIPALITY_LIST = Arrays.asList("������", "�����", "�Ϻ���", "������");
	
	public  interface UserStatus {
//		String ENABLE = "user_enable";
//		String DISABLE = "user_disable";
		String ACTIVE = "active";
		String INACTIVE = "inactive";
		String DELETE = "delete";
	}
	
	public interface UserRole {
		String ADMIN = "1001";
		String OPERATOR = "1002";
		String USER = "1003";
		String MGR = "1004";
	}
	
//	public interface OrderStatus {
//		String BOOKED = "BOOKED";
//		String CANCELED = "CANCELED";
//		String FINISHED = "FINISHED";
//	}
	
	public interface AccountType {
		String TEMPORARY = "0";
		String FORMAL = "1";
	}
	
	/**
	 * ��Ϣ����
	 */
	public interface MessageType {
		String LOC = "LOC";
		String SMS = "SMS";
		String EMAIL = "EMAIL";
		String PUSH = "PUSH";
	}
	
	/**
	 * ����״̬
	 * @author LSH
	 *
	 */
	public interface CommentStatus {
		
		//����
		Integer NORMAL = 1; 
		
		//������
		Integer ABNORMAL = 0;
	}
	
	/**
     * <�û��Ļ�Ա�����׼�¼״̬>
     * @author zzq
     */
    public interface CrmUserCardStatus{
    	//����
    	Integer BUY = 0;
    	//����
    	Integer CONSUME = 1;
    	//ת��
    	Integer CHANGE = 2;
    	//���ֱ��
    	Integer CHANGETYPE = 3;
    	//����
    	Integer RENEW = 4;
    	//�˿�
    	Integer REFUEND = 5;
    }
	
	/**
	 * ����״̬
	 * @author LSH
	 *
	 */
	public interface OrderStatus{
		/**
         * �ֵ���
         */
        String DIC_CODE = "ORDER_STAT";

        /**
         * ������
         */
        String NEW = "ORDER_NEW";

        /**
         * ���׳ɹ�
         */
        String PLAYING = "ORDER_PLAYING";

        /**
         * ���׹ر�
         */
        String CANCELED = "ORDER_CANCELED";

        /**
         * �˿��еĶ���
         */
        String REFUNDING = "ORDER_REFUNDING";

        /**
         * ��֧������
         */
        String PAIED = "ORDER_PAIED";

        /**
         * �˿��
         */
        String REFUNDED = "ORDER_REFUNDED";

        /**
         * ��ȷ�ϵĶ���
         */
        String VERIFY = "ORDER_VERIFY";
        
		String BILLED = "ORDER_BILLED";  //���������
	}
	
    interface Result {
        String RESULT = "result";

        String REASON = "reason";

        String LIST = "list";
        
        String DATA = "data";
    }
    

    /**
     * �Ż�ȯ
     * 
     * @author lsh
     *
     */
    public interface couponInfo {
        /**
         * 1���������
         */
        Integer RADOM = 1;

        /**
         * 2�������
         */
        Integer QUOTA = 2;

        /**
         * �Ż�ȯ����������
         */
        String NUMBER = "number";

        /**
         * ʱ������:����ȡ��ʼ����ʱ��
         */
        Integer UNFIXTIME = 1;

        /**
         * ʱ������:�̶�ʱ�䷶Χ
         */
        Integer FIXTIME = 2;

        /**
         * ��ǰ����֪ͨ�ͻ��Ż�ȯ������(ע:value=2����ǰ3��֪ͨ)
         */
        Integer NOTICE = 2;

        /**
         * �Ż�ȯ�ܽ��
         */
        String TOTALAMOUNT = "totalAmount";

        /**
         * �Ż�ȯ����ʱ��
         */
        String ENDTIME = "endTime";

        /**
         * redis����
         */
        String REDIS_KEY = "coupons";

        /**
         * redis����Ż�ȯ��key
         */
        String COUPON_KEY = "coupon_";

        /**
         * redis��ŷ�����Ż�ȯ��key
         */
        String GIVE_KEY = "give_";

        /**
         * redis����µ��ɹ����Ż�ȯ��key
         */
        String ORDER_KEY = "order_";

        /**
         * ʱ������
         */
        String TIMETYPE = "timeType";

        /**
         * �Ż�ȯ��Чʱ��
         */
        String DURATION = "duration";

        /**
         * �Ż�ȯ��Чʱ��
         */
        String BALLTYPE = "ballType";

        // �׵�����
        Integer FIRST = 0;

        // ֧���ɹ�����ȯ
        Integer PAY_SHARE = 1;

        /**
         * �Ż�ȯ���
         */
        String AMOUNT = "amount";


        public interface delFlag {
        	/**
             * ������־
             */
        	Integer NORMAL = 1;

            /**
             * ɾ����־
             */
        	Integer DELETE = 0;
        }
        
        /**
         * �Ż�ȯ״̬
         *
         */
        public interface status {
            // δ��ʼ
            Integer NOT_START = 0;

            // ��ʼ
            Integer START = 1;

            // ͣ��
            Integer STOP = 2;

            // ����
            Integer END = 3;
        }

        /**
         * �Ż�ȯ��ȡ����
         * 
         * @author lsh
         *
         */
        public interface givingType {
            // �׵��Ż�
            Integer FIRST = 0;

            // ��������
            Integer SHARE = 1;

            // �µ��ɹ�����
            Integer ORDERT = 2;
        }

        /**
         * �Ż�ȯʹ��״̬��0������ȡ 1����ʹ�� 2���ѹ��� 3 : ������ 4:����ȡ����
         * 
         * @author lsh
         *
         */
        public interface isUse {
            // ����ȡ
            Integer ISRECIVING = 0;

            // ��ʹ��
            Integer ISUSE = 1;

            // �ѹ���
            Integer ISOLD = 2;

            // ������
            Integer ISSHARE = 3;

            // ����ȡ����
            Integer ISRECIVINGGIVE = 4;
        }
    }
    
    public interface msgType{
    	String NEW = "new";
    	String CANCEL = "cancel";
    	String PAY = "pay";
    	String BILL = "bill";
    	String UPDATE = "update";
    }
	
    public interface event{
    	
    	public interface type{
    		Integer PERSONAL = 0; //����
        	Integer OGNATION = 1; //����
    	}
    	public interface state{
    		Integer Registration =0; //������ 
    		Integer FULL = 1; //��Ա
    		Integer STOP = 2;//������ֹ
    		Integer END = 3; //���½���
    	}
    }
    
    /**
     * ��������
     * 
     * @author aklong
     *
     */
    public interface OrderType {
        // ����Ԥ��
        Integer STATIUM = 0;

        // ����/����
        Integer COACH = 1;

        // �
        Integer ACTIVITY = 2;

        // Լ��
        Integer APPOINTMENT = 3;
        
        //����
        Integer EVENT = 4;

        // Լ����
        Integer BOOK_ENTER = 5;
    }
    
    /**
     * 
     * 
     * <emsg״̬��><���ܾ���ʵ��>
     * 
     * @author��cyy
     * @create��2015��9��24�� ����2:44:22
     * @version
     *
     */
    public interface Emsg {

        Integer ADD_FRIEND = 100; // ��Ӻ���

        Integer REMOVE_FRIEND = 101; // ɾ������

        Integer ALLOW_FRIEND = 102; // ͬ�����

        Integer REFUSE_FRIEND = 103; // �ܾ�����

        Integer CREATE_GROUP = 104; // ����Ⱥ

        Integer DISBAND_MEMBER = 105; // ��ɢȺ

        Integer ADD_GROUP_MEMBER = 106; // ���Ⱥ��Ա

        Integer REMOVE_GROUP_MEMBER = 107; // ɾ��Ⱥ��Ա

        Integer ACTIVE_ADD_GROUP_MEMBER = 108; // ��������Ⱥ
        
        Integer CORPS_INVITED  = 109; // ս��
        
        Integer INVITE_GROUP_MEMBER = 110; // Ⱥ�������Ա
        
        Integer REFUSE_GROUP_MEMBER = 111; // �ܾ�Ⱥ��Ⱥ
        
        Integer ENTERPRISE_GROUP_NOTIFY = 112; // ��ҵ����֪ͨ
        
        Integer QUIT_GROUP_MEMBER = 113; // ��Ⱥ
        
        String MESSAGE_TYPE = "messageType";

        String ADD_FRIEND_MESSAGE = "�������Ϊ����";
    }
    
    public interface pushEmsg {

        Integer PUSH_SUBJECT = 104; // ����ר��
        
        Integer PUSH_ACTIVITY = 105; // ���ͻ
        
        Integer PUSH_EVENT = 106; // ��������
        
        Integer PUSH_EVENT_TEXT = 107; // ���������ı�
        
        String PUSH_FROM = "boss@";// ���ͷ�(from)

    }
    
    public interface pushType{
		Integer PUSH_SUBJECT = 0; // ����ר��
        
        Integer PUSH_ACTIVITY = 1; // ���ͻ
        
        Integer PUSH_EVENT = 2; // �������� 
        
        Integer PUSH_TEXT = 3; // ����
        
        Integer PUSH_EVENT_TEXT = 4;// �����ı�֪ͨ
	}
    
    public interface isPush{
		Integer PUSH_TYPE_UNLIMIT = 0; // ��ʱ����
        
        Integer PUSH_RYPE_TIME = 1; // ��ʱ����
        
        Integer PUSH_RYPE_NO = 2; // ������
        
        Integer PUSH_RYPE_PTIME  = 3; // ���͹���ʱ���� 
        
        Integer PUSH_RYPE_PUNLIMIT = 4; // ���͹���ʱ����  
	}
    
    public interface pushMsgType{
		String PUSH_TYPE_ACTIVITY = "activity"; // �
        
		String PUSH_TYPE_EVENT = "event"; // ����
		
		String PUSH_TYPE_SOURCE = "source"; // ר��
		
		String PUSH_TYPE_TEXT = "text"; // �ı�
		
		String PUSH_TYPE_EVENT_TEXT = "eventMsg";// �����ı�֪ͨ
	}
    
    public enum Integral {
        clock(0, "ǩ��",10), // ǩ��
        statium_finish(1, "��������",100), // �����������
        statium_comment(2, "��������",50), // ��ɺ󷢲�����
        activity_finish(3, "�����",50), // �����������
        coacher_finish(4, "���㶩��",50),  // �������������
        coacher_comment(5, "��������",50), // ������������������
        share(6, "����",30), // ����
        book_ooperation(7, "����Լ��",100), // ���Լ�򣨺�����
        book_not_ooperation(8, "����Լ��",10), // ���Լ��δ������
        account(9, "�˺Ű�",50), // ������������˺�
        basic_info(10, "���ƻ�����Ϣ",60), // ���ƻ�����Ϣ
        bug(11, "��Ʒ�һ�",0), // ��Ʒ�һ�
        user_register(12, "ע�ἴ�ͻ��� ",300), // ע�ἴ�ͻ���
        create_corps(13, "����ս��",-100), // ����ս��
        ;

        public Integer type;

        public String msg;
        
        public Integer value;

        private Integral(Integer type) {
            this.type = type;
        }

        private Integral(Integer type, String msg,Integer value) {
            this.type = type;
            this.msg = msg;
            this.value = value;
        }
        
        public static boolean exist(Integer type){
            for (Integral i : Integral.values()) {
                if (i.type == type) {
                    return true;
                }
            }
            return false;
        }
        
        public static Integral getIntegral(Integer type){
            for (Integral i : Integral.values()) {
                if (i.type == type) {
                    return i;
                }
            }
            return null;
        }
    }
    
    /**
     * �Ż�ȯ����
     * @author byp
     *
     */
    public enum CouponType {
        
    	common("0","ͨ��"),
    	badminton("1","��ë��"),
    	tennis("2","����"),
    	basketball("3","����"),
    	table_Tennis("4","ƹ����"),
    	golf("5","�߶���"),
    	football("6","����"),
    	billiard("7","̨��"),
    	bowling("8","������"),
    	golf_book("9","�߶����³�"),
    	;
    	
        public String type;

        public String value;

        private CouponType(String type,String value) {
            this.type = type;
            this.value = value;
        }
        
        public String getType() {
			return type;
		}

		public String getValue() {
			return value;
		}

		public static boolean exist(String type){
            for (CouponType i : CouponType.values()) {
                if (type.equals(i.type)) {
                    return true;
                }
            }
            return false;
        }
        
        public static CouponType getCouponType(String type){
            for (CouponType i : CouponType.values()) {
                if (type.equals(i.type)) {
                    return i;
                }
            }
            return null;
        }
    }
    
    /**
     * �Ż�ȯһ������
     * @author byp
     *
     */
    public enum CouponTotalType {
        
    	statium("0","����"),
    	coach("1","������"),
    	activity("2","�"),
    	bookball("3","Լ��"),
    	event("4","����"),
    	;
    	
        public String type;

        public String value;

        private CouponTotalType(String type,String value) {
            this.type = type;
            this.value = value;
        }
        
        public String getType() {
			return type;
		}

		public String getValue() {
			return value;
		}

		public static boolean exist(String type){
            for (CouponTotalType i : CouponTotalType.values()) {
                if (type.equals(i.type)) {
                    return true;
                }
            }
            return false;
        }
        
        public static CouponTotalType getCouponTotalType(String type){
            for (CouponTotalType i : CouponTotalType.values()) {
                if (type.equals(i.type)) {
                    return i;
                }
            }
            return null;
        }
    }

    /**
     * ����ҵ����Ϊ
     *
     * @author wang.haibin
     *
     */
    public interface OrderAction {
        /**
         * �ֵ���
         */
        String DIC_CODE = "ORDER_ACTION";

        /**
         * Ԥ��
         */
        String CREATE = "ORDER_ACTION_CREATE";

        /**
         * ����
         */
        String PLAY = "ORDER_ACTION_PLAY";

        /**
         * ȡ��
         */
        String CANCEL = "ORDER_ACTION_CANCEL";

        /**
         * ��ʱ
         */

        String TIMEOUT = "ORDER_ACTION_TIMEOUT";

        /**
         * ����
         */
        String BILL = "ORDER_ACTION_BILL";

        /**
         * ɾ��
         */
        String DELETE = "ORDER_ACTION_DELETE";

        /**
         * ֧���ɹ�
         */
        String PAY = "ORDER_ACTION_PAY";

        /**
         * ֧���ɹ�
         */
        String PRE_PAY = "ORDER_ACTION_PRE_PAY";

        /**
         * �˿��еĶ���
         */
        String REFUNDING = "ORDER_ACTION_REFUNDING";

        /**
         * ���˿��
         */
        String REFUNDED = "ORDER_REFUNDED";

        /**
         * �˿�
         */
        String REFUND = "ORDER_ACTION_REFUND";

        /**
         * ��ȷ�ϵĶ���
         */
        String VERIFY = "ORDER_VERIFY";

        /**
         * �����˿�
         */
        String APPLY_REFUND = "APPLY_ACTION_REFUND";
    }

    public interface QRCode {
        /**
         * ��
         */
        int HEIGHT = 300;
        /**
         * ��
         */
        int WIDTH = 300;
    }
}
