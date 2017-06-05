pipeline {
  agent any
  stages {
    stage('Deploy.json Review') {
      steps {
        script {
          def exists = fileExists 'deploy.json'
          echo "-------------------------------------------------------"
          echo "Checking for deploy.json:"
          if (exists) {
            echo 'deploy.json found'
          } else {
            error("deploy.json cannot be found")
          }
          echo "-------------------------------------------------------"
        }
        
         script {
          def deployConfig = readJSON file: 'deploy.json'
          def files = deployConfig['deployment']['files']
          echo "-------------------------------------------------------"
          echo "Checking if each File in deploy.json exists:"
          for ( e in files ) {
            if (fileExists("${e.value}")) {
                echo "${e.key}:${e.value} FOUND"
              } else {
                error("${e.key}:${e.value} CANNOT BE FOUND")
              }
          }
          echo "-------------------------------------------------------"
        }

        
        script {
          echo "-------------------------------------------------------"
          echo "Building CURL String"
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
          
          def curlOutput = "curl --url http://172.17.0.1:8081/engine-rest/deployment/create -H \"Accept: application/json\" -F ${output} -w \"%{http_code}\""
          echo "Final CURL:"
          echo curlOutput
          echo "-------------------------------------------------------"
          echo "Saving CURL String into Env Variable CAMUNDA_CURL:"
          env.CAMUNDA_CURL = curlOutput
          echo "-------------------------------------------------------"
          
        }
        
        sh '''
echo "-------------------------------------------------------"
echo "DEPLOYING to Camunda:"
response=$(${CAMUNDA_CURL})

if [ $response != 200 ]
then
echo "-------------------------------------------------------"
echo "ERROR: Did not receive Status Code 200 from Camunda"
echo "-------------------------------------------------------"
 exit 1
else
  echo "-------------------------------------------------------"
  echo "SUCCESS: Received Status Code 200: Successfully Deployed to Camunda"
  echo "-------------------------------------------------------"
fi
'''
      }
    }
  }
}
