import { useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api'
import Alert from '../components/Alert'
import { useAuth } from '../context/AuthContext'

export default function Consultation() {
  const { user } = useAuth()
  const [message, setMessage] = useState('')
  const [image, setImage] = useState(null)
  const [error, setError] = useState('')
  const [sent, setSent] = useState(false)
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(event) {
    event.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      // Il form contiene un file, quindi usiamo FormData invece del JSON
      const formData = new FormData()
      formData.append('message', message)
      if (image) {
        formData.append('image', image)
      }
      await api.upload('/api/consultations', formData)
      setSent(true)
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  if (sent) {
    return (
      <main className="container narrow">
        <h1>Richiesta inviata</h1>
        <p>
          Grazie! Charlotte leggerà la tua idea e ti contatterà per accordarsi sui dettagli.
        </p>
        <Link to="/my-bookings" className="btn">Vai alle mie richieste</Link>
      </main>
    )
  }

  return (
    <main className="container narrow">
      <h1>Richiedi una consulenza</h1>
      <p className="subtitle">
        Hai un'idea che non trovi tra i flash? Raccontamela: ti contatterò per accordarci.
      </p>

      <form className="card form" onSubmit={handleSubmit}>
        <Alert>{error}</Alert>

        <p className="muted small">
          Charlotte ti risponderà a <strong>{user.email}</strong> o al <strong>{user.phone}</strong>. Puoi
          cambiare i tuoi contatti dal <Link to="/profile" className="text-link">profilo</Link>.
        </p>

        <label>
          Descrivi la tua idea
          <textarea
            rows={6}
            maxLength={1500}
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            placeholder="Soggetto, dimensioni, zona del corpo, significato..."
            required
          />
          <small>{message.length}/1500 caratteri</small>
        </label>

        <label>
          Immagine di riferimento (facoltativa)
          <input
            type="file"
            accept="image/jpeg,image/png,image/webp"
            onChange={(e) => setImage(e.target.files[0] || null)}
          />
          <small>JPG, PNG o WEBP, massimo 5 MB.</small>
        </label>

        <button className="btn" type="submit" disabled={submitting}>
          {submitting ? 'Invio in corso...' : 'Invia la richiesta'}
        </button>
      </form>
    </main>
  )
}
