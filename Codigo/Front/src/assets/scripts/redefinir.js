import Cookies from 'js-cookie';
import { jwtDecode } from 'jwt-decode';

export const redefinir = () => {
    // Referências aos elementos do DOM
    const resetPasswordForm = document.getElementById("reset-password-form");

    // Função de redefinição de senha
    if (resetPasswordForm) {
        document.getElementById('pwd-reset-btn').addEventListener("click", async (event) => {
            event.preventDefault();

            const email = document.getElementById("reset-email").value.trim();
            const code = document.getElementById("reset-code").value.trim();
            const newPassword = document.getElementById("reset-password").value.trim();

            if (!email || !code || !newPassword) {
                alert("Por favor, preencha todos os campos.");
                return;
            }

            if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{5,}$/.test(newPassword)) {
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

            try {
                const apiUrl = import.meta.env.PUBLIC_API_URL;
                const response = await globalThis.fetchWithLoader(`${apiUrl}/auth/reset-password`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ email, code, newPassword }),
                });

                if (response.ok) {
                    alert("Senha redefinida com sucesso!");
                    window.location.href = "/auth";
                } else {
                    const data = await response.json();
                    alert("Erro ao redefinir senha.\n" + Object.values(data).join("\n"));
                }
            } catch (error) {
                console.error("Erro ao redefinir senha:", error);
                alert("Erro ao redefinir senha. Tente novamente mais tarde.");
            }
        });
    }

    // Função para alternar visibilidade da senha
    function togglePasswordVisibility(passwordFieldId) {
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
    }

    // Adicionar funcionalidade de exibição de senha para todos os ícones
    document.querySelectorAll(".toggle-password").forEach((icon) => {
        icon.addEventListener("click", () => {
            const passwordField = icon.previousElementSibling;
            togglePasswordVisibility(passwordField.id);
        });
    });
}

