/*
 * Fuel UX Data components - static data source
 * https://github.com/ExactTarget/fuelux-data
 *
 * Copyright (c) 2012 ExactTarget
 * Licensed under the MIT license.
 */

var DataSourceTree = function(options) {
	this._data = options.data;
	this._delay = options.delay;
};

DataSourceTree.prototype = {

	data : function(options, callback) {
		var self = this;
		if(options.name){
			self._data = options.files;
		}
		
		setTimeout(function() {
			var data = $.extend(true, [], self._data);

			callback({
				data : data
			});

		}, this._delay)
	}

};
