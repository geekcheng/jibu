package org.gaixie.jibu.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.security.model.Criteria;

/**
 * 提供静态方法将不同的对象与 Javabean 进行转化。
 *
 */
public class BeanConverter {

    /**
     * 通过 Map 对Javabean 进行实例化并赋值。
     * <p>
     * Map 对象的格式必须为 key/value 。如果 map 中的 key 与 javabean
     * 中的属性名称一致，并且值可以被转化，则进行赋值。<p>
     *
     * Map 中的 value 不能为数组类型，也就是说不能用
     * request.getParameterValues() 来获取 value。<p>
     *
     * @param cls 被处理 bean 的 Class
     * @param map 由 bean 属性名做 key 的 Map
     * @return 实例化，并赋值完成的 Bean
     *
     * @exception JibuException 转化失败时抛出
     */
    public static <T> T mapToBean(Class<T> cls, Map map) throws JibuException {
        try {
            T bean = cls.newInstance();
            BeanInfo beanInfo = Introspector.getBeanInfo(cls);
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

            for( PropertyDescriptor pd : pds ){
                Object obj = map.get(pd.getName());
                // 如果map中有相同的属性名
                if( null != obj ) {
                    Class type = pd.getPropertyType();
                    Object value = getBeanValue(type, obj);
                    if (null != value ) {
                        pd.getWriteMethod().invoke(bean,value);
                    }
                }
            }
            return bean;
        } catch (Exception e) {
            throw new JibuException(e.getMessage());
        }
    }

    private static Object getBeanValue(Class cls, Object value) throws JibuException {
        try {
            if (int.class.equals(cls)) {
                return Integer.parseInt(value.toString());
            } else if (Integer.class.equals(cls)) {
                return Integer.valueOf(value.toString());
            } else if (String.class.equals(cls)) {
                return value.toString();
            } else if (boolean.class.equals(cls)) {
                return Boolean.parseBoolean(value.toString());
            } else if (Boolean.class.equals(cls)) {
                return Boolean.valueOf(value.toString());
            } else if (float.class.equals(cls)) {
                return Float.parseFloat(value.toString());
            } else if (Float.class.equals(cls)) {
                return Float.valueOf(value.toString());
            } else if (Date.class.equals(cls)) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                return format.parse(value.toString());
            } else if (Timestamp.class.equals(cls)) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = format.parse(value.toString());
                return new Timestamp(date.getTime());
            }
        } catch (ParseException e) {
            throw new JibuException(value.toString()+" cant convert to "+cls.getName());
        }
        return null;
    }
}
