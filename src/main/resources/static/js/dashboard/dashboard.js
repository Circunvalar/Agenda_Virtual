const html = document.documentElement;

if(localStorage.getItem("theme") === "light"){
    html.classList.add("light");
}

function toggleTheme(){

    html.classList.toggle("light");

    if(html.classList.contains("light")){
        localStorage.setItem("theme","light");
    }else{
        localStorage.setItem("theme","dark");
    }

}