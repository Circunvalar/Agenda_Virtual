// Validacion y feedback visual para el formulario de login
(function(){
    const form = document.querySelector('.auth-card');
    if(!form) return;

    const usernameInput = form.querySelector('input[name="username"]');
    const passwordInput = form.querySelector('input[name="password"]');

    const usernameError = document.getElementById('usernameError');
    const passwordError = document.getElementById('passwordError');
    const loginErrors = document.getElementById('loginErrors');

    function setFieldError(input, errorEl, message){
        if(!input) return true;
        if(message){
            input.classList.add('is-invalid');
            if(errorEl){
                errorEl.textContent = message;
                errorEl.classList.add('error');
            }
            return false;
        }
        input.classList.remove('is-invalid');
        if(errorEl){
            errorEl.textContent = '';
            errorEl.classList.remove('error');
        }
        return true;
    }

    function validateUsername(){
        if(!usernameInput) return true;
        const val = (usernameInput.value || '').trim();
        if(!val){
            usernameInput.setCustomValidity('El usuario es obligatorio.');
            return setFieldError(usernameInput, usernameError, 'El usuario es obligatorio.');
        }
        if(val.length < 3){
            usernameInput.setCustomValidity('El usuario debe tener al menos 3 caracteres.');
            return setFieldError(usernameInput, usernameError, 'El usuario debe tener al menos 3 caracteres.');
        }
        usernameInput.setCustomValidity('');
        return setFieldError(usernameInput, usernameError, '');
    }

    function validatePassword(){
        if(!passwordInput) return true;
        const val = (passwordInput.value || '').trim();
        if(!val){
            passwordInput.setCustomValidity('La contraseña es obligatoria.');
            return setFieldError(passwordInput, passwordError, 'La contraseña es obligatoria.');
        }
        if(val.length < 6){
            passwordInput.setCustomValidity('La contraseña debe tener al menos 6 caracteres.');
            return setFieldError(passwordInput, passwordError, 'La contraseña debe tener al menos 6 caracteres.');
        }
        passwordInput.setCustomValidity('');
        return setFieldError(passwordInput, passwordError, '');
    }

    function validateForm(){
        const okUser = validateUsername();
        const okPass = validatePassword();
        return okUser && okPass;
    }

    if(usernameInput){
        usernameInput.addEventListener('input', validateUsername);
        usernameInput.addEventListener('blur', validateUsername);
    }

    if(passwordInput){
        passwordInput.addEventListener('input', validatePassword);
        passwordInput.addEventListener('blur', validatePassword);
    }

    form.addEventListener('submit', function(e){
        if(loginErrors){
            loginErrors.style.display = 'none';
            loginErrors.textContent = '';
        }
        const ok = validateForm();
        if(!ok || !form.checkValidity()){
            e.preventDefault();
            e.stopPropagation();
            if(loginErrors){
                loginErrors.textContent = 'Revisa los campos marcados.';
                loginErrors.style.display = 'block';
            }
            const firstInvalid = form.querySelector(':invalid');
            if(firstInvalid && typeof firstInvalid.reportValidity === 'function'){
                firstInvalid.reportValidity();
            }
            return false;
        }
        return true;
    });

    validateUsername();
    validatePassword();
})();

