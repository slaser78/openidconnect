<!doctype html>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Demonstration of OpenID Connect in Grail 5</title>
</head>

<body>

<div id="content" role="main">
    <div class="container">
        <section class="row colset-2-its">
            <h1>Welcome User: ${username} </h1>

            <p>
                When you view this content you have either logged in using an
                OpenID Connect based exchange or you are rememebered having login
                not to long ago.
            </p>
            <p>
                <a href="./secret">Watch this very secret text.</a>
            </p>
<g:form useToken="true" class="form-horizontal" name="openidForm">
    <div class="form-group">
                <div class="col-sm-offset-4 col-sm-8">
                    <button type='submit' class="btn btn-success" id="submit" value='Logout' formaction="/logout" >Logout</button>
                </div>
            </div>
        </section>
    </div>
</g:form>
</div>

</body>
</html>