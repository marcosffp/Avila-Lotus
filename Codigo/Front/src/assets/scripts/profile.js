import { jwtDecode } from "jwt-decode";
import Cookies from "js-cookie";
import { logout, validateToken } from "./components/validateToken";

export const profile = () => {
    if (!validateToken()) logout();

    const apiUrl = import.meta.env.PUBLIC_API_URL;
    const urlParams = new URLSearchParams(window.location.search);
    const isFirstLogin = urlParams.get("firstLogin") === "true";

    const token = Cookies.get("token");
    const decodedToken = jwtDecode(token);
    const email = urlParams.get("email") || decodedToken.sub;

    const handleBeforeUnload = (e) => {
        if(window.isLogginOut) return;
        e.preventDefault();
        e.returnValue = "Você ainda não completou seu perfil. Tem certeza que deseja sair?";
    };

    const isProfileIncomplete = () => {
        const requiredFields = ["nome-input", "telefone-input", "dataDeNascimento-input", "CPF-input", "PIX-input"];
        return requiredFields.some(id => {
            const el = document.getElementById(id);
            return !el || !el.value.trim();
        });
    };

    // Após preencher os dados...
    if (isFirstLogin && isProfileIncomplete()) {
        window.addEventListener("beforeunload", handleBeforeUnload);
    }

    const getClientFullData = () => {
        globalThis.fetchWithLoader(`${apiUrl}/cliente/perfil-full?email=${email}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + token
            },
            cache: "force-cache"
        })
        .then(response => response.json())
        .then(data => {
            if (data.email !== decodedToken.sub) disableButtons();

            // Preenche perfil
            const profileFields = {
                "nome-input": data.nome,
                "telefone-input": data.telefone,
                "dataDeNascimento-input": data.dataNascimento,
                "CPF-input": data.CPF,
                "PIX-input": data.PIX,
                "email-input": data.email
            };

            Object.entries(profileFields).forEach(([id, value]) => {
                const input = document.getElementById(id);
                if (input) input.value = value || "";
            });

            // Preenche campos de texto da anamnese
            const textFields = {
                "queixas-input": data.queixas,
                "cirurgia-input": data.nomeCirurgia,
                "remedio-input": data.nomeRemedio,
                "anticoncepcional-input": data.nomeAnticoncepcional,
                "alergiaMedicamento-input": data.nomeAlergiaMedicamento,
                "pressao-input": data.valorPressao,
                "tratamento-input": data.nomeTratamento
            };

            Object.entries(textFields).forEach(([id, value]) => {
                if (document.getElementById(id)) {
                    document.getElementById(id).value = value || "";
                }
            });

            // Preenche radios Sim/Não
            const radioFields = [
                "gestante", "tontura", "cirurgia", "asma", "fumante",
                "hepatite", "convulsao", "renal", "remedio", "pressao",
                "problemas", "anticoncepcional", "tratamento",
                "alergiaMedicamento", "diabetes", "cardiaco"
            ];

            radioFields.forEach(field => {
                const value = data[field];
                if (document.getElementById(`${field}-sim-radio`)) {
                    document.getElementById(`${field}-sim-radio`).checked = value === true;
                    document.getElementById(`${field}-nao-radio`).checked = value === false;
                }
            });
        })
        .catch(err => console.error("Erro ao carregar os dados completos do cliente:", err));
    };

    // Função para atualizar os dados do perfil
    const updateProfileData = () => {
        const formErrorDiv = document.getElementById("form-error");
        formErrorDiv.innerHTML = "";
        formErrorDiv.classList.add("hidden");

        const formData = {
            nome: document.getElementById("nome-input").value,
            telefone: document.getElementById("telefone-input").value,
            dataNascimento: document.getElementById("dataDeNascimento-input").value,
            CPF: document.getElementById("CPF-input").value,
            PIX: document.getElementById("PIX-input").value
        };

        globalThis.fetchWithLoader(`${apiUrl}/cliente/editar-perfil`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + token
            },
            body: JSON.stringify(formData)
        })
        .then(async response => {
            const contentType = response.headers.get("content-type");

            // Tenta ler o corpo como JSON, apenas se houver conteúdo
            let data = {};
            if (contentType && contentType.includes("application/json")) {
                data = await response.json();
            }

            if (!response.ok) {
                let hasErrors = false;

                for (const field in data) {
                    const input = document.getElementById(`${field}-input`);
                    if (input) input.classList.add("border-red-500");

                    if (data[field]) {
                        hasErrors = true;
                        const errorMsg = document.createElement("div");
                        errorMsg.textContent = data[field];
                        formErrorDiv.appendChild(errorMsg);
                    }
                }

                if (hasErrors) {
                    formErrorDiv.classList.remove("hidden");
                } else {
                    // Mensagem genérica se não houver erro específico
                    formErrorDiv.innerHTML = "<div>Erro ao salvar as informações. Verifique os dados e tente novamente.</div>";
                    formErrorDiv.classList.remove("hidden");
                }

                throw new Error("Erro de validação");
            }

            alert("Informações do perfil atualizadas com sucesso!");
            window.removeEventListener("beforeunload", handleBeforeUnload);
            getClientFullData();
        })
        .catch(err => {
            if (err.message !== "Erro de validação") {
                console.error(err);
                formErrorDiv.innerHTML = "<div>Erro inesperado ao salvar as informações. Tente novamente mais tarde.</div>";
                formErrorDiv.classList.remove("hidden");
            }
        });
    };

    // Função para atualizar os dados da anamnese
    const updateAnamnesisData = () => {
        const formData = {
            queixas: document.getElementById("queixas-input").value,
            nomeCirurgia: document.getElementById("cirurgia-input").value,
            nomeRemedio: document.getElementById("remedio-input").value,
            nomeAnticoncepcional: document.getElementById("anticoncepcional-input").value,
            nomeAlergiaMedicamento: document.getElementById("alergiaMedicamento-input").value,
            valorPressao: document.getElementById("pressao-input").value,
            nomeTratamento: document.getElementById("tratamento-input").value,
            gestante: document.getElementById("gestante-sim-radio").checked,
            tontura: document.getElementById("tontura-sim-radio").checked,
            cirurgia: document.getElementById("cirurgia-sim-radio").checked,
            asma: document.getElementById("asma-sim-radio").checked,
            fumante: document.getElementById("fumante-sim-radio").checked,
            hepatite: document.getElementById("hepatite-sim-radio").checked,
            convulsao: document.getElementById("convulsao-sim-radio").checked,
            renal: document.getElementById("renal-sim-radio").checked,
            remedio: document.getElementById("remedio-sim-radio").checked,
            pressao: document.getElementById("pressao-sim-radio").checked,
            problemas: document.getElementById("problemas-sim-radio").checked,
            anticoncepcional: document.getElementById("anticoncepcional-sim-radio").checked,
            tratamento: document.getElementById("tratamento-sim-radio").checked,
            alergiaMedicamento: document.getElementById("alergiaMedicamento-sim-radio").checked,
            diabetes: document.getElementById("diabetes-sim-radio").checked,
            cardiaco: document.getElementById("cardiaco-sim-radio").checked
        };

        globalThis.fetchWithLoader(`${apiUrl}/cliente/editar-anamnese`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: "Bearer " + token
            },
            body: JSON.stringify(formData)
        })
        .then(response => {
            if (!response.ok) throw new Error("Erro ao salvar");
            alert("Anamnese atualizada!");
            getClientFullData();
        })
        .catch(err => {
            console.error(err);
            alert("Erro ao salvar.");
        });
    };

    // Função para desabilitar campos do perfil
    const disableProfileFields = () => {
        const profileFields = ["nome", "telefone", "dataDeNascimento", "CPF", "PIX"];
        profileFields.forEach(field => {
            const input = document.getElementById(`${field}-input`);
            if (input) input.disabled = true;
        });
    };

    // Função para desabilitar todos os campos da anamnese
    const disableAnamnesisFields = () => {
        document.querySelectorAll('.anamnese input, .anamnese textarea').forEach(input => {
            input.disabled = true;
        });
        document.getElementById("queixas-input").disabled = true;
    };

    const disableButtons = () => {
        editProfileButton.style.display = "none";
        editAnamnesisButton.style.display = "none";
    };

    const editProfileButton = document.getElementById("edit-info-btn");
    const editAnamnesisButton = document.getElementById("edit-anam-btn");

    if (isFirstLogin)
        document.getElementById("information-modal").classList.replace("hidden", "flex");

    getClientFullData();

    if(decodedToken.tipo !== 'CLIENTE'){
        disableButtons();
    }

    disableProfileFields();
    disableAnamnesisFields();

    editProfileButton.addEventListener("click", () => {
        if (editProfileButton.innerText === "Editar") {
            document.getElementById("nome-input").disabled = false;
            document.getElementById("telefone-input").disabled = false;
            document.getElementById("dataDeNascimento-input").disabled = false;
            document.getElementById("CPF-input").disabled = false;
            document.getElementById("PIX-input").disabled = false;

            editProfileButton.innerText = "Salvar";
        } else {
            updateProfileData();

            document.getElementById("nome-input").disabled = true;
            document.getElementById("telefone-input").disabled = true;
            document.getElementById("dataDeNascimento-input").disabled = true;
            document.getElementById("CPF-input").disabled = true;
            document.getElementById("PIX-input").disabled = true;

            editProfileButton.innerText = "Editar";
        }
    });

    editAnamnesisButton.addEventListener("click", () => {
        if (editAnamnesisButton.innerText === "Editar") {
            // Habilita todos os inputs
            document.querySelectorAll('.anamnese input, .anamnese textarea').forEach(input => {
                input.disabled = false;
            });
            document.getElementById("queixas-input").disabled = false;
            editAnamnesisButton.innerText = "Salvar";
        } else {
            updateAnamnesisData();
            // Desabilita todos os inputs
            document.querySelectorAll('.anamnese input, .anamnese textarea').forEach(input => {
                input.disabled = true;
            });
            editAnamnesisButton.innerText = "Editar";
        }
    });
};
