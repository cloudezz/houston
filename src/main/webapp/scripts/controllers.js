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

houstonApp.controller('AccountSettingsController', ['$scope','Cloud','resolvedOrganisations','resolvedUsers','resolvedTeams','OrgInfo','TeamInfo','UserInfo',
		 function ($scope,Cloud,resolvedOrganisations,resolvedUsers,resolvedTeams,OrgInfo,TeamInfo,UserInfo) {
	$scope.selection = "views/cloud.html";
	/* for users tab */
	
	$scope.breadcrumbs=[{text:"Organisation",jsFn:""}];
	
	$scope.isOrgCreate=false;
	$scope.isUserCreate=false;
	$scope.isTeamCreate=false;	
	
	$scope.organisations=resolvedOrganisations;
	$scope.users=resolvedUsers;
	$scope.teams=resolvedTeams;
	
	$scope.createNewOrg =function(){
		$scope.breadcrumbs=[{text:"Organisation",jsFn:"back();"},{text:"CreateOrganisation",jsFn:""}];
		$scope.isOrgCreate=true;
		$scope.isUserCreate=false;
		$scope.isTeamCreate=false;
	}
	$scope.createOrganisation=function(){	
		$scope.orgDTO={};
		$scope.orgDTO.orgName=$("#orgName").val();
		$scope.orgDTO.orgDesc=$("#orgDesc").val();
		OrgInfo.save($scope.orgDTO,
				function () {
			OrgInfo.query(function (data) {
				$scope.organisations=data;
			});
		});
	}	
	$scope.createNewUser =function(){
		$scope.breadcrumbs=[{text:"User",jsFn:"back();"},{text:"CreateUser",jsFn:""}];
		$scope.isOrgCreate=false;
		$scope.isUserCreate=true;
		$scope.isTeamCreate=false;
	}
	$scope.createUser=function(){	
		$scope.userDTO={};
		$scope.userDTO.firstName=$("#userFName").val();
		$scope.userDTO.lastName=$("#userLName").val();
		$scope.userDTO.email=$("#userEmail").val();
		UserInfo.save($scope.userDTO,
				function () {
			UserInfo.query(function (data) {
				$scope.users=data;
			});
		});
	}
	$scope.createNewTeam =function(){
		$scope.breadcrumbs=[{text:"Team",jsFn:"back();"},{text:"CreateTeam",jsFn:""}];
		$scope.isOrgCreate=false;
		$scope.isUserCreate=false;
		$scope.isTeamCreate=true;
		
		$scope.itemIds = [];

		/* To create picklist */
			$("#teamUsers").pickList({"afterAdd": function(event, obj){
				obj.items.each(function()
				{
					$scope.itemIds.push($(this).attr("data-value"));
				});
				alert("added" +$scope.itemIds);
			}});
	}
	$scope.createTeam=function(){	
		$scope.teamDTO={};
		$scope.teamDTO.teamName=$("#teamName").val();
		$scope.teamDTO.teamDesc=$("#teamDesc").val();
		$scope.teamDTO.teamOrg=$("#teamOrg").val();
		$scope.teamDTO.selectedUsers=$scope.itemIds;
		TeamInfo.save($scope.teamDTO,
				function () {
			TeamInfo.query(function (data) {
				$scope.teams=data;
			});
		});
	}		
	$scope.back=function(){
		 if($scope.isOrgCreate)
			 $scope.breadcrumbs=[{text:"Organisation",jsFn:""}];
		 if($scope.isUserCreate)
			 $scope.breadcrumbs=[{text:"User",jsFn:""}];
		 if($scope.isTeamCreate)
			 $scope.breadcrumbs=[{text:"Team",jsFn:""}];
		reset();
	}
	$scope.onOrgTabSelect=function(){
		$scope.breadcrumbs=[{text:"Organisation",jsFn:""}];
		reset();
	}
	$scope.onUserTabSelect=function(){
		$scope.breadcrumbs=[{text:"User",jsFn:""}];
		reset();
	}
	$scope.onTeamTabSelect=function(){
		$scope.breadcrumbs=[{text:"Team",jsFn:""}];
		reset();
	}
	function reset(){
		$scope.isOrgCreate=false;
		$scope.isUserCreate=false;
		$scope.isTeamCreate=false;
	}
	/* for users tab ends */
	
}]);

