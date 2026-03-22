function FloorPlan({ allTables, isHighlighted, isOccupied }) {
  return (
    <div className="floor-plan">
      <h2>Restorani plaan</h2>
      <div className="grid-container">
        <div className="zone-divider" style={{ top: `${7 * 40}px` }} />
        <div className="zone-divider" style={{ top: `${11 * 40}px` }} />

        {allTables.map(table => (
          <div
            key={table.id}
            className={`table-block ${isHighlighted(table.id) ? 'highlighted' : ''} ${isOccupied(table.id) ? 'occupied' : 'free'}`}
            style={{
              left: `${table.x * 40}px`,
              top: `${table.y * 40}px`,
              width: `${table.width * 40}px`,
              height: `${table.height * 40}px`
            }}
          >
            <span className="table-label">Laud #{table.id}</span>
          </div>
        ))}
      </div>
    </div>
  )
}

export default FloorPlan