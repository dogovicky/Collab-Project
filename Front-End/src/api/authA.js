import axios from 'axios';

const api = axios.create({
  baseURL: "",
  headers: {
    'Content-Type': 'application/json'
  }
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const loginUser = async (credentials) => {
  try {
    const response = await api.post('/auth/login', credentials);
    return response.data;
  } catch (error) {
    const errorMessage = error.response?.data?.message || error.message || "Login failed";
    console.error("Login failed:", errorMessage);
    throw {
      message: errorMessage,
      status: error.response?.status,
      originalError: error
    };
  }
};

export const requestResetCode = async (data) => {
  try {
    const response = await api.post('/auth/forgot-password', data);
    return response.data;
  } catch (error) {
    const errorMessage = error.response?.data?.message || error.message || "Request reset code failed";
    console.error("Request reset code failed:", errorMessage);
    throw {
      message: errorMessage,
      status: error.response?.status,
      originalError: error
    };
  }
};

export const updatePassword = async (data) => {
  try {
    const response = await api.post('/auth/reset-password', data);
    return response.data;
  } catch (error) {
    const errorMessage = error.response?.data?.message || error.message || "Update password failed";
    console.error("Update password failed:", errorMessage);
    throw {
      message: errorMessage,
      status: error.response?.status,
      originalError: error
    };
  }
};

export const createPost = async (formData) => {
  try {
    const response = await api.post('/posts', formData, {
      headers: {
        Accept: "application/json",
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data;
  } catch (error) {
    const errorMessage = error.response?.data?.message || error.message || "Failed to create post";
    console.error("Failed to create post:", errorMessage);
    throw {
      message: errorMessage,
      status: error.response?.status,
      originalError: error
    };
  }
};

export const sendVerificationCode = async (email) => {
  try {
    const response = await api.post('/send-verification', { email });
    return response.data;
  } catch (error) {
    const errorMessage = error.response?.data?.message || error.message || "Failed to send verification code";
    console.error("Failed to send verification code:", errorMessage);
    throw {
      message: errorMessage,
      status: error.response?.status,
      originalError: error
    };
  }
};

export const verifyCode = async (email, code) => {
  try {
    const response = await api.post('/verify-code', { email, code });
    return response.data;
  } catch (error) {
    const errorMessage = error.response?.data?.message || error.message || "Failed to verify code";
    console.error("Failed to verify code:", errorMessage);
    throw {
      message: errorMessage,
      status: error.response?.status,
      originalError: error
    };
  }
};

export const fetchUserData = async () => {
  try {
    const response = await api.get('/user/profile');
    return response.data; // Assuming response.data contains user data
  } catch (error) {
    const errorMessage = error.response?.data?.message || error.message || "Failed to fetch user data";
    console.error("Failed to fetch user data:", errorMessage);
    throw {
      message: errorMessage,
      status: error.response?.status,
      originalError: error
    };
  }
};

export const updateUserData = async (updatedUser) => {
  try {
    const response = await api.put('/user/profile', updatedUser);
    return response.data; // Assuming response.data contains updated user data
  } catch (error) {
    const errorMessage = error.response?.data?.message || error.message || "Failed to update user data";
    console.error("Failed to update user data:", errorMessage);
    throw {
      message: errorMessage,
      status: error.response?.status,
      originalError: error
    };
  }
};

export const toggleFollow = async (userId, follow) => {
  try {
    const response = await api.post('/follow', { userId, follow });
    return response.data; // Assuming response.data contains a message
  } catch (error) {
    const errorMessage = error.response?.data?.message || error.message || "Failed to toggle follow status";
    console.error("Failed to toggle follow status:", errorMessage);
    throw {
      message: errorMessage,
      status: error.response?.status,
      originalError: error
    };
  }
};
