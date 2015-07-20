var usBrowser = angular.module('usBrowser',
		[ 'ngRoute', 'usBrowserControllers' ]);

usBrowser.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/json-ld/facet', {
		templateUrl : 'partials/facet-ruleforms.html',
		controller : 'FacetRuleformsListCtrl'
	}).when('/json-ld/facet/:ruleform', {
		templateUrl : 'partials/all-facets.html',
		controller : 'FacetListCtrl'
	}).when('/json-ld/facet/:ruleform/:classifier/:classification', {
		templateUrl : 'partials/all-facet-instances.html',
		controller : 'FacetDetailCtrl'
	}).when('/json-ld/facet/:ruleform/:classifier/:classification/:instance', {
		templateUrl : 'partials/facet-instance-detail.html',
		controller : 'FacetInstanceDetailCtrl'
	})
	$routeProvider.otherwise({
		redirectTo : '/json-ld/facet'
	});
} ]);