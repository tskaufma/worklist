var angular = angular || {};

angular.module("worklist.controllers", [])
    .controller("NavigationCtrl", ["$scope", "$location", function($scope, $location) {
        $scope.links = [{
            name: "Projects",
            url: "/projects",
            active: false
        },
        {
            name: "Tasks",
            url: "/tasks",
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
    
    .controller("ApplicationCtrl", ["$scope", "tkUserService", "typelistService", function($scope, tkUserService, typelistService) {
        $scope.user = tkUserService;
        
        typelistService.get("priority").then(function(typelist) {
            $scope.priority = typelist;
        });
        
        typelistService.get("status").then(function(typelist){
            $scope.status =  typelist;
        });
        
        typelistService.get("resolution").then(function(typelist) {
            $scope.resolution = typelist;
        });
        
    }])
;
