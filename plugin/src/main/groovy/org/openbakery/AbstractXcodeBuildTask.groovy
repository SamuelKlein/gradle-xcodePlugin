package org.openbakery

import org.apache.commons.lang.StringUtils
import org.gradle.api.DefaultTask
import org.openbakery.signing.ProvisioningProfileIdReader

/**
 * User: rene
 * Date: 15.07.13
 * Time: 11:57
 */
abstract class AbstractXcodeBuildTask extends DefaultTask {

	CommandRunner commandRunner

	AbstractXcodeBuildTask() {
		super()
		commandRunner = new CommandRunner()
	}

	def createCommandList() {

		def commandList = [
					project.xcodebuild.xcodebuildCommand
		]

		if (project.xcodebuild.scheme) {
			commandList.add("-scheme");
			commandList.add(project.xcodebuild.scheme);

			if (project.xcodebuild.workspace != null) {
				commandList.add("-workspace")
				commandList.add(project.xcodebuild.workspace)
			}

			if (project.xcodebuild.sdk != null) {
				commandList.add("-sdk")
				commandList.add(project.xcodebuild.sdk)
				if (project.xcodebuild.sdk.equals(XcodePlugin.SDK_IPHONESIMULATOR) && project.xcodebuild.arch != null) {
					commandList.add("ONLY_ACTIVE_ARCH=NO")
				}
			}

			if (project.xcodebuild.configuration != null) {
				commandList.add("-configuration")
				commandList.add(project.xcodebuild.configuration)
			}


		} else {
			commandList.add("-configuration")
			commandList.add(project.xcodebuild.configuration)
			commandList.add("-sdk")
			commandList.add(project.xcodebuild.sdk)
			commandList.add("-target")
			commandList.add(project.xcodebuild.target)
		}

			// disable signing during xcodebuild, signing is done later at the package task
		commandList.add("CODE_SIGN_IDENTITY=")
		commandList.add("CODE_SIGNING_REQUIRED=NO")


		if (project.xcodebuild.arch != null) {
			StringBuilder archs = new StringBuilder("ARCHS=");
			for (String singleArch in project.xcodebuild.arch) {
				if (archs.length() > 7) {
					archs.append(" ");
				}
				archs.append(singleArch);
			}
			commandList.add(archs.toString());

		}

		commandList.add("DSTROOT=" + project.xcodebuild.dstRoot.absolutePath)
		commandList.add("OBJROOT=" + project.xcodebuild.objRoot.absolutePath)
		commandList.add("SYMROOT=" + project.xcodebuild.symRoot.absolutePath)
		commandList.add("SHARED_PRECOMPS_DIR=" + project.xcodebuild.sharedPrecompsDir.absolutePath)


		if (project.xcodebuild.additionalParameters instanceof List) {
			for (String value in project.xcodebuild.additionalParameters) {
				commandList.add(value)
			}
		} else {
			if (project.xcodebuild.additionalParameters != null) {
				commandList.add(project.xcodebuild.additionalParameters)
			}
		}

		return commandList;
	}


}
