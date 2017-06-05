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
          def exists = fileExists 'bpmn/pay_taxes.bpmn'
          
          if (exists) {
            echo 'BPMN found'
          } else {
            error("BPMN cannot be found")
          }
        }
        
                script {
          def deployConfig = readJSON file: 'deploy.json'
          
          def deploymentName = '"deployment-name=${deployConfig['deployment']['deployment-name']}"'
          echo deploymentName
          
          def enableDuplicateFiltering = '"enable-duplicate-filtering=${deployConfig['deployment']['enable-duplicate-filtering']}"'
          echo enableDuplicateFiltering
          
          def deployChangedOnly = '"deploy-changed-only=${deployConfig['deployment']['deploy-changed-only']}"'
          echo deployChangedOnly
          
          def deploymentSource = '"deployment-source=${deployConfig['deployment']['deployment-source']}"'
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
            k, v -> fields << "\"${k}=@${v}\""
          }
          
          def output = fields.join(" -F ")
          
          def curlOutput = "curl -H 'Accept: application/json' -F ${output} --url 'http://172.17.0.1:8081/engine-rest/deployment/create' -w '%{http_code}'"
          echo "Final CURL:  "
          echo curlOutput
          env.CAMUNDA_CURL = curlOutput        
          
        }

        script {
          def props = readJSON file: 'deploy.json'
          echo props.toString()
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
