import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { sendVerificationCode, verifyCode } from "../api/authA";
import axios from "axios";

const EmailValidation = () => {
  const [email, setEmail] = useState("");
  const [isValid, setIsValid] = useState(true);
  const [codeSent, setCodeSent] = useState(false);
  const [userCode, setUserCode] = useState("");
  const [isVerified, setIsVerified] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const username = localStorage.getItem("username");
  console.log("Username from localStorage: ", username);

  const [validationRequest, setValidationRequest] = useState({
    username: username,
    code: "",
  });

  const handleChange = (e) => {
    const { value } = e.target;
    setValidationRequest((prev) => ({
      ...prev,
      code: value,
    }));
  };

  const handleSubmit = async (e) => {
    const API_URL = "http://localhost:8080/auth/verify-account";
    e.preventDefault();

    const response = await axios.post(API_URL, validationRequest, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    if (response.data.status == 200) {
      console.log(response.data);
      localStorage.setItem("authToken", response.data);
      navigate("/home");
    } else {
      console.error("Verification error: ", response.data.message);
    }
  };

  return (
    <div>
      <h2>Email Validation</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      <div>
        <label>Enter Verification Code:</label>
        <input
          type="text"
          name="code"
          value={validationRequest.code}
          onChange={handleChange}
          disabled={loading}
        />
        <button onClick={handleSubmit} disabled={loading}>
          {loading ? "Verifying..." : "Verify Code"}
        </button>
      </div>
      {!isValid && (
        <p style={{ color: "red" }}>Please enter a valid email address.</p>
      )}
      {isVerified && (
        <p style={{ color: "green" }}>Email verified successfully!</p>
      )}
    </div>
  );
};

export default EmailValidation;
