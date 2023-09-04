import time
import docker

dockerclient = docker.DockerClient(base_url='unix://var/run/docker.sock', timeout=120, version='auto')

# сначала стартуем зукипер
dockerclient.containers.run(
    'confluentinc/cp-zookeeper:7.4.1',
    '',
    name='zookeeper',
    hostname='zookeeper',
    auto_remove=True,
    detach=True,
    oom_kill_disable=True,
    remove=True,
    network='integration',
    environment = {
        'ZOOKEEPER_SERVER_ID': 1,
        'ZOOKEEPER_CLIENT_PORT': 2181,
        'ZOOKEEPER_TICK_TIME': 5000,
        'ZOOKEEPER_SYNC_LIMIT': 2,
        'ZOOKEEPER_INIT_LIMIT': 6,
    },
    ports = {
        '2181/tcp': 2181,
    }
)

time.sleep(1.0)

# а потом кафку
dockerclient.containers.run(
    'confluentinc/cp-kafka:7.4.1',
    '',
    name='kafka',
    hostname='kafka',
    auto_remove=True,
    detach=True,
    oom_kill_disable=True,
    remove=True,
    network='integration',
    environment = {
        'KAFKA_BROKER_ID': '1',
        'KAFKA_ZOOKEEPER_CONNECT': 'zookeeper:2181',
        'KAFKA_ADVERTISED_LISTENERS': 'PLAINTEXT://kafka:9092,CONNECTIONS_FROM_HOST://localhost:9093',
        'KAFKA_LISTENER_SECURITY_PROTOCOL_MAP': 'PLAINTEXT:PLAINTEXT,CONNECTIONS_FROM_HOST:PLAINTEXT',
        'KAFKA_INTER_BROKER_LISTENER_NAME': 'PLAINTEXT',
        'KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR': '1'
    },
    ports = {
        '9092/tcp': 9092,
        '9093/tcp': 9093,
    },
    ** {
        'mem_limit': '550m',
        'mem_reservation': '500m',
        'mem_swappiness': 0,
        'nano_cpus': int(1 * 1000000000),
    }
)
