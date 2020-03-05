import socketio

sio = socketio.Client()

@sio.on('connect')
def on_connect():
    print('connection established')
    sio.emit('alert', {"data":"bleh"})

@sio.on('disconnect')
def on_disconnect():
    print('disconnected from server')

sio.connect('http://192.168.137.1:10000')
sio.wait()