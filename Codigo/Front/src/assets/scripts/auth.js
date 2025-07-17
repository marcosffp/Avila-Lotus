import Cookies from 'js-cookie';
import { jwtDecode } from 'jwt-decode';

const apiUrl = import.meta.env.PUBLIC_API_URL;

export const auth = () => {
    // Referências aos elementos do DOM
    const loginForm = document.getElementById("login-form");
    const registerForm = document.getElementById("register-form");
    const forgotPasswordForm = document.getElementById("forgot-password-form");
    const loginTab = document.getElementById("login-tab");
    const registerTab = document.getElementById("register-tab");

    // Alternar entre Login e Cadastro
    if (loginTab && registerTab) {
        loginTab.addEventListener("click", () => {
            loginTab.classList.add("active");
            registerTab.classList.remove("active");
            loginForm.style.display = "block";
            registerForm.style.display = "none";
        });

        registerTab.addEventListener("click", () => {
            registerTab.classList.add("active");
            loginTab.classList.remove("active");
            loginForm.style.display = "none";
            registerForm.style.display = "block";
        });
    }

    // Função de login
    if (loginForm) {
        document.getElementById('login-btn').addEventListener("click", async (event) => {
            event.preventDefault();

            const email = document.getElementById("email").value.trim();
            const password = document.getElementById("password").value.trim();

            if (!email || !password) {
                alert("Por favor, preencha todos os campos.");
                return;
            }

            try {
                const response = await globalThis.fetchWithLoader(`${apiUrl}/auth/login`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ email, password }),
                });

                const data = await response.json();
                if (response.ok) {
                    const expiresInDays = (jwtDecode(data.token).exp * 1000 - Date.now()) / (1000 * 60 * 60 * 24);
                    Cookies.set('token', data.token, { path: '/', secure: true, sameSite: 'Strict', expires: expiresInDays });

                    alert("Login realizado com sucesso!");
                    window.location.href = "/agenda";
                } else alert("Erro ao realizar login.\n" + Object.values(data).join("\n"));
            } catch (error) {
                console.error("Erro ao realizar login:", error);
                alert("Erro ao realizar login. Tente novamente mais tarde.");
            }
        });
    }

    // Função de cadastro
    if (registerForm) {
        document.getElementById('register-btn').addEventListener("click", async (event) => {
            event.preventDefault();

            const email = document.getElementById("register-email").value.trim();
            const password = document.getElementById("register-password").value.trim();
            const userType = document.querySelector("input[name='userType']:checked")?.value;
            const authCode = document.getElementById("register-code")?.value.trim();

            if (!email || !password || !userType) {
                alert("Por favor, preencha todos os campos.");
                return;
            }

            if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{5,}$/.test(password)) {
                alert(
                    "A senha deve:\n" +
                    "- Ter pelo menos 5 caracteres\n" +
                    "- Conter uma letra maiúscula\n" +
                    "- Conter uma letra minúscula\n" +
                    "- Conter um número\n" +
                    "- Conter um caractere especial"
                );
                return;
            }

            const body = { email, password };
            let route = userType === "professional" ? "prof" : "cliente";

            if (userType === "professional" && authCode) {
                body.codAutenticacao = authCode;
            }

            try {
                const response = await globalThis.fetchWithLoader(`${apiUrl}/${route}/cadastrar`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(body),
                });

                const data = await response.json();
                if (response.ok) {
                    const expiresInDays = (jwtDecode(data.token).exp * 1000 - Date.now()) / (1000 * 60 * 60 * 24);
                    Cookies.set('token', data.token, { path: '/', secure: true, sameSite: 'Strict', expires: expiresInDays });
                    alert("Cadastro realizado com sucesso!");
                    window.location.href = (route === "prof" ? "/sobre" : "/profile") + "?firstLogin=true";
                } else {
                    alert("Erro ao realizar cadastro.\n" + Object.values(data).join("\n"));
                }
            } catch (error) {
                console.error("Erro ao realizar cadastro:", error);
                alert("Erro ao realizar cadastro. Tente novamente mais tarde.");
            }
        });
    }

    // Função de recuperação de senha
    if (forgotPasswordForm) {
        document.getElementById('pwd-request-btn').addEventListener("click", async (event) => {
            event.preventDefault();

            const email = document.getElementById("forgot-email").value.trim();

            if (!email) {
                alert("Por favor, digite seu email.");
                return;
            }

            try {
                const response = await globalThis.fetchWithLoader(`${apiUrl}/auth/forgot-password`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ email }),
                });

                if (response.ok) {
                    document.getElementById("forgot-password-message").innerText = "Email enviado com sucesso!";
                    forgotPasswordForm.reset();
                    window.location.href = "/redefinir";
                } else {
                    const data = await response.json();
                    alert("Erro ao enviar email.\n" + Object.values(data).join("\n"));
                }
            } catch (error) {
                console.error("Erro ao enviar email:", error);
                alert("Erro ao enviar email. Tente novamente mais tarde.");
            }
        });
    }

    // Função para alternar visibilidade da senha
    const togglePasswordVisibility = (passwordFieldId) => {
        const passwordField = document.getElementById(passwordFieldId);
        const icon = passwordField.nextElementSibling;

        if (passwordField.type === "password") {
            passwordField.type = "text";
            icon.classList.remove("bi-eye-slash");
            icon.classList.add("bi-eye");
        } else {
            passwordField.type = "password";
            icon.classList.remove("bi-eye");
            icon.classList.add("bi-eye-slash");
        }
    };

    // Adicionar funcionalidade de exibição de senha para todos os ícones
    document.querySelectorAll(".toggle-password").forEach((icon) => {
        icon.addEventListener("click", () => {
            const passwordField = icon.previousElementSibling;
            togglePasswordVisibility(passwordField.id);
        });
    });

    // Alternar formulário de cadastro entre cliente e profissional
    const toggleRegisterForm = (userType) => {
        const professionalFields = document.querySelectorAll(".professional-field");
        const userFields = document.querySelectorAll(".user-field");

        if (userType === "professional") {
            professionalFields.forEach((field) => (field.style.display = "block"));
            userFields.forEach((field) => (field.style.display = "none"));
        } else {
            professionalFields.forEach((field) => (field.style.display = "none"));
            userFields.forEach((field) => (field.style.display = "block"));
        }
    };

    // Eventos para alternar o tipo de usuário
    document.querySelectorAll("input[name='userType']").forEach((radio) => {
        radio.addEventListener("change", (event) => {
            toggleRegisterForm(event.target.value);
        });
    });

    // Modal de recuperação de senha
    const forgotPasswordLink = document.getElementById("forgot-password-link");
    const closeForgotPassword = document.getElementById("close-forgot-password");
    const forgotPasswordModal = document.getElementById("forgot-password-modal");
    const mainContainer = document.getElementById("main-container");

    if (forgotPasswordLink && closeForgotPassword && forgotPasswordModal) {
        forgotPasswordLink.addEventListener("click", (event) => {
            event.preventDefault();
            forgotPasswordModal.classList.add("active");
            mainContainer.classList.add("hidden");
        });

        closeForgotPassword.addEventListener("click", (event) => {
            event.preventDefault();
            forgotPasswordModal.classList.remove("active");
            mainContainer.classList.remove("hidden");
        });
    }

    // Validação de força da senha
    const passwordInput = document.getElementById('password');
    const bar = document.getElementById('password-strength-bar');
    const rules = [
        { id: 'rule-length', test: v => v.length >= 5 },
        { id: 'rule-upper', test: v => /[A-Z]/.test(v) },
        { id: 'rule-lower', test: v => /[a-z]/.test(v) },
        { id: 'rule-number', test: v => /\d/.test(v) },
        { id: 'rule-special', test: v => /[\W_]/.test(v) }
    ];

    if (passwordInput) {
        passwordInput.addEventListener('input', () => {
            let value = passwordInput.value;
            let passed = 0;
            rules.forEach(rule => {
                const li = document.getElementById(rule.id);
                if (rule.test(value)) {
                    li.classList.add('valid');
                    passed++;
                } else {
                    li.classList.remove('valid');
                }
            });
            bar.style.width = (passed * 20) + '%';
            bar.style.background = passed < 3 ? '#d6439e' : passed < 5 ? '#f8c01b' : '#35cda7';
        });
    }

    // Validação de força da senha SÓ NO CADASTRO
    const registerPasswordInput = document.getElementById('register-password');
    const registerBar = document.getElementById('register-password-strength-bar');
    const registerStrengthDiv = document.querySelector('.password-strength');
    const registerRules = [
        { id: 'register-rule-length', test: v => v.length >= 6 },
        { id: 'register-rule-number', test: v => /\d/.test(v) },
        { id: 'register-rule-special', test: v => /[\W_]/.test(v) },
        { id: 'register-rule-upper', test: v => /[A-Z]/.test(v) },
        { id: 'register-rule-lower', test: v => /[a-z]/.test(v) }
    ];

    if (registerPasswordInput) {
        registerPasswordInput.addEventListener('input', () => {
            // Mostrar dicas ao digitar
            if (registerPasswordInput.value.length > 0) {
                registerStrengthDiv.style.display = 'block';
            } else {
                registerStrengthDiv.style.display = 'none';
            }

            let value = registerPasswordInput.value;
            let passed = 0;
            registerRules.forEach(rule => {
                const li = document.getElementById(rule.id);
                if (rule.test(value)) {
                    li.classList.add('valid');
                    passed++;
                } else {
                    li.classList.remove('valid');
                }
            });
            registerBar.style.width = (passed * 20) + '%';
            registerBar.style.background = passed < 3 ? '#d6439e' : passed < 5 ? '#f8c01b' : '#35cda7';
        });
    }
};
