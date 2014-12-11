var angular = angular || {};

angular.module("worklist")
    .service("alertList", function() {
        var list = [];
        
        return list;
    })
    
    .service("tkUserService", ["$http", function($http) {
        var service = {
            currentUser: null,
            loggedIn: false
        }
        $http.get("/api/user/me").then(function(response) {
            service.currentUser = response.data;
            service.loggedIn = true;
        });
        
        return service;
    }])
;
