const html = document.documentElement;

function toggleTheme() {

    const currentTheme = html.getAttribute("data-theme");

    if(currentTheme === "dark"){

        html.setAttribute("data-theme","light");
        localStorage.setItem("theme","light");

    }else{

        html.setAttribute("data-theme","dark");
        localStorage.setItem("theme","dark");

    }

}

const savedTheme = localStorage.getItem("theme");

if(savedTheme){

    html.setAttribute("data-theme", savedTheme);

}else{

    html.setAttribute("data-theme","light");

}