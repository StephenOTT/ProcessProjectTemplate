pipeline {
  agent any
  stages {
    stage('getConflig') {
      steps {
        script {
          def exists = fileExists 'deploy1.json'
          
          if (exists) {
            echo 'File = Yes'
          } else {
            echo 'File = No'
          }
        }
        
      }
    }
  }
}