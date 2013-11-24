function MyAuth() {
    var self = this;
    self.loginEmail = undefined;
    self.loginPassword = undefined;
    self.registrationEmail = undefined;
    self.registrationPasword = undefined;
    self.registrationConfirmPasword = undefined;


    this.showError = function(message) {
        $('#authErrorBlock').text(message);
        $('#authErrorBlock').show();
    }

    this.init = function() {

        $('#loginButton').click(function() {
            self.loginEmail = $('#loginEmail').val();
            self.loginPassword = $('#loginPassword').val();
            //TODO: validate
            if (self.loginEmail === undefined || self.loginEmail === '' || self.loginPassword === undefined || self.loginPassword === '') {
                alert('incorrect input data');
                return;
            }
            self.login(self.loginEmail, self.loginPassword);  
            return false;
        });

        $('#registerButton').click(function (){
            //alert('dfdfdf');
            console.log('trying to register');
            self.registrationEmail = $('#registrationEmail').val();
            self.registrationPasword = $('#registrationPassword').val();
            self.registrationConfirmPasword = $('#registrationConfirmPassword').val();

            console.log(self.registrationEmail);
            console.log(self.registrationPasword);
            console.log(self.registrationConfirmPasword);

            //TODO: validate
            if (self.registrationEmail == undefined || self.registrationEmail == '' || self.registrationPasword == undefined || self.registrationPasword == '' || self.registrationPasword != self.registrationConfirmPasword) {
                alert('incorrect input data');
                return;
            }
            self.register(self.registrationEmail, self.registrationPasword);
        });

        $('#logoutButton').click(function(){
            self.logout();
        });

    };

    this.login = function(userEmail, userPassword) {
        console.log('login: email = ' + userEmail + '; password = ' + userPassword);
        $.post(
            '/EasyTagWeb/resources/auth/login',
            $('#login_form').serialize()
            ).success(
            function(data) {
                console.log(data);
                var resp = data;    
                    
                if (data.responseCode === 0) {
                    alert(resp.error.message);
                    return;
                } else {
                    window.location.href = '/EasyTagWeb/index.xhtml';  
                    alert('Logged in as ' + self.loginEmail);
                }                    
            //window.location.reload();
            }
            );   
    };


    this.logout = function(){
        $.ajax({
            url:'/EasyTagWeb/resources/auth/logout',
            success: function(){
                window.location.reload();
            }
        });
    }

    this.register = function(email, password) {
        console.log('registration: email = ' + email + '; password = ' + password);
        $.post(
            '/EasyTagWeb/resources/auth/register',
            $('#registration_form').serialize()
            ).success(
            function(data) {
                console.log(data);
                if (data.error !== undefined) {
                    alert(data.error.message);
                    return;
                }
                window.location.reload();
            }
            );
    };
}