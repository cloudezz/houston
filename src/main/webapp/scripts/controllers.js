'use strict';

/* Controllers */

houstonApp.controller('MainController', ['$scope',
    function ($scope) {
    }]);

houstonApp.controller('AdminController', ['$scope',
    function ($scope) {
    }]);

houstonApp.controller('LanguageController', ['$scope', '$translate',
    function ($scope, $translate) {
        $scope.changeLanguage = function (languageKey) {
            $translate.use(languageKey);
        };
    }]);

houstonApp.controller('MenuController', ['$scope',
    function ($scope) {
    }]);

houstonApp.controller('LoginController', ['$scope', '$location', 'AuthenticationSharedService',
    function ($scope, $location, AuthenticationSharedService) {
        $scope.rememberMe = true;
        $scope.login = function () {
            AuthenticationSharedService.login({
                username: $scope.username,
                password: $scope.password,
                rememberMe: $scope.rememberMe,
                success: function () {
                    $location.path('/appimagecfg');
                }
            });
        };
    }]);

houstonApp.controller('LogoutController', ['$location', 'AuthenticationSharedService',
    function ($location, AuthenticationSharedService) {
        AuthenticationSharedService.logout({
            success: function () {
                $location.path('');
            }
        });
    }]);


houstonApp.controller('SettingsController', ['$scope', 'Account',
    function ($scope, Account) {
        $scope.success = null;
        $scope.error = null;
        $scope.settingsAccount = Account.get();

        $scope.save = function () {
            Account.save($scope.settingsAccount,
                function (value, responseHeaders) {
                    $scope.error = null;
                    $scope.success = 'OK';
                    $scope.settingsAccount = Account.get();
                },
                function (httpResponse) {
                    $scope.success = null;
                    $scope.error = "ERROR";
                });
        };
    }]);

houstonApp.controller('PasswordController', ['$scope', 'Password',
    function ($scope, Password) {
        $scope.success = null;
        $scope.error = null;
        $scope.doNotMatch = null;
        $scope.changePassword = function () {
            if ($scope.password != $scope.confirmPassword) {
                $scope.doNotMatch = "ERROR";
            } else {
                $scope.doNotMatch = null;
                Password.save($scope.password,
                    function (value, responseHeaders) {
                        $scope.error = null;
                        $scope.success = 'OK';
                    },
                    function (httpResponse) {
                        $scope.success = null;
                        $scope.error = "ERROR";
                    });
            }
        };
    }]);

houstonApp.controller('SessionsController', ['$scope', 'resolvedSessions', 'Sessions',
    function ($scope, resolvedSessions, Sessions) {
        $scope.success = null;
        $scope.error = null;
        $scope.sessions = resolvedSessions;
        $scope.invalidate = function (series) {
            Sessions.delete({series: encodeURIComponent(series)},
                function (value, responseHeaders) {
                    $scope.error = null;
                    $scope.success = "OK";
                    $scope.sessions = Sessions.get();
                },
                function (httpResponse) {
                    $scope.success = null;
                    $scope.error = "ERROR";
                });
        };
    }]);

 houstonApp.controller('MetricsController', ['$scope', 'resolvedMetrics', 'HealthCheckService',
    function ($scope, resolvedMetrics, HealthCheckService) {
        $scope.metrics = resolvedMetrics;

        HealthCheckService.check().then(function(data) {
            $scope.healthCheck = data;
        });

        resolvedMetrics.$get({}, function(items) {
            $scope.servicesStats = {};
            $scope.cachesStats = {};
            angular.forEach(items.timers, function(value, key) {
                if (key.indexOf("web.rest") != -1) {
                    $scope.servicesStats[key] = value;
                }

                if (key.indexOf("net.sf.ehcache.Cache") != -1) {
                    // remove gets or puts
                    var index = key.lastIndexOf(".");
                    var newKey = key.substr(0, index);

                    // Keep the name of the domain
                    index = newKey.lastIndexOf(".");
                    $scope.cachesStats[newKey] = {
                        'name': newKey.substr(index + 1),
                        'value': value
                    };
                }
            });
        });
    }]);

