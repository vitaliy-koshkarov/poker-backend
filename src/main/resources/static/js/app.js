'use strict'

const endPoint = '/ws';
const topic = '/topic/greetings';
const toPath = '/app/hello';
const stompClient = Stomp.over(new SockJS(endPoint));

connectToChat();

function connectToChat() {
    stompClient.connect({}, frame => {
        console.log('Connected to: ' + frame);

// Subscribe to topic from server
        stompClient.subscribe(topic, (message) => {
            const chat = document.getElementById('chat');
            chat.textContent += message.body + '\r\n';
        });
    });
}

function disconnectFromChat() {
    stompClient.disconnect({}, frame => {
        console.log('Disconnected: ' + frame);
    });
}

function sendMessage() {
    const input = document.getElementById('messageInput');
    const text = input.value.trim();
    if (text) {
        stompClient.send(toPath, {}, JSON.stringify(text));
        input.value = '';
    }
}