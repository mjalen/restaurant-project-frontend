document.addEventListener("DOMContentLoaded", function() {
	initButtons();
	load();
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
const reservationTimes = ['11:00 AM','11:30 AM','12:00 AM', '12:30 PM', '04:00 PM', '04:30 PM'];
let data;
let reservationIds = [];

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

function openDeleteReservationModal(selectedDate) {
  const deleteReservationModal = document.getElementById('deleteReservationModal');
  deleteReservationModal.style.display = 'block';

  const reservationId = reservationIds[data.reservation.findIndex(reservation => reservation.date === selectedDate)];

  document.getElementById('selectedDateDelete').innerText = selectedDate;

  document.getElementById('deleteReservation').addEventListener('click', () => {
    deleteReservation(reservationId);
  });

  document.getElementById('goBack').addEventListener('click', () => {
    closeDeleteReservationModal();
  });
}


function closeDeleteReservationModal() {
  const deleteReservationModal = document.getElementById('deleteReservationModal');
  deleteReservationModal.style.display = 'none';
  modalBackDrop.style.display = 'none';
  clicked = null;
}

function openModal(date) {
  clicked = date;
  const existingReservation = data.reservation.find(reservation => reservation.date === date);

  if (existingReservation) {
    openDeleteReservationModal(date);
  } else {
    openReservationTimesModal(date);
  }

  backDrop.style.display = 'block';
}

function openNewReservationModal(selectedTime, selectedDate) {
	const newReservationModal = document.getElementById('newReservationModal');
	newReservationModal.style.display = 'block';
	document.getElementById('selectedDateReservation').innerText = selectedDate;
	document.getElementById('selectedTime').innerText = selectedTime;
}

function fetchReservations() {
	fetch('CustomerCalendar') 
		.then(response => {
			if (!response.ok) {
				throw new Error(`HTTP error! Status: ${response.status}`);
			}
			return response.json();
		})
		.then(reservationsData => {
			data = reservationsData;
			displayReservations(reservationsData);
			reservationIds = reservationsData.reservation.map(reservation => reservation.id);
		})
		.catch(error => {
			console.error('Error fetching reservations:', error);
		});
}

function displayReservations(reservationsData) {
	const days = document.querySelectorAll('.day');
	days.forEach(day => {
		const existingReservations = day.getElementsByClassName('reservation-info');
		while (existingReservations.length > 0) {
			existingReservations[0].parentNode.removeChild(existingReservations[0]);
		}
	});

	reservationsData.reservation.forEach(reservation => {
		const dateParts = reservation.date.split('/');
		const reservationDate = new Date(dateParts[2], dateParts[0] - 1, dateParts[1]);

		days.forEach(day => {
			const dayNumber = parseInt(day.innerText);
			const currentMonth = reservationDate.getMonth() + 1; 
			const currentYear = reservationDate.getFullYear();

			if (
				!isNaN(dayNumber) &&
				dayNumber === reservationDate.getDate() &&
				currentMonth === month + 1 && 
				currentYear === year
			) {
				const reservationInfo = document.createElement('div');
				reservationInfo.classList.add('reservation-info');
				reservationInfo.innerHTML = `<p>${reservation.first_name} ${reservation.last_name}</p><p>${reservation.time}</p>`;
				reservationInfo.classList.add('small-text');
				day.appendChild(reservationInfo);
			}
		});
	});
}

function load() {
  const dt = new Date();

  if (nav !== 0) {
    dt.setMonth(new Date().getMonth() + nav);
  }

  month = dt.getMonth();
  year = dt.getFullYear();

  const day = dt.getDate();
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

  for (let i = 1; i <= paddingDays + daysInMonth; i++) {
    const daySquare = document.createElement('div');
    daySquare.classList.add('day');

    const dayString = `${month + 1}/${i - paddingDays}/${year}`;

    if (i > paddingDays) {
      daySquare.innerText = i - paddingDays;

      if (i - paddingDays === day && nav === 0) {
        daySquare.id = 'currentDay';
      }

      if (new Date(year, month, daySquare.innerText) >= new Date()) {
        daySquare.addEventListener('click', () => openModal(dayString));
      } else {
        daySquare.classList.add('inactive');
      }
      
    } else {
      daySquare.classList.add('padding');
    }

    calendar.appendChild(daySquare);
  }
  fetchReservations();
}

function closeReservationTimesModal() {
	const reservationTimesModal = document.getElementById('reservationTimesModal');
	reservationTimesModal.style.display = 'none';
	modalBackDrop.style.display = 'none';	
	customerFirst.value = '';
	customerLast.value = '';
	customerPhone.value = '';
	customerEmail.value = '';
	clicked = null;
}

function closeNewReservationModal(){
	const newReservationModal = document.getElementById('newReservationModal');
	newReservationModal.style.display = 'none';
	customerFirst.value = '';
	customerLast.value = '';
	customerPhone.value = '';
	customerEmail.value = '';
}

function saveReservation() {
  const selectedDate = document.getElementById('selectedDate').innerText;
  const selectedTime = document.getElementById('selectedTime').innerText;
  const first_name = document.getElementById('customerFirst').value;
  const last_name = document.getElementById('customerLast').value;
  const phoneInput = document.getElementById('customerPhone');
  const emailInput = document.getElementById('customerEmail');
  const phone = phoneInput.value;
  const email = emailInput.value;

  // Validation checks
  let invalidMessages = [];

  if (!first_name) {
    invalidMessages.push('Please enter a valid first name.');
  }

  if (!last_name) {
    invalidMessages.push('Please enter a valid last name.');
  }

  // Phone format validation
  const phoneRegex = /^\d{3}-\d{3}-\d{4}$/;
  if (!phoneRegex.test(phone)) {
    invalidMessages.push('Please enter a valid phone number in the format XXX-XXX-XXXX.');
    phoneInput.value = '';
  }

  // Email format validation
  const emailRegex = /\S+@\S+\.\S+/;
  if (!emailRegex.test(email)) {
    invalidMessages.push('Please enter a valid email address.');
    emailInput.value = '';
  }

  if (invalidMessages.length > 0) {
    alert('Invalid Input:\n' + invalidMessages.join('\n'));
    return;
  }

  const reservationData = {
    first_name: first_name,
    last_name: last_name,
    phone: phone,
    email: email,
    date: selectedDate,
    time: selectedTime
  };

  fetch('CustomerCalendar', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(reservationData),
  })
  .then(response => {
    if (!response.ok) {
      throw new Error(`Error:${response.status}`);
    }
    return response.json();
  })
  .then(responseData => {
    console.log('Success!:', responseData);
    closeReservationTimesModal();
    closeNewReservationModal();
    fetchReservations();
  })
  .catch(error => {
    console.error('Error:', error);
  });
}




function deleteReservation(reservationId) {
  fetch('Cust_Calendar_Deletion', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ id: reservationId }),
  })
  .then(response => {
    if (!response.ok) {
      throw new Error(`Error:${response.status}`);
    }
    return response.json();
  })
  .then(responseData => {
    console.log('Success!:', responseData);
    closeDeleteReservationModal();
    fetchReservations();
  })
  .catch(error => {
    console.error('Error:', error);
  });
}

function signOut() {
  window.location.href = 'index.html';
}

function initButtons() {
	document.getElementById('nextButton').addEventListener('click', () => {
		nav++;
		load();
		fetchReservations();
	});

	document.getElementById('backButton').addEventListener('click', () => {
		nav--;
		load();
		fetchReservations();
	});
	document.getElementById('signoutButton').addEventListener('click', signOut);
	document.getElementById('saveButton').addEventListener('click', saveReservation);
	document.getElementById('cancelButton').addEventListener('click', closeNewReservationModal);
}
