document.addEventListener('DOMContentLoaded', () => {
    const grid = document.getElementById("cardGrid");

    fetch("http://localhost:8080/api/users")
        .then(res => res.json())
        .then(users => {
            users.forEach(user => {
                const card = document.createElement("div");
                card.className = "card";

                card.innerHTML = `
          <img src="${user.profilePicturePath}" alt="Profile"/>

          <div>
            <h2>${user.fullName}</h2>
            <p><strong>Designation:</strong> ${user.designation}</p>
            <p><strong>Age:</strong> ${user.age}</p>
            <p><strong>Join Date:</strong> ${user.joiningDate}</p>
          </div>
        `;

                grid.appendChild(card);
            });
        });
});