houstonApp.controller('LogsController', ['$scope', 'resolvedLogs', 'LogsService',
    function ($scope, resolvedLogs, LogsService) {
        $scope.loggers = resolvedLogs;

        $scope.changeLevel = function (name, level) {
            LogsService.changeLevel({name: name, level: level}, function () {
                $scope.loggers = LogsService.findAll();
            });
        }
    }]);


/**
 * Signup controller that will send the request to rest services
 */
houstonApp.controller('SignUpController', ['$location', 'SignUpService',
   function ($location, SignUpService) {
		$scope.signUp = function (email) {
			SignUpService.signUp({email:email}, function(data){
				
			});
		}
	}]);
	

houstonApp.controller('AuditsController', ['$scope', '$translate', '$filter', 'AuditsService',
    function ($scope, $translate, $filter, AuditsService) {
        $scope.onChangeDate = function() {
            AuditsService.findByDates($scope.fromDate, $scope.toDate).then(function(data){
                $scope.audits = data;
            });
        };

        // Date picker configuration
        $scope.today = function() {
            // Today + 1 day - needed if the current day must be included
            var today = new Date();
            var tomorrow = new Date(today.getFullYear(), today.getMonth(), today.getDate()+1); // create
																								// new
																								// increased
																								// date

            $scope.toDate = $filter('date')(tomorrow, "yyyy-MM-dd");
        };

        $scope.previousMonth = function() {
            var fromDate = new Date();
            if (fromDate.getMonth() == 0) {
                fromDate = new Date(fromDate.getFullYear() - 1, 0, fromDate.getDate());
            } else {
                fromDate = new Date(fromDate.getFullYear(), fromDate.getMonth() - 1, fromDate.getDate());
            }

            $scope.fromDate = $filter('date')(fromDate, "yyyy-MM-dd");
        };

        $scope.today();
        $scope.previousMonth();
        
        AuditsService.findByDates($scope.fromDate, $scope.toDate).then(function(data){
            $scope.audits = data;
        });
    }]);



function QueryStringToJSON(queryStr) {            
    var pairs = queryStr.split('&');
    
    var result = {};
    pairs.forEach(function(pair) {
        pair = pair.split('=');
        result[pair[0]] = decodeURIComponent(pair[1] || '');
    });

    return JSON.parse(JSON.stringify(result));
}


