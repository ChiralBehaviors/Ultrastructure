var usBrowser = angular.module('usBrowser', [
  'ngRoute',
  'usBrowserControllers'
]);

usBrowser.config(['$routeProvider',
                    function($routeProvider) {
                      $routeProvider.
                        when('/json-ld/facet', {
                          templateUrl: 'partials/facet-ruleforms.html',
                          controller: 'FacetRuleformsListCtrl'
                        }).
                        when('/json-ld/facet/:ruleform', {
                          templateUrl: 'partials/all-facet-instances.html',
                          controller: 'FacetListCtrl'
                        }).
                        when('/json-ld/facet/:ruleform/:instance', {
                          templateUrl: 'partials/facet-detail.html',
                          controller: 'FacetDetailCtrl'
                        })
                        $routeProvider.otherwise({redirectTo: '/json-ld/facet'});
                    }]);