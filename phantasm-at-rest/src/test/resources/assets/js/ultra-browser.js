var usBrowser = angular.module('usBrowser', [ 'ngRoute',
		'usBrowserControllers', 'jsonFormatter' ]);

usBrowser.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/facet', {
		templateUrl : 'partials/facet-ruleforms.html',
		controller : 'FacetRuleformsListCtrl'
	}).when('/facet/:ruleform', {
		templateUrl : 'partials/facets.html',
		controller : 'FacetListCtrl'
	}).when('/facet/:ruleform/:classifier/:classification/instances', {
		templateUrl : 'partials/facet-instances.html',
		controller : 'FacetInstancesListCtrl'
	}).when('/facet/:ruleform/:classifier/:classification/:instance', {
		templateUrl : 'partials/facet-instance-detail.html',
		controller : 'FacetInstanceDetailCtrl'
	});
	$routeProvider.otherwise({
		redirectTo : '/facet'
	});
} ]);