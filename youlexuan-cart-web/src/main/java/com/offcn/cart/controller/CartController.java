package com.offcn.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.offcn.cart.service.CartService;
import com.offcn.entity.Cart;
import com.offcn.entity.Result;
import com.offcn.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;
    /*
     * 当前购物车列表
     * */

    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        //获取当前登陆的用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        /*System.out.println("当前登陆的用户名："+name);*/
        String cartListString = CookieUtil.getCookieValue(request, "cartList");
        if(cartListString == null || cartListString.equals("")){
            cartListString = "[]";
        }
        List<Cart> cookieCartLists = JSON.parseArray(cartListString, Cart.class);
        if(name == "anonymousUser"){
            String cartList_Cookie  = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
            if(cartList_Cookie  == null || cartList_Cookie .equals("")){
                cartList_Cookie  = "[]";
            }
            List<Cart> cookieCartList = null ;
            cookieCartList = JSON.parseArray(cartList_Cookie ,Cart.class);
            return  cookieCartList;
        } else {
            List<Cart> redisCartList = cartService.findCartByUserNameFromRedis(name);
            if(cookieCartLists.size() > 0 ){
                //合并购物车
                cartService.mergeCartList(cookieCartLists,redisCartList);
                //清楚本地cookie
                CookieUtil.deleteCookie(request,response,"cartList");
                //保存新的购物车集合到redis
                cartService.addCartToRedis(name,redisCartList);
            }
            return redisCartList;
        }



    }


    /*
    * 添加商品到购物车
    * */
    @RequestMapping("/addToCartList")
    @CrossOrigin(origins="http://localhost:9105",allowCredentials="true")
    public Result addToCartList(Long itemId, Integer num) {

        response.setHeader("Access-Control-Allow-Orgin","http:localhost:9105");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        //获取当前登陆的用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登陆的用户名：" + name);
        try {
            List<Cart> cartList = this.findCartList();//获取购物车列表
            cartList = cartService.addGoodsToCart(cartList, itemId, num);
            if (name.equals("anonymousUser")) {

                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "UTF-8");
            } else {
                cartService.addCartToRedis(name, cartList);
            }
            return new Result(true, "添加成功");
        } catch (RuntimeException e) {
            return new Result(false, "出现异常");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }
}
