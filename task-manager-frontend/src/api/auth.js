import axios from "axios";

const API_URL = 'http://localhost:8080/api/auth'

export const loginUser = (email, password) => {
    return axios.post(`${API_URL}/login`, {email, password})
}

export const registerUser = (userData) => {
    return axios.post(`${API_URL}/register`, userData)
}