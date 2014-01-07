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
