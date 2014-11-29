var angular = angular || {};
var moment = moment || function() {};

angular.module("worklist.filter", [])
  //We already have a limitTo filter built-in to angular,
  //let's make a startFrom filter
  .filter('startFrom', function() {
    return function(input, start) {
      start = +start; //parse to int
      return input.slice(start);
    }
  })

// Create filter to parse dates
.filter('parseDate', function() {
  var result = function(dateStr, formatStr) {
    var date = null;
    if (formatStr === null) {
      date = moment(dateStr);
    }
    else {
      date = moment(dateStr, formatStr);
    }
    return date.toDate();
  }
  return result;
})

// Filter to translate date to from now string (moment.js)
.filter('fromNow', function() {
  return function(date) {
    return moment(date).fromNow();
  }
})
;
