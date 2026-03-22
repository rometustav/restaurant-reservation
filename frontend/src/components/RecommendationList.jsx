const zoneNames = {
  INDOOR: 'Sisesaal',
  TERRACE: 'Terrass',
  PRIVATE: 'Privaatruum'
}

function RecommendationList({ recommendations, selectedRec, setSelectedRec, showAllRecs, setShowAllRecs }) {
  return (
    <div className="recommendations-list">
      <h2>Soovitused</h2>
      {(showAllRecs ? recommendations : recommendations.slice(0, 3)).map((rec, i) => (
        <div
          key={i}
          className={`rec-card ${selectedRec === rec ? 'selected' : ''}`}
          onClick={() => setSelectedRec(rec)}
        >
          <div className="rec-info">
            <span className="rec-tables">
              {rec.tables.length > 1 ? 'Lauad' : 'Laud'} {rec.tables.map(t => `#${t.id}`).join(' + ')}
            </span>
            <span className="rec-details">
              {rec.tables.reduce((sum, t) => sum + t.capacity, 0)} kohta · {zoneNames[rec.tables[0].zone]}
              {rec.tables.some(t => t.windowTable) && ' · Akna ääres'}
              {rec.tables.some(t => t.cornerTable) && ' · Vaikne nurk'}
              {rec.tables.some(t => t.kidsAreaTable) && ' · Laste mängunurga lähedal'}
            </span>
          </div>
          <div className="rec-score-bar">
            <div className="rec-score-fill" style={{ width: `${rec.score}%` }} />
          </div>
          <span className="rec-score-text">{rec.score}%</span>
        </div>
      ))}
      {!showAllRecs && recommendations.length > 3 && (
        <button className="show-more-btn" onClick={() => setShowAllRecs(true)}>
          Näita rohkem ({recommendations.length - 3})
        </button>
      )}
    </div>
  )
}

export default RecommendationList