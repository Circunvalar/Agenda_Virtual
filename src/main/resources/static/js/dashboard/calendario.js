document.addEventListener('DOMContentLoaded', () => {
    const calendarEl = document.getElementById('calendar');
    const dayTitle = document.getElementById('dayTitle');
    const dayBadge = document.getElementById('dayBadge');
    const dayList = document.getElementById('dayList');
    const modal = document.getElementById('calModal');
    const modalClose = document.getElementById('calModalClose');
    const modalTitle = document.getElementById('calModalTitle');
    const modalTipo = document.getElementById('calModalTipo');
    const modalFecha = document.getElementById('calModalFecha');
    const modalHora = document.getElementById('calModalHora');
    const modalDetalle = document.getElementById('calModalDetalle');

    if(!calendarEl){
        return;
    }

    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        height: 'auto',
        timeZone: 'local',
        nowIndicator: true,
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek'
        },
        events: {
            url: '/calendario/eventos',
            method: 'GET'
        },
        dateClick: (info) => {
            renderDayList(info.date, calendar.getEvents());
        },
        eventClick: (info) => {
            openDetailModal(buildDetailFromEvent(info.event));
        }
    });

    calendar.render();

    function renderDayList(date, events){
        if(!date){
            return;
        }
        const dayKey = formatDateKey(date);
        const items = events.filter(evt => {
            const start = evt.start;
            if(!start){
                return false;
            }
            const end = evt.end;
            const dayStart = startOfDay(date);
            const dayEnd = endOfDay(date);

            if(end){
                return start <= dayEnd && end >= dayStart;
            }
            return formatDateKey(start) === dayKey;
        });

        dayTitle.textContent = formatReadableDate(date);
        dayBadge.textContent = String(items.length);

        if(!items.length){
            dayList.innerHTML = '<p class="day-empty">No hay eventos o recordatorios para este día.</p>';
            return;
        }

        dayList.innerHTML = items.map(evt => {
            const type = evt.extendedProps && evt.extendedProps.tipo ? evt.extendedProps.tipo : 'evento';
            const time = evt.allDay ? 'Todo el día' : formatTime(evt.start);
            const extra = evt.extendedProps && evt.extendedProps.detalle ? evt.extendedProps.detalle : '';
            const color = evt.backgroundColor || evt.borderColor || '#6366f1';
            return (
                '<div class="day-item" data-id="' + escapeHtml(evt.id || '') + '">' +
                '<div class="day-item-title">' + escapeHtml(evt.title) + '</div>' +
                '<div class="day-item-meta">' +
                '<span class="day-pill">' + escapeHtml(type) + '</span>' +
                '<span>' + escapeHtml(time) + '</span>' +
                (extra ? '<span>' + escapeHtml(extra) + '</span>' : '') +
                '</div>' +
                '</div>'
            );
        }).join('');
    }

    function formatDateKey(date){
        const d = new Date(date);
        const y = d.getFullYear();
        const m = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        return y + '-' + m + '-' + day;
    }

    function startOfDay(date){
        const d = new Date(date);
        d.setHours(0, 0, 0, 0);
        return d;
    }

    function endOfDay(date){
        const d = new Date(date);
        d.setHours(23, 59, 59, 999);
        return d;
    }

    function formatReadableDate(date){
        const d = new Date(date);
        return d.toLocaleDateString('es-CO', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }

    function formatTime(date){
        const d = new Date(date);
        return d.toLocaleTimeString('es-CO', {
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    function escapeHtml(text){
        if(text === null || text === undefined){
            return '';
        }
        return String(text)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    function buildDetailFromEvent(evt){
        const type = evt.extendedProps && evt.extendedProps.tipo ? evt.extendedProps.tipo : 'evento';
        const detalle = evt.extendedProps && evt.extendedProps.detalle ? evt.extendedProps.detalle : '-';
        const fecha = formatReadableDate(evt.start || new Date());
        const hora = evt.allDay ? 'Todo el día' : formatTimeRange(evt.start, evt.end);
        return {
            title: evt.title || 'Detalle',
            tipo: type,
            fecha,
            hora,
            detalle
        };
    }

    function openDetailModal(detail){
        if(!modal || !detail){
            return;
        }
        modalTitle.textContent = detail.title || 'Detalle';
        modalTipo.textContent = detail.tipo || '-';
        modalFecha.textContent = detail.fecha || '-';
        modalHora.textContent = detail.hora || '-';
        modalDetalle.textContent = detail.detalle || '-';
        modal.classList.add('active');
        modal.setAttribute('aria-hidden', 'false');
    }

    function closeDetailModal(){
        if(!modal){
            return;
        }
        modal.classList.remove('active');
        modal.setAttribute('aria-hidden', 'true');
    }

    function formatTimeRange(start, end){
        if(!start){
            return '-';
        }
        const startText = formatTime(start);
        if(!end){
            return startText;
        }
        return startText + ' - ' + formatTime(end);
    }

    if(dayList){
        dayList.addEventListener('click', (event) => {
            const item = event.target.closest('.day-item');
            if(!item){
                return;
            }
            const id = item.getAttribute('data-id');
            const evt = calendar.getEvents().find(e => String(e.id) === String(id));
            if(evt){
                openDetailModal(buildDetailFromEvent(evt));
            }
        });
    }

    if(modalClose){
        modalClose.addEventListener('click', closeDetailModal);
    }

    if(modal){
        modal.addEventListener('click', (event) => {
            if(event.target === modal){
                closeDetailModal();
            }
        });
    }

    document.addEventListener('keydown', (event) => {
        if(event.key === 'Escape'){
            closeDetailModal();
        }
    });
});
