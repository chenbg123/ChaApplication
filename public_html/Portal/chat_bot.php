<?php 
require_once("config.php"); 
if( isset($_SESSION['id']))
{ 
    
    
	if( isset($_GET['addnew']) ) { //log

    	if( $_GET['addnew'] == "ok" ) { //login user
    
    		 $question = htmlspecialchars($_POST['question'], ENT_QUOTES);
    	     $answer = htmlspecialchars($_POST['answer'], ENT_QUOTES);
    	    //$returnlink=htmlspecialchars($_POST['returnlink'], ENT_QUOTES);
    	    
    	    if($question!="" && $answer!="") { 
    
    			$headers = array(
    				"Accept: application/json",
    				"Content-Type: application/json",
    				"Api-Key: V98IhPYJQmunYMplfBMb48wOxGvBzlVS"
    			);
    
    			$data = array(
    				"question" => $question, 
    				"answer" => $answer
    			);
    
    			$ch = curl_init( $baseurl.'addNewQuestion' );
    
    			curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    			curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
    			curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
    			curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    
    			$return = curl_exec($ch);
    
    			$json_data = json_decode($return, true);
    
    			$curl_error = curl_error($ch);
    			$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    
    			//echo $json_data['code'];
    			//print_r($json_data['code']);
    			//die();
    			
    			curl_close($ch);
    
    			if($json_data['code'] == 201){
    				echo "<script>window.location='dashboard.php?p=chat_bot&action=error'</script>";
    			} 
    			else 
    			{	
    			
    				echo "<script>window.location='dashboard.php?p=chat_bot&action=success'</script>";
    
    			}
    
    		} 
    		else 
    		{
    			echo "<script>window.location='dashboard.php?p=chat_bot&action=error'</script>";
    		} 
    
    	} //login user = end
    
    
    	
    
    } //log = end
    else
    if( isset($_GET['add_suggestion']) ) { //log

    	if( $_GET['add_suggestion'] == "ok" ) { //login user
    
    		 $question = htmlspecialchars($_POST['question'], ENT_QUOTES);
    	     $answer = htmlspecialchars($_POST['answer'], ENT_QUOTES);
    	     $level=htmlspecialchars($_POST['id'], ENT_QUOTES);
    	    
    	    if($question!="" && $answer!="") { 
    
    			$headers = array(
    				"Accept: application/json",
    				"Content-Type: application/json",
    				"Api-Key: V98IhPYJQmunYMplfBMb48wOxGvBzlVS"
    			);
    
    			$data = array(
    				"question" => $question, 
    				"answer" => $answer,
    				"level" => $level
    			);
    
    			$ch = curl_init( $baseurl.'addNewSuggestion' );
    
    			curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    			curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
    			curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
    			curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    
    			$return = curl_exec($ch);
    
    			$json_data = json_decode($return, true);
    
    			$curl_error = curl_error($ch);
    			$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    
    			//echo $json_data['code'];
    			//print_r($json_data['code']);
    			//die();
    			
    			curl_close($ch);
    
    			if($json_data['code'] == 201){
    				echo "<script>window.location='dashboard.php?p=chat_bot&action=error'</script>";
    			} 
    			else 
    			{	
    			
    				echo "<script>window.location='dashboard.php?p=chat_bot&action=success'</script>";
    
    			}
    
    		} 
    		else 
    		{
    			echo "<script>window.location='dashboard.php?p=chat_bot&action=error'</script>";
    		} 
    
    	} //login user = end
    
    
    	
    
    } //log = end
	
	?>

	<link rel="stylesheet" type="text/css" href="css/jquery.dataTables.min.css">
  	<!--<script type="text/javascript" charset="utf8" src="https://code.jquery.com/jquery-3.3.1.js"></script>-->
  	<script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/1.10.16/js/jquery.dataTables.min.js"></script>
	<script>
		$(document).ready(function() {
		    $('#data1').DataTable();
		} );
	</script>

	
	<h2 class="title">ChatBot</h2>
	
	<?php 
		
		$headers = array(
			"Accept: application/json",
			"Content-Type: application/json"
		);

		$data = array(
			//"user_id" => $user_id
		);

		$ch = curl_init( $baseurl.'showChatLevelBased' );

		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
		curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'POST');
		curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
		curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);

		$return = curl_exec($ch);

		$json_data = json_decode($return, true);
	    //var_dump($return);

		$curl_error = curl_error($ch);
		$http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);

		//echo $json_data['code'];
		//die;

		foreach( $json_data['msg'] as $str => $val ) 
        {
			?>
			    <div class="question">
			        <span>Question:</span>
			        <?php echo $val['question'];  ?>
			       
			    </div>
			    
			    <div class="answer">
			        <span>Answer:</span>
			        <?php echo $val['answers'];  ?>
			        <br>
			    </div>
			    
			<?php
			   // if(count($val['level'])!="0")
			   // {
			        ?>
			            <div class="subquestion" style="background: #E9E9E9;">
			                <div style="margin-bottom:10px;padding-top: 5px;">Suggestion's</div>
			               
			                <?php
			                    foreach( $val['level'] as $str1 => $val1 ) 
                                {
                                    ?>
                                        
                        			    <div class="question" style="color:black; background:#E9E9E9;font-weight: bold; margin-bottom: 0px;">
                        			        <span>Question:</span>
                        			        <?php echo $val1['question'];  ?>
                        			       
                        			    </div>
                        			    
                        			    <div class="answer" style="color:black; background:#E9E9E9; margin-bottom: 0px;">
                        			        <span>Answer:</span>
                        			        <?php echo $val1['answers'];  ?>
                        			    </div>
                        			<?php
                                }
			                ?>
			                <div style="padding: 15px 20px;text-align: center;border: dashed 2px #80808080;font-size: 14px;border-radius: 50px;margin-top: 30px; cursor: pointer;" onclick="add_suggestion('<?php echo $val['id'] ?>');">
                                Add New Suggestion's
                            </div>
			            </div>
			        <?php
			    //}
			    
		}

		curl_close($ch);
	?>
    
    <div style="padding: 15px 20px;text-align: center;border: dashed 2px #80808080;font-size: 14px;border-radius: 50px;margin-top: 30px; cursor: pointer;" onclick="add_new();">
        Add New Question & Answer
    </div>
	
	<style>
	    .question{margin-bottom: 10px;border-radius: 50px;padding: 8px 8px 8px 8px;font-size: 14px; background: #4776E6; background: -webkit-linear-gradient(to top, #8E54E9, #4776E6); background: linear-gradient(to top, #8E54E9, #4776E6); color: white;}
	    .answer{margin-bottom: 10px;border-radius: 50px;padding: 8px 8px 8px 8px; position: relative; margin-left: 20px;font-size: 14px; background: #4776E6; background: -webkit-linear-gradient(to top, #8E54E9, #4776E6); background: linear-gradient(to top, #8E54E9, #4776E6); color: white;}
	    
	    .subquestion{background:white;margin-bottom: 10px;border-radius: 4px;padding: 8px 8px 8px 8px; position: relative; margin-left: 60px;}
	    .subanswer{background:white;margin-bottom: 10px;border-radius: 4px;padding: 8px 8px 8px 8px; position: relative; margin-left: 80px;}
	</style>
    
    <script>
		
		function add_new()
		{	
			//alert(data1);
			document.getElementById("PopupParent").style.display="block";
			document.getElementById("contentReceived").innerHTML="<div style='margin-top:150px;'><img src='img/loader.gif' width='150px'></div>";
		    var xmlhttp;
		    if(window.XMLHttpRequest)
		      {// code for IE7+, Firefox, Chrome, Opera, Safari
		        xmlhttp=new XMLHttpRequest();
		      }
		    else
		      {// code for IE6, IE5
		        xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		      }
		      
		      xmlhttp.onreadystatechange=function()
		      {
		        if(xmlhttp.readyState==4 && xmlhttp.status==200)
		        {
		           // alert(xmlhttp.responseText);
		           document.getElementById("contentReceived").innerHTML=xmlhttp.responseText;
		        }
		      }
		    xmlhttp.open("GET","ajex-events.php?add_new=ok");
		    xmlhttp.send();
		}
		
		function add_suggestion(data)
		{
		    
		    //alert(data1);
			document.getElementById("PopupParent").style.display="block";
			document.getElementById("contentReceived").innerHTML="<div style='margin-top:150px;'><img src='img/loader.gif' width='150px'></div>";
		    var xmlhttp;
		    if(window.XMLHttpRequest)
		      {// code for IE7+, Firefox, Chrome, Opera, Safari
		        xmlhttp=new XMLHttpRequest();
		      }
		    else
		      {// code for IE6, IE5
		        xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		      }
		      
		      xmlhttp.onreadystatechange=function()
		      {
		        if(xmlhttp.readyState==4 && xmlhttp.status==200)
		        {
		           // alert(xmlhttp.responseText);
		           document.getElementById("contentReceived").innerHTML=xmlhttp.responseText;
		        }
		      }
		    xmlhttp.open("GET","ajex-events.php?add_suggestion=ok&id="+data);
		    xmlhttp.send();
		    
		}
	
	</script>
    
<?php } else {
	
	@header("Location: index.php");
    echo "<script>window.location='index.php'</script>";
    die;
    
} ?>