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
        {errors.email && <p>{errors.email}</p>}
  
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
        {errors.password && <p>{errors.password}</p>}
  
        <button onClick={nextStep}>Next <FaArrowRight /></button>
      </div>
    );
  };
  
  export default Step1;
