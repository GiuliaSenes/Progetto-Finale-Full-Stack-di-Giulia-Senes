import { useEffect, useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { api } from '../api'
import Alert from '../components/Alert'
import StatusBadge from '../components/StatusBadge'
import {
  BOOKING_STATUS,
  CONSULTATION_STATUS_CLIENT,
  formatBookingWhen,
  formatDateTime,
  formatTime,
} from '../utils/format'

export default function MyBookings() {
  const location = useLocation()
  const [bookings, setBookings] = useState([])
  const [consultations, setConsultations] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(location.state?.success || '')

  function loadData() {
    return Promise.all([api.get('/api/bookings/mine'), api.get('/api/consultations/mine')])
      .then(([bookingsData, consultationsData]) => {
        setBookings(bookingsData)
        setConsultations(consultationsData)
      })
      .catch((err) => setError(err.message))
  }

  useEffect(() => {
    loadData().finally(() => setLoading(false))
  }, [])

  async function handleCancel(booking) {
    if (!window.confirm('Vuoi davvero annullare questa richiesta?')) {
      return
    }
    setError('')
    setSuccess('')
    try {
      await api.put(`/api/bookings/${booking.id}/cancel`)
      setSuccess('Richiesta annullata.')
      await loadData()
    } catch (err) {
      setError(err.message)
    }
  }

  const canCancel = (booking) => booking.status === 'PENDING' || booking.status === 'ACCEPTED'

  return (
    <main className="container narrow-wide">
      <h1>Le mie richieste</h1>

      <Alert type="success">{success}</Alert>
      <Alert>{error}</Alert>
      {loading && <p className="page-message">Caricamento...</p>}

      {!loading && (
        <>
          <h2>Prenotazioni flash</h2>
          {bookings.length === 0 && (
            <p className="muted">
              Non hai ancora inviato richieste. <Link to="/gallery" className="text-link">Sfoglia i flash</Link>
            </p>
          )}

          <div className="list">
            {bookings.map((booking) => (
              <article key={booking.id} className="card list-item">
                <img src={booking.flash.imageUrl} alt="Flash scelto" className="thumb" />
                <div className="list-item-body">
                  <div className="list-item-head">
                    <strong>Flash da {booking.flash.size}</strong>
                    <StatusBadge labels={BOOKING_STATUS} value={booking.status} />
                  </div>
                  <p>{formatBookingWhen(booking)}</p>
                  {booking.status === 'PENDING' && (
                    <p className="muted small">Orario da definire: te lo confermerà Charlotte.</p>
                  )}
                  {booking.note && <p className="muted">Il tuo messaggio: {booking.note}</p>}
                  <p className="muted small">Inviata il {formatDateTime(booking.createdAt)}</p>

                  {booking.status === 'REJECTED' && (
                    <p className="rejection">
                      <strong>Motivo del rifiuto:</strong> {booking.rejectionReason}
                    </p>
                  )}
                  {booking.status === 'ACCEPTED' && (
                    <p className="accepted-note">
                      Richiesta accettata! Charlotte ti aspetta alle {formatTime(booking.confirmedTime)}. Se non puoi
                      venire, annulla la richiesta.
                    </p>
                  )}

                  {canCancel(booking) && (
                    <button className="btn btn-small btn-outline" onClick={() => handleCancel(booking)}>
                      Annulla richiesta
                    </button>
                  )}
                </div>
              </article>
            ))}
          </div>

          <h2>Consulenze</h2>
          {consultations.length === 0 && (
            <p className="muted">
              Nessuna consulenza richiesta. <Link to="/consultation" className="text-link">Richiedine una</Link>
            </p>
          )}
          <div className="list">
            {consultations.map((c) => (
              <article key={c.id} className="card list-item">
                <div className="list-item-body">
                  <div className="list-item-head">
                    <strong>Richiesta di consulenza inviata</strong>
                    <StatusBadge labels={CONSULTATION_STATUS_CLIENT} value={c.status} />
                  </div>
                  <p className="muted small">Il {formatDateTime(c.createdAt)}</p>
                  <p className="field-label">Il tuo messaggio</p>
                  <p className="pre-line">{c.message}</p>
                  <p className="muted small">
                    {c.status === 'HANDLED'
                      ? 'Charlotte ha preso in carico la richiesta e ti contatterà.'
                      : 'Charlotte la leggerà e ti contatterà via email o telefono.'}
                  </p>
                </div>
              </article>
            ))}
          </div>
        </>
      )}
    </main>
  )
}
