/**
 * Created by hparry on 1/6/14.
 */

angular.module('uiApp')
    .controller('NewCtrl', function ($scope) {
        $scope.products = [{name: 'prod1', rels: [{name: 'rel1'}, {name: 'rel2'}]},
            {name: 'prod2',
            rels: [{name: 'rel3'}, {name: 'rel4'}]}];

        $scope.product = $scope.products[0];




        $scope.product_children = ['prod3', 'prod4'];

        $scope.colors = [
            {name:'black', shade:'dark'},
            {name:'white', shade:'light'},
            {name:'red', shade:'dark'},
            {name:'blue', shade:'dark'},
            {name:'yellow', shade:'light'}
        ];
        $scope.color = $scope.colors[2]; // red
    });