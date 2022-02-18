<?php
@require_once("config.php");


if(@$_GET['add_new']=="ok") 
{   
    
    ?>
        <h2 style="font-weight: 300;" align="center">Add New Question & Answer </h2>

          <br><br>
          
          <form action="dashboard.php?p=chat_bot&addnew=ok" method="post">
            <p style="width: 100%;text-align: left;" class="left">
              <input name="question" required=""  type="text">
              <label alt="Questions" placeholder="Questions"></label>
            </p>
    
            <p style="width: 100%;text-align: left;" class="right">
              <input name="answer" required="" type="text">
              <label alt="Answer" placeholder="Answer"></label>
            </p>
    
           <p style="width: 100%;" class="right">
              <input value="Add Now" style="background:#333333; color: white; border: 0px;" type="submit">
            </p>
          </form>
 
    <?php

}
else
if(@$_GET['add_suggestion']=="ok") 
{   
    
    ?>
        <h2 style="font-weight: 300;" align="center">Add New Suggestion </h2>

          <br><br>
          
          <form action="dashboard.php?p=chat_bot&add_suggestion=ok" method="post">
            <input name="id" value="<?php echo @$_GET['id']; ?>" type="hidden">
            <p style="width: 100%;text-align: left;" class="left">
              <input name="question" required=""  type="text">
              <label alt="Questions" placeholder="Questions"></label>
            </p>
    
            <p style="width: 100%;text-align: left;" class="right">
              <input name="answer" required="" type="text">
              <label alt="Answer" placeholder="Answer"></label>
            </p>
    
           <p style="width: 100%;" class="right">
              <input value="Add Now" style="background:#333333; color: white; border: 0px;" type="submit">
            </p>
          </form>
 
    <?php

}


?>