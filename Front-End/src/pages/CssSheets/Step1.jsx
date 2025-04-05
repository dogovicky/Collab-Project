import { FaArrowRight, FaEnvelope, FaLock } from 'react-icons/fa';
import './Step1.css';

const Step1 = ({ formData = {}, handleChange, nextStep, errors = {}, isCheckingEmail }) => {
    const handleNextStep = (e) => {
        e.preventDefault();
        nextStep(e);
    };

    return (
      <div className="step-container">
        <div className="input-group">
          <FaEnvelope className="input-icon" />
          <input
            className="form-input"
            type="email"
            name="email"
            placeholder="Enter your email"
            value={formData.email || ""}
            onChange={handleChange}
            data-testid="email-input"
            required
          />
          {isCheckingEmail && (
            <span className="checking-email" data-testid="checking-email">
              Checking email...
            </span>
          )}
        </div>
        {errors.email && (
          <p className="error-message" data-testid="email-error">
            {errors.email}
          </p>
        )}
  
        <div className="input-group">
          <FaLock className="input-icon" />
          <input
            className="form-input"
            type="password"
            name="password"
            placeholder="Enter your password"
            value={formData.password || ""}
            onChange={handleChange}
            data-testid="password-input"
            required
          />
        </div>
        {errors.password && (
          <p className="error-message" data-testid="password-error">
            {errors.password}
          </p>
        )}
  
        <button 
          className="next-button"
          type="button" 
          onClick={handleNextStep}
          data-testid="next-button"
          disabled={isCheckingEmail || Object.keys(errors).length > 0}
        >
          Next <FaArrowRight className="button-icon" />
        </button>
      </div>
    );
};

export default Step1;
