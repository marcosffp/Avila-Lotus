import Cookies from "js-cookie";

export const reportModal = () => {
    const modal = document.getElementById('relatorio-modal');
    const openBtn = document.getElementById('open-relatorio-btn');
    const closeBtn = document.getElementById('cancelar-relatorio-btn');
    const confirmarBtn = document.getElementById('confirmar-relatorio-btn');
    const mesAtualBtn = document.getElementById('mes-atual-btn');
    const dataInicio = document.getElementById('data-inicio');
    const dataFim = document.getElementById('data-fim');
    const errorDiv = document.getElementById('report-error');

    const apiUrl = import.meta.env.PUBLIC_API_URL;
    const token = Cookies.get("token");

    let isMesAtual = false;

    if (openBtn && modal) {
        openBtn.addEventListener('click', () => {
            modal.classList.remove('hidden');
            errorDiv.textContent = '';
        });
    }

    
    if (closeBtn && modal) {
        closeBtn.addEventListener('click', () => {
            modal.classList.add('hidden');
        });
    }

    if (mesAtualBtn) {
        mesAtualBtn.addEventListener('click', () => {
            const now = new Date();
            const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
            const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0);
            dataInicio.value = firstDay.toISOString().split('T')[0];
            dataFim.value = lastDay.toISOString().split('T')[0];
            isMesAtual = true;
        });
    }

    if (dataInicio && dataFim) {
        dataInicio.addEventListener('input', () => { isMesAtual = false; });
        dataFim.addEventListener('input', () => { isMesAtual = false; });
    }

    if (confirmarBtn) {
        confirmarBtn.addEventListener('click', async (e) => {
            e.preventDefault();
            errorDiv.textContent = '';

            if (!dataInicio.value || !dataFim.value) {
                errorDiv.textContent = "Preencha as duas datas!";
                return;
            }
            if (dataInicio.value > dataFim.value) {
                errorDiv.textContent = "A data de início não pode ser maior que a data de fim!";
                return;
            }

            try {
                let url;
                let fileName = "relatorio.pdf";

                if (isMesAtual) {
                    const [ano, mes] = dataInicio.value.split('-');
                    url = `${apiUrl}/relatorio/mensal?mes=${mes}&ano=${ano}`;
                    fileName = `relatorio_${mes}-${ano}.pdf`;
                } else {
                    url = `${apiUrl}/relatorio/periodo?dataInicio=${dataInicio.value}&dataFim=${dataFim.value}`;

                    // Formatar datas para DD-MM-YYYY
                    const formatDate = (dateStr) => {
                        const [year, month, day] = dateStr.split('-');
                        return `${day}-${month}-${year}`;
                    };
                    
                    fileName = `relatorio_${formatDate(dataInicio.value)}_a_${formatDate(dataFim.value)}.pdf`;
                }
                const res = await globalThis.fetchWithLoader(url, {
                    method: "GET",
                    headers: { Authorization: `Bearer ${token}` }
                });

                if (!res.ok) {
                    const err = await res.json();
                    errorDiv.textContent = "Erro ao gerar relatório: " + (err.message || Object.values(err).join("\n"));
                    return;
                }

                const blob = await res.blob();
                const urlBlob = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = urlBlob;
                a.download = fileName;
                document.body.appendChild(a);
                a.click();
                a.remove();
                window.URL.revokeObjectURL(urlBlob);

                modal.classList.add('hidden');
                dataInicio.value = "";
                dataFim.value = "";
                isMesAtual = false;
            } catch (err) {
                errorDiv.textContent = "Erro ao conectar com o servidor.";
            }
        });
    }
};