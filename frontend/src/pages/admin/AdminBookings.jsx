import { useEffect, useState } from 'react'
import { api } from '../../api'
import Alert from '../../components/Alert'
import StatusBadge from '../../components/StatusBadge'
import { BOOKING_STATUS, formatDateTime, formatSlot } from '../../utils/format'

const FILTERS = [
  { value: 'PENDING', label: 'In attesa' },
  { value: 'ACCEPTED', label: 'Accettate' },
  { value: 'REJECTED', label: 'Rifiutate' },
  { value: 'CANCELLED', label: 'Annullate' },
  { value: '', label: 'Tutte' },
]

export default function AdminBookings() {
  const [filter, setFilter] = useState('PENDING')
  const [bookings, setBookings] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  // Id della richiesta per cui sto scrivendo il motivo del rifiuto
  const [rejectingId, setRejectingId] = useState(null)
  const [reason, setReason] = useState('')

  function loadBookings() {
    const query = filter ? `?status=${filter}` : ''
    return api
      .get(`/api/admin/bookings${query}`)
      .then(setBookings)
      .catch((err) => setError(err.message))
  }

  useEffect(() => {
    setLoading(true)
    loadBookings().finally(() => setLoading(false))
  }, [filter])

  async function handleAccept(booking) {
    setError('')
    setSuccess('')
    try {
      await api.put(`/api/admin/bookings/${booking.id}/accept`)
      setSuccess('Richiesta accettata: l\'orario è ora bloccato.')
      await loadBookings()
    } catch (err) {
      setError(err.message)
    }
  }

  async function handleReject(booking) {
    setError('')
    setSuccess('')
    try {
      await api.put(`/api/admin/bookings/${booking.id}/reject`, { reason })
      setSuccess('Richiesta rifiutata: flash e orario sono tornati disponibili.')
      setRejectingId(null)
      setReason('')
      await loadBookings()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <section>
      <div className="filters">
        {FILTERS.map((item) => (
          <button
            key={item.label}
            className={`chip ${filter === item.value ? 'active' : ''}`}
            onClick={() => setFilter(item.value)}
          >
            {item.label}
          </button>
        ))}
      </div>

      <Alert type="success">{success}</Alert>
      <Alert>{error}</Alert>
      {loading && <p className="page-message">Caricamento...</p>}
      {!loading && bookings.length === 0 && <p className="muted">Nessuna richiesta in questa categoria.</p>}

      <div className="list">
        {bookings.map((booking) => (
          <article key={booking.id} className="card list-item">
            <img src={booking.flash.imageUrl} alt="Flash richiesto" className="thumb" />
            <div className="list-item-body">
              <div className="list-item-head">
                <strong>
                  {booking.customer.firstName} {booking.customer.lastName}
                </strong>
                <StatusBadge labels={BOOKING_STATUS} value={booking.status} />
              </div>
              <p>
                Flash da {booking.flash.size} · {formatSlot(booking.slot)}
              </p>
              <p className="muted">
                {booking.customer.email} · {booking.customer.phone}
              </p>
              {booking.note && <p className="muted">Nota: {booking.note}</p>}
              <p className="muted small">Richiesta del {formatDateTime(booking.createdAt)}</p>
              {booking.status === 'REJECTED' && (
                <p className="rejection">
                  <strong>Motivo del rifiuto:</strong> {booking.rejectionReason}
                </p>
              )}

              {booking.status === 'PENDING' && rejectingId !== booking.id && (
                <div className="actions">
                  <button className="btn btn-small" onClick={() => handleAccept(booking)}>
                    Accetta
                  </button>
                  <button
                    className="btn btn-small btn-outline"
                    onClick={() => {
                      setRejectingId(booking.id)
                      setReason('')
                    }}
                  >
                    Rifiuta
                  </button>
                </div>
              )}

              {rejectingId === booking.id && (
                <div className="reject-box">
                  <label>
                    Motivo del rifiuto (lo leggerà il cliente)
                    <textarea
                      rows={2}
                      maxLength={500}
                      value={reason}
                      onChange={(e) => setReason(e.target.value)}
                      placeholder="Es. quel giorno sono in viaggio, scegli un'altra data"
                    />
                  </label>
                  <div className="actions">
                    <button className="btn btn-small" onClick={() => handleReject(booking)}>
                      Conferma rifiuto
                    </button>
                    <button className="btn btn-small btn-outline" onClick={() => setRejectingId(null)}>
                      Annulla
                    </button>
                  </div>
                </div>
              )}
            </div>
          </article>
        ))}
      </div>
    </section>
  )
}
