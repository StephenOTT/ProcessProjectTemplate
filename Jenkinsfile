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
          def deployConfig = readJSON file: 'deploy.json'
          def files = deployConfig['deployment']['files']
          for ( e in files ) {
              if (fileExists(e.value)) {
                echo "${e.key}:${e.value} FOUND"
              } else {
                error("${e.key}:${e.value} CANNOT BE FOUND")
              }
          }
        }

        
        script {
          def deployConfig = readJSON file: 'deploy.json'

          def deploymentName = "deployment-name=${deployConfig['deployment']['deployment-name']}"
          echo deploymentName
          
          def enableDuplicateFiltering = "enable-duplicate-filtering=${deployConfig['deployment']['enable-duplicate-filtering']}"
          echo enableDuplicateFiltering
          
          def deployChangedOnly = "deploy-changed-only=${deployConfig['deployment']['deploy-changed-only']}"
          echo deployChangedOnly
          
          def deploymentSource = "deployment-source=${deployConfig['deployment']['deployment-source']}"
          echo deploymentSource
          
          def fields = []
          fields << deploymentName
          fields << enableDuplicateFiltering
          fields << deployChangedOnly
          fields << deploymentSource
          
          echo "Files to be deployed"
          def files = deployConfig['deployment']['files']
          echo files.toString()
          files.each {
            k, v -> fields << "${k}=@${v}"
          }
          
          def output = fields.join(" -F ")
          
          def curlOutput = "curl -H Accept: application/json -F ${output} http://172.17.0.1:8081/engine-rest/deployment/create"
          echo "Final CURL:"
          echo curlOutput
          env.CAMUNDA_CURL = curlOutput        
          
        }
        
        sh '''response=$(${CAMUNDA_CURL})

if [ $response != 200 ]
then
 exit 1
fi
'''
      }
    }
  }
}
