pipeline {
  agent any
  stages {
    stage('getConflig') {
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
          def deployConfig = readFile 'deploy.json'
        }
        
      }
    }
  }
}
