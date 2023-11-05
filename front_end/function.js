const fields = {}; // Initialize an object to store key-value pairs
const sendDataButton = document.getElementById("sendData");

// Hide the "Send Data" button initially
sendDataButton.style.display = "none";

document.getElementById("addField").addEventListener("click", function () {
    // Create a new key-value input field pair
    console.log("Prabhat");
    const inputFieldContainer = document.createElement("div");
    inputFieldContainer.classList.add("input-field");

    const keyInput = document.createElement("input");
    keyInput.type = "text";
    keyInput.classList.add("form-control", "key");
    keyInput.placeholder = "Field Key";

    const valueInput = document.createElement("input");
    valueInput.type = "text";
    valueInput.classList.add("form-control", "value");
    valueInput.placeholder = "Field Value";

    inputFieldContainer.appendChild(keyInput);
    inputFieldContainer.appendChild(valueInput);

    // Append the new input fields to the container
    const additionalFieldsContainer = document.getElementById("additionalFieldsContainer");
    additionalFieldsContainer.appendChild(inputFieldContainer);

    // Show the "Send Data" button
    sendDataButton.style.display = "block";
});





sendDataButton.addEventListener("click", function () {
    // Create an object to store the key-value pairs
    const data = {};

    console.log("Prabhat")
    // Select all key and value input fields
    const keyInputs = document.querySelectorAll(".key");
    const valueInputs = document.querySelectorAll(".value");

    // Check if there are key and value pairs
    if (keyInputs.length === valueInputs.length) {
        for (let i = 0; i < keyInputs.length; i++) {
            const key = keyInputs[i].value;
            const value = valueInputs[i].value;
            
            // Check if both key and value are provided
            if (key && value) {
                data[key] = value;
            } else {
                alert("Please enter both a key and a value for all pairs.");
                return;
            }
        }

	console.log(data);
        // Send the data object as JSON to the API
        fetch("http://localhost:8080/api/database/mapping", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
		"Accept": "application/json",
		"Access-Control-Allow-Origin": "http://localhost:8080",
		"Access-Control-Allow-Credentials": "true",
            },
            body: JSON.stringify(data),
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            return response.json();
        })
        .then(data => {
            // Handle the response from the API
            console.log(data);

            // Assuming you have a message in the response
            alert(data.message);
        })
        .catch(error => {
            console.error("API Error:", error);

            // Handle the error, display an alert, or update your UI as needed
            alert("API Error: " + error.message);
        });
    } else {
        alert("Please enter key and value pairs.");
    }
});
