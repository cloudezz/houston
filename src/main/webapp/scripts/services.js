'use strict';

/* Services */

houstonApp.factory('Account', ['$resource',
    function ($resource) {
        return $resource('app/rest/account', {}, {
        });
    }]);

houstonApp.factory('Password', ['$resource',
    function ($resource) {
        return $resource('app/rest/account/change_password', {}, {
        });
    }]);

houstonApp.factory('Sessions', ['$resource',
    function ($resource) {
        return $resource('app/rest/account/sessions/:series', {}, {
            'get': { method: 'GET', isArray: true}
        });
    }]);

houstonApp.factory('Metrics', ['$resource',
    function ($resource) {
        return $resource('metrics/metrics', {}, {
            'get': { method: 'GET'}
        });
    }]);

houstonApp.factory('HealthCheckService', ['$rootScope', '$http',
    function ($rootScope, $http) {
        return {
            check: function() {
                var promise = $http.get('metrics/healthcheck').then(function(response){
                    return response.data;
                });
                return promise;
            }
        };
    }]);

houstonApp.factory('LogsService', ['$resource',
    function ($resource) {
        return $resource('app/rest/logs', {}, {
            'findAll': { method: 'GET', isArray: true},
            'changeLevel':  { method: 'PUT'}
        });
    }]);

houstonApp.factory('SignUpService', ['$resource',
   function ($resource) {
       return $resource('app/rest/signup', {}, {
           'signUp': { method: 'POST'}
       });
   }]);

houstonApp.factory('AuditsService', ['$http',
    function ($http) {
        return {
            findAll: function() {
                var promise = $http.get('app/rest/audits/all').then(function (response) {
                    return response.data;
                });
                return promise;
            },
            findByDates: function(fromDate, toDate) {
                var promise = $http.get('app/rest/audits/byDates', {params: {fromDate: fromDate, toDate: toDate}}).then(function (response) {
                    return response.data;
                });
                return promise;
            }
        }
    }]);

houstonApp.factory('AuthenticationSharedService', ['$rootScope', '$http', 'authService',
    function ($rootScope, $http, authService) {
        return {
            authenticate: function() {
               var promise = $http.get('app/rest/authenticate')
                    .success(function (response) {
                        return response.data;
                    });
                return promise;
            },
            login: function (param) {
                var data ="j_username=" + param.username +"&j_password=" + param.password +"&_spring_security_remember_me=" + param.rememberMe +"&submit=Login";
                $http.post('app/authentication', data, {
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    ignoreAuthModule: 'ignoreAuthModule'
                }).success(function (data, status, headers, config) {
                    $rootScope.authenticationError = false;
                    if(param.success){
                        param.success(data, status, headers, config);
                    }
                }).error(function (data, status, headers, config) {
                    $rootScope.authenticationError = true;
                    if(param.error){
                        param.error(data, status, headers, config);
                    }
                });
            },
            logout: function () {
                $rootScope.authenticationError = false;
                $http.get('app/logout')
                    .success(function (data, status, headers, config) {
                        authService.loginCancelled();
                    });
            }
        };
    }]);

houstonApp.factory('AppImageCfg', ['$resource',
   function ($resource) {
       return $resource('app/rest/appimagecfgs/:id', {id:'@id'}, {
           'query': { method: 'GET', isArray: true},
           'get': { method: 'GET'}
       });
   }]);


houstonApp.factory('AppImageService', ['$http',
     function ($http) {
         return {
        	 start: function(id, callback) {
        		 var url = 'app/rest/appimagecfgs/start/' + id
                 $http.post(url).success(function (data, status){
                	 callback(data)
                 }).error(function (data, status){
        			 
                 })
             }
         }
     }]);




houstonApp.factory('ServiceImageCfg', ['$resource',
   function ($resource) {
       return $resource('app/rest/serviceimagecfgs/:id', {}, {
           'query': { method: 'GET', isArray: true},
           'get': { method: 'GET'}
       });
   }]);
