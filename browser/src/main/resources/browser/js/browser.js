var browser = angular.module('browser', [ 'ngRoute',
		'browserControllers', 'jsonFormatter' ]);

browser.config([ '$routeProvider', function($routeProvider) {
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
	});
	$routeProvider.otherwise({
		redirectTo : '/facet'
	});
} ]);