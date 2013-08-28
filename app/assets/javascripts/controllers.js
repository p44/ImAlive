'use strict';

/** Controllers */
angular.module('imAlive.controllers', ['imAlive.services']).
    controller('AliveCtrl', function ($scope, $http, aliveModel) {
        $scope.customers = aliveModel.getCustomers();
        $scope.msgs = [];
        $scope.inputText = "";
        $scope.currentCustomer = $scope.customers[0];
        $scope.count = 0; // count all messages
        $scope.d1_0 = [ [1, 0] ];
        $scope.d1_1 = [ [2, 0] ];
        $scope.barData = [ $scope.d1_0, $scope.d1_1 ]
        var color0 = "#F89406";
        var color1 = "#3A87AD";
        $scope.data1 = [
                     {
                         label: "Government",
                         data: $scope.barData[0], // $scope.d1_0,
                         bars: {
                             show: true,
                             barWidth: 1,
                             fill: true,
                             lineWidth: 1,
                             order: 1,
                             fillColor: color0
                         },
                         color: color0
                     },
                     {
                         label: "Hackers",
                         data: $scope.barData[1], // $scope.d1_1,
                         bars: {
                             show: true,
                             barWidth: 1,
                             fill: true,
                             lineWidth: 1,
                             order: 2,
                             fillColor: color1
                         },
                         color: color1
                     }
                   ]
        var options1 = {
                xaxis: {
                	tickSize: [1, "each"],
                	tickLength: 0,
                	minTickSize: 1,
                    axisLabel: 'Customers',
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial, Helvetica, Tahoma, sans-serif',
                    axisLabelPadding: 5
                },
                yaxis: {
                	minTickSize: 1,
                    axisLabel: 'Count',
                    axisLabelUseCanvas: true,
                    axisLabelFontSizePixels: 12,
                    axisLabelFontFamily: 'Verdana, Arial, Helvetica, Tahoma, sans-serif',
                    axisLabelPadding: 5
                },
                grid: {
                    hoverable: true,
                    clickable: false,
                    borderWidth: 3
                },
                series: {
                    shadowSize: 1
                }
        };

        /** increment count value for the customer at index specified */
        $scope.incrementBar = function(i) {
        	// console.log('incrementBar ' + i)
        	$scope.data1[i].data[0][1] = $scope.data1[i].data[0][1] + 1
        	
        }

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
        	//console.log('addMsg...');
        	$scope.count += $scope.count + 1
            $scope.$apply(function () { $scope.msgs.push(msgobj); });
        };

        /** one time init the chart */
        $scope.initChart = function() {
        	console.log('initChart...');
        	var ph = $('#feedchart');
        	var pts = $scope.points;
        	var d = $scope.data1;
        	$.plot(ph, d, options1);
        }
        
        /** handle incoming messages: update chart */
        $scope.updateChart = function (msg) { 
        	var msgobj = JSON.parse(msg.data);
        	console.log(msgobj);
        	//console.log('updateChart...');
        	$scope.incrementBar(msgobj.cid -1);
        	var ph = $('#feedchart');
        	var d = $scope.data1;
        	$.plot(ph, d, options1);
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

