const API_BASE_URL = '/api/activities';

// DOM Elements
const activityForm = document.getElementById('activityForm');
const activitiesContainer = document.getElementById('activitiesContainer');

// Load activities on page load
document.addEventListener('DOMContentLoaded', () => {
  loadActivities();
  
  // Refresh activities every 30 seconds
  setInterval(loadActivities, 30000);
});

// Handle form submission
activityForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  
  const title = document.getElementById('title').value;
  const description = document.getElementById('description').value;
  const dateTime = document.getElementById('dateTime').value;
  const notificationEnabled = document.getElementById('notificationEnabled').checked;
  
  const activity = {
    title,
    description,
    dateTime,
    notificationEnabled
  };
  
  try {
    const response = await fetch(API_BASE_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(activity)
    });
    
    if (!response.ok) {
      throw new Error('Failed to create activity');
    }
    
    // Clear form
    activityForm.reset();
    
    // Reload activities
    loadActivities();
  } catch (error) {
    showError('Error creating activity: ' + error.message);
  }
});

// Load all activities
async function loadActivities() {
  try {
    const response = await fetch(API_BASE_URL);
    
    if (!response.ok) {
      throw new Error('Failed to load activities');
    }
    
    const activities = await response.json();
    renderActivities(activities);
  } catch (error) {
    activitiesContainer.innerHTML = `<p class="error">Error loading activities: ${error.message}</p>`;
  }
}

// Render activities to the DOM
function renderActivities(activities) {
  if (!activities || activities.length === 0) {
    activitiesContainer.innerHTML = `
      <div class="empty-state">
        <p>No activities yet. Add one above!</p>
      </div>
    `;
    return;
  }
  
  // Sort activities by datetime
  activities.sort((a, b) => {
    if (!a.dateTime) return 1;
    if (!b.dateTime) return -1;
    return new Date(a.dateTime) - new Date(b.dateTime);
  });
  
  activitiesContainer.innerHTML = activities.map(activity => `
    <div class="activity-item" data-id="${activity.id}">
      <h3>${escapeHtml(activity.title)}
        ${activity.notificationEnabled ? '<span class="notification-badge">🔔 Notified</span>' : ''}
      </h3>
      <p class="description">${escapeHtml(activity.description) || 'No description'}</p>
      <p class="datetime">📅 ${formatDateTime(activity.dateTime)}</p>
      <div class="actions">
        <button class="btn btn-danger" onclick="deleteActivity('${activity.id}')">Delete</button>
      </div>
    </div>
  `).join('');
}

// Delete an activity
async function deleteActivity(id) {
  if (!confirm('Are you sure you want to delete this activity?')) {
    return;
  }
  
  try {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: 'DELETE'
    });
    
    if (!response.ok) {
      throw new Error('Failed to delete activity');
    }
    
    // Reload activities
    loadActivities();
  } catch (error) {
    showError('Error deleting activity: ' + error.message);
  }
}

// Format datetime for display
function formatDateTime(dateTimeStr) {
  if (!dateTimeStr) return 'No date set';
  
  const date = new Date(dateTimeStr);
  const options = {
    weekday: 'short',
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  };
  return date.toLocaleDateString('en-US', options);
}

// Escape HTML to prevent XSS
function escapeHtml(text) {
  if (!text) return '';
  const div = document.createElement('div');
  div.textContent = text;
  return div.innerHTML;
}

// Show error message
function showError(message) {
  activitiesContainer.innerHTML = `<p class="error">${escapeHtml(message)}</p>`;
}

// Make deleteActivity available globally
window.deleteActivity = deleteActivity;

