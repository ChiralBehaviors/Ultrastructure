var usBrowser = angular.module('usBrowser', [ 'ngRoute',
		'usBrowserControllers', 'jsonFormatter', 'restangular' ]);

usBrowser.config([ '$routeProvider', "RestangularProvider",
		function($routeProvider, RestangularProvider) {
			RestangularProvider.setBaseUrl("/json-ld");
			$routeProvider.when('/facet', {
				templateUrl : 'partials/facet-ruleforms.html',
				controller : 'FacetRuleformsListCtrl'
			}).when('/facet/:ruleform', {
				templateUrl : 'partials/facets.html',
				controller : 'FacetListCtrl'
			}).when('/facet/:ruleform/:classifier/:classification', {
				templateUrl : 'partials/facet-instances.html',
				controller : 'FacetInstancesListCtrl'
			}).when('/facet/:ruleform/:classifier/:classification/:instance', {
				templateUrl : 'partials/facet-instance-detail.html',
				controller : 'FacetInstanceDetailCtrl'
			})
			$routeProvider.otherwise({
				redirectTo : '/facet'
			});
		} ]);

usBrowser.factory("Facet", [ "Restangular", function(Restangular) {
	var service = Restangular.service("facet");
	return service;
} ]);