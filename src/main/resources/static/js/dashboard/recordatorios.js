let createChanged = false;
let editChanged = false;

/* =========================
   ELEMENTOS
========================= */

const createModal =
    document.getElementById('createModal');

const editModal =
    document.getElementById('editModal');

const createForm =
    document.getElementById('createForm');

const editForm =
    document.getElementById('editForm');

const openCreateModalBtn =
    document.getElementById('openCreateModalBtn');

/* =========================
   MODALES
========================= */

function openCreateModal(){

    resetCreateForm();

    createModal.style.display = 'flex';

}

function openEditModal(button){

    resetEditForm();

    editModal.style.display = 'flex';

    // Datos desde dataset del botón; si faltan, hacemos fallback al DOM
    const data = (button && button.dataset) ? button.dataset : {};

    // Encuentra la tarjeta (card) más cercana para extraer info si dataset incompleto
    const card = (button && button.closest)
        ? button.closest('.recordatorio-card')
        : null;

    // id (desde dataset o atributo data-id), usado para construir la acción del form
    const id = data.id || (button.getAttribute && button.getAttribute('data-id')) || (card && card.dataset && card.dataset.id);

    if(id){
        editForm.action = `/recordatorios/update/${id}`;
    }

    const getTextFromCard = (selector) => {
        if(!card || !selector) return '';
        const el = card.querySelector(selector);
        return el ? el.textContent.trim() : '';
    };

    // Título y mensaje
    const titulo = data.titulo || getTextFromCard('.recordatorio-title-group h3') || getTextFromCard('.recordatorio-title h3');
    const mensaje = data.mensaje || getTextFromCard('.recordatorio-title-group p') || getTextFromCard('.recordatorio-title p');

    setValue('editTitulo', titulo);
    setValue('editMensaje', mensaje);

    // Fecha límite: preferimos dataset, si no está intentamos parsear el texto mostrado
    let fechaVal = data.fecha || data.fechaLimite || '';
    if(!fechaVal && card){
        const dateEl = card.querySelector('.recordatorio-info span');
        if(dateEl){
            const txt = dateEl.textContent || '';
            const m = txt.match(/(\d{2}\/\d{2}\/\d{4}\s+\d{2}:\d{2})/);
            if(m){
                const d = m[1]; // dd/MM/yyyy HH:mm
                const parts = d.split(' ');
                const dateParts = parts[0].split('/');
                const timePart = parts[1];
                fechaVal = `${dateParts[2]}-${dateParts[1]}-${dateParts[0]}T${timePart}`;
            }
        }
    }

    setValue('editFechaLimite', formatDateTimeLocal(fechaVal));

    // Recordar antes
    let recordar = data.recordarantes || data.recordar || '';
    if(!recordar && card){
        const spans = card.querySelectorAll('.recordatorio-info span');
        if(spans && spans.length > 1){
            const rtxt = spans[1].textContent || '';
            const num = rtxt.match(/(\d+)/);
            if(num) recordar = num[1];
        }
    }

    setValue('editRecordarAntes', recordar || 30);

    // Prioridad y categoría (dataset o badges)
    const prioridad = data.prioridad || (card && (card.querySelector('.badge.prioridad') || card.querySelector('.badge-priority')) && (card.querySelector('.badge.prioridad') || card.querySelector('.badge-priority')).textContent.trim()) || 'MEDIA';
    const categoria = data.categoria || (card && (card.querySelector('.badge.categoria') || card.querySelector('.badge-category')) && (card.querySelector('.badge.categoria') || card.querySelector('.badge-category')).textContent.trim()) || 'OTRO';

    setValue('editPrioridad', prioridad);
    setValue('editCategoria', categoria);

    // Color
    let colorVal = data.color || '';
    if(!colorVal && card){
        const colorEl = card.querySelector('.recordatorio-color');
        if(colorEl){
            const bg = colorEl.style.background || getComputedStyle(colorEl).backgroundColor || '';
            colorVal = bg;
        }
    }
    setValue('editColor', colorVal || '#6366f1');

    // Intervalo días
    setValue('editIntervaloDias', data.intervalodias || data.intervalo || '');

    // Repetitivo: dataset o existencia de la badge
    const rep = (data.repetitivo !== undefined) ? data.repetitivo : (card && !!card.querySelector('.badge.repetitivo'));
    const editRepetitivoCheckbox = document.getElementById('editRepetitivo');
    if(editRepetitivoCheckbox){
        editRepetitivoCheckbox.checked = (rep === 'true' || rep === true || rep === '1' || rep === 1);
        toggleRepeatFields('edit', editRepetitivoCheckbox.checked);
    }

}

function closeModal(modal){

    modal.style.display = 'none';

}

function attemptCloseModal(target){

    const normalized =
        target === 'createModal' || target === 'create'
            ? 'create'
            : 'edit';

    const changed =
        normalized === 'create'
            ? createChanged
            : editChanged;

    if(changed){

        const confirmClose = confirm(
            'Tienes cambios sin guardar. ¿Deseas salir?'
        );

        if(!confirmClose){
            return;
        }

    }

    if(normalized === 'create'){

        closeModal(createModal);

    }else{

        closeModal(editModal);

    }

}

