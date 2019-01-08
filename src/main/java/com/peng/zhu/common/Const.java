package com.peng.zhu.common;


import com.google.common.collect.Sets;

import java.util.Set;

public class Const {
    public static final String CURRENT_USER = "currentUser";
    public static final String USERNAME="username";
    public static final String EMAIL="email";

    public interface RedisCacheExtime{
        int Redis_Session_Extime = 60*30;//30分钟
    }
    public interface role{
        int ROLE_CUSTOM=0 ;//普通用户
        int ROLE_ADMIN=1;//管理员
    }
    public interface ProductListOrderBy{
        Set<String> PRICE_ASE_DSE = Sets.newHashSet("price_asc","price_desc");
    }
    public interface Cart{
        int CHECKED=1 ;//购物车中该商品已经选中
        int UN_CHECKED=0 ; //购物车中该商品未选中
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }
    public enum ProductStatusEnum{
        ON_SALE(1,"在线");
        private int code;
        private String status;
        ProductStatusEnum(int code,String status){
        this.code=code;
        this.status=status;
        }
        public int getCode() {
            return code;
        }

        public String getStatus() {
            return status;
        }
    }
    public enum OrderStatusEnum{
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已付款"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭");
        OrderStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }
        public static OrderStatusEnum codeOf(int code){
            for(OrderStatusEnum orderStatusEnum:values()){
                if(orderStatusEnum.getCode()==code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
        private int code;
        private String value;
        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
    public interface  AlipayCallBack{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    public enum PayPlatFormEnum{
        ALIPAY(1,"支付宝");
        PayPlatFormEnum(int caode,String value){
            this.code = code;
            this.value = value;
        }
        private int code;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        private String value;
    }
    public enum PaymentType{
        ON_PAY(1,"支付宝");
        PaymentType(int code,String value){
            this.code = code;
            this.value = value;
        }
        public static PaymentType codeOf(int code){
            for(PaymentType paymentType:values()){
                if(paymentType.getCode()==code){
                    return paymentType;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
        private int code;
        private String value;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

}
