import Cookies from 'js-cookie';
import { jwtDecode } from 'jwt-decode';
import { logout, validateToken } from "./components/validateToken";
import { carregarCalendario, carregarHorariosDoDia } from "./components/calendar";
import { carregarAgendamentos } from "./components/reservas";

export const agenda = async () => {
    if (!validateToken()) logout();

    const token = Cookies.get("token");
    const apiUrl = import.meta.env.PUBLIC_API_URL;
    const decodedToken = jwtDecode(token);

    const deletIcons = document.querySelectorAll('.delete-icon');
    const addHorarioBtn = document.getElementById('add-horario-btn');
    const copyAgenda = document.getElementById('copy-agenda');
    const relatorioBtn = document.getElementById('open-relatorio-btn');

    // Permissões baseadas no tipo
    if (decodedToken.tipo !== 'PROFISSIONAL') {
        deletIcons.forEach(icon => icon.style.display = 'none');
        if (addHorarioBtn) addHorarioBtn.style.display = 'none';
        if (relatorioBtn) relatorioBtn.style.display = 'none';
        if (copyAgenda) copyAgenda.innerText = 'Escolha um dia e um horário para agendar!';
    } else {
        if (copyAgenda) copyAgenda.innerText = 'Crie horários, gerencie sua agenda e copie o link para compartilhar com seus pacientes!';
    }

    // Chama o calendário
    await carregarCalendario(token, apiUrl);

    document.querySelectorAll(".numero-dia").forEach(dia => {
        dia.addEventListener("click", () => {
            document.querySelectorAll(".numero-dia").forEach(d => d.classList.remove("selecionado"));
            dia.classList.add("selecionado");

            const [ano, mes, diaDoMes] = dia.dataset.date.split('-').map(Number);
            const dataSelecionada = new Date(ano, mes - 1, diaDoMes); // mês começa do zero
            const diaMes = `${dataSelecionada.getDate()}-${dataSelecionada.getMonth() + 1}`;
            carregarHorariosDoDia(diaMes, token, apiUrl);
        });
    });

    // Alternância de tabs
    const disponibilidadeTab = document.getElementById("disponibilidade-tab");
    const agendamentosTab = document.getElementById("agendamentos-tab");
    const disponibilidadeContainer = document.getElementById("disponibilidade-container");
    const agendamentosContainer = document.getElementById("agendamentos-container");

    disponibilidadeTab?.addEventListener("click", () => {
        sessionStorage.setItem("activeTab", "disponibilidade");

        disponibilidadeTab.classList.add("active");
        agendamentosTab.classList.remove("active");

        disponibilidadeContainer.classList.replace("hidden", "flex");
        agendamentosContainer.classList.replace("flex", "hidden");
    });

    agendamentosTab?.addEventListener("click", () => {
        sessionStorage.setItem("activeTab", "agendamentos");

        agendamentosTab.classList.add("active");
        disponibilidadeTab.classList.remove("active");

        agendamentosContainer.classList.replace("hidden", "flex");
        disponibilidadeContainer.classList.replace("flex", "hidden");
    });

    // Restaurar estado salvo da aba
    const activeTab = sessionStorage.getItem("activeTab");

    if (activeTab === "agendamentos") {
        agendamentosTab.classList.add("active");
        disponibilidadeTab.classList.remove("active");

        agendamentosContainer.classList.replace("hidden", "flex");
        disponibilidadeContainer.classList.replace("flex", "hidden");
    } else {
        // Default: disponibilidade
        disponibilidadeTab.classList.add("active");
        agendamentosTab.classList.remove("active");

        disponibilidadeContainer.classList.replace("hidden", "flex");
        agendamentosContainer.classList.replace("flex", "hidden");
    }

    carregarAgendamentos(token, apiUrl);

    // Adicione este script para mostrar o nome do arquivo
    const dropzone = document.getElementById('dropzone-comprovante');
    if (dropzone) {
        dropzone.addEventListener('change', function(e) {
            const fileName = e.target.files[0]?.name || 'Nenhum arquivo selecionado';
            const fileNameElem = document.querySelector('.file-name');
            if (fileNameElem) fileNameElem.textContent = fileName;
        });
    }
};