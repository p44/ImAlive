$(function() {
   var d1 = [];
   for (var i = 0; i < 14; i += 0.5) {
    	d1.push([i, Math.sin(i)]);
   }
   var d2 = [[0, 3], [4, 8], [8, 5], [9, 13]];
   // A null signifies separate line segments
   var d3 = [[0, 12], [7, 12], null, [7, 2.5], [12, 2.5]];
   var ph = $('#placeholder');
   var ph2 = $('#placeholder2');
   $.plot(ph, [ d1, d2, d3 ]);
   $.plot(ph2, [ [[0, 0], [1, 1]] ], { yaxis: { max: 1 } });
});