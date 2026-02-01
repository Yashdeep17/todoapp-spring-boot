document.addEventListener("DOMContentLoaded", function () {

    const taskInput = document.getElementById("taskInput");
    const addBtn = document.getElementById("addBtn");

    function toggleButton() {
        addBtn.disabled = taskInput.value.trim() === "";
    }

    taskInput.addEventListener("input", toggleButton);

    taskInput.focus();
});
// TOGGLE DONE/UNDO
function toggleTask(btn) {
    const li = btn.closest("li");
    const id = li.dataset.id;

    fetch(`/api/tasks/${id}/toggle`, {
        method: "PUT"
    }).then(() => {
        li.classList.toggle("completed");
    });
}


// DELETE TASK
function deleteTask(btn) {
    if (!confirm("Delete this task?")) return;

    const li = btn.closest("li");
    const id = li.dataset.id;

    fetch(`/api/tasks/${id}`, {
        method: "DELETE"
    }).then(() => {
        li.remove();
    });
}

// ===================
// DARK MODE TOGGLE
// ===================

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


// load saved theme on start
window.onload = function () {
    const saved = localStorage.getItem("theme");

    if (saved === "dark") {
        document.body.classList.add("dark");
        document.getElementById("themeBtn").textContent = "☀️ Light";
    }
};

// ===================
// LIVE SEARCH FILTER
// ===================

function searchTasks() {
    const input = document.getElementById("searchInput").value.toLowerCase();
    const tasks = document.querySelectorAll("#taskList li");

    tasks.forEach(task => {
        const text = task.innerText.toLowerCase();

        if (text.includes(input)) {
            task.style.display = "flex";
        } else {
            task.style.display = "none";
        }
    });
}