houstonApp.controller('AppImageCfgController', ['$scope', '$modal' , 'resolvedAppImageCfg', 'AppImageCfg','AppImageService','ImageInfo', 
    function ($scope, $modal, resolvedAppImageCfg, AppImageCfg, AppImageService, ImageInfo) {

        $scope.appimagecfgs = resolvedAppImageCfg;
		
        $scope.service;
        $scope.startStop ="Start";
        
        $scope.started = false;
        
        $scope.appImageCfgDTO = {};
        
        var wizard;
        
		$scope.setService = function(serviceId) {
			 $scope.service = serviceId;
			 $scope.appImageCfgDTO.imageName = serviceId;
		}
		
		
        $scope.openWizard = function () {

			$scope.appimagecfg = new Object();
			$scope.appimagecfg.appName="Your App";

        	
        	$scope.serviceImages = {};
        	ImageInfo.query(function(data) {
        		$scope.serviceImages = data;
        	});
        	

			$scope.sshpwd="";
        	var options = {
    				contentWidth : 800,
    				contentHeight : 400,
    				keyboard : false,
    				backdrop : false
    			};
        		if(!wizard) {
        			wizard = $("#appImageConfigWzd").wizard(options);
        		}
        		
        		//TODO: query ImageInfo and populate all on the load of the Wizard
        		
        		
	       		wizard.show();
	       		$('.modal-backdrop').addClass();

	       		
	       		wizard.on("submit", function(wizard) {       			
	       
	       			console.log($scope.appimagecfg);
	       			
	       			// TODO: x = wizard.serialize()
	       			// convert x to json of type AppImageCfgDTO
	       			 $scope.create();
	       			
	       			$scope.appImageCfgDTO = QueryStringToJSON(wizard.serialize() );
	       			console.log( $scope.appImageCfgDTO );
	       			console.log( wizard.serialize() );
	       			
	       			
	       			$scope.create(function(){	       				
	       				wizard.trigger("success");
	       				wizard.hideButtons();
	       				wizard._submitting = false;
	       				wizard.showSubmitCard("success");
	       				wizard.updateProgressBar(0);	       				
	       			});
	       			
				});
	       		
	       		wizard.on("incrementCard", function(wizard) {
	       		var activeCard=	wizard.getActiveCard();
	       		if(activeCard.name=="card3"){
	       			$scope.loadForm();
	       		}
				});
				
	       		wizard.on("closed", function(wizard) {
	       			$('.modal-backdrop').remove();
				});
	       		
				wizard.on("reset", function(wizard) {
					wizard.setSubtitle("");
				});

				wizard.el.find(".wizard-success .im-done").click(function() {
					wizard.reset().close();
       				$('.modal-backdrop').remove();
				});

				wizard.el.find(".wizard-success .create-another-server").click(function() {
					wizard.reset();
				});
				

        		$scope.loadForm = function () {
        			AppImageService.loadForm($scope.service).then(function(data){
        				createForm(data);
        			});
        		};
        		$scope.formSave = function(){
        			for ( var i = 0; i < $scope.currentForm.formElement.length; i++){
        				var item = $scope.currentForm.formElement[i];  
        				if(item.type=='file-upload'){
        					item.value=$("#"+item.name+"").val();
        				}
        				else{
        				item.value=$scope.formElementHolder[item.name];
        				}
        				$scope.currentForm.formElement[i]=item;
        			}
        			AppImageService.saveFormValues($scope.currentForm);
            	};
        		function createForm(data){                                        
        			var comp = $("#formDiv"); 
        			$scope.currentForm=data;
        			$scope.formElementHolder=new Object();
        			var htmlCont = " <form class=\"form-inline\" role=\"form\" name=\""+data.name+"\"><fieldset> ";
        			for ( var i = 0; i < data.formElement.length; i++) {   
        			$scope.formloaded=true;
        			var item = data.formElement[i];      
        			switch (item.type) {
        			case "input":
        				htmlCont=htmlCont+"<div class=\"wizard-input-section\"><label  for=\""+item.name+"\">"+item.displayName+"</label><input type=\"text\" class=\"form-control\" id=\""+item.name+"\" name=\""+item.name+"\" value=\""+item.value+"\" ng-model=\"formElementHolder['"+item.name+"']\"></div>";
        			break;
        			case "password":
        				htmlCont=htmlCont+"<div class=\"wizard-input-section\"><label  for=\""+item.name+"\">"+item.displayName+"</label><input type=\"password\" class=\"form-control\" id=\""+item.name+"\" name=\""+item.name+"\" value=\""+item.value+"\" ng-model=\"formElementHolder['"+item.name+"']\"></div>";
        				break;
        			case "checkbox":
        				htmlCont=htmlCont+"<div class=\"wizard-input-section\"><label  for=\""+item.name+"\">"+item.displayName+"</label><div class=\"controls\" style=\"width:70px\"><label class=\"checkbox\"><input type=\"checkbox\" id=\""+item.name+"\" name=\""+item.name+"\" value=\"option1\" ng-model=\"formElementHolder['"+item.name+"']\"></label></div>";
        				break;
        			case "file-upload": 
        				htmlCont=htmlCont+"<div ng-controller=\"FileUploadCtrl\"><label  for=\""+item.name+"\">"+item.displayName+"</label><input type=\"file\" data-url=\"app\/rest\/upload\" id=\""+item.name+"\" name=\""+item.name+"\" upload></div><div><span ng-click=\"upload()\">Upload</span></div>";
        				break;
        			};}                                                                                                    			
        		     var $el = $(htmlCont).appendTo(comp);
        			 $compile($el)($scope);       			
        		};
        	
        }
        
        $scope.create = function (callback) {
            AppImageCfg.save($scope.appImageCfgDTO,
                function () {
                    $scope.appimagecfgs = AppImageCfg.query();
// $('#saveAppImageCfgModal').modal('hide');
                    callback();
                });
        };

        $scope.start = function (appimagecfgId) {
        	AppImageService.start(appimagecfgId, function (data, status) {
        		if(status == 200 ) {
        			alert("Machine Started");
        			$scope.started = true
        		}
        		else {
        			alert("Machine was not started :: Error is - " + data.error);
        		}
        	});
        };
        
        $scope.stop = function (appimagecfgId) {
        	AppImageService.stop(appimagecfgId, function (data, status) {
        		if(status == 200 ) {
        			alert("Machine Stopped");
        			$scope.started = false;
        		}
        		else {
        			alert("Machine was not started :: Error is - " + data.error);
        		}
        	});
        };
        
        
        $scope.update = function (id) {
            $scope.appimagecfg = AppImageCfg.get({id: id});
            $('#saveAppImageCfgModal').modal('show');
        };

        $scope.delete = function (id) {
            AppImageCfg.delete({id: id},
                function () {
                    $scope.appimagecfgs = AppImageCfg.query();
                });
        };

// $scope.clear = function () {
// $scope.appimagecfg = {id: "", sampleTextAttribute: "", sampleDateAttribute:
// ""};
// };
    }]);



