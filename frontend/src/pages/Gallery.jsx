import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api'
import Alert from '../components/Alert'
import StatusBadge from '../components/StatusBadge'
import { FLASH_STATUS } from '../utils/format'

export default function Gallery() {
  const [flashes, setFlashes] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [onlyAvailable, setOnlyAvailable] = useState(false)

  useEffect(() => {
    api
      .get('/api/flashes')
      .then(setFlashes)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  const visibleFlashes = onlyAvailable ? flashes.filter((f) => f.status === 'AVAILABLE') : flashes

  return (
    <main className="container">
      <h1>I flash di Charlotte</h1>
      <p className="subtitle">
        Disegni pronti da tatuare, ognuno una sola volta. Scegli quello che ti piace e prenota il tuo orario.
      </p>

      <label className="checkbox-label">
        <input type="checkbox" checked={onlyAvailable} onChange={(e) => setOnlyAvailable(e.target.checked)} />
        Mostra solo i flash disponibili
      </label>

      <Alert>{error}</Alert>
      {loading && <p className="page-message">Caricamento...</p>}
      {!loading && !error && visibleFlashes.length === 0 && (
        <p className="page-message">Al momento non ci sono flash da mostrare.</p>
      )}

      <div className="flash-grid">
        {visibleFlashes.map((flash) => (
          <article key={flash.id} className={`flash-card ${flash.status !== 'AVAILABLE' ? 'is-unavailable' : ''}`}>
            <div className="flash-image">
              <img src={flash.imageUrl} alt={`Flash di ${flash.size}`} loading="lazy" />
            </div>
            <div className="flash-info">
              <p className="flash-size">Dimensione: {flash.size}</p>
              <StatusBadge labels={FLASH_STATUS} value={flash.status} />
              {flash.status === 'AVAILABLE' ? (
                <Link to={`/booking/${flash.id}`} className="btn btn-small">
                  Scegli questo flash
                </Link>
              ) : (
                <p className="flash-note">
                  {flash.status === 'PENDING'
                    ? 'Qualcuno lo ha già richiesto: se Charlotte rifiuta tornerà disponibile.'
                    : 'Questo flash non è più disponibile.'}
                </p>
              )}
            </div>
          </article>
        ))}
      </div>
    </main>
  )
}
