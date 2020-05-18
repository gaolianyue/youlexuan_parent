app.service("cartService",function ($http) {
    this.findCartList = function () {
        return $http.get("../cart/findCartList.do");
    }
    this.addToCartList = function (itemId,num) {
        return $http.get("../cart/addToCartList.do?itemId="+itemId+"&num="+num)
    }

    //获取地址列表
    this.findAddressList = function () {
        return $http.get('address/findListByLoginUser.do');
    }
    //保存订单
    this.submitOrder=function(order){
        return $http.post('order/add.do',order);
    }
});