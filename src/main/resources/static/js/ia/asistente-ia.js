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

        const accionIA =
            document.getElementById("iaAccion");

        const preferenciasIA =
            document.getElementById("preferenciasIA");

        const acciones = {
            "crear-recordatorios": {
                endpoint: "/api/ia/crear-recordatorios",
                tipo: "recordatorios",
                textoBoton: "Analizar y crear tareas",
                textoCargando: "La IA está organizando tus tareas..."
            },
            "chat": {
                endpoint: "/api/ia/chat",
                tipo: "texto",
                textoBoton: "Enviar al chat",
                textoCargando: "La IA está respondiendo..."
            },
            "plan-diario": {
                endpoint: "/api/ia/plan-diario",
                tipo: "texto",
                textoBoton: "Generar plan diario",
                textoCargando: "La IA está creando tu plan diario..."
            },
            "plan-semanal": {
                endpoint: "/api/ia/plan-semanal",
                tipo: "texto",
                textoBoton: "Generar plan semanal",
                textoCargando: "La IA está creando tu plan semanal..."
            },
            "priorizar": {
                endpoint: "/api/ia/priorizar",
                tipo: "texto",
                textoBoton: "Priorizar tareas",
                textoCargando: "La IA está priorizando tus tareas..."
            },
            "tareas-nl": {
                endpoint: "/api/ia/tareas-nl",
                tipo: "tareas-nl",
                textoBoton: "Aplicar acciones en tareas",
                textoCargando: "La IA está interpretando acciones..."
            }
        };

        function actualizarTextoBoton(){

            const config =
                acciones[accionIA.value];

            if(config){
                btnIA.innerHTML = `

<i class="fa-solid fa-wand-magic-sparkles"></i>
${config.textoBoton}

`;
            }

        }

        accionIA.addEventListener(
            "change",
            actualizarTextoBoton
        );

        actualizarTextoBoton();

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

                const accionConfig =
                    acciones[accionIA.value];

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
    ${accionConfig.textoCargando}
</h3>

</div>

</div>

`;

                try{

                    const body = {
                        mensaje: mensaje
                    };

                    if(accionIA.value !== "crear-recordatorios"){
                        const preferencias =
                            preferenciasIA.value.trim();

                        if(preferencias){
                            body.preferencias = preferencias;
                        }
                    }

                    const response =
                        await fetch(
                            accionConfig.endpoint,
                            {
                                method:"POST",

                                headers:{
                                    "Content-Type":
                                        "application/json"
                                },

                                body: JSON.stringify(body)
                            }
                        );

                    if(!response.ok){

                        throw new Error(
                            "Error servidor"
                        );

                    }

                    const data =
                        await response.json();

                    if(accionConfig.tipo === "recordatorios"){

                        mostrarTareas(
                            data
                        );

                    }else if(accionConfig.tipo === "tareas-nl"){

                        mostrarResultadoTareasNL(
                            data
                        );

                    }else{

                        mostrarRespuestaTexto(
                            data && data.respuesta
                                ? data.respuesta
                                : ""
                        );

                    }

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

                    actualizarTextoBoton();

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

        function mostrarRespuestaTexto(
            texto
        ){

            const contenido =
                texto && texto.trim()
                    ? texto.trim()
                    : "Sin respuesta";

            respuestaIA.innerHTML = `

<div class="ia-response-card">

    <h3>
        ✅ Respuesta IA
    </h3>

    <div class="ia-response-text">
        ${contenido}
    </div>

</div>

`;

        }

        function mostrarResultadoTareasNL(
            resultado
        ){

            const creadas =
                resultado && resultado.creadas
                    ? resultado.creadas
                    : [];

            const actualizadas =
                resultado && resultado.actualizadas
                    ? resultado.actualizadas
                    : [];

            const completadas =
                resultado && resultado.completadas
                    ? resultado.completadas
                    : [];

            const eliminadas =
                resultado && resultado.eliminadas
                    ? resultado.eliminadas
                    : [];

            const advertencias =
                resultado && resultado.advertencias
                    ? resultado.advertencias
                    : [];

            let html = `

<div class="ia-response-card">

    <h3>
        ✅ Resultado de acciones
    </h3>

    <div class="ia-response-text">
        Creadas: ${creadas.length}\n
        Actualizadas: ${actualizadas.length}\n
        Completadas: ${completadas.length}\n
        Eliminadas: ${eliminadas.length}
    </div>

</div>

`;

            if(creadas.length){
                html += renderListaTareas(
                    "Tareas creadas",
                    creadas
                );
            }

            if(actualizadas.length){
                html += renderListaTareas(
                    "Tareas actualizadas",
                    actualizadas
                );
            }

            if(completadas.length){
                html += renderListaSimple(
                    "Tareas completadas",
                    completadas
                );
            }

            if(eliminadas.length){
                html += renderListaSimple(
                    "Tareas eliminadas",
                    eliminadas
                );
            }

            if(advertencias.length){
                html += renderListaSimple(
                    "Advertencias",
                    advertencias
                );
            }

            respuestaIA.innerHTML = html;

        }

        function renderListaTareas(
            titulo,
            tareas
        ){

            let html = `

<div class="ia-response-card">
    <h3>
        ${titulo}
    </h3>
    <div class="ia-response-text">
`;

            tareas.forEach(tarea => {

                html += `
- ${tarea.titulo || "Sin titulo"} | prioridad: ${tarea.prioridad || "MEDIA"} | limite: ${tarea.fechaLimite || "Sin fecha"}\n`;

            });

            html += `
    </div>
</div>

`;

            return html;

        }

        function renderListaSimple(
            titulo,
            items
        ){

            let html = `

<div class="ia-response-card">
    <h3>
        ${titulo}
    </h3>
    <div class="ia-response-text">
`;

            items.forEach(item => {
                html += `- ${item}\n`;
            });

            html += `
    </div>
</div>

`;

            return html;

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
