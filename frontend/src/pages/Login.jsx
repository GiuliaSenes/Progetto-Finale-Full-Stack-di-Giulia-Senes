import { useState } from 'react'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import Alert from '../components/Alert'
import { useAuth } from '../context/AuthContext'

export default function Login() {
  const { user, login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  // Se l'utente è già loggato non ha senso mostrare il login
  if (user) {
    return <Navigate to={user.role === 'ADMIN' ? '/admin' : '/gallery'} replace />
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      const loggedUser = await login(email, password)
      // Se arrivavamo da una pagina protetta, ci torniamo; altrimenti home dell'area giusta
      const destination = location.state?.from || (loggedUser.role === 'ADMIN' ? '/admin' : '/gallery')
      navigate(destination, { replace: true })
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main className="container narrow">
      <h1>Accedi</h1>
      <p className="subtitle">Entra per vedere i flash e prenotare il tuo tatuaggio.</p>

      <form className="card form" onSubmit={handleSubmit}>
        <Alert>{error}</Alert>

        <label>
          Email
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            autoComplete="email"
            required
          />
        </label>

        <label>
          Password
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
            required
          />
        </label>

        <button className="btn" type="submit" disabled={submitting}>
          {submitting ? 'Accesso in corso...' : 'Accedi'}
        </button>

        <p className="form-footer">
          <Link to="/forgot-password" className="text-link">Hai dimenticato la password?</Link>
        </p>
      </form>

      <p className="form-footer">
        Non hai un account? <Link to="/register" className="text-link">Registrati</Link>
      </p>
    </main>
  )
}
