import { useEffect, useState } from 'react'
import { api } from '../../api'
import Alert from '../../components/Alert'
import StatusBadge from '../../components/StatusBadge'
import { CONSULTATION_STATUS, formatDateTime } from '../../utils/format'

export default function AdminConsultations() {
  const [consultations, setConsultations] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  function loadConsultations() {
    return api
      .get('/api/admin/consultations')
      .then(setConsultations)
      .catch((err) => setError(err.message))
  }

  useEffect(() => {
    loadConsultations().finally(() => setLoading(false))
  }, [])

  async function handleHandled(consultation) {
    setError('')
    try {
      await api.put(`/api/admin/consultations/${consultation.id}/handled`)
      await loadConsultations()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <section>
      <Alert>{error}</Alert>
      {loading && <p className="page-message">Caricamento...</p>}
      {!loading && consultations.length === 0 && <p className="muted">Nessuna richiesta di consulenza.</p>}

      <div className="list">
        {consultations.map((c) => (
          <article key={c.id} className="card list-item">
            {c.referenceImageUrl && (
              <a href={c.referenceImageUrl} target="_blank" rel="noreferrer">
                <img src={c.referenceImageUrl} alt="Immagine di riferimento" className="thumb" />
              </a>
            )}
            <div className="list-item-body">
              <div className="list-item-head">
                <strong>
                  {c.customer.firstName} {c.customer.lastName}
                </strong>
                <StatusBadge labels={CONSULTATION_STATUS} value={c.status} />
              </div>
              <p className="muted">
                {c.customer.email} · {c.customer.phone}
              </p>
              <p className="pre-line">{c.message}</p>
              <p className="muted small">Ricevuta il {formatDateTime(c.createdAt)}</p>
              {c.status === 'NEW' && (
                <button className="btn btn-small" onClick={() => handleHandled(c)}>
                  Segna come gestita
                </button>
              )}
            </div>
          </article>
        ))}
      </div>
    </section>
  )
}
