var usBrowserControllers = angular.module('usBrowserControllers', []);

usBrowserControllers.controller('FacetListCtrl', ['$scope', '$http','$routeParams', 
  function ($scope, $http, $routeParams) {
    $http.get('/json-ld/facet/' + $routeParams.ruleform).success(function(data) {
      $scope.facets = data;
    });
  }]);

usBrowserControllers.controller('FacetRuleformsListCtrl', ['$scope', '$http',
  function ($scope, $http) {
    $http.get('/json-ld/facet').success(function(data) {
      $scope.facetRuleforms = data;
    });
  }]);

usBrowserControllers.controller('FacetDetailCtrl', ['$scope', '$http','$routeParams', 
  function ($scope, $http, $routeParams) {
    $http.get('/json-ld/facet/$routeParams.ruleform/$routeParams.instance').success(function(data) {
      $scope.facet = data;
    });
  }]);