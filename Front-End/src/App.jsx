import { useState } from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import react from 'react'
import { useNavigate } from 'react-router-dom'
//import axios from 'axios'
//import toast from 'react-hot-toast'
import Signup from './pages/signup'
import Signin from './pages/signin'

function App() {
  return(
    <Router>
    <div className='App'>
      <Routes>
        <Route path='/signup' element={<Signup/>} />
        <Route path='/signin' element={<Signin/>} />
      </Routes>
      
    </div>
  </Router>
  );
}

export default App
