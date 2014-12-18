var angular = angular || {};

angular.module("worklist")
    .service("alertList", ["$timeout", function($timeout) {
        var list = [];
        
        return {
            list: list,
            error : function(message) {
                this.addAlert("alert", message);
            },
            warn : function(message) {
                this.addAlert("warning", message);
            },
            info : function(message) {
                this.addAlert("info", message);
            },
            success : function(message) {
                this.addAlert("success", message);
            },
            addAlert : function(type , message) {
                var alert = {
                    type: (type ||  "") + " radius",
                    message: message
                };
                list.push(alert);
                $timeout(function() {
                    var idx = list.indexOf(alert);
                    if  (idx >= 0) {
                        list.splice(idx, 1);
                    }
                }, 5000);
            }
        };
    }])
    
    .service("tkUserService", ["$http", "$window", function($http, $window) {
        var service = {
            currentUser: null,
            loggedIn: false
        };
        $http.get("/api/user/me").then(function(response) {
            service.currentUser = response.data;
            service.loggedIn = true;
        }).catch(function(error) {
            $window.location.href = '/login';
        });
        
        return service;
    }])
    
    .service ("typelistService", ["$http", function($http) {
        return {
            get : function(typelist) {
                return $http.get("/api/typelist/" + typelist).then(function (response) {
                    return response.data;
                });
            }
        };
    }])
;
