// Messaggio di errore o di successo. Se "children" è vuoto non mostra nulla.
export default function Alert({ type = 'error', children }) {
  if (!children) {
    return null
  }
  return (
    <div className={`alert alert-${type}`} role={type === 'error' ? 'alert' : 'status'}>
      {children}
    </div>
  )
}
