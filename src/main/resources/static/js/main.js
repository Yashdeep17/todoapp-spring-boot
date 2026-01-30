const taskInput = document.getElementById("taskInput");
const addBtn = document.getElementById("addBtn");

function toggleButton() {
    addBtn.disabled = taskInput.value.trim() === "";
}

taskInput.addEventListener("input", toggleButton);
window.onload = () => {
    taskInput.focus();
};
