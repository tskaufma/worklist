var angular = angular || {};

angular.module("tasks.service", [])
    .service("tasksResource", ['$http', function($http) {
        return {
            newTask: function(task)  {
               return $http.post("api/tasks", task).then(function(response) {
                   console.log("newTask success");
                   return response.data;
                }); 
            },
            listTasks: function() {
               return $http.get("api/tasks").then(function(response) {
                   console.log("List Tasks Success");
                   //this.tasks.splice(0, this.tasks.length);
                   var tasks = [];
                   response.data.forEach(function(item) {
                        tasks.push(item);
                   });
                   return tasks;
                });
            }
        };
    }])
    .service("taskResource", ['$http', function($http) {
        return {
            getTask: function(id)  {
               var promise = $http.get("api/task/" + id).then(function(response) {
                    console.log("get Task success");
                    var task = response.data;
                    return task;
                }); 
                return promise;
            },
            updateTask: function(id, task) {
                return $http.put("api/task/" + id, task).then(function(response) {
                    console.log("update task success");
                    return response.data;
                });
            }
        };
    }])
;
