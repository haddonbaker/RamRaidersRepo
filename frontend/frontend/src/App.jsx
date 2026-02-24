import React, { useState } from 'react';
import SearchPanel from './components/SearchPanel';
import SearchResults from './components/SearchResults.jsx';
import CandidateSchedule from './components/CandidateSchedule.jsx';
import WeeklyScheduleModal from './components/WeeklyScheduleModal';
import FilterModal from './components/FilterModal';

function App() {
  const [showScheduleModal, setShowScheduleModal] = useState(false);
  const [showFilterModal, setShowFilterModal] = useState(false);

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
      <SearchPanel openFilters={() => setShowFilterModal(true)} />

      {/* Middle: Search Results */}
      <SearchResults />

      {/* Bottom: Candidate Schedule */}
      <CandidateSchedule openModal={() => setShowScheduleModal(true)} />

      {/* Weekly Schedule Popup */}
      {showScheduleModal && <WeeklyScheduleModal closeModal={() => setShowScheduleModal(false)} />}

      {/* Filter Modal Popup */}
      {showFilterModal && <FilterModal closeModal={() => setShowFilterModal(false)} />}
    </div>
  );
}

export default App;