// DARK MODE
function toggleTheme() {
    const body = document.body;
    const btn = document.getElementById("themeBtn");

    body.classList.toggle("dark");

    if (body.classList.contains("dark")) {
        localStorage.setItem("theme", "dark");
        btn.textContent = "☀️ Light";
    } else {
        localStorage.setItem("theme", "light");
        btn.textContent = "🌙 Dark";
    }
}

window.onload = function () {
    const saved = localStorage.getItem("theme");

    if (saved === "dark") {
        document.body.classList.add("dark");
        document.getElementById("themeBtn").textContent = "☀️ Light";
    }
};


// LIVE SEARCH
function searchTasks() {
    const input = document.getElementById("searchInput").value.toLowerCase();
    const tasks = document.querySelectorAll("#taskList li");

    tasks.forEach(task => {
        const text = task.innerText.toLowerCase();
        task.style.display = text.includes(input) ? "flex" : "none";
    });
}

function addTask(e) {
    e.preventDefault();

    const title = document.querySelector("input[name='title']").value;
    const priority = document.querySelector("select[name='priority']").value;

    fetch("/api/tasks", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `title=${encodeURIComponent(title)}&priority=${priority}`
    })
    .then(res => res.json())
    .then(task => {

        const ul = document.getElementById("taskList");

        const li = document.createElement("li");
        li.dataset.id = task.id;

        li.innerHTML = `
            <span>${task.title}</span>
            <button onclick="toggleTask(this)">Done</button>
            <button onclick="deleteTask(this)" class="delete-btn">Delete</button>
            <span class="badge ${task.priority.toLowerCase()}">${task.priority}</span>
        `;

        ul.prepend(li);

        document.querySelector("input[name='title']").value = "";
    });
}

/* ======================================
   DRAG & DROP SORTING
====================================== */

const sortable = new Sortable(document.getElementById("taskList"), {
    animation: 250,
    ghostClass: "drag-ghost",
    chosenClass: "drag-chosen",
    dragClass: "drag-dragging",

    onEnd: function () {
        saveOrder();
    }
});

/* send new order to backend */
function saveOrder() {
    const ids = [];

    document.querySelectorAll(".task-row").forEach(row => {
        ids.push(row.dataset.id);
    });

    // optional AJAX call
    /*
    fetch("/todos/reorder", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(ids)
    });
    */

    showToast("Order updated");
}


/* ======================================
   ANIMATIONS
====================================== */

/* ADD animation */
function animateAdd(el) {
    el.classList.add("task-enter");
    setTimeout(() => el.classList.remove("task-enter"), 300);
}

/* DELETE animation */
function animateDelete(el) {
    el.classList.add("task-exit");
    setTimeout(() => el.remove(), 250);
}

/* toggle done */
function toggleTask(btn) {
    const row = btn.closest(".task-row");
    row.classList.toggle("completed");
    row.classList.add("pulse");
    setTimeout(()=>row.classList.remove("pulse"),200);
}

/* delete */
function deleteTask(btn) {
    const row = btn.closest(".task-row");
    animateDelete(row);
}


/* ======================================
   TOAST NOTIFICATION
====================================== */

function showToast(text) {
    const toast = document.createElement("div");
    toast.className = "toast";
    toast.innerText = text;

    document.body.appendChild(toast);

    setTimeout(() => toast.classList.add("show"), 10);
    setTimeout(() => toast.remove(), 2000);
}

document.getElementById("addForm").addEventListener("submit", function(e) {
    e.preventDefault(); // 🔥 stops page reload

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
    .then(() => {
        document.getElementById("taskInput").value = "";
        location.reload(); // simple refresh
    });
});
