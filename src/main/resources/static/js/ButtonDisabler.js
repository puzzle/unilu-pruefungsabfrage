document.addEventListener("DOMContentLoaded", function () {
    const examNumberInput = document.getElementById("examNumber");
    const submitButton = document.getElementById("submitButton");
    function updateButtonState() {
        const examNumber = examNumberInput.value;
        submitButton.disabled = examNumber === "";
    }
    examNumberInput.addEventListener("input", updateButtonState);
    updateButtonState();
});
