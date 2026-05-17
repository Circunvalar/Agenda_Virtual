const html = document.documentElement;

function initializeTheme(){

    const savedTheme = localStorage.getItem("theme");

    if(savedTheme === "dark"){
        html.classList.add("dark");
    }

}

function toggleTheme() {

    document.documentElement.classList.toggle('dark');

    if(document.documentElement.classList.contains('dark')){

        localStorage.setItem('theme', 'dark');

    }else{

        localStorage.setItem('theme', 'light');

    }
}

initializeTheme();