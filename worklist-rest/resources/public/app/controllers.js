var angular = angular || {};

angular.module("worklist.nav", [])
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
              })
          });
    }])