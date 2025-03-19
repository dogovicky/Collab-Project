import './Step4.css';

const Step4 = ({ formData, handleSubmit, prevStep, isSubmitting }) => {
  return (
    <div className="step-container">
      <p>Confirm your details before submitting:</p>
      <pre>{JSON.stringify(formData, null, 2)}</pre>

      <div className="button-group">
        <button onClick={prevStep}>Back</button>
        <button type="submit" onClick={handleSubmit} disabled={isSubmitting}>
          {isSubmitting ? "Submitting..." : "Submit"}
        </button>
      </div>
    </div>
  );
};

export default Step4;

  