import { useNavigate } from 'react-router-dom';
import { useAuth } from './useAuth';

export const useRoutes = () => {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const protectedRoute = (path) => {
    if (!isAuthenticated) {
      navigate('/EmailValidation');
      return false;
    }
    return true;
  };

  return { protectedRoute };
};
