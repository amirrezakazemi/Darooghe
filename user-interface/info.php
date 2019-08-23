<!DOCTYPE html>
<html>
<head>
<title>Creating Dynamic Data Graph using PHP and Chart.js</title>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script type="text/javascript" src="Chart.min.js"></script>
</head>
<body>
    <div id="chart-container">
        <canvas id="mycanvas"></canvas>
    </div>

    <script>
 var ctx_live = document.getElementById("mycanvas");
                var myChart = new Chart(ctx_live, {
                  type: 'line',
                  data: {
                   labels: [],
                    datasets: [{
		    data: [],
			    borderColor: "#3e95cd",
        fill: false
                     // borderWidth: 1,
                      //backgroundColor:['#FF6384','#ffffff'],
                      
                    }]
	 },
	 options: {
    title: {
      display: true,
      text: 'coinprice'
    }
  }
                 /* options: {
                    responsive: true,
                    title: {
                      display: true,
                      text: "Chart.js - Dynamically Update Chart Via Ajax Requests",
                    },
                    legend: {
                      display: false
                    },

 
		  */
 });
 var getData = function() {
                  $.ajax({
		  url: 'http://localhost/live.php',
			dataType: "json",
                    success: function(result) {
                      // process your data to pull out what you plan to use to update the chart
		//	    console.log(result.x);
			    for(var i in result){
			if(myChart.data.labels[myChart.data.labels.length - 1]!= result[i].x){
				myChart.data.datasets[0].data.push(result[i].y);
				console.log(result[i].y, result[i].x);
			//	console.log(result[i]);
				myChart.data.labels.push(result[i].x);
			//	 myChart.data.labels.push(result[i].x - 1);

			}
			    }
			   
			   myChart.update();
		    }
	   });
 	
 }
 setInterval(getData, 1000);

   
</script>
</body>
</html>