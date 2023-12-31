'use strict'

let loginPage = document.querySelector('#login-page')
let registerPage = document.querySelector('#register-page')
let messengerPage = document.querySelector('#messenger-page')
let loginForm = document.querySelector('#loginForm')
let registerForm = document.querySelector('#registerForm')
let messageForm = document.querySelector('#messageForm')
let messageInput = document.querySelector('#messageInput')
let recipientInput = document.querySelector('#recipientInput')
let messageArea = document.querySelector('#messageArea')
let connectingElement = document.querySelector('.connecting')

let username
let sessionId
let stompClient
let privateStompClient

let colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
]

function register(event) {
    username = document.querySelector('#registerUsername').value
    let password = document.querySelector('#registerPassword').value
    let passwordConfirm = document.querySelector('#registerPasswordConfirm').value

    if (!username) {
        console.log("Please enter username")
    } else if (!password || !passwordConfirm) {
        console.log("Please enter password")
    } else if (password !== passwordConfirm) {
        console.log("Passwords don't match")
    } else {
        fetch('/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                password: password,
                passwordConfirm: passwordConfirm
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.code === 201) {
                    console.log(data)
                    connectMessengerWS(username)
                } else {
                    console.log(data.message)
                }
            })
            .catch(error => {
                console.error('Error:', error)
            })
    }
    event.preventDefault()
}

function login(event) {
    username = document.querySelector('#loginUsername').value
    let password = document.querySelector('#loginPassword').value

    if (!username) {
        console.log("Please enter username")
    } else if (!password) {
        console.log("Please enter password")
    } else {
        fetch('/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                password: password,
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.code === 200) {
                    console.log(data)
                    connectMessengerWS()
                } else {
                    console.log(data.message)
                }
            })
            .catch(error => {
                console.error('Error:', error)
            })
    }
    event.preventDefault()
}

function connectMessengerWS() {
    loginPage.classList.add('hidden')
    registerPage.classList.add('hidden')
    messengerPage.classList.remove('hidden')

    let socket = new SockJS('/ws')
    stompClient = Stomp.over(socket)
    stompClient.connect({}, function () {
        stompClient.subscribe('/all/notifications', function (result) {
            onMessage(JSON.parse(result.body))
        })
    }, onError)

    socket = new SockJS('/ws')
    privateStompClient = Stomp.over(socket)
    privateStompClient.connect({}, function () {
        let url = privateStompClient.ws._transport.url;
        url = url.replace(/^ws:\/\/localhost:8080\/ws\/\d+\//, "");
        url = url.replace(/\/websocket$/, "");
        console.log("Your current session is: " + url);
        sessionId = url;
        privateStompClient.subscribe('/private-user' + sessionId, function (result) {
            onMessage(JSON.parse(result.body))
        })
        privateStompClient.send("/app/chat.login", {}, JSON.stringify({username: username}))
    }, onError)

    connectingElement.classList.add('hidden')
}

function onError(error) {
    console.log(error)
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!'
    connectingElement.style.color = 'red'
}


function sendMessage(event) {
    let messageContent = messageInput.value.trim()
    let recipient = recipientInput.value
    if (messageContent && stompClient) {
        let chatMessage = {
            sender: username,
            recipient: recipient,
            content: messageInput.value,
            action: 'MESSAGE'
        }
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage))
        messageInput.value = ''
    }
    event.preventDefault()
}


function onMessage(payload) {
    let messageElement = document.createElement('li')

    console.log(payload)

    // if (data.action === 'LOGIN') {
    //     messageElement.classList.add('event-message')
    //     message.content = message.sender + ' joined!'
    // } else if (message.action === 'REGISTER') {
    //     console.log("register dude")
    // } else if (message.action === 'DISCONNECT') {
    //     messageElement.classList.add('event-message')
    //     message.content = message.sender + ' left!'
    // } else {
    //     messageElement.classList.add('messenger-message')
    //
    //     let avatarElement = document.createElement('i')
    //     let avatarText = document.createTextNode(message.sender[0])
    //     avatarElement.appendChild(avatarText)
    //     avatarElement.style['background-color'] = getAvatarColor(message.sender)
    //
    //     messageElement.appendChild(avatarElement)
    //
    //     let usernameElement = document.createElement('span')
    //     let usernameText = document.createTextNode(message.sender)
    //     usernameElement.appendChild(usernameText)
    //     messageElement.appendChild(usernameElement)
    // }
    //
    // let textElement = document.createElement('p')
    // let messageText = document.createTextNode(message.content)
    // textElement.appendChild(messageText)
    //
    // messageElement.appendChild(textElement)
    //
    // messageArea.appendChild(messageElement)
    // messageArea.scrollTop = messageArea.scrollHeight
}


function getAvatarColor(messageSender) {
    let hash = 0
    for (let i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i)
    }
    let index = Math.abs(hash % colors.length)
    return colors[index]
}

loginForm.addEventListener('submit', login, true)
registerForm.addEventListener('submit', register, true)
messageForm.addEventListener('submit', sendMessage, true)