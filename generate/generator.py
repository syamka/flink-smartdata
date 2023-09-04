import socket
import time
from random import randint

from confluent_kafka import Producer
from confluent_kafka.admin import AdminClient, NewTopic


def acked(err, msg):
    if err is not None:
        print("Failed to deliver message: %s: %s" % (str(msg), str(err)))
    else:
        print("Message produced: %s" % (str(msg)))

conf = {'bootstrap.servers': "localhost:9093",
        'client.id': socket.gethostname()}

def wait_ready() -> bool:
    timer: int = 0
    topics = admin_client.list_topics()
    while not topics:
        time.sleep(5)
        timer += 5
        topics = admin_client.list_topics()
        if timer > 60000 * 2:
            return False
    return True

admin_client = AdminClient(conf)
wait_ready()

admin_client.create_topics([NewTopic('example-smartdata', num_partitions=3), NewTopic('example-smartdata-blacklist', num_partitions=1), NewTopic('example-smartdata-result', num_partitions=1)])

time.sleep(1.0)

producer = Producer(conf)

event_str = "{{\"id\":{id}, \"uid\":{uid}, \"ip\":\"{ip}\", \"itemId\":{itemId}, \"timestamp\":{timestamp}}}"
for i in range(1, 1000000):
        json = event_str.format(id=i,
                                uid=randint(1, 100),
                                ip=str(randint(1, 99)) + "." + str(randint(1, 99)) + "." + str(randint(1, 99)) + "." + str(randint(1, 99)),
                                itemId=randint(1, 1000),
                                timestamp=round(time.time()*1000))
        producer.produce('example-smartdata', key=str(round(time.time()*1000)), value=json, callback=acked)
        if i % 100 == 0:
            producer.poll(1)

producer.produce('example-smartdata-blacklist', value="1.1.1.1", callback=acked)
producer.poll(1)
