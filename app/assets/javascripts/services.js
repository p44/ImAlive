'use strict';

/** imAlive service, provides customers (could also be loaded from server) */
angular.module('imAlive.services', []).service('aliveModel', function () {
    var getCustomers = function () {
        return [ {name: 'Govermnet Snoopers', value: '1'}, {name: 'Hackers Unlimited', value: '2'} ];
    };
    return { getCustomers: getCustomers };
});