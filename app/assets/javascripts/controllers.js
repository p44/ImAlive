'use strict';

/** Controllers */
angular.module('imAlive.controllers', ['imAlive.services']).
    controller('AliveCtrl', function ($scope, $http, aliveModel) {
        $scope.customers = aliveModel.getCustomers();
        $scope.msgs = [];
        $scope.inputText = "";
        $scope.currentCustomer = $scope.customers[0];
        $scope.count = 0;
        //$scope.data = [[0, 0], [1, 0], [2, 0], [3, 0], [4, 0], [5, 0]];
        //$scope.ph3 = $('#placeholder3');
        //$scope.plot = $.plot($scope.ph3, [$scope.data]); // [{ data: [[0, 0], [0, 0]], bars: {show: true, horizontal: true} }]
        //$scope.plot = $.plot($scope.ph3).data("plot")
        
        var data1 = [ [[0, 1], [1, 5], [2, 2]] ], data2 = [ [[0, 4], [1, 2], [2, 4]] ], curr = 1;
        $scope.data = data1;
        
        /** swap data sets */
        $scope.change = function(){ 
           if(curr === 1){ 
             $scope.data = data2;
             curr = 2;
           }else{
             $scope.data = data1;
             curr = 1;
           }
        };

        /** change current customer, restart EventSource connection */
        $scope.setCurrentCustomer = function (customer) {
            $scope.currentCustomer = customer;
            $scope.imAliveFeed.close();
            $scope.msgs = [];
            $scope.listen();
        };

        /** handle incoming messages: add to messages array */
        $scope.addMsg = function (msg) { 
        	var msgobj = JSON.parse(msg.data)
        	console.log('addMsg...');
            $scope.$apply(function () { $scope.msgs.push(msgobj); });
        };

        /** one time init the chart */
        $scope.initChart = function() {
        	console.log('initChart...');
        	//$scope.plot.setData($scope.data)
        	//$scope.plot.draw();
        	var ph3 = $('#placeholder3');
        	//var d = $scope.data
        	$.plot(ph3, [ [[0, 4], [1, 2], [2, 4]] ]);
        	ph3.show();
        }
        
        /** handle incoming messages: update chart */
        $scope.updateChart = function (msg) { 
        	var msgobj = JSON.parse(msg.data)
        	console.log(msgobj);
        	console.log('updateChart...');
        	//$scope.count = $scope.count + 1
        	//var dataSet = [[0, 0], [1, 6], [2,6], [3, 5], [4, 5], [5, 8]];
        	//$.plot($scope.ph3).setData(dataSet);
        	//$.plot($scope.ph3).draw();
        	$scope.change
        	//$.plot($scope.ph3, [$scope.data]);
        	//$scope.ph3.show();
        	var ph3 = $('#placeholder3');
        	$.plot(ph3, [ [[0, 1], [1, 5], [2, 2]] ]);
        	ph3.show();
        };

        /** start listening on messages from selected customer (or all messages depending on what is implemented below) */
        $scope.listen = function () {
            //$scope.imAliveFeed = new EventSource("/feed/imalive/" + $scope.currentCustomer.value);
        	$scope.imAliveFeed = new EventSource("/feed/imalive"); // all messages
            $scope.imAliveFeed.addEventListener("message", $scope.addMsg, false);
            $scope.imAliveFeed.addEventListener("message", $scope.updateChart, false);
        };

        $scope.initChart();
        $scope.listen();
});

