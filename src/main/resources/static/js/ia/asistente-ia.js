document.addEventListener(
    "DOMContentLoaded",
    () => {

        const btnIA =
            document.getElementById("btnIA");

        const mensajeIA =
            document.getElementById("mensajeIA");

        const respuestaIA =
            document.getElementById(
                "respuestaIA"
            );

        btnIA.addEventListener(
            "click",
            async () => {

                const mensaje =
                    mensajeIA.value.trim();

                if(!mensaje){

                    alert(
                        "Escribe una instrucción"
                    );

                    return;

                }

                btnIA.disabled = true;

                btnIA.innerHTML = `

<i class="fa-solid fa-spinner fa-spin"></i>
Analizando...

`;

                respuestaIA.innerHTML = `

<div class="ia-response-card">

    <div class="loading-container">

    <div class="loading-spinner"></div>

<h3>
    La IA está organizando tus tareas...
</h3>

</div>

</div>

`;

                try{

                    const response =
                        await fetch(
                            "/api/ia/crear-recordatorios",
                            {
                                method:"POST",

                                headers:{
                                    "Content-Type":
                                        "application/json"
                                },

                                body: JSON.stringify({
                                    mensaje: mensaje
                                })
                            }
                        );

                    if(!response.ok){

                        throw new Error(
                            "Error servidor"
                        );

                    }

                    const tareas =
                        await response.json();

                    mostrarTareas(
                        tareas
                    );

                }catch(error){

                    console.error(error);

                    respuestaIA.innerHTML = `

<div class="ia-response-card">

    <h3>
                                ❌ Error IA
</h3>

<div class="ia-response-text">
    No fue posible procesar
    la solicitud.
</div>

</div>

`;

                }finally{

                    btnIA.disabled = false;

                    btnIA.innerHTML = `

<i class="fa-solid fa-wand-magic-sparkles"></i>
Analizar y crear tareas

    `;

                }

            }
        );

        function mostrarTareas(
            tareas
        ){

            if(!tareas.length){

                respuestaIA.innerHTML = `

<div class="ia-response-card">

    <h3>
                            ⚠️ Sin tareas
</h3>

<div class="ia-response-text">
    La IA no generó tareas.
</div>

</div>

`;

                return;

            }

            let html = `

<div class="ia-result-title">
                
                    ✨ ${tareas.length}
tareas creadas correctamente

</div>

    `;

            tareas.forEach(tarea => {

                html += `

<div class="task-card">

    <div class="task-top">

    <h3>
    ${tarea.titulo}
</h3>

<span class="
                                priority-badge
                                ${tarea.prioridad}
                            ">
                                ${tarea.prioridad}
                            </span>

</div>

<p class="task-message">
    ${tarea.mensaje}
</p>

<div class="task-footer">

                            <span>
                                📂 ${tarea.categoria}
                            </span>

    <span>
                                📅 ${formatearFecha(
        tarea.fechaLimite
    )}
                            </span>

</div>

</div>

`;

            });

            respuestaIA.innerHTML = html;

        }

        function formatearFecha(
            fecha
        ){

            if(!fecha){

                return "Sin fecha";

            }

            return new Date(
                fecha
            ).toLocaleString();

        }

    }
);
