def PROJECT_GIT_REPOSITORY = 'https://github.com/opendatalabcz/graphwiki-services.git'
def GIT_CREDENTIALS = 'github_GregerTomas'
def SERVICES = ["comment-service", "graph-service", "task-service", "user-service"]

node {
    stage('GIT checkout') {
        git(
            url: PROJECT_GIT_REPOSITORY,
            credentialsId: GIT_CREDENTIALS
        )
    }

    stage('Build') {
        sh 'mvn clean package'
    }

    stage('DOCKER images') {
        for(service in SERVICES) {
            sh "docker build -t graphwiki-${service}-image ${service}"
        }
    }

    stage('DOCKER run containers') {
        for(service in SERVICES) {
            sh "docker container stop graphwiki-${service}-container > /dev/null 2>&1 && echo \"Container stopped\" || echo \"Nothing to stop\""
            sh "docker container rm graphwiki-${service}-container > /dev/null 2>&1 && echo \"Container removed\" || echo \"Nothing to remove\""
            // use host network, because PostgreSQL and JanusGraph are running locally
            sh "docker run --name graphwiki-${service}-container -d --network=host graphwiki-${service}-image"
        }
    }
}
