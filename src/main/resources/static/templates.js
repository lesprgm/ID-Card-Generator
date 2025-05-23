document.addEventListener('DOMContentLoaded', () => {
    const templateButtons = document.querySelectorAll('.template-button');
    const colorMap = {
        "Corporate": "#1e3a8a",   // blue
        "School": "#0f766e",      // green
        "Event": "#7c3aed",       // purple
        "Hospital": "#dc2626",    // red
        "Government": "#374151",  // gray
        "Creative": "#f59e0b"     // yellow
    };

    templateButtons.forEach(button => {
        button.addEventListener('click', e => {
            e.preventDefault();

            const card = button.closest('.template-card');
            const title = card.querySelector('.template-header h2').textContent.trim();
            const color = colorMap[title] || "#1e3a8a"; // fallback to default blue

            // Save color to localStorage
            localStorage.setItem("selectedTemplateColor", color);

            // Redirect to form page
            window.location.href = "create.html";
        });
    });
});
