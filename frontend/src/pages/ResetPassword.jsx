import { useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { api } from '../api'
import Alert from '../components/Alert'

// Pagina aperta dal link di recupero: /reset-password?token=...
export default function ResetPassword() {
  const [searchParams] = useSearchParams()
  const token = searchParams.get('token')

  const [newPassword, setNewPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [error, setError] = useState('')
  const [done, setDone] = useState(false)
  const [submitting, setSubmitting] = useState(false)

  const passwordsDiffer = confirmPassword !== '' && newPassword !== confirmPassword

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')

    if (newPassword !== confirmPassword) {
      setError('Le due password non coincidono')
      return
    }

    setSubmitting(true)
    try {
      await api.post('/api/auth/reset-password', { token, newPassword })
      setDone(true)
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  if (!token) {
    return (
      <main className="container narrow">
        <h1>Link non valido</h1>
        <p>Questo link non è completo. Richiedine uno nuovo.</p>
        <Link to="/forgot-password" className="btn">Richiedi un nuovo link</Link>
      </main>
    )
  }

  if (done) {
    return (
      <main className="container narrow">
        <h1>Password cambiata</h1>
        <p>Ora puoi accedere con la nuova password.</p>
        <Link to="/login" className="btn">Vai all'accesso</Link>
      </main>
    )
  }

  return (
    <main className="container narrow">
      <h1>Scegli una nuova password</h1>

      <form className="card form" onSubmit={handleSubmit}>
        <Alert>{error}</Alert>

        <label>
          Nuova password
          <input
            type="password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            autoComplete="new-password"
            minLength={8}
            required
          />
          <small>Almeno 8 caratteri.</small>
        </label>

        <label>
          Conferma nuova password
          <input
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            autoComplete="new-password"
            aria-invalid={passwordsDiffer}
            required
          />
          {passwordsDiffer && <small className="field-error">Le due password non coincidono</small>}
        </label>

        <button className="btn" type="submit" disabled={submitting}>
          {submitting ? 'Salvataggio...' : 'Salva la nuova password'}
        </button>
      </form>
    </main>
  )
}
