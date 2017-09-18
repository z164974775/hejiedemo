<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>登录1</title>
	<script type="text/javascript" src="js/jquery-1.7.2.min.js"></script>
	<script type="text/javascript">
		$(function(){
			$("#addSubmit").click(function(){
				var userId = $("#userId").val();
				if(userId==""){
					alert("用户名未输入...");
					return;
				}
				var pwd = $("#pwd").val();
				if(pwd==""){
					alert("密码未输入...");
					return;
				}
				var name = $("#name").val();
				if(name==""){
					alert("名称未输入...");
					return;
				}
				$.ajax({
					url:"addUser.action",
					type:"POST",
					data:{
						'userId':userId,
						'pwd':pwd,
						'name':name
					},
					success:function(data){
						alert(data);
					}
				})
			})
		})
	</script>
</head>
<body >
	<center>
		<div>
			用户名:<input type="text" id="userId"/><br>
			密    码:<input type="password" id="pwd"/><br>
			名    称:<input type="text" id="name"/><br>
			<input type="button" value="提交" id="addSubmit"/>
		</div>
	</center>
</body>
</html>