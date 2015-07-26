var usBrowserControllers = angular.module('usBrowserControllers',
		[ "phantasm" ]);

usBrowserControllers.controller('FacetInstancesListCtrl', [
		'$scope',
		'Phantasm',
		'$routeParams',
		function($scope, Phantasm, $routeParams) {
			Phantasm.facetInstances($routeParams.ruleform, $routeParams.classifier,
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
		'Phantasm',
		'$routeParams',
		function($scope, Phantasm, $routeParams) {
			Phantasm.facetInstance($routeParams.ruleform,
					$routeParams.classifier, $routeParams.classification,
					$routeParams.instance).get().then(function(data) {
				$scope.facetInstance = data.plain();
			});
		} ]);