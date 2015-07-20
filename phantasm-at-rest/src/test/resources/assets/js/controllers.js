var usBrowserControllers = angular.module('usBrowserControllers', []);

usBrowserControllers.controller('FacetInstancesListCtrl', [
		'$scope',
		'$http',
		'$routeParams',
		function($scope, $http, $routeParams) {
			$http.get(
					'/json-ld/facet/' + $routeParams.ruleform + '/'
							+ $routeParams.classifier + '/'
							+ $routeParams.classification).success(
					function(data) {
						for ( var key in data) {
							if (data.hasOwnProperty(key)) {
								var parser = document.createElement('a');
								parser.href = data[key]; 
								data[key] = parser.pathname;
							}
						}
						$scope.facetInstances = data;
					});
		} ]);

usBrowserControllers.controller('FacetListCtrl', [
		'$scope',
		'$http',
		'$routeParams',
		function($scope, $http, $routeParams) {
			$http.get('/json-ld/facet/' + $routeParams.ruleform).success(
					function(data) {
						$scope.facets = data['@graph'];
					});
		} ]);

usBrowserControllers.controller('FacetRuleformsListCtrl', [ '$scope', '$http',
		function($scope, $http) {
			$http.get('/json-ld/facet').success(function(data) {
				for ( var key in data) {
					var parser = document.createElement('a');
					parser.href = data[key]; 
					data[key] = parser.pathname;
				}
				$scope.facetRuleforms = data;
			});
		} ]);

usBrowserControllers.controller('FacetDetailCtrl', [
		'$scope',
		'$http',
		'$routeParams',
		function($scope, $http, $routeParams) {
			$http.get(
					'/json-ld/facet/' + $routeParams.ruleform + '/'
							+ $routeParams.classifier + '/'
							+ $routeParams.classification).success(
					function(data) {
						$scope.facet = data;
					});
		} ]);

usBrowserControllers.controller('FacetInstanceDetailCtrl', [
		'$scope',
		'$http',
		'$routeParams',
		function($scope, $http, $routeParams) {
			$http.get(
					'/json-ld/facet/' + $routeParams.ruleform + '/'
							+ $routeParams.classifier + '/'
							+ $routeParams.classification + '/'
							+ $routeParams.instance).success(function(data) {
				$scope.facetInstance = data;
			});
		} ]);