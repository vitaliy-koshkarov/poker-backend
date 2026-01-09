'use strict'

const endPoint = '/ws';
const topic = '/topic/messages';
const appDestinationPrefix = '/app';
const method = '/chat';
var stompClient = null;

function connectToChat() {
    stompClient = Stomp.over(new SockJS(endPoint));

    stompClient.connect({}, frame => {
        console.log('Connected to: ' + frame);

        stompClient.subscribe(topic, (message) => {
            const chat = document.getElementById('chat');
            chat.textContent += message.body + '\r\n';
        });
    });

    changeState('connectBtn');
    changeState('disconnectBtn');
    changeState('messageInput');
    changeState('sendBtn');

    console.log('Connected');
}

function disconnectFromChat() {
    stompClient.disconnect();

    changeState('connectBtn');
    changeState('disconnectBtn');
    changeState('messageInput');
    changeState('sendBtn');

    console.log('Disconnected');
}

function sendMessage() {
    const input = document.getElementById('messageInput');
    const text = input.value.trim();
    if (text) {
        stompClient.send(appDestinationPrefix + method, {}, JSON.stringify(text));
        input.value = '';
    }
}

function changeState(elementId) {
    let elem = document.getElementById(elementId);
    elem.disabled = !elem.disabled;
}
