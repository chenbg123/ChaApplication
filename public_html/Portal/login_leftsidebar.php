<ul class="login_leftsidebar"> 

	<?php 

		?>
			
			<li <?php if(isset($_GET['p'])) { if( $_GET['p'] == "chat_bot" ) {
				echo 'class="active"';
			} } ?> ><a href="dashboard.php?p=chat_bot">Chat Bot</a></li>

			<li <?php if(isset($_GET['log'])) { if( $_GET['log'] == "out" ) {
				echo 'class="active"';
			} } ?> ><a href="dashboard.php?log=out">Logout</a></li>

			

	
		<?php

		?>

</ul>