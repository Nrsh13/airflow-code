#!/usr/bin/env groovy

String pipeline_name = 'airflow_image_build'
String pipeline_desc = 'Job DSL example for building Airflow image'
String bitbucket_project = 'nrsh13'
String bitbucket_repo = 'airflow_image_build'
String unique_id = "${bitbucket_project}/${bitbucket_repo}_bb"

multibranchPipelineJob(pipeline_name) {
  displayName(pipeline_name)
  description(pipeline_desc)
  branchSources {
    branchSource {
      source {
        bitbucket {
          id(unique_id) // This must be unique for each job!
          serverUrl('https://bitbucket.org')
          credentialsId('bitbucket-user-pass')
          repoOwner(bitbucket_project.toUpperCase())
          repository(bitbucket_repo)
          traits {
            bitbucketBranchDiscovery {
              strategyId(1) // Exclude branches that are also filed as PRs
            }
            bitbucketPullRequestDiscovery {
              strategyId(1) // Merging the pull request with the current target branch revision
            }
          }
        }
      }
      strategy {
        defaultBranchPropertyStrategy {
          props {
            // keep only the last 10 builds
            buildRetentionBranchProperty {
              buildDiscarder {
                logRotator {
                  daysToKeepStr('-1')
                  numToKeepStr('10')
                  artifactDaysToKeepStr('-1')
                  artifactNumToKeepStr('-1')
                }
              }
            }
          }
        }
      }
    }
  }
  // don't keep build jobs for deleted branches
  orphanedItemStrategy {
    discardOldItems {
      numToKeep(-1)
    }
  }
}
