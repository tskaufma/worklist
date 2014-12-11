var angular = angular || {};

angular.module("people.service", []) 
    .service("peopleResource", ['$http', function($http) {
        return {
            newperson: function(person)  {
               return $http.post("/api/people", person).then(function(response) {
                   console.log("newperson success");
                   return response.data;
                }); 
            },
            listpeople: function() {
               return $http.get("/api/people").then(function(response) {
                   console.log("List people Success");
                   //this.people.splice(0, this.people.length);
                   var people = [];
                   response.data.forEach(function(item) {
                        people.push(item);
                   });
                   return people;
                });
            }
        };
    }])
    .service("personResource", ['$http', function($http) {
        return {
            getperson: function(id)  {
               var promise = $http.get("/api/person/" + id).then(function(response) {
                    console.log("get person success");
                    var person = response.data;
                    return person;
                }); 
                return promise;
            },
            updateperson: function(id, person) {
                return $http.put("/api/person/" + id, person).then(function(response) {
                    console.log("update person success");
                    return response.data;
                });
            }
        };
    }])
;
