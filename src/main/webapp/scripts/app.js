'use strict';

/* App Module */

var houstonApp = angular.module('houstonApp', ['http-auth-interceptor', 'tmh.dynamicLocale',
    'ngResource', 'ngRoute', 'ngCookies', 'pascalprecht.translate', 'ui.bootstrap']);

houstonApp
    .config(['$routeProvider', '$httpProvider', '$translateProvider',  'tmhDynamicLocaleProvider',
        function ($routeProvider, $httpProvider, $translateProvider, tmhDynamicLocaleProvider) {
            $routeProvider
                .when('/login', {
                    templateUrl: 'views/login.html',
                    controller: 'LoginController'
                })
                .when('/signup', {
                    templateUrl: 'views/signup.html',
                    controller: 'SignUpController'
                })
                .when('/settings', {
                    templateUrl: 'views/settings.html',
                    controller: 'SettingsController'
                })
                .when('/password', {
                    templateUrl: 'views/password.html',
                    controller: 'PasswordController'
                })
                .when('/setpassword/:accountId', {
                    templateUrl: 'views/setpwd.html',
                    controller: 'SetPasswordController'
                })
                .when('/terminal/:containerId', {
                    templateUrl: 'views/terminal.html',
                    controller: 'TerminalController'
                })
                .when('/sessions', {
                    templateUrl: 'views/sessions.html',
                    controller: 'SessionsController',
                    resolve:{
                        resolvedSessions:['Sessions', function (Sessions) {
                            return Sessions.get();
                        }]
                    }
                })
                .when('/metrics', {
                    templateUrl: 'views/metrics.html',
                    controller: 'MetricsController',
                    resolve:{
                        resolvedMetrics:['Metrics', function (Metrics) {
                            return Metrics.get();
                        }]
                    }
                })
                .when('/logs', {
                    templateUrl: 'views/logs.html',
                    controller: 'LogsController',
                    resolve:{
                        resolvedLogs:['LogsService', function (LogsService) {
                            return LogsService.findAll();
                        }]
                    }
                })
                .when('/audits', {
                    templateUrl: 'views/audits.html',
                    controller: 'AuditsController'
                })
                .when('/logout', {
                    templateUrl: 'views/main.html',
                    controller: 'LogoutController'
                })
                .when('/appimagecfg', {
                    templateUrl: 'views/appimagecfgs.html',
                    controller: 'AppImageCfgController',
                    resolve:{
                        resolvedAppImageCfg: ['AppImageCfg', function (AppImageCfg) {
                            return AppImageCfg.query();
                        }]
                    }
                })
                .when('/appImgConfigWizard', {
                    templateUrl: 'views/appimagecfgsWizard.html',
                    controller: 'AppImgConfigWizardController'
                })
                .when('/serviceimagecfg', {
                    templateUrl: 'views/serviceimagecfgs.html',
                    controller: 'ServiceImageCfgController',
                    resolve:{
                        resolvedServiceImageCfg: ['ServiceImageCfg', function (ServiceImageCfg) {
                            return ServiceImageCfg.query();
                        }]
                    }
                })
                .when('/imageinfo', {
                    templateUrl: 'views/imageinfos.html',
                    controller: 'ImageInfoController',
                    resolve:{
                        resolvedImageInfo: ['ImageInfo', function (ImageInfo) {
                            return ImageInfo.query();
                        }]
                    }
                })                
                .otherwise({
                    templateUrl: 'views/main.html',
                    controller: 'MainController'
                })

            // Initialize angular-translate
            $translateProvider.useStaticFilesLoader({
                prefix: 'i18n/',
                suffix: '.json'
            });

            $translateProvider.preferredLanguage('en');

            $translateProvider.useCookieStorage();

            tmhDynamicLocaleProvider.localeLocationPattern('bower_components/angular-i18n/angular-locale_{{locale}}.js')
            tmhDynamicLocaleProvider.useCookieStorage('NG_TRANSLATE_LANG_KEY');
        }])
        .run(['$rootScope', '$location', 'AuthenticationSharedService', 'Account',
            function($rootScope, $location, AuthenticationSharedService, Account) {

            $rootScope.hasRole = function(role) {
                if ($rootScope.account === undefined) {
                    return false;
                }

                if ($rootScope.account.roles === undefined) {
                    return false;
                }

                if ($rootScope.account.roles[role] === undefined) {
                    return false;
                }

                return $rootScope.account.roles[role];
            };
            
            $rootScope.$on("$routeChangeStart", function(event, next, current) {

            	if(current) {
            		var template = current.loadedTemplateUrl;
            		if(template == "views/terminal.html") {
            			$rootScope.$broadcast('event:close-term');
            		}
            	}
                // Check if the status of the user. Is it authenticated or not?
                $rootScope.page = $location.path();
        		var currentPage = $location.path();
                if(currentPage.indexOf('/setpassword/') != 0) {
	                AuthenticationSharedService.authenticate().then(function(response) {
	                    if (response.data == '') {
	                        $rootScope.$broadcast('event:auth-loginRequired');
	                    } else {
	                        $rootScope.authenticated = true;
	                        $rootScope.login = response.data;
	                        $rootScope.account = Account.get();
	
	                        // If the login page has been requested and the user is already logged in
	                        // the user is redirected to the home page
	                        if ($location.path() === "") {
	                            $location.path('/appimagecfg').replace();
	                        }
	                        
	                        if ($location.path() === "/login") {
	                            $location.path('/').replace();
	                        }
	                    }
	                });
                }
            });

            // Call when the 401 response is returned by the client
            $rootScope.$on('event:auth-loginRequired', function(rejection) {
                $rootScope.authenticated = false;
                if ($location.path() !== "/" && $location.path() !== "" && $location.path() != "/signup") {
                    $location.path('/login').replace();
                }
            });

            // Call when the user logs out
            $rootScope.$on('event:auth-loginCancelled', function() {
                $rootScope.login = null;
                $rootScope.authenticated = false;
                $location.path('');
            });
        }]);

