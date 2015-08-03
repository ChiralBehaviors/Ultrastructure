var browserControllers = angular.module('browserControllers', [ "phantasm" ]);

browserControllers.controller('FacetRuleformsListCtrl', [ '$scope', 'Facet',
		function($scope, Facet) {
			Facet.one().get().then(function(data) {
				$scope.facetRuleforms = data.ruleforms;
			});
		} ]);

browserControllers.controller('FacetListCtrl', [ '$scope', 'Facet',
		'$routeParams', function($scope, Facet, $routeParams) {
			Facet.one($routeParams.ruleform).get().then(function(data) {
				$scope.facets = data["@graph"];
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
				$scope.instance = data.plain();
			});
		} ]);

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

browserControllers.controller('RuleformListCtrl', [ '$scope',
		'RuleformResource', function($scope, RuleformResource) {
			RuleformResource.one().get().then(function(data) {
				$scope.ruleforms = data;
			});
		} ]);

browserControllers.controller('RuleformInstanceDetailCtrl', [
		'$scope',
		'Ruleform',
		'$routeParams',
		function($scope, Ruleform, $routeParams) {
			Ruleform.instance($routeParams.ruleform, $routeParams.instance)
					.get().then(function(data) {
						$scope.instance = data.plain();
					});
		} ]);

browserControllers.controller('RuleformInstancesListCtrl', [
		'$scope',
		'Ruleform',
		'$routeParams',
		function($scope, Ruleform, $routeParams) {
			Ruleform.instances($routeParams.ruleform).get().then(
					function(data) {
						$scope.ruleform = $routeParams.ruleform;
						$scope.ruleformInstances = data;
					});
		} ]);

browserControllers.controller('FacetController', [
		'$scope',
		'Facet',
		'$routeParams',
		function($scope, RoutedFacet, $routeParams) {
			$scope.listOfFacets = null;
			$scope.selectedFacet = null;
			$scope.selectedInstance = null;
			Facet.one($routeParams.ruleform).getList().then(function(data) {
				$scope.listOfFacets = data['@graph'];

				if ($scope.listOfFacets.length > 0) {
					$scope.selectedFacet = $scope.listOfFacets[0]["@id"];
					$scope.loadInstances();
				}
			});

			$scope.selectFacet = function(val) {
				$scope.selectedFacet = val["@id"];
				$scope.loadInstances();
			};

			$scope.loadInstances = function() {
				$scope.listOfInstances = null;
				$scope.selectedInstance = null;
				Facet.one($routeParams.ruleform).all($scope.selectedFacet).one(
						"instances").get().then(function(data) {
					$scope.instances = data['@graph'];

					if ($scope.instances.length > 0) {
						$scope.selectedInstance = $scope.instances[0]["@id"];
						$scope.loadInstance();
					}
				});
			};

			$scope.selectInstance = function(val) {
				$scope.selectedInstance = val["@id"];
				$scope.loadInstance();
			};

			$scope.loadInstance = function() {
				Facet.one($routeParams.ruleform).all($scope.selectedFacet).one(
						$scope.selectedInstance).get().then(function(data) {
					$scope.instances = data['@graph'];
				});
			};
		} ]);