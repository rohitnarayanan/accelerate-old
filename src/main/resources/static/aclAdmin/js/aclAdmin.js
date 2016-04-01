'use strict';

$.aclAdmin = {
	controllers : {},
	services : {},
	functions : {}
};

/**
 * Controller Instance
 * 
 * @param $scope
 * @param $rootScope
 * @param $location
 */
$.aclAdmin.controllers.dashboardController = function($scope, $rootScope,
		$route, $location, dashboardService) {
	$scope.checkString = dashboardService.test("HI");
};

/**
 * Controller Instance
 * 
 * @param $scope
 * @param $rootScope
 * @param $location
 */
$.aclAdmin.controllers.cacheController = function($scope, $rootScope, $route,
		$location, cacheService) {
	$rootScope.pageHeader = "Cache Home";
	if (!$.fn.DataTable.isDataTable('#cacheListDT')) {
		$.aclAdmin.functions.initCacheHomeDT();
	}

	cacheService.list().then(function(aCacheList) {
		var dataList = [];
		$.each(aCacheList, function(key, value) {
			value.id = key;
			dataList.push(value);
		});

		var dataTable = $('#cacheListDT').DataTable();
		dataTable.clear();
		dataTable.rows.add(dataList);
		dataTable.columns.adjust().draw();
	}, function(aResponse) {
		console.log(aResponse);
	});
};

/**
 * Service Function
 * 
 * @param $http
 * @param $q
 */
$.aclAdmin.services.dashboardService = function($http, $q) {
	/*
	 * Method Declaration
	 */
	return ({
		test : test
	});

	/**
	 * service functinon to fetch all users
	 */
	function test(aArg) {
		return aArg;
	}
};

/**
 * Service Function
 * 
 * @param $http
 * @param $q
 */
$.aclAdmin.services.cacheService = function($http, $q) {
	/*
	 * Method Declaration
	 */
	return ({
		list : list,
		get : get
	});

	/**
	 * service functinon to fetch all users
	 */
	function list() {
		var request = $http({
			method : "get",
			url : context_path + "/aclAdmin/cache/list"
		});

		return (request.then($.aclAdmin.serviceUtils.httpSuccess, _handleError));
	}

	/**
	 * service functinon to fetch all users
	 */
	function get(aCacheId) {
		var request = $http({
			method : "get",
			url : context_path + "/aclAdmin/cache/" + aCacheId
		});

		return (request.then($.aclAdmin.serviceUtils.httpSuccess, _handleError));
	}

	/**
	 * internal error handler
	 */
	function _handleError(aResponse) {
		return $.aclAdmin.serviceUtils.httpError(aResponse, $q);
	}
};

$.aclAdmin.functions.initCacheHomeDT = function() {
	var dataTableOptions = {
		"pageLength" : 5,
		"dom" : '<"top"f>rt<"bottom"ip><"clear">',
		"rowId" : 'id',
		"columns" : [ {
			"title" : "Cache Name",
			"data" : "name",
			"defaultContent" : ""
		}, {
			"title" : "Cache Size",
			"data" : "size",
			"defaultContent" : ""
		}, {
			"title" : "Initialized At",
			"data" : "initializedTime",
			"defaultContent" : ""
		}, {
			"title" : "Refreshed At",
			"data" : "lastRefreshedTime",
			"defaultContent" : ""
		} ]
	};

	var dataTable = $('#cacheListDT').DataTable(dataTableOptions);

	$('#cacheListDT tbody').on('click', 'tr', function() {
		// lse_AngularHelper.changeRoute('#cache/detail/'
		// + dataTable.row(this).id());
		alert("View Cache: " + dataTable.row(this).id());
	});
};

$.aclAdmin.serviceUtils = {
	httpSuccess : function(aResponse) {
		// console.log("response:" + JSON.stringify(aResponse.data));
		return (aResponse.data);
	},
	httpError : function(aResponse, aPromise) {
		if (!angular.isObject(aResponse.data) || !aResponse.data.message) {
			// console.log("error1: An unknown error occurred.");
			return (aPromise.reject("An unknown error occurred."));
		}

		// Otherwise, use expected error message.
		// console.log("error2:" + JSON.stringify(aResponse.data.message));
		return (aPromise.reject(aResponse.data.message));
	},
	serverError : function(aErrorMsg) {
		console.log("Error:" + aErrorMsg);
		$("#serverError").empty().text(aErrorMsg).show();
		$("#serverError ~ *").hide();
	},
	changeRoute : function(aPath) {
		$("#routingLink").attr("href", aPath).get(0).click();
	}
};

/*
 * Angular Module Declaration
 */
angular.module('aclAdmin', [ 'ngRoute' ]).config(
		[
				'$routeProvider',
				'$httpProvider',
				function($routeProvider, $httpProvider) {
					$routeProvider.when(
							'/dashboard',
							{
								templateUrl : context_path
										+ "/aclAdmin/view/dashboard",
								controller : 'dashboardController'
							}).when(
							'/cache/home',
							{
								templateUrl : context_path
										+ "/aclAdmin/view/cache/home",
								controller : 'cacheController'
							}).when(
							'/cache/detail/:cacheId',
							{
								templateUrl : context_path
										+ "/aclAdmin/view/cache/detail",
								controller : 'cacheController'
							}).otherwise({
						redirectTo : '/dashboard'
					});
				} ]).controller('dashboardController',
		$.aclAdmin.controllers.dashboardController).controller(
		'cacheController', $.aclAdmin.controllers.cacheController).service(
		'dashboardService', $.aclAdmin.services.dashboardService).service(
		'cacheService', $.aclAdmin.services.cacheService);