houstonApp.directive('upload', ['uploadManager', function factory(uploadManager) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            $(element).fileupload({
                dataType: 'text',
                add: function (e, data) {
                    uploadManager.add(data);
                },
                progressall: function (e, data) {
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    uploadManager.setProgress(progress);
                },
                done: function (e, data) {
                    uploadManager.setProgress(0);
                }
            });
        }
    };
}]);

houstonApp
.filter(
		'timeago',
		function() {
			return function(input) {
				if(input != null && input != ""){
					var substitute = function(stringOrFunction, number,
							strings) {
						var string = $.isFunction(stringOrFunction) ? stringOrFunction(
								number, dateDifference)
								: stringOrFunction;
						var value = (strings.numbers && strings.numbers[number])
								|| number;
						return string.replace(/%d/i, value);
					}, nowTime = (new Date()).getTime(), date = (new Date(
							input)).getTime(),
	
					strings = {
						prefixAgo : null,
						prefixFromNow : null,
						suffixAgo : "ago",
						suffixFromNow : "from now",
						seconds : "less than a minute",
						minute : "about a minute",
						minutes : "%d minutes",
						hour : "about an hour",
						hours : "about %d hours",
						day : "a day",
						days : "%d days",
						month : "about a month",
						months : "%d months",
						year : "about a year",
						years : "%d years"
					}, dateDifference = nowTime - date, words, seconds = Math
							.abs(dateDifference) / 1000, minutes = seconds / 60, hours = minutes / 60, days = hours / 24, years = days / 365, separator = strings.wordSeparator === undefined ? " "
							: strings.wordSeparator,
	
					prefix = strings.prefixAgo, suffix = strings.suffixAgo;
	
					words = seconds < 45
							&& substitute(strings.seconds, Math
									.round(seconds), strings)
							|| seconds < 90
							&& substitute(strings.minute, 1, strings)
							|| minutes < 45
							&& substitute(strings.minutes, Math
									.round(minutes), strings)
							|| minutes < 90
							&& substitute(strings.hour, 1, strings)
							|| hours < 24
							&& substitute(strings.hours, Math.round(hours),
									strings)
							|| hours < 42
							&& substitute(strings.day, 1, strings)
							|| days < 30
							&& substitute(strings.days, Math.round(days),
									strings)
							|| days < 45
							&& substitute(strings.month, 1, strings)
							|| days < 365
							&& substitute(strings.months, Math
									.round(days / 30), strings)
							|| years < 1.5
							&& substitute(strings.year, 1, strings)
							|| substitute(strings.years, Math.round(years),
									strings);
	
					return $
							.trim([ prefix, words, suffix ].join(separator));
				} else {
					return "-";
				}
			};
		});
