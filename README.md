# ATAK Push-To-Talk To Text
A plugin that performs offline speech-to-text transcription for the Android Team Awareness Kit
## Version 1.0.5 Release Notes
### New Features
- Numeric words in transcriptions are automatically converted to the single-character digits they represent, and words in the Phonetic alphabet in transcriptions are converted to the single-character letters they represent.
- The user can send a transcription to any contact made available through the ATAK Chat Plugin, which can be configured in the Settings page of the plugin.

### Bug Fixes
- In previous versions, Speech-to-Text transcription did not work at all, despite the library being in place. This has been corrected in the current version, and so Speech-to-Text transcription produces a valid result in most cases.

### Known Bugs to be Fixed in Future Releases
- The plugin currently crashes upon audio without any distinct words being supplied to the transcription library. This is likely due to a logical error in handling empty Strings in the plugin source code.
- Currently, Speech-to-Text is working, but the accuracy is rather poor. According to the documentation, the accuracy of the transcription algorithm can be improved by tweaking the language model that the library uses to transcribe the audio. One possible approach to doing this is reducing the number of words for which the model is looking and instructing users to use purposefully simple language. This may be a necessary tradeoff due to the limited computation resources available.

## Install Guide
There is no binary release for this plugin. Because of this, in order to add it to ATAK, you will need to build this project from source within the context of a larger ATAK development environment.
### Prerequisites
- JDK 8 or higher
- Android 5.0 (API 21) or later.
- A graphics processor that supports GLES 3.0
- (Recommended) Android Studio

### Development Libraries
Running the binary version of our plugin on an android device (virtual or otherwise) requires the ATAK system to be installed. Furthermore, you must have an ATAK build variant (MIL, CIV, etc.) that matches the plugin’s build variant. The binary we will be distributing will be the debug CIV version.
Download Instructions: Please pull or download this repository.

### Build Instructions
The following steps can be used to build the ATAK Push-to-Talk to Text Plugin:
1. Download this repository, or clone it using `git clone https://github.com/andrewjc2000/ATAKPushToTalk.git`
2. Somewhere on your machine, generate a keyfile with the following command (note the system will not build without this being configured). This command will prompt you to add a password to the file. Make sure to remember it.
```
keytool -genkey -v -keystore my-release-key.keystore -alias alias_name -keyalg RSA -keysize 2048 -validity 10000
```
3. Navigate to the pushToTalk directory, which is at the root of the git repository.
4. Create a file called `local.properties`.
5. Populate `local.properties` with the following information, where `[password you entered earlier]` is replaced with the password you used when running the above `keytool` command.
```
sdk.dir= /path/to/androidSDK
takDebugKeyFile= /path/to/key/my-release-key.keystore
takDebugKeyFilePassword= [password you entered earlier]
takDebugKeyAlias= alias_name
takDebugKeyPassword= [password you entered earlier]
takReleaseKeyFile= /path/to/my-release-key.keystore
takReleaseKeyFilePassword= [password you entered earlier]
takReleaseKeyAlias= alias_name
takReleaseKeyPassword= [password you entered earlier]
```
5. Launch ATAK such that the gradle tasks ran from this project are able to add plugins to the environment ATAK is running in. Our development team used an emulator running ATAK launched by Android Studio to accomplish this.
6. In the pushToTalk directory, run the gradle task `assembleCivDebug` (if you do not wish to build the debug-based civilian version, subsitute this with the appropriate command). This step will produce the binary for the plugin.
7. ATAK will automatically prompt you on your device to continue installation. 
### Run Instructions
Navigate to the plugin/app section of ATAK and select the PushToTalk icon. This will launch the plugin.
### Troubleshooting
- "Gradle can’t find keyfile/sdk" - Make sure you replaced `/path/to` in your `local.properties` file with the actual path
- "Error about ATAK not being signed" - Make sure you have correctly generated your keyfile and correctly entered the information about it in `local.properties`.
