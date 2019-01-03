package com.peng.zhu.util;

import java.math.BigDecimal;

public class BigDecimalUtil {
    public BigDecimalUtil(){

    }
    public static BigDecimal add(Double a,Double b){
        BigDecimal b1 = new BigDecimal(Double.toString(a));
        BigDecimal b2 = new BigDecimal(Double.toString(b));
        return  b1.add(b2);
    }
    public static BigDecimal sub(Double a,Double b){
        BigDecimal b1 = new BigDecimal(Double.toString(a));
        BigDecimal b2 = new BigDecimal(Double.toString(b));
        return  b1.subtract(b2);
    }
    public static BigDecimal mul(Double a,Double b){
        BigDecimal b1 = new BigDecimal(Double.toString(a));
        BigDecimal b2 = new BigDecimal(Double.toString(b));
        return  b1.multiply(b2);
    }
    public static BigDecimal divide(Double a,Double b){
        BigDecimal b1 = new BigDecimal(Double.toString(a));
        BigDecimal b2 = new BigDecimal(Double.toString(b));
        return  b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);//四舍五入
    }
}
