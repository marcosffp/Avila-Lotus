import { validateToken, logout } from './validateToken.js';
import Cookies from 'js-cookie';
import { jwtDecode } from 'jwt-decode';

export const header = () => {
    // Elementos do DOM
    const navLinks = document.querySelectorAll('nav a:not(#auth-link)');
    const authLink = document.querySelector('#auth-link');
    const profileLink = document.getElementById('profile-link');
    const logoutLink = document.getElementById('logout-link');
    const modal = document.getElementById('confirmation-modal');

    // Validação inicial do token
    const isValid = validateToken();

    if (!isValid) {
        handleUnauthenticated();
    } else {
        handleAuthenticated();
    }

    // Configuração do logout
    if (logoutLink) {
        logoutLink.addEventListener('click', (e) => {
            e.preventDefault();
            window.isLogginOut = true; // Variável global para indicar que o logout está em andamento
            showConfirmationModal();
        });
    }

    // Ouvinte de eventos do modal
    window.addEventListener('confirmation-modal:confirmed', () => {
        logout(); // Função do validateToken.js
        handleUnauthenticated();
    });

    window.addEventListener('confirmation-modal:cancelled', () => {
        hideConfirmationModal();
    });

    // Funções auxiliares
    function handleAuthenticated() {
        navLinks.forEach(link => link.classList.remove('hidden'));
        if(jwtDecode(Cookies.get('token')).tipo === 'PROFISSIONAL') {
            profileLink?.classList.add('hidden');
        }
        authLink.classList.add('hidden');
    }

    function handleUnauthenticated() {
        navLinks.forEach(link => link.classList.add('hidden'));
        authLink.classList.remove('hidden');
        profileLink?.classList.add('hidden');
    }

    function showConfirmationModal() {
        modal.classList.replace('hidden', 'flex');
    }

    function hideConfirmationModal() {
        modal.classList.replace('flex', 'hidden');
        navMenu.classList.remove('active');
    }

    const menuToggle = document.getElementById('menu-toggle');
    const navMenu = document.getElementById('nav-menu');

    menuToggle.addEventListener('click', () => {
        navMenu.classList.toggle('active');
    });
};