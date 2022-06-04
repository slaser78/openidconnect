<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Login Page</title>
</head>

<body>

<h1>Login Page</h1>

<g:form useToken="true" class="form-horizontal" name="openidForm" url="/openID/default">
    <div class="form-group">
        <div class="col-sm-offset-4 col-sm-8">
            <button type='submit' class="btn btn-success" id="submit" value='Microsoft' formaction="/login/oauth2/code/microsoft/init" >Microsoft</button>
        </div>
    </div>
</g:form>

</body>
</html>