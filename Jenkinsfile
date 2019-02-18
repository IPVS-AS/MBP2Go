pipeline {
    agent any
    tools {
        jdk 'jdk8'
    }
    stages {
		stage('Compile') {
			steps {
				//Compile the app
				sh './MBP2Go/gradlew compileDebugSources'
			}
		}
		stage('Unit test') {
			steps {
				//Compile and run unit tests
				sh './MBP2Go/gradlew testDebugUnitTest testDebugUnitTest'

				//Analyse the test results
				junit '**/TEST-*.xml'
			}
		}
		stage('Build APK') {
			steps {
				//Finish building and packaging the APK
				sh './MBP2Go/gradlew assembleDebug'

				//Archive the APK
				archiveArtifacts '**/*.apk'
			}
		}
		stage('Static analysis') {
			steps {
				// Run Lint and analyse the results
				sh './MBP2Go/gradlew lintDebug'
				androidLint pattern: '**/lint-results-*.xml'
			}
		}
    }
}
