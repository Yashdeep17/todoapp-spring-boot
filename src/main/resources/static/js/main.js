/* ==============================
   THEME
============================== */

function toggleTheme() {
    const body = document.body;
    const btn = document.getElementById("themeBtn");

    document.body.classList.toggle("dark");

    if (body.classList.contains("dark")) {
        localStorage.setItem("theme", "dark");
        btn.textContent = "☀️ Light";
    } else {
        localStorage.setItem("theme", "light");
        btn.textContent = "🌙 Dark";
    }
}

window.onload = function () {
    if (localStorage.getItem("theme") === "dark") {
        document.body.classList.add("dark");
        document.getElementById("themeBtn").textContent = "☀️ Light";
    }
};


/* ==============================
   SEARCH
============================== */

function searchTasks() {
    const input = document.getElementById("searchInput").value.toLowerCase();
    const tasks = document.querySelectorAll("#taskList li");

    tasks.forEach(task => {
        task.style.display = task.innerText.toLowerCase().includes(input)
            ? "flex"
            : "none";
    });
}


/* ==============================
   ADD TASK (AJAX)
============================== */

document.getElementById("addForm").addEventListener("submit", function (e) {
    e.preventDefault();

    const title = document.getElementById("taskInput").value.trim();
    const priority = document.getElementById("prioritySelect").value;

    if (!title) return;

    fetch("/api/tasks", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            title: title,
            priority: priority
        })
    })
    .then(res => res.json())
    .then(task => {

        const ul = document.getElementById("taskList");

        const li = document.createElement("li");
        li.className = "task-row";
        li.dataset.id = task.id;

        li.innerHTML = `
            <span class="task-title">${task.title}</span>

            <div class="task-actions">
                <button class="icon-btn done-btn" onclick="toggleTask(this)">✔</button>
                <button class="icon-btn delete-btn" onclick="deleteTask(this)">🗑</button>
                <span class="badge ${task.priority.toLowerCase()}">${task.priority}</span>
            </div>
        `;

        ul.prepend(li);

        document.getElementById("taskInput").value = "";
    });
});


/* ==============================
   TOGGLE TASK (AJAX)
============================== */

function toggleTask(btn) {
    const row = btn.closest(".task-row");
    const id = row.dataset.id;

    fetch(`/api/tasks/${id}/toggle`, {
        method: "PUT"
    }).then(() => {
        row.classList.toggle("completed");
    });
}


/* ==============================
   DELETE TASK (AJAX)
============================== */

function deleteTask(btn) {
    const row = btn.closest(".task-row");
    const id = row.dataset.id;

    fetch(`/api/tasks/${id}`, {
        method: "DELETE"
    }).then(() => {
        row.remove();
    });
}

/* ======================================
   INLINE EDIT FEATURE
====================================== */

function enableEdit(span) {

    const row = span.closest(".task-row");
    const taskId = row.dataset.id;

    const oldText = span.innerText;

    const input = document.createElement("input");
    input.type = "text";
    input.value = oldText;
    input.className = "inline-input";

    span.replaceWith(input);

    input.focus();
    input.select();

    /* SAVE FUNCTION */
    function save() {
        const newText = input.value.trim();

        if (!newText) {
            cancel();
            return;
        }

        fetch(`/api/tasks/${taskId}?title=${encodeURIComponent(newText)}`, {
            method: "PUT"
        })
        .then(res => res.json())
        .then(task => {
            const newSpan = document.createElement("span");
            newSpan.className = "task-title";
            newSpan.innerText = task.title;
            newSpan.ondblclick = () => enableEdit(newSpan);

            input.replaceWith(newSpan);
        });
    }

    /* CANCEL */
    function cancel() {
        const newSpan = document.createElement("span");
        newSpan.className = "task-title";
        newSpan.innerText = oldText;
        newSpan.ondblclick = () => enableEdit(newSpan);

        input.replaceWith(newSpan);
    }

    /* EVENTS */
    input.addEventListener("blur", save);

    input.addEventListener("keydown", (e) => {
        if (e.key === "Enter") save();
        if (e.key === "Escape") cancel();
    });

    /* ======================================
   PRIORITY INLINE EDIT ONLY
====================================== */

function editPriority(badge) {

    const row = badge.closest(".task-row");
    const id = row.dataset.id;

    const oldPriority = badge.innerText;

    const select = document.createElement("select");
    select.className = "inline-select";

    ["HIGH", "MEDIUM", "LOW"].forEach(p => {
        const option = document.createElement("option");
        option.value = p;
        option.text = p;
        if (p === oldPriority) option.selected = true;
        select.appendChild(option);
    });

    badge.replaceWith(select);
    select.focus();

    function save() {

        const newPriority = select.value;

        fetch(`/api/tasks/${id}/priority?priority=${newPriority}`, {
            method: "PUT"
        })
        .then(res => res.json())
        .then(task => {

            const newBadge = document.createElement("span");
            newBadge.className = "badge " + task.priority.toLowerCase();
            newBadge.innerText = task.priority;
            newBadge.ondblclick = () => editPriority(newBadge);

            select.replaceWith(newBadge);
        });
    }

    select.onblur = save;

    select.onkeydown = (e) => {
        if (e.key === "Enter") save();
        if (e.key === "Escape") select.replaceWith(badge);
    };
}

}
