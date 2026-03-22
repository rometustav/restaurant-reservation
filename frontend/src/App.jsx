import { useState } from 'react'
import './index.css'

function App() {
  const [step, setStep] = useState(1)
  const [date, setDate] = useState('')
  const [startTime, setStartTime] = useState('')
  const [length, setLength] = useState('1')
  const [partySize, setPartySize] = useState(2)
  const [zone, setZone] = useState(null)
  const [preferences, setPreferences] = useState({
    windowSeat: false,
    cornerSeat: false,
    kidsAreaSeat: false
  })

  return (
    <div className="app">
      {step === 1 && (
        <div className="step-container">
          <h1>Tere tulemast</h1>
          <p>Broneeri laud meie restoranis</p>
          <p>Avatud iga päev, 10:00 - 21:00</p>

          <label>Kuupäev</label>
          <input type="date" value={date} onChange={e => setDate(e.target.value)} />

          <label>Kellaaeg</label>
          <input type="time" value={startTime} onChange={e => setStartTime(e.target.value)} />

          <label>Broneeringu kestus</label>
          <input type="number" min="1" max="3" value={length} onChange={e => setLength(Number(e.target.value))} />

          <label>Inimeste arv</label>
          <input type="number" min="1" max="20" value={partySize} onChange={e => setPartySize(Number(e.target.value))} />

          <button onClick={() => setStep(2)} disabled={!date || !startTime}>
            Edasi →
          </button>
        </div>
      )}

      {step === 2 && (
        <div className="step-container">
          <h2>Kas Teil on tsooni eelistusi?</h2>
          <div className="zone-buttons">
            <button className={zone === 'INDOOR' ? 'active' : ''} onClick={() => setZone('INDOOR')}>Sisesaal</button>
            <button className={zone === 'TERRACE' ? 'active' : ''} onClick={() => setZone('TERRACE')}>Terrass</button>
            <button className={zone === 'PRIVATE' ? 'active' : ''} onClick={() => setZone('PRIVATE')}>Privaatruum</button>
          </div>
          <div className="nav-buttons">
            <button onClick={() => setStep(1)}>← Tagasi</button>
            <button onClick={() => setStep(3)}>Edasi →</button>
          </div>
        </div>
      )}

      {step === 3 && (
        <div className="step-container">
          <h2>Valige muud eelistused, kui soovite</h2>
          <label className="checkbox-label">
            <input type="checkbox" checked={preferences.windowSeat} onChange={e => setPreferences({...preferences, windowSeat: e.target.checked})} />
            Akna ääres
          </label>
          <label className="checkbox-label">
            <input type="checkbox" checked={preferences.cornerSeat} onChange={e => setPreferences({...preferences, cornerSeat: e.target.checked})} />
            Vaikne nurk
          </label>
          <label className="checkbox-label">
            <input type="checkbox" checked={preferences.kidsAreaSeat} onChange={e => setPreferences({...preferences, kidsAreaSeat: e.target.checked})} />
            Laste mängunurga lähedal
          </label>
          <div className="nav-buttons">
            <button onClick={() => setStep(2)}>← Tagasi</button>
            <button onClick={() => setStep(4)}>Otsi laudu →</button>
          </div>
        </div>
      )}

      {step === 4 && (
        <div className="step-container">
          <h2>Soovitused</h2>
          <button onClick={() => setStep(3)}>← Tagasi</button>
        </div>
      )}
    </div>
  )
}

export default App