import { FaArrowRight, FaEnvelope, FaLock } from 'react-icons/fa';
import './Step1.css';

const Step1 = ({ formData = {}, handleChange, nextStep, errors = {} }) => {
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
          <p className="error">
            {errors.email} (e.g., example@domain.com)
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
            {errors.password} (Password must be at least 8 characters long, include uppercase, lowercase, a number, and a special character.)
          </p>
        )}
  
        <button onClick={nextStep}>Next <FaArrowRight /></button>
      </div>
    );
  };
  
  export default Step1;
