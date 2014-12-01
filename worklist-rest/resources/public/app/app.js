var angular = angular || {};

// Declare app level module which depends on filters, and services
angular.module('worklist', ['ngRoute', 'worklist.filter', 'worklist.nav', 'tasks.service', 'tasks.controller', 'projects.service', 'projects.controller', 'mm.foundation'])
.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    $routeProvider.when('/tasks', {
        templateUrl: 'app/partials/task-list.html',
        controller: 'TaskController',
        resolve: {
            taskList: ['tasksResource', function(tasksResource) {
                return tasksResource.listTasks();
            }]
        }
    });
    
    $routeProvider.when('/task/:taskId', {
        templateUrl: 'app/partials/task-page.html',
        controller: 'TaskDetailController',
        resolve: {
            task: ['$route', 'taskResource', function($route, taskResource) {
                return taskResource.getTask($route.current.params.taskId);
            }]
        }
    });
    
    /* Project Routes */
    $routeProvider.when('/projects', {
        templateUrl: 'app/partials/project-list.html',
        controller: 'ProjectController',
        resolve: {
            projectList: ['projectsResource', function(projectsResource) {
                return projectsResource.listProjects();
            }]
        }
    });
    
    $routeProvider.when('/project/:projectId', {
        templateUrl: 'app/partials/project-page.html',
        controller: 'ProjectDetailController',
        resolve: {
            project: ['$route', 'projectResource', function($route, projectResource) {
                return projectResource.getProject($route.current.params.projectId);
            }]
        }
    });
    /*
    $routeProvider.when('/movie/:movieId', {
        templateUrl: 'partials/partial2.html',
        controller: 'MyCtrl2',
        resolve: {
            movie: ['$route', 'movieFinder', function($route, movieFinder) {
                return movieFinder.getById($route.current.params.movieId);
            }]
        }
    });
    $routeProvider.when('/about', {
        templateUrl: 'partials/about.html',
        controller: 'AboutCtrl'
    });
    */
    $routeProvider.otherwise({
        redirectTo: '/tasks'
    });
    $locationProvider.html5Mode(true);
}]);

angular.module("tasks", []);
angular.module("projects", []);
