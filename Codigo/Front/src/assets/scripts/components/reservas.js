import { jwtDecode } from "jwt-decode";
import Cookies from 'js-cookie';
import { logout, validateToken } from "./validateToken";

const token = Cookies.get("token");
if (!validateToken(token)) logout();
const apiUrl = import.meta.env.PUBLIC_API_URL;
const decodedToken = jwtDecode(token);


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

const cancelarAgendamento = async (id, token, apiUrl, endpoint, comprovanteFile = null, motivoCancelamento = null) => {
  const formData = new FormData();
  //const idKey = endpoint.includes("cancelar") ? "agendamentoId" : "idAgendamento";
  formData.append("idAgendamento", id);

  if (comprovanteFile) {
    formData.append("comprovante", comprovanteFile);
  }

  if (endpoint === "cancelar" && motivoCancelamento) {
    formData.append("motivoCancelamento", motivoCancelamento);
  }

  let body = formData;

  try {
    // Monta os headers condicionalmente
    const headers = {
      Authorization: `Bearer ${token}`
      // * NÃO colocar Content-Type, o browser define automaticamente para multipart/form-data com boundary
    };
    if (endpoint === "cancelar-cliente") {
      headers['Content-Type'] = 'application/json';
      body = JSON.stringify({ idAgendamento: id });
    }

    const res = await globalThis.fetchWithLoader(`${apiUrl}/agendamentos/${endpoint}`, {
      method: "PUT",
      headers,
      body
    });

    return res;
  } catch (error) {
    console.error(error);
    return false;
  }
};

export async function handleConfirmarAgendamento(id) {
  const modal = document.getElementById('appointment-details-modal');

  try {
    const res = await globalThis.fetchWithLoader(`${apiUrl}/agendamentos/aprovar`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ idAgendamento: +id })
    });

    if (res.ok) {
      alert("Agendamento confirmado com sucesso!");
      modal.classList.remove('show');
      carregarAgendamentos(token, apiUrl);
    } else {
      const errorData = await res.json();
      alert(`Erro ao confirmar agendamento: ${errorData.message || 'Erro desconhecido'}`);
    }
  } catch (error) {
    console.error("Erro ao confirmar agendamento:", error);
    alert("Erro ao confirmar agendamento. Tente novamente.");
  }
}

export function handleCancelarAgendamentoModal(agendamentoId, endpoint) {
  const modal = document.getElementById('appointment-details-modal');
  const confirmacao = confirm("Tem certeza que deseja " + (endpoint == 'cancelar-cliente' ? 'cancelar' : endpoint) + " este agendamento?");
  if (!confirmacao) return;

  const comprovanteInput = document.getElementById("reject-dropzone-comprovante");
  const comprovante = comprovanteInput?.files?.[0] || null;
  const motivoRecusa = document.getElementById("motivo-recusa").value || null;

  let action = endpoint.includes('cancelar') ? "cancelado" : endpoint === "recusar" ? "recusado" : "reembolsado";

  cancelarAgendamento(agendamentoId, token, apiUrl, endpoint, comprovante, motivoRecusa)
    .then(async response => {
      if (response.ok) {
        alert("Agendamento " + action + " com sucesso.");
        modal.classList.remove('show');
        carregarAgendamentos(token, apiUrl);
      } else {
        action = endpoint === "cancelar-cliente" ? "cancelar" : endpoint;
        const data = await response.json();
        alert("Erro ao " + action + " o agendamento.\n" + Object.values(data).join("\n"));
      }
    }
  );
}

export async function handleConcluiAgendamento(idAgendamento, endpoint) {

  const modal = document.getElementById('appointment-details-modal');

  try {
    const res = await globalThis.fetchWithLoader(`${apiUrl}/agendamentos/${endpoint}`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ idAgendamento })
    });

    if (res.ok) {
      alert("Agendamento concluído com sucesso!");
      modal.classList.remove('show');
      carregarAgendamentos(token, apiUrl);
    } else {
      const errorData = await res.json();
      alert(`Erro ao concluir agendamento: ${errorData.message || 'Erro desconhecido'}`);
    }
  } catch (error) {
    console.error("Erro ao concluir agendamento:", error);
    alert("Erro ao concluir agendamento. Tente novamente.");
  }
}

function criarModalDetalhes(agendamento) {
  globalThis.atualizarModalDetalhes(agendamento);
}

