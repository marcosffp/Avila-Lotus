import { jwtDecode } from "jwt-decode";
import { abrirModalComDados, configurarModalCliente } from "./modals/clientmodal";

export async function carregarCalendario(token, apiUrl) {
    const diasContainer = document.getElementById("dias-da-semana");
    const mesAtualDiv = document.getElementById("mes-atual");
    const diasDaSemanaNomes = ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"];
    const hoje = new Date();
    
    // Obter o mês e ano formatados
    const mesAnoFormatado = hoje.toLocaleDateString("pt-BR", {
        month: "long",
        year: "numeric",
    });
    
    // Capitalizar a primeira letra do mês
    const mesAnoCapitalizado = mesAnoFormatado.replace(/^\w/, (letra) => letra.toUpperCase());
    
    // Atualizar o conteúdo da div
    mesAtualDiv.textContent = mesAnoCapitalizado;

    let diasDisponiveis = [];

    try {
        const res = await globalThis.fetchWithLoader(`${apiUrl}/disponibilidade/dias`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        diasDisponiveis = await res.json(); // Ex: ["8-4", "9-4"]
    } catch (err) {
        console.error("Erro ao carregar dias disponíveis:", err);
    }

    diasContainer.innerHTML = "";

    if (!diasDisponiveis.length) {
        diasContainer.innerHTML = `<div class="nenhum-dia mx-auto text-center">Não há dias disponíveis para reserva de horário.</div>`;
        return;
    }

    const diasConvertidos = diasDisponiveis
        .map(str => {
            const [dia, mes] = str.split('-');
            return new Date(hoje.getFullYear(), parseInt(mes) - 1, parseInt(dia));
        })
        .filter(dia => dia >= hoje) // Filtra dias futuros
        .sort((a, b) => a - b);

    if (diasConvertidos.length === 0) {
        diasContainer.innerHTML = `<div class="nenhum-dia mx-auto text-center">Não há dias disponíveis para reserva de horário.</div>`;
        return;
    }

    diasConvertidos.forEach((dia, i) => {
        const dataFormatada = dia.toISOString().split("T")[0];
        const diaSemanaNome = diasDaSemanaNomes[dia.getDay()];
        const diaNumero = dia.getDate();

        const diaDiv = document.createElement("div");
        diaDiv.className = "dia";
        diaDiv.dataset.index = i;

        const span = document.createElement("span");
        span.textContent = diaSemanaNome;

        const numeroDia = document.createElement("div");
        numeroDia.className = "numero-dia disponivel";
        numeroDia.dataset.date = dataFormatada;
        numeroDia.textContent = diaNumero;

        const mes = document.createElement("span");
        mes.className = "mes";
        mes.textContent = dia.toLocaleDateString("pt-BR", { month: "short" });

        diaDiv.appendChild(mes);
        diaDiv.appendChild(numeroDia);
        diaDiv.appendChild(span);
        diasContainer.appendChild(diaDiv);
    });
}

export async function carregarHorariosDoDia(diaMes, token, apiUrl) {
    const container = document.getElementById("horarios-disponiveis-container");
    container.innerHTML = "<p>Carregando horários...</p>";
    
    try {
        const res = await globalThis.fetchWithLoader(`${apiUrl}/disponibilidade/horarios?diaMes=${diaMes}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        if (!res.ok) {
            throw new Error("Erro ao buscar horários.");
        }

        let horarios = await res.json(); // [{ id: 1, horaInicio: "14:00" }, ...]

        if (horarios.length === 0) {
            container.innerHTML = "<p>Não há horários disponíveis neste dia.</p>";
            return;
        }

        // Ordenar os horários pela horaInicio
        horarios = horarios.sort((a, b) => a.horaInicio.localeCompare(b.horaInicio));

        container.innerHTML = ""; // limpa antes de renderizar

        const decodedToken = jwtDecode(token);

        horarios.forEach(horario => {
            const div = document.createElement("div");
            div.className = "horario-card";
            div.textContent = horario.horaInicio.slice(0, 5); // mostra só HH:mm
            div.dataset.id = horario.id;
            const deleteIcon = document.createElement("span");
            if(decodedToken.tipo === 'CLIENTE')
                deleteIcon.style.display = 'none';
            deleteIcon.className = "delete-icon";
            deleteIcon.innerHTML = `<i class="bi bi-trash3-fill"></i>`;
            deleteIcon.addEventListener('click', () => {
                deleteHorario(horario.id, diaMes, token, apiUrl);
            });
            div.appendChild(deleteIcon);   
            div.addEventListener('click', () => {
                if(decodedToken.tipo !== 'PROFISSIONAL')
                    reservarHorario(horario.id, diaMes, token, apiUrl);
            });
            container.appendChild(div);
        });
    } catch (err) {
        console.error(err);
        container.innerHTML = "<p>Erro ao carregar horários.</p>";
    }
}

const deleteHorario = async (horarioId, diaMes, token, apiUrl) => {
    try {
        const res = await globalThis.fetchWithLoader(`${apiUrl}/disponibilidade/excluir?id=${horarioId}`, {
            method: 'DELETE',
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        if (!res.ok) {
            throw new Error("Erro ao deletar horário.");
        }

        carregarHorariosDoDia(diaMes, token, apiUrl);
    } catch (err) {
        console.error(err);
    }
}

const reservarHorario = (horarioId, diaMes) => {
    // diaMes: formato "8-4"
    const [dia, mes] = diaMes.split('-').map(Number);
    const data = new Date(new Date().getFullYear(), mes - 1, dia);

    // Encontrar a hora com base no ID do horário clicado
    const card = document.querySelector(`.horario-card[data-id="${horarioId}"]`);
    const hora = card?.textContent?.trim();

    if (!hora) {
        console.error("Hora não encontrada no card.");
        return;
    }

    abrirModalComDados({
        id: horarioId,
        data,
        hora,
        valor: 100 // valor fixo
    });
};

configurarModalCliente();