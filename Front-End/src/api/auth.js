import axios from "axios";

export const signUp = async (formData) => {
  try {
    const formDataToSend = new FormData();

    Object.entries(formData).forEach(([key, value]) => {
      if (Array.isArray(value)) {
        value.forEach((v) => formDataToSend.append(key, v));
      } else if (value instanceof File) {
        formDataToSend.append(key, value);
      } else {
        formDataToSend.append(key, value);
      }
    });

    const response = await axios.post("http://localhost:8080/auth/signup", formDataToSend, {
      headers: { "Content-Type": "multipart/form-data" },
      validateStatus: status => status < 500
    });

    if (response.status !== 200) {
      throw new Error(response.data.message || 'Registration failed');
    }

    return response;
  } catch (error) {
    if (error.response) {
      throw new Error(error.response.data.message || 'Server error occurred');
    }
    throw new Error('Network error occurred');
  }
};
