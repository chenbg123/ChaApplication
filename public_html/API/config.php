<?php
	
	$servername = "localhost";
	$database = "chatbot";
	$username = "admin";
	$password = "admin";

	// Create connection

	$conn = mysqli_connect($servername, $username, $password, $database);

	// Check connection

	if (!$conn) {

	    die("Connection failed: " . mysqli_connect_error());

	}
	
	
	
?>