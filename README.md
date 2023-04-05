# expiry-marker
`@Expiry` marker for setting experiment deadlines in codebase.

## Use Case
Often times we are using feature flags for running experiments. When experiments end, the tool or platform (like Firebase/Apptimize) on which experiment was created updates the user, but sometimes the code or feature flag that was put in for that experiment remain in the codebase. After all the SDK integrations and logics that are written for experiments, sometimes there is no relation between the actual code and experiment definitions. 

This creates a problem, how to be sure if the set feature flag or code branch has met its experiment deadline?

```
if( someFlagFromSdk.isEnabled() ){
    showScreen1()
} else {
    showScreen2()
}
```
This is sample code, which uses `someFlagFromSdk` instance to check if feature is enabled or not. There might be a time where feature will be always true (after a successful experiment) or always false (feature turned off forever). In that case, the code will still be checking these, and a code block will never be executed even though it is in codebase.

## Expiry to the rescue
Considering the experiment was planned for `dd-MM-yyyy` deadline, we can use this library to mark our pieces of code (class/variable/methods) to fail build if experiment deadline is passed. 

```
class FeatureFlags { 

    val someFlagFromSdk : Flag // arbitrary class

}
```
Adding `@Expiry` with deadline date:

```
class FeatureFlags { 

    @Expiry( "02-04-2023" )
    val someFlagFromSdk : Flag

}
```
Now, build project and it will fail with following error:

> @Expiry -> Feature expired: "someFlagFromSdk". Expiry: Wed Mar 02 00:00:00 PKT 2022 - Today: Mon Apr 03 06:36:35 PKT 2023. Can be found at : /<ABSOLUTE_PATH>/expiry/marker/FeatureFlags.kt:8


Since you are beyond experiment deadline, either validate if experiment is extended, or take necessary steps to update the logic.

This way, any code block that was written as experiment or even as temporary fix can be marked with an expiry which will indicate that an action is needed after certain period of time. 

## Getting started
Gradle:

In `build.gradle`:
```
plugins {
    ...
    id 'com.google.devtools.ksp'
}
```

In `dependencies{}`:
```
dependencies {
    ...
    implementation "io.github.talhahasanzia:expiry:1.0.0" // For annotation
    ksp "io.github.talhahasanzia:expiry:1.0.0" // For annotation processor
   
}
```
In `repositories{}`:
```
repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/talhahasanzia/expiry-marker")
            credentials {
                username = "github_username"
                password = "github_token"
            }
        }
    }
```

## It's KSP!
Notice this is KSP not KAPT, so yes, this library can be used with any Kotlin project including any Kotlin Multiplatform Project!


### Limitations:
- Format of date is always dd-MM-yyyy
- Time is considered 00:00 Hours local time zone. "02-04-2023" means 2 April 12:00am.
- Date check depends on system running, so if system time/date is not correct this will behave unexpectedly.

