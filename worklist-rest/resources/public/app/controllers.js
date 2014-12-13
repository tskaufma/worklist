var angular = angular || {};

angular.module("worklist.controllers", [])
    .controller("NavigationCtrl", ["$scope", "$location", function($scope, $location) {
        $scope.links = [{
            name: "Projects",
            url: "#/projects",
            active: false
        },
        {
            name: "Tasks",
            url: "#/tasks",
            active: true
        }];
        
        $scope.$watch(function () {
            return $location.path();
          }, function (path) {
              $scope.links.forEach(function(link) {
                  if (link.url === path) {
                      link.active = true;
                  } else {
                      link.active = false;
                  }
              });
          });
    }])
    
    // Alert Controller for Flash messages on screen.
    .controller("PageAlertCtrl", ["$scope", "alertList", function($scope, alertList) {
        $scope.alerts = alertList.list;
        
        $scope.closeAlert = function(index) {
            alertList.list.splice(index, 1);
        };
        
        alertList.info("Hello, World!");

    }])
    
    .controller("HeaderUserCtrl", ["$scope", "tkUserService", function($scope, tkUserService) {
        $scope.user = tkUserService;
        
    }])
;
