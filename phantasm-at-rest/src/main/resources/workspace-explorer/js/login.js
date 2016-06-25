var LoginForm = React.createClass({
    render: function() {
        return (<div id="login_form" class="login-form">
            <h2>Login Form</h2>
            <label>User Name :</label> <input type="text" name="username"
                                              id="username"></input>
            <label>Password :</label> <input
            type="password" name="password" id="password"> </input>
            <input
                type="button" name="login" id="login" value="Login"></input>
        </div>
        );
    }
});

var LoginToken = React.createClass({
    render: function() {
        return (<div id="token">
            <p>token goes here</p>
            </div>
        );
    }
})

var Login = React.createClass({
	render: function() {
		return (
            <div>
                <LoginForm/>
                <LoginToken/>
            </div>
		);
	}
});

        ReactDOM.render(<Login />, document.getElementById('login'));