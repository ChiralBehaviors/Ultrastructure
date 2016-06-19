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
                                    $("#login_token").text("Token: " + data);
                                }
                            ).fail(function(e) {
                            $("#login_token").text("Invalid credentials");
                        });

                    }
                );
        }
    );