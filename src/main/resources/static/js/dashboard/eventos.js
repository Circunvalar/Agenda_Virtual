let createChanged = false;
let editChanged = false;

const createForm =
    document.getElementById('createForm');

const editForm =
    document.getElementById('editForm');

function parseInvitados(value){

    if(!value){
        return [];
    }

    return value
        .split(',')
        .map(item => item.trim())
        .filter(Boolean);

}

function setInvitados(form, invitadosValue){

    if(!form){
        return;
    }

    const invitados = parseInvitados(invitadosValue);

    const checkboxes =
        form.querySelectorAll('input[name="invitadosIds"]');

    checkboxes.forEach(checkbox => {

        checkbox.checked = invitados.includes(checkbox.value);

    });

}

createForm.addEventListener(
    'input',
    () => createChanged = true
);

editForm.addEventListener(
    'input',
    () => editChanged = true
);

function openCreateModal(){

    if(createForm){
        createForm.reset();
        setInvitados(createForm, '');
        createChanged = false;
    }

    document.getElementById(
        'createModal'
    ).style.display = 'flex';

}

function openEditModal(button){

    const modal =
        document.getElementById('editModal');

    const id =
        button.dataset.id;

    editForm.action =
        `/eventos/update/${id}`;

    document.getElementById(
        'editTitulo'
    ).value = button.dataset.titulo;

    document.getElementById(
        'editDescripcion'
    ).value = button.dataset.descripcion;

    document.getElementById(
        'editFechaInicio'
    ).value = button.dataset.fechainicio;

    document.getElementById(
        'editFechaFin'
    ).value = button.dataset.fechafin;

    setValue('editHoraInicio', button.dataset.horainicio || '');
    setValue('editHoraFin', button.dataset.horafin || '');
    setValue('editRecordarAntes', button.dataset.recordarantes || 30);

    document.getElementById(
        'editUbicacion'
    ).value = button.dataset.ubicacion || '';

    document.getElementById(
        'editColor'
    ).value = button.dataset.color;

    document.getElementById(
        'editEstado'
    ).value = button.dataset.estado;

    const checkbox =
        document.getElementById('editTodoElDia');

    checkbox.checked =
        button.dataset.todoeldia === 'true';

    toggleHoras(checkbox);

    setInvitados(editForm, button.dataset.invitados);

    modal.style.display = 'flex';

}

function attemptCloseModal(modalId){

    if(
        modalId === 'createModal'
        && createChanged
    ){

        const confirmClose =
            confirm(
                'Tienes cambios sin guardar. ¿Deseas salir?'
            );

        if(!confirmClose){
            return;
        }

    }

    if(
        modalId === 'editModal'
        && editChanged
    ){

        const confirmClose =
            confirm(
                'Tienes cambios sin guardar. ¿Deseas salir?'
            );

        if(!confirmClose){
            return;
        }

    }

    document.getElementById(
        modalId
    ).style.display = 'none';

}

window.onclick = function(event){

    const createModal =
        document.getElementById('createModal');

    const editModal =
        document.getElementById('editModal');

    if(event.target === createModal){

        attemptCloseModal('createModal');

    }

    if(event.target === editModal){

        attemptCloseModal('editModal');

    }

}

function toggleHoras(checkbox){

    const modal =
        checkbox.closest('.modal');

    const horas =
        modal.querySelectorAll('.horas-container');

    horas.forEach(hora => {

        if(checkbox.checked){

            hora.classList.add('hidden');

        }else{

            hora.classList.remove('hidden');

        }

    });

}

document
    .querySelectorAll('input[name="todoElDia"]')
    .forEach(checkbox => {

        toggleHoras(checkbox);

        checkbox.addEventListener('change', function(){

            toggleHoras(this);

        });

    });

function setValue(id, value){
    document.getElementById(id).value = value;
}
