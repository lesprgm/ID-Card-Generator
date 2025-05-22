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

    const form = document.getElementById('userForm');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData(form);

        try {
            const response = await fetch('http://localhost:8080/api/users/upload', {
                method: 'POST',
                body: formData
            });

            const result = await response.text();
            alert(result);
        } catch (err) {
            alert('Something went wrong: ' + err.message);
        }
    });

    const photoInput = document.getElementById('profilePicture');
    const photoPreview = document.getElementById('photoPreview');

    photoInput.addEventListener('change', () => {
        const file = photoInput.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (e) => {
                photoPreview.innerHTML = `<img src="${e.target.result}" alt="preview" style="width: 80px; height: 80px; border-radius: 50%;" />`;
            };
            reader.readAsDataURL(file);
        } else {
            photoPreview.innerHTML = "No Photo";
        }
    });

});
