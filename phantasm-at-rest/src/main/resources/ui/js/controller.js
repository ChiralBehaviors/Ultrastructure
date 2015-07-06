myApp.controller('MasterDetailCtrl',
    function ($scope, $http) {
        $scope.selectedCustomer = null;

        $scope.listOfOrders = null;

        $http.get('http://www.iNorthwind.com/Service1.svc/getAllCustomers')
            .success(function (data) {
                console.log('success!');
                $scope.listOfCustomers = data.GetAllCustomersResult;

                if ($scope.listOfCustomers.length > 0) {
                    $scope.selectedCustomer = $scope.listOfCustomers[0].CustomerID;

                    $scope.loadOrders();
                }
            })
            .error(function (data, status, headers, config) {
                $scope.errorMessage = "Couldn't load list of custoemrs, error # " + status;
            });

        $scope.loadOrders = function () {
            $http.get('http://www.iNorthwind.com/Service1.svc/getBasketsForCustomer/' + $scope.selectedCustomer)
                .success(function (data) {
                    $scope.listOfOrders = data.GetBasketsForCustomerResult;
                })
                .error(function (data, status, headers, config) {
                    $scope.errorMessage = "Couldn't load the list of Orders, error # " + status;
                });
        };
    });
