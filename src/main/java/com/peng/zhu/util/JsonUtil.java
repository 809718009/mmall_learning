package com.peng.zhu.util;

import com.google.common.collect.Lists;
import com.peng.zhu.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //序列化对象所有属性
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);
        //当对象为空时忽略
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        //取消默认转换timestamps形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);
        //统一时间格式
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));
        //防止反序列化对象时 序列化有 对象没有的属性错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }
    /**
     * 对象序列化为字符串
     */

    public static <T> String objToStr(T obj){
        if(obj == null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj:objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to String error",e);
            return null;
        }
    }

    /**
     *已经序列化好的对象
     */
    public static <T> String objToStrPretty(T obj){
        if(obj == null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj:objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to String error",e);
            return null;
        }
    }
    /**
     *字符串序列化为对象
     */
    public static <T> T str2Object(String str,Class<T> clazz){
        if(StringUtils.isEmpty(str) || clazz == null){
            return null;
        }
        try {
            return clazz.equals(String.class)?(T)str:objectMapper.readValue(str,clazz);
        } catch (Exception e) {
            log.warn("Parse Object to String error",e);
            return null;
        }
    }
    public static <T> T str2Object(String str, TypeReference typeReference){
        if(StringUtils.isEmpty(str) || typeReference == null){
            return null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class)?str:objectMapper.readValue(str,typeReference));
        } catch (Exception e) {
            log.warn("Parse Object to String error",e);
            return null;
        }
    }
    public static <T> T str2Object(String str, Class<?> collectionClass,Class<?>... elementClasses){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClasses);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (Exception e) {
            log.warn("Parse Object to String error",e);
            return null;
        }
    }


    public static void main(String[] args) {
        User user1 = new User();
        user1.setUsername("zp");
        user1.setEmail("zp@163.com");
        User user2 = new User();
        user2.setUsername("zp");
        user2.setEmail("zp@163.com");
        String userString = JsonUtil.objToStr(user1);
        String userStringPretty = JsonUtil.objToStrPretty(user1);
        //log.info("user1对象序列化:"+userString);
        //log.info("userPretty对象序列化:"+userString);
        User str2User=JsonUtil.str2Object(userString,User.class);
        List<User> userList = Lists.newArrayList();
        userList.add(user1);
        userList.add(user2);
        String userListString = JsonUtil.objToStr(userList);
        //log.info("集合对象序列化:"+userListString);
        List<User> list1= JsonUtil.str2Object(userListString,List.class);
        List<User> list3= JsonUtil.str2Object(userListString,List.class,User.class);
        List<User> list2 = JsonUtil.str2Object(userListString, new TypeReference<List<User>>() {});
        log.info("集合对象序列化:"+list1);
        log.info("集合对象序列化:"+list2);
        log.info("集合对象序列化:"+list3);
        System.out.println("end");
    }
}
