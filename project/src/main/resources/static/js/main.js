'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;
var pass = null;
var registerCheck = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();
    pass = document.querySelector('#pass').value.trim();
    registerCheck = document.querySelector('#register');
    if (registerCheck.checked) {
       let email = prompt("Insert your email to register");
       fetch('http://localhost:8081'+'/register', {
              method: 'POST',
              headers: {
                  'Content-Type': 'application/json'
              },
              body: JSON.stringify({ username: username, password: pass, email: email })
          })
          .then(response => {
              if (response.ok) {
                  return response.json();
              } else {
                  throw new Error('registration failed');
              }
          })
          .catch(error => {
              console.error('Error during registration:', error);
          });
    }
    if(username) {
       fetch('http://localhost:8081'+'/login', {
                   method: 'POST',
                   headers: {
                       'Content-Type': 'application/json'
                   },
                   body: JSON.stringify({ username: username, password: pass })
               })
               .then(response => {
                   if (response.ok) {
                       return response.json();
                   } else {
                       throw new Error('Login failed');
                   }
               })
               .then(data => {
                    localStorage.setItem("Refresh", data.Refresh);
                    localStorage.setItem("jwt", data.jwt);
                   // If login is successful, proceed to connect to WebSocket
                   usernamePage.classList.add('hidden');
                   chatPage.classList.remove('hidden');

                   const socket = new SockJS('/ws');
                   stompClient = Stomp.over(socket);

                   stompClient.connect({}, onConnected, onError);
               })
               .catch(error => {
                   console.error('Error during login:', error);
               });
    }
    event.preventDefault();
}


function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);
    //stompClient.subscribe('/user/'+username+'/private', onPMReceived)
    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'CONNECT'})
    )

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMsg", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'CONNECT') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'DISCONNECT') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
        messageElement.appendChild(document.createElement('br'));
        var sentAtElement = document.createElement('span');
        sentAtElement.classList.add("timestamp")
        var sentAtText = document.createTextNode(message.sentAt);
        sentAtElement.appendChild(sentAtText);
        messageElement.appendChild(sentAtElement);
    }
    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)