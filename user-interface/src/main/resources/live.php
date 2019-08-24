<?php
$cluster = Cassandra::cluster()->build();

$keyspace = 'coinprice';
$datetime = (string)date('Y-m-d H:i', time());
//echo $datetime ;
$secondtime = 	(int)date('s', time());


// creating session with cassandra scope by keyspace
$session = $cluster->connect($keyspace);

// verifying connection with database
if(!$session) {
  echo "Error - Unable to connect to database";
}
$statement = $session->prepare( 'SELECT * FROM bitcoinprice where datetime =? and secondtime < ?  order by secondtime  desc limit 1 ALLOW FILTERING' );

$result = $session->execute($statement, new Cassandra\ExecutionOptions(array(
    'arguments' => array($datetime, $secondtime)
    )));
$dataPoints = array();
foreach($result as $row){
array_push($dataPoints, array("x" => $row['datetime'].":".(string)$row['secondtime'], "y" => (int)$row['last']));
}
echo json_encode($dataPoints);

?>