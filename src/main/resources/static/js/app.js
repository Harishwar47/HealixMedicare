// Minimal JS client: parses availableSlots from data attribute, renders buttons, and connects to WebSocket to get live updates
document.addEventListener('DOMContentLoaded', function(){
    document.querySelectorAll('.slots').forEach(renderSlots);
    connectWebSocket();
});

function renderSlots(el){
    const raw = el.getAttribute('data-slots') || '';
    el.innerHTML = '';
    const slots = raw.split(',').filter(s => s.trim().length>0);
    slots.forEach(s => {
        const btn = document.createElement('button');
        btn.textContent = s;
        btn.addEventListener('click', ()=> bookSlot(el, s));
        el.appendChild(btn);
    });
}

function showSlots(btn){
    const container = btn.closest('.doctor').querySelector('.slots');
    container.style.display = container.style.display === 'block' ? 'none' : 'block';
}

function bookSlot(container, time){
    const doctorEl = container.closest('.doctor');
    const doctorId = doctorEl.getAttribute('data-id') || null;
    const body = { doctorId: doctorId || 1, patientUsername: 'patient', date: new Date().toISOString().slice(0,10), time };

    fetch('/api/appointments', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(body)})
        .then(r=>r.json())
        .then(data => alert('Booked: ' + JSON.stringify(data)))
        .catch(e=>alert('Booking failed: '+e));
}

function connectWebSocket(){
    try{
        const socket = new SockJS('/ws');
        const stompClient = Stomp.over(socket);
        stompClient.connect({}, function(frame){
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/appointments', function(message){
                const payload = JSON.parse(message.body);
                console.log('Appointment update', payload);
                // TODO: adjust UI to mark slot as taken
            });
        });
    }catch(e){
        console.warn('WebSocket not available', e);
    }
}
