import Cookies from "js-cookie";

export const createDisponibilityModal = () => {
    const modal = document.getElementById('disponibility-modal');
    const cancelButton = document.getElementById('disponibility-cancel-btn');
    const createButton = document.getElementById('disponibility-create-btn');
    const dateInput = document.getElementById('disponibility-date-input');
    const timeInput = document.getElementById('disponibility-time-input');

    const apiUrl = import.meta.env.PUBLIC_API_URL;
    const token = Cookies.get("token");

    if (cancelButton && modal) {
        cancelButton.addEventListener('click', () => {
            modal.classList.add('hidden');
        });
    }

    if (dateInput) {
        dateInput.min = new Date().toISOString().split("T")[0];
    }

    if (createButton) {
        createButton.addEventListener("click", async () => {
            const data = dateInput.value;
            const hora = timeInput.value;

            if (!data || !hora) {
                alert("Preencha a data e o horário!");
                return;
            }

            const body = {
                data: data,
                horaInicio: hora
            };

            try {
                const res = await globalThis.fetchWithLoader(`${apiUrl}/disponibilidade/cadastrar`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${token}`
                    },
                    body: JSON.stringify(body)
                });

                if (!res.ok) {
                    const err = await res.json();
                    alert("Erro ao cadastrar: " + Object.values(err).join("\n"));
                    return;
                }

                alert("Horário cadastrado com sucesso!");
                modal.classList.add('hidden');
                dateInput.value = "";
                timeInput.value = "";
                window.location.reload();
            } catch (err) {
                console.error(err);
                alert("Erro ao conectar com o servidor.");
            }
        });
    }
};