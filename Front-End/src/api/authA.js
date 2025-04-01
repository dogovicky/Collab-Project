import axios from "axios";

const API_BASE_URL = "http://localhost:5000/api"; // Replace with your backend URL

export const loginUser = async (credentials) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/auth/login`, credentials);
    return response.data;
  } catch (error) {
    console.error("Login failed:", error);
    throw error;
  }
};

export const requestResetCode = async (data) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/auth/forgot-password`, data);
    return response.data;
  } catch (error) {
    console.error("Request reset code failed:", error);
    throw error;
  }
};

export const updatePassword = async (data) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/auth/reset-password`, data);
    return response.data;
  } catch (error) {
    console.error("Update password failed:", error);
    throw error;
  }
};

export const createPost = async (formData) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/posts`, formData, {
      headers: {
        Accept: "application/json",
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  } catch (error) {
    console.error("Failed to create post:", error);
    throw error;
  }
};
