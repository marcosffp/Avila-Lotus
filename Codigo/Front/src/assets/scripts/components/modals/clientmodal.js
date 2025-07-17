import Cookies from "js-cookie";
const apiUrl = import.meta.env.PUBLIC_API_URL;

export function configurarModalCliente() {
    document.addEventListener("DOMContentLoaded", () => {
        const modal = document.getElementById('client-modal');
        const cancelButton = document.getElementById('client-cancel-btn');
        const confirmButton = document.getElementById('client-confirm-btn');

        if (cancelButton && modal) {
            cancelButton.addEventListener('click', () => {
                modal.classList.add('hidden');
            });
        }

        if (confirmButton && modal) {
            confirmButton.addEventListener('click', () => {
                modal.classList.add('hidden');
                enviarReserva();
            });
        }
    });
}

export function abrirModalComDados({ id, data, hora, valor = 100 }) {
    const modal = document.getElementById('client-modal');
    if (!modal) return;

    const diaSemana = new Date(data).toLocaleDateString('pt-BR', { weekday: 'long' });
    const diaMes = new Date(data).toLocaleDateString('pt-BR', { day: 'numeric', month: 'short' });

    modal.querySelector(".service-date").textContent = `${capitalize(diaSemana)}, ${diaMes}`;
    modal.querySelector(".service-time").textContent = `${hora} - ${calcularFim(hora)}`;
    modal.querySelector(".service-price").textContent = `R$ ${valor.toFixed(2).replace('.', ',')}`;
    modal.dataset.id = id;
    carregarChavePix();

    modal.classList.remove("hidden");
}

function calcularFim(horaInicio) {
    const [h, m] = horaInicio.split(":").map(Number);
    const inicio = new Date();
    inicio.setHours(h, m, 0);
    inicio.setMinutes(inicio.getMinutes() + 60);
    return inicio.toTimeString().slice(0, 5);
}

function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

async function carregarChavePix() {
    const pixKeyElement = document.getElementById('pix-key');
    try {
        const response = await globalThis.fetchWithLoader(`${apiUrl}/prof/pix`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${Cookies.get("token")}`
            }
        });

        const data = await response.json();
        const chavePix = data.pix;

        pixKeyElement.innerHTML = `
            ${chavePix}
            <i class="bi bi-copy pix-copy-icon ml-1 cursor-pointer" title="Copiar chave PIX"></i>
        `;

        // Adiciona o listener ao ícone depois que ele for inserido
        const copyIcon = pixKeyElement.querySelector('.pix-copy-icon');
        if (copyIcon) {
            copyIcon.addEventListener('click', () => {
                navigator.clipboard.writeText(chavePix)
                    .then(() => {
                        copyIcon.classList.remove("bi-copy");
                        copyIcon.classList.add("bi-clipboard-check");
                        setTimeout(() => {
                            copyIcon.classList.remove("bi-clipboard-check");
                            copyIcon.classList.add("bi-copy");
                        }, 2000);
                    })
                    .catch(err => {
                        alert("Erro ao copiar chave PIX.");
                        console.error(err);
                    });
            });
        }

    } catch (e) {
        pixKeyElement.textContent = "Erro ao carregar chave PIX.";
        pixKeyElement.style.color = "red";
        console.error(e);
    }
}

async function enviarReserva(){
    const modal = document.getElementById("client-modal");
    const token = Cookies.get("token");
    const comprovanteInput = document.getElementById("dropzone-comprovante");

    if (!modal || !token) return;
    if (!comprovanteInput.files[0]) {
        alert("Por favor, insira o comprovante.");
        return;
    }

    const file = comprovanteInput.files[0];

    // Verificar tamanho do arquivo
    const fileSizeKB = file.size / 1024;
    if (fileSizeKB > 500) {
        const confirmar = confirm(
            "O arquivo PDF tem mais de 500KB.\n" +
            "Deseja abrir um site para comprimir o arquivo?"
        );
        if (confirmar) {
            window.open("https://smallpdf.com/compress-pdf", "_blank", "noopener,noreferrer");
        }
        return;
    }    

    const disponibilidadeId = modal.dataset.id;
    const observacao = document.getElementById("observation").value;
    const valorTexto = document.querySelector(".service-price").textContent;
    const valorNumerico = parseFloat(valorTexto.replace("R$", "").replace(",", ".").trim());
    const valorServico50Porcent = valorNumerico / 2;

    const formData = new FormData();
    formData.append("disponibilidadeId", disponibilidadeId);
    formData.append("observacao", observacao);
    formData.append("valorServico50Porcent", valorServico50Porcent.toString());
    formData.append("comprovante", comprovanteInput.files[0]);

    try {
        const response = await globalThis.fetchWithLoader(`${apiUrl}/agendamentos/criar`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });
    
        if (!response.ok) {
            const data = await response.json();
            alert("Erro ao realizar agendamento.\n" + Object.values(data).join("\n"));
            return;
        }
    
        // sucesso
        alert("Reserva criada com sucesso!");
        window.location.reload();
    } catch (error) {
        console.error("Erro ao criar reserva:", error);
    }
}