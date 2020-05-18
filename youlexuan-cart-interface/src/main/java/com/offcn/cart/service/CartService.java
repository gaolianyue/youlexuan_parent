package com.offcn.cart.service;

import com.offcn.entity.Cart;

import java.util.List;

public interface CartService {

    //添加商品到购物车（未登录）
    public List<Cart> addGoodsToCart(List<Cart> cartList, Long itemId,Integer num);

    /**
     * 从redis中查询购物车
     * @param username
     * @return
     */
    public List<Cart> findCartByUserNameFromRedis(String username);

    /**
     * 将购物车保存到redis
     * @param username
     * @param cartList
     */
    public void addCartToRedis(String username,List<Cart> cartList);

    /*
    * 合并购物车
    * */
    public List<Cart> mergeCartList(List<Cart> cookie_cartList,List<Cart> redis_cartList);
}
