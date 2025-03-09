import React from 'react';
import EValidation from '../components/EValidation';

const EmailValidationPage = ({ email }) => {
  return (
    <div>
      <EValidation email={email} />
    </div>
  );
};

export default EmailValidationPage;
