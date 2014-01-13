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
                templateUrl: 'views/exis_ruleform_list.html',
                controller: 'ERListViewCtrl',
                isArray: 'true'
            })
            .when('/:ruleform/:id', {
                templateUrl: 'views/exis_ruleform_detail.html',
                controller: 'ERDetailViewController'
            })
            .otherwise({
                redirectTo: '/'
            });
    });  