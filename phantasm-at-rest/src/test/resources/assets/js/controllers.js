var usBrowserControllers = angular.module('usBrowserControllers', []);

function isObject(obj) {
	return obj === Object(obj);
}

var r = new RegExp('^(?:[a-z]+:)?//', 'i');

function relativize(data) {
	for ( var key in data) {
		var prop = data[key];
		if (isObject(prop)) {
			relativize(prop);
		} else if (r.test(prop)) {
			var parser = document.createElement('a');
			parser.href = prop;
			data[key] = parser.pathname;
		}
	}
}

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
						for ( var idx in data) {
							relativize(data[idx]);
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
				relativize(data);
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
				relativize(data);
				$scope.facetInstance = data;
			});
		} ]);