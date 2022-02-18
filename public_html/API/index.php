<?php
	
		
	if(isset($_GET["p"]))
	{
		
		if($_GET["p"]=="bootChat")
		{
			bootChat();
		}
		else
		if($_GET["p"]=="Admin_Login")
		{
			Admin_Login();
		}
		else
		if($_GET["p"]=="showChatLevelBased")
		{
			showChatLevelBased();
		}
		
		//admin panel services
		else
		if($_GET["p"]=="addNewQuestion")
		{
			addNewQuestion();
		}
		else
		if($_GET["p"]=="addNewSuggestion")
		{
			addNewSuggestion();
		}
	}
	else
	{
		echo"Not Found";
    }
	
	
	function Admin_Login()
	{   
	   	require_once("config.php");
	    $input = @file_get_contents("php://input");
	    $event_json = json_decode($input,true);
		//print_r($event_json);
		//0= owner  1= company 2= ind mechanic
		
		if(isset($event_json['email']) && isset($event_json['password']) )
		{
			$email=htmlspecialchars(strip_tags($event_json['email'] , ENT_QUOTES));
			$password=strip_tags($event_json['password']);
		
			
			$log_in="select * from admin where email='".$email."' and pass='".md5($password)."' ";
			$log_in_rs=mysqli_query($conn,$log_in);
			
			if(mysqli_num_rows($log_in_rs))
			{
				$array_out = array();
				 $array_out[] = 
					//array("code" => "200");
					array(
						"response" => "login success"
					);
				
				$output=array( "code" => "200", "msg" => $array_out);
				print_r(json_encode($output, true));
			}	
			else
			{
			    
    			$array_out = array();
    					
        		 $array_out[] = 
        			array(
        			"response" =>"Error in login");
        		
        		$output=array( "code" => "201", "msg" => $array_out);
        		print_r(json_encode($output, true));
			}
			
			
			
		}
		else
		{
			$array_out = array();
					
			 $array_out[] = 
				array(
				"response" =>"Json Parem are missing");
			
			$output=array( "code" => "201", "msg" => $array_out);
			print_r(json_encode($output, true));
		}
	}
	
	
	function bootChat()
	{
		require_once("config.php");
	    $input = @file_get_contents("php://input");
	    $event_json = json_decode($input,true);
		//print_r($event_json);
		//0= owner  1= company 2= ind mechanic
		
		$query=mysqli_query($conn,"select * from chatBoot ");
		mysqli_set_charset($conn, "utf8");	
		$array_out = array();
		while($row=mysqli_fetch_array($query))
		{	
			 $array_out[] = 
				//array("code" => "200");
				array(
					"id" => $row['id'],
					"question" => strtolower($row['question']),
					"answers" => ucfirst($row['answers']),
					"level" => $row['level']
				);
			 
		}
		$output=array("code" => "200", "msg" => $array_out);
		print_r(json_encode($output, true));
	}
	
	
	
	function showChatLevelBased()
	{
	   
	    require_once("config.php");
	    $input = @file_get_contents("php://input");
	    $event_json = json_decode($input,true);
		//print_r($event_json);
		//0= owner  1= company 2= ind mechanic
		
		$query=mysqli_query($conn,"select * from chatBoot where level='0' ");
			
		$array_out = array();
		while($row=mysqli_fetch_array($query))
		{
		    
		    $query1=mysqli_query($conn,"select * from chatBoot where level='".$row['id']."' ");
			$array_out1 = array();
			while($row1=mysqli_fetch_array($query1))
    		{   
    		    $array_out1[] =
    		    array(
					"id" => $row1['id'],
					"question" => strtolower(htmlentities($row1['question'])),
					"answers" => ucfirst(htmlentities($row1['answers']))
                );
    		}
    		
			$array_out[] = 
				//array("code" => "200");
				array(
					"id" => $row['id'],
					"question" => strtolower(htmlentities($row['question'])),
					"answers" => ucfirst(htmlentities($row['answers'])),
					"level" =>  $array_out1
                    		
				);
			 
		}
		$output=array("code" => "200", "msg" => $array_out);
		print_r(json_encode($output, true)); 
	    
	}
	
	
	
	function addNewQuestion()
	{
	    require_once("config.php");
	    $input = @file_get_contents("php://input");
	    $event_json = json_decode($input,true);
		//print_r($event_json);
		//0= owner  1= company 2= ind mechanic
		$question = htmlspecialchars($event_json['question'], ENT_QUOTES);
        $answer = htmlspecialchars($event_json['answer'], ENT_QUOTES);
         
         
		$qrry_1="insert into chatBoot(question,answers)values(";
		$qrry_1.="'".$question."',";
		$qrry_1.="'".$answer."'";
		$qrry_1.=")";
		if(mysqli_query($conn,$qrry_1))
		{
		 
			 $array_out = array();
			 $array_out[] = 
				//array("code" => "200");
				array(
					"question" => $question,
					"answer" => $answer
				);
			
			$output=array( "code" => "200", "msg" => $array_out);
			print_r(json_encode($output, true));
		}
		else
		{
		    //echo mysqli_error();
		    $array_out = array();
				
    		 $array_out[] = 
    			array(
    			"response" =>"problem in signup");
    		
    		$output=array( "code" => "201", "msg" => $array_out);
    		print_r(json_encode($output, true));
		}
	}
	
	
	function addNewSuggestion()
	{
	    require_once("config.php");
	    $input = @file_get_contents("php://input");
	    $event_json = json_decode($input,true);
		//print_r($event_json);
		//0= owner  1= company 2= ind mechanic
		$question = htmlspecialchars($event_json['question'], ENT_QUOTES);
        $answer = htmlspecialchars($event_json['answer'], ENT_QUOTES);
        $level = htmlspecialchars($event_json['level'], ENT_QUOTES);
         
         
		$qrry_1="insert into chatBoot(question,answers,level)values(";
		$qrry_1.="'".$question."',";
		$qrry_1.="'".$answer."',";
		$qrry_1.="'".$level."'";
		$qrry_1.=")";
		if(mysqli_query($conn,$qrry_1))
		{
		 
			 $array_out = array();
			 $array_out[] = 
				//array("code" => "200");
				array(
					"question" => $question,
					"answer" => $answer
				);
			
			$output=array( "code" => "200", "msg" => $array_out);
			print_r(json_encode($output, true));
		}
		else
		{
		    //echo mysqli_error();
		    $array_out = array();
				
    		 $array_out[] = 
    			array(
    			"response" =>"problem in signup");
    		
    		$output=array( "code" => "201", "msg" => $array_out);
    		print_r(json_encode($output, true));
		}
	}
	
?>

