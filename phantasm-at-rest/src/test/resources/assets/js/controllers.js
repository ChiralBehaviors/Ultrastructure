var usBrowserControllers = angular.module('usBrowserControllers', []);

usBrowserControllers.controller('FacetInstancesListCtrl', [
		'$scope',
		'Facet',
		'$routeParams',
		function($scope, Facet, $routeParams) {
			Facet.one($routeParams.ruleform).one($routeParams.classifier).one(
					$routeParams.classification).get().then(function(data) {
				$scope.facetInstances = data.instances;
			});
		} ]);

usBrowserControllers.controller('FacetListCtrl', [ '$scope', 'Facet',
		'$routeParams', function($scope, Facet, $routeParams) {
			Facet.one($routeParams.ruleform).get().then(function(data) {
				$scope.facets = data.facets;
			});
		} ]);

usBrowserControllers.controller('FacetRuleformsListCtrl', [ '$scope', 'Facet',
		function($scope, Facet) {
			Facet.one().get().then(function(data) {
				$scope.facetRuleforms = data.ruleforms;
			});
		} ]);

usBrowserControllers.controller('FacetInstanceDetailCtrl', [
		'$scope',
		'Facet',
		'$routeParams',
		function($scope, Facet, $routeParams) {
			Facet.one($routeParams.ruleform).one($routeParams.classifier).one($routeParams.classification).one($routeParams.instance)
					.get().then(function(data) {
						$scope.facetInstance = data;
					});
		} ]);