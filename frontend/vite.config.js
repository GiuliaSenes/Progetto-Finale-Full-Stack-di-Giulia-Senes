import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Le chiamate a /api e /uploads vengono girate al backend Spring Boot (porta 8080).
// Se serve un altro indirizzo: API_URL=http://localhost:8081 npm run dev
const apiUrl = process.env.API_URL || 'http://localhost:8080'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': apiUrl,
      '/uploads': apiUrl,
    },
  },
})
