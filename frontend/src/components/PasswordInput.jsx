import { useState } from 'react'

// Occhio aperto: la password è nascosta, cliccando la mostri
function EyeIcon() {
  return (
    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="1.7"
      strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      <path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z" />
      <circle cx="12" cy="12" r="3" />
    </svg>
  )
}

// Occhio barrato: la password è visibile, cliccando la nascondi
function EyeOffIcon() {
  return (
    <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="1.7"
      strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      <path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z" />
      <circle cx="12" cy="12" r="3" />
      <path d="M3 3l18 18" />
    </svg>
  )
}

// Campo password con l'occhio per mostrare/nascondere. All'inizio la password è sempre nascosta.
// Accetta le stesse proprietà di un normale <input> (name, value, onChange, required...).
export default function PasswordInput(props) {
  const [visible, setVisible] = useState(false)
  const label = visible ? 'Nascondi la password' : 'Mostra la password'

  return (
    <div className="password-field">
      <input {...props} type={visible ? 'text' : 'password'} />
      <button
        type="button"
        className="password-toggle"
        onClick={() => setVisible(!visible)}
        aria-pressed={visible}
        aria-label={label}
        title={label}
      >
        {visible ? <EyeOffIcon /> : <EyeIcon />}
      </button>
    </div>
  )
}
