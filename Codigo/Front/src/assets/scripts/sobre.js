import Cookies from 'js-cookie';
import { jwtDecode } from 'jwt-decode';
import { logout, validateToken } from "./components/validateToken";

export const sobre = () => {
    const apiUrl = import.meta.env.PUBLIC_API_URL;
    const urlParams = new URLSearchParams(window.location.search);
    const isFirstLogin = urlParams.get('firstLogin') === 'true';

    const token = Cookies.get("token");
    let decodedToken = null;

    try {
        if (token && validateToken()) {
            decodedToken = jwtDecode(token);
        }
    } catch {
        logout(); // Token inválido
    }

    const isProfissional = decodedToken?.tipo === "PROFISSIONAL";

    function getAboutInfo() {
        globalThis.fetchWithLoader(`${apiUrl}/prof/sobre-mim`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
            cache: 'force-cache'
        })
            .then(response => response.json())
            .then(data => {
                if (data.sobreMim != null)
                    document.getElementById('about-me').innerHTML = data.sobreMim;
            })
            .catch(err => console.error(err));
    }

    function updateAboutInfo(content) {
        globalThis.fetchWithLoader(`${apiUrl}/prof/editar-sobre-mim`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({ sobreMim: content })
        })
            .then(() => getAboutInfo())
            .catch(err => {
                console.error(err);
                alert("Erro ao salvar informações. Tente novamente mais tarde.");
            });
    }

    function getNomePix() {
        globalThis.fetchWithLoader(`${apiUrl}/prof/pix-e-nome`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
            cache: 'force-cache'
        })
            .then(response => response.json())
            .then(data => {
                const nomeInput = document.getElementById("nome-input");
                const pixInput = document.getElementById("pix-input");

                if (data.nome != null) nomeInput.value = data.nome;
                if (data.pix != null) pixInput.value = data.pix;

                nomeInput.disabled = true;
                pixInput.disabled = true;
            })
            .catch(err => console.error(err));
    }

    function updateNome(nome) {
        if (nome === "") {
            alert("O campo nome não pode estar vazio.");
            return;
        }
        globalThis.fetchWithLoader(`${apiUrl}/prof/editar-nome`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({ nome })
        })
            .then(() => getNomePix())
            .catch(err => {
                console.error(err);
                alert("Erro ao salvar informações. Tente novamente mais tarde.");
            });
    }

    function updatePix(pix) {
        if (pix === "") {
            alert("O campo PIX não pode estar vazio.");
            return;
        }
        globalThis.fetchWithLoader(`${apiUrl}/prof/editar-pix`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({ pix })
        })
            .then(() => getNomePix())
            .catch(err => {
                console.error(err);
                alert("Erro ao salvar informações. Tente novamente mais tarde.");
            });
    }

    function getServices() {
        globalThis.fetchWithLoader(`${apiUrl}/prof/servicos`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' },
            cache: 'force-cache'
        })
            .then(response => response.json())
            .then(data => {
                const servicesContainer = document.getElementById('services-valeria');
                servicesContainer.innerHTML = '';

                if (data.servicosPrestados?.length > 0) {
                    data.servicosPrestados.forEach((service, index) => {
                        servicesContainer.appendChild(createServiceCard(service, index));
                    });
                } else {
                    servicesContainer.innerHTML = "<p>Nada aqui ainda.</p>";
                }
            })
            .catch(err => console.error(err));
    }

    function updateServices(services) {
        globalThis.fetchWithLoader(`${apiUrl}/prof/editar-servicos`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({ servicosPrestados: services })
        })
            .then(() => getServices())
            .catch(err => {
                console.error(err);
                alert("Erro ao salvar serviços. Tente novamente mais tarde.");
            });
    }

    function createServiceCard(service, index) {
        const serviceCard = document.createElement('div');
        serviceCard.classList.add('service-card');

        const serviceText = document.createElement('p');
        serviceText.innerText = service;
        serviceText.classList.add('service-text');

        const deleteButton = document.createElement('button');
        deleteButton.innerText = '✖';
        deleteButton.classList.add('delete-service-btn');
        deleteButton.onclick = () => removeService(index);
        deleteButton.style.display = isProfissional ? 'none' : 'none';

        serviceCard.appendChild(serviceText);
        serviceCard.appendChild(deleteButton);
        return serviceCard;
    }

    function removeService(index) {
        const servicesContainer = document.querySelectorAll('.service-text');
        let services = Array.from(servicesContainer).map(service => service.innerText);
        services.splice(index, 1);
        updateServices(services);
    }

    if (isFirstLogin)
        document.getElementById('information-modal').classList.replace('hidden', 'flex');

    getNomePix();
    getAboutInfo();
    getServices();

    const editAboutButton = document.getElementById('edit-about-btn');
    const aboutValeria = document.getElementById('about-me');

    if (!isProfissional) {
        editAboutButton.style.display = "none";
        document.getElementById('edit-services-btn').style.display = "none";
        document.getElementById('add-service-btn').style.display = "none";
    } else {
        document.getElementById('reserve-btn').style.display = "none";
    }

    editAboutButton.addEventListener('click', () => {
        if (editAboutButton.innerText === 'Editar') {
            document.getElementById("nome-input").disabled = false;
            document.getElementById("pix-input").disabled = false;
            aboutValeria.setAttribute('contenteditable', 'true');
            aboutValeria.style.borderBottom = '1px solid var(--color-accent)';
            aboutValeria.focus();
            editAboutButton.innerText = 'Salvar';
        } else {
            let updatedContent = aboutValeria.innerHTML.trim();
            if (updatedContent === "") updatedContent = "Nada aqui ainda.";
            const nome = document.getElementById("nome-input").value.trim();
            const pix = document.getElementById("pix-input").value.trim();
            updateAboutInfo(updatedContent);
            updateNome(nome);
            updatePix(pix);
            document.getElementById("nome-input").disabled = true;
            document.getElementById("pix-input").disabled = true;
            aboutValeria.setAttribute('contenteditable', 'false');
            aboutValeria.style.borderBottom = 'none';
            editAboutButton.innerText = 'Editar';
        }
    });

    const editServicesButton = document.getElementById('edit-services-btn');
    const addServiceButton = document.getElementById('add-service-btn');

    editServicesButton.addEventListener('click', () => {
        const isEditing = editServicesButton.innerText === 'Salvar';
        editServicesButton.innerText = isEditing ? 'Editar' : 'Salvar';
        addServiceButton.style.display = isEditing ? 'none' : 'block';

        const servicesContainer = document.getElementById('services-valeria');
        const placeholder = servicesContainer.querySelector('p');
        if (placeholder && placeholder.innerText === 'Nada aqui ainda.') {
            placeholder.remove();
        }

        const deleteButtons = document.querySelectorAll('.delete-service-btn');
        deleteButtons.forEach(button => {
            button.style.display = isEditing ? 'none' : 'inline-block';
        });

        if (isEditing) {
            const services = Array.from(document.querySelectorAll('.service-text'))
                .map(service => service.innerText.trim())
                .filter(service => service !== '');

            if (services.length > 0) {
                updateServices(services);
            } else {
                alert("Adicione ao menos um serviço para salvar.");
            }
        }
    });

    addServiceButton.addEventListener('click', () => {
        const servicesContainer = document.getElementById('services-valeria');

        const serviceCard = document.createElement('div');
        serviceCard.classList.add('service-card');

        const serviceInput = document.createElement('input');
        serviceInput.type = 'text';
        serviceInput.placeholder = 'Novo serviço';
        serviceInput.classList.add('service-input');

        const deleteButton = document.createElement('button');
        deleteButton.innerText = '✖';
        deleteButton.classList.add('delete-service-btn');
        deleteButton.onclick = () => serviceCard.remove();

        serviceCard.appendChild(serviceInput);
        serviceCard.appendChild(deleteButton);
        servicesContainer.appendChild(serviceCard);

        serviceInput.focus();

        serviceInput.addEventListener('blur', () => {
            const value = serviceInput.value.trim();
            if (value !== "") {
                const serviceCardText = document.createElement('p');
                serviceCardText.classList.add('service-text');
                serviceCardText.innerText = value;
                serviceCard.replaceChild(serviceCardText, serviceInput);
                deleteButton.style.display = 'inline-block';
            } else {
                serviceCard.remove();
            }
        });
    });
}