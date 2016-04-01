/*
 * author: Rohit Narayanan
 */
Date.prototype.getDisplayDate = function() {
	return this.getDate() > 9 ? "" : "0" + this.getDate();
};

Date.prototype.getDisplayMonth = function() {
	return this.getMonth() > 9 ? "" : "0" + this.getMonth();
};

Date.prototype.addMilliseconds = function(count) {
	this.setMilliseconds(this.getMilliseconds() + Number(count));
};

Date.prototype.addSeconds = function(count) {
	this.setSeconds(this.getSeconds() + Number(count));
};

Date.prototype.addMinutes = function(count) {
	this.setMinutes(this.getMinutes() + Number(count));
};

Date.prototype.addHours = function(count) {
	this.setHours(this.getHours() + Number(count));
};

Date.prototype.addDate = function(count) {
	this.setDate(this.getDate() + Number(count));
};

Date.prototype.addMonth = function(count) {
	this.setMonth(this.getMonth() + Number(count));
};

Date.prototype.addYear = function(count) {
	this.setYear(this.getFullYear() + Number(count));
};