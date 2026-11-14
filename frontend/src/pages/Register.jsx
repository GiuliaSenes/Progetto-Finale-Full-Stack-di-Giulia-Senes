import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import Alert from '../components/Alert'
import { useAuth } from '../context/AuthContext'

export default function Register() {
  const { user, register } = useAuth()
  const navigate = useNavigate()

  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    password: '',
  })
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  if (user) {
    return <Navigate to="/gallery" replace />
  }

  // Aggiorna un solo campo del form senza toccare gli altri
  function handleChange(event) {
    setForm({ ...form, [event.target.name]: event.target.value })
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      await register(form)
      navigate('/gallery', { replace: true })
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main className="container narrow">
      <h1>Crea il tuo account</h1>
      <p className="subtitle">Ti serve per scegliere un flash e inviare la richiesta a Charlotte.</p>

      <form className="card form" onSubmit={handleSubmit}>
        <Alert>{error}</Alert>

        <div className="form-row">
          <label>
            Nome
            <input name="firstName" value={form.firstName} onChange={handleChange} autoComplete="given-name" required />
          </label>
          <label>
            Cognome
            <input name="lastName" value={form.lastName} onChange={handleChange} autoComplete="family-name" required />
          </label>
        </div>

        <label>
          Email
          <input type="email" name="email" value={form.email} onChange={handleChange} autoComplete="email" required />
        </label>

        <label>
          Telefono
          <input type="tel" name="phone" value={form.phone} onChange={handleChange} autoComplete="tel" required />
        </label>

        <label>
          Password
          <input
            type="password"
            name="password"
            value={form.password}
            onChange={handleChange}
            autoComplete="new-password"
            minLength={8}
            required
          />
          <small>Almeno 8 caratteri.</small>
        </label>

        <button className="btn" type="submit" disabled={submitting}>
          {submitting ? 'Registrazione in corso...' : 'Registrati'}
        </button>
      </form>

      <p className="form-footer">
        Hai già un account? <Link to="/login" className="text-link">Accedi</Link>
      </p>
    </main>
  )
}
