import React, { useState } from 'react';
import SearchPanel from './components/SearchPanel';
import SearchResults from './components/SearchResults.jsx';
import CandidateSchedule from './components/CandidateSchedule.jsx';
import WeeklyScheduleModal from './components/WeeklyScheduleModal';

function App() {
  const [showScheduleModal, setShowScheduleModal] = useState(false);

  const containerStyle = {
    padding: '1rem 0.75rem',
    fontFamily: 'system-ui, -apple-system, sans-serif',
    display: 'flex',
    flexDirection: 'column',
    gap: '1.25rem',
    maxWidth: '1200px',
    margin: '0 auto',
    background: '#F8FAFC',
    minHeight: '100vh',
  }; 

  return (
    <div style={containerStyle}>
      
      {/* Top: Course Search */}
      <SearchPanel />

      {/* Middle: Search Results */}
      <SearchResults />

      {/* Bottom: Candidate Schedule */}
      <CandidateSchedule openModal={() => setShowScheduleModal(true)} />

      {/* Weekly Schedule Popup */}
      {showScheduleModal && <WeeklyScheduleModal closeModal={() => setShowScheduleModal(false)} />}
    </div>
  );
}

export default App;