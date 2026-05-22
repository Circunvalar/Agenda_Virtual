// Validación y feedback dinámico para el formulario de registro
(function(){
    const form = document.querySelector('.auth-card');
    if(!form) return;

    const passwordInput = document.getElementById('password');
    const phoneInput = document.getElementById('telefono');
    const emailInput = document.getElementById('email');
    const formErrors = document.getElementById('formErrors');
    const strengthBar = document.getElementById('passwordStrength');
    const togglePasswordBtn = document.getElementById('togglePassword');
    const passwordConfirm = document.getElementById('passwordConfirm');

    // Helpers
    function scorePassword(pw){
        let score = 0;
        if(!pw) return 0;
        if(pw.length >= 8) score++;
        if(/[a-z]/.test(pw)) score++;
        if(/[A-Z]/.test(pw)) score++;
        if(/\d/.test(pw)) score++;
        if(/[^A-Za-z0-9]/.test(pw)) score++;
        // normalized 0..4
        return Math.min(4, score);
    }

    function updateStrength(){
        if(!passwordInput || !strengthBar) return;
        const val = passwordInput.value || '';
        const s = scorePassword(val);
        const pct = Math.round((s/4)*100);
        strengthBar.style.width = pct + '%';
        // color scale
        if(pct < 40) strengthBar.style.background = '#e11d48'; // rojo
        else if(pct < 70) strengthBar.style.background = '#f59e0b'; // naranja
        else strengthBar.style.background = '#10b981'; // verde
    }

    function normalizePhoneInput(){
        if(!phoneInput) return;
        // permitir solo dígitos y limitar a 10
        phoneInput.value = phoneInput.value.replace(/\D/g, '').slice(0,10);
    }

    function validatePhone(){
        if(!phoneInput) return true;
        const val = phoneInput.value || '';
        if(val.length !== 10){
            phoneInput.setCustomValidity('El número debe tener exactamente 10 dígitos (ej: 3101234567).');
            return false;
        }
        phoneInput.setCustomValidity('');
        return true;
    }

    function validatePassword(){
        if(!passwordInput) return true;
        const val = passwordInput.value || '';
        const ok = /(?=.{8,})(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9])/.test(val);
        if(!ok){
            passwordInput.setCustomValidity('La contraseña no cumple los requisitos de seguridad.');
            return false;
        }
        passwordInput.setCustomValidity('');
        return true;
    }

    function validatePasswordMatch(){
        if(!passwordInput || !passwordConfirm) return true;
        const a = passwordInput.value || '';
        const b = passwordConfirm.value || '';
        if(a !== b){
            passwordConfirm.setCustomValidity('Las contraseñas no coinciden.');
            return false;
        }
        passwordConfirm.setCustomValidity('');
        return true;
    }

    function validateEmail(){
        if(!emailInput) return true;
        // confiar en type=email + required; pero reforzamos con simple regex
        const val = emailInput.value || '';
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const ok = re.test(val);
        if(!ok){
            emailInput.setCustomValidity('Introduce un correo válido.');
            return false;
        }
        emailInput.setCustomValidity('');
        return true;
    }

    function gatherErrors(){
        const errors = [];
        if(!validateEmail()) errors.push('Correo inválido.');
        if(!validatePhone()) errors.push('Teléfono inválido (10 dígitos).');
        if(!validatePassword()) errors.push('Contraseña insegura.');
        return errors;
    }

    // Listeners
    if(passwordInput){
        passwordInput.addEventListener('input', ()=>{
            updateStrength();
            validatePassword();
            // also revalidate match when user types
            if(passwordConfirm) validatePasswordMatch();
        });
        // mejorar compatibilidad si el navegador no reporta pattern hasta submit
        passwordInput.addEventListener('blur', validatePassword);
    }

    if(phoneInput){
        phoneInput.addEventListener('input', ()=>{
            normalizePhoneInput();
            validatePhone();
        });
        phoneInput.addEventListener('blur', validatePhone);
    }

    if(emailInput){
        emailInput.addEventListener('input', validateEmail);
        emailInput.addEventListener('blur', validateEmail);
    }

    if(passwordConfirm){
        passwordConfirm.addEventListener('input', validatePasswordMatch);
        passwordConfirm.addEventListener('blur', validatePasswordMatch);
    }

    if(togglePasswordBtn && passwordInput){
        togglePasswordBtn.addEventListener('click', function(){
            const isPwd = passwordInput.type === 'password';
            passwordInput.type = isPwd ? 'text' : 'password';
            if(passwordConfirm) passwordConfirm.type = isPwd ? 'text' : 'password';
            togglePasswordBtn.textContent = isPwd ? 'Ocultar' : 'Mostrar';
            // keep focus on password after toggling
            passwordInput.focus();
        });
    }

    form.addEventListener('submit', function(e){
        formErrors.style.display = 'none';
        const errors = gatherErrors();
        // validate password match explicitly
        if(!validatePasswordMatch()) errors.push('Las contraseñas no coinciden.');
        // also check native validity
        if(!form.checkValidity() || errors.length){
            e.preventDefault();
            e.stopPropagation();
            const nativeMsg = [];
            if(!form.checkValidity()){
                // find first invalid control and report
                const invalid = form.querySelector(':invalid');
                if(invalid && invalid.validationMessage) nativeMsg.push(invalid.validationMessage);
            }
            const all = nativeMsg.concat(errors);
            formErrors.innerText = all.join(' ');
            formErrors.style.display = 'block';
            // show browser built-in messages for the first invalid field
            const firstInvalid = form.querySelector(':invalid');
            if(firstInvalid && typeof firstInvalid.reportValidity === 'function'){
                firstInvalid.reportValidity();
            }
            return false;
        }
        // OK -- allow submission
        return true;
    });

    // initialize strength bar state in case of prefilled values
    updateStrength();
})();


