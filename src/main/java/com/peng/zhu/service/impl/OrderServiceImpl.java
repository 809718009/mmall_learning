package com.peng.zhu.service.impl;


import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.peng.zhu.common.Const;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.dao.*;
import com.peng.zhu.pojo.*;
import com.peng.zhu.service.IOrderService;
import com.peng.zhu.util.BigDecimalUtil;
import com.peng.zhu.util.DateTimeUtil;
import com.peng.zhu.util.FTPUtil;
import com.peng.zhu.util.PropertiesUtil;
import com.peng.zhu.vo.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service("iOrderService")
public class OrderServiceImpl  implements IOrderService {
    private static  AlipayTradeService tradeService;
    static {

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
    }
    private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ShippingMapper shippingMapper;



    public ServerResponse createOrder(Integer userId,Integer shippingId){
        //从购物车获取数据
        List<Cart> cartList = cartMapper.selectCheckedCartListByUserId(userId);
        //计算orderitem各自价格并封装。
        ServerResponse orderItemResponse = this.getCartOrderItemList(userId,cartList);
        if(!orderItemResponse.isSuccess()){
            return orderItemResponse;
        }
         //计算订单里面商品总价
        List<OrderItem> orderItemList = (List<OrderItem>)orderItemResponse.getData();
        BigDecimal payment = getOrderTotalPrice(orderItemList);
        //生成订单
        Order order = this.assembleorder(userId,shippingId,payment);
        if(order==null){
            return ServerResponse.createByErrorMessage("生成订单失败!");
        }
        if(CollectionUtils.isEmpty(orderItemList)){
            return ServerResponse.createByErrorMessage("订单明细为空!");
        }
        //订单明细添加订单号
        for(OrderItem orderItem:orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
        //mybatis批量插入
        int rowCount = orderItemMapper.batchInsert(orderItemList);
        if(rowCount==0){
            return ServerResponse.createByErrorMessage("生成订单明细失败!");
        }
        //减少库存
        this.reduceProductStock(orderItemList);
        //清空购物车
        this.cleanCart(cartList);
        //向前端返回数据
        OrderVo orderVo = assembleOrderVo(order,orderItemList);
        return  ServerResponse.createBySuccess(orderVo);
    }

    public ServerResponse cancleOrder(Integer userId,Long orderNo){
        Order order = orderMapper.selectByUserIdAndOrderId(userId,orderNo);
        if(order==null){
            return ServerResponse.createByErrorMessage("订单不存在");
        }
        order.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int rowCount = orderMapper.updateByPrimaryKeySelective(order);
        if(rowCount>0){
            return ServerResponse.createBySuccessMessage("订单取消成功");
        }
        return ServerResponse.createByErrorMessage("订单已付款，订单取消失败!");
    }

    public ServerResponse<OrderVo> getOrderDetail(Integer userId,Long orderNo){
        Order order = orderMapper.selectByUserIdAndOrderId(userId,orderNo);
        if(order==null){
            return  ServerResponse.createByErrorMessage("订单不存在!");
        }
        List<OrderItem> orderItemList = orderItemMapper.selectOrderItemsByOrderIdAndUserId(orderNo,userId);
        OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    public ServerResponse<PageInfo> getOrderList(Integer userId,Integer pagNum,Integer pagSize){
        PageHelper.startPage(pagNum,pagSize);
        List<Order> orderList = orderMapper.selectOrderListByUserId(userId);
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList,userId);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private List<OrderVo> assembleOrderVoList(List<Order> orderList,Integer userId){
        List<OrderVo> orderVoList = Lists.newArrayList();
        for(Order order:orderList) {
            List<OrderItem> orderItemList = Lists.newArrayList();
            if (userId == null) {
                //todo
                orderItemList = orderItemMapper.selectOrderItemsByOrderNo(order.getOrderNo());
            } else {
                orderItemList = orderItemMapper.selectOrderItemsByOrderIdAndUserId(order.getOrderNo(), userId);
                OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
            }
            OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }
    public ServerResponse getOrderCartProduct(Integer userId){
        List<Cart> cartList = cartMapper.selectCheckedCartListByUserId(userId);
        if(CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createByErrorMessage("购物车为空!");
        }
        ServerResponse orderItemListResponse  = this.getCartOrderItemList(userId,cartList);
        if(!orderItemListResponse.isSuccess()){
            return orderItemListResponse;
        }
        List<OrderItem> orderItemList = (List<OrderItem>)orderItemListResponse.getData();

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        BigDecimal payment = new BigDecimal("0");
        for(OrderItem orderItem:orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(this.assembleOrderItemVo(orderItem));
        }
        OrderProductVo orderProductVo = new OrderProductVo();
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        orderProductVo.setTotalPrice(payment);
        return ServerResponse.createBySuccess(orderProductVo);
    }
    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setUserId(order.getUserId());
        orderVo.setPayment(order.getPayment());
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setUpdateTime(DateTimeUtil.dateToStr(order.getUpdateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setPostage(order.getPostage());
        orderVo.setShippingId(order.getShippingId());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentType.codeOf(order.getPaymentType()).getValue());

        ShippingVo shippingVo = this.assembleShippingVo(order.getShippingId());
        if(shippingVo!=null){
            orderVo.setReceicverName(shippingVo.getReceiverName());
            orderVo.setShippingVo(shippingVo);
        }
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for(OrderItem orderItem:orderItemList){
            orderItemVoList.add(this.assembleOrderItemVo(orderItem));
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    private ShippingVo assembleShippingVo(Integer shippingId){
        Shipping shipping = shippingMapper.selectByPrimaryKey(shippingId);
        if(shipping==null){
            return null;
        }
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setId(shipping.getId());
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        return shippingVo;
    }
    private OrderItemVo assembleOrderItemVo(OrderItem orderItem){

            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setOrderNo(orderItem.getOrderNo());
            orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
            orderItemVo.setProductId(orderItem.getProductId());
            orderItemVo.setProductImage(orderItem.getProductImage());
            orderItemVo.setProductName(orderItem.getProductName());
            orderItemVo.setQuantity(orderItem.getQuantity());
            orderItemVo.setTotalPrice(orderItem.getTotalPrice());
            orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
            orderItemVo.setUpdateTime(DateTimeUtil.dateToStr(orderItem.getUpdateTime()));

        return orderItemVo;

    }
    private void cleanCart(List<Cart> cartList){
        for(Cart cart:cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }
    private void reduceProductStock(List<OrderItem> orderItemList){
        for(OrderItem orderItem:orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    private Order assembleorder(Integer userId,Integer shipping,BigDecimal payment){
        Order order = new Order();
        //生成订单号
        order.setOrderNo(this.generateOrderNo());
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPayment(payment);
        order.setPostage(0);
        order.setPaymentType(Const.PaymentType.ON_PAY.getCode());
        order.setShippingId(shipping);
        order.setUserId(userId);
        //
        int rowCount = orderMapper.insert(order);
        if(rowCount>0){
            return order;
        }
         return null;
    }

    private long generateOrderNo(){
        long currentTime =System.currentTimeMillis();
        return currentTime+new Random().nextInt(100);
    }
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList){
        BigDecimal totalPrice = new BigDecimal("0");
        for(OrderItem orderItem:orderItemList){
            totalPrice = BigDecimalUtil.add(totalPrice.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return totalPrice;
    }

    private ServerResponse getCartOrderItemList(Integer userId,List<Cart> cartList){
        if(CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createByErrorMessage("购物车中没有商品!");
        }
        List<OrderItem> orderItemList = Lists.newArrayList();
        for(Cart cartItem:cartList){
            OrderItem orderItem = new OrderItem();
            Product product =  productMapper.selectByPrimaryKey(cartItem.getProductId());
            //校验产品是在售状态和库存是否足够
            if(product.getStatus()!=Const.ProductStatusEnum.ON_SALE.getCode()){
                return ServerResponse.createByErrorMessage("商品"+product.getName()+"已经下架!");
            }
            if(cartItem.getQuantity()>product.getStock()){
                return ServerResponse.createByErrorMessage("商品"+product.getName()+"库存不足!");
            }
            //orderItem.setOrderNo();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartItem.getQuantity().doubleValue()));
            orderItem.setUserId(userId);
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    /**
     * backend  begin
     */

      public ServerResponse<PageInfo> getManageOrderList(Integer pagNum,Integer pagSize){
          PageHelper.startPage(pagNum,pagSize);
          List<Order> orderList = orderMapper.selectOrderList();
          //List<OrderItem> orderItemList = orderItemMapper.
          List<OrderVo> orderVoList = this.assembleOrderVoList(orderList,null);
          PageInfo pageInfo = new PageInfo(orderList);
          pageInfo.setList(orderVoList);
          return  ServerResponse.createBySuccess(pageInfo);
      }


      public ServerResponse<OrderVo> getManageOrderDetail(Long orderNo){
           Order order = orderMapper.selectByOrderId(orderNo);
           if(order==null){
               return ServerResponse.createByErrorMessage("订单不存在!");
           }
           List<OrderItem> orderItemList = orderItemMapper.selectOrderItemsByOrderNo(orderNo);
           OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
           return ServerResponse.createBySuccess(orderVo);
      }

      public ServerResponse<PageInfo> getManageSearchOrder(Long orderNo,Integer pagNum,Integer pagSize){
          PageHelper.startPage(pagNum,pagSize);
          Order order = orderMapper.selectByOrderId(orderNo);
          if(order==null){
              return ServerResponse.createByErrorMessage("订单不存在!");
          }
          List<OrderItem> orderItemList = orderItemMapper.selectOrderItemsByOrderNo(orderNo);
          OrderVo orderVo = this.assembleOrderVo(order,orderItemList);
          PageInfo pageInfo = new PageInfo(Lists.newArrayList(order));
          pageInfo.setList(Lists.newArrayList(orderVo));
          return  ServerResponse.createBySuccess(pageInfo);
      }


      public ServerResponse<String> manageSendGoods(Long orderNo){
          Order order = orderMapper.selectByOrderId(orderNo);
          if(order!=null){
             if(order.getStatus()==Const.OrderStatusEnum.PAID.getCode()){
                order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                order.setSendTime(new Date());
                order.setUpdateTime(new Date());
                int rowCount = orderMapper.updateByPrimaryKeySelective(order);
                if(rowCount>0){
                   return ServerResponse.createBySuccessMessage("发货成功!");
               }
             }
          }
          return ServerResponse.createByErrorMessage("订单不存在!");
      }


    /**
     * backend  end
     */

    @Override
    public void closeOrder(int hour) {
        Date closeOrderTime = DateUtils.addHours(new Date(),-hour);
        List<Order> orderList = orderMapper.selectOrderStatusByCreateTime(Const.OrderStatusEnum.NO_PAY.getCode(),DateTimeUtil.dateToStr(closeOrderTime));
        for(Order order:orderList){
            List<OrderItem> orderItemList = orderItemMapper.selectOrderItemsByOrderNo(order.getOrderNo());
            for(OrderItem orderItem:orderItemList){
                Integer stock = productMapper.selectStockByProducId(orderItem.getProductId());
                if(stock==null){
                    continue;
                }
                Product product = new Product();
                product.setId(orderItem.getProductId());
                product.setStock(stock+orderItem.getQuantity());
                productMapper.updateByPrimaryKeySelective(product);
            }
            orderMapper.updateOrderStatusById(order.getId());
            logger.info("close order orderNo {}",order.getOrderNo());
        }
    }
    public ServerResponse pay(Integer userId,Long orderNo,String path){
        Map<String,String> resultMap = new HashMap<String,String>();
        Order order = orderMapper.selectByUserIdAndOrderId(userId,orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("订单不存在!");
        }
        resultMap.put("orderNo",String.valueOf(order.getOrderNo()));

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店消费”
        String subject = new StringBuilder().append("zp订单支付:").append(order.getOrderNo()).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();


        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0.0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品3件共20.00元"
        String body = new StringBuilder().append("订单").append(order.getOrderNo()).append("购买商品共花费:").append(order.getPayment()).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        String providerId = "2088100200300400500";
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(providerId);

        // 支付超时，线下扫码交易定义为5分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        List<OrderItem> orderItemList = orderItemMapper.selectOrderItemsByOrderIdAndUserId(order.getOrderNo(),order.getUserId());
        for(OrderItem orderItem:orderItemList){
            GoodsDetail goods = new GoodsDetail();
            goods.newInstance(orderItem.getProductId().toString(),orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue(),orderItem.getQuantity());
            goodsDetailList.add(goods);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);


        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功:");
                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File folder = new File(path);
                if(!folder.exists()){
                    folder.setWritable(true);
                    folder.mkdirs();
                }
                // 需要修改为运行机器上的路径
                //细节细节细节
                String qrPath = String.format(path+"/qr-%s.png",response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png",response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                File targetFile = new File(qrPath,qrFileName);
                List<File> fileList = Lists.newArrayList();
                fileList.add(targetFile);
                try {
                    FTPUtil.uploadFile(fileList);
                } catch (IOException e) {
                    logger.error("上传二维码失败!",e);
                }
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix"+qrFileName);
                resultMap.put("qrUrl",qrUrl);
                return ServerResponse.createBySuccess(resultMap);
            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }
    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    public ServerResponse aliCallBack(Map<String,String> params){
        //验证订单是否存在
        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        Order order = orderMapper.selectByOrderId(orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("该订单不在zp商城，请忽略!");
        }
        //验证订单的状态
        if(order.getStatus()> Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccessMessage("支付宝重复调用");
        }
        if(Const.AlipayCallBack.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        PayInfo payInfo = new PayInfo();
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatFormEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);
        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();
    }
}