houstonApp.controller('CloudController', ['$scope', 'Cloud',
       		 function ($scope,Cloud) {
       	$scope.clouds = Cloud.query();
       	
       	$scope.names =[{name : "Amazon cloud", value : "amazon"},	{name : "Rackspace", value : "rackspace"}, {name : "Google cloud", value : "google"}];
       	$scope.cloud = new Object();
       	$scope.create = function () {
       		$('#saveCloudModal').modal('hide');
       		Cloud.save($scope.cloud,
       				function () {
       			Cloud.query(function (data) {
       				$scope.clouds = data;
       			});
       		});
       	};
       	
       }]);

houstonApp.controller('PermissionsController', ['$scope', 'Role',
            		 function ($scope, Role) {
            	$scope.roles = Role.query();
            	
            	$scope.role = new Object();
            	$scope.create = function () {
            		Role.save($scope.role,
            				function () {
            			Role.query(function (data) {
            				$scope.roles = data;
            			});
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

	$scope.appimagecfgsRows = [];
	AppImageCfg.query(function (data) {
		$scope.appimagecfgs  = data;
		AppConfigCommunicationService.setAppImgConfigs(data);
	});
	
		$scope.$watchCollection('appimagecfgs', function(newValue, oldValue) {
		    var appimagecfgsRows = [];
		    
		    var count = 0;
		    for (var i = 0; i < newValue.length; i++ ) {
		    	var app = formatAppImageConfig(newValue[i]);
		    	if(!appimagecfgsRows[count]){
		    		appimagecfgsRows.push([]);
		    	}
		    	appimagecfgsRows[count].push(app);
		    	if(count < 3){
		    		count++;
		    	} else {
		    		count = 0;
		    	}
		    }
		    $scope.appimagecfgsRows = appimagecfgsRows;
		});
		
		function formatAppImageConfig(appimagecfg){
			appimagecfg.starting = false;
			appimagecfg.stopping = false;
			var serviceToURLmap = {};
	        var exposedServices = appimagecfg.exposedServices;
	    	for(var k=0; k<exposedServices.length; k++){
	    		var instanceNo = exposedServices[k].instanceNo;
	    		var containerId = exposedServices[k].containerId;
	    		var serviceToURL = exposedServices[k].serviceToURL;
	    		for(var key in serviceToURL){
	    			if(serviceToURLmap[containerId + "_" + instanceNo]){
	    				serviceToURLmap[containerId + "_" + instanceNo].push({key : key, value : serviceToURL[key], instanceNo : instanceNo});
	    			} else {
	    				serviceToURLmap[containerId + "_" + instanceNo] = [];
	    				serviceToURLmap[containerId + "_" + instanceNo].push({key : key, value : serviceToURL[key], instanceNo : instanceNo});
	    			}
	    		}
	    	}
	    	var appImageCfgs = appimagecfg.appImageCfgs;
	    	for(var m=0; m<appImageCfgs.length; m++){
	    		if(appImageCfgs[m].container){
	    			appImageCfgs[m]['links'] = serviceToURLmap[appImageCfgs[m].container.containerId + "_" + appImageCfgs[m].instanceNo];
	    		}
	    	}
	    	var serImageCfgs = appimagecfg.serviceImageCfgs;
	    	for(var n=0; n<serImageCfgs.length; n++){
	    		if(serImageCfgs[n].container){
	    			serImageCfgs[n]['links'] = serviceToURLmap[serImageCfgs[n].container.containerId + "_" + serImageCfgs[n].instanceNo];
	    		}
	    	}
			return appimagecfg;
		}
				
        console.log($scope.appimagecfgs);

        $scope.start = function (appimagecfg, index1, index2) {
        	$scope.appimagecfgsRows[index1][index2].starting = true;
        	$("#progressBar" + appimagecfg.id).css("width", "0%");
        	var percent = 0;
        	progress();
        	function progress(){
        		if(appimagecfg.starting){
                	$timeout(function(){
                		percent = percent + 10;
                		$("#progressBar" + appimagecfg.id).css("width", percent+"%");
                		if(percent < 80){
                			progress();
                		}
                	}, 500);
        		} else {
        			$("#progressBar" + appimagecfg.id).css("width", "100%");
        		}
        	};
        	
        	AppImageService.start(appimagecfg.id, function (data, status) {
        		$scope.appimagecfgsRows[index1][index2].starting = false;
        		$("#progressBar" + appimagecfg.id).css("width", "100%");
        		if(status == 200 ) {
        			$rootScope.msg.update({
						message:'Machine Started!',
						type: 'success',
						showCloseButton: true
        			});
        			var updatedApp = AppImageCfg.get({id : $scope.appimagecfgsRows[index1][index2].id}, function(){
        				$scope.appimagecfgsRows[index1][index2] = formatAppImageConfig(updatedApp);
        			});
        			AppImageCfg.query(function (data) {
						// $scope.appimagecfgs = data;
						AppConfigCommunicationService.setAppImgConfigs(data);
        			});
        		} else {
    				$rootScope.msg.update({
						message:'Machine was not started : Error is : '+data.error+'!',
						type: 'error',
						showCloseButton: true
    				});        		
        		}
        	});
        };
        
        $scope.stop = function (appimagecfg, index1, index2) {
        	$scope.appimagecfgsRows[index1][index2].stopping = true;
        	$("#progressBar" + appimagecfg.id).css("width", "0%");
        	var percent = 0;
        	progress();
        	function progress(){
        		if(appimagecfg.stopping){
            	$timeout(function(){
            		percent = percent + 10;
            		$("#progressBar" + appimagecfg.id).css("width", percent+"%");
            		if(percent < 80){
            			progress();
            		}
            	}, 500);
        		} else {
        			$("#progressBar" + appimagecfg.id).css("width", "100%");
        		}
        	};
        	
        	AppImageService.stop(appimagecfg.id, function (data, status) {
        		$scope.appimagecfgsRows[index1][index2].stopping = false;
        		$("#progressBar" + appimagecfg.id).css("width", "100%");
        		if(status == 200 ) {
        			$rootScope.msg.update({
						message:'Machine Stopped!',
						type: 'success',
						showCloseButton: true
        			});
        			var updatedApp = AppImageCfg.get({id : $scope.appimagecfgsRows[index1][index2].id}, function(){
        				$scope.appimagecfgsRows[index1][index2] = formatAppImageConfig(updatedApp);
        			});
	        		AppImageCfg.query(function (data) {
						// $scope.appimagecfgs = data;
						AppConfigCommunicationService.setAppImgConfigs(data);
        			});
        		} else {
	        		$rootScope.msg.update({
						message:'Machine was not stopped :: Error is : '+data.error+'!',
						type: 'error',
						showCloseButton: true
					});
        		}
        	});
        };
        
	$scope.editConfig=function(config){
		AppConfigCommunicationService.setConfigToEdit(config,function openWizardToEdit(configData){
			location.href = "#/appImgConfigWizard";
		});
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
				// $log.info('Modal dismissed at: ' + new Date());
			});
        };
        
        $scope.changeToLightImage = function(event){
        	var src = $(event.target).attr('src');
        	src = src.replace("dark", "light"); 
        	$(event.target).attr('src', src);
        };
        
        $scope.changeToDarkImage = function(event){
        	var src = $(event.target).attr('src');
        	src = src.replace("light", "dark"); 
        	$(event.target).attr('src', src);
        };
        
        $scope.runScript = function(id){
        	$rootScope.selectedAppId = id;
        	$location.path("/runScript");
        };
    }]);

houstonApp.controller('AppImgConfigWizardController',['$rootScope','$scope','$compile','AppImageCfg','ImageInfo','ServiceImageInfo','AppImageService','AppConfigCommunicationService','defaultConfigs',
  function($rootScope,$scope,$compile,AppImageCfg,ImageInfo,ServiceImageInfo,AppImageService,AppConfigCommunicationService,defaultConfigs){

	$scope.service;				
	$scope.selectedImgDesc;	
	$scope.appDTO={};
	$scope.appImageCfgDTO = {};					
	var wizard;		        
	var serviceWizard;
	
	$scope.setServiceUI  = function(serviceId,serviceName){
		$scope.formElementHolder=new Object();		
		$scope.setService(serviceId,serviceName);
	}
	$scope.setSubServiceUI  = function(serviceId,serviceName){
		$scope.serviceFormElementHolder=new Object();
		$scope.setSubService(serviceId,serviceName);
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
        	if($scope.service==null){
        		$scope.prevService=serviceId;
        	}else{
        		$scope.prevService= $scope.service;
        	}
			 $scope.service = serviceId;
			 $scope.appImageCfgDTO.imageName = serviceName; 
	}
	
	$scope.setServiceWithName=function(serviceName){
		var serviceId;
		angular.forEach($scope.serviceImages, function(item) {
			if(item.imageName==serviceName){
				serviceId=item.id;
			}
		});
		$scope.setService(serviceId,serviceName);
	}
	
	$scope.$on('$viewContentLoaded', function(){
		$scope.openWizard();
	});

	$scope.$on('$destroy', function () {
		$scope.isAppEditCtxt=false;
		AppConfigCommunicationService.setConfigToEdit(null);
		});
	
	$scope.getImgInitialStyle=function(serviceId,imageName){
		if($scope.appImageCfgDTO.imageName==imageName){
			$("#app"+serviceId+"").css('border-color', 'grey');
		}
	}
	
	$scope.getSerImgInitialStyle=function(serviceId,imageName){
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
		if($scope.isServiceEditCtxt && $scope.serviceDTO.imageName!=serviceName){
			$scope.serviceFormElementHolder=new Object();				
		}
			 if($scope.subService!=null){
	        	 serviceWizard.find("#service"+$scope.subService+"").css('border-color', 'white');
	             }
	        var curSel=$("#service"+serviceId+""); 
	        serviceWizard.find("#service"+serviceId+"").css('border-color', 'grey');    
	    
	        if($scope.subService==null){
        		$scope.prevSubService=serviceId;
        	}else{
        		$scope.prevSubService= $scope.subService;
        	}    
		$scope.subService = serviceId;
		$scope.serviceDTO.imageName = serviceName;
	}

	$scope.create = function (callback) {
		AppImageCfg.save($scope.appDTO,
				function () {
			AppImageCfg.query(function (data) {
				AppConfigCommunicationService.setAppImgConfigs(data);
			});
			callback();
		});
	};
	
	$scope.update = function (callback) {
		AppImageCfg.update($scope.appDTO,
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

	$scope.updateService=function(id){
		angular.forEach($scope.serviceDTOList, function(item) {
			if(item.id==id){
				$scope.serviceDTO=item;
				$scope.openServiceWizard(true);
			}});
	}
	$scope.deleteService=function(id){
		angular.forEach($scope.serviceDTOList, function(item) {
			if(item.id!=null && item.id==id){
				$scope.serviceDTOList.splice($.inArray(item, $scope.serviceDTOList),1);
			}});
	}

	$scope.restoreDefaultScript=function () {
		$scope.appImageCfgDTO.initScript=$scope.defaultScript;
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
		var editItem;
		
		if(card.nwizard!=undefined){
			service=$scope.service;
			wizardCard= card.nwizard;
			errorSpan=$("#errorSpan");
			list=AppConfigCommunicationService.getAppImgConfigs();
			input=card.el.find("#appName");
			editItem=$scope.appDTO;
		}		           
		else{
			service=$scope.subService;
			wizardCard= card.wizard;
			errorSpan=serviceWizard.find("#serviceErrorSpan");
			list=$scope.serviceDTOList;
			input=card.el.find("#serviceAppName");
			editItem=$scope.serviceDTO;
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
				if($scope.isAppEditCtxt ||$scope.isServiceEditCtxt ){
				if(item.id!=editItem.id && item.appName==name){		    		
					wizardCard.errorPopover(input, "Name is not unique.Please use a different name");
					valid= false;		    	
				}}
				else{
					if(item.appName==name){		    		
						wizardCard.errorPopover(input, "Name is not unique.Please use a different name");
						valid= false;		    	
					}	
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
		
		$scope.formValidationCtxt=true;
	
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
		if(valid){
			$scope.formValidationCtxt=false;
		}
		return valid;		 
	}	
	/* Validation functions ends */		

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

	function createReqMap(formId,curFormEl){
		var reqMap=new Object();
		angular.forEach(curFormEl,function(item){
			reqMap[formId+"_"+item.name]=item.optional;
		});
		return reqMap;
	}

	/* Service wizard creation */
		
	$scope.openServiceWizard = function (isEdit) {
		$scope.isServiceEditCtxt=isEdit;
		if(!$scope.isServiceEditCtxt){
			$scope.serviceDTO={};
		}
		$scope.serviceFormElementHolder=new Object(); 
		if($scope.isServiceEditCtxt){
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
			if($scope.isServiceEditCtxt){
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
		
		if($scope.isServiceEditCtxt){
			enableDefaultConfig($scope.serviceDTO.memory,$scope.serviceDTO.cpuShares);
		}	
            	serviceWizard.on("submit", function(serviceWizard) { 
	       			for ( var i = 0; i < $scope.currentServiceForm.formElement.length; i++){
        				var item = $scope.currentServiceForm.formElement[i];  
        				if(item.type=='file-upload'){        					
        					$scope.serviceFormElementHolder[item.name]=$rootScope.servicefileSelected;
        				}
        			}
	       	$scope.appImageCfgDTO.appName=$scope.appDTO.appName;
			$scope.serviceDTO.memory=$("#service_memory").slider('getValue');
			$scope.serviceDTO.cpuShares=$("#service_cpuShares").slider('getValue');	       			
			$scope.serviceDTO.environmentMapping=$scope.serviceFormElementHolder;
			$scope.serviceDTO.noOfInstance=1;
			/* reset slider */
			$("#service_memory").slider('setValue',128);
			$("#service_cpuShares").slider('setValue',1);
			$("#smemorySliderVal").text("");
			$("#scpuSharesSliderVal").text("");

			$("#serviceWizardButtons").find(":button").removeClass("active");

			console.log($scope.serviceDTO );
			if($.inArray($scope.serviceDTO, $scope.serviceDTOList)==-1){
				$scope.serviceDTO.groupName=$scope.serviceDTO.appName;
			}
			if($scope.isServiceEditCtxt){
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
			if(!$scope.formValidationCtxt){
				$scope.loadForm($scope.subService,'serviceFormDiv','serviceFormElementHolder');
			}
		});

		serviceWizard.on("closed", function(serviceWizard) {
			$('.modal-backdrop').remove();
			resetServiceWizard(serviceWizard); 
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
		$scope.isServiceEditCtxt=false;
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
		if($scope.isServiceEditCtxt){
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
		if($scope.isServiceEditCtxt){
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
		
		if(AppConfigCommunicationService.getConfigToEdit()!=null){
			$scope.appDTO=AppConfigCommunicationService.getConfigToEdit();
			$scope.isAppEditCtxt=true;
		}
		
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
			$scope.appImageCfgDTO.noOfInstance=$("#noOfInstance").slider('getValue');	
			$scope.appDTO.tags=$("#tags").val();
			$scope.appImageCfgDTO.environmentMapping=$scope.formElementHolder;
			removeAndAddAppImg();
			$scope.appDTO.serviceImages=$scope.serviceDTOList;

			/* reset slider */
			$("#memory").slider('setValue',128);
			$("#cpuShares").slider('setValue',1);
			$("#noOfInstance").slider('setValue',1);
			$("#memorySliderVal").text("");
			$("#cpuSharesSliderVal").text("");
			$("#noOfInstanceSliderVal").text("");
			$("#wizardButtons").find(":button").removeClass("active");

			console.log( $scope.appDTO );	       				       			

				$scope.create(function(){	       				
					wizard.trigger("success");
					wizard.hideButtons();
					wizard._submitting = false;
					wizard.showSubmitCard("success");
					wizard.updateProgressBar(0);	       				 
				});
		
			$scope.isAppEditCtxt=false;
			AppConfigCommunicationService.setConfigToEdit(null);
		});
		function removeAndAddAppImg(){
			angular.forEach($scope.appDTO.appImages,function(appImg)
			{
				if(appImg.id==$scope.appImageCfgDTO.id){
					$scope.appDTO.appImages.splice($.inArray(appImg,$scope.appDTO.appImages),1);
				}
			});
			$scope.appDTO.appImages.push($scope.appImageCfgDTO);
		}
		wizard.on("incrementCard", function(wizard) {
			var activeCard=	wizard.getActiveCard();
			if(activeCard.name=="card3"){
				 if(!$scope.formValidationCtxt){
					 $scope.loadForm($scope.service,'formDiv','formElementHolder');
					 }
			}
			if(activeCard.name=="card4"){
				if($scope.prevService==null || ($scope.prevService!=null  && $scope.service!=$scope.prevService))
					$scope.loadDefaultScript($scope.service);
			}
		});

		wizard.on("closed", function(wizard) {
			$('.modal-backdrop').remove();
			$scope.isAppEditCtxt=false;
			AppConfigCommunicationService.setConfigToEdit(null);
		});

		wizard.on("reset", function(wizard) {
			wizard.setSubtitle("");
			$('.wizard-cards').find('input:text').val('');
		});

		wizard.el.find(".wizard-success .im-done").click(function() {
			wizard.reset().close();
			$('.modal-backdrop').remove();
			$(".wizard").remove();
			$scope.isAppEditCtxt=false;
			AppConfigCommunicationService.setConfigToEdit(null);
		});

		wizard.el.find(".wizard-success .create-another-server").click(function() {
			wizard.reset();
			$scope.isAppEditCtxt=false;
			AppConfigCommunicationService.setConfigToEdit(null);
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
		$scope.formElementHolder=new Object();
		$scope.serviceFormElementHolder=new Object(); 

		$scope.serviceImages = {};
		if(!$scope.isAppEditCtxt){
			$scope.appDTO = new Object();
			$scope.appImageCfgDTO = new Object();
			$scope.appDTO.appImages=[];
			$scope.service=null;
			$scope.appImageCfgDTO.imageName=null;
		}
		else{
			if($scope.appDTO!=null){
				$scope.appImageCfgDTO=$scope.appDTO.appImages[0];
				var envMapping=$scope.appImageCfgDTO.environmentMapping;
				angular.forEach(envMapping,function(value,key){
				$scope.formElementHolder[key]=value;
			});
				$scope.serviceDTOList=$scope.appDTO.serviceImages;
		}
		}
		ImageInfo.query(function(data) {
			$scope.serviceImages = data;
			$scope.setServiceWithName($scope.appImageCfgDTO.imageName);
		});
		createWizardSliders();
		createTagsInput(); 
	}
	function createTagsInput(){
		$('#tags').tagsinput();
		if($scope.isAppEditCtxt && $scope.appDTO!=null){
			var tags=$scope.appDTO.tags;
			angular.forEach(tags,function(tagsItem){
					$('#tags').tagsinput('add',tagsItem);
				});
			}
	}
	function createWizardSliders(){
		/* for sliders in wizard */
		$("#memory").slider({
			tooltip:"hide"
		});
		$("#memory").on('slide', function(slideEvt) {
			var val=$("#memory").slider('getValue');			
			$("#memorySliderVal").text(getMemDisplayVal(val));
		});

		$("#cpuShares").slider({
			tooltip:"hide"
		});
		$("#cpuShares").on('slide', function(slideEvt) {
			var val=$("#cpuShares").slider('getValue')+" cpu";
			$("#cpuSharesSliderVal").text(val);
		});
		$("#noOfInstance").slider({
			tooltip:"hide"
		});
		$("#noOfInstance").on('slide', function(slideEvt) {
			var val=$("#noOfInstance").slider('getValue');
			$("#noOfInstanceSliderVal").text(val);
		});
		if($scope.isAppEditCtxt && $scope.appDTO!=null){
			var appCfg=$scope.appImageCfgDTO;
			$("#memory").slider('setValue',appCfg.memory!=null?appCfg.memory:defaultConfigs.TINY_MEMORY);
			$("#cpuShares").slider('setValue',appCfg.cpuShares!=null?appCfg.cpuShares:defaultConfigs.TINY_CPU);
			$("#noOfInstance").slider('setValue',appCfg.noOfInstance!=null?appCfg.noOfInstance:1);
		}
		else
		{
			$("#memory").slider('setValue',defaultConfigs.TINY_MEMORY);
			$("#cpuShares").slider('setValue',defaultConfigs.TINY_CPU);
			$("#noOfInstance").slider('setValue',1);
		}
		$("#memorySliderVal").text(getMemDisplayVal($("#memory").slider('getValue')));
		$("#cpuSharesSliderVal").text($("#cpuShares").slider('getValue')+" cpu");
		$("#noOfInstanceSliderVal").text($("#noOfInstance").slider('getValue'));
	}
	function getMemDisplayVal(val){
		if(val>1024)
			val= val/1024+" gb";
		else 
			val= val+" mb";
		return val;
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
		if(formId=='serviceFormDiv'){ 
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


houstonApp.controller('DeploymentScriptController', ['$scope','$rootScope', 'resolvedDeploymentScript', 'DeploymentScript', '$http', '$modal',
                                                    function ($scope,$rootScope, resolvedDeploymentScript, DeploymentScript, $http, $modal) {

        $scope.deploymentScripts = resolvedDeploymentScript;
        $scope.files = [];
        $scope.fileChange = function(files){
        	$scope.files = files;
			$("#uploadStatus").hide();
			$("#uploadStatus").html("");
			/*
			 * for (var i = 0; i < ArticleTab.files.length; i++){
			 * setFileNameSize(ArticleTab.files[i].name,
			 * ArticleTab.files[i].size); }
			 */
        };

    	var sendFileToServer = function(formData, status){
    		$("#progressBar").show();
    		var url = document.URL;
    		var url1 = url.split("#")[0];
    	    var uploadURL = url1 + "app/rest/deploymentScript/create"; // Upload
																		// URL
    	    var jqXHR=$.ajax({
    	            xhr: function() {
    	            var xhrobj = $.ajaxSettings.xhr();
    	            if (xhrobj.upload) {
    	                    xhrobj.upload.addEventListener('progress', function(event) {
    	                        var percent = 0;
    	                        var position = event.loaded || event.position;
    	                        var total = event.total;
    	                        if (event.lengthComputable) {
    	                            percent = Math.ceil(position / total * 100);
    	                        }
    	                        // Set progress
    	                        status.setProgress(percent);
    	                    }, false);
    	                }
    	            return xhrobj;
    	        },
    	    url: uploadURL,
    	    type: "POST",
    	    contentType:false,
    	    processData: false,
    	        cache: false,
    	        data: formData,
    	        success: function(data){
    	        	status.setProgress(100);
    	        	$("#uploadStatus").show();
    	        	if(data.message){
    	        		$("#uploadStatus").html(data.message);
    	        		$('#deploymentScriptModal').modal('hide');
                        $scope.deploymentScripts = DeploymentScript.query();
                        $scope.clear();
    	        	} else {
    	        		$("#uploadStatus").html(data.error);
    	        	}
    	        }
    	    });
    	}
        
    	var createStatusbar = function(){
    	    this.setProgress = function(progress){      
    	        var progressBarWidth = progress * $("#progressBar").width()/ 100; 
    	        $("#progressBar").find('div').animate({ width: progressBarWidth }, 10).html(progress + "% ");
    	        if(parseInt(progress) >= 100)
    	        {
    	        	$("#progressBar").hide();
    	        }
    	    };
    	}

        $scope.create = function () {
     	   for (var i = 0; i < $scope.files.length; i++)
    	   {
    	        var fd = new FormData();
    	        fd.append('file', $scope.files[i]);
    	        
    			var scriptName = $scope.deploymentScript.scriptName;
    			var description = $scope.deploymentScript.desc;
    	        fd.append('scriptName', scriptName);
    	        fd.append('description', description);
    	 
    	        var status = new createStatusbar(); // Using this we can set
													// progress.
    	        sendFileToServer(fd, status);
    	   }
        };

        $scope.update = function (id) {
            $scope.deploymentScript = DeploymentScript.get({id: id});
            $('#deploymentScriptModal').modal('show');
        };
        
        $scope.delete = function (id) {
        	var modalInstance = $modal.open({
				templateUrl : 'deleteConfirm.html',
				controller : DeleteModalCtrl,
				scope : $scope,
				resolve : {
					AppImageCfg : function() {
						return DeploymentScript;
					}, 
					
					id: function () {
						return id;
					}
				}
			});
	
			modalInstance.result.then(function(selectedItem) {
				$scope.selected = selectedItem;
			}, function() {
				// $log.info('Modal dismissed at: ' + new Date());
			});
        };

        $scope.clear = function () {
            $scope.deploymentScript = {};
        };
    }]);

houstonApp.controller('RunScriptController', ['$scope','$rootScope', '$location', 'DeploymentScript', 'AppImageCfg', 'AppImageService', '$http',
                                                     function ($scope,$rootScope, $location, DeploymentScript, AppImageCfg, AppImageService, $http) {
	
	if($rootScope.selectedAppId){
		$scope.deploymentScripts = DeploymentScript.query();
		
		$scope.app = AppImageCfg.get({id : $rootScope.selectedAppId}, function(){
			
		});
	} else {
		$location.path("/appimagecfg");
	}
	
	$scope.toggleSelect = function(imageConfig){
		if(imageConfig.selected){
			imageConfig.selected = false;
		} else {
			imageConfig.selected = true;
		}
	};
	
    $scope.isActive = function (viewLocation) { 
        return viewLocation === $location.path();
    };
    
    
    $scope.runAppScript = function(){
    	var selectedScript = $('#deploymentScript').val();
    	var command = $("#command").val();
    	
    	var selectedAppImageConfigs = [];
    	var selectedServiceImageConfigs = [];
    	
    	var appImageConfigs = $scope.app.appImageCfgs;
    	for(var i=0; i<appImageConfigs.length; i++){
    		if(appImageConfigs[i].selected){
    			selectedAppImageConfigs.push(appImageConfigs[i].id);
    		}
    	}
    	
    	var serviceImageConfigs = $scope.app.serviceImageCfgs;
    	for(var i=0; i<serviceImageConfigs.length; i++){
    		if(serviceImageConfigs[i].selected){
    			selectedServiceImageConfigs.push(serviceImageConfigs[i].id);
    		}
    	}
    	if(!command)
    		command = "";
    	
    	var data = "id="+$rootScope.selectedAppId+ "&scriptId=" + selectedScript + "&command=" +command+ "&appImageConfigs=" + selectedAppImageConfigs + "&serviceImageConfigs=" + selectedServiceImageConfigs;
    	AppImageService.runAppScript(data, function(data, status){
    		
    	});
    }
}]);

function DeleteModalCtrl($scope, $timeout, $modal, $modalInstance,$rootScope,DeploymentScript, id){
	$scope.ok = function() {
        	DeploymentScript.delete({id: id},
        			 function () {
		            $scope.$parent.deploymentScripts = DeploymentScript.query();
					$rootScope.msg.update({
						message:'Deleted Deployment Script!',
						type: 'success',
						showCloseButton: true
					});
	            },function(httpResponse){
					$rootScope.msg.update({
						message:'Could not delete Deployment Script.Error is : '+httpResponse.data.error+'!',
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
     	    
     	     stompClient.subscribe('/sockjs/log/init/'+$scope.containerId, function(msg){
            	term.write(JSON.parse(msg.body));
            });
     	     
                 
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

