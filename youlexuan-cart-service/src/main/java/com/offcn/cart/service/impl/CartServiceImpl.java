package com.offcn.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.cart.service.CartService;
import com.offcn.entity.Cart;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addGoodsToCart(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据商品SKU ID查询SKU商品信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //如果当前数据从数据库中获取，一定要判断空值
        if(null == item){
            throw new RuntimeException("当前商品不存在");
        }
        if(item.getStatus().equals("0")){
            throw new RuntimeException("当前商品状态无效");
        }
        //2.获取商家ID
        String sellerId = item.getSellerId();
        //3.根据商家ID判断购物车列表（List<Cart>）中是否存在该商家的购物车
        Cart cart = this.selectCartInCartList(cartList, item.getSellerId());
        //4.如果购物车列表中不存在该商家的购物车
        if(null == cart){
            //4.1 新建购物车对象

            cart = new Cart();
            //商家ID
            cart.setSellerId(sellerId);
            //商家名字
            cart.setSellerName(item.getSeller());
            //添加购物车
            TbOrderItem orderItem = this.addOrderItem(item,num);
            List orderList = new ArrayList();
            orderList.add(orderItem);
            cart.setOrderItemList(orderList);
            //4.2 将新建的购物车对象添加到购物车列表
            cartList.add(cart);
        }else {
            //5.如果购物车列表中存在该商家的购物车
            // 查询购物车明细列表中是否存在该商品
            TbOrderItem orderItem = this.selectOrderItemInOrderItemList(cart.getOrderItemList(), itemId);
            //5.1. 如果没有，新增购物车明细
            if(null == orderItem){
                TbOrderItem tbOrderItem = this.addOrderItem(item, num);
                cart.getOrderItemList().add(tbOrderItem);

            } else {
                // 5.2. 如果有，在原购物车明细上添加数量，更改金额
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                //如果商品数量小于等于0
                if(orderItem.getNum() <= 0){
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果当前购物车明细对象无orderItem
                if(cart.getOrderItemList().size() == 0){
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }




    //3.根据商家ID判断购物车列表（List<Cart>）中是否存在该商家的购物车
    private Cart selectCartInCartList(List<Cart> cartList,String sellerId){
        for (Cart cart : cartList) {
            //4.如果购物车列表中不存在该商家的购物车
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    private TbOrderItem selectOrderItemInOrderItemList(List<TbOrderItem> orderItemList,Long itemId){
        for (TbOrderItem orderItem : orderItemList) {
            //4.如果购物车列表中不存在该商家的购物车
            if(orderItem.getItemId().longValue() == (itemId.longValue())){
                return orderItem;
            }
        }
        return null;
    }

    //添加购物车明细
    private TbOrderItem addOrderItem(TbItem item,Integer num){
        //判断数量大于等于0
        if(num <= 0){
            throw  new RuntimeException("数量非法");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setPicPath(item.getImage());
        orderItem.setTitle(item.getTitle());
        orderItem.setNum(num);
        orderItem.setPrice(item.getPrice());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));

        return orderItem;
    }

    /*从redis中查询购物车*/
    @Override
    public List<Cart> findCartByUserNameFromRedis(String username) {
        System.out.println("从redis中提取购物车数据....."+username);
        List<Cart> cartListRedis = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if(null == cartListRedis){
            cartListRedis=new ArrayList();
        }
        return cartListRedis;
    }
    /*将购物车保存到redis中*/
    @Override
    public void addCartToRedis(String username, List<Cart> cartList) {
        System.out.println("向redis存入购物车数据....."+username);
        redisTemplate.boundHashOps("cartList").put(username, cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cookie_cartList, List<Cart> redis_cartList) {
        for (Cart cart : cookie_cartList) {
            //遍历购物车明细对象
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                this.addGoodsToCart(redis_cartList,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return redis_cartList;
    }


}
