'use strict';

/** Controllers */
angular.module('imAlive.controllers', ['imAlive.services']).
    controller('AliveCtrl', function ($scope, $http, aliveModel) {
        $scope.customers = aliveModel.getCustomers();
        $scope.msgs = [];
        $scope.inputText = "";
        $scope.currentCustomer = $scope.customers[0];

        /** change current customer, restart EventSource connection */
        $scope.setCurrentCustomer = function (customer) {
            $scope.currentCustomer = customer;
            $scope.imAliveFeed.close();
            $scope.msgs = [];
            $scope.listen();
        };

        /** posting chat text to server */
        $scope.submitMsg = function () {
            $http.post("/chat", { text: $scope.inputText, user: $scope.user,
                time: (new Date()).toUTCString(), room: $scope.currentRoom.value });
            $scope.inputText = "";
        };

        /** handle incoming messages: add to messages array */
        $scope.addMsg = function (msg) { 
            $scope.$apply(function () { $scope.msgs.push(JSON.parse(msg.data)); });
        };

        /** start listening on messages from selected customer (or all messages depending on what is implemented below) */
        $scope.listen = function () {
            //$scope.imAliveFeed = new EventSource("/feed/imalive/" + $scope.currentCustomer.value);
        	$scope.imAliveFeed = new EventSource("/feed/imalive"); // all messages
            $scope.imAliveFeed.addEventListener("message", $scope.addMsg, false);
        };

        $scope.listen();
    });