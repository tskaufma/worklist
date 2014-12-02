var angular = angular || {};

angular.module("worklist.nav", ['worklist.services', 'mm.foundation'])
    .controller("NavigationController", ["$scope", "$location", function($scope, $location) {
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
    .controller("AlertController", ["$scope", "alertList", function($scope, alertList) {
        $scope.alerts = alertList;
        
        $scope.closeAlert = function(index) {
            alertList.splice(index, 1);
        };
        
        // test only
        $scope.addAlert = function() {
            alertList.push({type: "alert", message: "Another alert!"});
        };

    }])
;

angular.module("worklist.services", [])
    .service("alertList", function() {
        var list = [{type: "info", message: "initial alert"}];
        
        return list;
    })
;
