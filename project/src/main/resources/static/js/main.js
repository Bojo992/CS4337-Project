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

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

let image = "";

const reader = new FileReader;
reader.onload = () => {
    const dataURL = reader.result;
    const base64 = reader.result.split(",").pop();
    image = base64;
}
imageInput.onchange = () => {
    reader.abort();
    reader.readAsDataURL(imageInput.files[0]);
}


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
                   }
                   else if (response.status = 403){
                        errorElement.classList.remove('hidden');
                        errorElement.textContent = response.json();
                        throw new Error('Login failed, response 403');
                   }
                    else {
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
     console.log(localStorage.getItem("jwt"), "bojo test")

       // If login is successful, proceed to connect to WebSocket
       usernamePage.classList.add('hidden');
       chatListPage.classList.remove('hidden');

       populateList();
       addWebSocket();
}

function loginOld() {
    usernamePage.classList.add('hidden');
    addWebSocket();
}

function populateList() {
    var token = localStorage.getItem("jwt")
    var arrayToken = token.split('.')
    var tokenPayload = JSON.parse(atob(arrayToken[1]))
    var username = tokenPayload?.sub

    console.log(username, "bojo test")

    fetch('http://localhost:8080'+'/getUseId', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username: username})
    }).then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('failed to get userId');
        }
    }).then(data => {
        console.log("populateList bojo")
       getChatMessages(data)
    })
}

function getUserChats(data) {
    fetch('http://localhost:8080'+'/getAllChatsForUser', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ userId: data.userId})
    }).then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Failed to get chats for user');
        }
    }).then(data => {
        console.log("getUserChats bojo")
       createList(data);
    })
}

function getChatMessages(data) {
    fetch('http://localhost:8080'+'/api/chat', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        }).then(response => {
        if (response.ok) {
            return response.json();
        }
        else {
            throw new Error('Failed to get chats for user');
        }
    }).then(data => {
       console.log("getUserChats ROY" + data)
      // createList(data);
      data.forEach(msg => {renderMessage(msg)})
    })
}

function createList(data) {
    data.chats.forEach((item) => {
        console.log(item.chatId, "chat id bojo")

        let btn = document.createElement("button")
        btn.innerText = item.chatName;
        btn.id = item.chatId
        btn.addEventListener('click', addWebSocket)

        let li = document.createElement("li");
        li.appendChild(btn)
        usersChatList.appendChild(li);
    });
}

function addWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    chatListPage.classList.add('hidden')
    chatPage.classList.remove('hidden')

    stompClient.connect({}, onConnected, onError);
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

async function sendMessage(event) {
    event.preventDefault(); // Prevent form submission reload

    var messageContent = messageInput.value.trim();
    let chatMessage = {
        sender: username,
        type: 'CHAT',
        content: '',
    };

    if (imageInput.value) {
        const imageFileId = await getImage(); // Await the image upload
        if (imageFileId) {
            chatMessage.media = imageFileId; // Attach the uploaded image ID
        } else {
            console.error("Image upload failed");
            return; // Stop if the image upload fails
        }
    }

    if (messageContent) {
        chatMessage.content = messageContent; // Add message text if available
    }

    if (stompClient) {
        stompClient.send("/app/chat.sendMsg", {}, JSON.stringify(chatMessage));
    }

    messageInput.value = ''; // Clear message input
    imageInput.value = ''; // Clear image input
}

async function getImage() {
    let body = { "base64Data": image };
    const url = "http://localhost:8084/uploadObj";
    let resultImgId = '';

    try {
        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(body),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const responseData = await response.json(); // Parse the response JSON
        resultImgId = responseData.filename; // Extract the filename
    } catch (error) {
        console.error("Error occurred during image upload:", error);
    }

    return resultImgId; // Return the uploaded image ID (or filename)
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    renderMessage(message)
}

function renderMessage(messageToLoad) {
    console.log(messageToLoad)
    var messageElement = document.createElement('li');

    if(messageToLoad.type === 'CONNECT') {
        messageElement.classList.add('event-message');
        messageToLoad.content = messageToLoad.sender + ' joined!';
    } else if (messageToLoad.type === 'DISCONNECT') {
        messageElement.classList.add('event-message');
        messageToLoad.content = messageToLoad.sender + ' left!';
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
        messageElement.appendChild(document.createElement('br'));
        var sentAtElement = document.createElement('span');
        sentAtElement.classList.add("timestamp")
        var sentAtText = document.createTextNode(messageToLoad.sentAt);
        sentAtElement.appendChild(sentAtText);
        messageElement.appendChild(sentAtElement);
    }
    var textElement = document.createElement('p');
    var messageText = document.createTextNode(messageToLoad.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    var imgElement = document.createElement("img");
    if (messageToLoad.media) {
        var link = "https://test-cs4337.s3.eu-west-1.amazonaws.com/"
        imgElement.src = link + messageToLoad["media"];
        imgElement.ariaPlaceholder = "File id" + messageToLoad["media"];
        imgElement.className = "mediaImage";
    }

    messageArea.appendChild(messageElement);
    messageArea.appendChild(imgElement);
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
document.addEventListener("DOMContentLoaded", (event) => {
fetch('http://localhost:8081'+'/checkJwtOutside', {
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
           // If login is successful, proceed to connect to WebSocket
           login();
        }
   })
   .catch(error => {
       console.error('Error during login:', error);
   });

    event.preventDefault();
});