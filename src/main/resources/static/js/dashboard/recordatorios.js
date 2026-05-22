let createChanged = false;
let editChanged = false;

const createModal =
    document.getElementById('createModal');

const editModal =
    document.getElementById('editModal');

const createForm =
    document.getElementById('createForm');

const editForm =
    document.getElementById('editForm');

/* =========================
   MODALES
========================= */

function openCreateModal() {

    createModal.style.display = 'flex';

}

function openEditModal(button) {

    editModal.style.display = 'flex';

    const id =
        button.dataset.id;

    editForm.action =
        `/recordatorios/update/${id}`;

    document.getElementById(
        'editTitulo'
    ).value =
        button.dataset.titulo || '';

    document.getElementById(
        'editMensaje'
    ).value =
        button.dataset.mensaje || '';

    const fecha =
        button.dataset.fecha;

    if (fecha) {

        document.getElementById(
            'editFechaLimite'
        ).value =
            fecha.substring(0, 16);

    }

    document.getElementById(
        'editPrioridad'
    ).value =
        button.dataset.prioridad || 'MEDIA';

    document.getElementById(
        'editCategoria'
    ).value =
        button.dataset.categoria || 'OTRO';

    document.getElementById(
        'editColor'
    ).value =
        button.dataset.color || '#6366f1';

    document.getElementById(
        'editRecordarAntes'
    ).value =
        button.dataset.recordarantes || '';

    document.getElementById(
        'editIntervaloDias'
    ).value =
        button.dataset.intervalodias || '';

    document.getElementById(
        'editTipoRepeticion'
    ).value =
        button.dataset.tiporepeticion || 'DIARIO';

    const repetitivo =
        button.dataset.repetitivo === 'true';

    document.getElementById(
        'editRepetitivo'
    ).checked =
        repetitivo;

}

/* =========================
   CERRAR MODAL
========================= */

function closeModal(modalId) {

    document.getElementById(
        modalId
    ).style.display = 'none';

}

/* =========================
   CONFIRMAR CIERRE
========================= */

function attemptCloseModal(modalId) {

    const changed =
        modalId === 'createModal'
            ? createChanged
            : editChanged;

    if (changed) {

        const confirmClose = confirm(
            'Tienes cambios sin guardar. ¿Deseas salir?'
        );

        if (!confirmClose) {
            return;
        }

    }

    closeModal(modalId);

}

/* =========================
   TRACK CHANGES
========================= */

if (createForm) {

    createForm.addEventListener(
        'input',
        () => createChanged = true
    );

    createForm.addEventListener(
        'submit',
        () => createChanged = false
    );

}

if (editForm) {

    editForm.addEventListener(
        'input',
        () => editChanged = true
    );

    editForm.addEventListener(
        'submit',
        () => editChanged = false
    );

}

/* =========================
   CERRAR POR FUERA
========================= */

window.onclick = function (event) {

    if (event.target === createModal) {

        attemptCloseModal('createModal');

    }

    if (event.target === editModal) {

        attemptCloseModal('editModal');

    }

};

/* =========================
   FILTROS
========================= */

const searchInput =
    document.getElementById('searchInput');

const priorityFilter =
    document.getElementById('priorityFilter');

const categoryFilter =
    document.getElementById('categoryFilter');

function filterCards() {

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

        if (
            matchesSearch &&
            matchesPriority &&
            matchesCategory
        ) {

            card.style.display = 'flex';

        } else {

            card.style.display = 'none';

        }

    });

}

if (searchInput) {

    searchInput.addEventListener(
        'input',
        filterCards
    );

}

if (priorityFilter) {

    priorityFilter.addEventListener(
        'change',
        filterCards
    );

}

if (categoryFilter) {

    categoryFilter.addEventListener(
        'change',
        filterCards
    );

}