document.addEventListener("DOMContentLoaded", function() {
  load();
  initButtons();
});

let nav = 0;
let clicked = null;
const calendar = document.getElementById('calendar');
const newReservationModal = document.getElementById('newReservationModal');
const backDrop = document.getElementById('modalBackDrop');
const customerFirst = document.getElementById('customerFirst');
const customerLast = document.getElementById('customerLast');
const customerPhone = document.getElementById('customerPhone');
const customerEmail = document.getElementById('customerEmail');
const modalBackDrop = document.getElementById('modalBackDrop');
const weekdays = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
const reservationTimes = ['09:00 AM', '12:00 PM', '06:00 PM', '08:00 PM'];

function openReservationTimesModal(date) {
  document.getElementById('selectedDate').innerText = date;
  const timesContainer = document.getElementById('timesContainer');
  timesContainer.innerHTML = ''; 
	
  for (const time of reservationTimes) {
    const timeButton = document.createElement('button');
    timeButton.textContent = time;
    timeButton.classList.add('time-button');
    timesContainer.appendChild(timeButton);
  }

  const reservationTimesModal = document.getElementById('reservationTimesModal');
  reservationTimesModal.style.display = 'block';
  
  document.getElementById('closeTimesButton').addEventListener('click', closeReservationTimesModal);
  const timeButtons = document.querySelectorAll('.time-button');
  timeButtons.forEach((button) => {
    button.addEventListener('click', () => {
      const selectedTime = button.textContent;
      openNewReservationModal(selectedTime, date);
    });
  });
}

function closeReservationTimesModal() {
  const reservationTimesModal = document.getElementById('reservationTimesModal');
  reservationTimesModal.style.display = 'none';
  closeModal();
}

function openModal(date) {
  clicked = date;
  openReservationTimesModal(date);
  backDrop.style.display = 'block';
}

function openNewReservationModal(selectedTime, selectedDate) {
  const newReservationModal = document.getElementById('newReservationModal');
  newReservationModal.style.display = 'block';
  document.getElementById('selectedDateReservation').innerText = selectedDate;
  document.getElementById('selectedTime').innerText = selectedTime;
}

function load() {
  const dt = new Date();

  if (nav !== 0) {
    dt.setMonth(new Date().getMonth() + nav);
  }

  const day = dt.getDate();
  const month = dt.getMonth();
  const year = dt.getFullYear();

  const firstDayOfMonth = new Date(year, month, 1);
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  
  const dateString = firstDayOfMonth.toLocaleDateString('en-us', {
    weekday: 'long',
    year: 'numeric',
    month: 'numeric',
    day: 'numeric',
  });
  const paddingDays = weekdays.indexOf(dateString.split(', ')[0]);

  document.getElementById('monthDisplay').innerText = 
    `${dt.toLocaleDateString('en-us', { month: 'long' })} ${year}`;

  calendar.innerHTML = '';

  for(let i = 1; i <= paddingDays + daysInMonth; i++) {
    const daySquare = document.createElement('div');
    daySquare.classList.add('day');

    const dayString = `${month + 1}/${i - paddingDays}/${year}`;

    if (i > paddingDays) {
      daySquare.innerText = i - paddingDays;
      
      if (i - paddingDays === day && nav === 0) {
        daySquare.id = 'currentDay';
      }


      daySquare.addEventListener('click', () => openModal(dayString));
    } else {
      daySquare.classList.add('padding');
    }

    calendar.appendChild(daySquare);    
  }
}

function closeModal() {
  const newReservationModal = document.getElementById('newReservationModal');
  newReservationModal.style.display = 'none';
  modalBackDrop.style.display = 'none';	
  customerFirst.value = '';
  customerLast.value = '';
  customerPhone.value = '';
  customerEmail.value = '';
  clicked = null;
  load();
}

function saveReservation() {

}

function deleteReservation() {

}

function initButtons() {
  document.getElementById('nextButton').addEventListener('click', () => {
    nav++;
    load();
  });

  document.getElementById('backButton').addEventListener('click', () => {
    nav--;
    load();
  });
  document.getElementById('saveButton').addEventListener('click', saveReservation);
  document.getElementById('cancelButton').addEventListener('click', closeModal);
}
