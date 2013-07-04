package org.gaixie.jibu.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.gaixie.jibu.JibuException;
import org.gaixie.jibu.config.JibuConfig;
import org.gaixie.jibu.security.model.Criteria;

/**
 * 提供静态方法自动生成 SQL 语句。
 *
 */
public class SQLBuilder {

    /**
     * 通过 Bean 实例转化为 SQL 语句段，只转化非空属性。
     * <p>
     * 支持的属性类型有 int, Integer, float, Float, boolean, Boolean ,Date
     *
     * @param bean Bean 实例
     * @param split sql 语句的分隔符，如 " AND " 或 " , "
     * @return SQl 语句段，如果所有属性值都为空，返回 ""。
     *
     * @exception JibuException 转化失败时抛出
     */
    public static String beanToSQLClause(Object bean, String split)
        throws JibuException {
        StringBuilder sb = new StringBuilder();
        try{
            if(null != bean ) {
                BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
                PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

                for( PropertyDescriptor pd : pds ){
                    String clause = columnClause("      "+pd.getName(),
                                                 pd.getPropertyType(),
                                                 pd.getReadMethod().invoke(bean));

                    if (null != clause) {
                        sb.append(clause + split+" \n");
                    }
                }
            }
        } catch( Exception e){
            throw new JibuException(e.getMessage());
        }
        return sb.toString();
    }

    private static String columnClause(String name, Class type, Object value){
        if (null == value        ||
            "class".equals(name) ||
            "serialVersionUID".equals(name)) return null;
        String databaseType = JibuConfig.getProperty("databaseType");
        if (String.class.equals(type)) {
            return (name +" = '"+(String)value+"' ");
        } else if (Date.class.equals(type)) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            if("Derby".equals(databaseType) || "MySQL".equals(databaseType)) {
                return (name +" = '"+format.format((Date)value)+"' ");
            } else if("PostgreSQL".equals(databaseType) || "Oracle".equals(databaseType)) {
                return (name +" = to_date('"+format.format((Date)value)+"','YYYY-MM-DD') ");
            }
        } else if (Timestamp.class.equals(type)) {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if("Derby".equals(databaseType) || "MySQL".equals(databaseType)) {
                return (name +" = '"+format.format((Timestamp)value)+"' ");
            } else if("PostgreSQL".equals(databaseType) || "Oracle".equals(databaseType)) {
                return (name +" = to_timestamp('"+format.format((Timestamp)value)+"','YYYY-MM-DD HH24:MI:SS') ");
            }

        } else if (boolean.class.equals(type)||Boolean.class.equals(type)) {
            if("Derby".equals(databaseType) || "MySQL".equals(databaseType) || "Oracle".equals(databaseType)) {
                return (name +" = "+(((Boolean)value)? "1":"0") +" ");
            } else if("PostgreSQL".equals(databaseType)) {
                return (name +" = "+(Boolean)value+" ");
            }
        } else if (int.class.equals(type)||Integer.class.equals(type)) {
            return (name +" = "+(Integer)value+" ");
        } else if (float.class.equals(type)||Float.class.equals(type)) {
            return (name +" = "+(Float)value+" ");
        }
        return null;
    }

    /**
     * 整理 Where 语句，补充 Where 关键字，剔除多余的 And 关键字。
     * <p>
     *
     * @param sql 要处理的 Where 语句。
     * @return 有效的 Where 语句。
     *
     */
    public static String getWhereClause (String sql) {
        if (null == sql ) return null;

        String s = sql.trim();
        int len = s.length();
        int last = s.lastIndexOf("AND");
        if (len > 0 && (last == len -3)) {
            if (s.indexOf("WHERE") == -1) {
                s = "WHERE " + s.substring(0,len-3);
            } else {
                s = s.substring(0,len-3);
            }
        }
        return s;
    }

    /**
     * 整理 SET 语句，补充 SET 关键字，剔除多余的 " , "。
     * <p>
     *
     * @param sql 要处理的 Where 语句。
     * @return 有效的 Where 语句。
     *
     */
    public static String getSetClause (String sql) {
        if (null == sql ) return null;

        String s = sql.trim();
        int len = s.length();
        int last = s.lastIndexOf(",");
        if (len > 0 && (last == len -1)) {
            if (s.indexOf("SET") == -1) {
                s = "SET   " + s.substring(0,len-1);
            } else {
                s = s.substring(0,len-1);
            }
        }
        return s;
    }

    /**
     * 通过 criteria 生成分页的 SQL 语句。
     * <p>
     * 必须在 WHERE 子句全部处理完以后条用。如果有 ORDER BY，也要在其之前调用。
     * Derby 10.5，Oracle 8 以后版本有效。
     *
     * @param sql 要处理语句。
     * @param crt 传递分页信息。
     * @return 有效的 sql语句。
     * @see #getWhereClause(String sql)
     */
    public static String getPagingClause (String sql,Criteria crt) {
        if (null == sql ) return null;
        if (null == crt) return sql;

        if (crt.getLimit()>0) {
            String databaseType = JibuConfig.getProperty("databaseType");
            if("Derby".equals(databaseType)) {
                // 参考 ：http://db.apache.org/derby/docs/dev/ref/rrefsqljoffsetfetch.html
                return  sql  + " OFFSET "+crt.getStart()+" ROWS FETCH NEXT "+crt.getLimit()+" ROWS ONLY ";
            } else if("PostgreSQL".equals(databaseType) || "MySQL".equals(databaseType)) {
                return  sql  + " LIMIT "+crt.getLimit()+" OFFSET "+crt.getStart()+" ";
            } else if("Oracle".equals(databaseType)) {
                // http://asktom.oracle.com/pls/asktom/f?p=100:11:0::::P11_QUESTION_ID:127412348064
                return (" SELECT * \n"+ 
                        " FROM ( SELECT a.*, rownum rnum \n"+
                        "        FROM ( "+ sql +" ) a \n"+
                        "        WHERE rownum < "+(crt.getStart()+crt.getLimit())+" ) \n"+
                        " WHERE rnum >= "+crt.getStart());
            }
        }
        return sql;
    }

    /**
     * 通过 criteria 生成排序的 sql 语句。数据库类型无关。
     * <p>
     * 注意，如果结合分页，要保证在分页语句之前条用。
     *
     * @param sql 要处理语句。
     * @param crt 传递排序信息。
     * @return 有效的 sql语句。
     *
     */
    public static String getSortClause (String sql,Criteria crt) {
        if (null == sql ) return null;

        if (null != crt && null != crt.getSort()) {
            String[] sorts = crt.getSort().split(",");
            String s = "";
            for(String sort: sorts) {
                if(s.length()==0) 
                    s = sort + " "+crt.getDir();
                else
                    s = s + " , " +sort + " "+crt.getDir();
            }
            return sql + " ORDER BY " + s;
        }
        return sql;
    }
}
