#example script to send data to a java application listening to localhost:6666
import socket
import time
from random import random

destination_ip = socket.gethostbyname("localhost")#"127.0.0.1"
destination_port = 6666

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM, 0)
sock.connect((destination_ip, destination_port))
print(destination_ip,destination_port)
while True:
    time.sleep(1)
    number = random()
    sock.send((str(number) + "\n").encode(encoding="utf8")) # +"\n" required since the java application listens to input line by line

