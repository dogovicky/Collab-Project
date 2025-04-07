import { useState } from "react";
import Step1 from "./CssSheets/Step1";
import Step2 from "./CssSheets/Step2";
import Step3 from "./CssSheets/Step3";
import Step4 from "./CssSheets/Step4";
import { useSignUp } from "../hooks/useSignUp";
import { useFormValidation } from "../hooks/useFormValidation";
import { signUp } from "../api/auth";
import "./CssSheets/SignUp.css";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const SignUp = () => {
  const API_URL = "http://localhost:8080/auth/signup";
  const navigate = useNavigate();
  const [signUpRequest, setSignUpRequest] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    username: "",
    dateOfBirth: "",
    gender: "",
    institution: "",
    fieldOfInterest: "",
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setSignUpRequest((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    //Convert comma-separated string to array, trim whitespace, filter out empty string
    const interestArray = signUpRequest.fieldOfInterest
      .split(",")
      .map((interest) => interest.trim())
      .filter((interest) => interest.length > 0);

    const payload = {
      ...signUpRequest,
      fieldOfInterest: interestArray,
    };

    try {
      const response = await axios.post(API_URL, payload, {
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (response.data.status == 200) {
        console.log(response.data);
        console.log(response.data.data);
        localStorage.setItem("username", response.data.data);
        navigate("/emailValidation");
      }
    } catch (error) {
      console.error("Sign up error: ", error);
    }
  };

  return (
    <div className="signup-container">
      <div className="form-wrapper">
        <div className="header">
          <h2>
            Welcome to Nexus, Please fill in your correct details to sign up.
          </h2>
        </div>
        <div className="form-container">
          <form action="" className="row g-3" onSubmit={handleSubmit}>
            <div className="row g-3 input-container">
              <div className="col">
                <input
                  type="text"
                  className="form-control"
                  placeholder="First name"
                  aria-label="First name"
                  name="firstName"
                  value={signUpRequest.firstName}
                  onChange={handleInputChange}
                />
              </div>
              <div className="col">
                <input
                  type="text"
                  className="form-control"
                  placeholder="Last name"
                  aria-label="Last name"
                  name="lastName"
                  value={signUpRequest.lastName}
                  onChange={handleInputChange}
                />
              </div>
            </div>
            <div className="input-container">
              <div className="col-md-6">
                <input
                  type="email"
                  className="form-control"
                  id="inputEmail4"
                  placeholder="Email e.g name@example.com"
                  name="email"
                  value={signUpRequest.email}
                  onChange={handleInputChange}
                />
              </div>
              <div className="col-md-6">
                <input
                  type="password"
                  className="form-control"
                  id="inputPassword4"
                  placeholder="Strong password"
                  name="password"
                  value={signUpRequest.password}
                  onChange={handleInputChange}
                />
              </div>
            </div>
            <div className="col-12">
              <input
                type="text"
                className="form-control"
                id="inputAddress"
                placeholder="@username"
                name="username"
                value={signUpRequest.username}
                onChange={handleInputChange}
              />
            </div>
            <div className="details">
              <div className="col-md-6">
                <label for="inputCity" className="form-label">
                  Date of Birth
                </label>
                <input
                  type="date"
                  className="form-control"
                  id="inputCity"
                  name="dateOfBirth"
                  value={signUpRequest.dateOfBirth}
                  onChange={handleInputChange}
                />
              </div>
              <div className="col-md-4">
                <label for="inputState" className="form-label">
                  Gender
                </label>
                <select
                  id="inputState"
                  className="form-select"
                  name="gender"
                  value={signUpRequest.gender}
                  onChange={handleInputChange}
                >
                  <option selected>--Choose Gender--</option>
                  <option>Male</option>
                  <option>Female</option>
                </select>
              </div>
              <div className="col-md-2">
                <label for="inputZip" className="form-label">
                  Institution
                </label>
                <input
                  type="text"
                  className="form-control"
                  id="inputZip"
                  name="institution"
                  value={signUpRequest.institution}
                  onChange={handleInputChange}
                />
              </div>
            </div>
            <div className="col-12">
              <label for="inputAddress2" className="form-label">
                Fields of Interest
              </label>
              <input
                type="text"
                className="form-control"
                id="inputAddress2"
                placeholder="Example; Cyber Security, Medicine, Aviation etc.."
                name="fieldOfInterest"
                value={signUpRequest.fieldOfInterest}
                onChange={handleInputChange}
              />
            </div>
            <div className="col-12">
              <button onClick={handleSubmit} className="btn btn-primary">
                Sign Up
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default SignUp;
