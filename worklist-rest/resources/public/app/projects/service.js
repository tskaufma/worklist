var angular = angular || {};

angular.module("projects.service", [])
    .service("projectsResource", ['$http', function($http) {
        return {
            newProject: function(project)  {
               return $http.post("api/projects", project).then(function(response) {
                   console.log("newProject success");
                   return response.data;
                }); 
            },
            listProjects: function() {
               return $http.get("api/projects").then(function(response) {
                   console.log("List Projects Success");
                   //this.projects.splice(0, this.projects.length);
                   var projects = [];
                   response.data.forEach(function(item) {
                        projects.push(item);
                   });
                   return projects;
                });
            }
        };
    }])
    .service("projectResource", ['$http', function($http) {
        return {
            getProject: function(id)  {
               var promise = $http.get("api/project/" + id).then(function(response) {
                    console.log("get Project success");
                    var project = response.data;
                    return project;
                }); 
                return promise;
            },
            updateProject: function(id, project) {
                return $http.put("api/project/" + id, project).then(function(response) {
                    console.log("update project success");
                    return response.data;
                });
            }
        };
    }])
;
