import './Step4.css';

const Step4 = ({ formData, handleSubmit, prevStep, isSubmitting, errors }) => {
  const handleFormSubmit = async (e) => {
    e.preventDefault();
    console.log('Submitting form data:', formData); // Debug log
    try {
      await handleSubmit(e);
    } catch (error) {
      console.error('Submission error:', error); // Debug log
    }
  };

  return (
    <div className="step-container">
      <p>Confirm your details before submitting:</p>
      <div className="form-summary">
        {Object.entries(formData).map(([key, value]) => (
          <div key={key} className="summary-item">
            <strong>{key}:</strong>{' '}
            {key === 'profilePicture' 
              ? (value ? value.name : 'No file selected')
              : Array.isArray(value)
                ? value.join(', ')
                : value.toString()}
          </div>
        ))}
      </div>

      {/* Display any validation errors */}
      {Object.keys(errors).length > 0 && (
        <div className="error-summary">
          <p>Please correct the following errors:</p>
          {Object.entries(errors).map(([key, error]) => (
            <p key={key} className="error">{error}</p>
          ))}
        </div>
      )}

      <div className="button-group">
        <button type="button" onClick={prevStep} disabled={isSubmitting}>
          Back
        </button>
        <button
          type="submit"
          onClick={handleFormSubmit}
          disabled={isSubmitting || Object.keys(errors).length > 0}
        >
          {isSubmitting ? (
            <div className="submit-loading">
              <span className="loading-spinner"></span>
              Submitting...
            </div>
          ) : (
            "Submit"
          )}
        </button>
      </div>

      {isSubmitting && (
        <div className="submission-status">
          Processing your registration...
        </div>
      )}
    </div>
  );
};

export default Step4;

