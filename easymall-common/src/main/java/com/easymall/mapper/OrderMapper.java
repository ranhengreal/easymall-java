package com.easymall.mapper;

import com.easymall.entity.po.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    // ==================== 查询 ====================

    /**
     * 查询所有订单
     */
    @Select("SELECT * FROM orders ORDER BY create_time DESC")
    List<Order> selectAll();

    /**
     * 根据ID查询订单
     */
    @Select("SELECT * FROM orders WHERE order_id = #{orderId}")
    Order selectById(@Param("orderId") String orderId);

    /**
     * 根据订单号查询订单
     */
    @Select("SELECT * FROM orders WHERE order_sn = #{orderSn}")
    Order selectByOrderSn(@Param("orderSn") String orderSn);

    /**
     * 根据用户ID查询订单列表
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Order> selectByUserId(@Param("userId") String userId);

    /**
     * 分页查询订单
     */
    @Select("<script>" +
            "SELECT * FROM orders WHERE 1=1" +
            "<if test='orderSn != null and orderSn != \"\"'> AND order_sn LIKE CONCAT('%', #{orderSn}, '%')</if>" +
            "<if test='userId != null and userId != \"\"'> AND user_id = #{userId}</if>" +
            "<if test='orderStatus != null'> AND order_status = #{orderStatus}</if>" +
            "<if test='payStatus != null'> AND pay_status = #{payStatus}</if>" +
            "<if test='startTime != null and startTime != \"\"'> AND create_time >= #{startTime}</if>" +
            "<if test='endTime != null and endTime != \"\"'> AND create_time &lt;= #{endTime}</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<Order> selectByCondition(Order condition);

    /**
     * 获取最大订单ID
     */
    @Select("SELECT MAX(order_id) FROM orders")
    String getMaxOrderId();

    // ==================== 增删改 ====================

    /**
     * 新增订单
     */
    @Insert("INSERT INTO orders (order_id, order_sn, user_id, user_name, total_amount, " +
            "discount_amount, freight_amount, pay_amount, pay_type, pay_status, order_status, " +
            "receiver_name, receiver_phone, receiver_province, receiver_city, receiver_district, " +
            "receiver_address, receiver_zip, user_note) " +
            "VALUES (#{orderId}, #{orderSn}, #{userId}, #{userName}, #{totalAmount}, " +
            "#{discountAmount}, #{freightAmount}, #{payAmount}, #{payType}, #{payStatus}, #{orderStatus}, " +
            "#{receiverName}, #{receiverPhone}, #{receiverProvince}, #{receiverCity}, #{receiverDistrict}, " +
            "#{receiverAddress}, #{receiverZip}, #{userNote})")
    int insert(Order order);

    /**
     * 更新订单
     */
    @Update("UPDATE orders SET order_status = #{orderStatus}, " +
            "pay_status = #{payStatus}, pay_time = #{payTime}, " +
            "cancel_reason = #{cancelReason} WHERE order_id = #{orderId}")
    int update(Order order);

    /**
     * 更新订单状态
     */
    @Update("UPDATE orders SET order_status = #{orderStatus}, " +
            "cancel_reason = #{cancelReason} WHERE order_id = #{orderId}")
    int updateStatus(@Param("orderId") String orderId,
                     @Param("orderStatus") Integer orderStatus,
                     @Param("cancelReason") String cancelReason);

    /**
     * 更新支付状态
     */
    @Update("UPDATE orders SET pay_status = #{payStatus}, pay_time = #{payTime}, " +
            "pay_type = #{payType} WHERE order_id = #{orderId}")
    int updatePayStatus(@Param("orderId") String orderId,
                        @Param("payStatus") Integer payStatus,
                        @Param("payTime") java.time.LocalDateTime payTime,
                        @Param("payType") Integer payType);

    /**
     * 删除订单
     */
    @Delete("DELETE FROM orders WHERE order_id = #{orderId}")
    int deleteById(@Param("orderId") String orderId);
}