var angular = angular || {};

angular.module('projects.controller', ['projects.service', 'worklist.services'])
    .controller('ProjectController', ['$scope', '$filter', 'projectList', 'projectsResource', 'alertList', function($scope, $filer, projectList, projectsResource, alertList) {
        
        $scope.projects = projectList;
        $scope.newProject = {};
        $scope.projectText = '';
        
        $scope.pager = {
            pageSize: 10,
            currentPage: 0,
            
            numberOfPages: function() {
                return $scope.projects.length === 0 ? 1 :  Math.ceil($scope.projects.length / this.pageSize);
            },
            
            pageStart: function() {
                return $scope.projects.length === 0 ? 0 : this.currentPage*this.pageSize + 1;
            },
            pageEnd: function() {
                return Math.min((this.currentPage+1)*this.pageSize,$scope.projects.length);
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
        
        $scope.addProject = function() {
            if ($scope.projectText !== '') {
                console.log("Quick Project")
                projectsResource.newProject({name:$scope.projectText}).then(function(project) {
                    $scope.projects.push(project);
                    alertList.push({type: "info", message: "Project " + project.name + " created successfully."})
                }).catch(function (error) {
                    console.log(error);
                    console.log("new project failed.")
                    alertList.push({type: "error", message: "Failed to created project. " + error.error});
                });
                $scope.projectText = '';
            } else {
                console.log($scope.newProject);
                projectsResource.newProject($scope.newProject).then(function(project) {
                    $scope.projects.push(project);
                });
                $scope.newProject = {};
                $('#newProjectModal').foundation('reveal', 'close');
            }
        };
        
        $scope.newProjectWindow = function() {
            $('#newProjectModal').foundation('reveal', 'open');
        };
        
    }])
    
    .controller('ProjectDetailController', ['$scope', 'project', 'projectResource', function($scope, project, projectResource) {
        $scope.newProject = project;
        
        $scope.editProjectWindow = function() {
            $('#editProjectModal').foundation('reveal', 'open');
        };
        
        $scope.editProject = function() {
           
                console.log($scope.newProject);
                projectResource.updateProject($scope.newProject.id, $scope.newProject).then(function(project) {
                    $scope.newProject = project;
                });
                
                $('#editProjectModal').foundation('reveal', 'close');
                
        };
    }])
;
