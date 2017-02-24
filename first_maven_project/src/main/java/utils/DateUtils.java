package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;


public class DateUtils {

    private DateUtils() {
    }

    private static final String ISODATE_FORMAT = "yyyy-MM-dd";

    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String DATETIME_MIL_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public static String nowDateString() {
        return DateTime.now().toString(ISODATE_FORMAT);
    }

    public static String nowDateTimeString() {
        return DateTime.now().toString(DATETIME_FORMAT);
    }

    public static String nowDateTimeMilString() {
        return DateTime.now().toString(DATETIME_MIL_FORMAT);
    }

    public static String nowToString(String format_pattern) {
        return DateTime.now().toString(format_pattern);
    }

    /**
     * �ַ���ת�������� "yyyy-MM-dd"
     * 
     * @���ߣ�wang.haibin
     * @���ڣ�2015-03-27
     * @param str
     * @param defaultValue
     * @return
     */
    public static Date parse(String str, Date defaultValue) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
    
    /**
     * �ַ���ת��������
     * 
     * @���ߣ�wang.haibin
     * @���ڣ�2015-03-27
     * @param str
     * @param pattern
     * @param defaultValue
     * @return
     */
    public static Date parse(String str, String pattern, Date defaultValue) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.parse(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /***
     * �Ƚ��������ڵĴ�С
     * 
     * @param startDate
     *            ��ʼ����
     * @param endDate
     *            ��������
     * @return -1 �� ��ʼ����С�ڽ������� 0 �� ��ʼ���ڵ��ڽ������� 1 �� ��ʼ���ڴ��ڽ�������
     */
    public static int compareDate(Date startDate, Date endDate) {
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        if (startTime < endTime) {
            return -1;
        }
        if (startTime == endTime) {
            return 0;
        }
        return 1;
    }
    
    /***
     * ��ʽ�����ڵķ���
     *
     * @param date
     *            ����ʽ��������
     * @param pattern
     *            ��ʽ���ı��ʽ ���� yyyy-MM-dd  
     * @return ��ʽ������ַ���
     */
    public static String formatDate(Date date, String pattern) {
        // ��������FORMAT��ʵ��
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }
    
    /**
     * 
     * <�ַ�����ʽ��ת����"yyyy-MM-dd HH:mm:ss"><���ܾ���ʵ��>
     *
     * @create��2015��12��4�� ����3:41:29
     * @author�� sl
     * @param strDate
     * @param defaultValue
     * @return
     */
    public static Date parseStr(String strDate, Date defaultValue){  
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
    	try {
			return sdf.parse(strDate);
		} catch (ParseException e) {
			return defaultValue;
		}
    }  


    public static void main(String[] args) {
        System.out.println("foo bar"+DateUtils.nowDateTimeString());
        
        System.out.println(DateUtils.formatDate(
                        DateUtils.parse("2015-12-06 15:11", null), "yyyy��MM��dd�� HH:mm"));
        
        
        System.out.println(DateUtils.parse("2015-12-06 15:11", null));
        
        System.out.println(DateUtils.parseStr("2015-12-05 15:58", null));

    }
}
