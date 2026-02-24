import React from 'react';

function WeeklyScheduleModal({ closeModal }) {
  const modalStyle = {
    position: 'fixed',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    backgroundColor: '#FFFFFF',
    border: '2px solid #1976D2',
    borderRadius: '12px',
    zIndex: 1000,
    width: '95%',
    maxWidth: '650px',
    maxHeight: '80vh',
    display: 'flex',
    flexDirection: 'column',
    boxShadow: '0 10px 40px rgba(0, 0, 0, 0.2)',
    overflow: 'hidden',
  };

  const contentStyle = {
    padding: '1.25rem',
    overflowY: 'auto',
    flex: 1,
  };

  const overlayStyle = {
    position: 'fixed',
    top: 0,
    left: 0,
    height: '100%',
    width: '100%',
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    zIndex: 999,
  };

  const headingStyle = {
    color: '#1976D2',
    fontSize: '1.25rem',
    marginTop: 0,
    marginBottom: '1rem',
  };

  const gridContainerStyle = {
    display: 'grid',
    gridTemplateColumns: `70px repeat(5, 1fr)`,
    gap: '0px',
    textAlign: 'center',
    marginBottom: '1rem',
    fontSize: '0.85rem',
  };

  const headerCellStyle = {
    border: '1px solid #E5E7EB',
    fontWeight: 'bold',
    backgroundColor: '#1976D2',
    color: 'white',
    padding: '0.6rem 0.4rem',
    fontSize: '0.85rem',
  };

  const timeLabelStyle = {
    border: '1px solid #E5E7EB',
    backgroundColor: '#F3F4F6',
    color: '#1F2937',
    padding: '0.6rem 0.4rem',
    fontSize: '0.8rem',
    fontWeight: '500',
  };

  const cellStyle = {
    border: '1px solid #E5E7EB',
    minHeight: '35px',
    backgroundColor: '#FFFFFF',
  };

  const buttonStyle = {
    padding: '0.6rem 1.2rem',
    background: '#1976D2',
    color: 'white',
    border: 'none',
    borderRadius: '8px',
    fontSize: '0.95rem',
    fontWeight: '500',
    cursor: 'pointer',
    transition: 'background-color 0.2s',
    alignSelf: 'flex-start',
    marginLeft: '1.25rem',
    marginBottom: '1.25rem',
  };

  const days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"];
  
  // Function to format hour as 12-hour time
  const formatHour = (hour) => {
    if (hour === 0) return '12:00 AM';
    if (hour < 12) return `${hour}:00 AM`;
    if (hour === 12) return '12:00 PM';
    return `${hour - 12}:00 PM`;
  };

  const hours = Array.from({ length: 10 }, (_, i) => 8 + i); // 8am–5pm

  return (
    <>
      <div style={overlayStyle} onClick={closeModal}></div>
      <div style={modalStyle}>
        <div style={contentStyle}>
          <h2 style={headingStyle}>Weekly Schedule</h2>
          <div style={gridContainerStyle}>
            
            {/* Top-left empty corner */}
            <div style={headerCellStyle}></div>

            {/* Day headers */}
            {days.map(day => (
              <div key={day} style={headerCellStyle}>
                {day}
              </div>
            ))}

            {/* Time labels and empty cells */}
            {hours.map(hour => (
              <React.Fragment key={`hour-${hour}`}>
                {/* Time label */}
                <div style={timeLabelStyle}>
                  {formatHour(hour)}
                </div>

                {/* Empty cells for each day */}
                {days.map(day => (
                  <div key={`${day}-${hour}`} style={cellStyle}></div>
                ))}
              </React.Fragment>
            ))}
          </div>
        </div>

        <button 
          onClick={closeModal} 
          style={buttonStyle}
          onMouseEnter={(e) => e.target.style.background = '#1565C0'}
          onMouseLeave={(e) => e.target.style.background = '#1976D2'}
        >
          Close
        </button>
      </div>
    </>
  );
}

export default WeeklyScheduleModal;