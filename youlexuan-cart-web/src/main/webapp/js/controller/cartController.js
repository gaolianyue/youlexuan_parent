app.controller("cartController",function (cartService,$scope) {

    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;

            $scope.sum($scope.cartList);

        })
    }
    
    $scope.addToCartList = function (itemId,num) {
        cartService.addToCartList(itemId,num).success(function (response) {
            if(response.success){
                $scope.findCartList();
                $scope.totalValue={
                    totalNum:0,
                    totalMoney:0.00
                };

            }else {
                alert(response.message);
            }
        })
    }

//求总和
    $scope.totalValue={
        totalNum:0,
        totalMoney:0.00
    };
    $scope.sum=function(cartList){
        //自定义一个合计实体

        for(var i=0;i<cartList.length;i++){
            var cart=cartList[i];
            for(var j=0;j<cartList[i].orderItemList.length;j++){
                $scope.totalValue.totalNum+=cartList[i].orderItemList[j].num;//购物车明细
                $scope.totalValue.totalMoney+= cartList[i].orderItemList[j].totalFee;
            }
        }

    }
    //获取地址列表
    $scope.findAddressList=function(){
        cartService.findAddressList().success(
            function(response){
                $scope.addressList=response;
                //设置默认地址
                for(var i=0;i< $scope.addressList.length;i++){
                    if($scope.addressList[i].isDefault=='1'){
                        $scope.address=$scope.addressList[i];
                        break;
                    }
                }
            }
        );
    }
    //选择地址
    $scope.selectAddress=function(address){
        $scope.address=address;
    }

    //判断是否是当前选中的地址
    $scope.isSelectedAddress=function(address){
        if(address==$scope.address){
            return true;
        }else{
            return false;
        }
    }

    $scope.order={paymentType:'1'};
    //选择支付方式
    $scope.selectPayType=function(type){
        $scope.order.paymentType= type;
    }

    //保存订单
    $scope.submitOrder=function(){
        $scope.order.receiverAreaName=$scope.address.address;//地址
        $scope.order.receiverMobile=$scope.address.mobile;//手机
        $scope.order.receiver=$scope.address.contact;//联系人
        cartService.submitOrder( $scope.order ).success(
            function(response){
                if(response.success){
                    //页面跳转
                    if($scope.order.paymentType=='1'){//如果是扫码支付，跳转到支付页面
                        location.href="pay.html";
                    }else{//如果货到付款，跳转到提示页面
                        location.href="paysuccess.html";
                    }
                }else{
                    alert(response.message);	//也可以跳转到提示页面
                }
            }
        );
    }
})