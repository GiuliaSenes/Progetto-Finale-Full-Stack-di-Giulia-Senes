// Tutte le chiamate al backend passano da qui.
// Il token JWT viene salvato nel localStorage e inviato nell'header Authorization.

const TOKEN_KEY = 'charlotte_token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}

async function request(path, { method = 'GET', body, formData } = {}) {
  const headers = {}
  const token = getToken()
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  let payload
  if (formData) {
    // Per i form con file il browser imposta da solo il Content-Type
    payload = formData
  } else if (body !== undefined) {
    headers['Content-Type'] = 'application/json'
    payload = JSON.stringify(body)
  }

  let response
  try {
    response = await fetch(path, { method, headers, body: payload })
  } catch {
    throw new Error('Impossibile contattare il server. Riprova tra poco.')
  }

  if (response.status === 204) {
    return null
  }

  const data = await response.json().catch(() => null)

  if (!response.ok) {
    // Token scaduto o non valido: avvisiamo l'AuthContext che fa il logout
    if (response.status === 401 && token && !path.startsWith('/api/auth')) {
      window.dispatchEvent(new Event('auth:expired'))
    }
    throw new Error(data?.message || 'Si è verificato un errore, riprova.')
  }

  return data
}

export const api = {
  get: (path) => request(path),
  post: (path, body) => request(path, { method: 'POST', body }),
  put: (path, body) => request(path, { method: 'PUT', body }),
  delete: (path) => request(path, { method: 'DELETE' }),
  upload: (path, formData) => request(path, { method: 'POST', formData }),
}
