var usBrowserControllers = angular.module('usBrowserControllers',
		[ "phantasm" ]);

usBrowserControllers
		.controller(
				'FacetInstancesListCtrl',
				[
						'$scope',
						'Phantasm',
						'PhantasmRelative',
						'$routeParams',
						function($scope, Phantasm, PhantasmRelative,
								$routeParams) {
							Phantasm
									.facetInstances($routeParams.ruleform,
											$routeParams.classifier,
											$routeParams.classification)
									.get()
									.then(
											function(data) {
												for ( var i in data.instances) {
													data.instances[i]["@id"] = PhantasmRelative
															.fullyQualifiedInstance(data.instances[i]["@id"]);
												}
												$scope.facetInstances = data.instances;
											});
						} ]);

usBrowserControllers.controller('FacetListCtrl', [
		'$scope',
		'Facet',
		'PhantasmRelative',
		'$routeParams',
		function($scope, Facet, PhantasmRelative, $routeParams) {
			Facet.one($routeParams.ruleform).get().then(
					function(data) {
						for (var i = 0; i < data.facets.length; i++) {
							data.facets[i].instances = PhantasmRelative
									.facetInstances(data.facets[i].instances);
						}
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