document.addEventListener('DOMContentLoaded', () => {
    const nameInput = document.getElementById('fullName');
    const ageInput = document.getElementById('age');
    const designationInput = document.getElementById('designation');
    const dateInput = document.getElementById('joiningDate');

    const previewName = document.getElementById('previewName');
    const previewAge = document.getElementById('previewAge');
    const previewDesignation = document.getElementById('previewDesignation');
    const previewDate = document.getElementById('previewDate');
    const idDisplay = document.getElementById('generatedId');

    idDisplay.textContent = Math.floor(100000 + Math.random() * 900000);

    nameInput.addEventListener('input', () => {
        previewName.textContent = nameInput.value;
    });

    ageInput.addEventListener('input', () => {
        previewAge.textContent = ageInput.value ;
    });

    designationInput.addEventListener('input', () => {
        previewDesignation.textContent = designationInput.value ;
    });

    dateInput.addEventListener('input', () => {
        previewDate.textContent = dateInput.value;
    });
});
