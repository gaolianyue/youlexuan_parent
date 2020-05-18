app.service('payService',function($http,$location){
    //本地支付
    this.createNative=function(){
        return $http.get('pay/createNative.do');
    }
    //查询支付状态
    this.queryPayStatus=function(out_trade_no){
        return $http.get('pay/queryPayStatus.do?out_trade_no='+out_trade_no);
    }
    //获取金额
    $scope.getMoney=function(){
        return $location.search()['money'];
    }
});