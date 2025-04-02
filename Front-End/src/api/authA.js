import axios from "axios";

const API_BASE_URL = "http://localhost:5000/api"; // Replace with your backend URL

export const loginUser = async (credentials) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/auth/login`, credentials);
    return response.data;
  } catch (error) {
    console.error("Login failed:", error.response?.data?.message || error.message);
    throw new Error(error.response?.data?.message || "Login failed");
  }
};

export const requestResetCode = async (data) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/auth/forgot-password`, data);
    return response.data;
  } catch (error) {
    console.error("Request reset code failed:", error.response?.data?.message || error.message);
    throw new Error(error.response?.data?.message || "Request reset code failed");
  }
};

export const updatePassword = async (data) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/auth/reset-password`, data);
    return response.data;
  } catch (error) {
    console.error("Update password failed:", error.response?.data?.message || error.message);
    throw new Error(error.response?.data?.message || "Update password failed");
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
    console.error("Failed to create post:", error.response?.data?.message || error.message);
    throw new Error(error.response?.data?.message || "Failed to create post");
  }
};

export const sendVerificationCode = async (email) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/send-verification`, { email });
    return response.data;
  } catch (error) {
    console.error("Failed to send verification code:", error.response?.data?.message || error.message);
    throw new Error(error.response?.data?.message || "Failed to send verification code");
  }
};

export const verifyCode = async (email, code) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/verify-code`, { email, code });
    return response.data;
  } catch (error) {
    console.error("Failed to verify code:", error.response?.data?.message || error.message);
    throw new Error(error.response?.data?.message || "Failed to verify code");
  }
};

export const fetchUserData = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/user/profile`);
    return response.data; // Assuming response.data contains user data
  } catch (error) {
    console.error("Failed to fetch user data:", error.response?.data?.message || error.message);
    throw new Error(error.response?.data?.message || "Failed to fetch user data");
  }
};

export const updateUserData = async (updatedUser) => {
  try {
    const response = await axios.put(`${API_BASE_URL}/user/profile`, updatedUser);
    return response.data; // Assuming response.data contains updated user data
  } catch (error) {
    console.error("Failed to update user data:", error.response?.data?.message || error.message);
    throw new Error(error.response?.data?.message || "Failed to update user data");
  }
};

export const toggleFollow = async (userId, follow) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/follow`, { userId, follow });
    return response.data; // Assuming response.data contains a message
  } catch (error) {
    console.error("Failed to toggle follow status:", error.response?.data?.message || error.message);
    throw new Error(error.response?.data?.message || "Failed to toggle follow status");
  }
};
