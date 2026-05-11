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