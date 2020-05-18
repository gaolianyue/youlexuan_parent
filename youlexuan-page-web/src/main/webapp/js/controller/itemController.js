//商品详细页（控制层）
app.controller('itemController',function($scope,$http){
	//数量操作
	$scope.addNum=function(x){
		$scope.num=$scope.num+x;
		if($scope.num<1){
			$scope.num=1;
		}
	}		
	
	$scope.specificationItems={};//记录用户选择的规格
	//用户选择规格
	$scope.selectSpecification=function(name,value){	
		$scope.specificationItems[name]=value;
	}	
	//判断某规格选项是否被用户选中
	$scope.isSelected=function(name,value){
		if($scope.specificationItems[name]==value){
			return true;
		}else{
			return false;
		}		
	}

	//添加商品到购物车
	$scope.addToCart = function () {
		$http.get('http://localhost:9108/cart/addToCartList.do?itemId='+$scope.sku.id+'&num='+$scope.num,{'withCredentials':true}).success(function (response) {
			if(response.success()){
				location.href='http://localhost:9108/cart.html';
			} else {
				alert(response.message);
			}
        })
    }

});
