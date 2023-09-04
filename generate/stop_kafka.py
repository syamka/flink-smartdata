import docker

dockerclient = docker.DockerClient(base_url='unix://var/run/docker.sock', timeout=120, version='auto')
for container in dockerclient.containers.list(all=True):
    if container.name in ['zookeeper', 'kafka']:
        container.stop()