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
houstonApp.controller('SignUpController', ['$scope', '$location', 'SignUpService', '$tooltip',
   function ($scope, $location, SignUpService, $tooltip) {
	
	$scope.username = "";
	$scope.success = false;
	$scope.validEmail = true;
		$scope.signUp = function (signupForm) {
			if($scope.username=='' || signupForm.username.$error.email) {
				console.log("In valid Email Id")
			}
			else {
				SignUpService.signUp($scope.username, function(data){
					console.log("Email Sent");
					$scope.success = true;
				});
			}
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


houstonApp.controller('AppImageCfgController', ['$rootScope','$scope', '$location', '$modal' ,'$compile', '$window', '$timeout', 'resolvedAppImageCfg', 'AppImageCfg','AppImageService','ImageInfo','AppConfigCommunicationService', 
    function ($rootScope,$scope, $location, $modal,$compile,$window, $timeout, resolvedAppImageCfg, AppImageCfg, AppImageService, ImageInfo,AppConfigCommunicationService) {
        AppImageCfg.query(function (data) {
			  $scope.appimagecfgs  = data;
			  AppConfigCommunicationService.setAppImgConfigs(data);
		});	
        console.log($scope.appimagecfgs);

        $scope.start = function (appimagecfg) {
        	appimagecfg.starting = true;
        	$scope.progressText ="Starting";
        	var percent = 0;
        	progress();
        	function progress(){
            	$timeout(function(){
            		percent = percent + 10;
            		$("#progressBar" + appimagecfg.id).css("width", percent+"%");
            		if(percent < 80){
            			progress();
            		}
            	}, 100);
        	};
        	
        	AppImageService.start(appimagecfg.id, function (data, status) {
        		delete appimagecfg.starting;
        		$scope.progressText ="";
        		if(status == 200 ) {
        			$("#progressBar" + appimagecfg.id).css("width", "100%");
        			  msg.update({
                  		  message:'Machine Started!',
                  		  type: 'success',
                  		  showCloseButton: true
                  		});
        			  AppImageCfg.query(function (data) {
        				  $scope.appimagecfgs  = data;
        				  AppConfigCommunicationService.setAppImgConfigs(data);
        			});
        		} else {
        			$("#progressBar" + appimagecfg.id).css("width", "0%");
        			$rootScope.msg.update({
                  		  message:'Machine was not started : Error is : '+data.error+'!',
                  		  type: 'error',
                    	  showCloseButton: true
                  		});
        		}
        	});
        };
        
        $scope.stop = function (appimagecfg) {
        	appimagecfg.starting = true;
        	$scope.progressText ="Stopping";
        	var percent = 0;
        	progress();
        	function progress(){
            	$timeout(function(){
            		percent = percent + 10;
            		$("#progressBar" + appimagecfg.id).css("width", percent+"%");
            		if(percent < 80){
            			progress();
            		}
            	}, 100);
        	};
        	
        	
        	AppImageService.stop(appimagecfg.id, function (data, status) {
        		delete appimagecfg.starting;
    			$scope.progressText ="";
        		if(status == 200 ) {
        			$("#progressBar" + appimagecfg.id).css("width", "100%");
        			
        			 $rootScope.msg.update({
                 		  message:'Machine Stopped!',
                 		  type: 'success',
                    	  showCloseButton: true
                 		});
        			 AppImageCfg.query(function (data) {
        				  $scope.appimagecfgs  = data;
        				  AppConfigCommunicationService.setAppImgConfigs(data);
        			});
        		} else {
        			$("#progressBar" + appimagecfg.id).css("width", "0%");
        			$rootScope.msg.update({
                		  message:'Machine was not stopped :: Error is : '+data.error+'!',
                		  type: 'error',
                    	  showCloseButton: true
                		});
        		}
        	});
        };
        
        $scope.editConfig=function(config){
        	AppConfigCommunicationService.setConfigToEdit(config);
        	location.href = "#/appImgConfigWizard";
        }

		$scope.openService = function (url) {
			  var win=$window.open(url, '_blank');
			  win.focus();
		}
		
		$scope.openTerminal= function (containerId) {
			var url = "/#/terminal/"+containerId;
			var win=$window.open(url, '_blank');
			win.focus();
		}
        
        $scope.update = function (id) {
            $scope.appimagecfg = AppImageCfg.get({id: id});
            $('#saveAppImageCfgModal').modal('show');
        };
        
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

houstonApp.controller('AppImgConfigWizardController',['$rootScope','$scope','$compile','AppImageCfg','ImageInfo','ServiceImageInfo','AppImageService','AppConfigCommunicationService','defaultConfigs',
	function($rootScope,$scope,$compile,AppImageCfg,ImageInfo,ServiceImageInfo,AppImageService,AppConfigCommunicationService,defaultConfigs){
		
		$scope.service;
		
		$scope.selectedImgDesc;
        
        $scope.appImageCfgDTO = {};
        
        if(AppConfigCommunicationService.getConfigToEdit()!=null){
        	 $scope.appImageCfgDTO=AppConfigCommunicationService.getConfigToEdit();
        	 $scope.setServiceWithName($scope.appImageCfgDTO.imageName);
        }	
        var wizard;
        
        var serviceWizard;
        
        $scope.$on('$viewContentLoaded', function(){
        	$scope.openWizard();
        	});
        $scope.setServiceWithName=function(serviceName){
			var serviceId;
			angular.forEach(AppConfigCommunicationService.getConfigToEdit(), function(item) {
				if(item.imageName==serviceName){
					serviceId=item.id;
				}
			});
			$scope.setService(serviceId,serviceName);
		
        }
		$scope.setService = function(serviceId,serviceName) {
		
		/* Marking image selection by adding grey border */
        if($scope.service!=null){
        	 $("#app"+$scope.service+"").css('border-color', 'white');  	    
            }
        $("#app"+serviceId+"").css('border-color', 'grey');
        
        /* Marking image selection by adding tick mark */
        
        /*
		 * if($scope.service!=null){
		 * $("#app"+$scope.service+"").find('.roll').stop().animate({ opacity: 0 },
		 * "slow"); } $("#app"+serviceId+"").find('.roll').stop().animate({
		 * opacity: .7 }, "slow");
		 */
			 $scope.service = serviceId;
			 $scope.serviceImg = serviceName; 
		}
		$scope.getInitialStyle=function(serviceId,imageName){
			if($scope.serviceDTO!=null &&  $scope.serviceDTO.imageName==imageName){
				serviceWizard.find("#service"+serviceId+"").css('border-color', 'grey');
			}
		}
		$scope.setSubServiceWithName = function(serviceName) {
			var serviceId;
			angular.forEach($scope.subServiceImages, function(item) {
				if(item.imageName==serviceName){
					serviceId=item.id;
				}
			});
			$scope.setSubService(serviceId,serviceName);
		}
		$scope.setSubService = function(serviceId,serviceName) {
			if($scope.isEditContext && $scope.serviceDTO.imageName!=serviceName){
					$scope.serviceFormElementHolder=new Object();				
			}
			 if($scope.subService!=null){
	        	 serviceWizard.find("#service"+$scope.subService+"").css('border-color', 'white');
	             }
	        var curSel=$("#service"+serviceId+""); 
	        serviceWizard.find("#service"+serviceId+"").css('border-color', 'grey');			

			 $scope.subService = serviceId;
			 $scope.serviceDTO.imageName = serviceName;
		}
        
        $scope.create = function (callback) {
            AppImageCfg.save($scope.appImageCfgDTO,
                function () {
            	AppImageCfg.query(function (data) {
      			  AppConfigCommunicationService.setAppImgConfigs(data);
            	});
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
			
			$scope.updateService=function(appName){
				angular.forEach($scope.serviceDTOList, function(item) {
			    	if(item.appName==appName){
			    		$scope.serviceDTO=item;
			    		$scope.openServiceWizard(true);
			    }});
			}
			$scope.deleteService=function(appName){
				angular.forEach($scope.serviceDTOList, function(item) {
			    	if(item.appName==appName){
			    		$scope.serviceDTOList.splice($.inArray(item, $scope.serviceDTOList),1);
			    }});
			}
			
		$scope.restoreDefaultScript=function () {
			$scope.appImageCfgDTO.initScript=$scope.defaultScript;
		}
		function enableDefaultConfig(memory,cpu){
			var id;
			if(memory==defaultConfigs.TINY_MEMORY && cpu==defaultConfigs.TINY_CPU){
				id="tinyConfig";
			}else if(memory==defaultConfigs.SMALL_MEMORY && cpu==defaultConfigs.SMALL_CPU){
				id="smallConfig";
			}else if(memory==defaultConfigs.MEDIUM_MEMORY && cpu==defaultConfigs.MEDIUM_CPU){
				id="medConfig";
			}else if(memory==defaultConfigs.LARGE_MEMORY && cpu==defaultConfigs.LARGE_CPU){
				id="largeConfig";
			}else if(memory==defaultConfigs.VLARGE_MEMORY && cpu==defaultConfigs.VLARGE_CPU){
				id="veryLargeConfig";
			}
			if(id!=null)
				$("#wizardButtons").find(":button").removeClass("active");
			    $("#"+id).addClass("active");
			
		}
		$scope.setDefaultConfig=function(configName){
			var id;
			switch (configName) {
			case "Tiny":
				$("#memory").slider('setValue', defaultConfigs.TINY_MEMORY);
				$("#cpuShares").slider('setValue', defaultConfigs.TINY_CPU);
				id="tinyConfig";
				break;
			case "Small":
				$("#memory").slider('setValue', defaultConfigs.SMALL_MEMORY);
				$("#cpuShares").slider('setValue', defaultConfigs.SMALL_CPU);
				id="smallConfig";
				break;
			case "Medium":
				$("#memory").slider('setValue', defaultConfigs.MEDIUM_MEMORY);
				$("#cpuShares").slider('setValue', defaultConfigs.MEDIUM_CPU);
				id="medConfig";
				break;
			case "Large": 
				$("#memory").slider('setValue', defaultConfigs.LARGE_MEMORY);
				$("#cpuShares").slider('setValue', defaultConfigs.LARGE_CPU);
				id="largeConfig";
				break;
            case "VeryLarge": 
            	$("#memory").slider('setValue', defaultConfigs.VLARGE_MEMORY);
				$("#cpuShares").slider('setValue', defaultConfigs.VLARGE_CPU);
				id="veryLargeConfig";
				break;
			};
			$("#wizardButtons").find(":button").removeClass("active");
			$("#"+id).addClass("active");
			
		}
		$scope.setServiceDefaultConfig=function(configName){
            var id;
			switch (configName) {
			case "Tiny":
				$("#service_memory").slider('setValue', defaultConfigs.TINY_MEMORY);
				$("#service_cpuShares").slider('setValue', defaultConfigs.TINY_CPU);
				id="service_tinyConfig";
			break;
			case "Small":
				$("#service_memory").slider('setValue', defaultConfigs.SMALL_MEMORY);
				$("#service_cpuShares").slider('setValue', defaultConfigs.SMALL_CPU);
				id="service_smallConfig";
				break;
			case "Medium":
				$("#service_memory").slider('setValue', defaultConfigs.MEDIUM_MEMORY);
				$("#service_cpuShares").slider('setValue', defaultConfigs.MEDIUM_CPU);
				id="service_medConfig";
				break;
			case "Large": 
				$("#service_memory").slider('setValue', defaultConfigs.LARGE_MEMORY);
				$("#service_cpuShares").slider('setValue', defaultConfigs.LARGE_CPU);
				id="service_largeConfig";
				break;
            case "VeryLarge": 
            	$("#service_memory").slider('setValue', defaultConfigs.VLARGE_MEMORY);
				$("#service_cpuShares").slider('setValue', defaultConfigs.VLARGE_CPU);
				id="service_veryLargeConfig";
				break;
			};	
			var bts=$("#serviceWizardButtons");
			var comppp=$("#"+id);
			$("#serviceWizardButtons").find(":button").removeClass("active");
			$("#"+id).addClass("active");
		}
		/* Wizard validation functions */
		
		$scope.validateFirstStep=function(card) {
		    var input;
		    var errorSpan;
		    var valid=true;
		    var wizardCard;
		    var service;
		    var list;
		    if(card.nwizard!=undefined){
		    	service=$scope.service;
		    	wizardCard= card.nwizard;
		    	errorSpan=$("#errorSpan");
		    	list=AppConfigCommunicationService.getAppImgConfigs();
		    	input=card.el.find("#appName");
		    	}		           
		    	else{
			    	service=$scope.subService;
			    	wizardCard= card.wizard;
			    	errorSpan=serviceWizard.find("#serviceErrorSpan");
			    	list=$scope.serviceDTOList;
			    	input=card.el.find("#serviceAppName");
			    	}
		    var name = input.val();
		    if (name == "") {
		    	wizardCard.errorPopover(input, "Name cannot be empty");		    
		        valid= false;
		    }				   
		    else{
		    input.popover("destroy");
		 
		    console.log($scope.serviceDTOList);
		    angular.forEach(list, function(item) {		    	
		    	if(item!=$scope.serviceDTO && item.appName==name){		    		
		 		    wizardCard.errorPopover(input, "Name is not unique.Please use a different name");
		    		 valid= false;		    	
		    }});}
		    if(valid){ 
		    	input.popover("destroy");
		    }	   
		    if(service==null){
	 		    	wizardCard.errorPopover(errorSpan, "Please select a service");
			        valid= false;
			        }
		    else
		    	errorSpan.popover("destroy");
		    return valid;
		
		}
		$scope.validateFn = function(card) {
			var valid=true;
			if(card.name=="card3" || card.name=="servicecard3"){
				var inputs = card.el.find("input");					
                valid=$scope.validateEnvForm(inputs,card,valid);
                inputs = card.el.find("textarea");					
                valid=$scope.validateEnvForm(inputs,card,valid);
			}else{
				var inputs = card.el.find("input");					
                valid=$scope.validateInputs(inputs,card,valid);
                inputs = card.el.find("textarea");					
                valid=$scope.validateInputs(inputs,card,valid);
			}					
		    return valid;
		};
		$scope.validateInputs=function(inputs,card,valid){
			var wizardCard; 
			var nModalWzd=false;
			if(card.nwizard!=undefined){
		           wizardCard= card.nwizard;
		           nModalWzd=true;
			}
		    	else
		    		wizardCard=card.wizard;
			 angular.forEach(inputs, function(inputItem){
                	var name = inputItem.value;
                	var inputComp;
                	if(nModalWzd){
                		inputComp=$("#"+inputItem.id);
                	}
                	else{
                		inputComp=serviceWizard.find("#"+inputItem.id);
                	}
				    if (name == "") {
				    	wizardCard.errorPopover(inputComp, "Field cannot be empty");
				        valid= false;
				    }
				    if(valid){
				    	inputComp.popover("destroy");
				    }
			    });
			 return valid;
		}
		
		$scope.validateEnvForm=function(inputs,card,valid){
			var wizardCard;
			var curFormEl;
			var formId;
			var nModalWzd=false;
			
			if(card.nwizard!=undefined){
		           wizardCard= card.nwizard;
		           curFormEl=$scope.currentForm.formElement;
		           formId="formDiv";
		           nModalWzd=true;
			}
		    else{
		    	   wizardCard=card.wizard;
		    	   curFormEl=$scope.currentServiceForm.formElement;
		    	   formId="serviceFormDiv";
		    }
			var reqMap=createReqMap(formId,curFormEl);
			 angular.forEach(inputs, function(inputItem){
                	var name = inputItem.value;
                	var inputComp;
                	if(nModalWzd){
                		inputComp=$("#"+inputItem.id);
                	}
                	else{
                		inputComp=serviceWizard.find("#"+inputItem.id);
                	}
                	var optional=reqMap[inputItem.id];
				    if (name == ""&& optional=='false') {				    	
				    wizardCard.errorPopover(inputComp, "Field cannot be empty");
				        valid= false;
				    }
				    if(valid){
				    	inputComp.popover("destroy");
				    }
			    });
			 return valid;
			 function createReqMap(formId,curFormEl){
				 var reqMap=new Object();
				 angular.forEach(curFormEl,function(item){
					 reqMap[formId+"_"+item.name]=item.optional;
				 });
				 return reqMap;
			 }
		}
		
	/* Validation functions ends */
		
	/* Service wizard creation */
		
		$scope.openServiceWizard = function (isEdit) {
		$scope.isEditContext=isEdit;
		if(!$scope.isEditContext){
			$scope.serviceDTO={};
		}
			$scope.serviceFormElementHolder=new Object(); 
			if($scope.isEditContext){
				if($scope.serviceDTO!=null){
					var envMapping=$scope.serviceDTO.environmentMapping;
					angular.forEach(envMapping,function(value,key){
						$scope.serviceFormElementHolder[key]=value;
					});
				}
			}

			$scope.subServiceImages = {};
			ServiceImageInfo.query(function(data) {
        		$scope.subServiceImages = data;
        		if($scope.isEditContext){
        			$scope.setSubServiceWithName($scope.serviceDTO.imageName);
        		}
        	});			
          
			$scope.sshpwd="";			
				
			var options = {
    				contentWidth : 890,
    				contentHeight : 400,
    				keyboard : false,
    				backdrop : 'static'
    			};
        		if(!serviceWizard) {
        			serviceWizard = $("#serviceConfigWzd").wizard(options);
        		}
        		serviceWizard.show();
	       		$('.modal-backdrop').addClass();
	       	   
	        	/* for sliders in service wizard */	
	       		createServiceMemSlider();
	       		createServiceCpuSlider();  
	       		if($scope.isEditContext){
	       			enableDefaultConfig($scope.serviceDTO.memory,$scope.serviceDTO.cpuShares);
	       		}	
            	serviceWizard.on("submit", function(serviceWizard) { 
	       			for ( var i = 0; i < $scope.currentServiceForm.formElement.length; i++){
        				var item = $scope.currentServiceForm.formElement[i];  
        				if(item.type=='file-upload'){        					
        					$scope.serviceFormElementHolder[item.name]=$rootScope.servicefileSelected;
        				}
        			}
	       			$scope.serviceDTO.memory=$("#service_memory").slider('getValue');
	       			$scope.serviceDTO.cpuShares=$("#service_cpuShares").slider('getValue');	       			
	       			$scope.serviceDTO.environmentMapping=$scope.serviceFormElementHolder;
	       			
	       			/* reset slider */
	       			$("#service_memory").slider('setValue',128);
	       			$("#service_cpuShares").slider('setValue',1);
	       			$("#smemorySliderVal").text("");
	       			$("#scpuSharesSliderVal").text("");
	       			
	       			$("#serviceWizardButtons").find(":button").removeClass("active");

	       			console.log($scope.serviceDTO );
	       			if($scope.isEditContext){
	       				$scope.serviceDTOList.splice($.inArray($scope.serviceDTO, $scope.serviceDTOList),1);
	       			}
	       			$scope.serviceDTOList.push($scope.serviceDTO);
	            	$scope.$apply();
	       			$scope.createService();	
	       			$scope.serviceDTO=null;
	       			$scope.subService=null;
				});
            	
            	serviceWizard.on("incrementCard", function(serviceWizard) {
	       		var activeCard=	serviceWizard.getActiveCard();
	       		if(activeCard.name=="servicecard3" && $scope.subService!=$scope.prevSubService){
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
            		resetServiceWizard(serviceWizard);            		
				});
            	
            	/* service wizard validation */
            	serviceWizard.cards["servicecard1"].on("validate", $scope.validateFirstStep);
            	serviceWizard.cards["servicecard3"].on("validate",$scope.validateFn);
        }
		function resetServiceWizard(serviceWizard){
			$scope.isEditContext=false;
			$scope.serviceFormElementHolder=new Object();
			$scope.serviceDTO = new Object();
			if(serviceWizard!=null){
				serviceWizard.find(".view-first").css('border-color', 'white');
				serviceWizard.find(":input").val("");
	          }
		}
		function createServiceMemSlider(){
			$("#service_memory").slider(
       				{
       				   tooltip:"hide"
       				});
			if($scope.isEditContext){
				$("#service_memory").slider('setValue',$scope.serviceDTO.memory);
			}
			else{
				$("#service_memory").slider('setValue',defaultConfigs.SMALL_MEMORY);
			}
			setMemSliderLabel.call();
			$("#service_memory").on('slide', setMemSliderLabel);
		}
		function createServiceCpuSlider(){
			$("#service_cpuShares").slider(  
       				{
       				   tooltip:"hide"
       				});
			if($scope.isEditContext){
				$("#service_cpuShares").slider('setValue',$scope.serviceDTO.cpuShares);
			}
			else{
				$("#service_cpuShares").slider('setValue',defaultConfigs.SMALL_CPU);
			}
			setCpuSliderLabel.call();
			$("#service_cpuShares").on('slide', setCpuSliderLabel);
		}
		var setCpuSliderLabel=function(slideEvt) {
   			var val=$("#service_cpuShares").slider('getValue')+" cpu";
   			$("#scpuSharesSliderVal").text(val);
   		}
		var setMemSliderLabel=function(slideEvt) {
       			var val=$("#service_memory").slider('getValue');	       			
       			if(val>1024)
						val= val/1024+" gb";
       			else 
						val= val+" mb";
       			$("#smemorySliderVal").text(val);
       		}
		
		/* Service wizard creation ends */
		
		/* App wizard creation */
		
		$scope.openWizard = function () {
			
			initializeWizard();

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
	       		
	       		createWizardSliders();
	       		
	       		wizard.on("submit", function(wizard) { 
	       			for ( var i = 0; i < $scope.currentForm.formElement.length; i++){
        				var item = $scope.currentForm.formElement[i];  
        				if(item.type=='file-upload'){        					
        					$scope.formElementHolder[item.name]=$rootScope.fileSelected;
        				}
        			}
	       			$scope.appImageCfgDTO.memory=$("#memory").slider('getValue');
	       			$scope.appImageCfgDTO.cpuShares=$("#cpuShares").slider('getValue');	       			
	       			$scope.appImageCfgDTO.imageName=$scope.serviceImg;
	       			$scope.appImageCfgDTO.environmentMapping=$scope.formElementHolder;
	       			$scope.appImageCfgDTO.serviceImages=$scope.serviceDTOList;
	       			
	       			/* reset slider */
	       			$("#memory").slider('setValue',128);
	       			$("#cpuShares").slider('setValue',1);
	       			$("#memorySliderVal").text("");
	       			$("#cpuSharesSliderVal").text("");
	       			$("#wizardButtons").find(":button").removeClass("active");

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
	       		if(activeCard.name=="card3" && $scope.service!=$scope.prevService){
	       			$scope.loadForm($scope.service,'formDiv','formElementHolder');
	       		}
	       		if(activeCard.name=="card4"){
	       			$scope.loadDefaultScript($scope.service);
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
       				$(".wizard").remove();
				});

				wizard.el.find(".wizard-success .create-another-server").click(function() {
					wizard.reset();
					initializeWizard();
				});
				 AppImageCfg.query(function (data) {
					  $scope.appimagecfgs  = data;
					  AppConfigCommunicationService.setAppImgConfigs(data);
				});					
				
				 /* App wizard validation */				
				wizard.cards["card1"].on("validate", $scope.validateFirstStep);
				// wizard.cards["card2"].on("validate",$scope.validateFn);
				wizard.cards["card3"].on("validate",$scope.validateFn);
		    	// wizard.cards["card4"].on("validate",$scope.validateFn);
				   		
		}	
		/* App wizard creation ends */
		
		function initializeWizard(){
			$scope.serviceDTOList = [];
			if(!$scope.isEditContext)
				$scope.appImageCfgDTO = new Object();
			$scope.formElementHolder=new Object();
        	$scope.serviceImages = {};
        	$scope.service=null;
			$scope.serviceImg=null;
        	ImageInfo.query(function(data) {
        		$scope.serviceImages = data;
        	});
        	createWizardSliders();
		}
		function createWizardSliders(){
			/* for sliders in wizard */
			$("#memory").slider(
				{
					value:defaultConfigs.SMALL_MEMORY,
				    tooltip:"hide"
				});
			$("#memory").on('slide', function(slideEvt) {
			var val=$("#memory").slider('getValue');
			if(val>1024)
				val= val/1024+" gb";
			else 
				val= val+" mb";
			$("#memorySliderVal").text(val);
			});
		
			$("#cpuShares").slider({
				value:defaultConfigs.SMALL_CPU,
				tooltip:"hide"
			});
			$("#cpuShares").on('slide', function(slideEvt) {
				var val=$("#cpuShares").slider('getValue')+" cpu";
				$("#cpuSharesSliderVal").text(val);
			});
}
		
$scope.loadDefaultScript=function(serviceId){
	AppImageService.loadScript(serviceId).then(function(data){
		$scope.defaultScript=data;
		$scope.appImageCfgDTO.initScript=data;
	});
};
        		$scope.loadForm = function (serviceId,formId,holdername) {
        			AppImageService.loadForm(serviceId).then(function(data){
        				if(formId=='formDiv')
        				   $scope.currentForm=data;
        				else if(formId=='serviceFormDiv')
        					 $scope.currentServiceForm=data;
        				createForm(data,formId,holdername);
        			});
        		};
        		function createForm(data,formId,holdername){ 
        			var comp = $("#"+formId+"");
        			if(formId=='formDiv')
        				$scope.prevService= $scope.service; 
     				else if(formId=='serviceFormDiv'){
     					$scope.prevSubService= $scope.subService; 
     					comp=serviceWizard.find("#serviceFormDiv");
     				}                                  
        			comp.empty();      
        			var htmlCont = "";
        			for ( var i = 0; i < data.formElement.length; i++) {   
        			$scope.formloaded=true;
        			var item = data.formElement[i];  
        			var compId=formId+"_"+item.name;
        			switch (item.type) {
        			case "input":
        				htmlCont=htmlCont+"<div class=\"wizard-input-section\"><label  for=\""+item.name+"\">"+item.displayName+"</label><input type=\"text\" class=\"form-control\" id=\""+compId+"\"  name=\""+item.name+"\" value=\""+item.value+"\" ng-model=\""+holdername+"['"+item.name+"']\" required></div>";
        			break;
        			case "password":
        				htmlCont=htmlCont+"<div class=\"wizard-input-section\"><label  for=\""+item.name+"\">"+item.displayName+"</label><input type=\"password\" class=\"form-control\" id=\""+compId+"\" name=\""+compId+"\" value=\""+item.value+"\" ng-model=\""+holdername+"['"+item.name+"']\" required></div>";
        				break;
        			case "checkbox":
        				htmlCont=htmlCont+"<div class=\"wizard-input-section\"><label  for=\""+item.name+"\">"+item.displayName+"</label><div class=\"controls\" style=\"width:70px\"><label class=\"checkbox\"><input type=\"checkbox\" id=\""+compId+"\"  name=\""+item.name+"\" ng-model=\""+holdername+"['"+item.name+"']\"></label></div>";
        				break;
        			case "file-upload": 
        				htmlCont=htmlCont+"<div class=\"wizard-input-section\"  ng-controller=\"FileUploadCtrl\"><label  for=\""+item.name+"\">"+item.displayName+"</label><div class=\"input-group\"><span class=\"input-group-btn\"><span class=\"btn btn-primary btn-file\">Browse<input type=\"file\" data-url=\"app\/rest\/upload\" id=\""+compId+"\"  name=\""+item.name+"\" upload></span></span><input class=\"form-control\" type=\"text\" readonly=\"\" id=\""+item.name+"fileInput\" ng-model=\"fileSelected\" style=\"width:300px\"><label ng-click=\"upload()\" class=\"form-control\">Upload</label></div></div>";
        				break;
        			};}                                                                                                    			
        		     var $el = $(htmlCont).appendTo(comp);
        			 $compile($el)($scope);       			
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
function DeleteModalInstanceCtrl($scope,$timeout, $modal, $modalInstance,$rootScope,AppImageCfg, id){
		
	$scope.ok = function() {
		
		AppImageCfg.delete({id: id},
                function () {
                    $scope.$parent.appimagecfgs = AppImageCfg.query();
                    $rootScope.msg.update({
              		  message:'Application deleted!',
              		  type: 'success',
                	  showCloseButton: true
              		});
                },function(httpResponse){
                	$rootScope.msg.update({
                		  message:'Could not delete application.Error is : '+httpResponse.data.error+'!',
                		  type: 'error',
                    	  showCloseButton: true
                		});
                    });		
		
		$modalInstance.close();
	};

	$scope.cancel = function() {
		$modalInstance.dismiss('cancel');
	};
}



houstonApp.controller('ServiceImageCfgController', ['$scope','$rootScope', 'resolvedServiceImageCfg', 'ServiceImageCfg',
    function ($scope,$rootScope, resolvedServiceImageCfg, ServiceImageCfg) {

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
                    $rootScope.msg.update({
                		  message:'Deleted service image!',
                		  type: 'success',
                    	  showCloseButton: true
                		});
                },function(httpResponse){
                	 $rootScope.msg.update({
               		  message:'Could not delete service image.Error is : '+httpResponse.data.error+'!',
               		  type: 'error',
                	  showCloseButton: true
               		});
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



houstonApp.controller('ImageInfoController', ['$scope','$rootScope', 'resolvedImageInfo', 'ImageInfo',
  function ($scope,$rootScope, resolvedImageInfo, ImageInfo) {

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
                  $rootScope.msg.update({
              		  message:'Image config deleted!',
              		  type: 'success',
                	  showCloseButton: true
              		});
              },function (httpResponse) {
            	  $rootScope.msg.update({
               		  message:'Could not delete image config.Error is :'+httpResponse.data.error+'!',
               		  type: 'error',
                	  showCloseButton: true
               		});
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

houstonApp.controller('TerminalController', ['$rootScope','$scope', '$location',  '$route', '$routeParams', 'Password',
    function ($rootScope, $scope,$location, $route, $routeParams, Password) {
		
		
	    $rootScope.$on('event:close-term', function() {
	    	if(term){
	    		term.write('disconnecting...\r\n');
	    		term.destroy();	
	    	}
	    	 
	    });
	
	 	$scope.containerId =  $routeParams.containerId;
	 	var stompClient = null;
	 	var term = null ;
	 	var socket = new SockJS('/log/register');
	 	stompClient = Stomp.over(socket);
      	var onConnect = function(frame) {
            console.log('Connected: ' + frame);
            term = new Terminal({
 	  	       cols: 150,
 	  	       rows: 35,
 	  	       useStyle: true,
 	  	       screenKeys: false
 		     });
            
            term.on('title', function(title) {
     	       document.title = title;
     	     });
     	
     	     var divTerminal = document.getElementById("terminal");
     	     term.open(divTerminal);
     	     
     	     term.write('Waiting for log from server..\r\n');
     	     
     	     
                 
            var onLogMessage = function(log) {
               term.write(JSON.parse(log.body));
             };
              
           stompClient.subscribe("/topic/log/"+$scope.containerId,onLogMessage)
        };
          
    	var onErr = function(err) {
            term.write(err.headers.message);
          };
          
        stompClient.connect({},onConnect,onErr);
	 	
    }]);


houstonApp.controller('DockerHostMachineController', ['$scope', 'resolvedDockerHostMachine', 'DockerHostMachine',
                                                      function ($scope, resolvedDockerHostMachine, DockerHostMachine) {

                                                          $scope.dockerhostmachines = resolvedDockerHostMachine;

                                                          $scope.create = function () {
                                                              DockerHostMachine.save($scope.dockerhostmachine,
                                                                  function () {
                                                                      $scope.dockerhostmachines = DockerHostMachine.query();
                                                                      $('#saveDockerHostMachineModal').modal('hide');
                                                                      $scope.clear();
                                                                  });
                                                          };

                                                          $scope.update = function (dockerhostmachine) {
                                                              $scope.dockerhostmachine = dockerhostmachine;
                                                              $('#saveDockerHostMachineModal').modal('show');
                                                          };

                                                          $scope.delete = function (id) {
                                                              DockerHostMachine.delete({id: id},
                                                                  function () {
                                                                      $scope.dockerhostmachines = DockerHostMachine.query();
                                                                  });
                                                          };

                                                          $scope.clear = function () {
                                                              $scope.dockerhostmachine = {};
                                                          };
                                                      }]);
