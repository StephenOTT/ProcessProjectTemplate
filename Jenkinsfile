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
          import groovy.json.JsonSlurper
          
          def deployConfig = readFile("deploy.json")
          
          def configJson = new JsonSlurper().parse(deployConfig)
          
          echo configJson
        }
        
      }
    }
  }
}