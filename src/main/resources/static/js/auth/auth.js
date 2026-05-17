function toggleTheme(){

    const html = document.documentElement;

    const currentTheme = html.getAttribute("data-theme");

    if(currentTheme === "dark"){

        html.setAttribute("data-theme","light");

        localStorage.setItem("theme","light");

    }else{

        html.setAttribute("data-theme","dark");

        localStorage.setItem("theme","dark");

    }

}

window.addEventListener("DOMContentLoaded", () => {

    const savedTheme = localStorage.getItem("theme");

    if(savedTheme){

        document.documentElement.setAttribute(
            "data-theme",
            savedTheme
        );

    }else{

        document.documentElement.setAttribute(
            "data-theme",
            "dark"
        );

    }

});