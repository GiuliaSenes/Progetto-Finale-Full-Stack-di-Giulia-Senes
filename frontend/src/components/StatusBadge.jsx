// Etichetta colorata per gli stati (flash, richieste, slot, consulenze).
// "labels" è una delle tabelle di format.js, "value" è lo stato che arriva dal backend.
export default function StatusBadge({ labels, value }) {
  return <span className={`badge badge-${value.toLowerCase()}`}>{labels[value]}</span>
}
