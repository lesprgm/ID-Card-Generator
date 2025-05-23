document.addEventListener('DOMContentLoaded', () => {
    const grid = document.getElementById("cardGrid");

    fetch("http://localhost:8080/api/users")
        .then(res => res.json())
        .then(users => {
            users.forEach(user => {
                const card = document.createElement("div");
                card.className = "card";
                console.log("User object:", user);

                const profileImage = `<img src="${user.profilePicturePath}" alt="Profile"/>`;
                const selectedColor = localStorage.getItem("selectedTemplateColor") || "#1e3a8a";
                const encodedColor = encodeURIComponent(selectedColor);


                const content = `
          ${profileImage}
          <div>
            <h2>${user.fullName}</h2>
            <p><strong>Designation:</strong> ${user.designation}</p>
            <p><strong>Age:</strong> ${user.age}</p>
            <p><strong>Join Date:</strong> ${user.joiningDate}</p>
            <a href="/api/users/${user.id}/pdf?templateColor=${encodedColor}" class="download-button" download>
              Download PDF
            </a>
          </div>
        ` ;

                card.innerHTML = content;
                grid.appendChild(card);
            });
        })
        .catch(error => {
            console.error("Failed to fetch users:", error);
        });
});
