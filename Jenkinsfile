pipeline {
  agent any
  parameters {
    string(name: 'CAMUNDA_URL', defaultValue: 'http://172.17.0.1:8081', description: 'URL of Camunda Instance.')
  }
  stages {
    stage('Check Deployment Files:') {
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
        }
      }
    }
    stage('Build Curl Parameters') {
      steps {
        script {
          def fields = []
          echo "-------------------------------------------------------"
          echo "Building Curl Base Parameters:"
          def deployConfig = readJSON file: 'deploy.json'

          for ( e in deployConfig['deployment'] ) {
            if (e.key != "files") {
              echo "Deployment Parameter: ${e.key}=${e.value}"
              fields << "--form-string \'${e.key}=${e.value}\'"
            }
          }

          echo "-------------------------------------------------------"
          echo "Building Curl File Parameters"
          echo "Files to be deployed:"
          def files = deployConfig['deployment']['files']
          echo files.toString()
          files.each {
            k, v -> fields << "-F ${k}=@${v}"
          }
          
          echo "-------------------------------------------------------"
          echo "Building Concatinated Parameters"
          def output = fields.join(" ")
          env.CAMUNDA_PARAMETERS = output
        }
        script {
          echo "-------------------------------------------------------"
          echo "Building Full CURL String:"
          def curlOutput = "curl --url ${CAMUNDA_URL}/engine-rest/deployment/create -H Accept:application/json ${CAMUNDA_PARAMETERS} -w \"%{http_code}\""
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

          if [ $response.equals(200) ]
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
