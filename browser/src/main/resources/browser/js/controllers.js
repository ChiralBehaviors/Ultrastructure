var browserControllers = angular.module('browserControllers', [ "phantasm" ]);

browserControllers.controller('FacetInstancesListCtrl', [
		'$scope',
		'Phantasm',
		'$routeParams',
		function($scope, Phantasm, $routeParams) {
			Phantasm.facetInstances($routeParams.ruleform,
					$routeParams.classifier, $routeParams.classification).get()
					.then(
							function(data) {
								$scope.facet = $routeParams.ruleform + "/"
										+ $routeParams.classifier + "/"
										+ $routeParams.classification;
								$scope.facetInstances = data["@graph"];
							});
		} ]);

browserControllers.controller('FacetListCtrl', [ '$scope', 'Facet',
		'$routeParams', function($scope, Facet, $routeParams) {
			Facet.one($routeParams.ruleform).get().then(function(data) {
				$scope.facets = data["@graph"];
			});
		} ]);

browserControllers.controller('FacetRuleformsListCtrl', [ '$scope', 'Facet',
		function($scope, Facet) {
			Facet.one().get().then(function(data) {
				$scope.facetRuleforms = data.ruleforms;
			});
		} ]);

browserControllers.controller('FacetInstanceDetailCtrl', [
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