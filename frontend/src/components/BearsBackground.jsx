// Gli orsacchiotti disegnati da Charlotte, piccoli e sparsi nello sfondo.
// "n" è il numero del file (bear-1..5), top/left sono percentuali dello schermo.
const BEARS = [
  { n: 1, top: 6, left: 4, size: 56, rotate: -8 },
  { n: 2, top: 11, left: 88, size: 60, rotate: 10 },
  { n: 3, top: 24, left: 9, size: 48, rotate: 14 },
  { n: 4, top: 31, left: 82, size: 52, rotate: -12 },
  { n: 5, top: 44, left: 3, size: 58, rotate: 6 },
  { n: 1, top: 50, left: 92, size: 50, rotate: -6 },
  { n: 2, top: 62, left: 11, size: 46, rotate: -16 },
  { n: 3, top: 68, left: 85, size: 60, rotate: 12 },
  { n: 4, top: 80, left: 5, size: 54, rotate: 9 },
  { n: 5, top: 86, left: 90, size: 48, rotate: -10 },
  { n: 1, top: 88, left: 20, size: 44, rotate: 8 },
  { n: 2, top: 89, left: 72, size: 46, rotate: -7 },
]

// Sfondo decorativo fisso, dietro al contenuto: non serve a chi usa uno screen reader
export default function BearsBackground() {
  return (
    <div className="bears-bg" aria-hidden="true">
      {BEARS.map((bear, index) => (
        <img
          key={index}
          src={`/img/bear-${bear.n}.png`}
          alt=""
          style={{
            top: `${bear.top}%`,
            left: `${bear.left}%`,
            width: `${bear.size}px`,
            transform: `rotate(${bear.rotate}deg)`,
          }}
        />
      ))}
    </div>
  )
}
