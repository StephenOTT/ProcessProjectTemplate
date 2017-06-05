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
          def deployConfig = readFile 'deploy.json'
          
          echo deployConfig
          
          def props = readJSON file: 'deploy.json'
          
          echo props.getClass()
        }
        
      }
    }
  }
}