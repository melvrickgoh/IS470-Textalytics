<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%@ page import="textalytics.dao.*" %>
<!DOCTYPE HTML>
<html>
    <head>
        <title>Database</title>
        <meta name="viewport" content="width=device-width, maximum-scale=1, user-scalable=no" />
        <meta name="mobile-web-app-capable" content="yes" />
        <meta name="apple-mobile-web-app-capable" content="yes" />
        <meta name="apple-mobile-web-app-status-bar-style" content="black" />

        <!-- Bootstrap core CSS -->
    	<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet">
    	<link href="css/simple-sidebar.css" rel="stylesheet">
    	<link href="css/dataTables.bootstrap.css" rel="stylesheet">
    	<%
    		/*ServletContext context = getServletContext();
			CrawlerDAO crawlDAO = (CrawlerDAO)context.getAttribute("crawlerDAO");
			TeamsDAO teamsDAO = (TeamsDAO)context.getAttribute("teamsDAO");
			LinksDAO linksDAO = (LinksDAO)context.getAttribute("linksDAO");
			StudentsDAO studentsDAO = (StudentsDAO)context.getAttribute("studentsDAO");
			SupervisorDAO supervisorDAO = (SupervisorDAO)context.getAttribute("supervisorDAO");*/
    	%>
    </head>
    <body>
    	
    	<div id="wrapper">

        <!-- Sidebar -->
        <div id="sidebar-wrapper">
            <ul class="sidebar-nav">
                <li class="sidebar-brand">
                    <a href="#">
                        Database Manager
                    </a>
                </li>
                <li>
                    <a href="#">Dashboard</a>
                </li>
            </ul>
        </div>
        <!-- /#sidebar-wrapper -->

        <!-- Page Content -->
        <div id="page-content-wrapper">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-lg-12">
                        <h1>Data Management</h1>
                        <p><strong>Crawler Table</strong> 
                        	<a class="btn btn-danger " onclick="databaseTruncate('crawler')">Truncate</a>
                        	<a class="btn btn-info" onclick="databaseShow('crawler')">Show Data</a>
                        	<a class="btn btn-success" onclick="databaseLoad('crawler')">Refresh Data</a>
                        	<div id="crawlerMsg" class="alert alert-success hide" role="alert"></div>
                        	<div id="crawlWrapper" data-toggle="collapse" data-parent="#accordion" href="crawlerTable">
                        		<table id="crawlerTable" class="table table-striped table-bordered" cellspacing="0" width="100%">
							        <thead>
							            <tr>
							                <th>ID</th>
							                <th>Link</th>
							                <th>Checksum</th>
							            </tr>
							        </thead>
							 
							        <tbody id="collapseCrawlerContent" class="panel-collapse collapse in">
							            
							        </tbody>
							  </table>
                        	</div>
                        </p>
                        <p><strong>Students Table</strong>
                        	<a class="btn btn-danger" onclick="databaseTruncate('students')">Truncate</a>
                        	<a class="btn btn-info" onclick="databaseShow('students')">Show Data</a>
                        	<a class="btn btn-success" onclick="databaseLoad('students')">Refresh Data</a>
                        	<div id="studentsMsg" class="alert alert-success hide" role="alert"></div>
                        	<div id="studentsWrapper" data-toggle="collapse" data-parent="#accordion" href="studentsTable">
                        		<table id="studentsTable" class="table table-striped table-bordered" cellspacing="0" width="100%">
							        <thead>
							            <tr>
							                <th>Name</th>
							                <th>Projects</th>
							                <th>Role</th>
							            </tr>
							        </thead>
							 
							        <tbody id="collapseStudentContent" class="panel-collapse collapse in">
							            
							        </tbody>
							  </table>
                        	</div>
                        	
                        </p>
                        <p><strong>Supervisor Table</strong> 
                        	<a class="btn btn-danger" onclick="databaseTruncate('supervisor')">Truncate</a>
                        	<a class="btn btn-info" onclick="databaseShow('supervisor')">Show Data</a>
                        	<a class="btn btn-success" onclick="databaseLoad('supervisor')">Refresh Data</a>
                        	<div id="supervisorMsg" class="alert alert-success hide" role="alert"></div>
                        	<div id="supervisorWrapper" data-toggle="collapse" data-parent="#accordion" href="supervisorTable">
                        		<table id="supervisorTable" class="table table-striped table-bordered" cellspacing="0" width="100%">
							        <thead>
							            <tr>
							                <th>Supervisor</th>
							                <th>Team</th>
							                <th>Year</th>
							                <th>Semester</th>
							            </tr>
							        </thead>
							 
							        <tbody id="collapseSupervisorContent" class="panel-collapse collapse in">
							            
							        </tbody>
							  </table>
                        	</div>
                        </p>
                        <p><strong>Links Table</strong> 
                        	<a class="btn btn-danger" onclick="databaseTruncate('links')">Truncate</a>
                        	<a class="btn btn-info" onclick="databaseShow('links')">Show Data</a>
                        	<a class="btn btn-success" onclick="databaseLoad('links')">Refresh Data</a>
                        	<div id="linksMsg" class="alert alert-success hide" role="alert"></div>
                        	<div id="linksWrapper" data-toggle="collapse" data-parent="#accordion" href="linksTable">
                        		<table id="linksTable" class="table table-striped table-bordered" cellspacing="0" width="100%">
							        <thead>
							            <tr>
							                <th>Link</th>
							                <th>Checksum</th>
							            </tr>
							        </thead>
							 
							        <tbody id="collapseLinkContent" class="panel-collapse collapse in">
							            
							        </tbody>
							  </table>
                        	</div>
                        </p>
                        <p><strong>Teams Table</strong> 
                        	<a class="btn btn-danger" onclick="databaseTruncate('teams')">Truncate</a>
                        	<a class="btn btn-info" onclick="databaseShow('teams')">Show Data</a>
                        	<a class="btn btn-success" onclick="databaseLoad('teams')">Refresh Data</a>
                        	<div id="teamsMsg" class="alert alert-success hide" role="alert"></div>
                        	<div id="teamsWrapper"  data-toggle="collapse" data-parent="#accordion" href="teamsTable">
                        		<table id="teamsTable" class="table table-striped table-bordered" cellspacing="0" width="100%">
							        <thead>
							            <tr>
							                <th>ID</th>
							                <th>Link</th>
							                <th>Checksum</th>
							            </tr>
							        </thead>
							 
							        <tbody id="#collapseTeamContent" class="panel-collapse collapse in">
							            
							        </tbody>
							  </table>
                        	</div>
                        </p>
                        <p><strong>All</strong> 
                        	<a class="btn btn-danger" onclick="databaseTruncate('all')">Truncate</a>
                        	<div id="allMsg" class="alert alert-success hide" role="alert"></div>
                        </p>
                        <a href="#menu-toggle" class="btn btn-default" id="menu-toggle">Toggle Menu</a>
                    </div>
                </div>
            </div>
        </div>
        <!-- /#page-content-wrapper -->

    </div>
    <!-- /#wrapper -->
    	
	    <script src="http://code.jquery.com/jquery-2.1.0.min.js"/>
	    <script src="js/ie10-viewport-bug-workaround.js"></script>
	    <script src="js/dataTables.bootstrap.js"></script>
	    <script src="js/jquery.dataTables.min.js"></script>
	    <script src="bootstrap/js/bootstrap.min.js"></script>
	    <!-- Menu Toggle Script -->
	    <script>
	    globalCrawlData = [];
	    
	    var _generateParameterString = function (options){
			var keys = Object.keys(options),
			result = '';
			for (var i in keys){
				var key = keys[i],
				result=result+'"'+key+'":"'+options[key]+'",';
			}
			return '{'+result.substring(0,result.length-1)+'}';
		}
	    var xhr = function(operationType,parameters,callback){
	    	var request = new XMLHttpRequest();
			
			request.onreadystatechange = function(e) {
				if (request.readyState == 4) {
					callback(request.responseText);
					//window.location.href = '/home';//('/home');
				}
			}
			request.open('GET', '/databaseops?operation='+operationType+'&json=' + parameters, true);
			request.send();
	    }
	    var databaseTruncate = function(dbName){
	    	if (confirm('Are you sure you want truncate the' + dbName + ' table?')) {
	    	    // Save it!	    	    
	    		var parameters = _generateParameterString({table:dbName});
	    	    xhr("truncate",parameters,function(e){
	    	    	//callback
	    	    	var msgElement = $('#'+dbName+'Msg');
	    	    	alert(e);
	    	    	msgElement.html(dbName + ' table has been truncated');
	    	    	msgElement.removeClass('hide');
	    	    	setTimeout(function(){
	    	    		msgElement.addClass('hide');
	    	    	},2000);
	    	    });
	    	} else {
	    	    // Do nothing!
	    	}
	    }
	    var databaseShow = function(dbName){
	    	var tableElement = $('#'+dbName+'Table_wrapper');
	    	if (tableElement.hasClass('hide')){
	    		tableElement.removeClass('hide');
	    	}else{
	    		tableElement.addClass('hide');
	    	}	    	
	    }
	    var emptyArray = function(array){
	    	while(array.length > 0) {
	    		array.pop();
	    	}
	    }
	    var concatArray = function(parent,child){
	    	for (var i in child){
	    		parent.push(child[i]);
	    	}
	    }
	    var databaseLoad = function(dbName){
	    	var parameters = _generateParameterString({table:dbName});
	    	xhr("select",parameters,function(e){
    	    	//callback
    	    	var json = JSON.parse(e);
    	    	if(json.result != null){
    	    		switch(dbName){
    	    			case 'crawler':
    	    				//concatArray(globalCrawlData,json.result);
    	    				$('#crawlWrapper').empty();
    	    				$('#crawlWrapper').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="crawlerTable"></table>' );
    	    				
    	    				$('#crawlerTable').dataTable({
    	    	    			data:json.result,
    	    	    			columns:[
    	    	    				{title:"Link"},
    	    	    				{title:"Checksum"}
    	    	    			]
    	    	    		});
    	    				break;
    	    			case 'students':
    	    				$('#studentsWrapper').empty();
    	    				$('#studentsWrapper').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="studentsTable"></table>' );
    	    				
    	    				$('#studentsTable').dataTable({
    	    	    			data:json.result,
    	    	    			columns:[
    	    	    				{title:"Name"},
    	    	    				{title:"Project"},
    	    	    				{title:"Role"}
    	    	    			]
    	    	    		});
    	    				break;
    	    			case 'supervisor':
    	    				$('#supervisorWrapper').empty();
    	    				$('#supervisorWrapper').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="supervisorTable"></table>' );
    	    				
    	    				$('#supervisorTable').dataTable({
    	    	    			data:json.result,
    	    	    			columns:[
    	    	    				{title:"Name"},
    	    	    				{title:"Team"},
    	    	    				{title:"Year"},
    	    	    				{title:"Semester"}
    	    	    			]
    	    	    		});
    	    				break;
    	    			case 'links':
    	    				$('#linksWrapper').empty();
    	    				$('#linksWrapper').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="linksTable"></table>' );
    	    				
    	    				$('#linksTable').dataTable({
    	    	    			data:json.result,
    	    	    			columns:[
    	    	    				{title:"Parent"},
    	    	    				{title:"Link"},
    	    	    				{title:"Type"}
    	    	    			]
    	    	    		});
    	    				break;
    	    			case 'teams':
    	    				$('#teamsWrapper').empty();
    	    				$('#teamsWrapper').html( '<table cellpadding="0" cellspacing="0" border="0" class="display" id="teamsTable"></table>' );
    	    				
    	    				$('#teamsTable').dataTable({
    	    	    			data:json.result,
    	    	    			columns:[
    	    	    	    		{title:"Name"},
    	    	    	    		{title:"Members"},
    	    	    				{title:"Year"},
    	    	    				{title:"Semester"},
    	    	    				{title:"Description"},
    	    	    				{title:"Sponsor"},
    	    	    				{title:"Page"},
    	    	    				{title:"Pitch"},
    	    	    				{title:"Acceptance"},
    	    	    				{title:"Poster"},
    	    	    				{title:"Midterms"},
    	    	    				{title:"Finals"},
    	    	    				{title:"Description Links"},
    	    	    				{title:"Sponsor Links"}
    	    	    			]
    	    	    		});
    	    				break;
    	    			default:
    	    		}
    	    	}
    	    });
	    }
	    
	    $(document).ready(function() {
	    	databaseLoad('crawler');
	    	databaseLoad('students');
	    	databaseLoad('teams');
	    	databaseLoad('supervisor');
	    	databaseLoad('links');
	        /*$('#crawlerTable').dataTable({
    			data:[],
    			columns:[
    				{title:"Link"},
    				{title:"Checksum"}
    			]
    		});
	        $('#studentsTable').dataTable({
    			data:[],
    			columns:[
    				{title:"Name"},
    				{title:"Project"},
    				{title:"Role"}
    			]
    		});
	        $('#supervisorTable').dataTable({
    			data:[],
    			columns:[
    				{title:"Name"},
    				{title:"Team"},
    				{title:"Year"},
    				{title:"Semester"}
    			]
    		});
	        $('#linksTable').dataTable({
    			data:[],
    			columns:[
    				{title:"Parent"},
    				{title:"Link"},
    				{title:"Type"}
    			]
    		});
	        $('#teamsTable').dataTable({
    			data:[],
    			columns:[
    	    		{title:"Name"},
    	    		{title:"Members"},
    				{title:"Year"},
    				{title:"Semester"},
    				{title:"Description"},
    				{title:"Sponsor"},
    				{title:"Page"},
    				{title:"Pitch"},
    				{title:"Acceptance"},
    				{title:"Poster"},
    				{title:"Midterms"},
    				{title:"Finals"},
    				{title:"Description Links"},
    				{title:"Sponsor Links"}
    			]
    		});
	        $("#menu-toggle").click(function(e) {
		        e.preventDefault();
		        $("#wrapper").toggleClass("toggled");
		    });*/
	    } );
	    </script>
    </body>
</html>
