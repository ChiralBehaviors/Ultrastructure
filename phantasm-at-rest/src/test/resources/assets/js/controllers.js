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

usBrowserControllers.controller('menuCtrl', function($scope, $http) {
	var urlRegEx = /^https?:\/\//
	$scope.type = function(thing) {
		switch (typeof thing) {
		case "object":
			if (Object.prototype.toString.call(thing) === "[object Array]") {
				return 'array'
			} else if (thing == null) {
				return 'null'
			} else {
				return 'hash'
			}
		case "string":
			if (urlRegEx.test(thing)) {
				return "url"
			} else {
				return "string"
			}
		default:
			return typeof thing
		}
	}

	$http.get("data.json").then(function(response) {
		$scope.value = response.data
	});

	window.sc = $scope
});