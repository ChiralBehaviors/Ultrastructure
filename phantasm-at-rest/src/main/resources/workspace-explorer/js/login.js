$(document).ready(
        function () {
            $("#login")
                .click(
                    function () {
                        var username = $("#username").val();
                        var password = $("#password").val();
                        $.post(
                                "/api/oauth2/token/login",
                                {
                                    username: username,
                                    password: password
                                },
                                function (data) {
                                    console.log("data: " + data);
                                    if (data == 'Successfully Logged in...') {
                                        $("form")[0]
                                            .reset();
                                        $(
                                            'input[type="text"],input[type="password"]')
                                            .css(
                                                {
                                                    "border": "2px solid #00F5FF",
                                                    "box-shadow": "0 0 5px #00F5FF"
                                                });
                                        alert(data);
                                    }
                                }
                            ).fail(function(e) {
                            console.log(e);
                        });

                    }
                );
        }
    );