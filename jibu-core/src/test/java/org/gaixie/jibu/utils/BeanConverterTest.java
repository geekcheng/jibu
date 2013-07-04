package org.gaixie.jibu.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.gaixie.jibu.utils.BeanConverter;
import org.gaixie.jibu.utils.Person;

public class BeanConverterTest {
    private Map<Object,Object> map;

    @Before
    public void setup() throws Exception {
        map = new HashMap<Object,Object>();
        map.put("id","1");
        map.put("name","王大");
        map.put("age","34");
        map.put("married","true");
        map.put("hasChildren","false");
        map.put("salary","10000.89");
        map.put("birthday","2010-10-10 23:10:00");
        map.put("deathtime","2011-10-10 23:10:00");
    }

    @Test
    public void testMapToBean() throws Exception {
        Person p = BeanConverter.mapToBean(Person.class,map);
        Assert.assertNotNull(p);
        Assert.assertTrue("王大".equals(p.getName()));
        Assert.assertTrue(34 == p.getAge());
        Assert.assertTrue(true == p.isMarried());
        Assert.assertTrue(false == p.getHasChildren());
        Assert.assertTrue(1 == p.getId());
        Assert.assertTrue(10000.89f == p.getSalary());
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Assert.assertEquals(p.getBirthday(), format.parse("2010-10-10 23:10:00"));
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse("2011-10-10 23:10:00");
        Assert.assertEquals(p.getDeathtime(), new Timestamp(date.getTime()));
    }
}
