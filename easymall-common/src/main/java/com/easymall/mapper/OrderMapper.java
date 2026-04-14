package com.easymall.mapper;

import com.easymall.entity.po.Order;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface OrderMapper {

    // ==================== 查询 ====================

    @Select("SELECT * FROM orders ORDER BY create_time DESC")
    List<Order> selectAll();

    @Select("SELECT * FROM orders WHERE order_id = #{orderId}")
    Order selectById(@Param("orderId") String orderId);

    @Select("SELECT * FROM orders WHERE order_sn = #{orderSn}")
    Order selectByOrderSn(@Param("orderSn") String orderSn);

    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Order> selectByUserId(@Param("userId") String userId);

    /**
     * 条件查询订单（支持分页）
     */
    @Select("<script>" +
            "SELECT * FROM orders WHERE 1=1" +
            "<if test='orderSn != null and orderSn != \"\"'> AND order_sn LIKE CONCAT('%', #{orderSn}, '%')</if>" +
            "<if test='userName != null and userName != \"\"'> AND user_name LIKE CONCAT('%', #{userName}, '%')</if>" +
            "<if test='orderStatus != null'> AND order_status = #{orderStatus}</if>" +
            "<if test='payStatus != null'> AND pay_status = #{payStatus}</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<Order> selectByCondition(
            @Param("orderSn") String orderSn,
            @Param("userName") String userName,
            @Param("orderStatus") Integer orderStatus,
            @Param("payStatus") Integer payStatus
    );

    @Select("SELECT MAX(order_id) FROM orders")
    String getMaxOrderId();

    // ==================== 增删改 ====================

    @Insert("INSERT INTO orders (order_id, order_sn, user_id, user_name, total_amount, " +
            "discount_amount, freight_amount, pay_amount, pay_type, pay_status, order_status, " +
            "receiver_name, receiver_phone, receiver_province, receiver_city, receiver_district, " +
            "receiver_address, receiver_zip, user_note) " +
            "VALUES (#{orderId}, #{orderSn}, #{userId}, #{userName}, #{totalAmount}, " +
            "#{discountAmount}, #{freightAmount}, #{payAmount}, #{payType}, #{payStatus}, #{orderStatus}, " +
            "#{receiverName}, #{receiverPhone}, #{receiverProvince}, #{receiverCity}, #{receiverDistrict}, " +
            "#{receiverAddress}, #{receiverZip}, #{userNote})")
    int insert(Order order);

    @Update("UPDATE orders SET order_status = #{orderStatus}, " +
            "pay_status = #{payStatus}, pay_time = #{payTime}, " +
            "cancel_reason = #{cancelReason} WHERE order_id = #{orderId}")
    int update(Order order);

    @Update("UPDATE orders SET order_status = #{orderStatus}, " +
            "cancel_reason = #{cancelReason} WHERE order_id = #{orderId}")
    int updateStatus(@Param("orderId") String orderId,
                     @Param("orderStatus") Integer orderStatus,
                     @Param("cancelReason") String cancelReason);

    @Update("UPDATE orders SET pay_status = #{payStatus}, pay_time = #{payTime}, " +
            "pay_type = #{payType} WHERE order_id = #{orderId}")
    int updatePayStatus(@Param("orderId") String orderId,
                        @Param("payStatus") Integer payStatus,
                        @Param("payTime") java.time.LocalDateTime payTime,
                        @Param("payType") Integer payType);

    @Delete("DELETE FROM orders WHERE order_id = #{orderId}")
    int deleteById(@Param("orderId") String orderId);

    @Select("SELECT COUNT(*) FROM orders")
    Integer countAll();

    @Select("SELECT COUNT(*) FROM orders WHERE order_status = #{status}")
    Integer countByStatus(@Param("status") Integer status);

    @Select("SELECT SUM(pay_amount) FROM orders WHERE order_status = 3")
    BigDecimal sumTotalAmountByStatus(@Param("status") Integer status);

    /**
     * 更新订单备注
     */
    @Update("UPDATE orders SET remark = #{remark} WHERE order_id = #{orderId}")
    int updateRemark(@Param("orderId") String orderId, @Param("remark") String remark);

    /**
     * 订单发货
     */
    @Update("UPDATE orders SET order_status = 2, " +
            "logistics_company = #{logisticsCompany}, " +
            "tracking_number = #{trackingNumber}, " +
            "ship_time = NOW() " +
            "WHERE order_id = #{orderId}")
    int ship(@Param("orderId") String orderId,
             @Param("logisticsCompany") String logisticsCompany,
             @Param("trackingNumber") String trackingNumber);
}