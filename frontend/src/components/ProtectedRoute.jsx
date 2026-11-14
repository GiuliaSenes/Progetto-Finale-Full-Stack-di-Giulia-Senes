import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

// Mostra la pagina solo se l'utente è loggato. Con adminOnly serve anche il ruolo ADMIN.
export default function ProtectedRoute({ children, adminOnly = false }) {
  const { user, loading } = useAuth()
  const location = useLocation()

  if (loading) {
    return <p className="page-message">Caricamento...</p>
  }
  if (!user) {
    // Ricordiamo la pagina richiesta per tornarci dopo il login
    return <Navigate to="/login" state={{ from: location.pathname }} replace />
  }
  if (adminOnly && user.role !== 'ADMIN') {
    return <Navigate to="/" replace />
  }
  return children
}
