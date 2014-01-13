/**
 * Created by hparry on 1/6/14.
 */

angular.module('uiApp')
    .controller('NewCtrl', function ($scope, $resource) {
        $scope.auth = {type: "ProductNetwork"};
        $scope.products = [{name: 'prod1', rels: [{name: 'rel1'}, {name: 'rel2'}]},
            {name: 'prod2',
            rels: [{name: 'rel3'}, {name: 'rel4'}]}];

        $scope.product = $scope.products[0];

        $scope.relationships = [{name: 'rel1'}, {name: 'rel2'}];


        $scope.product_children = ['prod3', 'prod4'];

        $scope.colors = [
            {name:'black', shade:'dark'},
            {name:'white', shade:'light'},
            {name:'red', shade:'dark'},
            {name:'blue', shade:'dark'},
            {name:'yellow', shade:'light'}
        ];
        $scope.color = $scope.colors[2]; // red

        var productResource = $resource('/v1/services/data/ruleform/Product/:id', {id: '@id'});
        var authResource = $resource('/v1/services/data/ruleform/ProductNetwork');
        $scope.authId = authResource.post($scope.auth, function() {
            console.log('posted to the thingie: ' + $scope.authId);
        });
        $scope.newProduct = productResource.get({id: '1'}, function() {
            console.log('got a resource: ' + $scope.newProduct.name);
        });
    });

angular.module('apiApp')
    .controller('ERListViewCtrl', function($scope, $resource) {
        var productResource = $resource('/v1/services/data/ruleform/Product/:productId', {
            
        });
        //$scope.ruleforms = [];
        $scope.ruleforms = productResource.query();
    })

