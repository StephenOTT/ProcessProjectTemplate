import org.apache.http.entity.mime.MultipartEntity
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.content.InputStreamBody
import org.apache.http.entity.mime.content.StringBody
import groovyx.net.http.*

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
        
        script {
          sendMultiPartFile(readFile: 'bpmn/pay_taxes.bpmn')
          // echo sendData.toString()
            
          
        }
        
      }
    }
  }
}


void sendMultiPartFile(CommonsMultipartFile multipartImageFile) {
  def http = new HTTPBuilder("http://www.localhost:8081/engine-rest/deployment/create")

  http.request(Method.POST) { req ->

  requestContentType: "multipart/form-data"

  MultipartEntity multiPartContent = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE)


  // Adding another string parameter "city"

  multiPartContent.addPart("deployment-name", "jenkins deployment")
  multiPartContent.addPart("enable-duplicate-filtering", false)
  multiPartContent.addPart("deploy-changed-only", false)
  multiPartContent.addPart("mybpmn.bpmn", new InputStreamBody(multipartImageFile.inputStream, multipartImageFile.contentType, multipartImageFile.originalFilename))

  req.setEntity(multiPartContent)

  response.success = { resp ->

        if (resp.statusLine.statusCode == 200) {

                  // response handling

                   }
            }
      }
}
