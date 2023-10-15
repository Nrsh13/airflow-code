pipelineJob('ecs-agent-sample') {
  displayName('ECS Agent Sample Pipeline')
  description('A sample Jenkins job with ECS Agent')

  // Declarative Pipeline section
  definition {
    cps {
      script('''
        pipeline {
          agent {
	    label 'agent'
	  }
          stages {
            stage('Checkout') {
              steps {
                checkout scm
              }
            }
            stage('Build and Test') {
              steps {
                script {
                   echo 'Hello, this is a sample Declarative Pipeline stage.'
                }
              }
            }
            stage('Deploy') {
              steps {
                script {
                  sh 'echo "Sample deployment steps go here"'
                }
              }
            }
          }
        }
     ''')
     sandbox(true)
    }
  }
}
