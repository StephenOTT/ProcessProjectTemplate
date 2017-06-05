pipeline {
  agent any
  stages {
    stage('Check Deployment Files') {
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
      }}
    stage('Build Curl String') {
      steps {
        script {
          echo "-------------------------------------------------------"
          echo "Building Curl Base parameters"
          def deployConfig = readJSON file: 'deploy.json'

          def deploymentName = "deployment-name=${deployConfig['deployment']['deployment-name']}"
          echo deploymentName
          
          def enableDuplicateFiltering = "enable-duplicate-filtering=${deployConfig['deployment']['enable-duplicate-filtering']}"
          echo enableDuplicateFiltering
          
          def deployChangedOnly = "deploy-changed-only=${deployConfig['deployment']['deploy-changed-only']}"
          echo deployChangedOnly
          
          def deploymentSource = "deployment-source=${deployConfig['deployment']['deployment-source']}"
          echo deploymentSource
        }
        script {
          echo "-------------------------------------------------------"
          echo "Building Curl File Parameters"
          def fields = []
          fields << deploymentName
          fields << enableDuplicateFiltering
          fields << deployChangedOnly
          fields << deploymentSource
          
          echo "Files to be deployed:"
          def files = deployConfig['deployment']['files']
          echo files.toString()
          files.each {
            k, v -> fields << "${k}=@${v}"
          }
        }
        script {
          echo "-------------------------------------------------------"
          echo "Building Concatinated Parameters"
          def output = fields.join(" -F ")

          echo "-------------------------------------------------------"
          echo "Building Full CURL String:"
          def curlOutput = "curl --url ${CAMUNDA_API_URL}/engine-rest/deployment/create -H Accept:application/json -F ${output} -w \"%{http_code}\""
          echo "Final CURL String:"
          echo curlOutput
          echo "-------------------------------------------------------"
          echo "Saving CURL String into Env Variable CAMUNDA_CURL:"
          env.CAMUNDA_CURL = curlOutput
          echo "-------------------------------------------------------"
        }
      }
    }
    stage('Deploy to Camunda') {
      steps {
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
