pipeline {
    agent any
    tools {
        jdk 'jdk8'
    }
    stages {
		stage('Initialize'){
			steps {
				cd './MBP2Go'
			}
		}
		stage('Compile') {
			steps {
				//Compile the app
				sh './gradlew compileDebugSources'
			}
		}
		stage('Unit test') {
			steps {
				//Compile and run unit tests
				sh './gradlew testDebugUnitTest testDebugUnitTest'

				//Analyse the test results
				junit '**/TEST-*.xml'
			}
		}
		stage('Build APK') {
			steps {
				//Finish building and packaging the APK
				sh './gradlew assembleDebug'

				//Archive the APK
				archiveArtifacts '**/*.apk'
			}
		}
		stage('Static analysis') {
			steps {
				// Run Lint and analyse the results
				sh './gradlew lintDebug'
				androidLint pattern: '**/lint-results-*.xml'
			}
		}
    }
}
