import { useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api'
import Alert from '../components/Alert'

export default function ForgotPassword() {
  const [email, setEmail] = useState('')
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setMessage('')
    setSubmitting(true)
    try {
      const data = await api.post('/api/auth/forgot-password', { email })
      setMessage(data.message)
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <main className="container narrow">
      <h1>Password dimenticata</h1>
      <p className="subtitle">
        Scrivi l'email con cui ti sei registrato: ti mandiamo un link per scegliere una nuova password.
        L'email è anche il tuo nome utente, quindi se ricordi quella puoi sempre accedere.
      </p>

      <form className="card form" onSubmit={handleSubmit}>
        <Alert>{error}</Alert>
        <Alert type="success">{message}</Alert>

        <label>
          Email
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} autoComplete="email" required />
        </label>

        <button className="btn" type="submit" disabled={submitting}>
          {submitting ? 'Invio in corso...' : 'Invia il link'}
        </button>
      </form>

      <p className="form-footer">
        <Link to="/login" className="text-link">Torna all'accesso</Link>
      </p>
    </main>
  )
}
