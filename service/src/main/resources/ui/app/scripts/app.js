'use strict';

angular.module('uiApp', [
  'ngCookies',
  'ngResource',
  'ngSanitize',
  'ngRoute'
])
  .config(function ($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'views/new.html',
        controller: 'NewCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  });

angular.module('apiApp', [
      'ngResource', 
      'ngRoute'
])
    .config(function($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'views/exis_ruleform.html',
                controller: 'ERListViewCtrl',
                isArray: 'true'
            })
            .otherwise({
                redirectTo: '/'
            });
    });  