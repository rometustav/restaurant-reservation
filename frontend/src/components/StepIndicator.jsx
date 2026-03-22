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

export default StepIndicator