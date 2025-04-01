import axios from "axios";

const API_BASE_URL = "http://localhost:5000/api"; // Replace with your backend URL

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
