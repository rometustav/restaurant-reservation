import { useState } from 'react'
import './index.css'

// Funktsioon sammude rea joonistamiseks
function StepIndicator({ currentStep }) {
  return (
    <div className="step-indicator">
      {[1, 2, 3, 4].map(s => (
        <div
          key={s}
          className={`step-dot ${s === currentStep ? 'active' : ''} ${s < currentStep ? 'done' : ''}`}
        />
      ))}
    </div>
  )
}

function App() {
  const [step, setStep] = useState(1)
  const [name, setName] = useState('')
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
      <div className="animated-bg" />
      
      {step === 1 && (
        <div className="step-container">
        <StepIndicator currentStep={1} />
          <h1>Tere tulemast!</h1>
          <p>Broneeri laud meie restoranis</p>
          <div className="subtitle">
            <p>Avatud iga päev</p>
            <p>10:00 - 21:00</p>
          </div>

          <label>Nimi</label>
          <input type="text" placeholder="Sisestage nimi..." value={name} onChange={e => setName(e.target.value)} />

          <label>Kuupäev</label>
          {/* Piirab tänasest varasema kuupäeva valimist */}
          <input type="date" min={new Date().toISOString().split('T')[0]} value={date} onChange={e => setDate(e.target.value)} />

          <label>Broneeringu kestus</label>
          <select value={length} onChange={e => setLength(Number(e.target.value))}>
            <option value={1}>1h</option>
            <option value={2}>2h</option>
            <option value={3}>3h</option>
          </select>

          <label>Kellaaeg</label>
          {/* Kellaaeg on piiratud broneeringu kestuse järgi, hiliseimaks ajaks on (restorani sulgemisaeg - broneeringu kestus) */}
          <select value={startTime} onChange={e => setStartTime(e.target.value)}>
            {Array.from({ length: (21 - length) - 10 + 1 }, (_, i) => {
              const hour = String(10 + i).padStart(2, '0')
              return <option key={hour} value={`${hour}:00`}>{hour}:00</option>
            })}
          </select>

          <label>Inimeste arv</label>
          <input type="number" min="1" max="20" value={partySize} onChange={e => setPartySize(Number(e.target.value))} />

          <button onClick={() => setStep(2)} disabled={!name || !date || !startTime}>
            Edasi →
          </button>
        </div>
      )}

      {step === 2 && (
        <div className="step-container">
          <StepIndicator currentStep={2} />
          <h2>{name}, kas eelistad kindlat tsooni?</h2>
          <div className="zone-buttons">
            <button className={zone === 'INDOOR' ? 'active' : ''} onClick={() => setZone(zone === 'INDOOR' ? null : 'INDOOR')}>Sisesaal</button>
            <button className={zone === 'TERRACE' ? 'active' : ''} onClick={() => setZone(zone === 'TERRACE' ? null : 'TERRACE')}>Terrass</button>
            <button className={zone === 'PRIVATE' ? 'active' : ''} onClick={() => setZone(zone === 'PRIVATE' ? null : 'PRIVATE')}>Privaatruum</button>
          </div>
          <p className="hint">Vajuta uuesti, et valik tühistada</p>
          <div className="nav-buttons">
            <button onClick={() => setStep(1)}>← Tagasi</button>
            <button onClick={() => setStep(3)}>Edasi →</button>
          </div>
        </div>
      )}

      {step === 3 && (
        <div className="step-container">
          <StepIndicator currentStep={3} />
          <h2>Vali veel laua eelistusi, kui soovid</h2>
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
        <div className="results-container">
          <StepIndicator currentStep={4} />
          <h2>Soovitused</h2>
          <button onClick={() => setStep(3)}>← Tagasi</button>
        </div>
      )}
    </div>
  )
}

export default App