houstonApp.controller('ServiceImageCfgController', ['$scope', 'resolvedServiceImageCfg', 'ServiceImageCfg',
    function ($scope, resolvedServiceImageCfg, ServiceImageCfg) {

        $scope.serviceimagecfgs = resolvedServiceImageCfg;

        $scope.create = function () {
            ServiceImageCfg.save($scope.serviceimagecfg,
                function () {
                    $scope.serviceimagecfgs = ServiceImageCfg.query();
                    $('#saveServiceImageCfgModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.serviceimagecfg = ServiceImageCfg.get({id: id});
            $('#saveServiceImageCfgModal').modal('show');
        };

        $scope.delete = function (id) {
            ServiceImageCfg.delete({id: id},
                function () {
                    $scope.serviceimagecfgs = ServiceImageCfg.query();
                });
        };

        $scope.clear = function () {
            $scope.serviceimagecfg = {id: "", sampleTextAttribute: "", sampleDateAttribute: ""};
        };
    }]);

houstonApp.controller('FileUploadCtrl',
	    ['$scope', '$rootScope', 'uploadManager', 
	    function ($scope, $rootScope, uploadManager) {
	    $scope.files = [];
	    $scope.percentage = 0;

	    $scope.upload = function () {
	        uploadManager.upload();
	        $scope.files = [];
	    };

	    $rootScope.$on('fileAdded', function (e, call) {
	        $scope.files.push(call);
	        $scope.$apply();
	    });

	    $rootScope.$on('uploadProgress', function (e, call) {
	        $scope.percentage = call;
	        $scope.$apply();
	    });
	}]);



houstonApp.controller('ImageInfoController', ['$scope', 'resolvedImageInfo', 'ImageInfo',
  function ($scope, resolvedImageInfo, ImageInfo) {

      $scope.imageinfos = resolvedImageInfo;

      $scope.create = function () {
          ImageInfo.save($scope.imageinfo,
              function () {
                  $scope.imageinfos = ImageInfo.query();
                  $('#saveImageInfoModal').modal('hide');
              });
      };

      $scope.update = function (id) {
          $scope.imageinfo = ImageInfo.get({id: id});
          $('#saveImageInfoModal').modal('show');
      };

      $scope.delete = function (id) {
          ImageInfo.delete({id: id},
              function () {
                  $scope.imageinfos = ImageInfo.query();
              });
      };

  }]);
