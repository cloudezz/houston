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
                username: $("#username").val(),
                password: $("#password").val(),
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
                Password.changePwd($scope.password,
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
houstonApp.controller('SignUpController', ['$scope', '$location', 'SignUpService',
   function ($scope, $location, SignUpService) {
	$scope.username = "";
	$scope.success = false;
		$scope.signUp = function () {
			SignUpService.signUp($scope.username, function(data){
				console.log("Email Sent");
				$scope.success = true;
			});
		};
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


houstonApp.controller('AppImageCfgController', ['$rootScope','$scope', '$location', '$modal' ,'$compile', '$window', '$timeout', 'resolvedAppImageCfg', 'AppImageCfg','AppImageService','ImageInfo', 
    function ($rootScope,$scope, $location, $modal,$compile,$window, $timeout, resolvedAppImageCfg, AppImageCfg, AppImageService, ImageInfo) {

        $scope.appimagecfgs = AppImageCfg.query();		
       
        console.log($scope.appimagecfgs);
        
        $scope.starting = false;
        
        $scope.start = function (appimagecfgId) {
        	$scope.starting = true;
        	$scope.progressText ="Starting";
        	var percent = 0;
        	progress();
        	function progress(){
            	$timeout(function(){
            		percent = percent + 10;
            		$("#progressBar").css("width", percent+"%");
            		if(percent < 80){
            			progress();
            		}
            	}, 100);
        	};
        	
        	AppImageService.start(appimagecfgId, function (data, status) {
        		if(status == 200 ) {
        			$scope.starting = false;
        			$scope.progressText ="";
        			$("#progressBar").css("width", "100%");
        			// alert("Machine Started");
        			AppImageCfg.query(function (data) {
        				  $scope.appimagecfgs  = data;
        			});
        		}
        		else {
        			$scope.starting = false;
        			$scope.progressText ="";
        			$("#progressBar").css("width", "0%");
        			// alert("Machine was not started :: Error is - " +
					// data.error);
        		}
        	});
        };
        
        $scope.stop = function (appimagecfgId) {
        	
        	$scope.starting = true;
        	$scope.progressText ="Stopping";
        	var percent = 0;
        	progress();
        	function progress(){
            	$timeout(function(){
            		percent = percent + 10;
            		$("#progressBar").css("width", percent+"%");
            		if(percent < 80){
            			progress();
            		}
            	}, 100);
        	};
        	
        	
        	AppImageService.stop(appimagecfgId, function (data, status) {
        		if(status == 200 ) {
        			$scope.starting = false;
        			$scope.progressText ="";
        			$("#progressBar").css("width", "100%");
        			
//        			alert("Machine Stopped");
        			AppImageCfg.query(function (data) {
      				  $scope.appimagecfgs  = data;
      			});
        		}
        		else {
        			$scope.starting = false;
        			$scope.progressText ="";
        			$("#progressBar").css("width", "0%");
// alert("Machine was not started :: Error is - " + data.error);
        		}
        	});
        };
        

		$scope.openService = function (url) {
			  var win=$window.open(url, '_blank');
			  win.focus();
		}
        
        $scope.update = function (id) {
            $scope.appimagecfg = AppImageCfg.get({id: id});
            $('#saveAppImageCfgModal').modal('show');
        };

// $scope.delete = function (id) {
// AppImageCfg.delete({id: id},
// function () {
// $scope.appimagecfgs = AppImageCfg.query();
// });
// };
        
        $scope.delete = function (id) {
        	
        	var modalInstance = $modal.open({
				templateUrl : 'deleteConfirm.html',
				controller : DeleteModalInstanceCtrl,
				scope : $scope,
				resolve : {
					AppImageCfg : function() {
						return AppImageCfg;
					}, 
					
					id: function () {
						return id;
					}
				}
			});
	
			modalInstance.result.then(function(selectedItem) {
				$scope.selected = selectedItem;
			}, function() {
				$log.info('Modal dismissed at: ' + new Date());
			});
        };
    }]);

houstonApp.controller('AppImgConfigWizardController',['$rootScope','$scope','$compile','AppImageCfg','ImageInfo','AppImageService',
	function($rootScope,$scope,$compile,AppImageCfg,ImageInfo,AppImageService){
		
		$scope.service;
        
        $scope.appImageCfgDTO = {};
        
        var wizard;
        
        var serviceWizard;
        
        $scope.$on('$viewContentLoaded', function(){
        	$scope.openWizard();
        	});
        
		$scope.setService = function(serviceId,serviceName) {
        if($scope.service!=null){
        	 $("#app"+$scope.service+"").removeClass('active');
             }
           $("#app"+serviceId+"").addClass('active');			
			 $scope.service = serviceId;
			 $scope.serviceImg = serviceName; 
		}
		$scope.setSubService = function(serviceId,serviceName) {
			 if($scope.subService!=null){
	        	 $("#service"+$scope.subService+"").removeClass('active');
	             }
	           $("#service"+serviceId+"").addClass('active');			

			 $scope.subService = serviceId;
			 $scope.subServiceImg = serviceName;
		}
        
        $scope.create = function (callback) {
            AppImageCfg.save($scope.appImageCfgDTO,
                function () {
                    $scope.appimagecfgs = AppImageCfg.query();
                    callback();
                });
        };
        $scope.createService=function(){
				serviceWizard.trigger("success");
				serviceWizard.hideButtons();
				serviceWizard._submitting = false;
				serviceWizard.showSubmitCard("success");
				serviceWizard.updateProgressBar(0);	 
			};
			
		$scope.openServiceWizard = function () {
			$scope.serviceDTO = new Object(); 
			
			$scope.serviceImages = {};
        	ImageInfo.query(function(data) {
        		$scope.serviceImages = data;
        	});
          
			$scope.sshpwd="";			
				
			var options = {
    				contentWidth : 800,
    				contentHeight : 400,
    				keyboard : false,
    				backdrop : true
    			};
        		if(!serviceWizard) {
        			serviceWizard = $("#serviceConfigWzd").wizard(options);
        		}
        		serviceWizard.show();
	       		$('.modal-backdrop').addClass();
	       		
            	serviceWizard.on("submit", function(serviceWizard) { 
	       			for ( var i = 0; i < $scope.currentServiceForm.formElement.length; i++){
        				var item = $scope.currentServiceForm.formElement[i];  
        				if(item.type=='file-upload'){        					
        					$scope.serviceFormElementHolder[item.name]=$rootScope.servicefileSelected;
        				}
        			}
	       			$scope.serviceDTO.imageName=$scope.subServiceImg;
	       			$scope.serviceDTO.environmentMapping=$scope.serviceFormElementHolder;

	       			console.log($scope.serviceDTO );	
	       			$scope.serviceDTOList.push($scope.serviceDTO);
	            	 $scope.$apply();
	       			$scope.createService();	
	       			
				});
            	
            	serviceWizard.on("incrementCard", function(serviceWizard) {
	       		var activeCard=	serviceWizard.getActiveCard();
	       		if(activeCard.name=="servicecard3"){
	       			$scope.loadForm($scope.subService,'serviceFormDiv','serviceFormElementHolder');
	       		}
				});
				
            	serviceWizard.on("closed", function(serviceWizard) {
	       			$('.modal-backdrop').remove();
				});
	       		
            	serviceWizard.on("reset", function(serviceWizard) {
            		serviceWizard.setSubtitle("");
				});

            	serviceWizard.el.find(".wizard-success .im-done").click(function() {
            		serviceWizard.reset().close();
       				$('.modal-backdrop').remove();
				});

            	serviceWizard.el.find(".wizard-success .create-another-server").click(function() {
            		serviceWizard.reset();
				});        		        	
        }		
		$scope.openWizard = function () {
			
			$scope.serviceDTOList = [];
			$scope.appImageCfgDTO = new Object();
        	
        	$scope.serviceImages = {};
        	ImageInfo.query(function(data) {
        		$scope.serviceImages = data;
        	});

			$scope.sshpwd="";
        	var options = {
    				keyboard : false,
    				isModal:false,
    				backdrop : false,
    				container:$("#wizardDivParent")
    			};
        		if(!wizard) {
        			wizard = $("#appImageConfigWzd").nwizard(options);
        		}
	       		wizard.show();
	       		
	       		wizard.on("submit", function(wizard) { 
	       			for ( var i = 0; i < $scope.currentForm.formElement.length; i++){
        				var item = $scope.currentForm.formElement[i];  
        				if(item.type=='file-upload'){        					
        					$scope.formElementHolder[item.name]=$rootScope.fileSelected;
        				}
        			}
	       			$scope.appImageCfgDTO.imageName=$scope.serviceImg;
	       			$scope.appImageCfgDTO.environmentMapping=$scope.formElementHolder;
	       			$scope.appImageCfgDTO.serviceImages=$scope.serviceDTOList;

	       			console.log( $scope.appImageCfgDTO );	       				       			
	       			
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
	       			$scope.loadForm($scope.service,'formDiv','formElementHolder');
	       		}
				});
				
	       		wizard.on("closed", function(wizard) {
	       			$('.modal-backdrop').remove();
				});
	       		
				wizard.on("reset", function(wizard) {
					wizard.setSubtitle("");
					$('.wizard-cards').find('input:text').val('');
				});

				wizard.el.find(".wizard-success .im-done").click(function() {
					wizard.reset().close();
       				$('.modal-backdrop').remove();
				});

				wizard.el.find(".wizard-success .create-another-server").click(function() {
					wizard.reset();
				});
		}	        		

        		$scope.loadForm = function (serviceId,formId,holdername) {
        			AppImageService.loadForm(serviceId).then(function(data){
        				if(formId=='formDiv')
        				   $scope.currentForm=data;
        				else if(formId=='serviceFormDiv')
        					 $scope.currentServiceForm=data;
        				createForm(data,formId,holdername);
        			});
        		};
        		function createForm(data,formId){                                        
        			var comp = $("#"+formId+""); 
        			comp.empty();
        			$scope.formElementHolder=new Object();
        			var htmlCont = "";
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
        				htmlCont=htmlCont+"<div class=\"wizard-input-section\"  ng-controller=\"FileUploadCtrl\"><label  for=\""+item.name+"\">"+item.displayName+"</label><div class=\"input-group\"><span class=\"input-group-btn\"><span class=\"btn btn-primary btn-file\">Browse<input type=\"file\" data-url=\"app\/rest\/upload\" id=\""+item.name+"\" name=\""+item.name+"\" upload></span></span><input class=\"form-control\" type=\"text\" readonly=\"\" id=\""+item.name+"fileInput\" ng-model=\"fileSelected\" style=\"width:300px\"><label ng-click=\"upload()\" class=\"form-control\">Upload</label></div></div>";
        				break;
        			};}                                                                                                    			
        		     var $el = $(htmlCont).appendTo(comp);
        			 $compile($el)($scope);       			
        		};
        
        $scope.create = function (callback) {
            AppImageCfg.save($scope.appImageCfgDTO,
                function () {
                    $scope.appimagecfgs = AppImageCfg.query();
                    callback();
                });
        };
        
    }]);
/**
 * Controller for the delete modal dialog confirmation window
 * 
 * @param $scope
 * @param $modal
 * @param $modalInstance
 * @param deal
 */
function DeleteModalInstanceCtrl($scope,$timeout, $modal, $modalInstance, AppImageCfg, id){
		
	$scope.ok = function() {
		
		AppImageCfg.delete({id: id},
                function () {
                    $scope.$parent.appimagecfgs = AppImageCfg.query();
                });
		
		
		$modalInstance.close();
	};

	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	};
}



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
	        var len= $scope.files.length;
	        $rootScope.fileSelected= $scope.files[len-1];
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

houstonApp.controller('SetPasswordController', ['$scope', '$location',  '$route', '$routeParams', 'Password',
     function ($scope,$location, $route, $routeParams, Password) {
		 $scope.accountId =  $routeParams.accountId
         $scope.success = null;
         $scope.error = null;
         $scope.doNotMatch = null;
         $scope.changePassword = function () {
             if ($scope.password != $scope.confirmPassword) {
                 $scope.doNotMatch = "ERROR";
             } else {
                 $scope.doNotMatch = null;
                 Password.setPwd($scope.accountId, $scope.password,
                     function (value, responseHeaders) {
                         $scope.error = null;
                         $scope.success = 'OK';
                         $location.path('/appimagecfg');
                     },
                     function (httpResponse) {
                         $scope.success = null;
                         $scope.error = "ERROR";
                     });
             }
         };
     }]);

// houstonApp.controller('SetPasswordController', ['$scope',
// function ($scope ) {
//	
// }]);
