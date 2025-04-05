import axios from "axios";

export const signUp = async (formData) => {
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

  const response = await axios.post("https://localhost:8080/auth/signup", formDataToSend, {
    headers: { "Content-Type": "multipart/form-data" },
  });

  return response.data;
};
