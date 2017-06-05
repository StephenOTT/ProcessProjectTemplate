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
        
        sh '''response=$(
curl -w "\n" \
-H "Accept: application/json" \
-F "deployment-name=Jenkins Deployment" \
-F "enable-duplicate-filtering=false" \
-F "deploy-changed-only=false" \
-F "myBPMN.bpmn.bpmn=@/bpmn/pay_taxes.bpmn" \
http://localhost:8081/engine-rest/deployment/create
)

if [ "$response" != "200" ]
then
 exit 1
fi'''
        }
      }
    }
  }