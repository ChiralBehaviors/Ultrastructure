var Login = React.createClass({
    getInitialState() {
        return {
            authToken: ""
        }
    },

    getToken: function(e) {
        e.preventDefault();
        console.log("getting token ");
        this.setState({authToken: "token"});
    },

	render: function() {
		return (
            <div>
                <form onSubmit={this.getToken} d="login_form" class="login-form">
                    <h2>Login Form</h2>
                    <label>User Name :</label> <input type="text" name="username"
                                                      id="username"></input>
                    <label>Password :</label> <input
                    type="password" name="password" id="password"> </input>
                    <button>Login</button>
                </form>
                <div id="token">
                    <p>Token: {this.state.authToken}</p>
                </div>
            </div>
		);
	}
});

ReactDOM.render(<Login />, document.getElementById('login'));