pipelineJob('ecs-agent-test') {
    description('Pipeline for ECS Agent Testing')
    definition {
        cps {
            script('''
                node {
                    stage('Checkout') {
                        // Check out the code from the source code management system if needed
                    }
                    stage('Build and Test') {
                        // Add your Groovy script here
                        echo 'Hello, this is a sample Groovy script!'
                        sh 'echo "Build and test steps go here"'
                    }
                    stage('Deploy') {
                        // Add deployment steps here
                        sh 'echo "Deployment steps go here"'
                    }
                }
            ''')
            sandbox(true)
        }
    }
    triggers {
        scm('H/5 * * * *') // Configure the polling schedule as needed
    }
}
