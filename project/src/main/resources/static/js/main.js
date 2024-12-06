'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var chatListPage = document.querySelector('#chat-list');
var usersChatList = document.querySelector('#usersChatList');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var imageInput = document.getElementById("imageInput");
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var errorElement = document.querySelector('#error-text');

var stompClient = null;
var username = null;
var pass = null;
var registerCheck = null;
let sse = null; // Declare globally for SSE

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

let image = "";

const reader = new FileReader();
reader.onload = () => {
    const dataURL = reader.result;
    const base64 = reader.result.split(",").pop();
    image = base64;
};

if (imageInput) {
    imageInput.onchange = () => {
        reader.abort();
        reader.readAsDataURL(imageInput.files[0]);
    };
}

function connect(event) {
    username = document.querySelector('#name').value.trim();
    pass = document.querySelector('#pass').value.trim();
    registerCheck = document.querySelector('#register');
    if (registerCheck.checked) {
        let email = prompt("Insert your email to register");
        fetch('http://localhost:8081/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: username, password: pass, email: email })
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Registration failed');
            }
        })
        .catch(error => {
            console.error('Error during registration:', error);
        });
    }
    if (username) {
        fetch('http://localhost:8081/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: username, password: pass })
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else if (response.status === 403) {
                errorElement.classList.remove('hidden');
                errorElement.textContent = "Login failed. Please check your credentials.";
                throw new Error('Login failed');
            } else {
                throw new Error('Login failed');
            }
        })
        .then(data => {
            localStorage.setItem("Refresh", data.Refresh);
            localStorage.setItem("jwt", data.jwt);
            login();
        })
        .catch(error => {
            console.error('Error during login:', error);
        });
    }
    event.preventDefault();
}

function login() {
    console.log(localStorage.getItem("jwt"), "Login successful");

    usernamePage.classList.add('hidden');
    chatListPage.classList.remove('hidden');

    populateList();
    addWebSocket();
    initializeSSE(); // Start SSE connection
}

function initializeSSE() {
    const token = localStorage.getItem("jwt");
    if (!token) {
        console.error("No JWT token found for SSE connection");
        return;
    }

    sse = new EventSource(`http://localhost:8083/notifications/subscribe?token=${token}`);

    sse.onmessage = function(event) {
        const notification = JSON.parse(event.data);
        console.log('Received notification via SSE:', notification);
        renderNotification(notification);
    };

    sse.onerror = function(error) {
        console.error('Error in SSE connection:', error);
        sse.close();
        setTimeout(initializeSSE, 5000); // Reconnect after 5 seconds
    };
}

function renderNotification(notification) {
    var notificationElement = document.createElement('div');
    notificationElement.classList.add('notification');
    notificationElement.textContent = `Notification: ${notification.subject} - ${notification.message}`;

    document.body.appendChild(notificationElement);

    setTimeout(() => {
        notificationElement.remove();
    }, 5000);
}

function populateList() {
    var token = localStorage.getItem("jwt");
    var arrayToken = token.split('.');
    var tokenPayload = JSON.parse(atob(arrayToken[1]));
    var username = tokenPayload?.sub;

    console.log(username, "Fetching user info");

    fetch('http://localhost:8080/getUseId', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: username })
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Failed to get user ID');
        }
    })
    .then(data => {
        console.log("User ID fetched successfully");
        getChatMessages(data);
    })
    .catch(error => console.error('Failed to fetch user ID:', error));
}

function getChatMessages(data) {
    fetch('http://localhost:8080/api/chat', {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Failed to fetch chat messages');
        }
    })
    .then(data => {
        data.forEach(msg => renderMessage(msg));
    })
    .catch(error => console.error('Error fetching chat messages:', error));
}

function renderMessage(messageToLoad) {
    var messageElement = document.createElement('li');

    if (messageToLoad.type === 'CONNECT') {
        messageElement.classList.add('event-message');
        messageToLoad.content = `${messageToLoad.sender} joined!`;
    } else if (messageToLoad.type === 'DISCONNECT') {
        messageElement.classList.add('event-message');
        messageToLoad.content = `${messageToLoad.sender} left!`;
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(messageToLoad.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(messageToLoad.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(messageToLoad.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);

        var textElement = document.createElement('p');
        var messageText = document.createTextNode(messageToLoad.content);
        textElement.appendChild(messageText);
        messageElement.appendChild(textElement);
    }

    if (messageToLoad.media) {
        var imgElement = document.createElement("img");
        imgElement.src = `https://test-cs4337.s3.eu-west-1.amazonaws.com/${messageToLoad.media}`;
        imgElement.className = "mediaImage";
        messageElement.appendChild(imgElement);
    }

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    return colors[Math.abs(hash % colors.length)];
}

usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendMessage, true);
document.addEventListener("DOMContentLoaded", (event) => {
fetch('http://localhost:8081/checkJwtOutside', {
       method: 'POST',
       headers: {
           'Content-Type': 'application/json'
       },
       body: JSON.stringify({ token: String(localStorage.getItem("jwt")) })
   })
   .then(response => {
       if (response.ok) {
           return response.json();
       } else {
           throw new Error('Token invalid. Please log in.');
       }
   })
   .then(data => {
        if (data.isCorrect) {
            username = data.username;
           login();
        }
   })
   .catch(error => {
       console.error('Error during login:', error);
   });

    event.preventDefault();
});