function closeFromButton(button){

    const targetId = button.dataset.close;

    if(targetId === 'createModal'){

        attemptCloseModal('create');

        return;

    }

    if(targetId === 'editModal'){

        attemptCloseModal('edit');

        return;

    }

    const modal = document.getElementById(targetId);

    if(modal){

        closeModal(modal);

    }

}

function bindEditButtons(){

    const editButtons =
        document.querySelectorAll(
            '.recordatorio-actions button[data-id]'
        );

    editButtons.forEach(button => {

        button.addEventListener(
            'click',
            () => openEditModal(button)
        );

    });

}

/* =========================
   HELPERS
========================= */

function setValue(id, value){

    const element =
        document.getElementById(id);

    if(element){

        element.value = value || '';

    }

}

function formatDateTimeLocal(dateString){

    if(!dateString){
        return '';
    }

    try{

        return dateString.substring(0,16);

    }catch(e){

        return '';

    }

}

/* =========================
   RESET FORMS
========================= */

function resetCreateForm(){

    if(createForm){

        createForm.reset();

        createChanged = false;

    }

}

function resetEditForm(){

    if(editForm){

        editForm.reset();

        editChanged = false;

    }

}

/* =========================
   TRACK CHANGES
========================= */

if(createForm){

    createForm.addEventListener(
        'input',
        () => createChanged = true
    );

}

if(editForm){

    editForm.addEventListener(
        'input',
        () => editChanged = true
    );

}

/* =========================
   CERRAR FUERA
========================= */

window.addEventListener('click', event => {

    if(event.target === createModal){

        attemptCloseModal('createModal');

    }

    if(event.target === editModal){

        attemptCloseModal('editModal');

    }

});

/* =========================
   FILTROS
========================= */

const searchInput =
    document.getElementById('searchInput');

const priorityFilter =
    document.getElementById('priorityFilter');

const categoryFilter =
    document.getElementById('categoryFilter');

function filterCards(){

    const cards =
        document.querySelectorAll(
            '.recordatorio-card'
        );

    const search =
        searchInput.value.toLowerCase();

    const priority =
        priorityFilter.value;

    const category =
        categoryFilter.value;

    cards.forEach(card => {

        const text =
            card.innerText.toLowerCase();

        const matchesSearch =
            text.includes(search);

        const matchesPriority =
            !priority ||
            card.dataset.prioridad === priority;

        const matchesCategory =
            !category ||
            card.dataset.categoria === category;

        card.style.display =
            matchesSearch &&
            matchesPriority &&
            matchesCategory
                ? 'block'
                : 'none';

    });

}

if(searchInput){

    searchInput.addEventListener(
        'input',
        filterCards
    );

}

if(priorityFilter){

    priorityFilter.addEventListener(
        'change',
        filterCards
    );

}

if(categoryFilter){

    categoryFilter.addEventListener(
        'change',
        filterCards
    );

}

/* =========================
   REPETICIÓN
========================= */

const createRepetitivo =
    document.getElementById('repetitivoCreate');

const editRepetitivo =
    document.getElementById('editRepetitivo');

function toggleRepeatFields(prefix, checked){

    const sectionId =
        prefix === 'create'
            ? 'intervaloDiasContainer'
            : 'editIntervaloDiasContainer';

    const section =
        document.getElementById(sectionId);

    if(section){

        section.style.display =
            checked
                ? 'block'
                : 'none';

    }

}

if(createRepetitivo){

    createRepetitivo.addEventListener(
        'change',
        e => {

            toggleRepeatFields(
                'create',
                e.target.checked
            );

        }
    );

}

if(editRepetitivo){

    editRepetitivo.addEventListener(
        'change',
        e => {

            toggleRepeatFields(
                'edit',
                e.target.checked
            );

        }
    );

}

document.addEventListener('DOMContentLoaded', () => {

    if(openCreateModalBtn){

        openCreateModalBtn.addEventListener(
            'click',
            openCreateModal
        );

    }

    const closeButtons =
        document.querySelectorAll('[data-close]');

    closeButtons.forEach(button => {

        button.addEventListener(
            'click',
            () => closeFromButton(button)
        );

    });

    bindEditButtons();

});

// Event delegation: si por alguna razón los botones se renderizan
// dinámicamente después, capturamos clicks en documento y
// abrimos el modal cuando el objetivo sea un `.edit-btn`.
document.addEventListener('click', event => {

    const btn = event.target.closest &&
        event.target.closest('.recordatorio-actions button[data-id]');

    if(btn){

        // Evita que algún otro manejador evite la acción
        event.preventDefault();

        // Debug opcional (queda en consola del navegador)
        try{
            console.debug('Edit button clicked, id=', btn.dataset.id);
        }catch(e){/* ignore */}

        openEditModal(btn);

    }

});

// Cerrar modal con ESC
window.addEventListener('keydown', e => {

    if(e.key === 'Escape'){

        if(createModal && createModal.style.display === 'flex'){
            attemptCloseModal('createModal');
            return;
        }

        if(editModal && editModal.style.display === 'flex'){
            attemptCloseModal('editModal');
        }

    }

});

