import { FaArrowRight, FaEnvelope, FaLock } from 'react-icons/fa';
import './Step1.css';

const Step1 = ({ formData = {}, handleChange, nextStep, errors = {} }) => {
    const handleNextStep = (e) => {
        e.preventDefault();
        nextStep(e);
    };

    return (
      <div>
        <div>
          <FaEnvelope />
          <input
            type="email"
            name="email"
            placeholder="Enter your email"
            value={formData.email || ""}
            onChange={handleChange}
            required
          />
        </div>
        {errors.email && (
          <p className="error" data-testid="email-error">
            {errors.email}
          </p>
        )}
  
        <div>
          <FaLock />
          <input
            type="password"
            name="password"
            placeholder="Enter your password"
            value={formData.password || ""}
            onChange={handleChange}
            required
          />
        </div>
        {errors.password && (
          <p className="error">
            {errors.password}
          </p>
        )}
  
        <button type="button" onClick={handleNextStep}>Next <FaArrowRight /></button>
      </div>
    );
};

export default Step1;
