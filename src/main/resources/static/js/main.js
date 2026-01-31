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
