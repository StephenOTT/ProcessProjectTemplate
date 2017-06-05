pipeline {
  agent any
  stages {
    stage('Deploy.json Review') {
      steps {
        script {
          def exists = fileExists 'deploy.json'
          
          if (exists) {
            echo 'deploy.json found'
          } else {
            error("deploy.json cannot be found")
          }
        }
        
        script {
          def props = readJSON file: 'deploy.json'
          echo props.toString()
        }
        
        script {
          def payload = """
          {"deployment-name": "My Deployment",
          "enable-duplicate-filtering": false,
          "deploy-changed-only": false,
          "deployment-source": "Automated Deployment",
          "tenant-id": "My Tenant"}
          """
          
          def response = httpRequest acceptType: 'APPLICATION_JSON', httpMode: 'POST', requestBody: payload, url: "https://localhost:8081/engine-rest/deployment/create"
        }
        
      }
    }
  }
}
