import { useEffect, useState } from 'react'
import { api } from '../../api'
import Alert from '../../components/Alert'
import StatusBadge from '../../components/StatusBadge'
import { FLASH_STATUS } from '../../utils/format'

export default function AdminFlashes() {
  const [flashes, setFlashes] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  // Form per aggiungere un nuovo flash
  const [newImage, setNewImage] = useState(null)
  const [newSize, setNewSize] = useState('')
  const [fileInputKey, setFileInputKey] = useState(0) // serve per svuotare il campo file

  function loadFlashes() {
    return api
      .get('/api/flashes')
      .then(setFlashes)
      .catch((err) => setError(err.message))
  }

  useEffect(() => {
    loadFlashes().finally(() => setLoading(false))
  }, [])

  async function handleCreate(event) {
    event.preventDefault()
    setError('')
    setSuccess('')
    try {
      const formData = new FormData()
      formData.append('image', newImage)
      formData.append('size', newSize)
      await api.upload('/api/admin/flashes', formData)
      setSuccess('Flash aggiunto alla galleria.')
      setNewImage(null)
      setNewSize('')
      setFileInputKey(fileInputKey + 1)
      await loadFlashes()
    } catch (err) {
      setError(err.message)
    }
  }

  // Cambia solo lo stato (disponibile / non disponibile) di un flash
  async function handleStatusChange(flash, status) {
    setError('')
    setSuccess('')
    try {
      await api.put(`/api/admin/flashes/${flash.id}`, { size: flash.size, status })
      setSuccess('Flash aggiornato.')
      await loadFlashes()
    } catch (err) {
      setError(err.message)
    }
  }

  async function handleDelete(flash) {
    if (!window.confirm('Vuoi eliminare questo flash? Non si può annullare.')) {
      return
    }
    setError('')
    setSuccess('')
    try {
      await api.delete(`/api/admin/flashes/${flash.id}`)
      setSuccess('Flash eliminato.')
      await loadFlashes()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <section>
      <form className="card form" onSubmit={handleCreate}>
        <h2>Aggiungi un flash</h2>
        <div className="form-row">
          <label>
            Immagine
            <input
              key={fileInputKey}
              type="file"
              accept="image/jpeg,image/png,image/webp"
              onChange={(e) => setNewImage(e.target.files[0] || null)}
              required
            />
          </label>
          <label>
            Dimensione
            <input value={newSize} onChange={(e) => setNewSize(e.target.value)} placeholder="Es. 8 cm" required />
          </label>
        </div>
        <button className="btn" type="submit" disabled={!newImage}>
          Aggiungi
        </button>
      </form>

      <Alert type="success">{success}</Alert>
      <Alert>{error}</Alert>
      {loading && <p className="page-message">Caricamento...</p>}

      <div className="flash-grid">
        {flashes.map((flash) => (
          <article key={flash.id} className="flash-card">
            <div className="flash-image">
              <img src={flash.imageUrl} alt={`Flash di ${flash.size}`} loading="lazy" />
            </div>
            <div className="flash-info">
              <p className="flash-size">Dimensione: {flash.size}</p>
              <StatusBadge labels={FLASH_STATUS} value={flash.status} />

              {flash.status === 'PENDING' ? (
                <p className="flash-note">Ha una richiesta in attesa: gestiscila dalla scheda "Richieste".</p>
              ) : (
                <select
                  value={flash.status}
                  onChange={(e) => handleStatusChange(flash, e.target.value)}
                  aria-label="Stato del flash"
                >
                  <option value="AVAILABLE">Disponibile</option>
                  <option value="UNAVAILABLE">Non disponibile</option>
                </select>
              )}

              <button className="btn btn-small btn-outline" onClick={() => handleDelete(flash)}>
                Elimina
              </button>
            </div>
          </article>
        ))}
      </div>
    </section>
  )
}
