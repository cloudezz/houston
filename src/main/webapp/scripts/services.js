'use strict';

/* Services */

houstonApp.factory('Account', ['$resource',
    function ($resource) {
        return $resource('app/rest/account', {}, {
        });
    }]);


//houstonApp.factory('Password', ['$resource',
//    function ($resource) {
//        return $resource('app/rest/account/change_password', {}, {
//        });
//    }]);

houstonApp.factory('Password', ['$resource', '$http',
    function ($resource , $http) {
        return {

        	changePwd : function(password, callback) {
        		$http.post('app/rest/account/change_password', password).success(function (data, status){
               	 callback(data, status);
                }).error(function (data, status){
               	 callback(data, status);
                });
        	},
        	
        	setPwd : function(id, password, callback) {
        		$http.post('app/rest/account/set_password/'+id, password).success(function (data, status){
               	 callback(data, status);
                }).error(function (data, status){
               	 callback(data, status);
                });
        	},
        }; 
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


houstonApp.factory('SignUpService', ['$http',
   function ($http) {
       return  {
    	   signUp : function (email, callback) {
    		   var url = 'app/rest/signup?email='+email;
    		   $http.post(url).success(function (data, status){
              	 callback(data, status);
               }).error(function (data, status){
              	 callback(data, status);
               });
    	   }
       };
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
                	if(data.message == "Account inactive"){
                		 $rootScope.accountInActiveError =true;
                		 $rootScope.authenticationError = false;
                	}else{
                		 $rootScope.authenticationError = true;
                		 $rootScope.accountInActiveError =false;
                	}
                	
                    if(param.error){
                        param.error(data, status, headers, config);
                    }
                });
            },
            logout: function () {
                $rootScope.authenticationError = false;
                $rootScope.accountInActiveError =false;
                $http.get('app/logout')
                    .success(function (data, status, headers, config) {
                        authService.loginCancelled();
                    });
            }
        };
    }]);


houstonApp.factory('ImageInfo', ['$resource',
 function ($resource) {
     return $resource('app/rest/imageinfos/:id', {}, {
         'query': { method: 'GET', isArray: true},
         'get': { method: 'GET'}
     });
 }]);

houstonApp.factory('ServiceImageInfo', [ '$resource', function($resource) {
	return $resource('app/rest/imageinfos/type/service', {}, {
		'query' : {
			method : 'GET',
			isArray : true
		},
		'get' : {
			method : 'GET'
		}
	});
} ]);


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
                	 callback(data, status)
                 }).error(function (data, status){
                	 callback(data, status)
                 })
             },
             
        	 stop: function(id, callback) {
        		 var url = 'app/rest/appimagecfgs/stop/' + id
                 $http.post(url).success(function (data, status){
                	 callback(data, status)
                 }).error(function (data, status){
                	 callback(data, status)
                 })
             },
			loadForm : function(id) {
				var form=$http.get('app/rest/imageInfos/form/'+id).then(
						function(response) {
							return response.data;
						});
				return form;
			},
			saveFormValues:function(formData){
				$http.post('app/rest/saveForm',formData);
			},
			
			listServices : function (appName, callback) {
				var url = "app/rest/appimagecfgs/"+appName+"/service"
				$http.get(url).success(function (data) {
					callback(data);
				});
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
houstonApp.factory('uploadManager', function ($rootScope, $http) {
    var _files = [];
    return {
        add: function (file) {
            _files.push(file);
            $rootScope.$broadcast('fileAdded', file.files[0].name);
        },
        clear: function () {
            _files = [];
        },
        files: function () {
            var fileNames = [];
            $.each(_files, function (index, file) {
                fileNames.push(file.files[0].name);
            });
            return fileNames;
        },
        upload: function () {
            $.each(_files, function (index, file) {
            	file.submit();
            });
            this.clear(); 
        },
        setProgress: function (percentage) {
            $rootScope.$broadcast('uploadProgress', percentage);
        }
    };
});
