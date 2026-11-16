import { useEffect, useState } from 'react'
import { api } from '../../api'
import Alert from '../../components/Alert'

export default function AdminFaqs() {
  const [faqs, setFaqs] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  // Form per aggiungere una nuova domanda
  const [newQuestion, setNewQuestion] = useState('')
  const [newAnswer, setNewAnswer] = useState('')

  // Domanda che sto modificando (null = nessuna) e i suoi campi
  const [editingId, setEditingId] = useState(null)
  const [editQuestion, setEditQuestion] = useState('')
  const [editAnswer, setEditAnswer] = useState('')

  function loadFaqs() {
    return api
      .get('/api/faqs')
      .then(setFaqs)
      .catch((err) => setError(err.message))
  }

  useEffect(() => {
    loadFaqs().finally(() => setLoading(false))
  }, [])

  async function handleCreate(event) {
    event.preventDefault()
    setError('')
    setSuccess('')
    try {
      await api.post('/api/admin/faqs', { question: newQuestion, answer: newAnswer })
      setSuccess('Domanda aggiunta.')
      setNewQuestion('')
      setNewAnswer('')
      await loadFaqs()
    } catch (err) {
      setError(err.message)
    }
  }

  function startEditing(faq) {
    setEditingId(faq.id)
    setEditQuestion(faq.question)
    setEditAnswer(faq.answer)
    setError('')
    setSuccess('')
  }

  async function handleSave(event) {
    event.preventDefault()
    setError('')
    setSuccess('')
    try {
      await api.put(`/api/admin/faqs/${editingId}`, { question: editQuestion, answer: editAnswer })
      setSuccess('Domanda aggiornata.')
      setEditingId(null)
      await loadFaqs()
    } catch (err) {
      setError(err.message)
    }
  }

  async function handleDelete(faq) {
    if (!window.confirm('Vuoi eliminare questa domanda? Non si può annullare.')) {
      return
    }
    setError('')
    setSuccess('')
    try {
      await api.delete(`/api/admin/faqs/${faq.id}`)
      setSuccess('Domanda eliminata.')
      await loadFaqs()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <section>
      <form className="card form" onSubmit={handleCreate}>
        <h2>Aggiungi una domanda</h2>
        <label>
          Domanda
          <input
            value={newQuestion}
            maxLength={300}
            onChange={(e) => setNewQuestion(e.target.value)}
            placeholder="Es. Posso portare un'amica?"
            required
          />
        </label>
        <label>
          Risposta
          <textarea
            rows={3}
            maxLength={2000}
            value={newAnswer}
            onChange={(e) => setNewAnswer(e.target.value)}
            required
          />
        </label>
        <button className="btn" type="submit">Aggiungi</button>
      </form>

      <Alert type="success">{success}</Alert>
      <Alert>{error}</Alert>
      {loading && <p className="page-message">Caricamento...</p>}
      {!loading && faqs.length === 0 && <p className="muted">Non ci sono ancora domande.</p>}

      <div className="list">
        {faqs.map((faq) =>
          editingId === faq.id ? (
            <form key={faq.id} className="card form" onSubmit={handleSave}>
              <label>
                Domanda
                <input
                  value={editQuestion}
                  maxLength={300}
                  onChange={(e) => setEditQuestion(e.target.value)}
                  required
                />
              </label>
              <label>
                Risposta
                <textarea
                  rows={4}
                  maxLength={2000}
                  value={editAnswer}
                  onChange={(e) => setEditAnswer(e.target.value)}
                  required
                />
              </label>
              <div className="actions">
                <button className="btn btn-small" type="submit">Salva</button>
                <button className="btn btn-small btn-outline" type="button" onClick={() => setEditingId(null)}>
                  Annulla
                </button>
              </div>
            </form>
          ) : (
            <article key={faq.id} className="card">
              <h3>{faq.question}</h3>
              <p className="pre-line">{faq.answer}</p>
              <div className="actions">
                <button className="btn btn-small btn-outline" onClick={() => startEditing(faq)}>
                  Modifica
                </button>
                <button className="btn btn-small btn-outline" onClick={() => handleDelete(faq)}>
                  Elimina
                </button>
              </div>
            </article>
          ),
        )}
      </div>
    </section>
  )
}
