tailwind.config = {
    darkMode:'class'
};

const html = document.documentElement;

if(localStorage.theme === 'dark'){
    html.classList.add('dark');
}

function toggleTheme(){

    html.classList.toggle('dark');

    localStorage.theme =
        html.classList.contains('dark')
            ? 'dark'
            : 'light';
}

const modal =
    document.getElementById('modal');

function openModal(){

    modal.classList.remove('hidden');
}

function closeModal(){

    modal.classList.add('hidden');
}

function toggleHoras(){

    const todoElDia =
        document.getElementById('todoElDia');

    const horasContainer =
        document.getElementById('horasContainer');

    const horaInputs =
        document.querySelectorAll('.hora-input');

    if(todoElDia.checked){

        horasContainer.classList.add('hidden');

        horaInputs.forEach(input => {
            input.value = '';
        });

    }else{

        horasContainer.classList.remove('hidden');
    }
}

document.addEventListener(
    'DOMContentLoaded',
    () => {
        toggleHoras();
    }
);
function openViewModal(card){

    document
        .getElementById("viewModal")
        .classList
        .remove("hidden");

    document
        .getElementById("viewTitulo")
        .innerText =
        card.dataset.titulo;

    document
        .getElementById("viewDescripcion")
        .innerText =
        card.dataset.descripcion || "Sin descripción";

    document
        .getElementById("viewFechaInicio")
        .innerText =
        card.dataset.fechainicio;

    document
        .getElementById("viewFechaFin")
        .innerText =
        card.dataset.fechafin;

    document
        .getElementById("viewHoraInicio")
        .innerText =
        card.dataset.horainicio || "No aplica";

    document
        .getElementById("viewHoraFin")
        .innerText =
        card.dataset.horafin || "No aplica";

    document
        .getElementById("viewUbicacion")
        .innerText =
        card.dataset.ubicacion || "Sin ubicación";

    document
        .getElementById("viewEstado")
        .innerText =
        card.dataset.estado;

    document
        .getElementById("viewTodoDia")
        .innerText =
        card.dataset.todoeldia === "true"
            ? "Sí"
            : "No";
}

function closeViewModal(){

    document
        .getElementById("viewModal")
        .classList
        .add("hidden");
}