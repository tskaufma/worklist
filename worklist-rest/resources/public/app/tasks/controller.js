var angular = angular || {};

angular.module('tasks.controller', ['tasks.service', 'projects.service'])
    .controller('TaskController', ['$scope', '$filter', 'taskList', 'tasksResource', 'projectsResource', 'alertList', 
    function($scope, $filer, taskList, tasksResource, projectsResource, alertList) {
        
        $scope.tasks = taskList;
        $scope.newTask = {};
        $scope.taskText = '';
        $scope.projects = [];
        projectsResource.listProjects().then(function (projects) {
            $scope.projects = projects;
        });
        
        $scope.pager = {
            pageSize: 10,
            currentPage: 0,
            
            numberOfPages: function() {
                return $scope.tasks.length === 0 ? 1 :  Math.ceil($scope.tasks.length / this.pageSize);
            },
            
            pageStart: function() {
                return $scope.tasks.length === 0 ? 0 : this.currentPage*this.pageSize + 1;
            },
            pageEnd: function() {
                return Math.min((this.currentPage+1)*this.pageSize,$scope.tasks.length);
            },
            
            prevPage: function() {
                this.currentPage = this.currentPage - 1;
                if (this.currentPage < 0)
                    this.currentPage = 0;
            },
            nextPage: function() {
                this.currentPage = this.currentPage + 1;
                if (this.currentPage >= this.numberOfPages())
                    this.currentPage = this.numberOfPages() - 1;
            },
            canPrev: function() {
                return this.currentPage > 0;
            },
            canNext: function() {
                return this.currentPage < this.numberOfPages() - 1;
            }
        };
        
        $scope.range = function (start, end) {
            var ret = [];
            if (!end) {
                end = start;
                start = 0;
            }
            for (var i = start; i < end; i++) {
                ret.push(i);
            }
            return ret;
        };
        
        $scope.addTask = function() {
            if ($scope.taskText !== '') {
                console.log("Quick Task");
                tasksResource.newTask({title:$scope.taskText}).then(function(task) {
                    $scope.tasks.push(task);
                    alertList.success("Task " + task.name + " added successfully.");
                });
                $scope.taskText = '';
            } else {
                console.log($scope.newTask);
                tasksResource.newTask($scope.newTask).then(function(task) {
                    $scope.tasks.push(task);
                    alertList.success("Task " + task.name + " added successfully.");
                });
                $scope.newTask = {};
                $('#newTaskModal').foundation('reveal', 'close');
            }
        };
        
        $scope.newTaskWindow = function() {
            $('#newTaskModal').foundation('reveal', 'open');
        };
        
    }])
    
    .controller('TaskDetailController', ['$scope', 'task', 'taskResource', 'projectsResource', 'alertList', 
    function($scope, task, taskResource, projectsResource, alertList) {
        $scope.newTask = task;
        $scope.projects = [];
        projectsResource.listProjects().then(function (projects) {
            $scope.projects = projects;
        });
        
        $scope.editTaskWindow = function() {
            $('#editTaskModal').foundation('reveal', 'open');
        };
        
        $scope.editTask = function() {
           
                console.log($scope.newTask);
                taskResource.updateTask($scope.newTask.id, $scope.newTask).then(function(task) {
                    $scope.newTask = task;
                    alertList.success("Task " + task.name + " saved successfully.");
                });
                
                $('#editTaskModal').foundation('reveal', 'close');
                
        };
    }])
;
