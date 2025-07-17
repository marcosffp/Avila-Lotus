import Cookies from 'js-cookie';
import { jwtDecode } from 'jwt-decode';

export const validateToken = () => {
    const token = Cookies.get('token');
    let isValid = false;
    let decodedToken = null;

    try {
        if (token) {
            decodedToken = jwtDecode(token);
            isValid = decodedToken.exp * 1000 > Date.now();
        }
    } catch (error) {
        console.error('Erro na validação do token:', error);
        Cookies.remove('token');
    }

    return isValid;
};

export const logout = () => {
    Cookies.remove('token');
    window.location.href = '/auth';
};