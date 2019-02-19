pipeline {
    agent any
    tools {
        jdk 'jdk8'
		gradle 'Gradle5'
    }
    stages {
		stage('Initialize') {
			steps {
				runGradle("--version")
			}
		}
		stage('Compile') {
			steps {
				runGradle("compileDebugSources")
			}
		}
		stage('Unit test') {
			steps {
				//Compile and run unit tests
				runGradle("testDebugUnitTest testDebugUnitTest")

				//Analyse the test results
				junit '**/TEST-*.xml'
			}
		}
		stage('Build APK') {
			steps {
				//Finish building and packaging the APK
				runGradle("assembleDebug")

				//Archive the APK
				//archiveArtifacts '**/*.apk'
			}
		}
		stage('Static analysis') {
			steps {
				//Run Lint and analyse the results
				runGradle("lintDebug")
			}
		}
		stage('Copy') {
			steps {
				fileOperations([
					fileCopyOperation(
						flattenFiles: true,
						includes: "**/app-debug.apk",
						targetLocation: "/var/www/html/apk/${env.BRANCH_NAME}"
					)
				])
			}
		}
    }
}

def runGradle(command) {
    dir("MBP2Go") {
        sh "gradle ${command}"
    }
}