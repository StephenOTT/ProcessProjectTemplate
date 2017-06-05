pipeline {
  agent any
  stages {
    stage('') {
      steps {
        script {
          def exists = fileExists 'config.json'
          
          if (exists) {
                echo 'File = Yes'
          } else {
                echo 'File  = No'
          }
        }
        
      }
    }
  }
}