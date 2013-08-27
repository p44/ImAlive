'use strict';

/** Controllers */
angular.module('imAlive.controllers', ['imAlive.services']).
    controller('AliveCtrl', function ($scope, $http, aliveModel) {
        $scope.customers = aliveModel.getCustomers();
        $scope.msgs = [];
        $scope.inputText = "";
        $scope.currentCustomer = $scope.customers[0];
        $scope.data = [[[0, 1], [1, 5], [2, 2]]];

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
            $scope.$apply(function () { $scope.msgs.push(msgobj); });
        };
        
        /** handle incoming messages: update chart */
        $scope.updateChart = function (msg) { 
        	var msgobj = JSON.parse(msg.data)
        	console.log(msgobj);
        	var ph3 = $('#placeholder3');
        	//console.log(ph3);
        	$.plot(ph3, [ [[0, 0], [1, 1]] ], { yaxis: { max: 1 } });
        	ph3.show();
            // $scope.$apply(function () { $scope.msgs.push(msgobj); });
        };

        /** start listening on messages from selected customer (or all messages depending on what is implemented below) */
        $scope.listen = function () {
            //$scope.imAliveFeed = new EventSource("/feed/imalive/" + $scope.currentCustomer.value);
        	$scope.imAliveFeed = new EventSource("/feed/imalive"); // all messages
            $scope.imAliveFeed.addEventListener("message", $scope.addMsg, false);
            $scope.imAliveFeed.addEventListener("message", $scope.updateChart, false);
        };

        $scope.listen();
});