export async function carregarAgendamentos(token, apiUrl) {
  const container = document.getElementById("horarios-agendados-container");
  container.innerHTML = `
    <div class="loading-spinner">
      <i class="bi bi-arrow-repeat spin"></i> Carregando agendamentos...
    </div>
  `;

  try {
    const res = await globalThis.fetchWithLoader(`${apiUrl}/agendamentos/${decodedToken.tipo.toLowerCase()}`, {
      headers: {
        Authorization: `Bearer ${token}`
      },
      cache: "force-cache"
    });

    if (!res.ok) throw new Error("Erro ao buscar agendamentos.");

    const agendamentos = await res.json();

    console.log(agendamentos);
    
    if (agendamentos.length === 0) {
      container.innerHTML = "<p>Não há agendamentos disponíveis.</p>";
      return;
    }

    agendamentos.sort((a, b) => new Date(`${a.data}T${a.hora}`) - new Date(`${b.data}T${b.hora}`));
    container.innerHTML = "";

    agendamentos.forEach(agendamento => {
      const div = document.createElement("div");
      div.className = "agendamento-card";
      div.dataset.id = agendamento.id;
      div.dataset.observacoes = agendamento.observacoes;
      div.dataset.clienteEmail = agendamento.clienteEmail;

      const [year, month, day] = agendamento.data.split('-').map(Number);
      const dataObj = new Date(year, month - 1, day);  // mês começa do 0

      const diaSemana = dataObj.toLocaleDateString("pt-BR", { weekday: "long" });
      const capitalizado = diaSemana.charAt(0).toUpperCase() + diaSemana.slice(1);

      const data = dataObj
        .toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' })
        .replace('.', '').replace(' de ', ' ');

      const [dia, mes] = data.split(' ');
      const dataFormatada = `${dia} ${mes.charAt(0).toUpperCase()}${mes.slice(1)}`;

      const horaInicio = formatarHorario(agendamento.hora);
      const horaFim = calcularHoraFim(horaInicio);
      const status = agendamento.status.replace(/_/g, ' ');
      const subStatus = agendamento.subStatus !== 'SEM SUBSTATUS' ? agendamento.subStatus : '';

      div.classList.add(agendamento.status.toLowerCase());
      div.innerHTML = `
        <div class="card-header">
          ${(decodedToken.tipo.toLowerCase() === "profissional") ? `<a class="title" href="/profile?email=${agendamento.clienteEmail}">${agendamento.clienteNome || 'Nome do Cliente'}</a>` : ''}
          <p><i class="bi bi-circle-fill status"></i> <span class="text-white font-bold">${status}</span></p>
          <p class="text-sm italic font-medium"><span class="text-white">${subStatus}</span></p>
        </div>
        <div class="horario">
          <div><i class="bi bi-calendar"></i> ${capitalizado}, ${dataFormatada}</div>
          <div><i class="bi bi-clock"></i> ${horaInicio} - ${horaFim}</div>
        </div>
      `;

      const cardFooter = document.createElement("div");
      cardFooter.className = "card-footer";

      const detalhesBtn = document.createElement("button");
      detalhesBtn.className = "detalhes-btn";
      detalhesBtn.textContent = "Ver Detalhes";
      detalhesBtn.onclick = () => criarModalDetalhes(agendamento);

      const cancelarBtn = document.createElement("button");
      cancelarBtn.className = "cancelar-btn";
      cancelarBtn.textContent = "Cancelar";
      cancelarBtn.onclick = () => handleCancelarAgendamentoModal(agendamento.id, "cancelar-cliente");

      const agora = new Date();
      const dataAtendimento = new Date(`${agendamento.data}T${agendamento.hora}`);
      const diffHoras = (dataAtendimento - agora) / (1000 * 60 * 60);

      if (
        decodedToken.tipo.toLowerCase() === "cliente" &&
        (
          agendamento.status === "PENDENTE" ||
          (agendamento.status === "APROVADO" && diffHoras >= 48)
        )
      ) {
        cardFooter.appendChild(cancelarBtn);
      } else if (decodedToken.tipo.toLowerCase() === "profissional") {
        cardFooter.appendChild(detalhesBtn);
      }

      div.appendChild(cardFooter);
      container.appendChild(div);
    });

  } catch (error) {
    console.error(error);
    container.innerHTML = "<p>Erro ao carregar agendamentos.</p>";
  }
}

export async function carregarChavePix(email) {
  const pixKeyElement = document.getElementById('pix-cliente-key');
  try {
    const response = await globalThis.fetchWithLoader(`${apiUrl}/cliente/pix?email=${email}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    const data = await response.json();
    const chavePix = data.PIX;

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