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
          def props = readJSON file: 'deploy.json'
          echo props.toString()
        }
        
        sh '''response=$(curl -H "Accept: application/json" -F "deployment-name=JenkinsDeployment" -F "enable-duplicate-filtering=false" -F "deploy-changed-only=false" -F "myBPMN.bpmn=@bpmn/pay_taxes.bpmn" --url "http://172.17.0.1:8081/engine-rest/deployment/create" -w "%{http_code}")

if [ $response != 200 ]
then
 exit 1
fi
'''
      }
    }
    stage('Build CURL') {
      steps {
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
          
          echo "Files to be deployed"
          def files = deployConfig['deployment']['files']
          echo files.toString()
          files.each {
            k, v -> echo "${k.toString()}  :  ${v.toString()}"
          }
        }
        
      }
    }
  }
}
