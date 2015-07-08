myApp.controller('JsonController',
    function ($scope, $http) {
        $scope.test = 'Hello: "World"';
        $scope.json = null;
        $http.get('/json-ld/ruleform/context/Product')
            .success(function (data) {
                console.log('success!');
                $scope.json = data;
            })
            .error(function (data, status, headers, config) {
                $scope.errorMessage = "Couldn't load json, error # " + status;
            });

    });
