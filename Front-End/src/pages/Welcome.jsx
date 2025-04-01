import { useNavigate } from "react-router-dom";
import "./CssSheets/Welcome.css";
import Header from "../components/header";

const Welcome = () => {
  const navigate = useNavigate();

  const handleGetStarted = () => {
    navigate("/signup");
  };

  const handleSignIn = () => {
    navigate("/signin");
  };

  return (
    <>
      <Header />
      <div className="welcome-container">
        <div className="welcome-content">
          <h1>Welcome to Nexus </h1>
          <p>Your ultimate platform for academic, professional growth and connection.</p>
          <button onClick={handleGetStarted}>Get Started</button>
          <div className="signin-link">
            Already have an account?{" "}
            <span onClick={handleSignIn} className="signin-btn">
              Sign in
            </span>
          </div>
        </div>
      </div>
    </>
  );
};

export default Welcome;
