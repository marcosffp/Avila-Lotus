import { 
    handleCancelarAgendamentoModal, 
    handleConfirmarAgendamento, 
    handleConcluiAgendamento, 
    carregarChavePix
} from "../reservas.js";

export const appointmentDetailsModal = () => {
    const modal = document.getElementById('appointment-details-modal');
    const closeButton = modal?.querySelector('.close-button');
    const rejectButton = document.getElementById('reject-appointment-btn');
    const acceptButton = document.getElementById('accept-appointment-btn');
    const cancelButton = document.getElementById('cancel-modal-btn');
    const confirmButton = document.getElementById('confirm-modal-btn');
    const cancelApprovedButton = document.getElementById('cancel-appointment-btn');
    const presentButton = document.getElementById('btn-compareceu');
    const absentButton = document.getElementById('btn-nao-compareceu');
    const cancelReasonSection = document.getElementById('cancel-reason-section');
    const rejectionReasonSection = document.getElementById('rejection-reason-section');
    const comprovanteInput = document.getElementById('reject-dropzone-comprovante');
    let currentOption = null;

    const limparEstiloOpcoes = () => {
        cancelApprovedButton.style.backgroundColor = '#F2ECDF';
        cancelApprovedButton.style.color = '#e65729';
        rejectButton.style.backgroundColor = '#F2ECDF';
        rejectButton.style.color = '#d32f2f';
        acceptButton.style.backgroundColor = '#F2ECDF';
        acceptButton.style.color = '#388e3c';
        presentButton.style.backgroundColor = '#F2ECDF';
        presentButton.style.color = '#388e3c';
        absentButton.style.backgroundColor = '#F2ECDF';
        absentButton.style.color = '#d32f2f';
        currentOption = null;

        if (comprovanteInput) comprovanteInput.value = '';
        if (rejectionReasonSection) rejectionReasonSection.style.display = 'none';
        if (cancelReasonSection) cancelReasonSection.style.display = 'none';
    };

    const configurarBotoesModal = () => {
        closeButton?.addEventListener('click', () => {
            modal?.classList.remove('show');
            limparEstiloOpcoes();
        });

        cancelButton?.addEventListener('click', () => {
            modal?.classList.remove('show');
            limparEstiloOpcoes();
        });

        window.addEventListener('click', (event) => {
            if (event.target === modal) {
                modal?.classList.remove('show');
                limparEstiloOpcoes();
            }
        });

        acceptButton?.addEventListener('click', () => {
            limparEstiloOpcoes();
            acceptButton.style.backgroundColor = '#388e3c';
            acceptButton.style.color = '#fff';
            currentOption = 'accept';
        });

        rejectButton?.addEventListener('click', () => {
            limparEstiloOpcoes();
            rejectButton.style.backgroundColor = '#d32f2f';
            rejectButton.style.color = '#fff';
            if (rejectionReasonSection) rejectionReasonSection.style.display = 'block';
            currentOption = 'reject';
        });

        cancelApprovedButton?.addEventListener('click', () => {
            limparEstiloOpcoes();
            cancelApprovedButton.style.backgroundColor = '#e65729';
            cancelApprovedButton.style.color = '#fff';
            if (rejectionReasonSection) rejectionReasonSection.style.display = 'block';
            if (cancelReasonSection) cancelReasonSection.style.display = 'block';
            currentOption = 'cancel';
        });

        presentButton?.addEventListener('click', () => {
            limparEstiloOpcoes();
            presentButton.style.backgroundColor = '#388e3c';
            presentButton.style.color = '#fff';
            currentOption = 'compareceu';
        });

        absentButton?.addEventListener('click', () => {
            limparEstiloOpcoes();
            absentButton.style.backgroundColor = '#d32f2f';
            absentButton.style.color = '#fff';
            currentOption = 'ausente';
        });

        confirmButton?.addEventListener('click', async () => {
            const agendamentoId = modal?.dataset.agendamentoId;
            const comprovante = comprovanteInput?.files?.[0];

            if (!currentOption) return alert('Selecione uma opção antes de confirmar.');

            if (currentOption === 'accept') {
                handleConfirmarAgendamento(agendamentoId);
                return;
            }

            if (currentOption === 'reembolsando') {
                if (!comprovante) return alert('Por favor, anexe um comprovante de reembolso.');
                handleCancelarAgendamentoModal(agendamentoId, 'reembolsar');
                return;
            }

            if (currentOption === 'compareceu') {
                handleConcluiAgendamento(agendamentoId, 'finalizar');
                return;
            }

            if (currentOption === 'ausente') {
                handleConcluiAgendamento(agendamentoId, 'ausente');
                return;
            }

            const tipo = currentOption === 'cancel' ? 'cancelar' : 'recusar';
            handleCancelarAgendamentoModal(agendamentoId, tipo);
        });
    };

    configurarBotoesModal();

    const atualizarModal = (agendamento) => {
        const clienteNomeElement = document.getElementById('modal-cliente-nome');
        const clienteEmailElement = document.getElementById('modal-cliente-email');
        const dataCompletaElement = document.getElementById('modal-data-completa');
        const horarioCompletoElement = document.getElementById('modal-horario-completo');
        const valorFormatadoElement = document.getElementById('modal-valor-formatado');
        const statusTextElement = document.getElementById('modal-status-text');
        const diaElement = document.getElementById('modal-dia');
        const mesAbreviadoElement = document.getElementById('modal-mes-abreviado');
        const statusBar = modal?.querySelector('.status-indicator');

        modal.dataset.agendamentoId = agendamento.id;

        clienteNomeElement.textContent = agendamento.clienteNome || 'Nome do Cliente';
        clienteEmailElement.textContent = agendamento.clienteEmail || 'email@exemplo.com';

        carregarChavePix(agendamento.clienteEmail);

        const [year, month, day] = agendamento.data.split('-').map(Number);
        const dataObj = new Date(year, month - 1, day);
        const dia = dataObj.getDate();
        const mesAbrev = dataObj.toLocaleDateString('pt-BR', { month: 'short' }).replace('.', '');
        const dataCompleta = dataObj.toLocaleDateString('pt-BR', { weekday: 'long', day: '2-digit', month: 'short' });

        dataCompletaElement.textContent = dataCompleta.charAt(0).toUpperCase() + dataCompleta.slice(1);
        horarioCompletoElement.textContent = `${formatarHorario(agendamento.hora)} - ${calcularHoraFim(formatarHorario(agendamento.hora))}`;
        valorFormatadoElement.textContent = agendamento.valor?.replace('.', ',') || '100,00';
        diaElement.textContent = String(dia).padStart(2, '0');
        mesAbreviadoElement.textContent = mesAbrev.charAt(0).toUpperCase() + mesAbrev.slice(1);

        if (comprovanteInput) comprovanteInput.value = '';

        if (statusBar && statusTextElement) {
            statusBar.className = 'status-indicator';
            statusBar.classList.remove('aguardando', 'confirmado', 'cancelado', 'finalizado');

            switch (agendamento.status) {
                case 'PENDENTE': statusBar.classList.add('aguardando'); break;
                case 'APROVADO': statusBar.classList.add('confirmado'); break;
                case 'CANCELADO':
                case 'CANCELADO_PELO_CLIENTE':
                case 'NAO COMPARECEU':
                case 'RECUSADO':
                    statusBar.classList.add('cancelado'); break;
                case 'FINALIZADO': statusBar.classList.add('finalizado'); break;
                default: statusBar.classList.add('aguardando'); break;
            }

            statusTextElement.textContent = agendamento.status?.replace(/_/g, ' ') || 'Aguardando';
        }

        const agora = new Date();
        const [hora, minuto] = agendamento.hora.split(':').map(Number);
        const dataHoraAgendamento = new Date(year, month - 1, day, hora, minuto, 0);

        if (dataHoraAgendamento < agora) {
            acceptButton.style.display = 'none';
            rejectButton.style.display = 'none';
            cancelApprovedButton.classList.replace('inline-block', 'hidden');
            confirmButton.style.display = 'none';
            presentButton.style.display = 'none';
            absentButton.style.display = 'none';
            if(agendamento.status === 'APROVADO' && agendamento.subStatus === 'SEM SUBSTATUS') {
                presentButton.style.display = 'inline-block';
                absentButton.style.display = 'inline-block';
                confirmButton.style.display = 'block';
            }
        } else {
            confirmButton.style.display = 'block';
            cancelButton.style.display = 'block';
            presentButton.style.display = 'none';
            absentButton.style.display = 'none';
            if (agendamento.status === 'PENDENTE') {
                acceptButton.style.display = 'inline-block';
                rejectButton.style.display = 'inline-block';
                cancelApprovedButton.classList.replace('inline-block', 'hidden');
            } else if (agendamento.status === 'APROVADO') {
                acceptButton.style.display = 'none';
                rejectButton.style.display = 'none';
                cancelApprovedButton.classList.replace('hidden', 'inline-block');
            } else {
                acceptButton.style.display = 'none';
                rejectButton.style.display = 'none';
                cancelApprovedButton.classList.replace('inline-block', 'hidden');
                confirmButton.style.display = 'none';
            }
        }

        if (rejectionReasonSection) rejectionReasonSection.style.display = 'none';

        // Verificar substatus para exigir reembolso
        if (agendamento.subStatus === 'REEMBOLSO PENDENTE') {
            // Esconde todos os botões de ação exceto "confirmar"
            acceptButton.style.display = 'none';
            rejectButton.style.display = 'none';
            cancelApprovedButton.classList.replace('inline-block', 'hidden');

            // Mostra botão de confirmar
            confirmButton.style.display = 'block';
            cancelButton.style.display = 'block';

            // Define a currentOption como 'reembolsando'
            currentOption = 'reembolsando';

            // Garante que a seção de motivo não apareça
            if (rejectionReasonSection) rejectionReasonSection.style.display = 'block';
        }
    };

    const formatarHorario = (horario) => {
        const [hh, mm] = horario.split(':');
        return `${hh}:${mm}`;
    };

    const calcularHoraFim = (horaInicio) => {
        const [hh, mm] = horaInicio.split(':').map(Number);
        const date = new Date();
        date.setHours(hh, mm, 0);
        date.setHours(date.getHours() + 1);
        return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    };

    // Expor funções globais para uso externo
    globalThis.atualizarModalDetalhes = (agendamento) => {
        atualizarModal(agendamento);
        modal?.classList.add('show');
    };

    globalThis.esconderModalDetalhes = () => {
        modal?.classList.remove('show');
    };

    // Adicione este script para mostrar o nome do arquivo
    document.getElementById('reject-dropzone-comprovante')?.addEventListener('change', function(e) {
        const fileName = e.target.files[0]?.name || 'Nenhum arquivo selecionado';
        const fileNameElem = document.querySelector('.reject-file-name');
        if (fileNameElem) fileNameElem.textContent = fileName;
    });
};