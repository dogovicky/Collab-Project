import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import Signup from './pages/signup'
import Signin from './pages/signin'
import EmailValidationPage from './pages/EmailValidationPage'
import ForgotPassword from './pages/ForgotPassword'

function App() {
  return(
    <Router>
    <div className='App'>
      <Routes>
        <Route path='/signup' element={<Signup/>} />
        <Route path='/signin' element={<Signin/>} />
        <Route path='/email-validation' element={<EmailValidationPage/>} />
        <Route path='/forgot-password' element={<ForgotPassword/>} />
      </Routes>
      
    </div>
  </Router>
  );
}

export default App
